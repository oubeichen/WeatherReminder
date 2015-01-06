package com.oubeichen.weather;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {
    public BootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent s = new Intent(context, WeatherService.class);
        context.startService(s);
    }
}
