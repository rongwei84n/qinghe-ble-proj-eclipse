package com.example.activity.status;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lee.circleseekbar.R;
import com.example.activity.BaseActivity;

/**清除故障*/
public class OBDClearErrorActivity extends BaseActivity {
    private TextView mTvTips;
    private Button mBtnClear;
    @Override
    public void initLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_clear_error);
    }

    @Override
    public void afterInitView() {
        mTvTitle.setText("清除故障码");
        mTvTips = (TextView) findViewById(R.id.tv_tips);
        mTvTips.setText("1. 清除故障码，请关闭发动机，钥匙置于ON位置\n2. 在故障未解决之前，建议不要随意清除故障码，应尽快到维修点进行检测。");
        mBtnClear = (Button) findViewById(R.id.btn_clear);
        mBtnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
