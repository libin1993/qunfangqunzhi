package io.cordova.lexuncompany.units;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import io.cordova.lexuncompany.application.MyApplication;

/**
 * 基础工具类
 * Created by JasonYao on 2018/2/26.
 */

public class BaseUnits {
    public static BaseUnits mInstance = null;
    private TelephonyManager tm = (TelephonyManager) MyApplication.getInstance().getSystemService(Context.TELEPHONY_SERVICE);

    /**
     * 私有化构造方法
     */
    private BaseUnits() {
    }

    /**
     * 设计单例获取方式
     */
    public static BaseUnits getInstance() {
        if (mInstance == null) {
            synchronized (BaseUnits.class) {
                if (mInstance == null) {

                    mInstance = new BaseUnits();
                }
            }
        }

        return mInstance;
    }

    /**
     * 获取手机IMEI号（15位）
     *
     * @return
     */
    public String getPhoneKey() {
        String szImei = null;
        if (!isEmpty(ConfigUnits.getInstance().getPhoneAnalogIMEI())) {  //如果已经缓存手机唯一标识码，优先返回
            return ConfigUnits.getInstance().getPhoneAnalogIMEI();
        }

        szImei = getSerilNumByLength(20);
        ConfigUnits.getInstance().setPhoneAnalogIMEI(szImei);
        return szImei;
    }

    /**
     * 获取指定位数的唯一序列号
     *
     * @param length
     * @return
     */
    public String getSerilNumByLength(int length) {
        if (length < 20) {
            length = 20;
        }
        String serlNum = "";
        int random = (int) (Math.random() * 9) + 10;
        serlNum = String.valueOf(Math.abs(UUID.randomUUID().toString()
                .hashCode()));
        String str = "0000000000";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String nowStr = sdf.format(new Date());
        int len = length - nowStr.length() - (random + "").length();
        if (serlNum.length() > len) {
            serlNum = serlNum.substring(0, len);
        } else if (serlNum.length() < len) {
            serlNum = serlNum + str.substring(serlNum.length());
        }
        serlNum = nowStr + random + serlNum;
        return serlNum;
    }

    /**
     * 判断文本是否为空
     *
     * @param content
     * @return true：为空 false：不为空
     */
    public boolean isEmpty(String content) {
        if (content == null) {
            return true;
        }

        if (content.trim() == null || content.trim().equals("")) {
            return true;
        }

        return false;
    }

    /**
     * 检查特定权限，是否
     *
     * @param context
     * @param permission
     * @return
     */
    public boolean checkPermission(Context context, String permission) {
        if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {  //没有权限
            return false;
        }

        return true;
    }

    /**
     * 获取版本号名称
     *
     * @param context 上下文
     * @return
     */
    public String getVerName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().
                    getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verName;
    }

    public boolean isConnected(Context context) {
        ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = conn.getActiveNetworkInfo();
        return (info != null && info.isConnected());
    }

    /**
     * 获取手机IMEI
     *
     * @return
     */
    public String getIMEI() {
        try {
            //实例化TelephonyManager对象
            TelephonyManager telephonyManager = (TelephonyManager) MyApplication.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
            //获取IMEI号
            @SuppressLint("MissingPermission") String imei = telephonyManager.getDeviceId();
            //在次做个验证，也不是什么时候都能获取到的啊
            if (imei == null) {
                imei = "";
            }
            return imei;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }

    /**
     * 获取手机IMSI
     */
    public String getIMSI() {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) MyApplication.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
            //获取IMSI号
            @SuppressLint("MissingPermission") String imsi = telephonyManager.getSubscriberId();
            if (null == imsi) {
                imsi = "";
            }
            return imsi;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 获取当前手机号
     *
     * @return
     */
    public String getTel() {
        if (ActivityCompat.checkSelfPermission(MyApplication.getInstance(), Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MyApplication.getInstance(), Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MyApplication.getInstance(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            return tm.getLine1Number();
        }
        return "";
    }

}
