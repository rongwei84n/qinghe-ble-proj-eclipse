package com.example.activity.status;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
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

/*车辆信息*/
public class OBDCarInfoActivity extends JdyBaseActivity {
    private ListView mListView;
    private List<DataModel> mDataSource;
    private MyFuctionsAdapter mAdapter;
    
    private List<BleSendCommandModel> mCommandQueue;
    
    private Handler mHandler = new Handler();
    
    @Override
    protected void onBleConnectSuccess() {
		super.onBleConnectSuccess();
		ToastUtil.show(OBDCarInfoActivity.this, "蓝牙连接成功，正在读取车辆信息，请稍候...");
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
    	if (BleCommandManager.Sender.COMMAND_READ_CARINFO.contains(receiveParsedModel.getSendCmd())){
    		if(receiveParsedModel.isResultSuccess()) {
//    			updateDateSource("车辆信息读取", "成功");
    		}else {
//    			updateDateSource("车辆信息读取", "失败");
    			ToastUtil.show(OBDCarInfoActivity.this, "读取车辆信息失败");
    		}
    	}else if(BleCommandManager.Sender.COMMAND_CAR_VID.contains(receiveParsedModel.getSendCmd())) {
    		String result = receiveParsedModel.getResultByIndex(0);
    		if (!TextUtils.isEmpty(result)) {
				try {
					//尝试转化成16进制
					Integer resultInt = Integer.parseInt(result);
					result = Integer.toHexString(resultInt);
				} catch (Exception e) {
				}
			}
    		updateDateSource("车辆识别号", result);
    	}else if(BleCommandManager.Sender.COMMAND_STANDARD_ID.contains(receiveParsedModel.getSendCmd())) {
			updateDateSource("标定识别号", receiveParsedModel.getResultByIndex(0));
    	}else if(BleCommandManager.Sender.COMMAND_CVN.contains(receiveParsedModel.getSendCmd())) {
    		String result = receiveParsedModel.getResultByIndex(0);
//    		if (!TextUtils.isEmpty(result)) {
//				try {
//					//尝试转化成16进制
//					Integer resultInt = Integer.parseInt(result);
//					result = Integer.toBinaryString(resultInt);
//				} catch (Exception e) {
//				}
//			}
			updateDateSource("校准核查码(CVN)", result);
    	}
    	
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
        BleSendCommandModel readCarInfoCmd = new BleSendCommandModel(
                BleCommandManager.Sender.COMMAND_READ_CARINFO,
                4000);
        mCommandQueue.add(readCarInfoCmd);
        
        BleSendCommandModel carVidCmd = new BleSendCommandModel(
                BleCommandManager.Sender.COMMAND_CAR_VID,
                500);
        mCommandQueue.add(carVidCmd);
        
        BleSendCommandModel standardIdCmd = new BleSendCommandModel(
                BleCommandManager.Sender.COMMAND_STANDARD_ID,
                1000);
        mCommandQueue.add(standardIdCmd);
        
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
        mTvTitle.setText("车辆识别");
        mListView = (ListView) findViewById(R.id.lv_functionlist);
        mAdapter = new MyFuctionsAdapter();
        mListView.setAdapter(mAdapter);
    }
    
    @Override
    protected void onResume() {
		super.onResume();
		bindBleService();
	}
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (keyCode == KeyEvent.KEYCODE_BACK) {
    		BleSendCommandModel sendCommandModel = findNextSendCommand();
        	if(sendCommandModel != null) {
        		sendMessage(BleCommandManager.Sender.COMMAND_FINISH);
        	}
		}
    	return super.onKeyDown(keyCode, event);
    }
    
    @Override
    public void onGoback() {
    	BleSendCommandModel sendCommandModel = findNextSendCommand();
    	if(sendCommandModel != null) {
    		sendMessage(BleCommandManager.Sender.COMMAND_FINISH);
    	}
		super.onGoback();
	}

    private void initDataSource(){
        mDataSource = new ArrayList<DataModel>();
//        mDataSource.add(new DataModel("车辆识别号", "参数值"));
//        mDataSource.add(new DataModel("标定识别号", "参数值"));
//        mDataSource.add(new DataModel("参数描述", "参数值"));
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
                convertView = LayoutInflater.from(OBDCarInfoActivity.this).inflate(
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
