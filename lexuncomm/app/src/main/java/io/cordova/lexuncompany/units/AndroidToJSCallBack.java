package io.cordova.lexuncompany.units;

/**
 * Created by JasonYao on 2018/9/3.
 */
public interface AndroidToJSCallBack {
    void callBackResult(String callBack, String value);

    /**
     * 设置标题栏
     *
     * @param btnBackType
     * @param title
     * @param textColor
     * @param bgColor
     * @param isDisplay
     */
    void setTitleBar(String btnBackType, String title, String textColor, String bgColor,String isDisplay);
}
