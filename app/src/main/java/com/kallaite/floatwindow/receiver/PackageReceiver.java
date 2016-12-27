package com.kallaite.floatwindow.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.kallaite.floatwindow.utils.Utils;


/**
 * Created on 16-11-25.
 *用于接收应用安装和卸载
 */
public class PackageReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String packageName = intent.getData().getSchemeSpecificPart();
        Log.v("PackageReceiver","onReceive++"+packageName +","+intent.getAction());
        if( Utils.isHideApp(packageName) ){
            return;
        }
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

    }
}
