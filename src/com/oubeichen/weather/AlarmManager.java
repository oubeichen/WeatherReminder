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

import com.oubeichen.weather.common.logger.Log;

import android.content.Context;
import android.support.v4.app.Fragment;

public class AlarmManager {
    private static final String STORAGE_FILENAME = "AlarmStorage.json";

    private static JSONObject root;
    private static JSONArray alarms;

    private static Context mContext = ContextUtil.getInstance();
    
    private static List<String> mList;
    private static List<Boolean> mEnabled;

    private static boolean isOpen = false;

    public static List<String> loadAlarm() {
        byte[] bbuf = new byte[100000];
        int len;
        mList = new ArrayList<String>();
        mEnabled = new ArrayList<Boolean>();
        mList.clear();
        mEnabled.clear();
        try {
            // 读存储
            FileInputStream fin = mContext.openFileInput(STORAGE_FILENAME);
            len = fin.read(bbuf);
            fin.close();
            String str = new String(bbuf, 0, len);
            Log.i("AlarmManager", str);
            root = new JSONObject(str);
            alarms = root.getJSONArray("alarms");
            len = alarms.length();
            for(int i = 0;i < len; i++) {
                JSONObject alarm = alarms.getJSONObject(i);
                mList.add(alarm.getString("name"));
                mEnabled.add(alarm.getBoolean("enabled"));
            }
        } catch (Exception ex) {
            root = new JSONObject();
            alarms = new JSONArray();
            try {
                root.put("alarms", alarms);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
            }
        }
        isOpen = true;
        return mList;
    }

    public static void addAlarm(int count, CharSequence name, List<Fragment> frags)
            throws JSONException, IOException {
        if(!isOpen){
            loadAlarm();
        }
        JSONObject thisalarm;
        if(count == -1){
            thisalarm = new JSONObject();
        } else {
            thisalarm = alarms.getJSONObject(count);
        }
        alarms.put(thisalarm);
        JSONArray conds = new JSONArray();
        thisalarm.put("name", name);
        thisalarm.put("conds", conds);
        thisalarm.put("enabled", true);
 
        Iterator<Fragment> it = frags.iterator();
        while (it.hasNext()) {
            ConditionFragment frag = (ConditionFragment) it.next();
            JSONObject cond = new JSONObject();
            cond.put("opt1", frag.cond_type.getSelectedItemPosition());
            cond.put("opt2", frag.cond_type1.getSelectedItemPosition());
            cond.put("opt3", frag.cond_type2.getSelectedItemPosition());
            cond.put("opt4", frag.cond_type3.getSelectedItemPosition());
            conds.put(cond);
        }

        // 写存储
        FileOutputStream fout = mContext.openFileOutput(STORAGE_FILENAME, Context.MODE_PRIVATE);
        fout.write(root.toString().getBytes());
        fout.close();
    }
    
    public static List<Boolean> getEnabled() {
        return mEnabled;
    }
}
