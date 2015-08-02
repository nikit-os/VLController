package com.mediaremote.vlcontroller.fragment;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
    private ActionMode.Callback actionModeCallback = new ActionModeCallback();
    private ActionMode actionMode;

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
        favoritesRecyclerView.setItemAnimator(new DefaultItemAnimator());

        favoritesLayoutManager = new LinearLayoutManager(getActivity());
        favoritesRecyclerView.setLayoutManager(favoritesLayoutManager);

        datasource.open();
        favoritesAdapter = new VlcServersAdapter(datasource.getAllVlcServers());
        favoritesAdapter.setOnItemClickListener(new VlcServersAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent openPlayerActivityIntent = new Intent(getActivity(), PlayerControlActivity.class);
                openPlayerActivityIntent.putExtra(SERVER_DATA_URI, favoritesAdapter.get(position).getIpAndPort());
                startActivity(openPlayerActivityIntent);
            }

            @Override
            public boolean onLongItemClick(View view, int position) {
                if (actionMode == null) {
                    actionMode = getActivity().startActionMode(actionModeCallback);
                }

                toggleSelection(position);

                return true;
            }
        });
        favoritesRecyclerView.setAdapter(favoritesAdapter);

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        datasource.open();
        Log.i(TAG, "FavoritesFragment onResume()");

    }

    @Override
    public void onPause() {
        super.onPause();
        datasource.close();
        Log.i(TAG, "FavoritesFragment onPause()");
    }

    public void addServerToFavorites(VlcServer server) {
        if (!datasource.isServerExistsInDb(server)) {
            datasource.saveVlcServer(server);
            favoritesAdapter.add(server);
        } else {
            Toast.makeText(getActivity(), R.string.server_already_exist, Toast.LENGTH_SHORT).show();
        }

    }

    private void toggleSelection(int position) {
        favoritesAdapter.toggleSelection(position);
        int count = favoritesAdapter.getSelectedItemCount();

        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }


    private class ActionModeCallback implements ActionMode.Callback {
        @SuppressWarnings("unused")
        private final String TAG = ActionModeCallback.class.getSimpleName();

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate (R.menu.menu_selected, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.remove_favorite:
                    for(VlcServer serverToRemove : favoritesAdapter.getSelectedServers()) {
                        datasource.deleteVlcServer(serverToRemove);
                    }
                    favoritesAdapter.removeItems(favoritesAdapter.getSelectedItems());
                    Log.d(TAG, "remove_favorite");
                    mode.finish();
                    return true;

                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            favoritesAdapter.clearSelection();
            actionMode = null;
        }
    }



}
