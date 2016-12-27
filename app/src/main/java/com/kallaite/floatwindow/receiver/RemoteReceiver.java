package com.kallaite.floatwindow.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.kallaite.floatwindow.utils.Utils;


/**
 * Created on 16-11-25.
 * 熄灭和点亮屏幕以及后续扩展的广播
 */
public class RemoteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        Log.v("RemoteReceiver","onReceive++"+action );
        int show = Utils.readInt(context,Utils.SHOW_FLOAT_BALL,Utils.HIDE_FLOAT_BALL);
        if( show == Utils.DISPLAY_FLOAT_BALL){
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }

    }
}
