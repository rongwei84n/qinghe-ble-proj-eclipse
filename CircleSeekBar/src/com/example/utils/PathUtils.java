package com.example.utils;

import android.content.Context;
import android.os.Environment;

public class PathUtils {

    public static final String TEST_FILE_NAME="TEST_FILE_NAME.mp4";

    public static String getImagePath(Context context,String fileName)
    {

        if ((Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))) {

            return Environment.getExternalStorageDirectory().getPath() + "/DCIM/bcpphoto/"+fileName;
        }
        else
        {
            return context.getFilesDir().getPath()+"/DCIM/bcpphoto/"+fileName;
        }
    }

    public static String getLogPath(Context context)
    {
        if ((Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))) {
            return Environment.getExternalStorageDirectory().getPath() + "/auts/ble/logs/" ;
        }
        else
        {
            return context.getFilesDir().getPath()+"/auts/ble/logs" ;
        }
    }
//    public static String getVideoPath(Context context,String fileName)
//    {
//        if ((Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))) {
//
//            return Environment.getExternalStorageDirectory().getPath() + "/DCIM/bcvideo/"+fileName;
//        }
//        else
//        {
//            return context.getFilesDir().getPath()+"/DCIM/bcvideo/"+fileName;
//        }
//    }
}
