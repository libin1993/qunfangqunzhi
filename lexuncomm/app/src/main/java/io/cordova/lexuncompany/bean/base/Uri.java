package io.cordova.lexuncompany.bean.base;

import android.os.Environment;

/**
 * Created by JasonYao on 2018/2/26.
 */

public class Uri {
    //APP文件路径
    public static final String tdrPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/lexuncompany";
    //APP图片缓存路径
    public static final String imagePath = tdrPath + "/imgCache";
}
