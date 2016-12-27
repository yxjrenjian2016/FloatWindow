package com.kallaite.floatwindow.viewadapter;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kallaite.floatwindow.R;
import com.kallaite.floatwindow.utils.Utils;


/**
 * Created  on 16-11-21.
 */
public class CollectGridAdapter extends BaseAdapter {

    private static final int COUNT = 9;
    private static final int CENTER_POSITION = 4;

    private SparseArray<PackageInfo> mDataArray;
    private Context mContext;

    public CollectGridAdapter(SparseArray<PackageInfo> infos, Context context){
        mDataArray = infos;
        mContext = context;
    }
    @Override
    public int getCount() {
        return COUNT;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int pos, View convertView, ViewGroup viewGroup) {
       ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.app_grid_item, null);
            viewHolder.mIconImg = (ImageView) convertView.findViewById(R.id.app_icon);
            viewHolder.mTx = (TextView) convertView.findViewById(R.id.app_name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.mTx.setVisibility(View.GONE);
        if( pos == CENTER_POSITION){
            viewHolder.mIconImg.setImageResource(R.drawable.fw_back);
        }else {
            if( mDataArray == null || (mDataArray.get(pos) == null)){
                viewHolder.mIconImg.setImageResource(R.drawable.fw_collect);
            } else {
                viewHolder.mIconImg.setImageDrawable(mDataArray.get(pos).applicationInfo.loadIcon(mContext.getPackageManager()));
            }
        }
        viewHolder.mIconImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("collect","click pos"+pos);
                if( pos == CENTER_POSITION){
                    Utils.doWithFloatWindow(mContext,Utils.CMD_ADD_HOME_WINDOW);
                }else {
                    if( mDataArray == null || (mDataArray.get(pos) == null) ){
                        Utils.doWithFloatWindowWithExtra(mContext,Utils.CMD_ADD_ALL_APP_WINDOW,Utils.ACTION_POSITION,String.valueOf(pos));

                    }else {
                        PackageInfo info = mDataArray.get(pos);
                        Utils.startApp(mContext,info.packageName);
                    }
                }
            }
        });
        viewHolder.mIconImg.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Log.v("collect","long pos"+pos);
                if( mDataArray != null && mDataArray.get(pos) != null){
                    mDataArray.remove(pos);

                    Utils.removeKey(mContext,String.valueOf(pos));
                    CollectGridAdapter.this.notifyDataSetChanged();
                    Utils.doWithFloatWindow(mContext,Utils.CMD_ADD_FLOAT_BALL_DELAY);
                }
                return true;
            }
        });
        return convertView;
    }

    private class ViewHolder {
        private ImageView mIconImg;
        private TextView mTx;
    }
}
