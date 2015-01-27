package com.littleandroid.betterbatterylog;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;


public class MainActivity extends ListActivity {

    private BatteryLog mBatteryLog;
    private ListView mLogListView;
    private ProgressBar mLeftProgressBar;
    private ProgressBar mRightProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBatteryLog = BatteryLog.get(this);
        mLogListView = (ListView) findViewById(android.R.id.list);
        BatteryListAdapter adapter = new BatteryListAdapter(this, mBatteryLog.getBatteries());
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
    protected void onListItemClick(ListView l, View v, int position, long id) {
        if(l == mLogListView) {
            BatteryListAdapter adapter = (BatteryListAdapter) l.getAdapter();
            BatteryEntry b = adapter.getItem(position);

            Toast toast = Toast.makeText(this, b.toString(), Toast.LENGTH_SHORT);
            toast.show();
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
