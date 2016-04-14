package com.example.matianqiong.myweather.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.matianqiong.myweather.service.AutoUpdateService;

/**
 * Created by MaTianQiong on 2016/4/13.
 */
public class AutoUpdateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i=new Intent(context, AutoUpdateService.class);
        context.startActivity(i);
    }
}
