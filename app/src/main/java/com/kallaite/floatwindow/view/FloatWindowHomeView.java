package com.kallaite.floatwindow.view;

import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.kallaite.floatwindow.R;
import com.kallaite.floatwindow.utils.Utils;


public class FloatWindowHomeView extends LinearLayout implements View.OnClickListener{


	public FloatWindowHomeView(final Context context) {
		super(context);
		LayoutInflater.from(context).inflate(R.layout.float_home_window, this);
		View view = findViewById(R.id.big_window_layout);

		findViewById(R.id.up).setOnClickListener(this);
		findViewById(R.id.down).setOnClickListener(this);
		findViewById(R.id.left).setOnClickListener(this);
		findViewById(R.id.right).setOnClickListener(this);
		findViewById(R.id.center).setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {

		switch (view.getId()){
			case R.id.up:
				Utils.exec("input keyevent " + KeyEvent.KEYCODE_HOME + "\n");
				break;
			case R.id.down:
				Utils.doWithFloatWindow(getContext(), Utils.CMD_ADD_ALL_APP_WINDOW);
				break;
			case R.id.left:

				break;
			case R.id.right:
				Utils.doWithFloatWindow(getContext(),Utils.CMD_ADD_COLLECT_APP_WINDOW);
				break;
			case R.id.center:
				Utils.exec("input keyevent " + KeyEvent.KEYCODE_BACK + "\n");
				break;
		}
	}


}
