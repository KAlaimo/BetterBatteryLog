package com.littleandroid.betterbatterylog;


import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONException;


public class MainActivity extends ActionBarActivity implements BatteryListFragment.BatteryListListener, ButtonGaugeFragment.AddBatteryListener {

    private static final String TAG = "BBL-MainActivity";
    private static final int NEW_ENTRY_REQUEST_CODE = 1;
    private static final int ENTRY_EDIT_REQUEST_CODE = 2;
    private static final int CHANGE_SETTINGS_REQUEST_CODE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_activity_main);

        // Initialize with default settings.
        PreferenceManager.setDefaultValues(this, R.xml.fragment_preference, false);
    }

    @Override
    public void onAddBattery(Side side) {
        Intent explicitIntent = new Intent(this, BatteryEntryActivity.class);
        explicitIntent.putExtra(BatteryEntryActivity.SIDE_EXTRA, side.ordinal());
        startActivityForResult(explicitIntent, NEW_ENTRY_REQUEST_CODE);
    }

    @Override
    public void onEditBattery(BatteryEntry b) {
        Intent explicitIntent = new Intent(this, BatteryEntryActivity.class);
        try {
            explicitIntent.putExtra(BatteryEntryActivity.JSON_EXTRA, b.toJSON().toString());
            startActivityForResult(explicitIntent, ENTRY_EDIT_REQUEST_CODE);
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            if(requestCode == NEW_ENTRY_REQUEST_CODE) {
                if(data != null) {
                    String jsonString = data.getStringExtra(BatteryEntryActivity.JSON_EXTRA);
                    BatteryEntry b = BatteryEntry.getInstanceFromJSONString(jsonString);
                    addBattery(b);
                    updateProgress();
                } else {
                    Log.e(TAG, "No intent data found.");
                }
            } else if(requestCode == ENTRY_EDIT_REQUEST_CODE) {
                if(data != null) {
                    String jsonString = data.getStringExtra(BatteryEntryActivity.JSON_EXTRA);
                    BatteryEntry b = BatteryEntry.getInstanceFromJSONString(jsonString);
                    if(data.hasExtra(BatteryEntryActivity.DELETE_FLAG_EXTRA)) {
                        deleteBattery(b);
                    }
                    else {
                        updateBattery(b);
                    }
                    updateProgress();
                }
            }
        }

        if(requestCode == CHANGE_SETTINGS_REQUEST_CODE) {
            updatePreferences();
        }
    }

    private void addBattery(BatteryEntry b) {
        BatteryListFragment frag = (BatteryListFragment) getFragmentManager().findFragmentById(R.id.fragmentBatteryList);
        if(frag != null) {
            frag.addBatteryToList(b);
        }
    }

    private void updateBattery(BatteryEntry b) {
        BatteryListFragment frag = (BatteryListFragment) getFragmentManager().findFragmentById(R.id.fragmentBatteryList);
        if(frag != null) {
            Log.i(TAG, "Update " + b.toString());
            frag.updateBattery(b);
        }
    }

    private void deleteBattery(BatteryEntry b) {
        BatteryListFragment frag = (BatteryListFragment) getFragmentManager().findFragmentById(R.id.fragmentBatteryList);
        if(frag != null) {
            frag.clearUndoList();
            frag.deleteBattery(b);
            frag.sortListByInstallDate();
            frag.showUndoMessage();
        }
    }

    private void updateProgress() {
        ButtonGaugeFragment frag = (ButtonGaugeFragment) getFragmentManager().findFragmentById(R.id.fragmentButtonGauge);
        if(frag != null) {
            frag.updateProgress();
        }
    }

    private void updatePreferences() {
        ButtonGaugeFragment frag = (ButtonGaugeFragment) getFragmentManager().findFragmentById(R.id.fragmentButtonGauge);
        if(frag != null) {
            frag.setButtonPreferences();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, UserSettingsActivity.class);
            startActivityForResult(settingsIntent, CHANGE_SETTINGS_REQUEST_CODE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
