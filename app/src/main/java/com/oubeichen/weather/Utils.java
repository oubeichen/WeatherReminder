package com.oubeichen.weather;

import org.json.JSONArray;

import android.app.Application;

import java.util.HashMap;

/**
 * 一些小工具
 * 用来获取Context
 */
public class Utils extends Application {

    private static HashMap<String, Integer> mDrawableMap;

    private static Utils instance;

    public static Utils getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        instance = this;
    } static { //初始化天气所对应的drawable
        mDrawableMap = new HashMap<String, Integer>();
        mDrawableMap.put("晴", R.drawable.w0);
        mDrawableMap.put("晴转多云", R.drawable.w1);
        mDrawableMap.put("多云转晴", R.drawable.w1);
        mDrawableMap.put("晴转阴", R.drawable.w1);
        mDrawableMap.put("阴转晴", R.drawable.w1);
        mDrawableMap.put("多云转阴", R.drawable.w2);
        mDrawableMap.put("阴转多云", R.drawable.w2);
        mDrawableMap.put("阴", R.drawable.w2);
        mDrawableMap.put("多云", R.drawable.w2);
        mDrawableMap.put("雷阵雨", R.drawable.w4);
        mDrawableMap.put("小雨", R.drawable.w6);
        mDrawableMap.put("中雨", R.drawable.w7);
        mDrawableMap.put("大雨", R.drawable.w8);
        mDrawableMap.put("暴雨", R.drawable.w9);
        mDrawableMap.put("大暴雨", R.drawable.w10);
        mDrawableMap.put("雨夹雪转阴", R.drawable.w13);
        mDrawableMap.put("小雪", R.drawable.w14);
        mDrawableMap.put("中雪", R.drawable.w15);
        mDrawableMap.put("大雪", R.drawable.w16);
        mDrawableMap.put("暴雪", R.drawable.w17);
        mDrawableMap.put("雾", R.drawable.w18);
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
    
    /**
     * 获取对应的drawable 
     */
    public static int getDrawable(String weather) {
        int res;
        try {
            res = mDrawableMap.get(weather);
        } catch (Exception e) { //找不到 胡猜一个
            if(weather.contains("雨")) {
                res = R.drawable.w7;
            } else if(weather.contains("雪")) {
                res = R.drawable.w14;
            } else if(weather.contains("雾")) {
                res = R.drawable.w18;
            } else if(weather.contains("阴")) {
                res = R.drawable.w2;
            } else if(weather.contains("沙")) {
                res = R.drawable.w45;
            } else {
                res = R.drawable.w0;
            }
        }
        return res;
    }
}
