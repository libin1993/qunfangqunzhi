package io.cordova.lexuncompany.bean.base;

import android.Manifest;
import android.content.Context;

import io.cordova.lexuncompany.application.MyApplication;

/**
 * Created by JasonYao on 2018/3/5.
 */

public class App {


    public static final String[] mPermissionList = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_LOGS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.SET_DEBUG_APP,
            Manifest.permission.SYSTEM_ALERT_WINDOW,
            Manifest.permission.GET_ACCOUNTS,
            Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
            Manifest.permission.WRITE_APN_SETTINGS,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CAMERA
    };

    /**
     * 缓存数据库
     */
    public static class DB {
        public static final int version = 1;  //数据库版本号
        public static final String dbName = "db_data.db";  //数据库名称
    }

    public static class BaseDB {
        public static final int version = 1; //数据库版本号
        public static final String dbName = "db_base.db"; //数据库名称
    }

    public static class LexunCard {
//        public static final String CardUrl = "http://122.114.207.164:18081/"; //正式环境下CardUrl

        public static final String CardUrl = "http://ys-zcsapp.iotone.cn:22800"; //正式环境下CardUrl


        public static final String BUGLY_APPID = "e9950e84c6";  //bugly群防群治
    }
}

