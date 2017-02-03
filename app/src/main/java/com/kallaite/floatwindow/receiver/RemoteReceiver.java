package com.kallaite.floatwindow.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.kallaite.floatwindow.utils.Utils;


/**
 * Created on 16-11-25.
 * 熄灭和点亮屏幕以及后续扩展的广播，统一转发
 */
public class RemoteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        Log.v("RemoteReceiver","onReceive++"+action );
        int show = Utils.readInt(context,Utils.SHOW_FLOAT_BALL,Utils.HIDE_FLOAT_BALL);//如果悬浮窗是显示状态，那么intent继续传递
        if( show == Utils.DISPLAY_FLOAT_BALL){
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }

    }
}
