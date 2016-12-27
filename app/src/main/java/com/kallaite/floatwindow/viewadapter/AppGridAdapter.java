package com.kallaite.floatwindow.viewadapter;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kallaite.floatwindow.R;
import com.kallaite.floatwindow.utils.Utils;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created on 16-11-21.
 */
public class AppGridAdapter extends BaseAdapter {

    private static final String TAG = "AppGridAdapter";
    private ArrayList<PackageInfo> mPackageInfoList;

    private Context mContext;
    private String mPos;
    private PackageManager mPm;

    public AppGridAdapter(ArrayList<PackageInfo> packageInfos, Context context){

        mPackageInfoList = packageInfos;
        PackageInfo info = new PackageInfo();
        mPackageInfoList.add(0,info);
        mContext = context;
        mPm = mContext.getPackageManager();
    }

    public void setPos(String pos){
        mPos = pos;
    }

    @Override
    public int getCount() {
        if( mPackageInfoList == null){
            return 0;
        }
        return mPackageInfoList.size();
    }

    @Override
    public Object getItem(int i) {
        return mPackageInfoList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.app_grid_item, null);
            viewHolder.mIconImg = (ImageView) convertView.findViewById(R.id.app_icon);
            viewHolder.mNameTx = (TextView) convertView.findViewById(R.id.app_name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if( i == 0){
            viewHolder.mIconImg.setImageResource(R.drawable.fw_back);
            viewHolder.mNameTx.setVisibility(View.INVISIBLE);
        }else {

            viewHolder.mIconImg.setImageDrawable(mPackageInfoList.get(i).applicationInfo.loadIcon(mContext.getPackageManager()));
            viewHolder.mNameTx.setVisibility(View.VISIBLE);
            viewHolder.mNameTx.setText(mPm.getApplicationLabel(mPackageInfoList.get(i).applicationInfo));
        }
        viewHolder.mIconImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( i == 0){
                    Utils.doWithFloatWindow(mContext,Utils.CMD_ADD_HOME_WINDOW);
                }else {
                    PackageInfo info = mPackageInfoList.get(i);
                    if( !TextUtils.isEmpty(mPos)){

                        Utils.writeString(mContext,mPos,info.packageName);
                        Utils.doWithFloatWindow(mContext,Utils.CMD_ADD_COLLECT_APP_WINDOW);

                    }else {
                        String name = info.packageName;
                        Utils.startApp(mContext,name);
                    }
                }
            }
        });
        return convertView;
    }

    private class ViewHolder {
        private ImageView mIconImg;
        private TextView mNameTx;
    }
}
