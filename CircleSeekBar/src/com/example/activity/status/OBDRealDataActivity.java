package com.example.activity.status;

import android.os.Bundle;
import android.os.Message;
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

/** 实时数据 */
public class OBDRealDataActivity extends JdyBaseActivity {
    private ListView mListView;
    private List<DataModel> mDataSource;
    private MyFuctionsAdapter mAdapter;
    private List<BleSendCommandModel> mCommandQueue;
    private List<BleSendCommandModel> mRepeatCommandList = new ArrayList<BleSendCommandModel>();

    
    @Override
    protected void onBleConnectSuccess() {
        super.onBleConnectSuccess();
        ToastUtil.show(OBDRealDataActivity.this, "蓝牙连接成功，正在读取实时数据信息，请稍候...");
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
        if (BleCommandManager.Sender.COMMAND_REAL_DATA.contains(receiveParsedModel.getSendCmd())){
        	LogUtils.d(TAG, "实时数据读取返回");
//        	DataModel model = new DataModel("实时数据读取", receiveParsedModel.isResultSuccess()?"成功":"失败");
//        	mDataSource.add(model);
//        	mAdapter.notifyDataSetChanged();
        }else if (BleCommandManager.Sender.COMMAND_SPEED.contains(receiveParsedModel.getSendCmd())){
        	LogUtils.d(TAG, "车速读取返回");
            updateDateSource("车速", receiveParsedModel.getResultByIndex(0));
        }else if (BleCommandManager.Sender.COMMAND_RAND.contains(receiveParsedModel.getSendCmd())){
        	LogUtils.d(TAG, "发动机转速读取返回");
            updateDateSource("发动机转速", receiveParsedModel.getResultByIndex(0));
        }else if (BleCommandManager.Sender.COMMAND_TEMPTURE.contains(receiveParsedModel.getSendCmd())){
        	LogUtils.d(TAG, "发动机温度读取返回");
        	String temp = receiveParsedModel.getResultByIndex(0);
        	if (temp != null) {
        		temp = temp.replace("C", "").replace("c", "").concat("℃");
			}
            updateDateSource("发动机温度", temp);
        }else if (BleCommandManager.Sender.COMMAND_BATTARY_V.contains(receiveParsedModel.getSendCmd())){
        	LogUtils.d(TAG, "蓄电池读取返回");
            updateDateSource("蓄电池电压", receiveParsedModel.getResultByIndex(0));
        }else if (BleCommandManager.Sender.COMMAND_XIQI_TEMPTURE.contains(receiveParsedModel.getSendCmd())){
        	LogUtils.d(TAG, "吸气温度读取返回");
        	String temp = receiveParsedModel.getResultByIndex(0);
        	if (temp != null) {
        		temp = temp.replace("C", "").replace("c", "").concat("℃");
			}
            updateDateSource("吸气温度", temp);
        }else if (BleCommandManager.Sender.COMMAND_JINQIGUAN_PRESS.contains(receiveParsedModel.getSendCmd())){
        	LogUtils.d(TAG, "进气管压力读取返回");
            updateDateSource("进气管压力", receiveParsedModel.getResultByIndex(0));
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
        	LogUtils.d(TAG,"nextTrySendModel: " + nextTrySendModel.getCommand());
        	mMainHandler.removeMessages(MESSAGE_SEND_CMD);
        	Message handleMsg = mMainHandler.obtainMessage(MESSAGE_SEND_CMD);
        	handleMsg.obj = nextTrySendModel;
        	mMainHandler.sendMessageDelayed(handleMsg, delayTime);
        }
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
        mTvTitle.setText("实时数据");
        mListView = (ListView) findViewById(R.id.lv_functionlist);
        mAdapter = new MyFuctionsAdapter();
        mListView.setAdapter(mAdapter);
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

    private void initDataSource(){
        mDataSource = new ArrayList<DataModel>();
//        mDataSource.add(new DataModel("车速", "0km/h"));
//        mDataSource.add(new DataModel("点火时刻", "-10CA"));
//        mDataSource.add(new DataModel("吸气温度", "-30"));
    }

    private class DataModel{
        public String title;
        public String value;
        public DataModel(String title, String value){
            this.title = title;
            this.value = value;
        }
    }
    
    private void createCommandQueue(){
        mCommandQueue = new ArrayList<BleSendCommandModel>();
        BleSendCommandModel startRealData = new BleSendCommandModel(
                BleCommandManager.Sender.COMMAND_REAL_DATA,
                1000);
        mCommandQueue.add(startRealData);
        
        BleSendCommandModel speedCommand = new BleSendCommandModel(
                BleCommandManager.Sender.COMMAND_SPEED,
                500);
        mCommandQueue.add(speedCommand);
        BleSendCommandModel repeatFirst = new BleSendCommandModel(speedCommand);
        repeatFirst.setDelayTime(repeatDelayTime);
        mRepeatCommandList.add(repeatFirst);
        
        BleSendCommandModel randCommand = new BleSendCommandModel(
                BleCommandManager.Sender.COMMAND_RAND,
                500);
        mCommandQueue.add(randCommand);
        mRepeatCommandList.add(new BleSendCommandModel(randCommand));
        
        BleSendCommandModel temptureCmd = new BleSendCommandModel(
                BleCommandManager.Sender.COMMAND_TEMPTURE,
                500);
        mCommandQueue.add(temptureCmd);
        mRepeatCommandList.add(new BleSendCommandModel(temptureCmd));
        
        BleSendCommandModel battaryVCmd = new BleSendCommandModel(
                BleCommandManager.Sender.COMMAND_BATTARY_V,
                500);
        mCommandQueue.add(battaryVCmd);
        mRepeatCommandList.add(new BleSendCommandModel(battaryVCmd));
        
        BleSendCommandModel xiqiTempCmd = new BleSendCommandModel(
                BleCommandManager.Sender.COMMAND_XIQI_TEMPTURE,
                500);
        mCommandQueue.add(xiqiTempCmd);
        mRepeatCommandList.add(new BleSendCommandModel(xiqiTempCmd));
        
        BleSendCommandModel jiqiguanPressCmd = new BleSendCommandModel(
                BleCommandManager.Sender.COMMAND_JINQIGUAN_PRESS,
                500);
        mCommandQueue.add(jiqiguanPressCmd);
        mRepeatCommandList.add(new BleSendCommandModel(jiqiguanPressCmd));
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
                convertView = LayoutInflater.from(OBDRealDataActivity.this).inflate(
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
