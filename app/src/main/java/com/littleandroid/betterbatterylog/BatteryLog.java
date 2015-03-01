package com.littleandroid.betterbatterylog;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
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
            //if(mBatteries.isEmpty()) {
            //    addFakeEntries();
            //}
        } catch (Exception e) {
            mBatteries = new ArrayList<>();
            //addFakeEntries();
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

    public void addBattery(BatteryEntry b) {
        if(b != null) {
            mBatteries.add(b);
        }
    }

    public BatteryEntry getBattery(UUID id) {
        for(BatteryEntry b : mBatteries) {
            if(b.getId().equals(id)) {
                return b;
            }
        }
        return null;
    }

    public boolean deleteBattery(BatteryEntry b) {
        return mBatteries.remove(b);
    }

    public boolean updateBattery(BatteryEntry b) {
        // find entry in log with same ID as b. Remove entry and replace with b.
        BatteryEntry oldBattery = getBattery(b.getId());
        if(oldBattery != null) {
            mBatteries.remove(oldBattery);
            mBatteries.add(b);
        }
        return false;
    }

    public int averageLifeInDays(Side side) {
        int count = 0;
        int daySum = 0;
        int avg = 0;

        for(BatteryEntry b : mBatteries) {
            if((side == b.getSide() || side == null) && !b.isLost() && b.getDiedDate() != null) {
                ++count;
                daySum = daySum + b.getLifeSpanDays();
            }
        }
        if(count > 0) {
            avg = daySum / count;
        }
        return avg;
    }

    public int averageLifeInDays(Side side, String brand) {
        int count = 0;
        int daySum = 0;
        int avg = 0;

        for(BatteryEntry b : mBatteries) {
            if(brand.contentEquals(b.getBatteryBrand()) && (side == null || side == b.getSide()) && !b.isLost() && b.getDiedDate() != null) {
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

    public int getBatteryCount() {
        return mBatteries.size();
    }

    public int getBatteryCount(Side side) {
        int count = 0;
        if(side == null) {
            count = getBatteryCount();
        } else {
            for (BatteryEntry b : mBatteries) {
                if (b.getSide() == side) {
                    ++count;
                }
            }
        }
        return count;
    }

    public HashMap<String, Integer> getBrandLifeMap(Side side) {
        HashMap<String, Integer> brandTally = new HashMap<>();
        for(BatteryEntry b : mBatteries) {
            String brand = b.getBatteryBrand();
            if(brand != null) {
                if (!(brandTally.containsKey(brand))) {
                    int brandLife = averageLifeInDays(side, brand);
                    brandTally.put(brand, brandLife);
                }
            }
        }

        Log.i(TAG, "HashMap size: " + brandTally.size()) ;
        return brandTally;
    }

    public String getBestBrand(Side side) {
        HashMap<String, Integer> brandLifeMap = getBrandLifeMap(side);
        int bestBrandLife = 0;
        String bestBrand = null;

        Set<String> keys = brandLifeMap.keySet();
        for(String k : keys) {
            Integer avgLife = brandLifeMap.get(k);
            Log.i(TAG, "key " + k + " value " + avgLife);
            if(avgLife > bestBrandLife) {
                bestBrand = k;
                bestBrandLife = avgLife;
            }
        }

        return bestBrand;
    }
}
