package io.cordova.lexuncompany.units;

import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.cordova.lexuncompany.application.MyApplication;
import io.cordova.lexuncompany.bean.Card;
import io.cordova.lexuncompany.bean.base.App;
import io.cordova.lexuncompany.bean.base.Field;

/**
 * Created by JasonYao on 2018/3/5.
 */

public class BaseDbHelper extends SQLiteOpenHelper {
    private static final String TAG = "BaseDbHelper---";
    private static BaseDbHelper dbHelper = null;  //单例对象

    /**
     * 私有化构造方法
     */
    private BaseDbHelper() {
        super(MyApplication.getInstance().getBaseContext(), App.BaseDB.dbName, null, App.BaseDB.version);
    }

    /**
     * 单例获取
     *
     * @return
     */
    public static BaseDbHelper getInstance() {
        if (dbHelper == null) {
            synchronized (BaseDbHelper.class) {
                if (dbHelper == null) {
                    dbHelper = new BaseDbHelper();
                }
            }
        }

        return dbHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
//        sqLiteDatabase.execSQL(createCacheCard);
//        sqLiteDatabase.execSQL(createAreaTable);
//        sqLiteDatabase.execSQL(createDataDictionary);

        if (FormatUtils.getIntances().isEmpty(ConfigUnits.getInstance().getDBStatus()) ||
                !ConfigUnits.getInstance().getDBStatus().equals("0")) {  //没有拷贝，则进行拷贝
            BaseDbHelper.getInstance().copyDBFileInside();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //数据库更新操作
    }

    /**
     * 创建缓存卡片数据表
     */
    private String createCacheCard = "create table " + Field.CacheCard.dbName + "(" +
            "_id integer primary key autoincrement," +
            Field.CacheCard.card_id + "," +
            Field.CacheCard.card_area + "," +
            Field.CacheCard.card_introduction + "," +
            Field.CacheCard.card_bg + ")";

    /**
     * 创建三级行政区划表
     */
    private String createAreaTable = "create table " + Field.Area.dbName + "(" +
            "_id integer primary key autoincrement," +
            Field.Area.area_id + "," +
            Field.Area.area_parent_id + "," +
            Field.Area.area_name + "," +
            Field.Area.area_shortname + ")";

    /**
     * 创建配置字典数据表
     */
    public String createDataDictionary = "create table " + Field.DataDictionary.dbName + "(" +
            "_id integer primary key autoincrement," +
            Field.DataDictionary.data_id + "," +
            Field.DataDictionary.data_dictype + "," +
            Field.DataDictionary.data_dicName + "," +
            Field.DataDictionary.data_dicValue + "," +
            Field.DataDictionary.data_orderBy + "," +
            Field.DataDictionary.data_status + ")";


    /**
     * 向数据库中插入数据
     *
     * @param sql
     * @param arr
     */
    public void insert(String sql, Object[] arr) {
        dbHelper = new BaseDbHelper();

        //执行插入操作，如果没有数据表会自动创建
        dbHelper.getReadableDatabase().execSQL(sql, arr);

        //关闭数据库
        dbHelper.close();
    }

    /**
     * 从数据库中获取Cursor
     *
     * @param sql
     * @param arr
     * @return
     */
    public Cursor get(String sql, String[] arr) {
        dbHelper = new BaseDbHelper();

        Cursor cursorget = dbHelper.getReadableDatabase().rawQuery(sql, arr);

        return cursorget;
    }

    /**
     * 获取缓存用户缓存的卡片
     *
     * @param cursor
     * @return
     */
    public Card getCacheCard(Cursor cursor) {
        Card card = new Card();
        card.setCardId(cursor.getString(1));
        card.setCardArea(cursor.getString(2));
        card.setCardIntroduction(cursor.getString(3));
        card.setCardBg(cursor.getString(4));

        return card;
    }

    /**
     * 拷贝内部存储空间的数据库到外部
     *
     * @throws FileNotFoundException
     */
    public void copyDBFile() throws FileNotFoundException {
        File toDir = new File(Field.DB_PATH_SD);   //外部存储文件夹
        if (!toDir.exists()) {
            toDir.mkdirs();
        }

        File toDb = new File(Field.DB_PATH_SD + App.BaseDB.dbName); //外部存储数据库
        File fromDir = new File(Field.DB_PATH + App.BaseDB.dbName); //内部存储数据库

        InputStream is;
        OutputStream os;
        is = new FileInputStream(fromDir);
        os = new FileOutputStream(toDb);
        byte[] buffer = new byte[1024];
        int length;
        try {
            /**
             * 拷贝过程
             */
            while ((length = is.read(buffer, 0, buffer.length)) > 0) {
                Log.e(TAG, "执行");
                os.write(buffer, 0, length);
            }

            os.flush();
            os.close();
            is.close();
        } catch (IOException e) {
            Log.e(TAG, "错误:" + e.toString());
            e.printStackTrace();
        }
    }

    /**
     * 将数据库从asset拷贝到data下
     *
     * @return
     */
    public boolean copyDBFileInside() {
        File toDir = new File(Field.DB_PATH);

        if (!toDir.exists()) {
            toDir.mkdir();
        }

        File toDBDir = new File(Field.DB_PATH + App.BaseDB.dbName);

        AssetManager assetManager = MyApplication.getInstance().getAssets();

        InputStream is;
        OutputStream os;

        try {
            is = assetManager.open(App.BaseDB.dbName);
            os = new FileOutputStream(Field.DB_PATH + App.BaseDB.dbName);

            byte[] buffer = new byte[1024];
            int length;

            Log.e(TAG, "拷贝开始");
            /**
             * 拷贝过程
             */
            while ((length = is.read(buffer, 0, buffer.length)) > 0) {
                os.write(buffer, 0, length);
            }

            Log.e(TAG, "拷贝中");

            os.flush();
            os.close();
            is.close();
        } catch (IOException e) {
            Log.e(TAG,"错误信息："+e.toString());
            e.printStackTrace();
            return false;
        }

        ConfigUnits.getInstance().setDBStatus();  //拷贝数据库完成，修改状态
        return true;
    }
}
