package com.example.activity.status;

import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lee.circleseekbar.R;
import com.example.activity.JdyBaseActivity;
import com.example.model.BleReceiveParsedModel;
import com.example.model.BleSendCommandModel;
import com.example.utils.BleCommandManager;
import com.example.utils.LogUtils;
import com.example.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/** 冻结数据 */
public class OBDFrezenDataActivity extends JdyBaseActivity {
    private ListView mListView;
    private List<DataModel> mDataSource;
    private MyFuctionsAdapter mAdapter;
    
    private List<BleSendCommandModel> mCommandQueue;
    
    private Handler mHandler = new Handler();
    
    @Override
    protected void onBleConnectSuccess() {
		super.onBleConnectSuccess();
		ToastUtil.show(OBDFrezenDataActivity.this, "蓝牙连接成功，正在冻结数据，请稍候...");
		final BleSendCommandModel sendCommandModel = findNextSendCommand();
        if (sendCommandModel != null){
            mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					sendMessage(sendCommandModel);
				}
			}, 2000);
        }
	}
    
    private BleSendCommandModel findNextSendCommand(){
        for (BleSendCommandModel model: mCommandQueue){
            if (model.notSend()){
                return model;
            }
        }
        return null;
    }
    
    private BleSendCommandModel findSendCmdByReceive(String msg){
        for (BleSendCommandModel model: mCommandQueue){
            if (model.getCommand().contains(msg)){
                return model;
            }
        }
        return null;
    }
    
    private void updateDateSource(String title, String value) {
    	for (DataModel model : mDataSource) {
			if (model.title.equals(title)) {
				model.value = value;
				return;
			}
		}
    	//还没有保存这个数据，添加
    	DataModel model = new DataModel(title, value);
    	mDataSource.add(model);
    	mAdapter.notifyDataSetChanged();
    }
    
    @Override
    protected void onMessageReceive(String msg) {
    	super.onMessageReceive(msg);
    	BleReceiveParsedModel receiveParsedModel = new BleReceiveParsedModel(msg);
    	if (BleCommandManager.Sender.COMMAND_FREZEN_DATA.contains(receiveParsedModel.getSendCmd())){
    		LogUtils.d(TAG, "车速读取返回:  " + receiveParsedModel.isResultSuccess());
    		if(receiveParsedModel.isResultSuccess()) {
    			updateDateSource("车辆数据冻结", "成功");
    		}else {
    			updateDateSource("车辆数据冻结", "失败");
    		}
    	}else if (BleCommandManager.Sender.COMMAND_SPEED.contains(receiveParsedModel.getSendCmd())){
        	LogUtils.d(TAG, "车速读取返回");
            updateDateSource("车速", receiveParsedModel.getResultByIndex(0));
        }else if (BleCommandManager.Sender.COMMAND_RAND.contains(receiveParsedModel.getSendCmd())){
        	LogUtils.d(TAG, "发动机转速读取返回");
            updateDateSource("发动机转速", receiveParsedModel.getResultByIndex(0));
        }else if (BleCommandManager.Sender.COMMAND_TEMPTURE.contains(receiveParsedModel.getSendCmd())){
        	LogUtils.d(TAG, "发动机温度读取返回");
            updateDateSource("发动机温度", receiveParsedModel.getResultByIndex(0));
        }else if (BleCommandManager.Sender.COMMAND_BATTARY_V.contains(receiveParsedModel.getSendCmd())){
        	LogUtils.d(TAG, "蓄电池读取返回");
            updateDateSource("蓄电池电压", receiveParsedModel.getResultByIndex(0));
        }else if (BleCommandManager.Sender.COMMAND_XIQI_TEMPTURE.contains(receiveParsedModel.getSendCmd())){
        	LogUtils.d(TAG, "吸气温度读取返回");
            updateDateSource("吸气温度", receiveParsedModel.getResultByIndex(0));
        }else if (BleCommandManager.Sender.COMMAND_JINQIGUAN_PRESS.contains(receiveParsedModel.getSendCmd())){
        	LogUtils.d(TAG, "进气管压力读取返回");
            updateDateSource("进气管压力", receiveParsedModel.getResultByIndex(0));
        }else if (BleCommandManager.Sender.COMMAND_CHEPAI_VID.contains(receiveParsedModel.getSendCmd())){
        	LogUtils.d(TAG, "车牌识别号VID读取返回");
            updateDateSource("车辆识别号VID", receiveParsedModel.getResultByIndex(0));
        }else if (BleCommandManager.Sender.COMMAND_BIAODING_ID.contains(receiveParsedModel.getSendCmd())){
        	LogUtils.d(TAG, "标定识别ID读取返回");
            updateDateSource("标定识别ID", receiveParsedModel.getResultByIndex(0));
        }else if (BleCommandManager.Sender.COMMAND_CVN.contains(receiveParsedModel.getSendCmd())){
        	LogUtils.d(TAG, "校准核查码读取返回");
            updateDateSource("校准核查码(CVN)", receiveParsedModel.getResultByIndex(0));
        }else if(BleCommandManager.Sender.COMMAND_FINISH.contains(receiveParsedModel.getSendCmd())) {
        	LogUtils.d(TAG, "结束指令读取返回");
        	if(mWaitDialog != null) {
        		mWaitDialog.dismiss();
        		mWaitDialog = null;
        	}
        	finish();
        	return;
        }else {
        	LogUtils.d(TAG, "未知指令读取返回");
        }
    	
    	mAdapter.notifyDataSetChanged();
    	
    	BleSendCommandModel presendCmd = findSendCmdByReceive(receiveParsedModel.getSendCmd());
        int delayTime = 0;
        if (presendCmd != null){
            delayTime = presendCmd.getDelayTime();
        }
        final BleSendCommandModel nextSendModel = findNextSendCommand();

        if (nextSendModel != null) {
        	LogUtils.d(TAG,"nextTrySendModel: " + nextSendModel.getCommand());
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    sendMessage(nextSendModel);
                }
            }, delayTime);
        }
    }
    
    
    private void createCommandQueue(){
        mCommandQueue = new ArrayList<BleSendCommandModel>();
        BleSendCommandModel frezenCmd = new BleSendCommandModel(
                BleCommandManager.Sender.COMMAND_FREZEN_DATA,
                100);
        mCommandQueue.add(frezenCmd);
        
        BleSendCommandModel speedCommand = new BleSendCommandModel(
                BleCommandManager.Sender.COMMAND_SPEED,
                1000);
        mCommandQueue.add(speedCommand);
        
        BleSendCommandModel randCommand = new BleSendCommandModel(
                BleCommandManager.Sender.COMMAND_RAND,
                1000);
        mCommandQueue.add(randCommand);
        
        BleSendCommandModel temptureCmd = new BleSendCommandModel(
                BleCommandManager.Sender.COMMAND_TEMPTURE,
                1000);
        mCommandQueue.add(temptureCmd);
        
        BleSendCommandModel battaryVCmd = new BleSendCommandModel(
                BleCommandManager.Sender.COMMAND_BATTARY_V,
                1000);
        mCommandQueue.add(battaryVCmd);
        
        BleSendCommandModel xiqiTempCmd = new BleSendCommandModel(
                BleCommandManager.Sender.COMMAND_XIQI_TEMPTURE,
                1000);
        mCommandQueue.add(xiqiTempCmd);
        
        BleSendCommandModel jiqiguanPressCmd = new BleSendCommandModel(
                BleCommandManager.Sender.COMMAND_JINQIGUAN_PRESS,
                1000);
        mCommandQueue.add(jiqiguanPressCmd);
        
        BleSendCommandModel chepaiVidCmd = new BleSendCommandModel(
                BleCommandManager.Sender.COMMAND_CHEPAI_VID,
                1000);
        mCommandQueue.add(chepaiVidCmd);
        
        BleSendCommandModel biaodingIdCmd = new BleSendCommandModel(
                BleCommandManager.Sender.COMMAND_BIAODING_ID,
                1000);
        mCommandQueue.add(biaodingIdCmd);
        
        BleSendCommandModel cvnCmd = new BleSendCommandModel(
                BleCommandManager.Sender.COMMAND_CVN,
                1000);
        mCommandQueue.add(cvnCmd);
        
        BleSendCommandModel finishCmd = new BleSendCommandModel(
                BleCommandManager.Sender.COMMAND_FINISH,
                0);
        mCommandQueue.add(finishCmd);
    }
    
    @Override
    protected void onResume() {
		super.onResume();
		bindBleService();
	}
    
    @Override
    public void beforeInitLayout() {
		super.beforeInitLayout();
		createCommandQueue();
	}

    @Override
    public void initLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_obd_home);
    }

    @Override
    public void afterInitView() {
    	super.afterInitView();
        initDataSource();
        mTvTitle.setText("冻结数据");
        mListView = (ListView) findViewById(R.id.lv_functionlist);
        mAdapter = new MyFuctionsAdapter();
        mListView.setAdapter(mAdapter);
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

    private void initDataSource(){
        mDataSource = new ArrayList<DataModel>();
    }

    private class DataModel{
        public String title;
        public String value;
        public DataModel(String title, String value){
            this.title = title;
            this.value = value;
        }
    }

    private class MyFuctionsAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (mDataSource == null) {
                return 0;
            }
            return mDataSource.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null){
                convertView = LayoutInflater.from(OBDFrezenDataActivity.this).inflate(
                        R.layout.listitem_common, null);
            }
            TextView tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
            TextView tvValue = (TextView) convertView.findViewById(R.id.tv_right_text);
            tvTitle.setText(mDataSource.get(position).title);
            tvValue.setText(mDataSource.get(position).value);
            
            ImageView imgRight = (ImageView) convertView.findViewById(R.id.img_right_icon);
            imgRight.setVisibility(View.GONE);
            return convertView;
        }
    }
}
