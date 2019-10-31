package io.cordova.lexuncompany.inter;

/**
 * Created by JasonYao on 2018/7/27.
 */
public interface ShareResultInter {

    /**
     * 获取分享结果
     *
     * @param callback
     * @param status
     * @param message
     */
    void getShareResult(String callback, String status, String message);
}
