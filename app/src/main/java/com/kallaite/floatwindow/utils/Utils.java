package com.kallaite.floatwindow.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;


import com.kallaite.floatwindow.R;

import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created on 16-11-18.
 */
public class Utils {

    public static final String FLOAT_WINDOW_DATA = "float_window_data";

    public static final String SHOW_FLOAT_BALL = "show_float_ball";

    public static final String FLOAT_BALL_SIZE = "float_ball_size";

    public static final String ACTION_FLOAT_WINDOW = "float_window";

    public static final String ACTION_CMD = "action_cmd";

    public static final String ACTION_POSITION = "action_position";//位置

    public static final int CMD_ADD_FLOAT_BALL = 1;//添加悬浮球

    public static final int CMD_REMOVE_FLAOT_BALL = 2;//移除悬浮求

    public static final int CMD_ADD_HOME_WINDOW = 3;//添加中间布局

    public static final int CMD_ADD_ALL_APP_WINDOW = 4;//显示安装应用布局

    public static final int CMD_ADD_COLLECT_APP_WINDOW = 5;//显示收藏应用布局

    public static final int CMD_ADD_FLOAT_BALL_DELAY = 6;//创建悬浮球,带延时

    public static final int CMD_FLOAT_BALL_SIZE = 7;//修改悬浮球大小

    public static final int CMD_ADD_PACKAGE = 8;//安装应用

    public static final int CMD_REMOVE_PACKAGE = 9;//卸载应用

    public static final int CMD_ENTER_STANDBY = 10;//熄屏进standby

    public static final int CMD_EXIT_STANDBY = 11;//亮屏退出standby

    public static final int CMD_ADD_HOME_WINDOW_WITH_ANIMATION = 12;//添加中间布局带动画

    public static final int DISPLAY_FLOAT_BALL = 1;//1显示悬浮球

    public static final int HIDE_FLOAT_BALL = 0;//0不显示悬浮球

    public static final String[] HIDE_PACKAGE = new String[]{"com.kallaite.floatwindow"};//忽略的应用，不显示

    /**
     * @param context Context
     * @param cmd 命令值
     *
     */
    public static void doWithFloatWindow(Context context,int cmd) {
        Intent intent = new Intent(Utils.ACTION_FLOAT_WINDOW);
        intent.putExtra(Utils.ACTION_CMD,cmd);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    /**
     * @param context Context
     * @param cmd 命令值
     * @param key 附带键
     * @param value 附带值
     */
    public static void doWithFloatWindowWithExtra(Context context,int cmd,String key,String value) {
        Intent intent = new Intent(Utils.ACTION_FLOAT_WINDOW);
        intent.putExtra(Utils.ACTION_CMD,cmd);
        intent.putExtra(key,value);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    /**
     * 点击启动应用后显示悬浮球
     * @param context Context
     * @param packageName 包名
     */
    public static void startApp(Context context, String packageName){

        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if(intent != null){
            context.startActivity(intent);
        }else {
            Toast.makeText(context,context.getResources().getString(R.string.package_not_found),Toast.LENGTH_SHORT).show();
        }
        doWithFloatWindow(context,Utils.CMD_ADD_FLOAT_BALL);
    }

    /**
     * 悬浮球的大小和进度条进度转换
     * 进度条范围0-100
     * 球大小：70-90dp
     * @param context Context
     * @param size 大小
     * @return
     */
    public static int sizeToProgress(Context context,int size){
        int max = context.getResources().getDimensionPixelSize(R.dimen.fw_50dp);
        int min = context.getResources().getDimensionPixelSize(R.dimen.fw_20dp);
        int progress = (int) (100f * (size - min)/(max - min));
        return progress;
    }

    /**
     * 进度条进度和悬浮球的大小转换
     * @param context Context
     * @param progress 进度
     * @return
     */
    public static int progressToSize(Context context,int progress){
        int max = context.getResources().getDimensionPixelSize(R.dimen.fw_50dp);
        int min = context.getResources().getDimensionPixelSize(R.dimen.fw_20dp);
        int size = (int) (min + (max - min)/100f * progress);
        return size;
    }

    /**
     * 获取所有从桌面启动的应用(去除了隐藏应用)
     * @param context Context
     * @return
     */
    public static ArrayList<PackageInfo> getLaunchApp(Context context){
        PackageManager packageManager = context.getPackageManager();
        ArrayList<PackageInfo> allPackageInfos = (ArrayList<PackageInfo>) packageManager.getInstalledPackages(0);
        ArrayList<PackageInfo> packageInfos = new ArrayList<PackageInfo>();
        for( PackageInfo info:allPackageInfos){
            if( !isHideApp(info.packageName) && isLaunchApp(context,info.packageName)){
                packageInfos.add(info);
            }
        }
        return packageInfos;
    }

    /**
     * 是否是隐藏的应用
     * @param packageName 包名
     * @return
     */
    public static boolean isHideApp(String packageName){
        for(int i = 0; i < Utils.HIDE_PACKAGE.length; i++){
            if( packageName.equals(Utils.HIDE_PACKAGE[i])){
                return true;
            }
        }
        return false;
    }

    /**
     * 是否是可从桌面启动的应用
     * @param context Context
     * @param name 包名
     * @return
     */
    public static boolean isLaunchApp(Context context,String name){
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(name);
       return (intent != null);
    }

    public static void writeString(Context context,String key, String value){
        SharedPreferences preferences = context.getSharedPreferences(context.getPackageName(),Context.MODE_PRIVATE);
        preferences.edit().putString(key,value).apply();
    }

    public static String readString(Context context,String key, String value){
        SharedPreferences preferences = context.getSharedPreferences(context.getPackageName(),Context.MODE_PRIVATE);
        return preferences.getString(key,value);
    }

    public static void writeInt(Context context,String key, int value){
        SharedPreferences preferences = context.getSharedPreferences(context.getPackageName(),Context.MODE_PRIVATE);
        preferences.edit().putInt(key,value).apply();
    }

    public static int readInt(Context context,String key, int value){
        SharedPreferences preferences = context.getSharedPreferences(context.getPackageName(),Context.MODE_PRIVATE);
        return preferences.getInt(key,value);
    }

    public static void removeKey(Context context,String key){
        SharedPreferences preferences = context.getSharedPreferences(context.getPackageName(),Context.MODE_PRIVATE);
         preferences.edit().remove(key);
    }

    /**
     * 要求设备root
     * @param cmd 命令
     * @return
     */
    public static void exec(String cmd) {
        try {
            OutputStream os = Runtime.getRuntime().exec("su").getOutputStream();
            os.write(cmd.getBytes());
            os.flush();
            os.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 用于获取状态栏的高度。
     *
     * @return 返回状态栏高度的像素值。
     */
    public  static int getStatusBarHeight(Context context) {
        int mStatusBarHeight = 0;
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object o = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = (Integer) field.get(o);
            mStatusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mStatusBarHeight;
    }
}
