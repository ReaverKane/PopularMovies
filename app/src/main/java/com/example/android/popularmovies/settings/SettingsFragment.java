package com.example.android.popularmovies.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;

import com.example.android.popularmovies.R;

/**
 * Created by Kane on 17/03/2018.
 * Fragment class to setup the preferences/settings menu that will allow for
 * the user to specify the sort order (and other settings in the future if required)
 */

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        //Add preferences, defined in the XML resource file pref_settings
        addPreferencesFromResource(R.xml.pref_settings);


        //Set Summary

        //Get preference screen
        PreferenceScreen prefScreen = getPreferenceScreen();
        //Get Shared preferences
        SharedPreferences sharedPreferences = prefScreen.getSharedPreferences();

        //get size of prefScreen (should be 1, but prepare for the future)
        int count = prefScreen.getPreferenceCount();

        //iterate through all the preferences (just one for now, but prepare etc.)
        //and set up a preference summary when adequate (it sure is for the one we have)
        for (int i = 0; i < count; i++) {
            Preference pref = prefScreen.getPreference(i);
            //If it's a checklist it should be taken care of in the xml, so only proceed for
            //other types
            if (!(pref instanceof CheckBoxPreference)) {
                String prefValue = sharedPreferences.getString(pref.getKey(), "");
                setPrefSummary(pref, prefValue);
            }
        }

    }

    //Change Summary on PreferenceChanged
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference pref = findPreference(key);

        if (pref != null) {
            if (!(pref instanceof CheckBoxPreference)) {
                String prefValue = sharedPreferences.getString(pref.getKey(), "");
                setPrefSummary(pref, prefValue);
            }
        }
    }

    //This is the method that actually changes the summary for now i'll only use the case for
    //ListPreference, in the future can easily add code for others
    private void setPrefSummary(Preference preference, String value) {
        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(value);
            if (prefIndex >= 0) {
                listPreference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        }
    }

    //Override onCreate to register the listener
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    //Override onDestroy to unregister the listener

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}
