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

    public interface AddBatteryListener {
        public void onAddBattery(Side side);
    }

    private AddBatteryListener mCallback;

    private ProgressBar mLeftProgressBar;
    private ProgressBar mRightProgressBar;
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
        Button leftButton = (Button)getActivity().findViewById(R.id.leftBigButton);
        Button rightButton = (Button)getActivity().findViewById(R.id.rightBigButton);
        mBatteryLog = BatteryLog.get(getView().getContext());

        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCallback != null) {
                    mCallback.onAddBattery(Side.LEFT);
                }
            }
        });

        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCallback != null) {
                    mCallback.onAddBattery(Side.RIGHT);
                }
            }
        });

        updateProgress();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (AddBatteryListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement AddBatteryListener");
        }
    }

    public void updateProgress() {
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
