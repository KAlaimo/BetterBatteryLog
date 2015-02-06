package com.littleandroid.betterbatterylog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends Activity implements BatteryListFragment.SelectionListener, ButtonGaugeFragment.AddBatteryListener {

    private static final String TAG = "BBL-MainActivity";
    private static final int NEW_ENTRY_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_activity_main);
    }

    @Override
    public void onItemSelected(int position) {
        BatteryLog batteryLog = BatteryLog.get(this);
        BatteryEntry b = batteryLog.getBatteries().get(position);
        Toast toast = Toast.makeText(this, b.toString(), Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onAddBattery(Side side) {
        Intent explicitIntent = new Intent(this, BatteryEntryActivity.class);
        explicitIntent.putExtra(BatteryEntryActivity.SIDE_EXTRA, side.ordinal());
        startActivityForResult(explicitIntent, NEW_ENTRY_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            if(requestCode == NEW_ENTRY_REQUEST_CODE) {
                if(data != null) {
                    String jsonString = data.getStringExtra(BatteryEntryActivity.JSON_EXTRA);
                    try {
                        BatteryEntry b = new BatteryEntry(new JSONObject(jsonString));
                        addBattery(b);
                        updateProgress();
                    } catch (JSONException e) {
                        Log.e(TAG, e.toString());
                    }
                } else {
                    Log.e(TAG, "No intent data found.");
                }
            }

        }
    }

    private void addBattery(BatteryEntry b) {
        BatteryListFragment frag = (BatteryListFragment) getFragmentManager().findFragmentById(R.id.fragmentBatteryList);
        if(frag != null) {
            frag.addBatteryToList(b);
        }
    }

    private void updateProgress() {
        ButtonGaugeFragment frag = (ButtonGaugeFragment) getFragmentManager().findFragmentById(R.id.fragmentButtonGauge);
        if(frag != null) {
            frag.updateProgress();
        }
    }

    /**
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        if(l == mLogListView) {
            BatteryListAdapter adapter = (BatteryListAdapter) l.getAdapter();
            BatteryEntry b = adapter.getItem(position);

            Toast toast = Toast.makeText(this, b.toString(), Toast.LENGTH_SHORT);
            toast.show();
        }
    } */


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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
