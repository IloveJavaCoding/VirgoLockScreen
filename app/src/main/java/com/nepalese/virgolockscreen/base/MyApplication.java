package com.nepalese.virgolockscreen.base;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.nepalese.virgolockscreen.service.CoreService;

/**
 * @author nepalese on 2021/4/27 09:53
 * @usage
 */
public class MyApplication extends Application {
    private static MyApplication application;

    public MyApplication(){
        application = this;
    }

    public static MyApplication getApplication(){
        if(application==null){
            synchronized (MyApplication.class){
                if(application==null){
                    application = new MyApplication();
                    application.onCreate();
                }
            }
        }
        return application;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //启动服务

        startService(this);
    }

    private void startService(Context context) {
        Intent intent = new Intent(context, CoreService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }
}
