package com.oubeichen.weather;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.oubeichen.weather.location.DBManager;
import com.oubeichen.weather.location.LocationUtil;

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
    
    private Location mLocation;

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
                refreshWeather();
            }
        }, 0, refresh_interval);// 每隔4小时

        return super.onStartCommand(intent, flags, startId);
    }
    
    private void refreshWeather() {
        String jsonString;
        try {
            String cityid;
            String city = mSharedPrefs.getString("preference_city", "nj");
            if(city.equals("bj")) {
                cityid = "101010100";
            } else if(city.equals("sh")) {
                cityid = "101020100";
            } else if(city.equals("gz")) {
                cityid = "101280101";
            } else if(city.equals("nj")) {
                cityid = "101190101";
            } else {
                LocationManager mgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                mLocation = mgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                int retry = 0;
                while(mLocation  == null && retry < 3) //只尝试五次
                {
                    mgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60000, 1, locationListener);
                    retry++;
                }
                if(retry != 3) {
                    LocationUtil.logi("location = " + mLocation.getLatitude() + "," + mLocation.getLongitude());
                    //mLocation.setLatitude(39);
                    //mLocation.setLongitude(115);
                    String cityname = LocationUtil.getLocation(String.valueOf(mLocation.getLatitude()), String.valueOf(mLocation.getLongitude()));
                    LocationUtil.logi("cityname = " + cityname);
                    cityid = DBManager.getCityId(mContext, cityname);
                } else { // 默认南京
                    cityid = "101190101";
                }
            }
            jsonString = loadFromNetwork(SOURCE_URL + cityid);
        } catch (Exception e) {
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
            // 设置通知点击事件
            Intent notifyIntent =
                    new Intent(mContext, MainActivity.class);
            notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingNotifyIntent =
                    PendingIntent.getActivity(
                            mContext,
                            0,
                            notifyIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            // 设置Notification
            String content_text = getString(R.string.notification_content_text);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle(getString(R.string.notification_content_title))
                    .setContentText(content_text.replace("{v}", String.valueOf(alarms.size())))
                    .setContentIntent(pendingNotifyIntent);
            NotificationCompat.InboxStyle inboxStyle =
                    new NotificationCompat.InboxStyle();
            // 设置可扩展Notification
            inboxStyle.setBigContentTitle(getString(R.string.big_content_title));
            // Moves alarms into the expanded layout
            for(Alarm alarm : alarms) {
                inboxStyle.addLine(alarm.getName());
            }
            // Moves the expanded layout object into the notification object.
            mBuilder.setStyle(inboxStyle);
            notificationManager.notify(0, mBuilder.build());
        }
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

    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) { //当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
            // log it when the location changes
            if (location != null) {
                Log.i("SuperMap", "Location changed : Lat: "
                        + location.getLatitude() + " Lng: "
                        + location.getLongitude());
                mLocation = location;
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };
}
