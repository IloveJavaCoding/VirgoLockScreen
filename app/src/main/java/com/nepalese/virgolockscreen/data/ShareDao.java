package com.nepalese.virgolockscreen.data;

import android.content.Context;

import com.nepalese.virgosdk.Util.SPUtil;

/**
 * @author nepalese on 2021/4/27 10:16
 * @usage
 */
public class ShareDao {
    private static final String MAIN_CONFIG = "main_config";
    private static final String KEY_SELF_AUTO = "self_auto";//自启
    private static final String KEY_SELF_LOCK = "self_lock";//锁频
    private static final String KEY_CLOCK_CONFIG = "clock_config";//时钟属性

    public static clockBean getClockConfig(Context context){
        return SPUtil.getObject(context, MAIN_CONFIG, KEY_CLOCK_CONFIG, clockBean.class);
    }

    public static void setClockConfig(Context context, clockBean bean){
        SPUtil.saveObject(context, MAIN_CONFIG, KEY_CLOCK_CONFIG, bean);
    }

    public static boolean getSelfAuto(Context context){
        return SPUtil.getBoolean(context, MAIN_CONFIG, KEY_SELF_AUTO, true);
    }

    public static void setSelfAuto(Context context, boolean isOn){
        SPUtil.setBoolean(context, MAIN_CONFIG, KEY_SELF_AUTO, isOn);
    }

    public static boolean getSelfLock(Context context){
        return SPUtil.getBoolean(context, MAIN_CONFIG, KEY_SELF_LOCK, true);
    }

    public static void setSelfLock(Context context, boolean isOn){
        SPUtil.setBoolean(context, MAIN_CONFIG, KEY_SELF_LOCK, isOn);
    }
}
