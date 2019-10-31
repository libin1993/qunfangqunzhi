package io.cordova.lexuncompany.view;

import android.Manifest;
import android.app.AlertDialog;
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
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.jph.takephoto.compress.CompressImage;
import com.jph.takephoto.compress.CompressImageImpl;
import com.jph.takephoto.model.TImage;
import com.jph.takephoto.model.TResult;

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
import io.cordova.lexuncompany.units.PhotoUntils;
import io.cordova.lexuncompany.units.ViewUnits;

import static io.cordova.lexuncompany.bean.base.Request.Permissions.REQUEST_ALL_PERMISSIONS;


/**
 * 卡详情页面
 * Created by JasonYao on 2018/4/3.
 */

public class CardContentActivity extends BaseTakePhotoActivity implements AndroidToJSCallBack {
    private static final String TAG = "CardContentActivity--";
    private static CardContentActivity mInstance;
    private ActivityCardContentBinding mBinding;

    private GetImageListener mListener; //获取图片监听类

    private static boolean isFirstLoaded = true;  //标记是否为第一次加载
    private IntentFilter mFilter = new IntentFilter();
    public static boolean isRunning = false;
    private String mCode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_card_content);
        AndroidBug5497Workaround.assistActivity(mBinding.getRoot());  //解决在沉浸式菜单栏中，软键盘不能顶起页面的bug
        date();  //每次打开APP都获取剪切板数据,检查是否有推广码
        mInstance = this;

        mFilter.addAction(Request.Broadcast.RELOADURL);
        this.registerReceiver(mBroadcastReceiver, mFilter);

        initView();

        getAllPermission();

        setListener();

    }

    @Override
    protected void onResume() {
        super.onResume();
        isRunning = true;
        JPushInterface.setAlias(this, new Random().nextInt(900) + 100, BaseUnits.getInstance().getPhoneKey());
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
        if (!TextUtils.isEmpty(url)){
            mBinding.webView.loadUrl(url);
        }
    }

    @JavascriptInterface
    public void initView() {

        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;     // 屏幕宽度（像素）
        int height = metric.heightPixels;   // 屏幕高度（像素）

        if ((double) height / width > 2) {
            mBinding.layoutLoading.setImageResource(R.mipmap.bg_splash_large);
        } else {
            mBinding.layoutLoading.setImageResource(R.mipmap.bg_splash);
        }


        WebSettings webSettings = mBinding.webView.getSettings();
        webSettings.setJavaScriptEnabled(true);  //设置与JS交互权限
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //设置运行JS弹窗
        webSettings.setUserAgentString(webSettings.getUserAgentString() + "-Android");  //设置用户代理
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setSupportZoom(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);

        //启用地理定位
        webSettings.setGeolocationEnabled(true);
        webSettings.setUseWideViewPort(true);
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
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if (request.getUrl() == null) return false;
                if (request.getUrl().toString().startsWith("http:") || request.getUrl().toString().startsWith("https:")) {
                    view.loadUrl(String.valueOf(request.getUrl()));
                } else {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, request.getUrl());   //UrlScheam为自定义的，打开APP
                        startActivity(intent);
                    } catch (Exception e) {
                        return true;
                    }
                }
                return true;
            }
        });

        mBinding.webView.addJavascriptInterface(AndroidtoJS.getInstance(this), "NativeForJSUnits");
        mBinding.webView.loadUrl(App.LexunCard.CardUrl);
//        mBinding.webView.loadUrl("http://192.168.50.251/");
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

    @Override
    public void finish() {
        super.finish();
    }

    public void finish(View view) {
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
        showTakePicDialog(new TakePicOnClick() {
            @Override
            public void choosePhotoOnClick() {
                getTakePhoto().onPickFromGalleryWithCrop(PhotoUntils.getInstance().createImageFileUri(), PhotoUntils.getInstance().getCropOptions());
            }

            @Override
            public void takePictureOnClick() {
                getTakePhoto().onPickFromCaptureWithCrop(PhotoUntils.getInstance().createImageFileUri(), PhotoUntils.getInstance().getCropOptions());
            }
        });
    }

    public void openGallery(GetImageListener listener) {
        mListener = listener;
        getTakePhoto().onPickFromGalleryWithCrop(PhotoUntils.getInstance().createImageFileUri(), PhotoUntils.getInstance().getCropOptions());
    }

    public void openTheCamera(GetImageListener listener) {
        mListener = listener;
        getTakePhoto().onPickFromCaptureWithCrop(PhotoUntils.getInstance().createImageFileUri(), PhotoUntils.getInstance().getCropOptions());
    }

    @Override
    public void callBackResult(String callBack, String value) {
        if (FormatUtils.getIntances().isEmpty(callBack)) {
            return;
        }

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
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mInstance = null;
        mListener = null;
        mFilter = null;
        isFirstLoaded = true;
        unregisterReceiver(mBroadcastReceiver);
        destroyWebView();
        mBinding = null;
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
    public void takeSuccess(TResult result) {
        super.takeSuccess(result);
        ViewUnits.getInstance().showLoading(this, "压缩中");
        CompressImageImpl.of(this, PhotoUntils.getInstance().getCompressConfig(), result.getImages(), new CompressImage.CompressListener() {
            @Override
            public void onCompressSuccess(ArrayList<TImage> images) {
                ViewUnits.getInstance().missLoading();
                //图片压缩成功
                mListener.getImage(Base64.encode(ImageUtils.getInstance().image2byte(images.get(0).getCompressPath())));
                missTakePicDialog();
            }

            @Override
            public void onCompressFailed(ArrayList<TImage> images, String msg) {
                //图片压缩失败
            }
        }).compress();
    }

    @Override
    public void takeFail(TResult result, String msg) {
        super.takeFail(result, msg);
        ViewUnits.getInstance().showToast(msg);
        missTakePicDialog();
    }

    @Override
    public void takeCancel() {
        super.takeCancel();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
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
                                .setMessage("未获得相应权限，无法正常使用APP，请前往安全中心>权限管理>警保联控，开启相关权限")
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
                                .setMessage("未获得定位权限，无法正常使用APP，请前往安全中心>权限管理>警保联控，开启相关权限")
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
                mCode = content.split("=")[1];
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
