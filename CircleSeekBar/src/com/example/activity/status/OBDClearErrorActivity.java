package com.example.activity.status;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lee.circleseekbar.R;

import com.example.activity.JdyBaseActivity;
import com.example.model.BleReceiveParsedModel;
import com.example.utils.BleCommandManager;
import com.example.utils.LogUtils;
import com.example.utils.ToastUtil;

/**清除故障*/
public class OBDClearErrorActivity extends JdyBaseActivity {
    private TextView mTvTips;
    private Button mBtnClear;
    private TextView mTvClearResult;
    
    @Override
    protected void onMessageReceive(String msg) {
		super.onMessageReceive(msg);
		BleReceiveParsedModel receiveParsedModel = new BleReceiveParsedModel(msg);
		if (BleCommandManager.Sender.COMMAND_CLEAR_ERROR.contains(receiveParsedModel.getSendCmd())){
	    	if (receiveParsedModel.isResultSuccess()) {
	    		LogUtils.d(TAG, "清除故障码成功");
				ToastUtil.show(OBDClearErrorActivity.this, "清除故障码成功");
				mTvClearResult.setText("清除故障码成功");
			}else {
				LogUtils.d(TAG, "清除故障码失败");
				ToastUtil.show(OBDClearErrorActivity.this, "清除故障码失败");
				mTvClearResult.setText("清除故障码失败");
			}
	    	
	    	LogUtils.d(TAG, "清除故障命令返回后，下发停止指令");
	    	sendMessage(BleCommandManager.Sender.COMMAND_FINISH);
		}
	}
    
    @Override
    public void initLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_clear_error);
    }

    @Override
    public void afterInitView() {
    	super.afterInitView();
        mTvTitle.setText("清除故障码");
        mTvTips = (TextView) findViewById(R.id.tv_tips);
        mTvTips.setText("1. 清除故障码，请关闭发动机，钥匙置于ON位置\n2. 在故障未解决之前，建议不要随意清除故障码，应尽快到维修点进行检测。");
        mBtnClear = (Button) findViewById(R.id.btn_clear);
        mBtnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	LogUtils.d(TAG, "点击清除故障按钮");
            	sendMessage(BleCommandManager.Sender.COMMAND_CLEAR_ERROR);
            }
        });
        mTvClearResult = (TextView) findViewById(R.id.tv_result);
        
        bindBleService();
    }
}
