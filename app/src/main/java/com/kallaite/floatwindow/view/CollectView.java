package com.kallaite.floatwindow.view;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.kallaite.floatwindow.R;
import com.kallaite.floatwindow.utils.Utils;
import com.kallaite.floatwindow.viewadapter.CollectGridAdapter;


/**
 * Created on 16-11-21.
 */
public class CollectView extends RelativeLayout {

    private CollectGridAdapter mCollectGridAdapter;

    public CollectView(Context context) {
        super(context);
        initView();
    }

    public CollectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public CollectView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView(){

        LayoutInflater.from(this.getContext()).inflate(R.layout.collect_grid, this);
        GridView gridView = (GridView)findViewById(R.id.collect_gridView);


        PackageManager pm = this.getContext().getPackageManager();
        SparseArray<PackageInfo> infoSparseArray = new SparseArray<PackageInfo>();

        for( int i = 0; i < 9; i++){
            String name = Utils.readString(this.getContext(),String.valueOf(i),"");
            Log.v("collect","name "+name);
            if( !TextUtils.isEmpty(name)){
                try {
                        PackageInfo info = pm.getPackageInfo(name,0);
                        infoSparseArray.put(i,info);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        mCollectGridAdapter = new CollectGridAdapter(infoSparseArray,this.getContext());
        gridView.setAdapter(mCollectGridAdapter);
    }

}
