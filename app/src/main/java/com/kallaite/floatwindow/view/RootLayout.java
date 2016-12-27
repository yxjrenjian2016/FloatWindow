package com.kallaite.floatwindow.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.kallaite.floatwindow.utils.Utils;

/**
 * Created on 16-12-20.
 */
public class RootLayout extends RelativeLayout implements View.OnClickListener{
    public RootLayout(Context context) {
        super(context);
        init();
    }

    public RootLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RootLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(){
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        this.setLayoutParams(params);
        this.setOnClickListener(this);
    }



    @Override
    public void onClick(View view) {
        Log.v("RootLayout","onClick++"+ Utils.CMD_ADD_FLOAT_BALL);
        Utils.doWithFloatWindow(this.getContext(),Utils.CMD_ADD_FLOAT_BALL);
    }
}
