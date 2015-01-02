package com.oubeichen.weather;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;

@SuppressLint("SimpleDateFormat")
public class WeatherManager
{

    private static boolean isOpen = false;

    public static void setWeather(String weather, SharedPreferences storage){
        JSONObject jsonObject;
        try
        {
            jsonObject = new JSONObject(weather);

            // update realtime start
            JSONObject realtime = jsonObject.getJSONObject("realtime");
            // update temp
            temp_today = realtime.getString("temp") + "℃";
            // update weather
            weather_today = realtime.getString("weather");
            // update wind
            wind_direction_today = realtime.getString("WD");
            wind_scale_today = realtime.getString("WS");
            // update time
            update_time = realtime.getString("time");
            // update realtime end

            // update forecast
            JSONObject forecast = jsonObject.getJSONObject("forecast");
            JSONObject today = jsonObject.getJSONObject("today");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
            SimpleDateFormat sdf2 = new SimpleDateFormat("MM月dd日");
            // today
            date_text = forecast.getString("date_y");
            Date date = sdf.parse(today.getString("date"));
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            
            for(int i = 1;i <= 5;i++) {
                temp_day[i] = forecast.getString("temp" + i);
                weather_day[i] = forecast.getString("weather" + i);
                date_day[i] = sdf2.format(calendar.getTime());
                calendar.add(Calendar.DATE, 1);
            }

            // living index
            JSONArray indexArray = jsonObject.getJSONArray("index");
            living_length = indexArray.length();
            for(int i = 0;i < living_length; i++) {
                JSONObject index = indexArray.getJSONObject(i);
                living_name[i] = index.getString("name");
                living_index[i] = index.getString("index");
                living_des[i] = index.getString("details");
            }

            // city name
            cityname = forecast.getString("city");
            isOpen = true;
            storeWeather(storage);
        }
        catch (JSONException e)
        {

        } catch (ParseException e) {

        }
    }
    
    private static boolean storeWeather(SharedPreferences storage){
        if(!isOpen){
            return false;
        }
        SharedPreferences.Editor editor = storage.edit();
        // 保存实时天气
        editor.putString("temp_today", temp_today);
        editor.putString("weather_today", weather_today);
        editor.putString("wind_direction_today", wind_direction_today);
        editor.putString("wind_scale_today", wind_scale_today);
        editor.putString("update_time", update_time);
        editor.putString("cityname", cityname);
        editor.putString("date_text", date_text);
        // 保存近五天天气
        for(int i = 1;i <= 5;i++) {
            editor.putString("temp_day" + i, temp_day[i]);
            editor.putString("weather_day" + i, weather_day[i]);
            editor.putString("date_day" + i, date_day[i]);
        }
        // 保存生活指数
        editor.putInt("living_length", living_length);
        for(int i = 0;i < living_length; i++) {
            editor.putString("living_name" + i, living_name[i]);
            editor.putString("living_index" + i, living_index[i]);
            editor.putString("living_des" + i, living_des[i]);
        }
        
        // 不要忘记提交commit()
        editor.commit();
        return true;
    }
    
    public static boolean loadWeather(SharedPreferences storage){ 
        if(!storage.contains("temp_today")){
            return false;
        }
        // 获取实时天气
        temp_today = storage.getString("temp_today", "");
        weather_today = storage.getString("weather_today", "");
        wind_direction_today = storage.getString("wind_direction_today", "");
        wind_scale_today = storage.getString("wind_scale_today", "");
        update_time = storage.getString("update_time", "");
        cityname = storage.getString("cityname", "");
        date_text = storage.getString("date_text", "");
        
        // 获取近五天天气
        for(int i = 1;i <= 5;i++) {
            temp_day[i] = storage.getString("temp_day" + i, "");
            weather_day[i] = storage.getString("weather_day" + i, "");
            date_day[i] = storage.getString("date_day" + i, "");
        }
        // 获取生活指数
        living_length = storage.getInt("living_length", 0);
        for(int i = 0;i < living_length; i++) {
            living_name[i] = storage.getString("living_name" + i, "");
            living_index[i] = storage.getString("living_index" + i, "");
            living_des[i] = storage.getString("living_des" + i, "");
        }
        
        return true;
    }
    
    public static String temp_today;
    public static String weather_today;
    public static String wind_direction_today;
    public static String wind_scale_today;
    public static String update_time;
    public static String cityname;
    public static String date_text;
    
    public static String[] temp_day = new String[6];
    public static String[] weather_day = new String[6];
    public static String[] date_day = new String[6];
    
    public static int living_length = 0;
    public static String[] living_name = new String[5];
    public static String[] living_index = new String[5];
    public static String[] living_des = new String[5];
}
