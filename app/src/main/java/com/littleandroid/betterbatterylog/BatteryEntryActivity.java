package com.littleandroid.betterbatterylog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.Switch;
import android.widget.TextView;

import org.json.JSONException;

import java.util.Calendar;
import java.util.Date;


public class BatteryEntryActivity extends ActionBarActivity {

    private static final String TAG = "BBL-BatteryEntryActivity";

    public static final String SIDE_EXTRA = "setSideRequest";
    public static final String JSON_EXTRA = "jsonString";

    private Switch mLeftRightSwitch;
    private BatteryEntry mBattery;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery_entry);

        // Setup OK Button
        Button okButton = (Button) findViewById(R.id.okButton);
        okButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptEntry(true);
            }
        });

        // Setup Cancel Button
        Button cancelButton = (Button) findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptEntry(false);
            }
        });

        // Setup Lost checkbox
        CheckBox lostCheckBox = (CheckBox) findViewById(R.id.lostCheckBox);
        lostCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mBattery.setLost(isChecked);
            }
        });

        // Get Side value from Intent extra and initialize mBattery.
        Side side = Side.values()[getIntent().getIntExtra(SIDE_EXTRA, Side.LEFT.ordinal())];
        mBattery = new BatteryEntry(side);
        // Setup the Left-Right switch
        mLeftRightSwitch = (Switch) findViewById(R.id.leftRightSwitch);
        setSwitch(side);
        mLeftRightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    setSwitch(Side.RIGHT);
                }
                else {
                    setSwitch(Side.LEFT);
                }
            }
        });

    }

    private void setSwitch(Side side) {
        if(side == Side.LEFT) {
            mLeftRightSwitch.setChecked(false);
            mLeftRightSwitch.setBackgroundColor(getResources().getColor(R.color.color_left_blue));
        } else {
            mLeftRightSwitch.setChecked(true);
            mLeftRightSwitch.setBackgroundColor(getResources().getColor(R.color.color_right_red));
        }
        mBattery.setSide(side);
    }

    private void acceptEntry(boolean accept) {
        if(accept) {
            Intent intent = new Intent();
            try {
                intent.putExtra(JSON_EXTRA, mBattery.toJSON().toString());
                setResult(RESULT_OK);
            } catch(JSONException e) {
                Log.e(TAG, e.toString());
                setResult(RESULT_CANCELED);
            }

        } else {
            setResult(RESULT_CANCELED);
        }
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_battery_entry, menu);
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
