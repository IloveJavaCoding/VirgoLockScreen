package com.nepalese.virgolockscreen.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.nepalese.virgolockscreen.data.ShareDao;
import com.nepalese.virgolockscreen.service.CoreService;

/**
 * @author nepalese on 2021/4/27 09:56
 * @usage
 */
public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "BootReceiver";
    private static final int MSG_START_SERVICE = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(TextUtils.isEmpty(action)) return;

        if(!ShareDao.getSelfAuto(context)) return;
        Log.i(TAG, "onReceive: 自启/自醒");
        Message msg = Message.obtain();
        msg.obj = context;
        msg.what = MSG_START_SERVICE;

        handler.sendMessageDelayed(msg, 1000);
    }

    private final Handler handler = new Handler(Looper.myLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_START_SERVICE:
                    startService((Context) msg.obj);
                    break;
            }
        }
    };

    private void startService(Context context) {
        Intent intent = new Intent(context, CoreService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }
}
