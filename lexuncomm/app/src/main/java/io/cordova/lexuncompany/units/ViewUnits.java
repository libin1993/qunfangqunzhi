package io.cordova.lexuncompany.units;

import android.content.Context;
import android.widget.Toast;


import io.cordova.lexuncompany.application.MyApplication;

/**
 * 页面工具类
 * Created by JasonYao on 2018/3/1.
 */

public class ViewUnits {
    public static ViewUnits mIntances = null;

    //缓存等待框
    private LoadingDialog.Builder mLoadBuilder;
    private static LoadingDialog mDialog;

    private ViewUnits() {
    }

    public static ViewUnits getInstance() {
        if (mIntances == null) {
            synchronized (ViewUnits.class) {
                if (mIntances == null) {
                    mIntances = new ViewUnits();
                }
            }
        }

        return mIntances;
    }

    /**
     * 显示弹吐司
     *
     * @param message
     */
    public void showToast(String message) {
        Toast.makeText(MyApplication.getInstance().getBaseContext(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 显示等待框
     */
    public void showLoading(Context context, String message) {
        mLoadBuilder = new LoadingDialog.Builder(context)
                .setMessage(message)
                .setCancelable(true)
                .setCancelOutside(true);
        mDialog = mLoadBuilder.create();
        mDialog.show();
    }

    /**
     * 隐藏等待框
     */
    public void missLoading() {
        if (mDialog == null) {
            return;
        }

        if (mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }
}
