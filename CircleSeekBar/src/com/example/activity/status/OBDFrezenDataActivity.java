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
		BleSendCommandModel sendCommandModel = findNextSendCommand();
        if (sendCommandModel != null){
            sendMessage(sendCommandModel);
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
    
    @Override
    protected void onMessageReceive(String msg) {
    	super.onMessageReceive(msg);
    	BleReceiveParsedModel receiveParsedModel = new BleReceiveParsedModel(msg);
    	if (BleCommandManager.Sender.COMMAND_FREZEN_DATA.contains(receiveParsedModel.getSendCmd())){
    		if(receiveParsedModel.isResultSuccess()) {
    			DataModel dataModel = new DataModel("车辆数据冻结", "成功");
    			mDataSource.add(dataModel);
    		}else {
    			DataModel dataModel = new DataModel("车辆数据冻结", "失败");
    			mDataSource.add(dataModel);
    		}
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
        BleSendCommandModel frezenCmd = new BleSendCommandModel(
                BleCommandManager.Sender.COMMAND_FREZEN_DATA,
                0);
        mCommandQueue.add(frezenCmd);
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
        initDataSource();
        mTvTitle.setText("冻结数据");
        mListView = (ListView) findViewById(R.id.lv_functionlist);
        mAdapter = new MyFuctionsAdapter();
        mListView.setAdapter(mAdapter);
    }

    private void initDataSource(){
        mDataSource = new ArrayList<DataModel>();
        mDataSource.add(new DataModel("参数描述", "参数值"));
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
