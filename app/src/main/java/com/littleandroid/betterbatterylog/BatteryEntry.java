package com.littleandroid.betterbatterylog;

import android.text.format.DateUtils;
import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Kristen on 1/23/2015.
 * BatteryEntry in log.
 */
public class BatteryEntry {
    // private static final String TAG = "BBL-BatteryEntry";
    private static final String JSON_UUID = "id";
    private static final String JSON_SIDE = "side";
    private static final String JSON_INSTALL_DATE = "installed";
    private static final String JSON_DIED_DATE = "died";
    private static final String JSON_LOST = "lost";
    private static final String JSON_BRAND = "brand";

    private UUID mUUID;
    private Side mSide;
    private Date mInstallDate;
    private Date mDiedDate;
    private boolean mLost;
    private String mBatteryBrand;

    public BatteryEntry(Side side) {
        mUUID = UUID.randomUUID();
        mSide = side;
        setDefaultValues();
    }

    public void setDefaultValues() {
        mInstallDate = new Date();
        mDiedDate = null;
        mLost = false;
        mBatteryBrand = null;
    }

    public BatteryEntry(JSONObject json) throws JSONException {
        mUUID = UUID.fromString(json.getString(JSON_UUID));

        int sideOrd = json.getInt(JSON_SIDE);
        if(sideOrd == Side.LEFT.ordinal()) {
            mSide = Side.LEFT;
        }
        else {
            mSide = Side.RIGHT;
        }

        mInstallDate = new Date(json.getLong(JSON_INSTALL_DATE));
        if(json.has(JSON_DIED_DATE)) {
            mDiedDate = new Date(json.getLong(JSON_DIED_DATE));
        }

        mLost = json.getBoolean(JSON_LOST);

        if(json.has(JSON_BRAND)) {
            mBatteryBrand = json.getString(JSON_BRAND);
        }
    }

    private static Date removeTime(@NonNull Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private static int daySpan(Date dateStart, Date dateEnd) {
        Date start = removeTime(dateStart == null ? new Date() : dateStart);
        Date end = removeTime(dateEnd == null ? new Date() : dateEnd);
        return (int)((end.getTime() - start.getTime()) / DateUtils.DAY_IN_MILLIS);
    }

    public UUID getId() {
        return mUUID;
    }

    public Side getSide() {
        return mSide;
    }

    public void setSide(Side side) {
        mSide = side;
    }

    public Date getInstallDate() {
        return mInstallDate;
    }

    public void setInstallDate(Date installDate) {
        mInstallDate = installDate;
    }

    public Date getDiedDate() {
        return mDiedDate;
    }

    public void setDiedDate(Date diedDate) {
        mDiedDate = diedDate;
    }

    public boolean isLost() {
        return mLost;
    }

    public void setLost(boolean lost) {
        mLost = lost;
    }

    public String getBatteryBrand() {
        return mBatteryBrand;
    }

    public void setBatteryBrand(String brand) {
        mBatteryBrand = brand;
    }

    public boolean isCurrent() {
        return (!mLost && mDiedDate == null);
    }

    public int getLifeSpanDays() {
        return daySpan(mInstallDate, mDiedDate);
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_UUID, mUUID.toString());
        json.put(JSON_SIDE, mSide.ordinal());
        json.put(JSON_INSTALL_DATE, mInstallDate.getTime());
        if(mDiedDate != null) {
            json.put(JSON_DIED_DATE, mDiedDate.getTime());
        }
        json.put(JSON_LOST, mLost);
        if(mBatteryBrand != null) {
            json.put(JSON_BRAND, mBatteryBrand);
        }
        return json;
    }

    public static BatteryEntry getInstanceFromJSONString(String jsonString) {
        try {
            return (new BatteryEntry(new JSONObject(jsonString)));
        } catch (JSONException e) {
           // Log.e(TAG, e.toString());
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        else if(!(o instanceof BatteryEntry)) {
            return false;
        }

        BatteryEntry b = (BatteryEntry)o;
        return this.getId().equals(b.getId());
    }

    @Override
    public String toString() {
        return mSide + " " + DateFormat.getInstance().format(mInstallDate);
    }
}
