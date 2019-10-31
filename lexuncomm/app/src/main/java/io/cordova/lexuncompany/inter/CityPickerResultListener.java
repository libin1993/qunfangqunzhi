package io.cordova.lexuncompany.inter;

import org.json.JSONObject;

/**
 * Created by JasonYao on 2019/1/21.
 */
public interface CityPickerResultListener {
    /**
     * 返回城市选择结果
     *
     * @param callBack
     * @param result
     */
    void getCityPickerResultListener(String callBack, JSONObject result);
}
