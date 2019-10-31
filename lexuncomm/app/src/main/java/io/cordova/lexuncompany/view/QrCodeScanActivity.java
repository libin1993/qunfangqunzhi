package io.cordova.lexuncompany.view;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;

import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import io.cordova.lexuncompany.R;
import io.cordova.lexuncompany.databinding.ActivityQrcodeScanBinding;
import io.cordova.lexuncompany.inter.QrCodeScanInter;

public class QrCodeScanActivity extends AppCompatActivity implements QRCodeView.Delegate {
    private static final int REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY = 666;
    private ActivityQrcodeScanBinding mBinding;

    private static QrCodeScanInter mQrCodeScanInter;  //在AndroidForJSUnits中使用
    private String mCallBack;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_qrcode_scan);

        mBinding.zxingview.setDelegate(this);
        mCallBack = super.getIntent().getStringExtra("callBack");

        setListener();
    }

    /**
     * 在AndroidForJSUnits中动态设置扫描结果监听类
     *
     * @param qrCodeScanInter
     */
    public static void setQrCodeScanInter(QrCodeScanInter qrCodeScanInter) {
        mQrCodeScanInter = qrCodeScanInter;
    }

    private void setListener() {
        mBinding.checkboxOpenLight.setOnCheckedChangeListener((compoundButton, b) -> {
            turnFlashLightOff();
            if (b) {
                turnFlashlightOn();
            }
        });

        mBinding.textBtnRight.setOnClickListener(view -> {
            Intent photoPickerIntent = new BGAPhotoPickerActivity.IntentBuilder(this)
                    .cameraFileDir(null)
                    .maxChooseCount(1)
                    .selectedPhotos(null)
                    .pauseOnScroll(false)
                    .build();
            startActivityForResult(photoPickerIntent, REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY);
        });

        mBinding.imgBtnBackComm.setOnClickListener(v -> finish());
    }

    @Override
    protected void onStart() {
        super.onStart();
        mBinding.zxingview.startCamera(); // 打开后置摄像头开始预览，但是并未开始识别
        mBinding.zxingview.startSpotAndShowRect(); // 显示扫描框，并开始识别
    }

    @Override
    protected void onStop() {
        mBinding.zxingview.stopCamera(); // 关闭摄像头预览，并且隐藏扫描框
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mBinding.zxingview.onDestroy(); // 销毁二维码扫描控件
        super.onDestroy();
    }

    /**
     * 打开闪关灯
     */
    private void turnFlashlightOn() {
        mBinding.checkboxOpenLight.setText("关灯");
        mBinding.zxingview.openFlashlight();
    }

    /**
     * 关闭闪关灯
     */
    private void turnFlashLightOff() {
        mBinding.checkboxOpenLight.setText("开灯");
        mBinding.zxingview.closeFlashlight();
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        vibrate();
        Intent intent = new Intent();
        intent.putExtra("result", result);
        this.setResult(RESULT_OK, intent);
        //如果QrCodeScan不为空，这执行相关回调，同时销毁对象，防止内存堆积
        if (mQrCodeScanInter != null) {
            mQrCodeScanInter.getQrCodeScanResult(mCallBack, result);
            mQrCodeScanInter = null;
        }
        finish();
    }

    @Override
    public void onCameraAmbientBrightnessChanged(boolean isDark) {
        // 这里是通过修改提示文案来展示环境是否过暗的状态，接入方也可以根据 isDark 的值来实现其他交互效果
        if (isDark) {
            mBinding.checkboxOpenLight.setChecked(true);
        }
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mBinding.zxingview.startSpotAndShowRect(); // 显示扫描框，并开始识别

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY) {
            final String picturePath = BGAPhotoPickerActivity.getSelectedPhotos(data).get(0);
            // 本来就用到 QRCodeView 时可直接调 QRCodeView 的方法，走通用的回调
            mBinding.zxingview.decodeQRCode(picturePath);
        }
    }
}