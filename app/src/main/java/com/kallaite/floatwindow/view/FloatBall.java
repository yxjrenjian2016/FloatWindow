package com.kallaite.floatwindow.view;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;


import com.kallaite.floatwindow.R;
import com.kallaite.floatwindow.utils.Utils;

import java.lang.reflect.Field;

public class FloatBall extends LinearLayout {

	private static final String TAG = "FloatBall";
	/**
	 * 记录小悬浮窗的宽度
	 */
	public  int mViewWidth;

	/**
	 * 记录小悬浮窗的高度
	 */
	public  int mViewHeight;

	/**
	 * 记录系统状态栏的高度
	 */
	 private int mStatusBarHeight;

	/**
	 * 用于更新小悬浮窗的位置
	 */
	private WindowManager mWindowManager;

	/**
	 * 小悬浮窗的参数
	 */
	private WindowManager.LayoutParams mParams;

	/**
	 * 记录当前手指位置在屏幕上的横坐标值
	 */
	private float xInScreen;

	/**
	 * 记录当前手指位置在屏幕上的纵坐标值
	 */
	private float yInScreen;

	/**
	 * 记录手指按下时在屏幕上的横坐标的值
	 */
	private float xDownInScreen;

	/**
	 * 记录手指按下时在屏幕上的纵坐标的值
	 */
	private float yDownInScreen;

	/**
	 * 记录手指按下时在小悬浮窗的View上的横坐标的值
	 */
	private float xInView;

	/**
	 * 记录手指按下时在小悬浮窗的View上的纵坐标的值
	 */
	private float yInView;

	private ImageView mBall;

	public FloatBall(Context context) {
		super(context);
		mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		LayoutInflater.from(context).inflate(R.layout.float_ball, this);
		mBall = (ImageView) findViewById(R.id.ball);
		mStatusBarHeight = Utils.getStatusBarHeight(context);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// 手指按下时记录必要数据,纵坐标的值都需要减去状态栏高度
			xInView = event.getX();
			yInView = event.getY();
			xDownInScreen = event.getRawX();
			yDownInScreen = event.getRawY() - mStatusBarHeight;
			xInScreen = event.getRawX();
			yInScreen = event.getRawY() - mStatusBarHeight;
			//mBall.setImageResource(R.drawable.fw_ball_press);
			break;
		case MotionEvent.ACTION_MOVE:
			xInScreen = event.getRawX();
			yInScreen = event.getRawY() - mStatusBarHeight;
			// 手指移动的时候更新小悬浮窗的位置
			updateViewPosition();
			break;
		case MotionEvent.ACTION_UP:
			// 如果手指离开屏幕时，xDownInScreen和xInScreen相等，且yDownInScreen和yInScreen相等，则视为触发了单击事件。
			if (xDownInScreen == xInScreen && yDownInScreen == yInScreen) {
				Utils.doWithFloatWindow(this.getContext(),Utils.CMD_ADD_HOME_WINDOW_WITH_ANIMATION);
			}
			if( mParams.x >= mWindowManager.getDefaultDisplay().getWidth()/2){
				while (mParams.x++ < (mWindowManager.getDefaultDisplay().getWidth() - mViewWidth)){
					mWindowManager.updateViewLayout(this, mParams);
				}
			}else {
				while (mParams.x-- > mViewWidth){
					mWindowManager.updateViewLayout(this, mParams);
				}
			}
			//mBall.setImageResource(R.drawable.fw_ball_normal);
			break;
		default:
			break;
		}
		return true;
	}

	/**
	 * 将小悬浮窗的参数传入，用于更新小悬浮窗的位置。
	 * 
	 * @param params
	 *            小悬浮窗的参数
	 */
	public void setParams(WindowManager.LayoutParams params) {
		mParams = params;
	}

	/**
	 * 设置悬浮球大小
	 * @param size
	 */
	public void setSize(int size){
        LayoutParams params = (LayoutParams) mBall.getLayoutParams();
		params.width = size;
		params.height = size;
		mBall.setLayoutParams(params);
	}

	/**
	 * 更新小悬浮窗在屏幕中的位置。
	 */
	private void updateViewPosition() {
		mParams.x = (int) (xInScreen - xInView);
		mParams.y = (int) (yInScreen - yInView);
		mWindowManager.updateViewLayout(this, mParams);
	}

}
