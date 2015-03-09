package com.littleandroid.betterbatterylog;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by Kristen on 2/28/2015.
 * Fragment that displays battery log statistics. Builds report string for sharing.
 * Multiple instances of this fragment will be used in StatsActivity.
 */
public class BatteryStatsFragment extends Fragment {

    private final static String TAG = "BBL-BatteryStatsFrag";

    public final static String STATS_CATEGORY_KEY = "category";
    public final static int STATS_OVERALL = 0;
    public final static int STATS_LEFT = 1;
    public final static int STATS_RIGHT = 2;

    private int mStatsCategory;
    private String mReport;
    private View mView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Preserve across reconfigurations
        setRetainInstance(true);

        // Get arguments
        mStatsCategory = getArguments().getInt(STATS_CATEGORY_KEY, STATS_OVERALL);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.battery_stats_layout, container, false);
        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.i(TAG, "Entered onActivityCreated");

        TextView titleTV = (TextView) mView.findViewById(R.id.statsTitleTextView);
        if(titleTV != null) {
            if (mStatsCategory == STATS_OVERALL) {
                titleTV.setText("Overall Stats");
                titleTV.setTextColor(getResources().getColor(android.R.color.white));
                showSummary(null);
            } else if (mStatsCategory == STATS_LEFT) {
                titleTV.setText(R.string.left);
                titleTV.setTextColor(getResources().getColor(R.color.color_light_blue));

                LinearLayout header = (LinearLayout) mView.findViewById(R.id.headingLinearLayout);
                header.setBackgroundColor(getResources().getColor(R.color.color_left_blue));

                showSummary(Side.LEFT);

            } else if (mStatsCategory == STATS_RIGHT) {
                titleTV.setText(R.string.right);
                titleTV.setTextColor(getResources().getColor(R.color.color_light_red));

                LinearLayout header = (LinearLayout) mView.findViewById(R.id.headingLinearLayout);
                header.setBackgroundColor(getResources().getColor(R.color.color_right_red));

                showSummary(Side.RIGHT);
            }
        } else {
            Log.i(TAG, "titleTV is null");
        }
    }

    private void showSummary(Side side) {
        TextView summaryTV = (TextView) mView.findViewById(R.id.summaryTextView);

        BatteryLog batteryLog = BatteryLog.get(mView.getContext());
        int batteryCount = batteryLog.getBatteryCount(side);
        int lostCount = batteryLog.getLostBatteryCount(side);
        BatteryLog.BrandStats stats = batteryLog.getBestBrand(side);

        StringBuilder builder = new StringBuilder();
        if(mStatsCategory == STATS_OVERALL) {
            String startDateString = DateFormat.getDateInstance(DateFormat.SHORT).format(batteryLog.getStartDate());
            String todayDateString = DateFormat.getDateInstance(DateFormat.SHORT).format(new Date());
            builder.append("From ")
                    .append(startDateString)
                    .append(" to ")
                    .append(todayDateString)
                    .append("\n");
        }
        builder.append("Total batteries logged: ")
                .append(batteryCount)
                .append("\nBatteries lost or discarded early: ")
                .append(lostCount)
                .append("\nAverage lifespan: ")
                .append(batteryLog.averageLifeInDays(side))
                .append(" days")
                .append("\nBrand with best lifespan: ")
                .append(stats.getBrandName())
                .append(", averaging ")
                .append(stats.getAverageLifespan())
                .append(" days per battery.");
        mReport = builder.toString();
        summaryTV.setText(mReport);
    }

    public String getReport() {
        return mReport;
    }
}
