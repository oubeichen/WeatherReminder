package com.oubeichen.weather;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AlarmManager {
    private static final String STORAGE_FILENAME = "AlarmStorage.json";

    private static JSONObject mJSONRoot;
    private static JSONArray mJSONAlarms;

    private static Context mContext = Utils.getInstance();

    private static List<Alarm> mAlarms;

    private static boolean mIsOpen = false;

    public static List<Alarm> loadAlarm() {
        byte[] bbuf = new byte[100000];
        int len;
        mAlarms = new ArrayList<Alarm>();
        try {
            // 读存储
            FileInputStream fin = mContext.openFileInput(STORAGE_FILENAME);
            len = fin.read(bbuf);
            fin.close();
            String str = new String(bbuf, 0, len);
            mJSONRoot = new JSONObject(str);
            mJSONAlarms = mJSONRoot.getJSONArray("alarms");
            reload();
        } catch (Exception ex) {
            mJSONRoot = new JSONObject();
            mJSONAlarms = new JSONArray();
            try {
                mJSONRoot.put("alarms", mJSONAlarms);
            } catch (JSONException e) {
            }
        }
        mIsOpen = true;
        return mAlarms;
    }

    public static void addOrEditAlarm(Alarm alarm)
            throws JSONException, IOException {
        if(!mIsOpen){
            loadAlarm();
        }
        if(!mAlarms.contains(alarm)){
            Log.i("AlarmManager", "add alarm, now size " + mAlarms.size() + mIsOpen);
            mAlarms.add(alarm);
            Log.i("AlarmManager", "add alarm finished, now size " + mAlarms.size());
        }
        save();
    }

    private static void reload() throws JSONException {
        Log.i("AlarmManager", "reload");
        mAlarms.clear();
        int len = mJSONAlarms.length();
        for(int i = 0;i < len; i++) {
            JSONObject alarmJSON = mJSONAlarms.getJSONObject(i);
            JSONArray condsJSON = alarmJSON.getJSONArray("conds");
            Alarm alarm = new Alarm();
            alarm.setName(alarmJSON.getString("name"));
            alarm.setEnabled(alarmJSON.getBoolean("enabled"));
            ArrayList<Alarm.Cond> conds = new ArrayList<Alarm.Cond>();
            alarm.setConds(conds);
            int count = condsJSON.length();
            for(int j = 0;j < count;j++){
                JSONObject condJSON = condsJSON.getJSONObject(j);
                Alarm.Cond cond = alarm.new Cond();
                cond.setOpt1(condJSON.getInt("opt1"));
                cond.setOpt2(condJSON.getInt("opt2"));
                cond.setOpt3(condJSON.getInt("opt3"));
                cond.setOpt4(condJSON.getInt("opt4"));
                conds.add(cond);
            }
            mAlarms.add(alarm);
        }
    }

    private static void save() throws IOException, JSONException {
        mJSONAlarms = new JSONArray();
        mJSONRoot.put("alarms", mJSONAlarms);

        for (Alarm alarm : mAlarms) {
            JSONObject alarmJSON = new JSONObject();
            mJSONAlarms.put(alarmJSON);

            JSONArray jsonConds = new JSONArray();
            alarmJSON.put("name", alarm.getName());
            alarmJSON.put("conds", jsonConds);
            alarmJSON.put("enabled", alarm.getEnabled());

            for (Alarm.Cond condition : alarm.getConds()) {
                JSONObject jsonCond = new JSONObject();
                jsonCond.put("opt1", condition.getOpt1());
                jsonCond.put("opt2", condition.getOpt2());
                jsonCond.put("opt3", condition.getOpt3());
                jsonCond.put("opt4", condition.getOpt4());
                jsonConds.put(jsonCond);
            }
        }
        // 写存储
        FileOutputStream fout = mContext.openFileOutput(STORAGE_FILENAME, Context.MODE_PRIVATE);
        fout.write(mJSONRoot.toString().getBytes());
        fout.close();

    }

    public static void delAlarm(int pos){
        try {
            mAlarms.remove(pos);
            save();
        } catch (Exception e) {
        }
    }

    public static void setEnabled(int pos, Boolean enabled) {
        try {
            mAlarms.get(pos).setEnabled(enabled);
            save();
        } catch (Exception e) {

        }
    }
    
    public static List<Alarm> getAlarms() {
        if(!mIsOpen) {
            return loadAlarm();
        }
        return mAlarms;
    }

    /**
     * * 检查Alarms是否符合触发条件
     * @return 符合条件的Alarms
     */
    public static List<Alarm> checkAlarms() {
        if(!mIsOpen){
            loadAlarm();
        }
        List<Alarm> alarms = new ArrayList<Alarm>();
        for(Alarm alarm:mAlarms) {
            
        }
        return alarms;
    }
}
