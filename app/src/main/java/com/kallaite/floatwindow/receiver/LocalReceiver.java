package com.kallaite.floatwindow.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.kallaite.floatwindow.callback.IServiceViewCallback;
import com.kallaite.floatwindow.service.FloatWindowService;
import com.kallaite.floatwindow.utils.Utils;


/**
 * Created on 16-11-25.
 * 本地广播接收。用于接收用户点击，控制悬浮窗显示
 */
public class LocalReceiver extends BroadcastReceiver {

    private IServiceViewCallback mViewCallback;

    public LocalReceiver(IServiceViewCallback callback) {
        mViewCallback = callback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.v("LocalReceiver", "action:" + action);
        if(TextUtils.isEmpty(action)){
            return;
        }
        if (Utils.ACTION_FLOAT_WINDOW.equals(action)) {
            int cmd = intent.getIntExtra(Utils.ACTION_CMD, 0);
            Log.v("LocalReceiver", "cmd:" + cmd);
            switch (cmd) {
                case Utils.CMD_ADD_FLOAT_BALL:
                    mViewCallback.creatFloatBallWithAnimation();
                    break;
                case Utils.CMD_REMOVE_FLAOT_BALL://UI控制不显示任何悬浮窗
                    mViewCallback.removeAllWindow(true);
                    break;
                case Utils.CMD_ADD_HOME_WINDOW:
                    mViewCallback.createHomeWindow();

                    break;
                case Utils.CMD_ADD_FLOAT_BALL_DELAY:
                    mViewCallback.creatFloatBallWithAnimationDelay();
                    break;
                case Utils.CMD_FLOAT_BALL_SIZE:
                    String size = intent.getStringExtra(Utils.FLOAT_BALL_SIZE);
                    mViewCallback.setFloatBallSize(Integer.valueOf(size));
                    mViewCallback.createFloatBall();
                    break;
                case Utils.CMD_ADD_ALL_APP_WINDOW:
                    String pos = intent.getStringExtra(Utils.ACTION_POSITION);
                    mViewCallback.createInstalledAppWindow(pos);
                    break;
                case Utils.CMD_ADD_COLLECT_APP_WINDOW:
                    mViewCallback.createCollectView();
                    break;
                case Utils.CMD_ADD_HOME_WINDOW_WITH_ANIMATION:
                    mViewCallback.createHomeWindowWithAnimation();
                    break;
                default:
                    break;
            }
        }else if (action.equals(Intent.ACTION_PACKAGE_ADDED)) {
            String packageName = intent.getData().getSchemeSpecificPart();
            if( !Utils.isLaunchApp(context,packageName)){
                return;
            }
            mViewCallback.addPackage(packageName);
        }else if (action.equals(Intent.ACTION_PACKAGE_REMOVED)) {
            String packageName = intent.getData().getSchemeSpecificPart();
            mViewCallback.removePackage(packageName);
        } else if( action.equals(Intent.ACTION_SCREEN_OFF ) ){
            mViewCallback.removeAllWindow(false);
        }else if( action.equals(Intent.ACTION_SCREEN_ON )){
            if(Utils.readInt(context,Utils.SHOW_FLOAT_BALL,-1) == Utils.DISPLAY_FLOAT_BALL){
                mViewCallback.createFloatBall();
            }
        }
    }
}
