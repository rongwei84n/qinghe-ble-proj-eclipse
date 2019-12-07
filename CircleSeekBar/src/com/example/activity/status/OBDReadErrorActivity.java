package com.example.activity.status;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Pair;
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
    private List<Pair<String, String>> mDataSource;
    private MyFuctionsAdapter mAdapter;


    private List<BleSendCommandModel> mCommandQueue;

    private List<BleSendCommandModel> mRepeatCommandList = new ArrayList<BleSendCommandModel>();

    private Handler mHandler = new Handler();

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

//    private BleSendCommandModel findNextRepeatCommand(){
//        if (mRepeatCommandList == null || mRepeatCommandList.isEmpty()){
//            return null;
//        }
//        for (BleSendCommandModel model: mRepeatCommandList){
//            if (model.notSend()){
//                return model;
//            }
//        }
//
//        //如果已经遍历完了所有的待发队列，直接把待发队列设置成初始化状态，从头遍历.
//        for (BleSendCommandModel model: mRepeatCommandList){
//            model.setStatus(BleSendCommandModel.SendCmdStatus.STATUS_INIT);
//        }
//        for (BleSendCommandModel model: mRepeatCommandList){
//            if (model.notSend()){
//                return model;
//            }
//        }
//
//        return null;
//    }

    private BleSendCommandModel findNextSendCommand(){
        for (BleSendCommandModel model: mCommandQueue){
            if (model.notSend()){
                return model;
            }
        }
        return null;
    }

    @Override
    protected void onMessageReceive(String msg) {
    	super.onMessageReceive(msg);
        BleReceiveParsedModel receiveParsedModel = new BleReceiveParsedModel(msg);
        if (BleCommandManager.Sender.COMMAND_READ_ERROR_COUNT.contains(receiveParsedModel.getSendCmd())){
            try{
                errorCount = new BigDecimal(receiveParsedModel.getResultByIndex(0)).intValue();
            }catch (Exception e){
                LogUtils.e(TAG, e);
            }
            if (receiveParsedModel.isResultSuccess()){
            	mDataSource.add(new Pair<String, String>("故障个数: " + receiveParsedModel.getResultByIndex(0), 
            			""));
            }else {
            	mDataSource.add(new Pair<String, String>("读取故障个数失败", ""));
            }
            mAdapter.notifyDataSetChanged();

            LogUtils.i(TAG, "errorCount: " + errorCount);
            errorCount = errorCount > 10? 10:errorCount;

            for (int i = 1; i <= errorCount; i++){
                BleSendCommandModel composeCommand = null;
                switch (i){
                    case 1:
                        composeCommand = new BleSendCommandModel(
                                BleCommandManager.Sender.COMMAND_READ_ERROR_1, 0);
                        break;
                    case 2:
                        composeCommand = new BleSendCommandModel(
                                BleCommandManager.Sender.COMMAND_READ_ERROR_2, 0);
                        break;
                    case 3:
                        composeCommand = new BleSendCommandModel(
                                BleCommandManager.Sender.COMMAND_READ_ERROR_3, 0);
                        break;
                    case 4:
                        composeCommand = new BleSendCommandModel(
                                BleCommandManager.Sender.COMMAND_READ_ERROR_4, 0);
                        break;
                    case 5:
                        composeCommand = new BleSendCommandModel(
                                BleCommandManager.Sender.COMMAND_READ_ERROR_5, 0);
                        break;
                    case 6:
                        composeCommand = new BleSendCommandModel(
                                BleCommandManager.Sender.COMMAND_READ_ERROR_6, 0);
                        break;
                    case 7:
                        composeCommand = new BleSendCommandModel(
                                BleCommandManager.Sender.COMMAND_READ_ERROR_7, 0);
                        break;
                    case 8:
                        composeCommand = new BleSendCommandModel(
                                BleCommandManager.Sender.COMMAND_READ_ERROR_8, 0);
                        break;
                    case 9:
                        composeCommand = new BleSendCommandModel(
                                BleCommandManager.Sender.COMMAND_READ_ERROR_9, 0);
                        break;
                    case 10:
                        composeCommand = new BleSendCommandModel(
                                BleCommandManager.Sender.COMMAND_READ_ERROR_10, 0);
                        break;
                }
                if (composeCommand != null){
                    mCommandQueue.add(composeCommand);
                    mRepeatCommandList.add(new BleSendCommandModel(composeCommand));
                }
            }
        }else if (BleCommandManager.Sender.COMMAND_START_READ_ERROR.contains(receiveParsedModel.getSendCmd())){

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
            if (receiveParsedModel.isResultSuccess()){
            	StringBuilder sbTitle = new StringBuilder();
            	sbTitle.append("故障码: ");
            	if (TextUtils.isEmpty(receiveParsedModel.getResultByIndex(0))) {
            		sbTitle.append("无");
				}else {
					sbTitle.append(receiveParsedModel.getResultByIndex(0));
				}
            	
            	StringBuilder sbValue = new StringBuilder();
            	sbValue.append("故障描述:");
            	String desc = BleReceiveParsedModel.parseErrorDesc(receiveParsedModel.getResultByIndex(0));
            	if (TextUtils.equals(desc, receiveParsedModel.getResultByIndex(0))) {
            		sbValue.append("无");
				}else {
					sbValue.append(desc);
				}
                mDataSource.add(new Pair<String, String>(sbTitle.toString(), sbValue.toString()));
            }else {
            	mDataSource.add(new Pair<String, String>("故障码读取错误: " + receiveParsedModel.getResultByIndex(0), 
            			""));
            }

            mAdapter.notifyDataSetChanged();
        }else if(BleCommandManager.Sender.COMMAND_FINISH.contains(receiveParsedModel.getSendCmd())) {
        	if(mWaitDialog != null) {
        		mWaitDialog.dismiss();
        		mWaitDialog = null;
        	}
        	finish();
        	return;
        }

        BleSendCommandModel presendCmd = findSendCmdByReceive(receiveParsedModel.getSendCmd());
        int delayTime = 0;
        if (presendCmd != null){
            delayTime = presendCmd.getDelayTime();
        }
        BleSendCommandModel nextSendModel = findNextSendCommand();
//        if (nextSendModel == null){
//            nextSendModel = findNextRepeatCommand();
//            delayTime = repeatDelayTime;
//        }
        final BleSendCommandModel nextTrySendModel = nextSendModel;
        if (nextTrySendModel != null) {
        	LogUtils.d(TAG,"nextTrySendModel: " + nextTrySendModel.getCommand());
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    sendMessage(nextTrySendModel);
                }
            }, delayTime);
        }
    }

    private void createCommandQueue(){
        mCommandQueue = new ArrayList<BleSendCommandModel>();

        BleSendCommandModel startReadErrorCommand = new BleSendCommandModel(
                BleCommandManager.Sender.COMMAND_START_READ_ERROR,
                200);
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
        mDataSource = new ArrayList<Pair<String, String>>();
        mDataSource.add(new Pair<String, String>("正在读取故障信息.", ""));
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
//                convertView = LayoutInflater.from(OBDReadErrorActivity.this).inflate(
//                        R.layout.listitem_common, null);
                convertView = LayoutInflater.from(OBDReadErrorActivity.this).inflate(
                        android.R.layout.simple_list_item_2, null);
            }
            TextView textView1 = (TextView) convertView.findViewById(android.R.id.text1);
            textView1.setText(mDataSource.get(position).first);
            
            TextView textView2 = (TextView) convertView.findViewById(android.R.id.text2);
            textView2.setText(mDataSource.get(position).second);
            
            return convertView;
        }
    }
}
