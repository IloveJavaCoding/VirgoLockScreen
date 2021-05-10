package com.nepalese.virgolockscreen.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.nepalese.virgolockscreen.data.Constants;
import com.nepalese.virgolockscreen.data.ShareDao;
import com.nepalese.virgolockscreen.ui.VirgoLockActivity;

/**
 * @author nepalese on 2021/4/27 09:57
 * @usage
 */
public class CoreService extends Service {
    private static final String TAG = "CoreService";

    private static final int MSG_SCREEN_ON = 1;
    private static final int MSG_SCREEN_OFF = 2;
    private static final int MSG_UN_LOCKED = 3;

    private boolean isRegister = false;//屏幕变化监听

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        buildNoti("com.nepalese.virgolockscreen");
    }

    private void buildNoti(String channelId){
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= 26) {
            String channelName = "Virgo";
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }
        Notification notification = (new NotificationCompat.Builder(this, channelId)).build();
        startForeground(1, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        registerReceiver();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        unRegistertReceiver();
        selfCall();
        super.onDestroy();
    }

    ////////////////////////////////////屏幕开关监听///////////////////////////////
    private void registerReceiver(){
        if(!isRegister){
            Log.i(TAG, "registerReceiver: ");
            isRegister = true;
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            filter.addAction(Intent.ACTION_USER_PRESENT);
            registerReceiver(receiver, filter);
        }
    }

    private void unRegistertReceiver(){
        Log.i(TAG, "unRegistertReceiver: ");
        if(isRegister){
            isRegister = false;
            unregisterReceiver(receiver);
        }
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(TextUtils.isEmpty(action)) return;

            Log.i(TAG, "onReceive: " + action);
            if (Intent.ACTION_SCREEN_ON.equals(action)) {
                handler.sendEmptyMessage(MSG_SCREEN_ON);
            } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                handler.sendEmptyMessage(MSG_SCREEN_OFF);
            }else if(Intent.ACTION_USER_PRESENT.equals(action)){
                handler.sendEmptyMessage(MSG_UN_LOCKED);
            }
        }
    };

    private final Handler handler = new Handler(Looper.myLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_SCREEN_ON:
                    closeLock();
                    break;
                case MSG_SCREEN_OFF:
                    showLock();
                    break;
                case MSG_UN_LOCKED:
                    Log.i(TAG, "解锁: ");
                    break;
            }
        }
    };

    private void closeLock() {

    }

    private void showLock() {
        if(!ShareDao.getSelfLock(getApplicationContext())) return;
        Log.i(TAG, "showLock: ");
        Intent intent = new Intent(this, VirgoLockActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void selfCall(){
        Log.i(TAG, "selfCall: ");
        Intent intent = new Intent(Constants.ACTION_SELE_CALL);
        intent.setPackage(this.getPackageName());
        sendBroadcast(intent);
    }
}
