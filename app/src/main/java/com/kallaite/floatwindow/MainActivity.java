package com.kallaite.floatwindow;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.kallaite.floatwindow.service.FloatWindowService;
import com.kallaite.floatwindow.utils.Utils;


public class MainActivity extends Activity implements View.OnClickListener{

    private static final String TAG = "floatmain";

    private RelativeLayout mScaleLayout;

    private AlertDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CheckBox checkBox = (CheckBox) findViewById(R.id.start_float_window);
        int show = Utils.readInt(this,Utils.SHOW_FLOAT_BALL,Utils.DISPLAY_FLOAT_BALL);
        checkBox.setChecked(show == Utils.DISPLAY_FLOAT_BALL);

        if( show == Utils.DISPLAY_FLOAT_BALL ){
            Intent intent = new Intent(MainActivity.this, FloatWindowService.class);
            startService(intent);
        }
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {

                Utils.writeInt(MainActivity.this,Utils.SHOW_FLOAT_BALL,checked?Utils.DISPLAY_FLOAT_BALL:Utils.HIDE_FLOAT_BALL);
                //Utils.doWithFloatWindow(MainActivity.this,checked?Utils.CMD_ADD_FLOAT_BALL :Utils.CMD_REMOVE_FLAOT_BALL);
                mScaleLayout.setEnabled(checked);
                Intent intent = new Intent(MainActivity.this, FloatWindowService.class);
                if( checked){
                    startService(intent);
                }else {
                    MainActivity.this.stopService(intent);
                }
            }
        });

        mScaleLayout = (RelativeLayout) findViewById(R.id.scale_layout);
        mScaleLayout.setOnClickListener(this);
        mScaleLayout.setEnabled(checkBox.isChecked());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG,"onDestroy");
        if(mDialog != null){
            mDialog.dismiss();
            mDialog = null;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.scale_layout:
                createScaleDialog();
                break;
        }
    }

    /**
     * 调整悬浮球大小的对话框
     */
    private void createScaleDialog(){

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View v = LayoutInflater.from(this).inflate(R.layout.seekbar_layout,null);
        SeekBar seekBar = (SeekBar)v.findViewById(R.id.scale_seekbar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int size = Utils.progressToSize(getApplication(),i);
                Bundle bundle = new Bundle();
                bundle.putInt(Utils.FLOAT_BALL_SIZE,size);
                Utils.doWithFloatWindowWithExtra(MainActivity.this,Utils.CMD_FLOAT_BALL_SIZE,bundle);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Utils.writeInt(MainActivity.this,Utils.FLOAT_BALL_SIZE,Utils.progressToSize(getApplication(),seekBar.getProgress()));
            }
        });

        int ballSize = Utils.readInt(MainActivity.this,Utils.FLOAT_BALL_SIZE,this.getResources().getDimensionPixelSize(R.dimen.fw_50dp));
        seekBar.setProgress(Utils.sizeToProgress(getApplication(),ballSize));
        builder.setTitle(R.string.scale);
        builder.setView(v);
        builder.setCancelable(true);

        mDialog = builder.create();
        mDialog.show();
    }
}
