package com.littleandroid.betterbatterylog;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import org.json.JSONException;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;


public class BatteryEntryActivity extends ActionBarActivity implements OnDateSetListener {

    private static final String TAG = "BBL-BatteryEntryActivity";

    public static final String TAG_INSTALL_PICKER = "installDatePicker";
    public static final String TAG_DIED_PICKER = "diedDatePicker";
    public static final String SIDE_EXTRA = "setSideRequest";
    public static final String JSON_EXTRA = "jsonString";
    public static final String DATE_KEY = "logDate";

    private TextView mInstallDateTV;
    private TextView mDiedDateTV;
    private Switch mLeftRightSwitch;
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

        // Setup OK Button
        Button okButton = (Button) findViewById(R.id.okButton);
        okButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptEntry(true);
            }
        });

        // Setup Cancel Button
        Button cancelButton = (Button) findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptEntry(false);
            }
        });

        // Setup Lost checkbox
        CheckBox lostCheckBox = (CheckBox) findViewById(R.id.lostCheckBox);
        lostCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mBattery.setLost(isChecked);
            }
        });

        // Get Side value from Intent extra and initialize mBattery.
        Side side = Side.values()[getIntent().getIntExtra(SIDE_EXTRA, Side.LEFT.ordinal())];
        mBattery = new BatteryEntry(side);

        // Setup install Date TextView
        mInstallDateTV = (TextView) findViewById(R.id.installDateTextView);

        // Setup died Date TextView
        mDiedDateTV = (TextView) findViewById(R.id.diedDateTextView);

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
                frag.show(getFragmentManager(), mDatePickerTag);
            }
        });

        // Setup the Left-Right switch
        mLeftRightSwitch = (Switch) findViewById(R.id.leftRightSwitch);
        setSwitch(side);
        mLeftRightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    mLeftRightSwitch.setBackgroundColor(getResources().getColor(R.color.color_right_red));
                }
                else {
                    mLeftRightSwitch.setBackgroundColor(getResources().getColor(R.color.color_left_blue));
                }
            }
        });

    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        GregorianCalendar cal = new GregorianCalendar(year, monthOfYear, dayOfMonth);
        Log.i(TAG, "Tag: " + mDatePickerTag);
        if(mDatePickerTag == TAG_INSTALL_PICKER) {
            mBattery.setInstallDate(cal.getTime());
            mInstallDateTV.setText(DateFormat.getDateInstance(DateFormat.SHORT).format(mBattery.getInstallDate()));
        }
        else {
            mBattery.setDiedDate(cal.getTime());
            mDiedDateTV.setText(DateFormat.getDateInstance(DateFormat.SHORT).format(mBattery.getDiedDate()));
        }
    }

    private void setSwitch(Side side) {
        if(side == Side.LEFT) {
            mLeftRightSwitch.setChecked(false);
            mLeftRightSwitch.setBackgroundColor(getResources().getColor(R.color.color_left_blue));
        } else {
            mLeftRightSwitch.setChecked(true);
            mLeftRightSwitch.setBackgroundColor(getResources().getColor(R.color.color_right_red));
        }
        mBattery.setSide(side);
    }

    private void acceptEntry(boolean accept) {
        if(accept) {
            Intent intent = new Intent();
            try {
                intent.putExtra(JSON_EXTRA, mBattery.toJSON().toString());
                setResult(RESULT_OK);
            } catch(JSONException e) {
                Log.e(TAG, e.toString());
                setResult(RESULT_CANCELED);
            }

        } else {
            setResult(RESULT_CANCELED);
        }
        finish();
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
