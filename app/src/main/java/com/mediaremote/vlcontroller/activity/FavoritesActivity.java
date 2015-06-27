package com.mediaremote.vlcontroller.activity;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.mediaremote.vlcontroller.R;
import com.mediaremote.vlcontroller.db.VlcServersDataSource;
import com.mediaremote.vlcontroller.model.VlcServer;

import java.util.ArrayList;
import java.util.List;

public class FavoritesActivity extends ListActivity {
    public static final String SERVER_DATA_URI = "server_data";

    private VlcServersDataSource datasource;
    private ArrayAdapter<VlcServer> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        datasource = new VlcServersDataSource(this);
        datasource.open();

        //List<VlcServer> values = datasource.getAllVlcServers();

        adapter = new ArrayAdapter<VlcServer>(this,
                android.R.layout.simple_list_item_1, new ArrayList<VlcServer>());
        setListAdapter(adapter);

        updateAdapter();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        VlcServer selectedServer = (VlcServer)getListAdapter().getItem(position);

        Intent openPlayerActivityIntent = new Intent(this, PlayerControlActivity.class);
        openPlayerActivityIntent.putExtra(SERVER_DATA_URI, selectedServer.getIpAndPort());
        startActivity(openPlayerActivityIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_favorites, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_find_vlc) {
            startActivity(new Intent(this, VlcServersListActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        datasource.open();
        updateAdapter();
        super.onResume();
    }

    @Override
    protected void onPause() {
        datasource.close();
        super.onPause();
    }

    private void updateAdapter() {
        List<VlcServer> values = datasource.getAllVlcServers();

        adapter.clear();
        adapter.addAll(values);
    }
}
