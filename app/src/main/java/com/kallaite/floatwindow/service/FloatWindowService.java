package com.kallaite.floatwindow.service;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;


import com.kallaite.floatwindow.R;
import com.kallaite.floatwindow.callback.IServiceViewCallback;
import com.kallaite.floatwindow.receiver.LocalReceiver;
import com.kallaite.floatwindow.receiver.PackageReceiver;
import com.kallaite.floatwindow.receiver.RemoteReceiver;
import com.kallaite.floatwindow.utils.Utils;
import com.kallaite.floatwindow.view.CollectView;
import com.kallaite.floatwindow.view.FloatBall;
import com.kallaite.floatwindow.view.FloatWindowAppView;
import com.kallaite.floatwindow.view.FloatWindowHomeView;
import com.kallaite.floatwindow.view.RootLayout;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;

public class FloatWindowService extends Service implements IServiceViewCallback{

	private static final String TAG = "FloatWindowService";

	public static final int TIME_DELAY = 8000;//延时8s自动消失

	private static final int TIME_ANIMATION = 300;//动画时间
	/**
	 * 悬浮球
	 */
	private FloatBall mFloatBall;

	private WindowManager.LayoutParams mFloatBallParams;

	private int mFloatBallSize;

	/**
	 * 悬浮窗
	 */
	private FloatWindowHomeView mHomeWindow;

	/**
	 * 已安装应用窗口
	 */
	private FloatWindowAppView mAppWindow;

	private ArrayList<PackageInfo> mLaunchApp;

	/**
	 * 收藏应用窗口
	 */
	private CollectView mCollectView;

	/**
	 * 用于控制在屏幕上添加或移除窗口
	 */
	private WindowManager mWindowManager;

	private int mSreenWidth;
	private int mSreenHeight;

	private LocalBroadcastManager mLocalBroadcastManager;

	private BroadcastReceiver mLocalReceiver;

	private BroadcastReceiver mPackageReceiver;

	private BroadcastReceiver mReceiver;

	private RootLayout mRootLayout;

	private boolean mIsRootAdd;

	private ServiceHandler mHandler;

	private static class ServiceHandler extends Handler{

		private WeakReference<FloatWindowService> mWeakRef;

		public ServiceHandler(FloatWindowService  service){
			mWeakRef = new WeakReference<FloatWindowService>(service);
		}

		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			FloatWindowService ref = mWeakRef.get();
			if( ref == null){
				return;
			}
			switch (msg.what){
				case Utils.CMD_ADD_FLOAT_BALL:
					ref.creatFloatBallWithAnimation();
					break;
			}
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		if( mWindowManager == null){
			mWindowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
		}
		DisplayMetrics metrics = new DisplayMetrics();
		mWindowManager.getDefaultDisplay().getMetrics(metrics);
		mSreenWidth = metrics.widthPixels;
		mSreenHeight = metrics.heightPixels;

		mLaunchApp = Utils.getLaunchApp(this);
		mHandler = new ServiceHandler(this);

		mFloatBallSize = Utils.readInt(this,Utils.FLOAT_BALL_SIZE,this.getResources().getDimensionPixelSize(R.dimen.fw_80dp));

		registerReceiver();

		int show = Utils.readInt(this,Utils.SHOW_FLOAT_BALL,-1);
		if( show < 0){
			//第一次开机时默认显示
			createFloatBall();
			Utils.writeInt(this,Utils.SHOW_FLOAT_BALL,Utils.DISPLAY_FLOAT_BALL);
		}else if( show == Utils.DISPLAY_FLOAT_BALL){
			createFloatBall();
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v(TAG,"onStartCommand++"+startId);
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.v(TAG,"onDestroy");
		unRegisterReceiver();
		if( mHandler != null){
			mHandler.removeCallbacksAndMessages(null);
		}
	}

	/**
	 * 注册广播接收器
	 */
	private void registerReceiver(){
		if( mLocalReceiver == null ){
			mLocalReceiver = new LocalReceiver(this);
			IntentFilter filter = new IntentFilter();
			filter.addAction(Utils.ACTION_FLOAT_WINDOW);
			filter.addAction(Intent.ACTION_PACKAGE_ADDED);
			filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
			filter.addAction(Intent.ACTION_SCREEN_OFF);
			filter.addAction(Intent.ACTION_SCREEN_ON);
			mLocalBroadcastManager =  LocalBroadcastManager.getInstance(this);
			mLocalBroadcastManager.registerReceiver(mLocalReceiver,filter);
		}
		if (mPackageReceiver == null) {
			mPackageReceiver = new PackageReceiver();
			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
			intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
			intentFilter.addDataScheme("package");
			this.registerReceiver(mPackageReceiver, intentFilter);
		}

		if( mReceiver == null){
			mReceiver = new RemoteReceiver();
			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
			intentFilter.addAction(Intent.ACTION_SCREEN_ON);
			this.registerReceiver(mReceiver, intentFilter);
		}
	}

	private void unRegisterReceiver(){
		if( mLocalReceiver != null){
			mLocalBroadcastManager.unregisterReceiver(mLocalReceiver);
		}

		if( mPackageReceiver != null){
			this.unregisterReceiver(mPackageReceiver);
		}

		if( mReceiver != null){
			this.unregisterReceiver(mReceiver);
		}
	}
	@Override
	public void createFloatBall() {

		removeRootView();
		if (mFloatBall == null) {
			mFloatBall = new FloatBall(this);

			if( mFloatBallParams == null){
				mFloatBallParams = getParams();
				mFloatBallParams.width = mFloatBallSize;
				mFloatBallParams.height = mFloatBallSize;
				mFloatBallParams.x = mSreenWidth ;
				mFloatBallParams.y = mSreenHeight / 2;
			}

			mFloatBall.setSize(mFloatBallSize);
			mFloatBall.setParams(mFloatBallParams);
			mWindowManager.addView(mFloatBall, mFloatBallParams);
		}else {
			mFloatBallParams.width = mFloatBallSize;
			mFloatBallParams.height = mFloatBallSize;
			mFloatBall.setSize(mFloatBallSize);
			mWindowManager.updateViewLayout(mFloatBall,mFloatBallParams);
		}
		Log.v(TAG,"createFloatBall++"+mFloatBallParams.x +","+mFloatBallParams.y +","+mFloatBallSize);
	}


	@Override
	public void creatFloatBallWithAnimation() {
		if (mHomeWindow != null) {
			AnimatorSet animatorSet = getAnimatorSet(mHomeWindow);
			animatorSet.start();
		}else if( mCollectView != null){
			AnimatorSet animatorSet = getAnimatorSet(mCollectView);
			animatorSet.start();
		}else {
			createFloatBall();
		}
	}

	@Override
	public void creatFloatBallWithAnimationDelay() {
		mHandler.removeCallbacksAndMessages(null);
		mHandler.sendEmptyMessageDelayed(Utils.CMD_ADD_FLOAT_BALL, TIME_DELAY);
	}

	@Override
	public void removeFloatBall() {
		if (mFloatBall != null) {
			mWindowManager.removeView(mFloatBall);
			mFloatBall = null;
		}
	}

	@Override
	public void createHomeWindow() {

		/*mHomeWindow = new FloatWindowHomeView(context);
		WindowManager.LayoutParams params = getParams();
		params.x = mSreenWidth / 2 - mHomeWindow.getViewWidth() / 2;
		params.y = mSreenHeight / 2 - mHomeWindow.getViewHeight() / 2;
		params.width = mHomeWindow.getViewWidth();
		params.height = mHomeWindow.getViewHeight();

		mWindowManager.addView(mHomeWindow, params);*/
		addRootView();
		mHomeWindow = new FloatWindowHomeView(this);
		addViewToRoot(mHomeWindow);
		removeFloatBall();
		removeCollectView();
		removeInstalledAppWindow();
		creatFloatBallWithAnimationDelay();
	}


	@Override
	public void createHomeWindowWithAnimation(){
		removeFloatBall();
		addRootView();
		mHomeWindow = new FloatWindowHomeView(this);
		addViewToRoot(mHomeWindow);

		float homeSize = this.getResources().getDimension(R.dimen.fw_300dp);
		ObjectAnimator translationX = ObjectAnimator.ofFloat(mHomeWindow, "translationX", mFloatBallParams.x-(mSreenWidth/2f-homeSize/2),0f).setDuration(TIME_ANIMATION);

		ObjectAnimator translationY = ObjectAnimator.ofFloat(mHomeWindow, "translationY", mFloatBallParams.y-((mSreenHeight-Utils.getStatusBarHeight(this))/2f-homeSize/2)-mFloatBallSize,0f).setDuration(TIME_ANIMATION);

		DecimalFormat df = new DecimalFormat("#.00");
		float start = Float.valueOf(df.format(mFloatBallSize / homeSize));

		Log.v(TAG,"createHomeWindowWithAnimation+"+df.format(start));
		ObjectAnimator scaleX = ObjectAnimator.ofFloat(mHomeWindow, "scaleX", start,1f).setDuration(TIME_ANIMATION);

		ObjectAnimator scaleY = ObjectAnimator.ofFloat(mHomeWindow, "scaleY",start,1f).setDuration(TIME_ANIMATION);

		AnimatorSet animatorSet = new AnimatorSet();
		animatorSet.setInterpolator(new LinearInterpolator());
		animatorSet.play(translationX).with(translationY).with(scaleX).with(scaleY);
		animatorSet.start();
		creatFloatBallWithAnimationDelay();
	}

	@Override
	public void removeHomeWindow() {
		if (mHomeWindow != null) {
			mRootLayout.removeView(mHomeWindow);
			mHomeWindow = null;
		}
	}

	@Override
	public void createInstalledAppWindow(String pos){
		Log.v(TAG,"createInstalledAppWindow");
		mAppWindow = new FloatWindowAppView(this);
		mAppWindow.setPos(pos);
		Log.v(TAG,"createInstalledAppWindow++"+mLaunchApp.size());
		mAppWindow.setAppInfos(mLaunchApp);
		addViewToRoot(mAppWindow);
		removeHomeWindow();
		removeCollectView();
		removeFloatBall();
		creatFloatBallWithAnimationDelay();
	}


	@Override
	public void removeInstalledAppWindow(){
		if( mAppWindow != null){
			mRootLayout.removeView(mAppWindow);
			mAppWindow = null;
		}
	}


	@Override
	public void createCollectView() {

		mCollectView = new CollectView(this);
		addViewToRoot(mCollectView);
		removeInstalledAppWindow();
		removeHomeWindow();
		removeFloatBall();
		creatFloatBallWithAnimationDelay();
	}


	@Override
	public void removeCollectView(){
		if( mCollectView != null){
			mRootLayout.removeView(mCollectView);
			mCollectView = null;
		}
	}

	private WindowManager.LayoutParams getParams(){
		WindowManager.LayoutParams params = new WindowManager.LayoutParams();
		params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
				| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
		params.format = PixelFormat.RGBA_8888;
		params.gravity = Gravity.LEFT | Gravity.TOP;
		return params;
	}

	@Override
	public void addPackage(String name ){

		for( PackageInfo info:mLaunchApp){
			//列表中已经存在，则不需要添加，避免重复
			if(info.packageName.equals(name)){
				return;
			}
		}
		PackageManager pm = FloatWindowService.this.getPackageManager();
		try {
			PackageInfo info = pm.getPackageInfo(name,0);
			mLaunchApp.add(info);

		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void removePackage(String name){

		Iterator<PackageInfo>  iterator= mLaunchApp.iterator();
		while (iterator.hasNext()){
			if(name.equals(iterator.next().packageName)){
				iterator.remove();
				break;
			}
		}
	}

	@Override
	public void setFloatBallSize(int size) {
		mFloatBallSize = size;
	}

	@Override
	public int getFloatBallSize() {
		return mFloatBallSize;
	}

	@Override
	public void removeAllWindow(boolean resetFloatBall){
		if( resetFloatBall){
			mFloatBallParams = null;
		}
		removeFloatBall();
		removeCollectView();
		removeHomeWindow();
		removeInstalledAppWindow();
	}

	private void addRootView(){
		if( !mIsRootAdd ){
			mRootLayout = new RootLayout(this);
			WindowManager.LayoutParams param = getParams();
			param.width = WindowManager.LayoutParams.MATCH_PARENT;
			param.height = WindowManager.LayoutParams.MATCH_PARENT;
			mWindowManager.addView(mRootLayout,param);
			mIsRootAdd = true;
		}
	}
	private void removeRootView(){
		if( mIsRootAdd ){
			mWindowManager.removeView(mRootLayout);
			mIsRootAdd = false;
		}
	}

	private void addViewToRoot(View v){
		RelativeLayout.LayoutParams rlp=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		rlp.addRule(RelativeLayout.CENTER_IN_PARENT);
		mRootLayout.addView(v,rlp);
	}

	private AnimatorSet getAnimatorSet(final View view){
		float homeSize = this.getResources().getDimension(R.dimen.fw_300dp);
		ObjectAnimator translationX = ObjectAnimator.ofFloat(view, "translationX", 0f,mFloatBallParams.x-(mSreenWidth/2f-homeSize/2)).setDuration(TIME_ANIMATION);

		ObjectAnimator translationY = ObjectAnimator.ofFloat(view, "translationY", 0f,mFloatBallParams.y-((mSreenHeight-Utils.getStatusBarHeight(this))/2f-homeSize/2)-mFloatBallSize).setDuration(TIME_ANIMATION);

		DecimalFormat df = new DecimalFormat("#.00");
		float end = Float.valueOf(df.format(mFloatBallSize / homeSize));

		ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX",  1f,end).setDuration(TIME_ANIMATION);

		ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, end).setDuration(TIME_ANIMATION);

		AnimatorSet animatorSet = new AnimatorSet();
		animatorSet.setInterpolator(new LinearInterpolator());
		animatorSet.play(translationX).with(translationY).with(scaleX).with(scaleY);
		animatorSet.addListener(new Animator.AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animation) {

			}

			@Override
			public void onAnimationEnd(Animator animation) {
				createFloatBall();
				removeHomeWindow();
				removeCollectView();
				removeInstalledAppWindow();
			}

			@Override
			public void onAnimationCancel(Animator animation) {

			}

			@Override
			public void onAnimationRepeat(Animator animation) {

			}
		});
		return animatorSet;
	}
}
