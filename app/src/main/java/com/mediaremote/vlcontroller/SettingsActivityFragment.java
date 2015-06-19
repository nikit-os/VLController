package com.mediaremote.vlcontroller;

import android.app.Fragment;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A placeholder fragment containing a simple view.
 */
public class SettingsActivityFragment extends PreferenceFragment {

    private static final String IP_ADDRESS_KEY = "pref_ip_address_key";
    private static final String PORT_KEY = "pref_port_key";
    private static final String PASSWORD_KEY = "pref_password_key";



    public SettingsActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        Preference.OnPreferenceChangeListener preferenceChangeListener = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                preference.setSummary((String)newValue);
                return true;
            }
        } ;

        EditTextPreference ipAddressPref = (EditTextPreference)findPreference(IP_ADDRESS_KEY);
        ipAddressPref.setOnPreferenceChangeListener(preferenceChangeListener);
        ipAddressPref.setSummary(ipAddressPref.getText());

        EditTextPreference ipPortPref = (EditTextPreference)findPreference(PORT_KEY);
        ipPortPref.setOnPreferenceChangeListener(preferenceChangeListener);
        ipPortPref.setSummary(ipPortPref.getText());

        EditTextPreference ipPasswordPref = (EditTextPreference)findPreference(PASSWORD_KEY);
        ipPasswordPref.setOnPreferenceChangeListener(preferenceChangeListener);
        ipPasswordPref.setSummary(ipPasswordPref.getText());
    }

}
