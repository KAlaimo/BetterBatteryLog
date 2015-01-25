package com.littleandroid.betterbatterylog;

import android.text.format.DateUtils;

import java.text.DateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Kristen on 1/23/2015.
 */
public class BatteryEntry {
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

    @Override
    public String toString() {
        return mSide + " " + DateFormat.getInstance().format(mInstallDate);
    }
}
