package com.example.model;

import com.example.utils.LogUtils;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class BleReceiveParsedModel {
    private static final String TAG = "BleReceiveParsedModel";

    private String originResult;
    private boolean success = false;
    private List<String> results;
    private String sendCmd;
    
    public static final String ERROR_CODE_01 = "01";//不合法功能代码,Fxx_yyyy_z..z 中的xx非03或10，会报告此故障
    public static final String ERROR_CODE_02 = "02";//不合法数据地址
    public static final String ERROR_CODE_03 = "03";//不合法数据
    public static final String ERROR_CODE_04 = "04";//从机设备故障
    public static final String ERROR_CODE_05 = "05";//设备码不对
    public static final String ERROR_CODE_06 = "06";//其他
    
    private String parseError(String result) {
    	if (result.startsWith(ERROR_CODE_01)) {
			return "不合法功能代码";
		}else if(result.startsWith(ERROR_CODE_02)) {
			return "不合法数据地址";
		}else if(result.startsWith(ERROR_CODE_03)) {
			return "不合法数据";
		}else if(result.startsWith(ERROR_CODE_04)) {
			return "从机设备故障";
		}else if(result.startsWith(ERROR_CODE_05)) {
			return "设备码不对";
		}else if(result.startsWith(ERROR_CODE_06)) {
			return "未知错误";
		}
    	return "";
	}

    public BleReceiveParsedModel(String result){
        LogUtils.d(TAG, "BleReceiveParsedModel init: " + result);
        if (result != null){
            result = result.replaceAll("\r", "");
        }
        this.originResult = result;
        String error = parseError(result);
    	if (!TextUtils.isEmpty(error)) {
			this.success = false;
    		return;
		}

        String[] arrays = result.split("\\|");
        if (arrays == null){
            return;
        }
        if (arrays.length < 1){
            return;
        }
        sendCmd = arrays[0];

        if (arrays.length < 2){
            return;
        }
        if (arrays[1].contains("ERR")){
            success = false;
        }else {
            success = true;
        }
        LogUtils.d(TAG, "BleReceiveParsedModel init result: " + result + " success: " + success);
        String[] valuesArray = arrays[1].split("_");
        if (valuesArray == null){
            return;
        }
        results = new ArrayList<String>();
        for (int i = 1; i < valuesArray.length; i++){
            results.add(valuesArray[i]);
        }
    }

    public boolean isResultSuccess(){
        return this.success;
    }

    public String getResultByIndex(int index){
        if (results == null || results.size() <= index){
            return "";
        }
        return results.get(index);
    }

    public String getSendCmd(){
        return sendCmd;
    }
}
