package com.kallaite.floatwindow.view;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


import com.kallaite.floatwindow.R;
import com.kallaite.floatwindow.utils.Utils;
import com.kallaite.floatwindow.viewadapter.AppViewPagerAdapter;

import java.util.ArrayList;

/**
 * Created on 16-11-21.
 */
public class FloatWindowAppView extends RelativeLayout {

    private String mPos = "";//点击位置，用于判断是否是从收藏应用跳转过来

    private ViewPager mViewPager;
    private AppViewPagerAdapter mPagerAdapter;
    private LinearLayout mDotLayout;
    private int mViewHeight;
    private int mViewWidth;

    public FloatWindowAppView(Context context) {
        super(context);
        initView();
    }

    public FloatWindowAppView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public FloatWindowAppView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView(){
        View view = LayoutInflater.from(this.getContext()).inflate(R.layout.app_layout, this);

        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);

        mPagerAdapter = new AppViewPagerAdapter(this.getContext());
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                Utils.doWithFloatWindow(FloatWindowAppView.this.getContext(),Utils.CMD_ADD_FLOAT_BALL_DELAY);
                for(int j = 0; j < mDotLayout.getChildCount(); j++){
                    ImageView img = (ImageView) mDotLayout.getChildAt(j);
                    img.setImageResource((i == j)?R.drawable.dot_enable:R.drawable.dot_disable);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        mViewWidth = mViewPager.getLayoutParams().width;
        mViewHeight = mViewPager.getLayoutParams().height;
        mDotLayout = (LinearLayout)findViewById(R.id.dot_layout);

    }

    public int getViewHeight(){
        return mViewHeight;
    }

    public int getViewWidth(){
        return mViewWidth;
    }

    public void setAppInfos( ArrayList<PackageInfo> infos){
        mPagerAdapter.setPos(mPos);

        mPagerAdapter.setPackageInfos(infos);
        mDotLayout.removeAllViews();
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(5, 0, 5, 5);
        //Log.v("FloatWindowAppView","createInstalledAppWindow++ setAppInfos add dot");
        for (int i = 0; i < mPagerAdapter.getCount(); i++) {
            ImageView dotsDisable = new ImageView(this.getContext());
            dotsDisable.setImageResource((i == 0)?R.drawable.dot_enable:R.drawable.dot_disable);
            mDotLayout.addView(dotsDisable, i, layoutParams);
        }
        //Log.v("FloatWindowAppView","createInstalledAppWindow++ setAppInfos end");
    }

    public void setPos(String pos){
        mPos = pos;
    }
}
