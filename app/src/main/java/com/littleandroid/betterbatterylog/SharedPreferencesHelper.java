package com.littleandroid.betterbatterylog;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Kristen on 2/12/2015.
 *
 * Manage preferences including the custom battery list.
 */
public class SharedPreferencesHelper {

    private static final String TAG = "BBL-SharedPrefHelper";

    public static final int BOTH_EARS = 0;
    public static final int LEFT_ONLY = 1;
    public static final int RIGHT_ONLY = 2;

    private static SharedPreferencesHelper mHelper;
    private SharedPreferences mPrefs;
    private Context mAppContext;

    private static final String CURRENT_BRAND_KEY = "pref_current_brand";
    private static final String CUSTOM_BRAND_KEY = "pref_custom_brands";
    private static String sWhichEarsKey;

    private SharedPreferencesHelper(Context appContext) {
        mAppContext = appContext;
        mPrefs = PreferenceManager.getDefaultSharedPreferences(mAppContext);

        sWhichEarsKey = mAppContext.getString(R.string.pref_which_ears);
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

    public void setCurrentBrand(String brand) {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(CURRENT_BRAND_KEY, brand);
        editor.apply();
    }

    public String getCurrentBrand() {
        return mPrefs.getString(CURRENT_BRAND_KEY, getBrandArray()[0]);
    }

    private String[] getBrandArray() {
        return mAppContext.getResources().getStringArray(R.array.batteryBrandsShortList);
    }

    public ArrayList<String> getBrandArrayList() {
        ArrayList<String> arr = new ArrayList<>(Arrays.asList(getBrandArray()));
        Set<String> customBrands = getCustomBrandSet();
        if(customBrands != null) {
            //Log.i(TAG, "Custom brand count: " + customBrands.size());
            arr.addAll(customBrands);
            Collections.sort(arr);
        }
        return arr;
    }

    private Set<String> getCustomBrandSet() {
        Set<String> customBrands = mPrefs.getStringSet(CUSTOM_BRAND_KEY, null);
        HashSet<String> editableCustomBrandSet = new HashSet<>();
        if(customBrands != null) {
            editableCustomBrandSet.addAll(customBrands);
        }
        return editableCustomBrandSet;
    }

    public void addCustomBrand(String brand) {
        if(brand != null) {
            Set <String> customBrands = getCustomBrandSet();
            customBrands.add(brand);

            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putStringSet(CUSTOM_BRAND_KEY, customBrands);
            editor.apply();
        }
    }
}
