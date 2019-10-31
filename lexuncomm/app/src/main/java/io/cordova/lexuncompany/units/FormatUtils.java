package io.cordova.lexuncompany.units;

/**
 * Created by JasonYao on 2018/3/21.
 */

public class FormatUtils {
    //定义单例对象
    public static FormatUtils mIntances = null;
    public static StringBuffer sb = new StringBuffer();

    public final static double jzA = 6378245.0;
    public final static double jzEE = 0.00669342162296594323;
    public final static double pi = 3.14159265358979324;

    /**
     * 私有化构造方法
     */
    private FormatUtils() {
    }

    /**
     * 获取单例对象
     *
     * @return
     */
    public static final FormatUtils getIntances() {
        if (mIntances == null) {
            synchronized (FormatUtils.class) {
                if (mIntances == null) {
                    mIntances = new FormatUtils();
                }
            }
        }

        return mIntances;
    }

    /**
     * 判断文本是否为空
     *
     * @param content
     * @return true：为空 false：不为空
     */
    public boolean isEmpty(String content) {
        if (content == null) {
            return true;
        }

        if (content.trim() == null || content.trim().equals("")) {
            return true;
        }

        return false;
    }

    /**
     * 3位16进制颜色值转为6位
     *
     * @param color
     * @return
     */
    public String colorTo6Color(String color) {
        if (color.length() == 7) return color;
        if (color.length() == 4) {
            return color.substring(0, 2) + color.substring(1, 2) + color.substring(2, 3) + color.substring(2, 3) + color.substring(3, 4) + color.substring(3, 4);
        }

        return color;
    }
}
