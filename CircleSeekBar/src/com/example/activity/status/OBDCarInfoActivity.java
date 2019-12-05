package com.example.activity.status;

import android.os.Bundle;
import android.os.Handler;
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
		BleSendCommandModel sendCommandModel = findNextSendCommand();
        if (sendCommandModel != null){
            sendMessage(sendCommandModel);
        }
	}
    
    @Override
    protected void onMessageReceive(String msg) {
    	super.onMessageReceive(msg);
    	BleReceiveParsedModel receiveParsedModel = new BleReceiveParsedModel(msg);
    	if (BleCommandManager.Sender.COMMAND_READ_CARINFO.contains(receiveParsedModel.getSendCmd())){
    		if(receiveParsedModel.isResultSuccess()) {
    			DataModel dataModel = new DataModel("车辆信息读取", "成功");
    			mDataSource.add(dataModel);
    		}else {
    			DataModel dataModel = new DataModel("车辆信息读取", "失败");
    			mDataSource.add(dataModel);
    			ToastUtil.show(OBDCarInfoActivity.this, "读取车辆信息失败");
    		}
    	}else if(BleCommandManager.Sender.COMMAND_CAR_VID.contains(receiveParsedModel.getSendCmd())) {
    		DataModel dataModel = new DataModel("车辆识别号", receiveParsedModel.getResultByIndex(0));
			mDataSource.add(dataModel);
    	}else if(BleCommandManager.Sender.COMMAND_STANDARD_ID.contains(receiveParsedModel.getSendCmd())) {
    		DataModel dataModel = new DataModel("标定识别号", receiveParsedModel.getResultByIndex(0));
			mDataSource.add(dataModel);
    	}
    	
    	mAdapter.notifyDataSetChanged();
    	
    	BleSendCommandModel presendCmd = findSendCmdByReceive(receiveParsedModel.getSendCmd());
        int delayTime = 0;
        if (presendCmd != null){
            delayTime = presendCmd.getDelayTime();
        }
        final BleSendCommandModel nextSendModel = findNextSendCommand();
        LogUtils.d(TAG,"nextTrySendModel: " + nextSendModel.getCommand());

        if (nextSendModel != null) {
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
                0);
        mCommandQueue.add(readCarInfoCmd);
        
        BleSendCommandModel carVidCmd = new BleSendCommandModel(
                BleCommandManager.Sender.COMMAND_CAR_VID,
                0);
        mCommandQueue.add(carVidCmd);
        
        BleSendCommandModel standardIdCmd = new BleSendCommandModel(
                BleCommandManager.Sender.COMMAND_STANDARD_ID,
                0);
        mCommandQueue.add(standardIdCmd);
        
        
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
        initDataSource();
        mTvTitle.setText("车辆识别");
        mListView = (ListView) findViewById(R.id.lv_functionlist);
        mAdapter = new MyFuctionsAdapter();
        mListView.setAdapter(mAdapter);
        bindBleService();
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
