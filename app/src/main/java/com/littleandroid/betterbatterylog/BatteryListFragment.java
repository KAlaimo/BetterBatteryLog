package com.littleandroid.betterbatterylog;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by Kristen on 1/27/2015.
 * Fragment that shows battery log ListView and a message bar below.
 */
public class BatteryListFragment extends ListFragment {

    //private static final String TAG = "BBL-ListFragment";
    private BatteryLog mBatteryLog;
    private RelativeLayout mMessageBarGroup;
    private TextView mMessageBarTV;
    private Button mUndoButton;
    private BatteryListAdapter mAdapter;
    private ArrayList<BatteryEntry> mUndoList;

    public interface BatteryListListener {
        public void onEditBattery(BatteryEntry b);
    }

    private BatteryListListener mCallback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout and find the ListView
        View v = inflater.inflate(R.layout.battery_log_layout, container, false);
        ListView listView = (ListView) v.findViewById(android.R.id.list);

        // Find message bar layout
        mMessageBarGroup = (RelativeLayout) v.findViewById(R.id.messageBarLayout);

        // Find message bar
        mMessageBarTV = (TextView) v.findViewById(R.id.messageBar);

        // Find undo button
        mUndoButton = (Button) v.findViewById(R.id.undoButton);
        mUndoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mUndoList != null) {
                    for(BatteryEntry b : mUndoList) {
                        mBatteryLog.addBattery(b);
                    }
                    sortListByInstallDate();
                }
                clearUndoList();
                hideMessageBar();
            }
        });

        // Setup multi choice mode on the ListView
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                // not used here
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                //Log.i(TAG, "onCreateActionMode");
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.battery_list_item_context, menu);
                mAdapter.clearSelectedPosition();
                hideMessageBar();
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                // not used here
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

                switch(item.getItemId()) {

                    case R.id.menu_item_delete :

                        clearUndoList();
                        for(int i = mAdapter.getCount() - 1; i >= 0; --i) {
                            if(getListView().isItemChecked(i)) {
                                deleteBattery(mAdapter.getItem(i));
                            }
                        }
                        mode.finish();
                        mAdapter.notifyDataSetChanged();
                        showUndoMessage();
                        return true;

                    case R.id.menu_item_edit :

                        // find first selection and edit
                        BatteryEntry b = null;
                        for(int i = mAdapter.getCount() - 1; i >= 0 &&  b == null; --i) {
                            if(getListView().isItemChecked(i)) {
                                b = mAdapter.getItem(i);
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

        //Log.i(TAG, "Entered onActivityCreated()");

        // Get context
        //Context c = this.getView().getContext();
        Context c = getActivity();

        // Get the BatteryLog and set the adapter
        mBatteryLog = BatteryLog.get(c);
        mAdapter = new BatteryListAdapter(c, mBatteryLog.getBatteries());
        setListAdapter(mAdapter);

        // sort list
        sortListByInstallDate();

        // update message
        String msg = c.getString(R.string.loaded) + " " + mAdapter.getCount();
        mMessageBarTV.setText(msg);
    }

    @Override
    public void onListItemClick(ListView l, View view, int position, long id) {
        //Log.i(TAG, "onListItemClick");
        if(mAdapter.getSelectedPosition() == position) {
            mAdapter.clearSelectedPosition();
            updateMessageBar(null);
        }
        else {
            mAdapter.setSelectedPosition(position);
            BatteryEntry b = mAdapter.getItem(position);
            updateMessageBar(b);
        }
        mAdapter.notifyDataSetChanged();
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

        // clear undo list and hide message bar, selection not valid now.
        clearUndoList();
        hideMessageBar();

        // now add the new battery to the list
        mAdapter.add(b);
        sortListByInstallDate();
    }

    private void updateMessageBar(BatteryEntry b) {
        if(b != null) {
            // Make sure view is visible
            mMessageBarGroup.setVisibility(View.VISIBLE);
            // Get side string
            String side = (b.getSide() == Side.LEFT) ? getText(R.string.left).toString() : getText(R.string.right).toString();
            // Get brand string
            String brand = b.getBatteryBrand();
            if(brand == null) {
                brand = "";
            }
            // Get lifespan in days
            int days = b.getLifeSpanDays();
            // Format message and display
            if(b.isCurrent()) {
                String msg = String.format("%s %s battery is %d days old.", side, brand, days);
                mMessageBarTV.setText(msg);
            }
            else {
                String msg = String.format("%s %s battery lasted %d days.", side, brand, days);
                mMessageBarTV.setText(msg);
            }
        } else {
            hideMessageBar();
        }

    }

    private void hideMessageBar() {
        mAdapter.clearSelectedPosition();
        mMessageBarTV.setText("");
        mUndoButton.setVisibility(View.GONE);
        mMessageBarGroup.setVisibility(View.GONE);
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

            if(mUndoList == null) {
                mUndoList = new ArrayList<>();
            }

            mUndoList.add(b);
        }
    }

    public void clearUndoList() {
        if(mUndoList != null) {
            mUndoList.clear();
        }
        mUndoButton.setVisibility(View.GONE);
    }

    public void showUndoMessage() {
        if(mUndoList != null) {
            mMessageBarGroup.setVisibility(View.VISIBLE);
            mMessageBarTV.setText(getText(R.string.deleted) + " " + mUndoList.size());
            mUndoButton.setVisibility(View.VISIBLE);
        }
    }

    public void sortListByInstallDate() {

        mAdapter.sort(new Comparator<BatteryEntry>() {
            @Override
            public int compare(BatteryEntry lhs, BatteryEntry rhs) {
                Date lhsDate = lhs.getInstallDate();
                Date rhsDate = rhs.getInstallDate();
                return rhsDate.compareTo(lhsDate);
            }
        });
    }

}
