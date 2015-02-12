package com.littleandroid.betterbatterylog;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Kristen on 1/27/2015.
 */
public class ButtonGaugeFragment extends Fragment {

    private static final String TAG = "BBL-ButtonGaugeFragment";

    public interface AddBatteryListener {
        public void onAddBattery(Side side);
    }

    private AddBatteryListener mCallback;

    private Button mLeftButton;
    private Button mRightButton;
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

        // Find views
        Log.i(TAG, "Entered onActivityCreated()");
        mLeftProgressBar = (ProgressBar)getActivity().findViewById(R.id.leftProgressBar);
        mRightProgressBar = (ProgressBar)getActivity().findViewById(R.id.rightProgressBar);
        mLeftButton = (Button)getActivity().findViewById(R.id.leftBigButton);
        mRightButton = (Button)getActivity().findViewById(R.id.rightBigButton);
        mBatteryLog = BatteryLog.get(getView().getContext());

        setButtonPreferences();

        mLeftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onAddBattery(Side.LEFT);
                }
            }
        });

        mRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
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

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
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

    public void setButtonPreferences() {

        // Configure according to our preferences.
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if(prefs != null) {
            try {
                String earPrefString = prefs.getString(getString(R.string.pref_which_ears), null);
                if (earPrefString != null) {

                    int earIndex = Integer.parseInt(earPrefString);
                    if (earIndex == 1) {
                        // Use left only...
                       hideRightWidgets();
                    } else if (earIndex == 2) {
                        // Use right only...
                        hideLeftWidgets();
                    } else {
                        // Use both...
                        showLeftAndRightWidgets();
                    }
                }
            } catch(ClassCastException e) {
                Log.e(TAG, e.toString());
            }
        }
    }

    private void hideLeftWidgets() {
        LinearLayout.LayoutParams hiddenLayout = new LinearLayout.LayoutParams(0,0);
        LinearLayout.LayoutParams fullLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
        mLeftButton.setVisibility(Button.INVISIBLE);
        mLeftButton.setLayoutParams(hiddenLayout);
        mRightButton.setVisibility(Button.VISIBLE);
        mRightButton.setLayoutParams(fullLayout);
        mLeftProgressBar.setVisibility(ProgressBar.INVISIBLE);
        mRightProgressBar.setVisibility(ProgressBar.VISIBLE);
        ViewGroup.LayoutParams params = mRightProgressBar.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        mRightProgressBar.setLayoutParams(params);
    }

    private void hideRightWidgets() {
        LinearLayout.LayoutParams hiddenLayout = new LinearLayout.LayoutParams(0,0);
        LinearLayout.LayoutParams fullLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
        mLeftButton.setVisibility(Button.VISIBLE);
        mLeftButton.setLayoutParams(fullLayout);
        mRightButton.setVisibility(Button.INVISIBLE);
        mRightButton.setLayoutParams(hiddenLayout);
        mLeftProgressBar.setVisibility(ProgressBar.VISIBLE);
        ViewGroup.LayoutParams params = mLeftProgressBar.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        mLeftProgressBar.setLayoutParams(params);
        mRightProgressBar.setVisibility(ProgressBar.INVISIBLE);
    }

    private void showLeftAndRightWidgets() {
        LinearLayout.LayoutParams normalButtonLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.MATCH_PARENT,1f);
        mLeftButton.setVisibility(Button.VISIBLE);
        mLeftButton.setLayoutParams(normalButtonLayout);
        mRightButton.setVisibility(Button.VISIBLE);
        mRightButton.setLayoutParams(normalButtonLayout);
        mLeftProgressBar.setVisibility(ProgressBar.VISIBLE);
        ViewGroup.LayoutParams lParams = mLeftProgressBar.getLayoutParams();
        lParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        mLeftProgressBar.setLayoutParams(lParams);
        mRightProgressBar.setVisibility(ProgressBar.VISIBLE);
        ViewGroup.LayoutParams rParams = mRightProgressBar.getLayoutParams();
        rParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        mRightProgressBar.setLayoutParams(rParams);
    }
}
