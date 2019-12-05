package com.example.model;

import com.example.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

public class BleReceiveParsedModel {
    private static final String TAG = "BleReceiveParsedModel";

    private String originResult;
    private boolean success = false;
    private List<String> results;
    private String sendCmd;

    public BleReceiveParsedModel(String result){
        LogUtils.d(TAG, "BleReceiveParsedModel init: " + result);
        if (result != null){
            result = result.replaceAll("\r", "");
        }
        this.originResult = result;

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
        if (results == null || results.size() < index){
            return "";
        }
        return results.get(index);
    }

    public String getSendCmd(){
        return sendCmd;
    }
}
