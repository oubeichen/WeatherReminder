package com.oubeichen.weather;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class WeatherService extends Service {
    
    public static final String TAG = "WeatherService";
    
    public static final String PREFS_NAME = "WeatherStorage";

    public static final String BROADCAST_REFRESH = "com.oubeichen.weather.refresh";

    public static final String SOURCE_URL = "http://weatherapi.market.xiaomi.com/wtr-v2/weather?cityId=";
    
    private Context mContext;

    private Timer timer;

    SharedPreferences mSharedPrefs;

    public WeatherService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "service started");
        mContext = this;
        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        
        int refresh_interval = Integer.valueOf(mSharedPrefs.getString("preference_refresh_interval", "10800000"));
        Log.i(TAG, "refresh_interval:" + refresh_interval);
        // updateWeather();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // 定时更新
                String jsonString;
                try {
                    int cityid;
                    String city = mSharedPrefs.getString("preference_city", "nj");
                    if(city.equals("bj")) {
                        cityid = 101010100;
                    } else if(city.equals("sh")) {
                        cityid = 101020100;
                    } else if(city.equals("gz")) {
                        cityid = 101280101;
                    } else if(city.equals("nj")) {
                        cityid = 101190101;
                    } else {
                        cityid = 101190101;
                    }
                    jsonString = loadFromNetwork(SOURCE_URL + cityid);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i(TAG, "get weather error");
                    return;
                }
                Log.i(TAG, "get weather");
                SharedPreferences storage = getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE);
                WeatherManager.setWeather(jsonString, storage);
                // 发送广播
                LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(Utils.getInstance());
                lbm.sendBroadcast(new Intent(BROADCAST_REFRESH));
                
                List<Alarm> alarms = AlarmManager.checkAlarms();
                
                if(alarms != null && alarms.size() > 0) {
                    NotificationManager notificationManager = (NotificationManager)
                            mContext.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
                    // Creates an Intent for the Activity
                    Intent notifyIntent =
                            new Intent(mContext, MainActivity.class);
                    // Sets the Activity to start in a new, empty task
                    notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    PendingIntent pendingNotifyIntent =
                            PendingIntent.getActivity(
                                    mContext,
                                    0,
                                    notifyIntent,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            );
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext)
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setContentTitle("你有新的天气提醒")
                            .setContentText("天气提醒")
                            .setContentIntent(pendingNotifyIntent);
                    NotificationCompat.InboxStyle inboxStyle =
                            new NotificationCompat.InboxStyle();
                    // Sets a title for the Inbox in expanded layout
                    inboxStyle.setBigContentTitle("提醒内容：");
                    // Moves events into the expanded layout
                    for(Alarm alarm : alarms) {
                        inboxStyle.addLine(alarm.getName());
                    }
                    // Moves the expanded layout object into the notification object.
                    mBuilder.setStyle(inboxStyle);
                    notificationManager.notify(0, mBuilder.build());
                }
            }
        }, 0, refresh_interval);// 每隔4小时

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
        return conn.getInputStream();
        // END_INCLUDE(get_inputstream)
    }

    /** Reads an InputStream and converts it to a String.
     * @param stream InputStream containing HTML from targeted site.
     * @param len Length of string that this method returns.
     * @return String concatenated according to len parameter.
     * @throws java.io.IOException
     * @throws java.io.UnsupportedEncodingException
     */
    private String readIt(InputStream stream, int len) throws IOException {
        Reader reader;
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
