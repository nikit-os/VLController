package com.mediaremote.vlcontroller.fragment;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mediaremote.vlcontroller.R;
import com.mediaremote.vlcontroller.activity.PlayerControlActivity;
import com.mediaremote.vlcontroller.adapter.VlcServersAdapter;
import com.mediaremote.vlcontroller.db.VlcServersDataSource;
import com.mediaremote.vlcontroller.model.VlcServer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nikita on 28/06/15.
 */
public class FavoritesFragment extends Fragment {
    public static final String TAG = FavoritesFragment.class.toString();
    public static final String SERVER_DATA_URI = "server_data";

    private RecyclerView favoritesRecyclerView;
    private VlcServersAdapter favoritesAdapter;
    private RecyclerView.LayoutManager favoritesLayoutManager;
    private VlcServersDataSource datasource;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        datasource = new VlcServersDataSource(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        favoritesRecyclerView = (RecyclerView) view.findViewById(R.id.favorites_recycler_view);
        favoritesRecyclerView.setHasFixedSize(true);

        favoritesLayoutManager = new LinearLayoutManager(getActivity());
        favoritesRecyclerView.setLayoutManager(favoritesLayoutManager);

        favoritesAdapter = new VlcServersAdapter(new ArrayList<VlcServer>());
        favoritesAdapter.setOnItemClickListener(new VlcServersAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent openPlayerActivityIntent = new Intent(getActivity(), PlayerControlActivity.class);
                openPlayerActivityIntent.putExtra(SERVER_DATA_URI, favoritesAdapter.get(position).getIpAndPort());
                startActivity(openPlayerActivityIntent);
            }

            @Override
            public void onLongItemClick(View view) {

            }
        });
        favoritesRecyclerView.setAdapter(favoritesAdapter);

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        datasource.open();
        updateFavoritesAdapter();
        Log.i(TAG, "FavoritesFragment onResume()");

    }

    @Override
    public void onPause() {
        super.onPause();
        datasource.close();
        Log.i(TAG, "FavoritesFragment onPause()");
    }

    public void updateFavoritesAdapter() {
        List<VlcServer> servers = datasource.getAllVlcServers();
        favoritesAdapter.clear();
        for (VlcServer server : servers) {
            favoritesAdapter.add(server);
        }
    }

    public void addServerToFavorites(VlcServer server) {
        if (!datasource.isServerExistsInDb(server)) {
            datasource.saveVlcServer(server);
            favoritesAdapter.add(server);
        }

    }



}
