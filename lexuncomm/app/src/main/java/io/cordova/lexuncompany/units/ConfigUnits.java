package io.cordova.lexuncompany.units;

import android.database.Cursor;

import io.cordova.lexuncompany.bean.base.Field;

/**
 * 配置工具类
 * Created by JasonYao on 2018/2/27.
 */

public class ConfigUnits {
    private static ConfigUnits mInstances = null;  //单例对象

    private ConfigUnits() {
    }  //私有化构造方法

    public static final ConfigUnits getInstance() {
        if (mInstances == null) {
            synchronized (ConfigUnits.class) {
                if (mInstances == null) {
                    mInstances = new ConfigUnits();
                }
            }
        }

        return mInstances;
    }

    /**
     * 设置模拟手机IMEI号
     */
    public void setPhoneAnalogIMEI(String imei) {
        SPUtils.getInstance().put(SPUtils.FILE_CONFIG, SPUtils.analogImei, imei);
    }

    /**
     * 清空模拟手机的IMEI号
     */
    public void clearPhoneAnalogIMEI() {
        SPUtils.getInstance().clear(SPUtils.FILE_CONFIG);
    }

    /**
     * 获取模拟手机IMEI号
     */
    public String getPhoneAnalogIMEI() {
        return (String) SPUtils.getInstance().get(SPUtils.FILE_CONFIG, SPUtils.analogImei, "");
    }

    /**
     * 设置本地数据库是否拷贝完成（0即为拷贝完成）
     */
    public void setDBStatus() {
        SPUtils.getInstance().put(SPUtils.DBCOPY, "DBStatus", "0");
    }

    /**
     * 获取本地数据库是否拷贝完成（0即为拷贝完成）
     *
     * @return
     */
    public String getDBStatus() {
        return SPUtils.getInstance().get(SPUtils.DBCOPY, "DBStatus", "").toString();
    }

    /**
     * 根据城市名称获取城市ID
     *
     * @param cityName
     * @return
     */
    public String getCityIdByName(String cityName) {
        String cityId = null;
        Cursor cursor = BaseDbHelper.getInstance().get("select " + Field.Area.area_id + " from " + Field.Area.dbName + " where " + Field.Area.area_name + "='" + cityName + "'", new String[]{});

        if (cursor.getCount() <= 0) {
            return null;
        } else {
            while (cursor.moveToNext()) {
                cityId = cursor.getString(0);
            }

            return cityId;
        }
    }

    /**
     * 获取乐巡推荐码
     *
     * @return
     */
    public String getLexunReferralCode() {
        return (String) SPUtils.getInstance().get(SPUtils.FILE_CONFIG, SPUtils.lexunReferralCode, "");
    }

    /**
     * 设置乐巡推荐码
     *
     * @param lexunReferralCode
     */
    public void setLexunReferralCode(String lexunReferralCode) {
        SPUtils.getInstance().put(SPUtils.FILE_CONFIG, SPUtils.lexunReferralCode, lexunReferralCode);
    }
}
