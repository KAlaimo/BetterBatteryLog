package com.littleandroid.betterbatterylog;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.Comparator;
import java.util.Date;

/**
 * Created by Kristen on 1/27/2015.
 */
public class BatteryListFragment extends ListFragment {

    private static final String TAG = "BBL-ListFragment";
    private BatteryLog mBatteryLog;

    public interface SelectionListener {
        public void onItemSelected(int position);
    }

    private SelectionListener mCallback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.battery_log_layout, container, false);
        return v;

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Make sure that the hosting Activity has implemented
        // the SelectionListener callback interface. We need this
        // because when an item in this ListFragment is selected,
        // the hosting Activity's onItemSelected() method will be called.

        try {

            mCallback = (SelectionListener) activity;

        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement SelectionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.i(TAG, "Entered onActivityCreated()");

        // Get context
        Context c = this.getView().getContext();

        // Get the BatteryLog and set the adapter
        mBatteryLog = BatteryLog.get(c);
        BatteryListAdapter adapter = new BatteryListAdapter(c, mBatteryLog.getBatteries());
        setListAdapter(adapter);

        // sort list
        sortListByInstallDate();
    }

    @Override
    public void onListItemClick(ListView l, View view, int position, long id) {

        // Notify the hosting Activity that a selection has been made.
        try {
            BatteryListAdapter adapter = (BatteryListAdapter) l.getAdapter();
            BatteryEntry b = adapter.getItem(position);
            Log.i(TAG, "Clicked item " + b.getId());
        } catch (ClassCastException e) {
            throw new ClassCastException(l.getAdapter().toString() + " must be BatteryListAdapter");
        }

        if(mCallback != null) {
            mCallback.onItemSelected(position);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        BatteryLog.get(getActivity()).saveBatteryLog();
    }

    public void addBatteryToList(BatteryEntry b) {
        // get most recent battery in log for this side
        BatteryEntry currBattery = mBatteryLog.getCurrentBattery(b.getSide());
        // set its died date to the new battery's install date.
        if(currBattery != null) {
            currBattery.setDiedDate(b.getInstallDate());
        }

        // now add the new battery to the list
        try {
            BatteryListAdapter adapter = (BatteryListAdapter) getListAdapter();
            adapter.add(b);
            sortListByInstallDate();
        } catch (ClassCastException e) {
            throw new ClassCastException(getListAdapter().toString() + " must be BatteryListAdapter");
        }
    }

    public void sortListByInstallDate() {
        try {
            BatteryListAdapter adapter = (BatteryListAdapter) getListAdapter();
            adapter.sort(new Comparator<BatteryEntry>() {
                @Override
                public int compare(BatteryEntry lhs, BatteryEntry rhs) {
                    Date lhsDate = lhs.getInstallDate();
                    Date rhsDate = rhs.getInstallDate();
                    return rhsDate.compareTo(lhsDate);
                }
            });

        } catch (ClassCastException e) {
            throw new ClassCastException(getListAdapter().toString() + " must be BatteryListAdapter");
        }
    }

    public int getListCount() {
        return getListAdapter().getCount();
    }
}
