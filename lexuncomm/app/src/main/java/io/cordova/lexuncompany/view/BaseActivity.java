package io.cordova.lexuncompany.view;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import io.cordova.lexuncompany.R;
import io.cordova.lexuncompany.inter.TakePicOnClick;
import io.cordova.lexuncompany.units.LoadingDialog;

/**
 * Created by JasonYao on 2018/3/19.
 */

public class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity---";
    private LoadingDialog.Builder mLoadBuilder;
    private LoadingDialog mDialog;
    public static BaseActivity mInstance;

    //选择照片底部弹出框
    TextView mChoosePhoto;
    TextView mTakePhoto;
    Dialog mDialogTakePic;
    View mTakePicView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();


        mInstance = this;
    }

    private void initView() {
        mLoadBuilder = new LoadingDialog.Builder(this)
                .setMessage("请求中...")
                .setCancelable(true)
                .setCancelOutside(true);
        mDialog = mLoadBuilder.create();

        /**
         * 照片选择弹出
         */
        mDialogTakePic = new Dialog(this, R.style.ActionSheetDialogStyle);
        //填充对话框的布局
        mTakePicView = LayoutInflater.from(this).inflate(R.layout.layout_buttom_popup, null);
        //初始化控件
        mChoosePhoto = mTakePicView.findViewById(R.id.choosePhoto);
        mTakePhoto = mTakePicView.findViewById(R.id.takePhoto);
        //将布局设置给Dialog
        mDialogTakePic.setContentView(mTakePicView);
        //获取当前Activity所在的窗体
        Window dialogWindow = mDialogTakePic.getWindow();
        //设置Dialog从窗体底部弹出
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialogWindow.getDecorView().setPadding(0, 0, 0, 0);
        //获得窗体的属性
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //将属性设置给窗体
        dialogWindow.setAttributes(lp);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    /**
     * 显示照片选择器弹出框
     */
    public void showTakePicDialog(TakePicOnClick mOnClickListener) {
        mChoosePhoto.setOnClickListener(view -> mOnClickListener.choosePhotoOnClick());

        mTakePhoto.setOnClickListener(view -> mOnClickListener.takePictureOnClick());

        runOnUiThread(() -> {
            mDialogTakePic.show();//显示对话框
        });
    }

    /**
     * 消失照片选择器弹出框
     */
    public void missTakePicDialog() {
        if (mDialogTakePic == null) {
            return;
        }

        runOnUiThread(() -> {
            if (mDialogTakePic.isShowing()) {
                mDialogTakePic.dismiss();
            }
        });
    }


    public void back(View view) {
        finish();
    }

}
