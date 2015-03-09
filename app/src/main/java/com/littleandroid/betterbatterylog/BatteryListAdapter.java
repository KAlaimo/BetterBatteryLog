package com.littleandroid.betterbatterylog;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;

/**
 * Created by Kristen on 1/25/2015.
 * Adapter for main battery log ListView.
 */
public class BatteryListAdapter extends ArrayAdapter<BatteryEntry> {

    //private static final String TAG = "BBL-BatteryListAdapter";

    private Context mContext;
    private int mSelectedPosition;

    public BatteryListAdapter(Context c, ArrayList<BatteryEntry> batteries) {
        super(c, 0, batteries);
        mContext = c;
        clearSelectedPosition();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_details_battery, null);
        }

        BatteryEntry b = getItem(position);
        TextView sideCharTV = (TextView) convertView.findViewById(R.id.sideCharTextView);
        TextView installDateTV = (TextView) convertView.findViewById(R.id.installDateTextView);
        TextView diedDateTV = (TextView) convertView.findViewById(R.id.diedDateTextView);
        TextView batteryBrandTV = (TextView) convertView.findViewById(R.id.batteryBrandTextView);

        if(b.getSide() == Side.LEFT) {
            Drawable bg = mContext.getResources().getDrawable(R.drawable.background_left_blue);
            sideCharTV.setText(R.string.symbol_left);
            sideCharTV.setTextColor(mContext.getResources().getColor(R.color.color_light_blue));
            sideCharTV.setBackground(bg);
        }
        else {
            Drawable bg = mContext.getResources().getDrawable(R.drawable.background_right_red);
            sideCharTV.setText(R.string.symbol_right);
            sideCharTV.setTextColor(mContext.getResources().getColor(R.color.color_light_red));
            sideCharTV.setBackground(bg);
        }

        installDateTV.setText(DateFormat.getDateInstance(DateFormat.SHORT).format(b.getInstallDate()) + " - ");
        if(b.getDiedDate() != null) {
            diedDateTV.setText(DateFormat.getDateInstance(DateFormat.SHORT).format(b.getDiedDate()));
            if(b.isLost()) {
                diedDateTV.setTextColor(mContext.getResources().getColor(R.color.color_light_gray));
            }
            else {
                diedDateTV.setTextColor(mContext.getResources().getColorStateList(R.color.list_text_color));
            }
        }
        else {
            diedDateTV.setText(" ");
        }

        if(b.getBatteryBrand() != null) {
            batteryBrandTV.setText(b.getBatteryBrand());
        } else {
            batteryBrandTV.setText(" ");
        }

        if(position == mSelectedPosition) {
            convertView.setBackgroundColor(mContext.getResources().getColor(R.color.color_lighter_lavender));
        }
        else {
            convertView.setBackground(mContext.getResources().getDrawable(R.drawable.background_activated));
        }

        return convertView;
    }

    public void setSelectedPosition(int pos) {
        mSelectedPosition = pos;
    }

    public int getSelectedPosition() {
        return mSelectedPosition;
    }

    public void clearSelectedPosition() {
        mSelectedPosition = -1;
    }

}
