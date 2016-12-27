package com.kallaite.floatwindow.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.kallaite.floatwindow.service.FloatWindowService;

/**
 * Created on 16-12-19.
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v("BootReceiver","boot++"+intent.getAction());
        Intent i = new Intent(context, FloatWindowService.class);
        context.startService(i);
    }
}
