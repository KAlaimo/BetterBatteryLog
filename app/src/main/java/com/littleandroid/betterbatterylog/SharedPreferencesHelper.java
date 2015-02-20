package com.littleandroid.betterbatterylog;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Kristen on 2/12/2015.
 */
public class SharedPreferencesHelper {

    private static final String TAG = "BBL-SharedPrefHelper";

    public static final int BOTH_EARS = 0;
    public static final int LEFT_ONLY = 1;
    public static final int RIGHT_ONLY = 2;

    private static SharedPreferencesHelper mHelper;
    private SharedPreferences mPrefs;
    private Context mAppContext;

    private static String sWhichEarsKey;
    private static String sWhichBrandKey;

    private SharedPreferencesHelper(Context appContext) {
        mAppContext = appContext;
        mPrefs = PreferenceManager.getDefaultSharedPreferences(mAppContext);

        sWhichEarsKey = mAppContext.getString(R.string.pref_which_ears);
        sWhichBrandKey = mAppContext.getString(R.string.pref_which_brand);
    }

    public static SharedPreferencesHelper get(Context c) {
        if(mHelper == null) {
            mHelper = new SharedPreferencesHelper(c.getApplicationContext());
        }
        return mHelper;
    }

    public int getEarPreference() {

        try {

            String earPrefString = mPrefs.getString(sWhichEarsKey, "0");
            int code = Integer.parseInt(earPrefString);
            if(code != LEFT_ONLY && code != RIGHT_ONLY && code != BOTH_EARS) {
                code = BOTH_EARS; // default
            }
            return code;

        } catch(NumberFormatException e) {
            Log.e(TAG, e.toString());
        }

        return BOTH_EARS;
    }

    public String[] getBrandArray() {
        return mAppContext.getResources().getStringArray(R.array.batteryBrands);
    }

    public ArrayList<String> getBrandArrayList() {
        ArrayList<String> arr = new ArrayList<>(Arrays.asList(getBrandArray()));
        return arr;
    }

    public String getBrandPreference() {
        String[] arr = getBrandArray();
        int arrIndex = 0;
        String indexString = mPrefs.getString(sWhichBrandKey, "0");
        try{
            arrIndex = Integer.parseInt(indexString);
        } catch (NumberFormatException e) {
            Log.e(TAG, e.toString());
        }

        if(arrIndex < 0 || arrIndex >= arr.length) {
            arrIndex = 0;
        }


        return arr[arrIndex];
    }
}
