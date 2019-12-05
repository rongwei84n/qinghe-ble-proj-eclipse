package com.example;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

import com.example.logs.LogConsoleControl;
import com.example.logs.LogFileControl;
import com.example.utils.LogUtils;
import com.example.utils.PathUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BleApplication extends Application {
	private static final String TAG = "BleApplication";

    @Override
    public void onCreate() {
        super.onCreate();

        initLogConfig();
        
        try {
			PackageInfo packageInfo = getApplicationContext()
					.getPackageManager()
					.getPackageInfo(getPackageName(), 0);
			LogUtils.d(TAG, "versionName: " + packageInfo.versionName + " versionCode: " + packageInfo.versionCode);
		} catch (NameNotFoundException e) {
			LogUtils.e(TAG, e);
		}
    }

    private void initLogConfig(){
        LogUtils.addLogControl(new LogConsoleControl());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        String fileName = sdf.format(new Date())+".txt";
        LogUtils.addLogControl(new LogFileControl(PathUtils.getLogPath(this),fileName));
    }
}
