package com.littleandroid.betterbatterylog;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceManager;


public class UserSettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize with default settings.
        PreferenceManager.setDefaultValues(this, R.xml.fragment_preference, false);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }


}
