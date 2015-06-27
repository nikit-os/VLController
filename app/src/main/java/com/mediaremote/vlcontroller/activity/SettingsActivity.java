package com.mediaremote.vlcontroller.activity;

import android.app.Activity;
import android.os.Bundle;

import com.mediaremote.vlcontroller.fragment.SettingsActivityFragment;


public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsActivityFragment())
                .commit();

    }
}
