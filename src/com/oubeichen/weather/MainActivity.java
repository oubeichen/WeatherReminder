package com.oubeichen.weather;

import java.util.List;

import com.oubeichen.weather.common.activities.SampleActivityBase;
import com.oubeichen.weather.common.logger.Log;
import com.oubeichen.weather.common.logger.LogFragment;
import com.oubeichen.weather.common.logger.LogWrapper;
import com.oubeichen.weather.common.logger.MessageOnlyLogFilter;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

/**
 * A simple launcher activity containing a summary sample description, sample
 * log and a custom {@link android.support.v4.app.Fragment} which can display a
 * view.
 * <p>
 * For devices with displays with a width of 720dp or greater, the sample log is
 * always visible, on other devices it's visibility is controlled by an item on
 * the Action Bar.
 */
public class MainActivity extends SampleActivityBase {

    public static final String TAG = "MainActivity";

    private SlidingTabsBasicFragment mFragment;

    BroadcastMain broadcastMain;
    
    private static Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager()
                    .beginTransaction();
            mFragment = new SlidingTabsBasicFragment();
            transaction.replace(R.id.sample_content_fragment, mFragment);
            transaction.commit();
        }

        // log whether the service is running
        if(isServiceRunning(this, RefreshWeatherService.class.getName())){
            System.out.println("service is running now");
        } else {
            System.out.println("service is not running");
        }
        
        // receive the broadcast from the RefreshWeatherService
        broadcastMain = new BroadcastMain();
        IntentFilter filter = new IntentFilter();
        filter.addAction(RefreshWeatherService.BROADCAST_REFRESH);
        registerReceiver(broadcastMain, filter);
    }

    /** Create a chain of targets that will receive log data */
    @Override
    public void initializeLogging() {
        // Wraps Android's native log framework.
        LogWrapper logWrapper = new LogWrapper();
        // Using Log, front-end to the logging chain, emulates android.util.log
        // method signatures.
        Log.setLogNode(logWrapper);

        // Filter strips out everything except the message text.
        MessageOnlyLogFilter msgFilter = new MessageOnlyLogFilter();
        logWrapper.setNext(msgFilter);

        // On screen logging via a fragment with a TextView.
        LogFragment logFragment = (LogFragment) getSupportFragmentManager()
                .findFragmentById(R.id.log_fragment);
        msgFilter.setNext(logFragment.getLogView());

        Log.i(TAG, "Ready");
    }

    public void refresh(View v) {
        Intent intent = new Intent();
        intent.setClass(this, RefreshWeatherService.class);
        stopService(intent);
        startService(intent);
    }

    @Override
    protected void onDestroy() {
        //Intent intent = new Intent();
        //intent.setClass(this, RefreshWeatherService.class);
        //stopService(intent);
        unregisterReceiver(broadcastMain);
        super.onDestroy();
    }

    public class BroadcastMain extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            handler.post(new MyRunnable());
        }
    }

    public class MyRunnable implements Runnable {
        @Override
        public void run() {
            // Refresh UI
            mFragment.getViewPager().getAdapter().notifyDataSetChanged();
        }
    }
    
    // 判断服务线程是否存在
    public static boolean isServiceRunning(Context context, String serviceName) {
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningServiceInfo> list = am.getRunningServices(30);
        for (RunningServiceInfo info : list) {
            if (info.service.getClassName().equals(serviceName)) {
                return true;
            }
        }
        return false;
    }
    
    public void onAddAlarmClick(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, AddAlarmActivity.class);
        intent.putExtra("Count", 1);
        startActivity(intent);
    }
}
