package com.nepalese.virgolockscreen;

import android.Manifest;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;

import com.nepalese.virgocomponent.view.color.VirgoColorBoard;
import com.nepalese.virgolockscreen.data.ShareDao;
import com.nepalese.virgolockscreen.data.ClockBean;
import com.nepalese.virgolockscreen.view.VirgoTextClockView;
import com.nepalese.virgosdk.Util.ColorUtil;
import com.nepalese.virgosdk.Util.DialogUtil;
import com.nepalese.virgosdk.Util.SystemUtil;

import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity implements VirgoColorBoard.ColorCallback{
    private static final String TAG = "MainActivity";

    private static final int ACTION_REQUEST_PERMISSIONS = 0x001;
    private static final int TAG_COLOR_SELECT = 0;
    private static final int TAG_COLOR_DEFAULT = 1;
    private boolean needCheck = false;

    private final String[] NEEDED_PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.FOREGROUND_SERVICE
    };

    private Context context;
    private ClockBean bean;
    private SwitchCompat switchAuto, switchLock;
    private VirgoColorBoard colorBoard;

    private VirgoTextClockView clockView;
    private TextView tvColorSelect, tvColorDefault;
    private TextView tvSizeCenter, tvSizeClock, tvOffset, tvRH, tvRM, tvRS;
    private SeekBar sbSizeCenter, sbSizeClock, sbOffset, sbRH, sbRM, sbRS;
    private int tag;

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
        colorBoard = new VirgoColorBoard(this);
        colorBoard.setCallback(this);

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
            Log.i(TAG, "setData: ???????????????");
            bean = new ClockBean();

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
            //??????????????????
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
    }

    private void setListener() {
        // TODO: 2021/4/29 ???????????????
        //??????
        tvColorSelect.setOnClickListener(v -> {
            tag = TAG_COLOR_SELECT;
            colorBoard.setmColor(Color.parseColor(tvColorSelect.getText().toString()));
            colorBoard.show();
        });

        tvColorDefault.setOnClickListener(v ->{
            tag = TAG_COLOR_DEFAULT;
            colorBoard.setmColor(Color.parseColor(tvColorDefault.getText().toString()));
            colorBoard.show();
        });

        switchAuto.setOnCheckedChangeListener((buttonView, isChecked) -> ShareDao.setSelfAuto(context, isChecked));

        switchLock.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                needCheck = true;
                checkNeededPermissions(context);
            }else {
                needCheck = false;
            }
        });

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
            SystemUtil.showToast(context, "???????????????");
        }catch (Throwable throwable){
            throwable.printStackTrace();
            SystemUtil.showToast(context, "???????????????" );
        }
    }

    //?????????????????????????????????????????????????????????????????????????????????
    private void checkNeededPermissions(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(!Settings.canDrawOverlays(context) || !canShowLockView(context)){
                DialogUtil.showMsgDialog(this, "????????????", "??????????????????????????????????????????????????????????????????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_NEGATIVE:
                                dialog.dismiss();
                                SystemUtil.showToast(context, "?????????????????????");
                                ShareDao.setSelfLock(context, false);
                                needCheck = false;
                                break;
                            case DialogInterface.BUTTON_POSITIVE:
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" + getPackageName()));
                                startActivity(intent);
                                break;
                        }
                    }
                });
            }else{
                SystemUtil.showToast(context, "?????????????????????");
                ShareDao.setSelfLock(context, true);
            }
        }
    }

    /**
     * ??????????????????????????????
     * @param context
     * @return
     */
    public static boolean canShowLockView(Context context) {
        //>=21
        AppOpsManager ops = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        try {
            int op = 10020; // >= 23
            // ops.checkOpNoThrow(op, uid, packageName)
            Method method = ops.getClass().getMethod("checkOpNoThrow", new Class[]{int.class, int.class, String.class});
            Integer result = (Integer) method.invoke(ops, op,  android.os.Process.myUid(), context.getPackageName());
            Log.i(TAG, "canShowLockView: " + result);
            return result == AppOpsManager.MODE_ALLOWED;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
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

    @Override
    protected void onResume() {
        super.onResume();
        if(needCheck){
            checkNeededPermissions(context);
        }
    }

    /**
     * ???????????????????????????????????????????????????
     */
    private void checkAlertPermission(Context context, int requsetCode) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(context)) {
                    //???????????????????????????
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, requsetCode);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPick(int color) {
        String str = "#" + Integer.toHexString(color);
        if(tag == TAG_COLOR_SELECT){
            clockView.setmColorMain(color);
            tvColorSelect.setText(str);
            bean.setColorSelect(str);
        }else if(tag == TAG_COLOR_DEFAULT){
            clockView.setmColorSecond(color);
            tvColorDefault.setText(str);
            bean.setColorDefault(str);
        }
    }
}