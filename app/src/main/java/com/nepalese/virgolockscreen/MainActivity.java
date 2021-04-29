package com.nepalese.virgolockscreen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;

import com.nepalese.virgolockscreen.data.ShareDao;
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
    private SwitchCompat switchAuto, switchLock;

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
    }

    private void setData() {
        switchAuto.setChecked(ShareDao.getSelfAuto(context));
        switchLock.setChecked(ShareDao.getSelfLock(context));
    }

    private void setListener() {
        switchAuto.setOnCheckedChangeListener((buttonView, isChecked) -> ShareDao.setSelfAuto(context, isChecked));

        switchLock.setOnCheckedChangeListener((buttonView, isChecked) -> ShareDao.setSelfLock(context, isChecked));
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