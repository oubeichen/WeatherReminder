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
            Date date = sdf.parse(today.getString("date"));
            
            temp_day1 = forecast.getString("temp1");
            weather_day1 = forecast.getString("weather1");
            date_day1 = sdf2.format(date);

            // day1
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DATE, 1);
            temp_day2 = forecast.getString("temp2");
            weather_day2 = forecast.getString("weather2");
            date_day2 = sdf2.format(calendar.getTime());
            
            // day2
            calendar.add(Calendar.DATE, 1);
            temp_day3 = forecast.getString("temp3");
            weather_day3 = forecast.getString("weather3");
            date_day3 = sdf2.format(calendar.getTime());

            // day3
            calendar.add(Calendar.DATE, 1);
            temp_day4 = forecast.getString("temp4");
            weather_day4 = forecast.getString("weather4");
            date_day4 = sdf2.format(calendar.getTime());

            // day4
            calendar.add(Calendar.DATE, 1);
            temp_day5 = forecast.getString("temp5");
            weather_day5 = forecast.getString("weather5");
            date_day5 = sdf2.format(calendar.getTime());

            // living index
            JSONArray indexArray = jsonObject.getJSONArray("index");
            living_length = indexArray.length();
            for(int i = 0;i < living_length; i++) {
                JSONObject index = indexArray.getJSONObject(i);
                living_name[i] = index.getString("name");
                living_index[i] = index.getString("index");
                living_des[i] = index.getString("details");
            }
            
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
        editor.putString("temp_today", temp_today);
        editor.putString("weather_today", weather_today);
        editor.putString("wind_direction_today", wind_direction_today);
        editor.putString("wind_scale_today", wind_scale_today);
        editor.putString("update_time", update_time);
        
        editor.putString("temp_day1", temp_day1);
        editor.putString("weather_day1", weather_day1);
        editor.putString("date_day1", date_day1);
        editor.putString("temp_day2", temp_day2);
        editor.putString("weather_day2", weather_day2);
        editor.putString("date_day2", date_day2);
        editor.putString("temp_day3", temp_day3);
        editor.putString("weather_day3", weather_day3);
        editor.putString("date_day3", date_day3);
        editor.putString("temp_day4", temp_day4);
        editor.putString("weather_day4", weather_day4);
        editor.putString("date_day4", date_day4);
        editor.putString("temp_day5", temp_day5);
        editor.putString("weather_day5", weather_day5);
        editor.putString("date_day5", date_day5);
        
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
        temp_today = storage.getString("temp_today", "");
        weather_today = storage.getString("weather_today", "");
        wind_direction_today = storage.getString("wind_direction_today", "");
        wind_scale_today = storage.getString("wind_scale_today", "");
        update_time = storage.getString("update_time", "");
        
        temp_day1 = storage.getString("temp_day1", "");
        weather_day1 = storage.getString("weather_day1", "");
        date_day1 = storage.getString("date_day1", "");
        
        temp_day2 = storage.getString("temp_day2", "");
        weather_day2 = storage.getString("weather_day2", "");
        date_day2 = storage.getString("date_day2", "");
        
        temp_day3 = storage.getString("temp_day3", "");
        weather_day3 = storage.getString("weather_day3", "");
        date_day3 = storage.getString("date_day3", "");
        
        temp_day4 = storage.getString("temp_day4", "");
        weather_day4 = storage.getString("weather_day4", "");
        date_day4 = storage.getString("date_day4", "");
        
        temp_day5 = storage.getString("temp_day5", "");
        weather_day5 = storage.getString("weather_day5", "");
        date_day5 = storage.getString("date_day5", "");
        
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
    
    public static String temp_day1;
    public static String weather_day1;
    public static String date_day1;
    public static String temp_day2;
    public static String weather_day2;
    public static String date_day2;
    public static String temp_day3;
    public static String weather_day3;
    public static String date_day3;
    public static String temp_day4;
    public static String weather_day4;
    public static String date_day4;
    public static String temp_day5;
    public static String weather_day5;
    public static String date_day5;
    
    public static int living_length = 0;
    public static String[] living_name = new String[5];
    public static String[] living_index = new String[5];
    public static String[] living_des = new String[5];
}
