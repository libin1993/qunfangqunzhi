package io.cordova.lexuncompany.units;

import android.util.Log;

import java.util.Map;

import io.cordova.lexuncompany.bean.User;

/**
 * Created by JasonYao on 2018/3/22.
 */

public class UserUnits {
    //构建单例对象
    public static UserUnits mUserUnits = null;

    //私有化构造方法
    private UserUnits() {
    }

    /**
     * 单例对象获取方式
     *
     * @return
     */
    public static UserUnits getInstance() {
        if (mUserUnits == null) {
            synchronized (UserUnits.class) {
                if (mUserUnits == null) {
                    mUserUnits = new UserUnits();
                }
            }
        }

        return mUserUnits;
    }

    /**
     * 设置全部的用户信息
     *
     * @param user
     */
    public void setAllUserInfo(User user) {
        if (user == null) {
            return;
        }

        Log.e("TAGTAG--", "status为：" + user.getStatus());
        SPUtils.getInstance().put(SPUtils.FILE_USER, SPUtils.token, user.getToken()); //缓存Token
        SPUtils.getInstance().put(SPUtils.FILE_USER, SPUtils.accountId, user.getAccountID());
        SPUtils.getInstance().put(SPUtils.FILE_USER, SPUtils.phone, user.getPhone());
        SPUtils.getInstance().put(SPUtils.FILE_USER, SPUtils.email, user.getEmail());
        SPUtils.getInstance().put(SPUtils.FILE_USER, SPUtils.accountName, user.getAccountName());
        SPUtils.getInstance().put(SPUtils.FILE_USER, SPUtils.cardId, user.getCardID());
        SPUtils.getInstance().put(SPUtils.FILE_USER, SPUtils.realName, user.getRealName());
        SPUtils.getInstance().put(SPUtils.FILE_USER, SPUtils.status, user.getStatus());
        SPUtils.getInstance().put(SPUtils.FILE_USER, SPUtils.confirmStatusName, user.getConfirmStatusName());
        SPUtils.getInstance().put(SPUtils.FILE_USER, SPUtils.autherizeName, user.getAutherizeName());
        SPUtils.getInstance().put(SPUtils.FILE_USER, SPUtils.cardId, user.getCardID());
        SPUtils.getInstance().put(SPUtils.FILE_USER, SPUtils.headImgPath, user.getHeadImgPath());
        SPUtils.getInstance().put(SPUtils.FILE_USER, SPUtils.nickName, user.getNickName());
        SPUtils.getInstance().put(SPUtils.FILE_USER, SPUtils.birthDay, user.getBirthday());
        SPUtils.getInstance().put(SPUtils.FILE_USER, SPUtils.sex, user.getSex());
        SPUtils.getInstance().put(SPUtils.FILE_USER, SPUtils.address, user.getAddress());
    }

    /**
     * 清除用户信息
     */
    public void clearUserInfo() {
        SPUtils.getInstance().clear(SPUtils.FILE_USER); //清空缓存
    }

    /**
     * 获取全部的用户信息
     *
     * @return
     */
    public Map<String, ?> getAllUser() {
        Map<String, ?> mMap;
        mMap = SPUtils.getInstance().getAll(SPUtils.FILE_USER);

        return mMap;
    }

    /**
     * 获取token
     *
     * @return
     */
    public String getToken() {
        return SPUtils.getInstance().get(SPUtils.FILE_USER, SPUtils.token, "").toString();
    }

    /**
     * 设置Token
     *
     * @param token
     */
    public void setToken(String token) {
        SPUtils.getInstance().put(SPUtils.FILE_USER, SPUtils.token, token);
    }

    /**
     * 获取Account
     *
     * @return
     */
    public String getAccount() {
        return SPUtils.getInstance().get(SPUtils.FILE_USER, SPUtils.account, "").toString();
    }

    /**
     * 获取AccountId
     *
     * @return
     */
    public String getAccountId() {
        return SPUtils.getInstance().get(SPUtils.FILE_USER, SPUtils.accountId, "").toString();
    }

    /**
     * 获取AccountName
     *
     * @return
     */
    public String getAccountName() {
        return SPUtils.getInstance().get(SPUtils.FILE_USER, SPUtils.accountName, "").toString();
    }

    /**
     * 获取当前定位城市
     *
     * @return
     */
    public String getLocation() {
        return SPUtils.getInstance().get(SPUtils.FILE_LOCATION, SPUtils.location, "").toString();
    }

    /**
     * 设置当前定位城市
     *
     * @param location
     * @return
     */
    public void setLocation(String location) {
        SPUtils.getInstance().put(SPUtils.FILE_LOCATION, SPUtils.location, location);
    }

    /**
     * 获取选择的城市
     * @return
     */
    public String getSelectCity(){
        return SPUtils.getInstance().get(SPUtils.FILE_LOCATION,SPUtils.selectCity,"").toString();
    }

    /**
     * 设置选择城市
     * @param selectCity
     */
    public void setSelectCity(String selectCity){
        SPUtils.getInstance().put(SPUtils.FILE_LOCATION,SPUtils.selectCity,selectCity);
    }

    public String getConfirmStatusName() {
        return SPUtils.getInstance().get(SPUtils.FILE_USER, SPUtils.confirmStatusName, "").toString();
    }

    public String getAutherizeName() {
        return SPUtils.getInstance().get(SPUtils.FILE_USER, SPUtils.autherizeName, "").toString();
    }

    public String getStatus() {
        return SPUtils.getInstance().get(SPUtils.FILE_USER, SPUtils.status, "").toString();
    }

    public String getPhone() {
        return SPUtils.getInstance().get(SPUtils.FILE_USER, SPUtils.phone, "").toString();
    }

    public String getRealName() {
        return SPUtils.getInstance().get(SPUtils.FILE_USER, SPUtils.realName, "").toString();
    }

    public String getCardId() {
        return SPUtils.getInstance().get(SPUtils.FILE_USER, SPUtils.cardId, "").toString();
    }

    public String getHeadImgPath() {
        return SPUtils.getInstance().get(SPUtils.FILE_USER, SPUtils.headImgPath, "").toString();
    }

    public String getNickName() {
        return SPUtils.getInstance().get(SPUtils.FILE_USER, SPUtils.nickName, "").toString();
    }

    public String getSex() {
        return SPUtils.getInstance().get(SPUtils.FILE_USER, SPUtils.sex, "").toString();
    }

    public String getBirthDay() {
        return SPUtils.getInstance().get(SPUtils.FILE_USER, SPUtils.birthDay, "").toString();
    }

    public String getAddress() {
        return SPUtils.getInstance().get(SPUtils.FILE_USER, SPUtils.address, "").toString();
    }
}
