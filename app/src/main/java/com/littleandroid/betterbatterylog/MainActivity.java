package com.littleandroid.betterbatterylog;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;


public class MainActivity extends ActionBarActivity {

    private BatteryLog mBatteryLog;
    private ListView mLogListView;
    private ProgressBar mLeftProgressBar;
    private ProgressBar mRightProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBatteryLog = BatteryLog.get(this);
        mLogListView = (ListView) findViewById(R.id.logListView);
        ArrayAdapter<BatteryEntry> adapter = new ArrayAdapter<BatteryEntry>(getApplication(), R.layout.list_item_battery, mBatteryLog.getBatteries());
        mLogListView.setAdapter(adapter);

        mLeftProgressBar = (ProgressBar) findViewById(R.id.leftProgressBar);
        mRightProgressBar = (ProgressBar) findViewById(R.id.rightProgressBar);
        updateProgress();
    }

    private void updateProgress() {
        int leftAvg = mBatteryLog.averageLifeInDays(Side.LEFT);
        int rightAvg = mBatteryLog.averageLifeInDays(Side.RIGHT);
        BatteryEntry currentLeft = mBatteryLog.getCurrentBattery(Side.LEFT);
        BatteryEntry currentRight = mBatteryLog.getCurrentBattery(Side.RIGHT);

        mLeftProgressBar.setMax(leftAvg);
        mRightProgressBar.setMax(rightAvg);

        if(currentLeft != null) {
            if (currentLeft.getLifeSpanDays() > leftAvg) {
                mLeftProgressBar.setProgress(leftAvg);
            } else {
                mLeftProgressBar.setProgress(currentLeft.getLifeSpanDays());
            }
        }
        else {
            mLeftProgressBar.setProgress(0);
        }

        if(currentRight != null) {
            if (currentRight.getLifeSpanDays() > rightAvg) {
                mRightProgressBar.setProgress(rightAvg);
            } else {
                mRightProgressBar.setProgress(currentRight.getLifeSpanDays());
            }
        }
        else {
            mRightProgressBar.setProgress(0);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
