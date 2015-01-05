package com.oubeichen.weather.location;

/**
 * Created by oubeichen on 2015/1/5 0005.
 */
import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class LocationUtil {
    //log的标签
    public static final String TAG = "location";
    public static final boolean DEBUG = true;

    public static void logi(String content){
        if(DEBUG) {
            Log.i(TAG, content);
        }
    }

    public static void loge(String content){
        if(DEBUG) {
            Log.e(TAG, content);
        }
    }

    public static void logd(String content){
        if(DEBUG) {
            Log.d(TAG, content);
        }
    }

    /**
     * 获取地理位置
     *
     * @throws Exception
     */
    public static String getLocation(String latitude, String longitude) throws Exception {
        String resultString = "";

        /** 这里采用get方法，直接将参数加到URL上 */
        String urlString = String.format("http://maps.googleapis.com/maps/api/geocode/json?language=zh-CN&latlng=%s,%s", latitude, longitude);
        LocationUtil.logi("Util: getLocation: URL: " + urlString);

        /** 新建HttpClient */
        HttpClient client = new DefaultHttpClient();
        /** 采用GET方法 */
        HttpGet get = new HttpGet(urlString);
        try {
            /** 发起GET请求并获得返回数据 */
            HttpResponse response = client.execute(get);
            HttpEntity entity = response.getEntity();
            BufferedReader buffReader = new BufferedReader(new InputStreamReader(entity.getContent()));
            StringBuffer strBuff = new StringBuffer();
            String result = null;
            while ((result = buffReader.readLine()) != null) {
                strBuff.append(result);
            }
            /** 解析JSON数据，获得物理地址 */
            if (strBuff.length() > 0) {
                JSONObject jsonobject = new JSONObject(strBuff.toString());
                JSONArray jsonArray = jsonobject.getJSONArray("results");
                JSONObject jsonResult = jsonArray.getJSONObject(0);
                // address_components下 选locality地级市
                JSONArray components = jsonResult.getJSONArray("address_components");
                int len =components.length();
                for(int i = 0; i < len; i++) {
                    String types = components.getJSONObject(i).getJSONArray("types").getString(0);
                    if(types.equals("locality")) {
                        resultString = components.getJSONObject(i).getString("long_name");
                        break;
                    }
                }
                if(resultString.endsWith("市")) {
                    resultString = resultString.substring(0, resultString.length() - 1);
                }
            }
        } catch (Exception e) {
            throw new Exception("获取物理位置出现错误:" + e.getMessage());
        } finally {
            get.abort();
            client = null;
        }

        return resultString;
    }
}
