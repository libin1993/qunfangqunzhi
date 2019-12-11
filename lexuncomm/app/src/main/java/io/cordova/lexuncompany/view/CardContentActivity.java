package io.cordova.lexuncompany.view;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;
import cn.jpush.android.api.JPushInterface;
import io.cordova.lexuncompany.R;
import io.cordova.lexuncompany.bean.base.App;
import io.cordova.lexuncompany.bean.base.Request;
import io.cordova.lexuncompany.databinding.ActivityCardContentBinding;
import io.cordova.lexuncompany.inter.GetImageListener;
import io.cordova.lexuncompany.inter.TakePicOnClick;
import io.cordova.lexuncompany.units.AndroidBug5497Workaround;
import io.cordova.lexuncompany.units.AndroidToJSCallBack;
import io.cordova.lexuncompany.units.AndroidtoJS;
import io.cordova.lexuncompany.units.Base64;
import io.cordova.lexuncompany.units.BaseUnits;
import io.cordova.lexuncompany.units.ConfigUnits;
import io.cordova.lexuncompany.units.FormatUtils;
import io.cordova.lexuncompany.units.ImageUtils;
import io.cordova.lexuncompany.units.PermissionUtils;
import io.cordova.lexuncompany.units.ViewUnits;

import static io.cordova.lexuncompany.bean.base.Request.Permissions.REQUEST_ALL_PERMISSIONS;


/**
 * 卡详情页面
 * Created by JasonYao on 2018/4/3.
 */

public class CardContentActivity extends BaseActivity implements AndroidToJSCallBack {
    private static final String TAG = "libin";
    private static CardContentActivity mInstance;
    private ActivityCardContentBinding mBinding;

    private GetImageListener mListener; //获取图片监听类

    private static boolean isFirstLoaded = true;  //标记是否为第一次加载
    private IntentFilter mFilter = new IntentFilter();
    public static boolean isRunning = false;

    //开启gps
    private AlertDialog alertDialog;

    private AndroidtoJS androidtoJS;

    private NotificationManager notificationManager = null;

    //巡逻callback
    private String locationCallback;


    public static final String RECEIVER_ACTION = "location_in_background";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_card_content);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        AndroidBug5497Workaround.assistActivity(mBinding.getRoot());  //解决在沉浸式菜单栏中，软键盘不能顶起页面的bug
        date();  //每次打开APP都获取剪切板数据,检查是否有推广码
        mInstance = this;

        mFilter.addAction(Request.Broadcast.RELOADURL);
        this.registerReceiver(mBroadcastReceiver, mFilter);

        initView();

        getAllPermission();

        setListener();



//        buildNotification();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RECEIVER_ACTION);
        registerReceiver(locationChangeBroadcastReceiver, intentFilter);


    }

    private BroadcastReceiver locationChangeBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (RECEIVER_ACTION.equals(action)) {

                String location= intent.getStringExtra("location");
                sendCallback(locationCallback, "200", "success", location);

            }
        }
    };


    /**
     * @param callback
     * @param status
     * @param msg
     * @param value
     */
    private void sendCallback(String callback, String status, String msg, String value) {

        try {
            JSONObject jsonObject = new JSONObject();
            JSONObject data = new JSONObject();
            data.put("value", value);
            jsonObject.put("status", status);
            jsonObject.put("msg", msg);
            jsonObject.put("data", data);
            callBackResult(callback, jsonObject.toString());
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
            callBackResult(callback, "未知错误，联系管理员");
            e.printStackTrace();
        }
    }




    /**
     * 是否开启gps
     */
    private void isOpenGps() {
        if (!ConfigUnits.getInstance().isOpenGps()) {
            Log.d(TAG, "gps未开启");
            if (alertDialog != null) {
                alertDialog.show();
                return;
            }
            alertDialog = new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("该应用需要开启GPS定位服务，请检查是否开启并选择高精确度模式")
                    .setIcon(R.mipmap.logo)
                    .setPositiveButton("去开启", (dialogInterface, i1) -> openGps())
                    .setNegativeButton("取消", (dialogInterface, i12) -> cancelGps()).create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.setCancelable(false);
            alertDialog.show();
        } else {
            Log.d(TAG, "gps开启");
            if (alertDialog != null && alertDialog.isShowing()) {
                alertDialog.dismiss();
            }
        }
    }

    /**
     * 下拉状态栏开启gps,点击取消按钮作判断
     */
    public void cancelGps() {
        if (ConfigUnits.getInstance().isOpenGps()) {
            if (alertDialog != null && alertDialog.isShowing()) {
                alertDialog.dismiss();
            }
        } else {
            finish();
        }
    }

    /**
     * 开启gps
     */
    private void openGps() {
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
        Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(settingsIntent);
    }


    @Override
    protected void onResume() {
        super.onResume();
        isRunning = true;
        JPushInterface.setAlias(this, new Random().nextInt(900) + 100, BaseUnits.getInstance().getPhoneKey());
        isOpenGps();

    }

    @Override
    protected void onPause() {
        super.onPause();
        isRunning = false;
    }

    public static CardContentActivity getInstance() {
        return mInstance;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String url = intent.getStringExtra("url");
        if (!TextUtils.isEmpty(url)) {
            mBinding.webView.loadUrl(url);
        }
    }


    @JavascriptInterface
    public void initView() {
        WebSettings webSettings = mBinding.webView.getSettings();
        webSettings.setJavaScriptEnabled(true);  //设置与JS交互权限
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //设置运行JS弹窗
        webSettings.setUserAgentString(webSettings.getUserAgentString() + "-Android");  //设置用户代理
        webSettings.setDomStorageEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        //启用地理定位
        webSettings.setGeolocationEnabled(true);
        webSettings.setSupportZoom(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        webSettings.setUseWideViewPort(true);
        webSettings.setMediaPlaybackRequiresUserGesture(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        mBinding.webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (isFirstLoaded) {
                    mBinding.layoutLoading.setVisibility(View.VISIBLE);
                    if (newProgress >= 100) {
                        isFirstLoaded = false;
                        mBinding.layoutLoading.startAnimation(AnimationUtils.loadAnimation(CardContentActivity.this, R.anim.layout_card_loading_close));
                        mBinding.layoutLoading.setVisibility(View.GONE);

                    }
                }
            }

            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
                super.onGeolocationPermissionsShowPrompt(origin, callback);
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }
        });

        mBinding.webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                if (url == null) return false;
                if (url.startsWith("http:") || url.startsWith("https:")) {
                    view.loadUrl(url);
                } else {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                    } catch (Exception e) {
                        return true;
                    }
                }
                return true;
            }
        });

        androidtoJS = new AndroidtoJS(this);
        mBinding.webView.addJavascriptInterface(androidtoJS, "NativeForJSUnits");
        mBinding.webView.loadUrl(App.LexunCard.CardUrl);

    }

    private void setListener() {
        mBinding.imgBtnBack.setOnClickListener(view -> {
            if (mBinding.webView.canGoBack()) {
                mBinding.webView.goBack();
            } else {
                finish();
            }
        });

        mBinding.webView.setOnKeyListener((view, i, keyEvent) -> {
            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                if (i == KeyEvent.KEYCODE_BACK && mBinding.webView.canGoBack()) {
                    mBinding.webView.goBack();
                    return true;
                }
            }
            return false;
        });
    }

    /**
     * Android6.0动态获取所有权限
     */
    private void getAllPermission() {
        ActivityCompat.requestPermissions(this, App.mPermissionList, REQUEST_ALL_PERMISSIONS);
    }

    /**
     * @param view 退出app
     */
    public void closeApp(View view) {
        if (mBinding.webView.canGoBack()) {
            mBinding.webView.goBack();
        } else {
            finish();
        }
    }


    /**
     * 拍照或选择照片
     */
    public void takePhoto(GetImageListener listener) {
        mListener = listener;
        if (PermissionUtils.getInstance().hasPermission(this, App.pictureSelect)) {
            PictureSelector.create(this)
                    .openGallery(PictureMimeType.ofImage())
                    .selectionMode(PictureConfig.SINGLE)
                    .isCamera(true)
                    .enableCrop(true)// 是否裁剪 true or false
                    .compress(true)// 是否压缩 true or false
                    .previewImage(false)
                    .withAspectRatio(1, 1)// int 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                    .freeStyleCropEnabled(true)// 裁剪框是否可拖拽 true or false
                    .circleDimmedLayer(false)// 是否圆形裁剪 true or false
                    .showCropGrid(false)
                    .minimumCompressSize(100)// 小于100kb的图片不压缩
                    .scaleEnabled(true)// 裁剪是否可放大缩小图片 true or false
                    .isDragFrame(true)// 是否可拖动裁剪框(固定)
                    .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
        } else {
            ActivityCompat.requestPermissions(this, App.pictureSelect, Request.Permissions.REQUEST_CAMERA);
        }
    }

    public void openGallery(GetImageListener listener) {
        mListener = listener;
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())
                .selectionMode(PictureConfig.SINGLE)
                .previewImage(false)
                .isCamera(false)
                .enableCrop(true)// 是否裁剪 true or false
                .compress(true)// 是否压缩 true or false
                .withAspectRatio(1, 1)// int 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                .freeStyleCropEnabled(true)// 裁剪框是否可拖拽 true or false
                .circleDimmedLayer(false)// 是否圆形裁剪 true or false
                .showCropGrid(false)
                .minimumCompressSize(100)// 小于100kb的图片不压缩
                .scaleEnabled(true)// 裁剪是否可放大缩小图片 true or false
                .isDragFrame(true)// 是否可拖动裁剪框(固定)
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
    }

    public void openTheCamera(GetImageListener listener) {
        mListener = listener;
        if (PermissionUtils.getInstance().hasPermission(this, App.pictureSelect)) {
            PictureSelector.create(this)
                    .openCamera(PictureMimeType.ofImage())
                    .selectionMode(PictureConfig.SINGLE)
                    .previewImage(false)
                    .enableCrop(true)// 是否裁剪 true or false
                    .compress(true)// 是否压缩 true or false
                    .withAspectRatio(1, 1)// int 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                    .freeStyleCropEnabled(true)// 裁剪框是否可拖拽 true or false
                    .circleDimmedLayer(false)// 是否圆形裁剪 true or false
                    .showCropGrid(false)
                    .minimumCompressSize(100)// 小于100kb的图片不压缩
                    .scaleEnabled(true)// 裁剪是否可放大缩小图片 true or false
                    .isDragFrame(true)// 是否可拖动裁剪框(固定)
                    .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
        } else {
            ActivityCompat.requestPermissions(this, App.pictureSelect, Request.Permissions.REQUEST_CAMERA);
        }
    }

    @Override
    public void callBackResult(String callBack, String value) {
        if (FormatUtils.getIntances().isEmpty(callBack)) {
            return;
        }

        Log.d(TAG, callBack + ":" + value);

        runOnUiThread(() -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mBinding.webView.post(() -> mBinding.webView.evaluateJavascript("javascript:" + callBack + "('" + value + "')", s -> Log.e(TAG, s)));
            } else {
                mBinding.webView.post(() -> mBinding.webView.loadUrl("javascript:" + callBack + "('" + value + "')"));
            }
        });
    }

    @Override
    public void setTitleBar(String btnBackType, String title, String textColor, String bgColor, String isDisplay) {

    }


    @Override
    protected void onDestroy() {
        mInstance = null;
        mListener = null;
        mFilter = null;
        isFirstLoaded = true;
        unregisterReceiver(mBroadcastReceiver);
        destroyWebView();
        mBinding = null;

        if (locationChangeBroadcastReceiver != null)
            unregisterReceiver(locationChangeBroadcastReceiver);

        super.onDestroy();

    }

    public void destroyWebView() {
        mBinding.webView.loadUrl("about:blank"); // clearView() should be changed to loadUrl("about:blank"), since clearView() is deprecated now
        mBinding.webView.onPause();
        mBinding.webView.clearCache(true);
        mBinding.webView.removeAllViews();
        mBinding.webView.destroy();
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Request.Broadcast.RELOADURL)) {
                String url = intent.getStringExtra("url");
                mBinding.webView.loadUrl(url);
            }
        }
    };



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == PictureConfig.CHOOSE_REQUEST) {
            //图片压缩成功
            String img = PictureSelector.obtainMultipleResult(data).get(0).getCompressPath();
            if (!TextUtils.isEmpty(img) && mListener != null) {
                mListener.getImage(Base64.encode(ImageUtils.getInstance().image2byte(img)));
            }

        }
    }


    /**
     * 横竖屏切换
     *
     * @param requestedOrientation
     */
    @Override
    public void setRequestedOrientation(int requestedOrientation) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_ALL_PERMISSIONS) {
            if (!BaseUnits.getInstance().checkPermission(this, Manifest.permission.READ_PHONE_STATE)) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_DENIED) {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)) {
                        AlertDialog alertDialog = new AlertDialog.Builder(this)
                                .setTitle("提示")
                                .setMessage("未获得相应权限，无法正常使用APP，请前往安全中心>权限管理>乐巡，开启相关权限")
                                .setIcon(R.mipmap.logo)
                                .setPositiveButton("确定", (dialogInterface, i1) -> {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                                    intent.setData(uri);
                                    startActivityForResult(intent, REQUEST_ALL_PERMISSIONS);
                                })
                                .setNegativeButton("取消", (dialogInterface, i12) -> this.finish()).create();
                        alertDialog.setCanceledOnTouchOutside(false);
                        alertDialog.setCancelable(false);
                        alertDialog.show();
                        return;
                    }
                }
                AlertDialog alertDialog = new AlertDialog.Builder(this)
                        .setTitle("提示")
                        .setMessage("未获得读取手机状态权限，无法正常使用APP，是否给予权限？")
                        .setIcon(R.mipmap.logo)
                        .setPositiveButton("是", (dialogInterface, i1) -> getAllPermission())
                        .setNegativeButton("否", (dialogInterface, i12) -> this.finish()).create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.setCancelable(false);
                alertDialog.show();
            } else if (!BaseUnits.getInstance().checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                        AlertDialog alertDialog = new AlertDialog.Builder(this)
                                .setTitle("提示")
                                .setMessage("未获得定位权限，无法正常使用APP，请前往安全中心>权限管理>智慧家园，开启相关权限")
                                .setIcon(R.mipmap.logo)
                                .setPositiveButton("确定", (dialogInterface, i1) -> {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                                    intent.setData(uri);
                                    startActivityForResult(intent, REQUEST_ALL_PERMISSIONS);
                                })
                                .setNegativeButton("取消", (dialogInterface, i12) -> this.finish()).create();
                        alertDialog.setCanceledOnTouchOutside(false);
                        alertDialog.setCancelable(false);
                        alertDialog.show();
                        return;
                    }
                }
                AlertDialog alertDialog = new AlertDialog.Builder(this)
                        .setTitle("提示")
                        .setMessage("未获得定位权限，无法正常使用APP，是否给予权限？")
                        .setIcon(R.mipmap.logo)
                        .setPositiveButton("是", (dialogInterface, i1) -> getAllPermission())
                        .setNegativeButton("否", (dialogInterface, i12) -> this.finish()).create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.setCancelable(false);
                alertDialog.show();
            }
        }
    }

    /**
     * 获取剪切板数据
     */
    private void date() {
        ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData data = cm.getPrimaryClip();
        try {
            ClipData.Item item = data.getItemAt(0);
            String content = item.getText().toString();
            Log.e(TAG, content);
            if (content.split("=").length == 2 && content.split("=")[0].equals("lexunReferralCode")) {
                String mCode = content.split("=")[1];
                ConfigUnits.getInstance().setLexunReferralCode(mCode);
                Log.e(TAG, "推广码为1：" + mCode);
                Log.e(TAG, "推广码为2：" + ConfigUnits.getInstance().getLexunReferralCode());
                cm.setPrimaryClip(ClipData.newPlainText(null, ""));  //清空剪切板
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }
}
