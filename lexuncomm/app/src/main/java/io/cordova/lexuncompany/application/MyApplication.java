package io.cordova.lexuncompany.application;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.facebook.stetho.Stetho;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.yanzhenjie.nohttp.Logger;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.URLConnectionNetworkExecutor;

import cn.jpush.android.api.JPushInterface;
import io.cordova.lexuncompany.bean.base.App;
import io.cordova.lexuncompany.view.CardContentActivity;

/**
 * Created by JasonYao on 2018/2/27.
 */

public class MyApplication extends Application {
    public static MyApplication mInstance = null;

    @Override
    public void onCreate() {
        super.onCreate();

        //初始化NoHttp
        NoHttp.initialize(this, new NoHttp.Config()
                .setReadTimeout(30 * 1000)  //服务器响应超时时间
                .setConnectTimeout(30 * 1000)  //连接超时时间
                .setNetworkExecutor(new URLConnectionNetworkExecutor()) //使用HttpURLConnection做网络层
        );

        Logger.setTag("NoHttpSample");
        Logger.setDebug(true); //开启调试模式

        Stetho.initializeWithDefaults(this);  //初始化Chrome查看Sqlite插件

        //极光推送
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);

        //友盟统计
        UMConfigure.init(this, "5bbf0375b465f5d4170000f8", "测试环境", UMConfigure.DEVICE_TYPE_PHONE, null);
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);

        this.mInstance = this;
    }

    public static MyApplication getInstance() {
        return mInstance;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
