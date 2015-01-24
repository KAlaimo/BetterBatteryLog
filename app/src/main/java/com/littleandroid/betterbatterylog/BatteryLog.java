package com.littleandroid.betterbatterylog;

import android.content.Context;
import android.text.format.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Kristen on 1/23/2015.
 */
public class BatteryLog {
    private static BatteryLog mBatteryLog;
    private Context mAppContext;

    private ArrayList<BatteryEntry> mBatteries;

    /** Private constructor for singleton */
    private BatteryLog(Context appContext) {
        mAppContext = appContext;
        mBatteries = new ArrayList<BatteryEntry>();

        /** For testing, start with 10 fake entries */
        Date today = new Date();
        BatteryEntry prevEntry = null;
        for(int i = 0; i < 10; ++i) {
            BatteryEntry b;
            if(i % 2 == 0) {
                b = new BatteryEntry(Side.LEFT);
            }
            else {
                b = new BatteryEntry(Side.RIGHT);
            }
            Date installDate = new Date(today.getTime() - (DateUtils.DAY_IN_MILLIS * i * 14));
            b.setInstallDate(installDate);
            if(prevEntry != null) {
                prevEntry.setDiedDate(b.getInstallDate());
            }
            mBatteries.add(b);

            prevEntry = b;
        }
    }

    public static BatteryLog get(Context c) {
        if(mBatteryLog == null) {
            mBatteryLog = new BatteryLog(c.getApplicationContext());
        }
        return mBatteryLog;
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


}
