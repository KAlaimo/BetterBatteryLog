package com.littleandroid.betterbatterylog;

import android.text.format.DateUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Kristen on 1/23/2015.
 */
public class BatteryEntry {
    private static final String JSON_UUID = "id";
    private static final String JSON_SIDE = "side";
    private static final String JSON_INSTALL_DATE = "installed";
    private static final String JSON_DIED_DATE = "died";
    private static final String JSON_LOST = "lost";

    private UUID mUUID;
    private Side mSide;
    private Date mInstallDate;
    private Date mDiedDate;
    private boolean mLost;
    /** TODO: consider adding battery brand info */

    public BatteryEntry(Side side) {
        mUUID = UUID.randomUUID();
        mSide = side;
        mInstallDate = new Date();
        mDiedDate = null;
        mLost = false;
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

    public boolean isCurrent() {
        if(!mLost && mDiedDate == null) {
            return true;
        }
        return false;
    }

    public int getLifeSpanDays() {
        int days = 0;
        if(mDiedDate != null) {
           long milliSecs = mDiedDate.getTime() - mInstallDate.getTime();
           days = (int)(milliSecs/ DateUtils.DAY_IN_MILLIS);
        }
        else {
            Date today = new Date();
            long milliSecs = today.getTime() - mInstallDate.getTime();
            days = (int)(milliSecs/ DateUtils.DAY_IN_MILLIS);
        }
        return days;
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
        return json;
    }

    @Override
    public String toString() {
        return mSide + " " + DateFormat.getInstance().format(mInstallDate);
    }
}
