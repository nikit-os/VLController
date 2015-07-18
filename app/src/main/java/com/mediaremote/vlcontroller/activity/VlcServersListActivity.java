package com.mediaremote.vlcontroller.activity;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.mediaremote.vlcontroller.R;
import com.mediaremote.vlcontroller.fragment.AddNewServerDialog;
import com.mediaremote.vlcontroller.fragment.AutoFindServersFragment;
import com.mediaremote.vlcontroller.fragment.FavoritesFragment;
import com.mediaremote.vlcontroller.fragment.SettingsActivityFragment;
import com.mediaremote.vlcontroller.model.VlcServer;


public class VlcServersListActivity extends AppCompatActivity implements AddNewServerDialog.AddNewServerDialogListener {
    public static final String TAG = VlcServersListActivity.class.toString();

    private ViewPager pager;
    private FavoritesFragment favoritesFragment;
    private AutoFindServersFragment autoFindServersFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vlc_servers_list);
        setupToolbar();

        favoritesFragment = new FavoritesFragment();
        autoFindServersFragment = new AutoFindServersFragment();

        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new VlcServersPagerAdapter(getSupportFragmentManager()));

        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(pager);
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.mipmap.ic_launcher);
        ab.setDisplayHomeAsUpEnabled(true);
    }



    public void openAddServerDialog(View view) {
        AddNewServerDialog dialog = new AddNewServerDialog();
        dialog.show(getFragmentManager(), "NoticeDialogFragment");
    }

    @Override
    public void onAddingServer(VlcServer newServer) {
        favoritesFragment.addServerToFavorites(newServer);
    }

    private class VlcServersPagerAdapter extends FragmentPagerAdapter {
        private final int PAGE_COUNT = 2;

        public VlcServersPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: {
                    return favoritesFragment;
                }
                case 1: {
                    return autoFindServersFragment;
                }
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0: {
                    return "Favorites";
                }
                case 1: {
                    return "Find servers";
                }
                default:
                    return null;
            }
        }
    }

}
