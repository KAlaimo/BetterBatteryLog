package com.littleandroid.betterbatterylog;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

/**
 * Created by Kristen on 1/27/2015.
 */
public class ButtonGaugeFragment extends Fragment {

    private static final String TAG = "BBL-ButtonGaugeFragment";

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

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.i(TAG, "Entered onActivityCreated()");
        mLeftProgressBar = (ProgressBar)getActivity().findViewById(R.id.leftProgressBar);
        mRightProgressBar = (ProgressBar)getActivity().findViewById(R.id.rightProgressBar);
        mLeftButton = (Button)getActivity().findViewById(R.id.leftBigButton);
        mRightButton = (Button)getActivity().findViewById(R.id.rightBigButton);
        mBatteryLog = BatteryLog.get(getView().getContext());

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
}
