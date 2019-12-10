package com.example.activity.status;

import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.lee.circleseekbar.R;
import com.example.activity.JdyBaseActivity;
import com.example.model.BleReceiveParsedModel;
import com.example.model.BleSendCommandModel;
import com.example.utils.BleCommandManager;
import com.example.utils.LogUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/** 读取故障码顺序
 *  1. 读取故障总数 F03_0106
 *  2. 读取故障指令 F10_0001_3
 *  3. 根据故障总数，构造单个故障指令列表，故障(1-10)，超过10个的不获取？
 *  F03_0108，F03_010A, F03_010C， F03_010E，F03_0110，F03_0112， F03_0114， F03_0116， F03_0118， F03_0120
 *  4. 一次获取，获取完所有故障后，停止指令F10_0001_0
 */
public class OBDReadErrorActivity extends JdyBaseActivity {

    private ListView mListView;
    private List<ErrorDataModel> mDataSource;
    private MyFuctionsAdapter mAdapter;


    private List<BleSendCommandModel> mCommandQueue;

    private List<BleSendCommandModel> mRepeatCommandList = new ArrayList<BleSendCommandModel>();

    private int errorCount = 0;

    @Override
    protected void onBleConnectSuccess() {
        super.onBleConnectSuccess();
        BleSendCommandModel sendCommandModel = findNextSendCommand();
        if (sendCommandModel != null){
            sendMessage(sendCommandModel);
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

    private BleSendCommandModel findNextSendCommand(){
        for (BleSendCommandModel model: mCommandQueue){
            if (model.notSend()){
                return model;
            }
        }
        return null;
    }
    
    private void updateDateSource(String key, String text1, String text2) {
    	for (ErrorDataModel model : mDataSource) {
			if (model.key.equals(key)) {
				model.text1 = text1;
				model.text2 = text2;
				return;
			}
		}
    	//还没有保存这个数据，添加
    	ErrorDataModel model = new ErrorDataModel(key, text1, text2);
    	mDataSource.add(model);
    	mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onMessageReceive(String msg) {
    	super.onMessageReceive(msg);
        BleReceiveParsedModel receiveParsedModel = new BleReceiveParsedModel(msg);
        if (BleCommandManager.Sender.COMMAND_READ_ERROR_COUNT.contains(receiveParsedModel.getSendCmd())){
        	LogUtils.d(TAG, "收到读取命令数量返回");
            try{
                errorCount = new BigDecimal(receiveParsedModel.getResultByIndex(0)).intValue();
            }catch (Exception e){
                LogUtils.e(TAG, e);
            }
            if (receiveParsedModel.isResultSuccess()){
            	updateDateSource("故障个数", "故障个数: " + receiveParsedModel.getResultByIndex(0), "");
            }else {
            	updateDateSource("故障个数", "读取故障个数失败", "");
            }

            LogUtils.i(TAG, "errorCount: " + errorCount);
            errorCount = errorCount > 10? 10:errorCount;

            for (int i = 1; i <= errorCount; i++){
                BleSendCommandModel composeCommand = null;
                switch (i){
                    case 1:
                        composeCommand = new BleSendCommandModel(
                                BleCommandManager.Sender.COMMAND_READ_ERROR_1, 200);
                        break;
                    case 2:
                        composeCommand = new BleSendCommandModel(
                                BleCommandManager.Sender.COMMAND_READ_ERROR_2, 200);
                        break;
                    case 3:
                        composeCommand = new BleSendCommandModel(
                                BleCommandManager.Sender.COMMAND_READ_ERROR_3, 200);
                        break;
                    case 4:
                        composeCommand = new BleSendCommandModel(
                                BleCommandManager.Sender.COMMAND_READ_ERROR_4, 200);
                        break;
                    case 5:
                        composeCommand = new BleSendCommandModel(
                                BleCommandManager.Sender.COMMAND_READ_ERROR_5, 200);
                        break;
                    case 6:
                        composeCommand = new BleSendCommandModel(
                                BleCommandManager.Sender.COMMAND_READ_ERROR_6, 200);
                        break;
                    case 7:
                        composeCommand = new BleSendCommandModel(
                                BleCommandManager.Sender.COMMAND_READ_ERROR_7, 200);
                        break;
                    case 8:
                        composeCommand = new BleSendCommandModel(
                                BleCommandManager.Sender.COMMAND_READ_ERROR_8, 200);
                        break;
                    case 9:
                        composeCommand = new BleSendCommandModel(
                                BleCommandManager.Sender.COMMAND_READ_ERROR_9, 200);
                        break;
                    case 10:
                        composeCommand = new BleSendCommandModel(
                                BleCommandManager.Sender.COMMAND_READ_ERROR_10, 200);
                        break;
                }
                if (composeCommand != null){
                    mCommandQueue.add(composeCommand);
                    if (i == 1) {
                    	BleSendCommandModel cmdfirst = new BleSendCommandModel(composeCommand);
                    	cmdfirst.setDelayTime(repeatDelayTime);
                    	mRepeatCommandList.add(cmdfirst);
					}else {
						mRepeatCommandList.add(new BleSendCommandModel(composeCommand));
					}
                }
            }
        }else if (BleCommandManager.Sender.COMMAND_START_READ_ERROR.contains(receiveParsedModel.getSendCmd())){
        	LogUtils.d(TAG, "收到开始读取命令返回");
        }else if (BleCommandManager.Sender.COMMAND_READ_ERROR_1.contains(receiveParsedModel.getSendCmd()) ||
                  BleCommandManager.Sender.COMMAND_READ_ERROR_2.contains(receiveParsedModel.getSendCmd()) ||
                BleCommandManager.Sender.COMMAND_READ_ERROR_3.contains(receiveParsedModel.getSendCmd()) ||
                BleCommandManager.Sender.COMMAND_READ_ERROR_4.contains(receiveParsedModel.getSendCmd()) ||
                BleCommandManager.Sender.COMMAND_READ_ERROR_5.contains(receiveParsedModel.getSendCmd()) ||
                BleCommandManager.Sender.COMMAND_READ_ERROR_6.contains(receiveParsedModel.getSendCmd()) ||
                BleCommandManager.Sender.COMMAND_READ_ERROR_7.contains(receiveParsedModel.getSendCmd()) ||
                BleCommandManager.Sender.COMMAND_READ_ERROR_8.contains(receiveParsedModel.getSendCmd()) ||
                BleCommandManager.Sender.COMMAND_READ_ERROR_9.contains(receiveParsedModel.getSendCmd()) ||
                BleCommandManager.Sender.COMMAND_READ_ERROR_10.contains(receiveParsedModel.getSendCmd())){
        	LogUtils.d(TAG, "收到故障读取命令返回");
            if (receiveParsedModel.isResultSuccess()){
            	StringBuilder sbTitle = new StringBuilder();
            	sbTitle.append("故障码: ");
            	if (TextUtils.isEmpty(receiveParsedModel.getResultByIndex(0))) {
            		sbTitle.append("无");
				}else {
					sbTitle.append(receiveParsedModel.getResultByIndex(0));
				}
            	
            	StringBuilder sbValue = new StringBuilder();
            	String desc = BleReceiveParsedModel.parseFaultDesc(receiveParsedModel.getResultByIndex(0));
            	if (TextUtils.equals(desc, receiveParsedModel.getResultByIndex(0))) {
            		sbValue.append("无");
				}else {
					sbValue.append(desc);
				}
            	updateDateSource("故障" + receiveParsedModel.getResultByIndex(0), sbTitle.toString(), sbValue.toString());
            }else {
            	updateDateSource("故障" + receiveParsedModel.getResultByIndex(0), "故障码读取错误: " + receiveParsedModel.getResultByIndex(0), "");
            }
            mAdapter.notifyDataSetChanged();
        }else if(BleCommandManager.Sender.COMMAND_FINISH.contains(receiveParsedModel.getSendCmd())) {
        	LogUtils.d(TAG, "收到结束命令返回");
        	if(mWaitDialog != null) {
        		mWaitDialog.dismiss();
        		mWaitDialog = null;
        	}
        	mCommandQueue.clear();
        	mRepeatCommandList.clear();
        	finish();
        	return;
        }else {
        	LogUtils.d(TAG, "收到未知命令返回");
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

    private void createCommandQueue(){
        mCommandQueue = new ArrayList<BleSendCommandModel>();

        BleSendCommandModel startReadErrorCommand = new BleSendCommandModel(
                BleCommandManager.Sender.COMMAND_START_READ_ERROR,
                2000);
        mCommandQueue.add(startReadErrorCommand);
        
        BleSendCommandModel totalErrorCommand = new BleSendCommandModel(
                BleCommandManager.Sender.COMMAND_READ_ERROR_COUNT,
                200);
        mCommandQueue.add(totalErrorCommand);
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
        mTvTitle.setText("读取当前故障码");
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
        mDataSource = new ArrayList<ErrorDataModel>();
//        mDataSource.add(new ErrorDataModel("正在读取故障信息", "正在读取故障信息", ""));
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
                convertView = LayoutInflater.from(OBDReadErrorActivity.this).inflate(
                        android.R.layout.simple_list_item_2, null);
            }
            TextView textView1 = (TextView) convertView.findViewById(android.R.id.text1);
            textView1.setText(mDataSource.get(position).text1);
            
            TextView textView2 = (TextView) convertView.findViewById(android.R.id.text2);
            textView2.setText(mDataSource.get(position).text2);
            
            return convertView;
        }
    }
    
    public static class ErrorDataModel{
    	public String key;
    	public String text1;
    	public String text2;
    	
    	public ErrorDataModel(String key, String text1, String text2) {
    		this.key = key;
    		this.text1 = text1;
    		this.text2 = text2;
    	}
    }
}
