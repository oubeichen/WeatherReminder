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

/**
 * 管理提醒数据
 */
public class AlarmManager {
    private static final String TAG = "AlarmManager";
    
    private static final String STORAGE_FILENAME = "AlarmStorage.json";

    private static JSONObject mJSONRoot;
    private static JSONArray mJSONAlarms;

    private static Context mContext = Utils.getInstance();

    private static List<Alarm> mAlarms;

    private static boolean mIsOpen = false;

    /**
     * 从文件读取数据，没有数据则新建一个空的JSON
     * @return
     */
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

    /**
     * 新建或者编辑一个提醒
     * @param alarm
     * @throws JSONException
     * @throws IOException
     */
    public static void addOrEditAlarm(Alarm alarm)
            throws JSONException, IOException {
        if(!mIsOpen){
            loadAlarm();
        }
        if(!mAlarms.contains(alarm)){
            mAlarms.add(alarm);
        }
        save();
    }

    /**
     * 从JSON中读取数据到类
     * @throws JSONException
     */
    private static void reload() throws JSONException {
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

    /**
     * 存储数据到JSON文件
     * @throws IOException
     * @throws JSONException
     */
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

    /**
     * 删除一个提醒
     * @param pos 要删除的位置
     */
    public static void delAlarm(int pos){
        try {
            mAlarms.remove(pos);
            save();
        } catch (Exception e) {
        }
    }

    /**
     * 开启/关闭一个提醒
     * @param pos
     * @param enabled
     */
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
        int temp_values[] = mContext.getResources().getIntArray(R.array.temp_values);
        int temps_min[] = new int[5],temps_max[] = new int[5];
        List<String[]> index_types = new ArrayList<String[]>();
        index_types.add(mContext.getResources().getStringArray(R.array.fs_type));
        index_types.add(mContext.getResources().getStringArray(R.array.cy_type));
        index_types.add(mContext.getResources().getStringArray(R.array.yd_type));
        index_types.add(mContext.getResources().getStringArray(R.array.xc_type));
        index_types.add(mContext.getResources().getStringArray(R.array.ls_type));
        try {
            // 处理天气的最大值和最小值 如 "11℃~0℃" 的天气最大值最小值分别为 11 和 0
            for(int i = 1;i <= 5;i++) {
                String t[] = WeatherManager.temp_day[i].split("~");
                temps_max[i - 1] = Integer.valueOf(t[0].substring(0, t[0].length() - 1));
                temps_min[i - 1] = Integer.valueOf(t[1].substring(0, t[1].length() - 1));
            }
        } catch (Exception e) {
            Log.d(TAG, "Error parsing temps");
        }
        for(Alarm alarm : mAlarms) {
            if(alarm.getEnabled()) {
                Boolean flag = true;
                for(Alarm.Cond cond : alarm.getConds()) {
                    if(cond.getOpt1() == 0) { //最低气温 opt2为高于还是低于 opt3为具体的温度 opt4为哪一天
                        if(cond.getOpt2() == 0) {// 高于等于
                            if(temps_min[cond.getOpt4()] < temp_values[cond.getOpt3()]) {
                                flag = false;
                                break;
                            }
                        } else { //低于等于
                            if(temps_min[cond.getOpt4()] > temp_values[cond.getOpt3()]) {
                                flag = false;
                                break;
                            }
                        }
                    } else if(cond.getOpt1() == 1) { //最高气温 opt2为高于还是低于 opt3为具体的温度 opt4为哪一天
                        if(cond.getOpt2() == 0) {// 高于等于
                            if(temps_max[cond.getOpt4()] < temp_values[cond.getOpt3()]) {
                                flag = false;
                                break;
                            }
                        } else { //低于等于
                            if(temps_max[cond.getOpt4()] > temp_values[cond.getOpt3()]) {
                                flag = false;
                                break;
                            }
                        }
                    } else if(cond.getOpt1() >= 2 && cond.getOpt1() <= 6) { // 2,3,4,5,6 分别为防晒、穿衣、运动、洗车、晾晒指数
                        int location = cond.getOpt1() - 2; //各指数的数据存放从0开始
                        Boolean res = WeatherManager.living_index[location].equals(
                                index_types.get(location)[cond.getOpt3()]);
                        if(res == (cond.getOpt2() == 0 ? false : true)) { //0则res应该为true 1则res应该为false
                            flag = false;
                            break;
                        }
                    }
                }
                if(flag) {
                    alarms.add(alarm);
                }
            }
        }
        return alarms;
    }
}
