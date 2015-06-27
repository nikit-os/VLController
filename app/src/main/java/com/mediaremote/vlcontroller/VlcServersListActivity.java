package com.mediaremote.vlcontroller;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.support.v4.widget.SwipeRefreshLayout;

import java.io.EOFException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class VlcServersListActivity extends ListActivity {
    public static final String TAG = FindVlcServerAsyncTask.class.toString();

    private static int PORT;

    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayAdapter<String> adapter;

    private Handler findVlcHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (null == msg.obj)
                return false;

            String ipAddress = (String) msg.obj;
            adapter.add(ipAddress);

            return true;
        }
    });

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        String item = (String) getListAdapter().getItem(position);
        Toast.makeText(this, item + " choose", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_vlc_servers_list);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        PORT = Integer.parseInt(sharedPref.getString(SettingsActivityFragment.PORT_KEY, "8080"));

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        adapter = new ArrayAdapter<String>(this,
                    R.layout.row_vlc_server_item, R.id.label, new ArrayList<String>());

        setListAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
            WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();

            swipeRefreshLayout.setRefreshing(true);

            Log.d(TAG, "[!] >>> Start executing FindVlcServerAsyncTask");
            new FindVlcServerAsyncTask().execute(dhcpInfo.ipAddress);
            }
        });
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
                workers[i] = new Thread(new CheckServerAvailable(inetAddressList.get(i), PORT));
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
