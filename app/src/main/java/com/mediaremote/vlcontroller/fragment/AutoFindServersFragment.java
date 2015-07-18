package com.mediaremote.vlcontroller.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mediaremote.vlcontroller.R;
import com.mediaremote.vlcontroller.adapter.VlcServersAdapter;
import com.mediaremote.vlcontroller.db.VlcServersDataSource;
import com.mediaremote.vlcontroller.model.VlcServer;
import com.mediaremote.vlcontroller.util.NetUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nikita on 28/06/15.
 */
public class AutoFindServersFragment extends Fragment {
    public static final String TAG = FavoritesFragment.class.toString();
    private VlcServersDataSource datasource;

    private RecyclerView autoFindServersRecyclerView;
    private VlcServersAdapter autoFindServersAdapter;
    private RecyclerView.LayoutManager autoFindServersLayoutManager;
    //private OnItemClickListener listener;
    private SwipeRefreshLayout swipeRefreshLayout;
    private static int port;

    public Handler findVlcHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (null == msg.obj)
                return false;

            String ipAddress = (String) msg.obj;
            autoFindServersAdapter.add(new VlcServer(ipAddress, "123", ipAddress, port));

            return true;
        }
    });

    public interface OnItemClickListener {
        void onItemClick(VlcServer server);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_auto_find_servers, container, false);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        port = Integer.parseInt(sharedPref.getString(SettingsActivityFragment.PORT_KEY, "8080"));

        datasource = new VlcServersDataSource(getActivity());

        autoFindServersRecyclerView = (RecyclerView) view.findViewById(R.id.auto_find_servers_recycler_view);
        autoFindServersRecyclerView.setHasFixedSize(true);
        autoFindServersRecyclerView.setItemAnimator(new DefaultItemAnimator());

        autoFindServersLayoutManager = new LinearLayoutManager(getActivity());
        autoFindServersRecyclerView.setLayoutManager(autoFindServersLayoutManager);

        autoFindServersAdapter = new VlcServersAdapter(new ArrayList<VlcServer>());
        autoFindServersAdapter.setOnItemClickListener(new VlcServersAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                VlcServer selectedServer = autoFindServersAdapter.get(position);
                if (!datasource.isServerExistsInDb(selectedServer)) {
                    Toast.makeText(getActivity(), R.string.server_already_exist, Toast.LENGTH_SHORT).show();
                } else {

                }
            }

            @Override
            public void onLongItemClick(View view) {

            }
        });
        autoFindServersRecyclerView.setAdapter(autoFindServersAdapter);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                WifiManager wifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
                DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();

                swipeRefreshLayout.setRefreshing(true);
                clearAdapter();
                Log.d(TAG, "[!] >>> Start executing FindVlcServerAsyncTask");
                new FindVlcServerAsyncTask().execute(dhcpInfo.ipAddress);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        datasource.open();
        super.onResume();
    }

    @Override
    public void onPause() {
        datasource.close();
        super.onPause();
    }

//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        try {
//            listener = (OnItemClickListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement AutoFindServersFragment.OnItemClickListener");
//        }
//    }

    public void clearAdapter() {
        autoFindServersAdapter.clear();
    }

    public class CheckServerAvailable implements Runnable {
        InetAddress address;
        int port;

        public CheckServerAvailable(InetAddress address, int port) {
            this.address = address;
            this.port = port;
        }

        @Override
        public void run() {
            String hostAddress = address.getHostAddress();
            try {
                URL url = new URL("http", hostAddress, this.port, "requests/status.xml");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(1000);
                try {
                    int responseCode = connection.getResponseCode();
                    String hostname = address.getHostName();
                    if (responseCode == 401) {
                        Message msg = Message.obtain();
                        msg.obj = hostname;
                        findVlcHandler.sendMessage(msg);
                        Log.d(TAG, "[*] >>> FIND VLC SERVER -> " + address.getHostAddress());
                    }
                } finally {
                    connection.disconnect();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                Log.d(TAG, "[!] >>> Fail connect to " + hostAddress + " -> "+ e.getMessage());
            }
        }
    }

    public class FindVlcServerAsyncTask extends AsyncTask<Integer, Void, Void> {
        public final String TAG = FindVlcServerAsyncTask.class.toString();

        @Override
        protected Void doInBackground(Integer ... params) {
            Integer ipAddress = params[0];

            List<InetAddress> inetAddressList = NetUtils.getIpRangeFromMask(ipAddress);
            Thread[] workers = new Thread[inetAddressList.size()];

            for (int i = 0; i < inetAddressList.size(); i++) {
                workers[i] = new Thread(new CheckServerAvailable(inetAddressList.get(i), port));
                workers[i].setPriority(Thread.MIN_PRIORITY);
            }

            for (Thread work : workers) {
                work.start();
            }

            try {
                for (Thread work : workers) {
                    work.join();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            swipeRefreshLayout.setRefreshing(false);
            Log.d(TAG, "[!] >>> Stop executing FindVlcServerAsyncTask");
        }

    }
}
