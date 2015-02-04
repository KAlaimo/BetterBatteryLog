package com.littleandroid.betterbatterylog;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

/**
 * Created by Kristen on 2/3/2015.
 */
public class BatteryLogJSONSerializer {
    private Context mContext;
    private String mFilename;

    public BatteryLogJSONSerializer(Context c, String filename) {
        mContext = c;
        mFilename = filename;
    }

    public void saveBatteryLog(ArrayList<BatteryEntry> log) throws JSONException, IOException {
        JSONArray jsonArray = new JSONArray();

        for(BatteryEntry b : log) {
            jsonArray.put(b.toJSON());
        }

        Writer writer = null;
        try {
            OutputStream out = mContext.openFileOutput(mFilename, mContext.MODE_PRIVATE);
            writer = new OutputStreamWriter(out);
            writer.write(jsonArray.toString());
        }
        finally {
            if(writer != null) {
                writer.close();
            }
        }
    }

    public ArrayList<BatteryEntry> loadBatteryLog() throws IOException, JSONException {
        ArrayList<BatteryEntry> batteries = new ArrayList<>();

        BufferedReader reader = null;
        try{
            InputStream in = mContext.openFileInput(mFilename);
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder jsonString = new StringBuilder();
            String line = null;
            while((line = reader.readLine()) != null) {
                jsonString.append(line);
            }
            JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
            for(int i = 0; i < array.length(); i++) {
                batteries.add(new BatteryEntry(array.getJSONObject(i)));
            }

        } catch(FileNotFoundException e) {

        } finally {
            if(reader != null) {
                reader.close();
            }
        }

        return batteries;
    }
}
