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

import java.util.Comparator;
import java.util.Date;

/**
 * Created by Kristen on 1/27/2015.
 */
public class BatteryListFragment extends ListFragment {

    private static final String TAG = "BBL-ListFragment";
    private BatteryLog mBatteryLog;

    public interface BatteryListListener {
        public void onItemSelected(int position);
        public void onEditBattery(BatteryEntry b);
    }

    private BatteryListListener mCallback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.battery_log_layout, container, false);
        ListView listView = (ListView) v.findViewById(android.R.id.list);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
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

                        for(int i = adapter.getCount() - 1; i >= 0; --i) {
                            if(getListView().isItemChecked(i)) {
                                mBatteryLog.deleteBattery(adapter.getItem(i));
                            }
                        }
                        mode.finish();
                        adapter.notifyDataSetChanged();
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
        });
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

    public void updateBattery(BatteryEntry b) {
        if(b != null) {
            mBatteryLog.updateBattery(b);
            sortListByInstallDate();
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
