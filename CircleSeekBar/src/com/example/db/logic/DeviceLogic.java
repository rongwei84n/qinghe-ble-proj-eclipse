package com.example.db.logic;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.db.DBHelper;
import com.example.db.SqliteDAL;
import com.example.model.BleDeviceModel;
import com.example.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

public class DeviceLogic {
    private static final String TAG = "TAG";

    public static List<BleDeviceModel> getAllBleDevices(Context context){
        SQLiteDatabase db = null;
        Cursor cur = null;
        List<BleDeviceModel> result = new ArrayList<BleDeviceModel>();
        try{
            db = SqliteDAL.getInstance(context).mDbHelper.getReadableDatabase();
            cur = db.query(DBHelper.TBL_BLE_DEVICES.TABLE_NAME,null,
                    null, null, null, null, null);
            while (cur.moveToNext()){
                BleDeviceModel model = new BleDeviceModel();
                model.setDeviceName(cur.getString(cur.getColumnIndex(DBHelper.TBL_BLE_DEVICES.DEVICE_NAME)));
                model.setDeviceAddress(cur.getString(cur.getColumnIndex(DBHelper.TBL_BLE_DEVICES.DEVICE_ADDRESS)));
                model.setModuleID(cur.getString(cur.getColumnIndex(DBHelper.TBL_BLE_DEVICES.MODULE_ID)));
                model.setCreateTime(cur.getString(cur.getColumnIndex(DBHelper.TBL_BLE_DEVICES.CREATE_TIME)));
                model.setCreateTimeStr(cur.getString(cur.getColumnIndex(DBHelper.TBL_BLE_DEVICES.CREATE_TIME_STR)));
                model.setStatus(cur.getInt(cur.getColumnIndex(DBHelper.TBL_BLE_DEVICES.STATUS)));
                result.add(model);
            }
        }catch (Exception e){
            LogUtils.e(TAG, e);
        }finally{
            if (cur != null){
                cur.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return result;
    }

    public static void addDevice(Context context, BleDeviceModel model){
        SQLiteDatabase db = null;
        try{
            ContentValues cv = new ContentValues();
            cv.put(DBHelper.TBL_BLE_DEVICES.DEVICE_NAME, model.getDeviceName());
            cv.put(DBHelper.TBL_BLE_DEVICES.DEVICE_ADDRESS, model.getDeviceAddress());
            cv.put(DBHelper.TBL_BLE_DEVICES.CREATE_TIME, model.getCreateTime());
            cv.put(DBHelper.TBL_BLE_DEVICES.CREATE_TIME_STR, model.getCreateTimeStr());
            cv.put(DBHelper.TBL_BLE_DEVICES.STATUS, model.getStatus());


            db = SqliteDAL.getInstance(context).mDbHelper.getWritableDatabase();
            db.insert(DBHelper.TBL_BLE_DEVICES.TABLE_NAME, "", cv);
        }catch (Exception e){
            LogUtils.e(TAG, e);
        }finally{
            if (db != null) {
                db.close();
            }
        }
    }
}
