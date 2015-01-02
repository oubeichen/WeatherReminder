package com.oubeichen.weather;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

/**
 * A simple launcher activity containing a summary sample description, sample
 * log and a custom {@link android.support.v4.app.Fragment} which can display a
 * view.
 * <p>
 * For devices with displays with a width of 720dp or greater, the sample log is
 * always visible, on other devices it's visibility is controlled by an item on
 * the Action Bar.
 */
public class MainActivity extends FragmentActivity {

    public static final String TAG = "MainActivity";

    private SlidingTabsBasicFragment mFragment;

    BroadcastMain broadcastMain;
    
    private LocalBroadcastManager mLbm;
    
    private static Handler handler = new Handler();
    
    public static final int ADD_ALARM_REQUEST = 0x01;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // 设置Prefrence的默认值
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager()
                    .beginTransaction();
            mFragment = new SlidingTabsBasicFragment();
            transaction.replace(R.id.sample_content_fragment, mFragment);
            transaction.commit();
        }

        // log whether the service is running
        if(isServiceRunning(this, WeatherService.class.getName())){
            System.out.println("service is running now");
        } else {
            System.out.println("service is not running, restarting...");
            Intent intent = new Intent(this, WeatherService.class);
            startService(intent);
        }
        
        // receive the broadcast from the WeatherService
        broadcastMain = new BroadcastMain();
        IntentFilter filter = new IntentFilter();
        filter.addAction(WeatherService.BROADCAST_REFRESH);
        mLbm = LocalBroadcastManager.getInstance(this);
        mLbm.registerReceiver(broadcastMain, filter);

        Intent intent = new Intent();
        intent.setClass(this, WeatherService.class);
        stopService(intent);
        startService(intent);
    }

    public void refresh(View v) {
        Intent intent = new Intent();
        intent.setClass(this, WeatherService.class);
        stopService(intent);
        startService(intent);
    }

    @Override
    protected void onDestroy() {
        //Intent intent = new Intent();
        //intent.setClass(this, WeatherService.class);
        //stopService(intent);
        mLbm.unregisterReceiver(broadcastMain);
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
        intent.putExtra("Count", -1);
        startActivityForResult(intent, ADD_ALARM_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) { 
        if (requestCode == ADD_ALARM_REQUEST) { 
            if (resultCode == RESULT_OK) {
                // refresh ui
                mFragment.getViewPager().getAdapter().notifyDataSetChanged();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
