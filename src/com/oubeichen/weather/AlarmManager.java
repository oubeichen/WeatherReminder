package com.oubeichen.weather;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.support.v4.app.Fragment;

public class AlarmManager {
    private static final String STORAGE_FILENAME = "AlarmStorage.json";

    private static JSONObject mRoot;
    private static JSONArray mAlarms;

    private static Context mContext = Utils.getInstance();

    private static List<String> mTitle;
    private static List<Boolean> mEnabled;

    private static boolean mIsOpen = false;

    public static List<String> loadAlarm() {
        byte[] bbuf = new byte[100000];
        int len;
        mTitle = new ArrayList<String>();
        mEnabled = new ArrayList<Boolean>();
        try {
            // 读存储
            FileInputStream fin = mContext.openFileInput(STORAGE_FILENAME);
            len = fin.read(bbuf);
            fin.close();
            String str = new String(bbuf, 0, len);
            mRoot = new JSONObject(str);
            mAlarms = mRoot.getJSONArray("alarms");
            reload();
        } catch (Exception ex) {
            mRoot = new JSONObject();
            mAlarms = new JSONArray();
            try {
                mRoot.put("alarms", mAlarms);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
            }
        }
        mIsOpen = true;
        return mTitle;
    }

    public static void addAlarm(int count, CharSequence name, List<Fragment> frags)
            throws JSONException, IOException {
        if(!mIsOpen){
            loadAlarm();
        }
        JSONObject thisalarm;
        if(count == -1){
            thisalarm = new JSONObject();
        } else {
            thisalarm = mAlarms.getJSONObject(count);
        }
        mAlarms.put(thisalarm);
        JSONArray conds = new JSONArray();
        thisalarm.put("name", name);
        thisalarm.put("conds", conds);
        thisalarm.put("enabled", true);
 
        Iterator<Fragment> it = frags.iterator();
        if(it.hasNext()){
            while (it.hasNext()) {
                ConditionFragment frag = (ConditionFragment) it.next();
                JSONObject cond = new JSONObject();
                cond.put("opt1", frag.cond_type.getSelectedItemPosition());
                cond.put("opt2", frag.cond_type1.getSelectedItemPosition());
                cond.put("opt3", frag.cond_type2.getSelectedItemPosition());
                cond.put("opt4", frag.cond_type3.getSelectedItemPosition());
                conds.put(cond);
            }
            save();
        }
    }

    private static void reload() throws JSONException {
        mTitle.clear();
        mEnabled.clear();
        int len = mAlarms.length();
        for(int i = 0;i < len; i++) {
            JSONObject alarm = mAlarms.getJSONObject(i);
            mTitle.add(alarm.getString("name"));
            mEnabled.add(alarm.getBoolean("enabled"));
        }
    }

    private static void save() throws IOException {
        // 写存储
        FileOutputStream fout = mContext.openFileOutput(STORAGE_FILENAME, Context.MODE_PRIVATE);
        fout.write(mRoot.toString().getBytes());
        fout.close();
    }

    public static void delAlarm(int pos){
        try {
            mAlarms = Utils.RemoveJSONArray(mAlarms, pos);
            mRoot.put("alarms", mAlarms);
            reload();
            save();
        } catch (Exception e) {
        }
    }

    public static List<Boolean> getEnabled() {
        return mEnabled;
    }

    public static void setEnabled(int pos, Boolean enabled) {
        try {
            JSONObject alarm = mAlarms.getJSONObject(pos);
            alarm.put("enabled", enabled);
            mAlarms.put(pos, alarm);
            reload();
            save();
        } catch (Exception e) {

        }
        
    }

    public static List<String> getTitle() {
        return mTitle;
    }
    
    public static int getCount() {
        return mTitle.size();
    }

}
