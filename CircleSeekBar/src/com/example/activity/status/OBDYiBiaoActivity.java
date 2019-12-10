package com.example.activity.status;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.TextView;

import com.lee.circleseekbar.R;
import com.example.activity.JdyBaseActivity;
import com.example.model.BleReceiveParsedModel;
import com.example.model.BleSendCommandModel;
import com.example.utils.BleCommandManager;
import com.example.utils.LogUtils;
import com.example.utils.ToastUtil;
import com.example.views.DashboardView;
import com.example.views.HighlightCR;

import java.util.ArrayList;
import java.util.List;

public class OBDYiBiaoActivity extends JdyBaseActivity {
    private DashboardView mDashboardViewChesu;
    private DashboardView mDhZhuansu;
    private List<BleSendCommandModel> mCommandQueue;
    private List<BleSendCommandModel> mRepeatCommandList = new ArrayList<BleSendCommandModel>();
    
    private TextView mTvRandSpeed; //转速
    private TextView mTvTempture;//温度
    private TextView mTvDianya; //电压
    private TextView mTvLh;//LH  //油耗
    
    public void beforeInitLayout() {
		super.beforeInitLayout();
		createCommandQueue();
	}
    
    @Override
    protected void onMessageReceive(String msg) {
    	super.onMessageReceive(msg);
        BleReceiveParsedModel receiveParsedModel = new BleReceiveParsedModel(msg);
        if (BleCommandManager.Sender.COMMAND_REAL_DATA.contains(receiveParsedModel.getSendCmd())){
        	LogUtils.d(TAG, "仪表盘实时数据返回");
        }else if (BleCommandManager.Sender.COMMAND_SPEED.contains(receiveParsedModel.getSendCmd())){
        	LogUtils.d(TAG, "车速读取返回" + receiveParsedModel.getResultByIndex(0));
        	mTvLh.setText("车速：" + receiveParsedModel.getResultByIndex(0));
        	String pureData = receiveParsedModel.getResultByIndex(0);
        	if (!TextUtils.isEmpty(pureData)) {
        		pureData = pureData.replace("km/h", "");
        		try {
					int pureDataInt = Integer.parseInt(pureData);
					mDashboardViewChesu.mButtonCenterStr = receiveParsedModel.getResultByIndex(0);
					if (pureDataInt < 2000) {
						mDashboardViewChesu.setMaxValue(2000);
					}else {
						mDashboardViewChesu.setMaxValue(pureDataInt + 1500);
					}
					mDashboardViewChesu.setRealTimeValue(pureDataInt);
				} catch (Exception e) {
				}
			}
        }else if (BleCommandManager.Sender.COMMAND_RAND.contains(receiveParsedModel.getSendCmd())){
        	LogUtils.d(TAG, "发动机转速读取返回" );
        	mTvRandSpeed.setText("发动机转速：" + receiveParsedModel.getResultByIndex(0));
        	String pureData = receiveParsedModel.getResultByIndex(0);
        	if (!TextUtils.isEmpty(pureData)) {
        		pureData = pureData.replace("rpmi", "").replace("rpmin", "");
        		try {
					int pureDataInt = Integer.parseInt(pureData);
					mDhZhuansu.mButtonCenterStr = receiveParsedModel.getResultByIndex(0);
					if (pureDataInt < 2000) {
						mDhZhuansu.setMaxValue(2000);
					}else {
						mDhZhuansu.setMaxValue(pureDataInt + 1500);
					}
					mDhZhuansu.setRealTimeValue(pureDataInt);
				} catch (Exception e) {
				}
			}
        }else if (BleCommandManager.Sender.COMMAND_TEMPTURE.contains(receiveParsedModel.getSendCmd())){
        	LogUtils.d(TAG, "发动机温度读取返回");
        	String temp = receiveParsedModel.getResultByIndex(0);
        	if (temp != null) {
        		temp = temp.replace("C", "").replace("c", "").concat("℃");
			}
        	mTvTempture.setText("发动机温度：" + temp);
        }else if (BleCommandManager.Sender.COMMAND_BATTARY_V.contains(receiveParsedModel.getSendCmd())){
        	LogUtils.d(TAG, "蓄电池读取返回");
        	mTvDianya.setText("电压：" + receiveParsedModel.getResultByIndex(0));
        }else if(BleCommandManager.Sender.COMMAND_FINISH.contains(receiveParsedModel.getSendCmd())) {
        	LogUtils.d(TAG, "结束指令读取返回");
        	if(mWaitDialog != null) {
        		mWaitDialog.dismiss();
        		mWaitDialog = null;
        	}
        	mCommandQueue.clear();
        	mRepeatCommandList.clear();
        	finish();
        	return;
        }else {
        	LogUtils.d(TAG, "未知指令读取返回");
        	return;
        }

        BleSendCommandModel presendCmd = findSendCmdByReceive(receiveParsedModel.getSendCmd());
        int delayTime = 0;
        if (presendCmd != null){
            delayTime = presendCmd.getDelayTime();
        }
        BleSendCommandModel nextSendModel = findNextSendCommand();
        if (nextSendModel == null){
            nextSendModel = findNextRepeatCommand();
            if (nextSendModel != null) {
            	delayTime = nextSendModel.getDelayTime();
			}
        }
        final BleSendCommandModel nextTrySendModel = nextSendModel;
        if (nextTrySendModel != null) {
        	LogUtils.d(TAG,"nextTrySendModel: " + nextTrySendModel.getCommand() + " delay: " + delayTime);
        	mMainHandler.removeMessages(MESSAGE_SEND_CMD);
        	Message handleMsg = mMainHandler.obtainMessage(MESSAGE_SEND_CMD);
        	handleMsg.obj = nextTrySendModel;
        	mMainHandler.sendMessageDelayed(handleMsg, delayTime);
        }
    }
    
    @Override
    protected void onBleConnectSuccess() {
        super.onBleConnectSuccess();
        ToastUtil.show(OBDYiBiaoActivity.this, "蓝牙连接成功，正在读取数信息，请稍候...");
		final BleSendCommandModel sendCommandModel = findNextSendCommand();
        if (sendCommandModel != null){
        	mMainHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					sendMessage(sendCommandModel);
				}
			}, 2000);
        }
    }

    private BleSendCommandModel findSendCmdByReceive(String msg){
        for (BleSendCommandModel model: mCommandQueue){
            if (model.getCommand().contains(msg)){
                return model;
            }
        }
        return null;
    }
    
    private BleSendCommandModel findNextSendCommand(){
        for (BleSendCommandModel model: mCommandQueue){
            if (model.notSend()){
                return model;
            }
        }
        return null;
    }
    
    private BleSendCommandModel findNextRepeatCommand(){
        if (mRepeatCommandList == null || mRepeatCommandList.isEmpty()){
            return null;
        }
        for (BleSendCommandModel model: mRepeatCommandList){
            if (model.notSend()){
                return model;
            }
        }

        //如果已经遍历完了所有的待发队列，直接把待发队列设置成初始化状态，从头遍历.
        for (BleSendCommandModel model: mRepeatCommandList){
            model.setStatus(BleSendCommandModel.SendCmdStatus.STATUS_INIT);
        }
        for (BleSendCommandModel model: mRepeatCommandList){
            if (model.notSend()){
                return model;
            }
        }

        return null;
    }

    @Override
    public void initLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_yibiao);
    }

    @Override
    public void afterInitView() {
    	super.afterInitView();
        mTvTitle.setText("仪表");
        mDashboardViewChesu = (DashboardView) findViewById(R.id.dbv_chesu);
        mDhZhuansu = (DashboardView) findViewById(R.id.dbv_zhuansu);
        mDhZhuansu.setHeaderTitle("转速");
        mDhZhuansu.setAnimEnable(true);
        
        mTvRandSpeed = (TextView) findViewById(R.id.tv_rs);
        mTvTempture = (TextView) findViewById(R.id.tv_temperature);
        mTvDianya = (TextView) findViewById(R.id.tv_v);
        mTvLh = (TextView) findViewById(R.id.tv_lh);

        List<HighlightCR> highlight2 = new ArrayList<HighlightCR>();
        highlight2.add(new HighlightCR(170, 140, Color.BLUE));
        highlight2.add(new HighlightCR(310, 60, Color.GREEN));
        mDashboardViewChesu.setStripeHighlightColorAndRange(highlight2);
        mDashboardViewChesu.mButtonCenterStr = "86%";
        bindBleService();
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (keyCode == KeyEvent.KEYCODE_BACK) {
    		LogUtils.d(TAG, "onKeyDown KEYCODE_BACK");
    		onExit();
    		return true;
		}
    	return super.onKeyDown(keyCode, event);
    }
    
    @Override
    public void onGoback() {
    	LogUtils.d(TAG, "onGoback");
    	onExit();
    }
    
    private void createCommandQueue(){
        mCommandQueue = new ArrayList<BleSendCommandModel>();
        BleSendCommandModel startRealData = new BleSendCommandModel(
                BleCommandManager.Sender.COMMAND_REAL_DATA,
                1000);
        mCommandQueue.add(startRealData);
        
        BleSendCommandModel speedCommand = new BleSendCommandModel(
                BleCommandManager.Sender.COMMAND_SPEED,
                1000);
        mCommandQueue.add(speedCommand);
        BleSendCommandModel rpfirst = new BleSendCommandModel(speedCommand);
        rpfirst.setDelayTime(repeatDelayTime);
        mRepeatCommandList.add(rpfirst);
        
        BleSendCommandModel randCommand = new BleSendCommandModel(
                BleCommandManager.Sender.COMMAND_RAND,
                1000);
        mCommandQueue.add(randCommand);
        mRepeatCommandList.add(new BleSendCommandModel(randCommand));
        
        BleSendCommandModel temptureCmd = new BleSendCommandModel(
                BleCommandManager.Sender.COMMAND_TEMPTURE,
                1000);
        mCommandQueue.add(temptureCmd);
        mRepeatCommandList.add(new BleSendCommandModel(temptureCmd));
        
        BleSendCommandModel battaryVCmd = new BleSendCommandModel(
                BleCommandManager.Sender.COMMAND_BATTARY_V,
                1000);
        mCommandQueue.add(battaryVCmd);
        mRepeatCommandList.add(new BleSendCommandModel(battaryVCmd));
    }
}
