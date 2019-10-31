package io.cordova.lexuncompany.units;

import android.net.Uri;

import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.CropOptions;

import java.io.File;

/**
 * Created by JasonYao on 2019/1/4.
 */
public class PhotoUntils {
    private static PhotoUntils mInstance = null;
    static TakePhoto mTakePhone;
    private CompressConfig mCompressConfig = new CompressConfig.Builder().setMaxSize(200 * 1024).create();

    private PhotoUntils() {
        if (mTakePhone == null) {
        }
    }

    public static final PhotoUntils getInstance() {
        if (mInstance == null) {
            synchronized (PhotoUntils.class) {
                if (mInstance == null) {
                    mInstance = new PhotoUntils();
                }
            }
        }

        return mInstance;
    }

    /**
     * 创建图片Uri
     *
     * @return
     */
    public Uri createImageFileUri() {
        File file = new File(io.cordova.lexuncompany.bean.base.Uri.imagePath + "/" + BaseUnits.getInstance().getSerilNumByLength(20) + ".jpg");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        return Uri.fromFile(file);
    }

    public CropOptions getCropOptions() {
        CropOptions.Builder builder = new CropOptions.Builder();
        builder.setAspectX(1).setAspectY(1);
        builder.setWithOwnCrop(false);
        return builder.create();
    }

    public CompressConfig getCompressConfig() {
        return mCompressConfig;
    }
}
