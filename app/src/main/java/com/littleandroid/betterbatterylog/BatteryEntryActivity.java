package com.littleandroid.betterbatterylog;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Switch;
import android.widget.TextView;

import org.json.JSONException;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;


public class BatteryEntryActivity extends ActionBarActivity implements OnDateSetListener {

    private static final String TAG = "BBL-BatteryEntryActivity";
    public static final String TAG_INSTALL_PICKER = "installDatePicker";
    public static final String TAG_DIED_PICKER = "diedDatePicker";

    public static final String SIDE_EXTRA = "setSideRequestExtra";
    public static final String JSON_EXTRA = "jsonStringExtra";
    public static final String DELETE_FLAG_EXTRA = "deleteFlagExtra";

    public static final String DATE_KEY = "logDateKey";
    private static final String JSON_KEY = "jsonStringKey";

    private TextView mInstallDateTV;
    private TextView mDiedDateTV;
    private Switch mLeftRightSwitch;
    private Spinner mBrandSpinner;
    private CheckBox mLostCheckBox;
    private BatteryEntry mBattery;
    private static String mDatePickerTag;

    public static class DatePickerFragment extends DialogFragment{

        private DatePickerDialog.OnDateSetListener onDateSetListener;

        static DatePickerFragment newInstance(Date date, DatePickerDialog.OnDateSetListener onDateSetListener) {
            DatePickerFragment pickerFragment = new DatePickerFragment();
            pickerFragment.setOnDateSetListener(onDateSetListener);

            //Pass the date in a bundle.
            Bundle bundle = new Bundle();
            bundle.putSerializable(DATE_KEY, date);
            pickerFragment.setArguments(bundle);
            return pickerFragment;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            super.onCreateDialog(savedInstanceState);

            Date initialDate = (Date) getArguments().getSerializable(DATE_KEY);
            int[] yearMonthDay = ymdTripleFor(initialDate);
            DatePickerDialog dialog = new DatePickerDialog(getActivity(), onDateSetListener, yearMonthDay[0], yearMonthDay[1],
                    yearMonthDay[2]);
            return dialog;
        }

        private void setOnDateSetListener(DatePickerDialog.OnDateSetListener listener) {
            this.onDateSetListener = listener;
        }

        private int[] ymdTripleFor(Date date) {
            Calendar cal = Calendar.getInstance(Locale.US);
            cal.setTime(date);
            return new int[]{cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)};
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery_entry);

        // If there is a savedInstanceState...
        if(savedInstanceState != null) {
            mBattery = BatteryEntry.getInstanceFromJSONString(savedInstanceState.getString(JSON_KEY));
        } // If no savedInstanceState, then find what extra data has been sent, and initialize mBattery.
        else if(getIntent().hasExtra(JSON_EXTRA)) {
            mBattery = BatteryEntry.getInstanceFromJSONString(getIntent().getStringExtra(JSON_EXTRA));
        } else if(getIntent().hasExtra(SIDE_EXTRA)) {
            Side side = Side.values()[getIntent().getIntExtra(SIDE_EXTRA, Side.LEFT.ordinal())];
            mBattery = new BatteryEntry(side);
        }
        // If we failed to get battery info, create a new default BatteryEntry.
        if(mBattery == null) {
            mBattery = new BatteryEntry(Side.LEFT);
        }

        // if no brand selected...
        SharedPreferencesHelper prefHelper = SharedPreferencesHelper.get(this);
        if(mBattery.getBatteryBrand() == null) {
            String prefBrand = prefHelper.getBrandPreference();
            if(prefBrand != null) {
                mBattery.setBatteryBrand(prefBrand);
            }
        }

        // Setup brand spinner
        mBrandSpinner = (Spinner) findViewById(R.id.brandSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, prefHelper.getBrandArrayList());
        mBrandSpinner.setAdapter(adapter);
        for(int i = 0; i < adapter.getCount(); ++i) {
            if(adapter.getItem(i).equals(mBattery.getBatteryBrand())) {
                mBrandSpinner.setSelection(i);
            }
        }

        mBrandSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mBattery.setBatteryBrand(parent.getAdapter().getItem(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Setup Lost checkbox
        mLostCheckBox = (CheckBox) findViewById(R.id.lostCheckBox);
        mLostCheckBox.setChecked(mBattery.isLost());
        mLostCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mBattery.setLost(isChecked);
            }
        });

        // Setup install Date TextView
        mInstallDateTV = (TextView) findViewById(R.id.installDateTextView);
        displayInstallDate();

        // Setup died Date TextView
        mDiedDateTV = (TextView) findViewById(R.id.diedDateTextView);
        displayDiedDate();

        // Setup install date ImageButton.
        ImageButton installImageButton = (ImageButton) findViewById(R.id.pickInstallDateButton);
        installImageButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Date d = mBattery.getInstallDate();
                if(d == null) {
                    d = new Date();
                }
                DatePickerFragment frag = DatePickerFragment.newInstance(d, BatteryEntryActivity.this);
                mDatePickerTag = TAG_INSTALL_PICKER;
                Log.i(TAG, "Showing install date picker.");
                frag.show(getFragmentManager(), mDatePickerTag);
            }
        });

        // Setup died date ImageButton.
        ImageButton diedImageButton = (ImageButton) findViewById(R.id.pickDiedDateButton);
        diedImageButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Date d = mBattery.getDiedDate();
                if(d == null) {
                   d = new Date();
                }
                DatePickerFragment frag = DatePickerFragment.newInstance(d, BatteryEntryActivity.this);
                mDatePickerTag = TAG_DIED_PICKER;
                Log.i(TAG, "Showing died date picker.");
                frag.show(getFragmentManager(), mDatePickerTag);
            }
        });

        // Setup the Left-Right switch
        mLeftRightSwitch = (Switch) findViewById(R.id.leftRightSwitch);
        setSwitch(mBattery.getSide());
        mLeftRightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    mBattery.setSide(Side.RIGHT);
                    setSwitchColor(getResources().getColor(R.color.color_right_red));
                }
                else {
                    mBattery.setSide(Side.LEFT);
                    setSwitchColor(getResources().getColor(R.color.color_left_blue));
                }
            }
        });

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        if(mBattery != null) {
            try {
                savedInstanceState.putString(JSON_KEY, mBattery.toJSON().toString());
            } catch (JSONException e) {
                Log.e(TAG, e.toString());
            }
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        GregorianCalendar cal = new GregorianCalendar(year, monthOfYear, dayOfMonth);
        Log.i(TAG, "Tag: " + mDatePickerTag);
        if(mDatePickerTag == TAG_INSTALL_PICKER) {
            mBattery.setInstallDate(cal.getTime());
            displayInstallDate();
        }
        else {
            mBattery.setDiedDate(cal.getTime());
            displayDiedDate();
        }
    }

    private void displayInstallDate() {
        if(mBattery != null) {
            if(mBattery.getInstallDate() != null) {
                mInstallDateTV.setText(DateFormat.getDateInstance(DateFormat.SHORT).format(mBattery.getInstallDate()));
            } else {
                mInstallDateTV.setText(R.string.not_set);
            }
        }
    }

    private void displayDiedDate() {
        if(mBattery != null) {
            if(mBattery.getDiedDate() != null) {
                mDiedDateTV.setText(DateFormat.getDateInstance(DateFormat.SHORT).format(mBattery.getDiedDate()));
            } else {
                mDiedDateTV.setText(R.string.not_set);
            }
        }
    }

    private void setSwitch(Side side) {
        if(side == Side.LEFT) {
            mLeftRightSwitch.setChecked(false);
            setSwitchColor(getResources().getColor(R.color.color_left_blue));
        } else {
            mLeftRightSwitch.setChecked(true);
            setSwitchColor(getResources().getColor(R.color.color_right_red));
        }
        mBattery.setSide(side);
    }

    private void setSwitchColor(int color) {
       // mLeftRightSwitch.setBackgroundColor(color);
        Drawable d = mLeftRightSwitch.getThumbDrawable();
        d.setColorFilter(color, PorterDuff.Mode.SRC);
    }

    private void acceptEntry(boolean accept) {
        if(accept) {
            Intent intent = new Intent();
            try {
                intent.putExtra(JSON_EXTRA, mBattery.toJSON().toString());
                setResult(RESULT_OK, intent);
            } catch(JSONException e) {
                Log.e(TAG, e.toString());
                setResult(RESULT_CANCELED);
            }

        } else {
            setResult(RESULT_CANCELED);
        }
        finish();
    }

    private void deleteEntry() {
        Intent intent = new Intent();
        try {
            intent.putExtra(JSON_EXTRA, mBattery.toJSON().toString());
            intent.putExtra(DELETE_FLAG_EXTRA, true);
            setResult(RESULT_OK, intent);
        } catch(JSONException e) {
            Log.e(TAG, e.toString());
            setResult(RESULT_CANCELED);
        }
        finish();
    }

    private void confirmationDialog() {
        // Build the AlertDialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.delete);
        builder.setMessage(R.string.delete_entry);
        // The positive button choice calls deleteEntry.
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteEntry();
            }
        });
        // The negative button displays the cancel string.
        builder.setNegativeButton(android.R.string.cancel, null);

        // Dialog will display a system info icon.
        //builder.setIcon(android.R.drawable.ic_dialog_info);

        // Create the dialog and show it.
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_battery_entry, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_menu_ok) {
            acceptEntry(true);
            return true;
        }
        else if(id == R.id.action_menu_cancel) {
            acceptEntry(false);
            return true;
        }
        else if(id == R.id.action_menu_delete) {
            confirmationDialog();
            return true;
        }
        else if(id == R.id.action_menu_clear) {
            mBattery.setDefaultValues();
            displayInstallDate();
            displayDiedDate();
            mLostCheckBox.setChecked(mBattery.isLost());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
