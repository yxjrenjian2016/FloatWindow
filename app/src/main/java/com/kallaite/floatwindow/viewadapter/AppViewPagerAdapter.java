package com.kallaite.floatwindow.viewadapter;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import com.kallaite.floatwindow.R;

import java.util.ArrayList;

/**
 * Created on 16-11-21.
 */
public class AppViewPagerAdapter extends PagerAdapter {

    private static final String TAG = "AppViewPagerAdapter";
    public static int APP_PER_PAGE = 8;

    private ArrayList<PackageInfo> mPackageInfo;
    private Context mContext;

    private String mPos = "";//点击位置，用于判断是否是从收藏应用跳转过来

    public AppViewPagerAdapter( Context context){
        mContext = context;

    }

    public void setPackageInfos(ArrayList<PackageInfo> packageInfos){
        mPackageInfo = packageInfos;
        this.notifyDataSetChanged();
    }

    public void setPos(String pos){
        mPos = pos;
    }

    @Override
    public int getCount() {
        if( mPackageInfo == null){
            return 0;
        }
        return (int) Math.ceil(1f*mPackageInfo.size() / APP_PER_PAGE);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        GridView gridView = (GridView) LayoutInflater.from(mContext).inflate(R.layout.app_grid,null);
        ArrayList<PackageInfo> mAppsPerPageList = new ArrayList<PackageInfo>(APP_PER_PAGE);

        int start = position * APP_PER_PAGE;
        int end = start + APP_PER_PAGE;
        int appCount = mPackageInfo.size() < end ? mPackageInfo.size():end;
        for( int i = start; i < appCount; i++){
            mAppsPerPageList.add(mPackageInfo.get(i));
        }
        AppGridAdapter adapter = new AppGridAdapter(mAppsPerPageList,mContext);
        adapter.setPos(mPos);
        gridView.setAdapter(adapter);
        container.addView(gridView);
        return gridView;
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return (view == o);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }

}
