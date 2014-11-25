package com.oubeichen.weather;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import com.oubeichen.weather.common.logger.Log;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

public class RefreshWeatherService extends Service {
    
    public static final String TAG = "RefreshWeatherService";
    
    public static final String PREFS_NAME = "WeatherStorage";

    public static final String BROADCAST_REFRESH = "com.oubeichen.weather.refresh";

    public static final String SOURCE_URL = "http://weatherapi.market.xiaomi.com/wtr-v2/weather?cityId=101190101";

    private Timer timer;

    public RefreshWeatherService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "service started");
        // updateWeather();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // 定时更新
                String jsonString;
                try {
                    jsonString = loadFromNetwork(SOURCE_URL);
                } catch (IOException e) {
                    Log.i(TAG, "get weather error");
                    return;
                }
                Log.i(TAG, "get weather");
                SharedPreferences storage = getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE);
                WeatherManager.setWeather(jsonString, storage);
                // 发送广播
                Intent intent = new Intent();
                intent.setAction(BROADCAST_REFRESH);
                sendBroadcast(intent);
            }
        }, 0, 4 * 3600 * 1000);// 每隔4小时

        return super.onStartCommand(intent, flags, startId);
    }
    
    
    /** Initiates the fetch operation. */
    private String loadFromNetwork(String url) throws IOException {
        InputStream stream = null;
        String str = null;
        try {
            stream = downloadUrl(url);
            str = readIt(stream, 50000);//500KB

        } finally {
            if (stream != null) {
                stream.close();
            }
        }
        return str;
    }

    /**
     * Given a string representation of a URL, sets up a connection and gets
     * an input stream.
     * @param urlString A string representation of a URL.
     * @return An InputStream retrieved from a successful HttpURLConnection.
     * @throws java.io.IOException
     */
    private InputStream downloadUrl(String urlString) throws IOException {
        // BEGIN_INCLUDE(get_inputstream)
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Start the query
        conn.connect();
        InputStream stream = conn.getInputStream();
        return stream;
        // END_INCLUDE(get_inputstream)
    }

    /** Reads an InputStream and converts it to a String.
     * @param stream InputStream containing HTML from targeted site.
     * @param len Length of string that this method returns.
     * @return String concatenated according to len parameter.
     * @throws java.io.IOException
     * @throws java.io.UnsupportedEncodingException
     */
    private String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if(timer != null){
            timer.cancel();
        }
        Log.i(TAG, "service stopped");
    }
}
