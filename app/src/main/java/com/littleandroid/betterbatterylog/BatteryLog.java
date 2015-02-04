package com.littleandroid.betterbatterylog;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Kristen on 1/23/2015.
 */
public class BatteryLog {

    private static final String TAG = "BBL-BatteryLog";
    private static final String FILENAME = "batterylog.json";

    private static BatteryLog mBatteryLog;
    private Context mAppContext;
    private ArrayList<BatteryEntry> mBatteries;
    private BatteryLogJSONSerializer mSerializer;

    /** Private constructor for singleton */
    private BatteryLog(Context appContext) {
        mAppContext = appContext;

        mSerializer = new BatteryLogJSONSerializer(mAppContext, FILENAME);
        try {
            mBatteries = mSerializer.loadBatteryLog();
            Log.i(TAG, "File loaded.");
            if(mBatteries.isEmpty()) {
                addFakeEntries();
            }
        } catch (Exception e) {
            mBatteries = new ArrayList<>();
            addFakeEntries();
        }
    }

    private void addFakeEntries() {

        Log.i(TAG, "No file. Faking it...");
        /** For testing, start with 20 fake entries */
        Date today = new Date();
        for(int i = 0; i < 20; ++i) {
            BatteryEntry b;

            /** alternate between left and right */
            if(i % 2 == 0) {
                b = new BatteryEntry(Side.LEFT);
            }
            else {
                b = new BatteryEntry(Side.RIGHT);
            }

            if(i == 0) {
                Date installDate = new Date(today.getTime() - DateUtils.DAY_IN_MILLIS);
                b.setInstallDate(installDate);
            }
            else if (i > 0) {
                Date installDate = new Date(today.getTime() - (DateUtils.DAY_IN_MILLIS * i * 7));
                b.setInstallDate(installDate);
                if (i > 1) {
                    Date diedDate = new Date(installDate.getTime() + (DateUtils.DAY_IN_MILLIS * 14));
                    b.setDiedDate(diedDate);
                }
            }
            mBatteries.add(b);
        }
    }

    public static BatteryLog get(Context c) {
        if(mBatteryLog == null) {
            mBatteryLog = new BatteryLog(c.getApplicationContext());
        }
        return mBatteryLog;
    }

    public boolean saveBatteryLog() {
        try {
            mSerializer.saveBatteryLog(mBatteries);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Context getAppContext () {
        return mAppContext;
    }

    public ArrayList<BatteryEntry> getBatteries() {
        return mBatteries;
    }

    public BatteryEntry getBattery(UUID id) {
        for(BatteryEntry b : mBatteries) {
            if(b.getId().equals(id)) {
                return b;
            }
        }
        return null;
    }

    public int averageLifeInDays(Side side) {
        int count = 0;
        int daySum = 0;
        int avg = 0;

        for(BatteryEntry b : mBatteries) {
            if(side == b.getSide() && !b.isLost() && b.getDiedDate() != null) {
                ++count;
                daySum = daySum + b.getLifeSpanDays();
            }
        }
        if(count > 0) {
            avg = daySum / count;
        }
        return avg;
    }

    public BatteryEntry getCurrentBattery(Side side) {
        for(BatteryEntry b : mBatteries) {
            if(side == b.getSide() && b.isCurrent()) {
                return b;
            }
        }
        return null;
    }

    public int getCurrentLifeDays(Side side) {
        int days = 0;
        BatteryEntry b = getCurrentBattery(side);
        if(b != null) {
            days = b.getLifeSpanDays();
        }
        return days;
    }
}
