package com.littleandroid.betterbatterylog;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class StatsActivity extends ActionBarActivity {

    private static final String STATS_BOTH_TAG = "overall stats fragment";
    private static final String STATS_LEFT_TAG = "left stats fragment";
    private static final String STATS_RIGHT_TAG = "right stats fragment";

    private FragmentManager mFragmentManager;
    private SharedPreferencesHelper mPreferencesHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        mFragmentManager = getFragmentManager();
        mPreferencesHelper = SharedPreferencesHelper.get(this);
        setupFragments();
    }

    private void setupFragments() {
        int earCode = mPreferencesHelper.getEarPreference();

        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        if(earCode == mPreferencesHelper.BOTH_EARS) {
            BatteryStatsFragment overallCategoryFrag = new BatteryStatsFragment();
            Bundle args = new Bundle();
            args.putInt(BatteryStatsFragment.STATS_CATEGORY_KEY, BatteryStatsFragment.STATS_BOTH);
            overallCategoryFrag.setArguments(args);
            transaction.add(R.id.stats_fragment_container, overallCategoryFrag, STATS_BOTH_TAG);

        }

        if(earCode == mPreferencesHelper.LEFT_ONLY || earCode == mPreferencesHelper.BOTH_EARS) {
            BatteryStatsFragment leftCategoryFrag = new BatteryStatsFragment();
            Bundle args = new Bundle();
            args.putInt(BatteryStatsFragment.STATS_CATEGORY_KEY, BatteryStatsFragment.STATS_LEFT);
            leftCategoryFrag.setArguments(args);
            transaction.add(R.id.stats_fragment_container, leftCategoryFrag, STATS_LEFT_TAG);
        }

        if(earCode == mPreferencesHelper.RIGHT_ONLY || earCode == mPreferencesHelper.BOTH_EARS) {
            BatteryStatsFragment rightCategoryFragment = new BatteryStatsFragment();
            Bundle args = new Bundle();
            args.putInt(BatteryStatsFragment.STATS_CATEGORY_KEY, BatteryStatsFragment.STATS_RIGHT);
            rightCategoryFragment.setArguments(args);
            transaction.add(R.id.stats_fragment_container, rightCategoryFragment, STATS_RIGHT_TAG);
        }

        transaction.commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_stats, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
