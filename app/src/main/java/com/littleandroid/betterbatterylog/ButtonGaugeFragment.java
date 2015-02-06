package com.littleandroid.betterbatterylog;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Kristen on 1/27/2015.
 */
public class ButtonGaugeFragment extends Fragment {

    private static final String TAG = "BBL-ButtonGaugeFragment";
    private static final int NEW_ENTRY_REQUEST_CODE = 1;

    private ProgressBar mLeftProgressBar;
    private ProgressBar mRightProgressBar;
    private Button mLeftButton;
    private Button mRightButton;
    private BatteryLog mBatteryLog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.button_gauge_layout, container, false);
        return v;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.i(TAG, "Entered onActivityCreated()");
        mLeftProgressBar = (ProgressBar)getActivity().findViewById(R.id.leftProgressBar);
        mRightProgressBar = (ProgressBar)getActivity().findViewById(R.id.rightProgressBar);
        mLeftButton = (Button)getActivity().findViewById(R.id.leftBigButton);
        mRightButton = (Button)getActivity().findViewById(R.id.rightBigButton);
        mBatteryLog = BatteryLog.get(getView().getContext());

        mLeftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBatteryEntryActivity(Side.LEFT);
            }
        });

        mRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBatteryEntryActivity(Side.RIGHT);
            }
        });

        updateProgress();
    }

    private void startBatteryEntryActivity(Side side) {
        Intent explicitIntent = new Intent(getView().getContext(), BatteryEntryActivity.class);
        explicitIntent.putExtra(BatteryEntryActivity.SIDE_EXTRA, side.ordinal());
        startActivityForResult(explicitIntent, NEW_ENTRY_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK) {
            if(requestCode == NEW_ENTRY_REQUEST_CODE) {
                if(data != null) {
                    String jsonString = data.getStringExtra(BatteryEntryActivity.JSON_EXTRA);
                    try {
                        BatteryEntry b = new BatteryEntry(new JSONObject(jsonString));
                        //mBatteryLog.addBattery(b);
                        Log.i(TAG, "added " + b.toString());
                    } catch (JSONException e) {
                        Log.e(TAG, e.toString());
                    }
                } else {
                    Log.e(TAG, "No intent data found.");
                }
            }

        }
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
}
