package com.littleandroid.betterbatterylog;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Kristen on 2/28/2015.
 */
public class BatteryStatsFragment extends Fragment {

    private final static String TAG = "BBL-BatteryStatsFrag";

    public final static String STATS_CATEGORY_KEY = "category";
    public final static int STATS_BOTH = 0;
    public final static int STATS_LEFT = 1;
    public final static int STATS_RIGHT = 2;

    private int mStatsCategory;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Preserve across reconfigurations
        //setRetainInstance(true);

        // Get arguments
        mStatsCategory = getArguments().getInt(STATS_CATEGORY_KEY, STATS_BOTH);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.battery_stats_layout, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.i(TAG, "Entered onActivityCreated");

        TextView titleTV = (TextView) getView().findViewById(R.id.statsTitleTextView);
        if(titleTV != null) {
            if (mStatsCategory == STATS_BOTH) {
                titleTV.setText("Overall Stats");
            } else if (mStatsCategory == STATS_LEFT) {
                titleTV.setText(R.string.left);
                titleTV.setTextColor(getResources().getColor(R.color.color_light_blue));

                LinearLayout header = (LinearLayout) getView().findViewById(R.id.headingLinearLayout);
                header.setBackgroundColor(getResources().getColor(R.color.color_left_blue));

            } else if (mStatsCategory == STATS_RIGHT) {
                titleTV.setText(R.string.right);
                titleTV.setTextColor(getResources().getColor(R.color.color_light_red));

                LinearLayout header = (LinearLayout) getView().findViewById(R.id.headingLinearLayout);
                header.setBackgroundColor(getResources().getColor(R.color.color_right_red));
            }
        } else {
            Log.i(TAG, "titleTV is null");
        }
    }
}
