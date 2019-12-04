package com.example.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * @author kaven 数据库访问类
 *
 */
public class DBHelper extends SQLiteOpenHelper
{

    private static final int DBVERSION = 1;
    private static final String DBNAME =  "auts_ble";// 前面加地址前缀可以生成该地址目录下，并且不会在APP被卸载时一起删除

    public DBHelper(Context context, String name, CursorFactory factory,
                    int version)
    {
        super(context, name, factory, version);
    }

    public DBHelper(Context context, String name, int version)
    {
        this(context, name, null, version);
    }

    public DBHelper(Context context)
    {
        this(context, DBNAME, null, DBVERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase arg0)
    {
        //设备表
        arg0.execSQL( "CREATE TABLE " + TBL_BLE_DEVICES.TABLE_NAME + " (" +
                TBL_BLE_DEVICES._ID + " INTEGER PRIMARY KEY ," +
                TBL_BLE_DEVICES.DEVICE_NAME + " TEXT ," +
                TBL_BLE_DEVICES.DEVICE_ADDRESS + " TEXT ," +
                TBL_BLE_DEVICES.MODULE_ID + " TEXT ," +
                TBL_BLE_DEVICES.CREATE_TIME + " TEXT ," +
                TBL_BLE_DEVICES.CREATE_TIME_STR +  " TEXT, "  +
                TBL_BLE_DEVICES.STATUS + " INTEGER )"  );

    }

    /**
     * 视频文件分段存储的图片路径
     */
    public static abstract class TBL_BLE_DEVICES implements BaseColumns {
        public static final String TABLE_NAME = "tbl_ble_devices";               //表名
        public static final String DEVICE_NAME = "device_name";
        public static final String DEVICE_ADDRESS = "device_address";
        public static final String MODULE_ID = "module_id";
        public static final String CREATE_TIME = "create_time";
        public static final String CREATE_TIME_STR = "create_time_str";
        public static final String STATUS = "status";
    }


    @Override
    public void onUpgrade(SQLiteDatabase arg0, int oldVersion, int newVersion)
    {
        if(oldVersion==1)
        {

            oldVersion=2;

        }
//        if(oldVersion==2)
//        {

//        }

    }



}
