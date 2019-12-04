package com.example.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.lee.circleseekbar.R;
import com.example.utils.AppManager;
import com.example.utils.LogUtils;
import com.example.views.ILoadingView;
import com.example.views.LoadingDialog;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Activity基类
 *
 * @author qisheng.lv
 * @date 2017/4/12
 * <p>
 * showLoading用法：
 * showLoading()：不传参数，没有加载文字。
 * showLoading(String)：显示加载文字。
 * showLoading(0)：显示“加载中...”
 * showLoading(resId)：显示引用文字
 */
public abstract class BaseActivity extends Activity implements ILoadingView, View.OnClickListener {
    protected  String TAG = this.getClass().getSimpleName();

    public TextView mTvTitle;
    public TextView mTvMenu;
    public ImageView mIvMenu;
    public ImageView mIvBack;
    public TextView mTvBack;

    protected LoadingDialog mLoadingDialog;

    public void beforeInitLayout(){};

    public abstract void initLayout(Bundle savedInstanceState);

    public abstract void afterInitView();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        // 设置只能竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setMiuiStatusBarDarkMode(true);
        initPresenter();

        beforeInitLayout();
        initLayout(savedInstanceState);

        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mTvMenu = (TextView) findViewById(R.id.tv_menu);
        mIvMenu = (ImageView) findViewById(R.id.iv_menu);
        mIvBack = (ImageView) findViewById(R.id.iv_back);
        mTvBack = (TextView) findViewById(R.id.tv_back);

        if(mTvMenu != null){
            mTvMenu.setOnClickListener(this);
        }

        if (mIvMenu != null){
            mIvMenu.setOnClickListener(this);
        }

        if (mIvBack != null) {
            mIvBack.setOnClickListener(this);
        }

        afterInitView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_title:

                break;
            case R.id.tv_menu:
                onTvMenuClick();
                break;
            case R.id.iv_menu:
                onClickIvMenu();
                break;
            case R.id.iv_back:
                iv_back();
                break;
            case R.id.tv_back:

                break;
        }
    }

    protected void onTvMenuClick(){

    }

    protected void initPresenter() {

    }

    protected void onClickIvMenu(){

    }

    public boolean setMiuiStatusBarDarkMode(boolean darkmode) {
        Class<? extends Window> clazz = getWindow().getClass();
        try {
            int darkModeFlag = 0;
            Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            darkModeFlag = field.getInt(layoutParams);

            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            extraFlagField.invoke(getWindow(), darkmode ? darkModeFlag : 0, darkModeFlag);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void iv_back() {
        onGoback();
    }

    @Override
    public void onBackPressed() {
        onGoback();
    }

    public void onGoback() {
        finish();
    }

    public void hideBack() {
        try {
            mIvBack.setVisibility(View.INVISIBLE);
        } catch (Exception e) {
            LogUtils.e(TAG, e);
        }
    }

    public void setPageTitle(String title) {
        try {
            mTvTitle.setText(title);
        } catch (Exception e) {
            LogUtils.e(TAG, e);
        }
    }

    public void setPageTitle(int titleResId) {
        String title = this.getResources().getString(titleResId);
        try {
            mTvTitle.setText(title);
        } catch (Exception e) {
            LogUtils.e(TAG, e);
        }
    }

    public void showTvMenu(String menu) {
        try {
            setTvMenuVisible(View.VISIBLE);
            mTvMenu.setText(menu);
        } catch (Exception e) {
            LogUtils.e(TAG, e);
        }
    }

    public void showTvMenu(int resId) {
        try {
            setTvMenuVisible(View.VISIBLE);
            mTvMenu.setText(resId);
        } catch (Exception e) {
            LogUtils.e(TAG, e);
        }
    }

    public void setTvMenuVisible(int visible) {
        mTvMenu.setVisibility(visible);
    }

    public void setTvMenuColor(int resId) {
        mTvMenu.setTextColor(getResources().getColor(resId));
    }


    public void showIvMenu(int ico) {
        try {
            mIvMenu.setVisibility(View.VISIBLE);
            mIvMenu.setImageResource(ico);
        } catch (Exception e) {
            LogUtils.e(TAG, e);
        }
    }


    public void showLoading() {
        showLoading(null);
    }

    public void showLoading(String message) {
        if (null == this.mLoadingDialog) {
            this.mLoadingDialog = new LoadingDialog(this, message);
        }
        mLoadingDialog.show(message, LoadingDialog.DURATION);
    }

    public void showLoading(String message, long duration) {
        if (null == this.mLoadingDialog) {
            this.mLoadingDialog = new LoadingDialog(this, message);
        }
        mLoadingDialog.show(message, duration > 0 ? duration : 10 * 1000);
    }

    public void showLoading(int resId) {
        if (null == this.mLoadingDialog) {
            this.mLoadingDialog = new LoadingDialog(this, resId);
        }
        mLoadingDialog.show(resId, LoadingDialog.DURATION);
    }

    public void showLoading(int resId, long duration) {
        if (null == this.mLoadingDialog) {
            this.mLoadingDialog = new LoadingDialog(this, resId);
        }
        mLoadingDialog.show(resId, duration);
    }

    public void hideLoading() {
        if (null != mLoadingDialog) {
            mLoadingDialog.dismiss();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().finishActivity(this);
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
    }

    @Override
    public void showLoadingDialog(int resId) {
        showLoading(resId);
    }

    @Override
    public void hideLoadingDialog() {
        hideLoading();
    }

    @Override
    public void updateLoadingMessage(String message) {
        showLoading(message);
    }


}
