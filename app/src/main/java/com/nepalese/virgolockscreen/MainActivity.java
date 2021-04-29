package com.nepalese.virgolockscreen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.nepalese.virgolockscreen.data.ShareDao;
import com.nepalese.virgolockscreen.data.clockBean;
import com.nepalese.virgolockscreen.view.VirgoTextClockView;
import com.nepalese.virgosdk.Util.SystemUtil;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private static final int ACTION_REQUEST_PERMISSIONS = 0x001;
    private final String[] NEEDED_PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.FOREGROUND_SERVICE
    };

    private Context context;
    private clockBean bean;
    private SwitchCompat switchAuto, switchLock;

    private VirgoTextClockView clockView;
    private TextView tvColorSelect, tvColorDefault;
    private TextView tvSizeCenter, tvSizeClock, tvOffset, tvRH, tvRM, tvRS;
    private SeekBar sbSizeCenter, sbSizeClock, sbOffset, sbRH, sbRM, sbRS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!SystemUtil.checkPermission(this, NEEDED_PERMISSIONS)){
            ActivityCompat.requestPermissions(this, NEEDED_PERMISSIONS, ACTION_REQUEST_PERMISSIONS);
            return;
        }

        init();
    }

    private void init() {
        initUi();
        setData();
        setListener();
    }

    private void initUi() {
        context = getApplicationContext();
        switchAuto = findViewById(R.id.switchAutoMain);
        switchLock = findViewById(R.id.switchLockMain);

        clockView = findViewById(R.id.clockMain);
        tvColorSelect = findViewById(R.id.tvColorSelect);
        tvColorDefault = findViewById(R.id.tvColorDefault);
        tvSizeCenter = findViewById(R.id.tvSizeCenter);
        tvSizeClock = findViewById(R.id.tvSizeClock);

        tvOffset = findViewById(R.id.tvOffset);
        tvRH = findViewById(R.id.tvRadiusHour);
        tvRM = findViewById(R.id.tvRadiusMinute);
        tvRS = findViewById(R.id.tvRadiusSecond);

        sbSizeCenter = findViewById(R.id.sbSizeCenter);
        sbSizeClock = findViewById(R.id.sbSizeClock);

        sbOffset = findViewById(R.id.sbOffset);
        sbRH = findViewById(R.id.sbRadiusHour);
        sbRM = findViewById(R.id.sbRadiusMinute);
        sbRS = findViewById(R.id.sbRadiusSecond);
    }

    private void setData() {
        switchAuto.setChecked(ShareDao.getSelfAuto(context));
        switchLock.setChecked(ShareDao.getSelfLock(context));

        bean = ShareDao.getClockConfig(context);
        if(bean==null){
            Log.i(TAG, "setData: 首次进入：");
            bean = new clockBean();

            bean.setColorSelect(tvColorSelect.getText().toString());
            bean.setColorDefault(tvColorDefault.getText().toString());
            bean.setSizeCenter(sbSizeCenter.getProgress());
            bean.setSizeClock(sbSizeClock.getProgress());
            bean.setOffset(sbOffset.getProgress());
            bean.setrHour(sbRH.getProgress());
            bean.setrMinute(sbRM.getProgress());
            bean.setrSecond(sbRS.getProgress());
            ShareDao.setClockConfig(context, bean);

        }else{
            //加载本地存储
            tvColorSelect.setText(bean.getColorSelect());
            tvColorDefault.setText(bean.getColorDefault());

            sbSizeCenter.setProgress(bean.getSizeCenter());
            sbSizeClock.setProgress(bean.getSizeClock());
            sbOffset.setProgress(bean.getOffset());
            sbRH.setProgress(bean.getrHour());
            sbRM.setProgress(bean.getrMinute());
            sbRS.setProgress(bean.getrSecond());
        }

        tvSizeCenter.setText(String.format(getString(R.string.main_size_center), String.valueOf(bean.getSizeCenter())));
        tvSizeClock.setText(String.format(getString(R.string.main_size_clock), String.valueOf(bean.getSizeClock())));

        tvOffset.setText(String.format(getString(R.string.main_clock_offset), String.valueOf(bean.getOffset())));
        tvRH.setText(String.format(getString(R.string.main_radius_hour), String.valueOf(bean.getrHour())));
        tvRM.setText(String.format(getString(R.string.main_radius_minute), String.valueOf(bean.getrMinute())));
        tvRS.setText(String.format(getString(R.string.main_radius_second), String.valueOf(bean.getrSecond())));

        clockView.setConfig(bean);

//        clockView.setmColorMain(Color.parseColor(tvColorSelect.getText().toString()));
//        clockView.setmColorSecond(Color.parseColor(tvColorDefault.getText().toString()));
//
//        clockView.setmTextSizeMain(sbSizeClock.getProgress());
//        clockView.setmTextSizeClock(sbSizeCenter.getProgress());
//        clockView.setmOffset(sbOffset.getProgress());
//        clockView.setmRadiusH(sbRH.getProgress());
//        clockView.setmRadiusM(sbRM.getProgress());
//        clockView.setmRadiusS(sbRS.getProgress());
    }

    private void setListener() {
        // TODO: 2021/4/29 颜色选择器

        switchAuto.setOnCheckedChangeListener((buttonView, isChecked) -> ShareDao.setSelfAuto(context, isChecked));

        switchLock.setOnCheckedChangeListener((buttonView, isChecked) -> ShareDao.setSelfLock(context, isChecked));

        sbSizeCenter.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvSizeCenter.setText(String.format(getString(R.string.main_size_center), String.valueOf(progress)));
                clockView.setmTextSizeClock(progress);
                bean.setSizeCenter(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sbSizeClock.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvSizeClock.setText(String.format(getString(R.string.main_size_clock), String.valueOf(progress)));
                clockView.setmTextSizeMain(progress);
                bean.setSizeClock(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sbOffset.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvOffset.setText(String.format(getString(R.string.main_clock_offset), String.valueOf(progress)));
                clockView.setmOffset(progress);
                bean.setOffset(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sbRH.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvRH.setText(String.format(getString(R.string.main_radius_hour), String.valueOf(progress)));
                clockView.setmRadiusH(progress);
                bean.setrHour(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sbRM.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvRM.setText(String.format(getString(R.string.main_radius_minute), String.valueOf(progress)));
                clockView.setmRadiusM(progress);
                bean.setrMinute(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sbRS.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvRS.setText(String.format(getString(R.string.main_radius_second), String.valueOf(progress)));
                clockView.setmRadiusS(progress);
                bean.setrSecond(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void onSave(View view){
        try{
            ShareDao.setClockConfig(context, bean);
            SystemUtil.showToast(context, "保存成功！");
        }catch (Throwable throwable){
            throwable.printStackTrace();
            SystemUtil.showToast(context, "保存失败！" );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean isAllGranted = true;
        for (int grantResult : grantResults) {
            isAllGranted &= (grantResult == PackageManager.PERMISSION_GRANTED);
        }

        if (requestCode == ACTION_REQUEST_PERMISSIONS) {
            if (isAllGranted) {
                init();
            } else {
                Log.i(TAG, "onRequestPermissionsResult: ");
                finish();
            }
        }
    }
}