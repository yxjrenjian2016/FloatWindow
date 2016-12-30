package com.kallaite.floatwindow.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.kallaite.floatwindow.service.FloatWindowService;
import com.kallaite.floatwindow.utils.Utils;

/**
 * Created on 16-12-19.
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v("BootReceiver","boot++"+intent.getAction());
        int show = Utils.readInt(context,Utils.SHOW_FLOAT_BALL,Utils.DISPLAY_FLOAT_BALL);
        if(show == Utils.DISPLAY_FLOAT_BALL);{
            Intent i = new Intent(context, FloatWindowService.class);
            context.startService(i);
        }

    }
}
