package io.cordova.lexuncompany.bean.base;

import android.os.Environment;

import java.io.File;

import io.cordova.lexuncompany.application.MyApplication;

/**
 * 定义本地数据库和数据表
 * Created by JasonYao on 2018/3/5.
 */

public class Field {

    /**
     * 内部数据库路径
     */
    public static final String DB_PATH = File.separator + "data"
            + Environment.getDataDirectory().getAbsolutePath() + File.separator
            + MyApplication.getInstance().getPackageName() + File.separator + "databases" + File.separator;

    /**
     * 外部数据库路径
     */
    public static final String DB_PATH_SD = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "tdr11" + File.separator;

    /**
     * 缓存卡片的表名和字段名（匿名登录时使用）
     */
    public static final class CacheCard {
        public static final String dbName = "CacheCard"; //表名称
        public static final String card_id = "CardId"; //卡ID
        public static final String card_area = "CardArea"; //卡地址
        public static final String card_introduction = "CardIntroduction"; //卡介绍
        public static final String card_bg = "CardBg"; //卡背景颜色
    }

    /**
     * 三级行政区划的表名和字段名
     */
    public static final class Area {
        public static final String dbName = "Area"; //表名称

        public static final String area_id = "ID"; //地域ID
        public static final String area_parent_id = "ParentId"; //父级ID
        public static final String area_name = "Name"; //地域Name
        public static final String area_shortname = "ShortName"; //地域名字简称
    }

    /**
     * 基础配置信息的表名和字段名
     */
    public static final class DataDictionary {
        public static final String dbName = "DataDictionary"; //表名称

        public static final String data_id = "ID"; //配置ID
        public static final String data_dictype = "DicType"; //配置dicType
        public static final String data_dicName = "DicName"; //配置dicName
        public static final String data_dicValue = "DicValue"; //配置dicValue
        public static final String data_orderBy = "OrderBy";
        public static final String data_status = "Status";
    }
}
