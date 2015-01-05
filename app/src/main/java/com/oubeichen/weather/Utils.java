package com.oubeichen.weather;

import org.json.JSONArray;

import android.app.Application;

/**
 * 用来获取Context
 */
public class Utils extends Application {
    private static Utils instance;

    public static Utils getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        instance = this;
    }

    /**
     * 删除JSONArray中的一项
     * @param jarray
     * @param pos
     * @return
     */
    public static JSONArray RemoveJSONArray(JSONArray jarray, int pos) {
        JSONArray Njarray = new JSONArray();
        try {
            for (int i = 0; i < jarray.length(); i++) {
                if (i != pos)
                    Njarray.put(jarray.get(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Njarray;

    }
}
