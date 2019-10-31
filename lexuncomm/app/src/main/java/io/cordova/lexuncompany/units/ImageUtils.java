package io.cordova.lexuncompany.units;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by JasonYao on 2017/10/25.
 */

public class ImageUtils {
    private static final String TAG = "ImageUtils---";

    public static ImageUtils mImageUtils = null;

    private ImageUtils() {
    }

    /**
     * 获取单例对象
     * @return
     */
    public static ImageUtils getInstance() {
        if (mImageUtils == null) {
            synchronized (ImageUtils.class) {
                if (mImageUtils == null) {
                    mImageUtils = new ImageUtils();
                }
            }
        }

        return mImageUtils;
    }

    /**
     * 根据图片路径，将图片转为二进制
     *
     * @param path
     * @return
     */
    public byte[] image2byte(String path) {
        byte[] data = null;
        FileInputStream input = null;
        try {
            input = new FileInputStream(new File(path));
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int numBytesRead = 0;
            while ((numBytesRead = input.read(buf)) != -1) {
                output.write(buf, 0, numBytesRead);
            }
            data = output.toByteArray();
            output.close();
            input.close();
        } catch (FileNotFoundException ex1) {
            Log.e(TAG, "FileNotFoundException:" + ex1.toString());
            ex1.printStackTrace();
        } catch (IOException ex1) {
            Log.e(TAG, "IOException:" + ex1.toString());
            ex1.printStackTrace();
        }

        return data;
    }
}
