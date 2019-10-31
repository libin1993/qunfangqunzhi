package io.cordova.lexuncompany.units;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.google.gson.Gson;
import com.tencent.bugly.beta.Beta;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import io.cordova.lexuncompany.application.MyApplication;
import io.cordova.lexuncompany.inter.QrCodeScanInter;
import io.cordova.lexuncompany.view.CardContentActivity;
import io.cordova.lexuncompany.inter.CityPickerResultListener;
import io.cordova.lexuncompany.view.QrCodeScanActivity;
import io.cordova.lexuncompany.view.ScanQRCodeActivity;


/**
 * Created by JasonYao on 2018/9/3.
 */
public class AndroidtoJS implements QrCodeScanInter, CityPickerResultListener {
    private static final String TAG = "AndroidtoJS--";
    private static AndroidtoJS mInstance = null;

    //巡逻相关
    private LocationClient mBaiduLocationClient;
    //百度定位相关
    private LocationClient mBaiduLocationClient1;


    private AndroidToJSCallBack mCallBack;
    private Gson mGson = new Gson();


    public static AndroidtoJS getInstance(AndroidToJSCallBack callBack) {
        mInstance = new AndroidtoJS(callBack);
        return mInstance;
    }

    private AndroidtoJS(AndroidToJSCallBack callBack) {
        this.mCallBack = callBack;
    }


    private LocationClient getBaiduLocationClient1(String callBack) {
        if (mBaiduLocationClient1 == null) {
            mBaiduLocationClient1 = new LocationClient(MyApplication.getInstance());
            LocationClientOption mBaiduOption1 = new LocationClientOption();
            mBaiduOption1.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
            mBaiduOption1.setCoorType("bd09ll");
            mBaiduOption1.setIgnoreKillProcess(false);
            mBaiduOption1.setScanSpan(0);
            mBaiduOption1.setOpenGps(true);
            mBaiduLocationClient1.setLocOption(mBaiduOption1);
            mBaiduLocationClient1.start();
            mBaiduLocationClient1.registerLocationListener(new BDAbstractLocationListener() {
                @Override
                public void onReceiveLocation(BDLocation bdLocation) {
                    if (bdLocation != null && bdLocation.getLatitude() > 1 && bdLocation.getLongitude() > 1) {
                        sendCallBack(callBack, "200", "success", bdLocation.getLatitude() + "," + bdLocation.getLongitude());
                        if (mBaiduLocationClient1 != null) {
                            mBaiduLocationClient1.stop();
                            mBaiduLocationClient1 = null;
                        }
                    }else {
                        sendCallBack(callBack, "500", "fail", "定位失败");
                    }

                }

                @Override
                public void onLocDiagnosticMessage(int i, int i1, String s) {
                    sendCallBack(callBack, "500", "fail", s);
                }
            });
        }

        return mBaiduLocationClient1;
    }

    @JavascriptInterface
    public void hello(Map<String, String> object) {

    }

    /**
     * 获取设备唯一标识码
     *
     * @return
     */
    @JavascriptInterface
    public void getDeviceId(String callBack) {
        sendCallBack(callBack, "200", "success", BaseUnits.getInstance().getPhoneKey());
    }

    /**
     * 获取设备硬件信息
     *
     * @param callBack
     */
    @JavascriptInterface
    public void getPhoneInfo(String callBack) {
        JSONObject jsonObject = new JSONObject();
        JSONObject data = new JSONObject();
        try {
            data.put("mac", MacUtils.getMobileMAC(MyApplication.getInstance()));
            data.put("imei", BaseUnits.getInstance().getIMEI());
            data.put("imsi", BaseUnits.getInstance().getIMSI());
            data.put("phone", BaseUnits.getInstance().getTel());
            jsonObject.put("status", "200");
            jsonObject.put("msg", "success");
            jsonObject.put("data", data);
            Log.e(TAG, String.valueOf(jsonObject));
            mCallBack.callBackResult(callBack, jsonObject.toString());
        } catch (JSONException e) {
            mCallBack.callBackResult(callBack, "未知错误，联系管理员");
            e.printStackTrace();
        }
    }

    /**
     * 获取用户UserId（在该项目中，userid使用Token标识）
     *
     * @return
     */
    @JavascriptInterface
    public void getUserId(String callBack) {
        sendCallBack(callBack, "200", "success", UserUnits.getInstance().getToken());
    }

    /**
     * 获取当前定位城市
     *
     * @return
     */
    @JavascriptInterface
    public void getLocation(String callBack) {
        JSONObject jsonObject = new JSONObject();
        JSONObject data = new JSONObject();
        try {
            data.put("cityName", UserUnits.getInstance().getLocation());
            data.put("cityCode", ConfigUnits.getInstance().getCityIdByName(UserUnits.getInstance().getLocation()));
            jsonObject.put("status", "200");
            jsonObject.put("msg", "success");
            jsonObject.put("data", data);
            Log.e(TAG, String.valueOf(jsonObject));
            mCallBack.callBackResult(callBack, jsonObject.toString());
        } catch (JSONException e) {
            mCallBack.callBackResult(callBack, "未知错误，联系管理员");
            e.printStackTrace();
        }
    }

    /**
     * 获取版本号
     *
     * @return
     */
    @JavascriptInterface
    public void getVersionNO(String callBack) {
        sendCallBack(callBack, "200", "success", "v." + BaseUnits.getInstance().getVerName(MyApplication.getInstance()));
    }

    /**
     * 版本号
     */
    @JavascriptInterface
    public void versionUpdate() {
        Beta.checkUpgrade();
    }

    /**
     * 扫描二维码
     *
     * @return
     */
    @JavascriptInterface
    public void qrScan(String callBack) {
        Intent intent = new Intent(MyApplication.getInstance().getBaseContext(), ScanQRCodeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("callBack", callBack);
        ScanQRCodeActivity.setQrCodeScanInter(AndroidtoJS.this);
        MyApplication.getInstance().getBaseContext().startActivity(intent);
    }

    /**
     * 获取当前定位城市
     *
     * @param callBack
     */
    @JavascriptInterface
    public void getCurrentCity(String callBack) {
        sendCallBack(callBack, "200", "success", ConfigUnits.getInstance().getCityIdByName(UserUnits.getInstance().getSelectCity()));
    }

    @JavascriptInterface
    public void getUserInfo(String callBack) {
        Log.e(TAG, "getUserInfo");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("clientId", BaseUnits.getInstance().getPhoneKey());
            jsonObject.put("token", UserUnits.getInstance().getToken());
            jsonObject.put("loginStatus", FormatUtils.getIntances().isEmpty(UserUnits.getInstance().getToken()) ? "0" : "1");
            jsonObject.put("phone", UserUnits.getInstance().getPhone());
            jsonObject.put("realName", UserUnits.getInstance().getRealName());
            jsonObject.put("status", UserUnits.getInstance().getStatus());
            jsonObject.put("headImgPath", UserUnits.getInstance().getHeadImgPath());
            jsonObject.put("nickName", UserUnits.getInstance().getNickName());
            jsonObject.put("sex", UserUnits.getInstance().getSex());
            jsonObject.put("birthDay", UserUnits.getInstance().getBirthDay());
            jsonObject.put("address", UserUnits.getInstance().getAddress());
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
        }

        Log.e(TAG, jsonObject.toString());

        sendCallBackJson(callBack, "200", "success", jsonObject);
    }

    /**
     * 拨打电话
     *
     * @param number
     */
    @JavascriptInterface
    public void call(String number) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MyApplication.getInstance().startActivity(intent);
    }

    /**
     * 退出页面
     */
    @JavascriptInterface
    public void finish() {
        if (CardContentActivity.getInstance() != null) {
            CardContentActivity.getInstance().finish();
        }
    }

    /**
     * 获取百度定位点
     *
     * @return
     */
    @JavascriptInterface
    public void getBaiduCoordinate(String callBack) {
        mBaiduLocationClient1 = new LocationClient(CardContentActivity.getInstance());

        LocationClientOption mBaiduOption1 = new LocationClientOption();

        mBaiduOption1.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        mBaiduOption1.setCoorType("bd09ll");

        mBaiduOption1.setIgnoreKillProcess(false);
        mBaiduOption1.setScanSpan(500);

        mBaiduOption1.setOpenGps(true);
        mBaiduLocationClient1.setLocOption(mBaiduOption1);
        mBaiduLocationClient1.start();
        mBaiduLocationClient1.registerLocationListener(new BDAbstractLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                if (bdLocation != null && bdLocation.getLatitude() > 1 && bdLocation.getLongitude() > 1) {
                    sendCallBack(callBack, "200", "success", bdLocation.getLatitude() + "," + bdLocation.getLongitude());
                    if (mBaiduLocationClient1 != null) {
                        mBaiduLocationClient1.stop();
                        mBaiduLocationClient1 = null;
                    }
                }else {
                    sendCallBack(callBack, "500", "fail", "定位失败");
                }

            }

            @Override
            public void onLocDiagnosticMessage(int i, int i1, String s) {
                sendCallBack(callBack, "500", "fail", s);
            }
        });
    }

    /**
     * 设置标题栏
     *
     * @param titleBar 0：黑色返回按钮，1：白色返回按钮，2：灰色返回按钮
     */
    @JavascriptInterface
    public void setTitleBar(String titleBar) {
        try {
            JSONObject jsonObject = new JSONObject(titleBar);
            Log.e(TAG, jsonObject.toString());
            String btnBackKey = jsonObject.getString("btnBackKey");
            String title = jsonObject.getString("tit");
            String textColor = jsonObject.getString("textColor");
            String bgColor = jsonObject.getString("bgColor");
            String isDisplay = jsonObject.getString("isShow");  //0:不显示  1:显示
            Log.e(TAG, "设置标题栏：" + btnBackKey + "," + title + "," + textColor + "," + bgColor);
            mCallBack.setTitleBar(btnBackKey, title, FormatUtils.getIntances().colorTo6Color(textColor), FormatUtils.getIntances().colorTo6Color(bgColor), isDisplay);
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void beginPatrol(String callBack) {

        if (mBaiduLocationClient == null) {

            mBaiduLocationClient = new LocationClient(MyApplication.getInstance());

            LocationClientOption mBaiduOption = new LocationClientOption();

            mBaiduOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
            mBaiduOption.setCoorType("bd09ll");

            mBaiduOption.setIgnoreKillProcess(false);
            mBaiduOption.setScanSpan(7000);

            mBaiduOption.setOpenGps(true);
            mBaiduLocationClient.setLocOption(mBaiduOption);
            mBaiduLocationClient.start();
            mBaiduLocationClient.registerLocationListener(new BDAbstractLocationListener() {
                @Override
                public void onReceiveLocation(BDLocation bdLocation) {
                    if (bdLocation != null && bdLocation.getLatitude() >1 && bdLocation.getLongitude() > 1) {
                        sendCallBack(callBack, "200", "success", bdLocation.getLatitude() + "," + bdLocation.getLongitude());
                    }
                }
            });
        } else if (!mBaiduLocationClient.isStarted()) {
            mBaiduLocationClient.start();
        }

    }


    /**
     * 结束巡逻（百度坐标系统）
     */
    @JavascriptInterface
    public void endPatrol() {
        if (mBaiduLocationClient != null) {
            mBaiduLocationClient.stop();
            mBaiduLocationClient = null;
        }

    }

    /**
     * 获取照片（拍照、相册选择）
     *
     * @param callBack
     */
    @JavascriptInterface
    public void getPhoto(String callBack) {
        CardContentActivity.getInstance().takePhoto(imageData -> {
            Log.e(TAG, imageData);
            sendCallBack(callBack, "200", "success", "data:image/jpeg;base64," + imageData);
        });

    }

    /**
     * 获取推荐码
     *
     * @return
     */
    @JavascriptInterface
    public void getLexunReferralCode(String callBack) {
        Log.e(TAG, "推广码为：" + ConfigUnits.getInstance().getLexunReferralCode());
        sendCallBack(callBack, "200", "success", ConfigUnits.getInstance().getLexunReferralCode());
        ConfigUnits.getInstance().setLexunReferralCode("");
    }

    /**
     * 打开相册
     *
     * @param callBack
     */
    @JavascriptInterface
    public void OpenGallery(String callBack) {
        CardContentActivity.getInstance().openGallery(imageData -> {
            Log.e(TAG, imageData);
            sendCallBack(callBack, "200", "success", "data:image/jpeg;base64," + imageData);
        });
    }

    /**
     * 打开相机
     *
     * @param callBack
     */
    @JavascriptInterface
    public void OpenTheCamera(String callBack) {
        CardContentActivity.getInstance().openTheCamera(imageData -> {
            Log.e(TAG, imageData);
            sendCallBack(callBack, "200", "success", "data:image/jpeg;base64," + imageData);
        });
    }

    public void sendCallBack(String callBack, String status, String msg, String value) {
        Log.e(TAG, "callBack:" + callBack);
        JSONObject jsonObject = new JSONObject();
        JSONObject data = new JSONObject();
        try {
            data.put("value", value);
            jsonObject.put("status", status);
            jsonObject.put("msg", msg);
            jsonObject.put("data", data);
            Log.e(TAG, "返回结果：" + value);
            mCallBack.callBackResult(callBack, jsonObject.toString());
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
            mCallBack.callBackResult(callBack, "未知错误，联系管理员");
            e.printStackTrace();
        }
    }

    public void sendCallBackJson(String callBack, String status, String msg, JSONObject value) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("status", status);
            jsonObject.put("msg", msg);
            jsonObject.put("data", value);
            Log.e(TAG, String.valueOf(jsonObject));
            mCallBack.callBackResult(callBack, jsonObject.toString());
        } catch (JSONException e) {
            mCallBack.callBackResult(callBack, "未知错误，联系管理员");
            e.printStackTrace();
        }
    }

    @Override
    public void getQrCodeScanResult(String result) {

    }

    @Override
    public void getQrCodeScanResult(String callBack, String result) {
        Log.e(TAG, result);
        sendCallBack(callBack, "200", "success", result);
    }

    @Override
    public void getCityPickerResultListener(String callBack, JSONObject result) {
        sendCallBackJson(callBack, "200", "success", result);
    }
}
