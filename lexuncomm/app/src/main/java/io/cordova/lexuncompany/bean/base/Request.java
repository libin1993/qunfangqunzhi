package io.cordova.lexuncompany.bean.base;

/**
 * Created by JasonYao on 2018/2/27.
 * 请求标识码
 */

public class Request {

    public static final class Permissions {
        public static final int REQUEST_WRITE_EXTERNAL_STORAGE = 0x0003; //请求读写存储卡权限
        public static final int REQUEST_ALL_PERMISSIONS = 0x0005; //请求获取所有权限
    }

    public static final class StartActivityRspCode {
        public static final int PUSH_CARDCONTENT_JUMP = 0x1002; //从推送跳转至卡片页面
    }

    public static final class Broadcast {
        public static final String RELOADURL = "reloadUrl"; //刷新页面
    }
}

