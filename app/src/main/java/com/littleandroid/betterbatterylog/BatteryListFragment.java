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

/**
 * Created by Kristen on 1/27/2015.
 */
public class BatteryListFragment extends ListFragment {

    private static final String TAG = "BBL-ListFragment";

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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.i(TAG, "Entered onActivityCreated()");

        // Get context
        Context c = this.getView().getContext();

        // Get the BatteryLog and set the adapter
        BatteryLog batteryLog = BatteryLog.get(c);
        BatteryListAdapter adapter = new BatteryListAdapter(c, batteryLog.getBatteries());
        setListAdapter(adapter);
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

        mCallback.onItemSelected(position);

    }
}
