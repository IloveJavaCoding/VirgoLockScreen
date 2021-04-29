package com.nepalese.virgolockscreen.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.nepalese.virgolockscreen.R;
import com.nepalese.virgolockscreen.data.ShareDao;
import com.nepalese.virgolockscreen.data.clockBean;
import com.nepalese.virgolockscreen.view.VirgoTextClockView;
import com.nepalese.virgosdk.Util.WinowUtil;

public class VirgoLockActivity extends AppCompatActivity {
    private static final String TAG = "VirgoLockActivity";

    private Context context;

    private VirgoTextClockView clockView;
    private ImageView imgLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showWhenLock();
        setContentView(R.layout.activity_virgo_lock);
        //全屏显示：隐藏状态栏和导航栏
        WinowUtil.setSNHide(this);
//        hideSystemLock();

        init();
    }

    private void init() {
        initUi();
        setData();
        setListener();
    }

    private void initUi() {
        context = getApplicationContext();
        clockView = findViewById(R.id.clockLock);
        imgLock = findViewById(R.id.imgLock);
    }

    private void setData() {
        clockBean bean = ShareDao.getClockConfig(context);
        if(bean !=null){
            clockView.setConfig(bean);
        }else{
            //默认值
        }
    }

    private void setListener() {
        imgLock.setOnClickListener(v -> {
            finish();
        });
    }

    //锁屏可显示
    private void showWhenLock() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
//                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
//                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
    }

    //隐藏系统锁屏：已被弃用
//    private void hideSystemLock(){
//        KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
//        KeyguardManager.KeyguardLock keyguardLock = km.newKeyguardLock("");
//        keyguardLock.disableKeyguard();
//    }
}