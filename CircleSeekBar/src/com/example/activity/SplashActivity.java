package com.example.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.lee.circleseekbar.R;
import com.example.db.SqliteDAL;

public class SplashActivity extends BaseActivity {
    private static final long SPLASH_TIME = 2000;
    Handler mHandler;

    Runnable mR = new Runnable() {
        @Override
        public void run() {
            gotoNextActivity(null);
        }
    };

    @Override
    public void initLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_splash);
    }

    @Override
    public void afterInitView() {
        SqliteDAL.getInstance(this); //生成一个SQLiteDatabase对象，此处调用主要是为了生成创建的表。
        mHandler = new Handler();
        mHandler.postDelayed(mR, SPLASH_TIME);
    }

    private void gotoNextActivity(String msg) {
        Intent intent = new Intent(this, PageHomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mR != null) {
            mHandler.removeCallbacks(mR);
        }
    }

}
