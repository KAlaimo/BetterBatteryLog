package com.littleandroid.betterbatterylog;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by Kristen on 2/9/2015.
 */
public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.fragment_preference);
    }
}
