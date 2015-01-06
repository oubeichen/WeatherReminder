package com.oubeichen.weather;

import android.app.Activity;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

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

/**
 * 后台Service，刷新天气，为提醒设置Notification
 */
public class WeatherService extends Service {
    
    public static final String TAG = "WeatherService";
    
    public static final String PREFS_NAME = "WeatherStorage";

    public static final String BROADCAST_REFRESH = "com.oubeichen.weather.refresh";

    public static final String SOURCE_URL = "http://weatherapi.market.xiaomi.com/wtr-v2/weather?cityId=";
    
    private static final String CALLED_FROM_NETWORKRECEIVER = "calledFromReceiver";
    
    private Context mContext;

    private Timer timer;
    
    private Location mLocation;
    
    private Boolean mCalledFromReceiver;

    SharedPreferences mSharedPrefs;

    IntentFilter mNetworkFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);

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
        mCalledFromReceiver = false;
        if(intent != null) {
            mCalledFromReceiver = intent.getBooleanExtra(CALLED_FROM_NETWORKRECEIVER, false);
        }
        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        int refresh_interval = Integer.valueOf(mSharedPrefs.getString("preference_refresh_interval", "10800000"));
        Log.i(TAG, "refresh_interval:" + refresh_interval);
        // updateWeather();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    Looper.prepare();
                } catch (Exception e) {

                }
                // 定时更新
                if(refreshWeather()) {
                    checkAlarms();
                    mCalledFromReceiver = false;
                    //成功获取一次天气
                }
            }
        }, 0, refresh_interval);// 每隔一定时间重新获取一次天气，并且发送Notification

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 获取天气
     */
    private Boolean refreshWeather() {
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
            } else { // 通过地理位置获取天气
                LocationManager mgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                mLocation = mgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//                if(mLocation  == null)
//                {
//                    mgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60000, 1, locationListener);
//                }
                if(mLocation != null) {
                    LocationUtil.logi("location = " + mLocation.getLatitude() + "," + mLocation.getLongitude());
                    //mLocation.setLatitude(39);
                    //mLocation.setLongitude(115);
                    String cityname = LocationUtil.getLocation(String.valueOf(mLocation.getLatitude()), String.valueOf(mLocation.getLongitude()));
                    LocationUtil.logi("cityname = " + cityname);
                    cityid = DBManager.getCityId(mContext, cityname);
                    LocationUtil.logi("cityid = " + cityid);
                } else { // 默认北京
                    LocationUtil.logi("cannot get location!");
                    cityid = "101010100";
                }
            }
            jsonString = loadFromNetwork(SOURCE_URL + cityid);
        } catch (Exception e) {
            e.printStackTrace();
            // 注册一个receiver
            // 下次网络联通的时候自动刷新天气
            if(!mCalledFromReceiver) {
                Log.i(TAG, "get weather error, will try again when network available");
                try {
                    registerReceiver(mNetworkReceiver, mNetworkFilter);
                } catch (Exception ex) {
                    Log.i(TAG, "cannot register network receiver");
                }
            } else {
                // 从receiver唤醒的，不再次获取了
                Log.i(TAG, "get weather error");
            }
            return false;
        }
        Log.i(TAG, "get weather");
        SharedPreferences storage = getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE);
        WeatherManager.setWeather(jsonString, storage);
        // 发送广播
        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(Utils.getInstance());
        lbm.sendBroadcast(new Intent(BROADCAST_REFRESH));
        return true;
    }

    /**
     * 检查是否有符合条件的提醒，并且创建Notification
     */
    private void checkAlarms() {
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
        mCalledFromReceiver = false;
        // 尝试取消注册receiver
        try {
            unregisterReceiver(mNetworkReceiver);
        } catch (Exception ex) {
            Log.i(TAG, "cannot unregister network receiver");
        }
        Log.i(TAG, "service stopped");
    }

//    /**
//     * 监听物理位置变化
//     */
//    private final LocationListener locationListener = new LocationListener() {
//        public void onLocationChanged(Location location) { //当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
//            // log it when the location changes
//            if (location != null) {
//                Log.i("SuperMap", "Location changed : Lat: "
//                        + location.getLatitude() + " Lng: "
//                        + location.getLongitude());
//                mLocation = location;
//            }
//        }
//
//        @Override
//        public void onStatusChanged(String s, int i, Bundle bundle) {
//
//        }
//
//        @Override
//        public void onProviderEnabled(String s) {
//
//        }
//
//        @Override
//        public void onProviderDisabled(String s) {
//
//        }
//    };

    private final BroadcastReceiver mNetworkReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                Log.i(TAG, "network available");
                // 重启服务，达到重新获取天气的效果
                Intent s = new Intent(context, WeatherService.class);
                s.putExtra(CALLED_FROM_NETWORKRECEIVER, true);
                context.stopService(s);
                context.startService(s);
            }
        }
    };
}
