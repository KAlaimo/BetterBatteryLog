package com.littleandroid.betterbatterylog;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Comparator;
import java.util.Date;

/**
 * Created by Kristen on 1/27/2015.
 */
public class BatteryListFragment extends ListFragment {

    private static final String TAG = "BBL-ListFragment";
    private BatteryLog mBatteryLog;

    public interface BatteryListListener {
        public void onEditBattery(BatteryEntry b);
    }

    private class BatteryListMultiChoiceModeListener implements AbsListView.MultiChoiceModeListener {
        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
            // not used here
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.battery_list_item_context, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            // not used here
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            BatteryListAdapter adapter = (BatteryListAdapter) getListAdapter();
            switch(item.getItemId()) {

                case R.id.menu_item_delete :

                    int delCount = 0;
                    for(int i = adapter.getCount() - 1; i >= 0; --i) {
                        if(getListView().isItemChecked(i)) {
                            deleteBattery(adapter.getItem(i));
                            ++delCount;
                        }
                    }
                    mode.finish();
                    adapter.notifyDataSetChanged();
                    Toast toast = Toast.makeText(BatteryListFragment.this.getListView().getContext(), getText(R.string.deleted) + " " + delCount, Toast.LENGTH_SHORT);
                    toast.show();
                    return true;

                case R.id.menu_item_edit :

                    // find first selection and edit
                    BatteryEntry b = null;
                    for(int i = adapter.getCount() - 1; i >= 0 &&  b == null; --i) {
                        if(getListView().isItemChecked(i)) {
                            b = adapter.getItem(i);
                            if(mCallback != null) {
                                mCallback.onEditBattery(b);
                            }
                        }
                    }
                    mode.finish();
                    return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            // not used here
        }
    }

    private BatteryListListener mCallback;
    private BatteryListMultiChoiceModeListener mActionModeCallback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout and find the ListView
        View v = inflater.inflate(R.layout.battery_log_layout, container, false);
        ListView listView = (ListView) v.findViewById(android.R.id.list);

        // Setup multi choice mode on the ListView
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        mActionModeCallback = new BatteryListMultiChoiceModeListener();
        listView.setMultiChoiceModeListener(mActionModeCallback);

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

            mCallback = (BatteryListListener) activity;

        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement SelectionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
        mActionModeCallback = null;
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
        if(mActionModeCallback != null) {
            l.startActionMode(mActionModeCallback);
            l.setItemChecked(position, true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mBatteryLog != null) {
            mBatteryLog.saveBatteryLog();
        }
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

    public void updateBattery(BatteryEntry b) {
        if(b != null) {
            mBatteryLog.updateBattery(b);
            sortListByInstallDate();
        }
    }

    public void deleteBattery(BatteryEntry b) {
        if(b != null) {
            mBatteryLog.deleteBattery(b);
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
