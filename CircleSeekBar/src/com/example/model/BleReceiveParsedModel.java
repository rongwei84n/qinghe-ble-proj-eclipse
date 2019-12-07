package com.example.model;

import com.example.utils.LogUtils;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BleReceiveParsedModel {
    private static final String TAG = "BleReceiveParsedModel";

    private String originResult;
    private boolean success = false;
    private List<String> results;
    private String sendCmd = "";
    
    private static Map<String, ErrorMapping> mErrorMapping = new HashMap<String, BleReceiveParsedModel.ErrorMapping>();
    static {
    	addMap(mErrorMapping, new ErrorMapping("0335", "P0335", "12", "动作", "曲轴传感器"));
    	addMap(mErrorMapping, new ErrorMapping("0105", "P0105", "13", "断线/对Vcc短路", "进气压力传感器(断线、短路故障)"));
    	addMap(mErrorMapping, new ErrorMapping("0107", "P0107", "13", "进气压力传感器对GND短路", "节气门位置传感器(断线、短路故障)"));
    	addMap(mErrorMapping, new ErrorMapping("0123", "P0123", "14", "对Vcc短路", "节气门位置传感器(断线、短路故障)"));
    	addMap(mErrorMapping, new ErrorMapping("0120", "P0120", "14", "断线/对GND短路", "节气门位置传感器(断线、短路故障)"));
    	addMap(mErrorMapping, new ErrorMapping("0115", "P0115", "15", "断线/对Vcc短路", "发动机温度传感器"));
    	addMap(mErrorMapping, new ErrorMapping("0117", "P0117", "15", "发动机温度传感器对GND短路故障", "发动机温度传感器"));
    	addMap(mErrorMapping, new ErrorMapping("0110", "P0110", "21", "断线/对Vcc短路", "进气温度传感器"));
    	addMap(mErrorMapping, new ErrorMapping("0112", "P0112", "21", "进气温度传感器对GND短路故障", "进气温度传感器"));
    	addMap(mErrorMapping, new ErrorMapping("1502", "P1502", "23", "对Vcc短路", "倾倒传感器"));
    	addMap(mErrorMapping, new ErrorMapping("1503", "P1503", "23", "断线/对GND短路", "倾倒传感器"));
    	addMap(mErrorMapping, new ErrorMapping("0350", "P0350", "24", "断线/对GND短路", "点火线圈"));
    	addMap(mErrorMapping, new ErrorMapping("0200", "P0200", "32", "断线/对GND短路", "喷油器"));
    	addMap(mErrorMapping, new ErrorMapping("0230", "P0230", "41", "断线/对GND短路", "燃油泵"));
    	addMap(mErrorMapping, new ErrorMapping("0130", "P0130", "44", "断线", "O2传感器"));
    	addMap(mErrorMapping, new ErrorMapping("0132", "P0132", "44", "对Vcc短路", "O2传感器"));
    	addMap(mErrorMapping, new ErrorMapping("0131", "P0131", "44", "O2传感器对GND短路故障", "O2传感器"));
    	addMap(mErrorMapping, new ErrorMapping("0508", "P0508", "40", "ISC执行器:断线故障", "ISC执行器"));
    	addMap(mErrorMapping, new ErrorMapping("0505", "P0505", "40", "ISC执行器:对VCC/GND短路或断线故障", "ISC执行器"));
    	addMap(mErrorMapping, new ErrorMapping("0507", "P0507", "40", "ISCV开固定", "ISC执行器"));
    	addMap(mErrorMapping, new ErrorMapping("0914", "P0914", "31", "动作", "档位开关"));
    	addMap(mErrorMapping, new ErrorMapping("0500", "P0500", "91", "动作", "车速传感器"));
    	addMap(mErrorMapping, new ErrorMapping("0560", "P0560", "99", "高电压", "电池电压"));
    	addMap(mErrorMapping, new ErrorMapping("1504", "P1504", "68", "动作", "O2F/B修正"));
    	addMap(mErrorMapping, new ErrorMapping("1501", "P1501", "42", "防盗判定", "防盗判定"));
    	addMap(mErrorMapping, new ErrorMapping("1660", "P1660", "42", "无认证信息或无法正确登录认证信息", "ECU"));
    	addMap(mErrorMapping, new ErrorMapping("1661", "P1661", "42", "ECU认证结果不一致", "请确认防盗系统状态代码"));
    	addMap(mErrorMapping, new ErrorMapping("1650", "P1650", "42", "防盗天线总成与ECU不匹配", "ECU或防盗天线总成"));
    	addMap(mErrorMapping, new ErrorMapping("1662", "P1662", "42", "通信时间超限制", "ECU或防盗天线总成"));
    	addMap(mErrorMapping, new ErrorMapping("1663", "P1663", "42", "ECU没有接收到防盗天线总成的认证请求", "请确认防盗系统状态代码"));
    }
    
    private static void addMap(Map<String, ErrorMapping> map, ErrorMapping model) {
    	map.put(model.faultIndex, model);
    	map.put(model.faultCode, model);
    	try {
    		//去掉行首空格
    		int index = Integer.parseInt(model.faultIndex);
    		map.put(String.valueOf(index), model);
    	}catch(Exception e) {
    		
    	}
    }
    
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
        LogUtils.d(TAG, "BleReceiveParsedModel init string: " + result);
        if (result != null){
            result = result.replaceAll("\r", "");
        }
        this.originResult = result;
        String error = parseError(result);
    	if (!TextUtils.isEmpty(error)) {
			this.success = false;
			results = new ArrayList<String>();
			results.add(error);
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
        	if (success) {
        		results.add(valuesArray[i]);
			}else {
				results.add(parseErrorDesc(valuesArray[i]));
			}
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
    	if (sendCmd == null) {
			return "";
		}
        return sendCmd;
    }
    
    public static String parseErrorDesc(String errorCode) {
    	if (TextUtils.isEmpty(errorCode)) {
			return errorCode;
		}
    	ErrorMapping mapping = mErrorMapping.get(errorCode);
    	if (mapping == null) {
			return errorCode;
		}
    	return mapping.faultDevice + "_" + mapping.faultDesc;
    }
    
    public static class ErrorMapping{
    	public ErrorMapping(String index, String code, String customer, String desc, String device) {
    		this.faultIndex = index;
    		this.faultCode = code;
    		this.customerCode = customer;
    		this.faultDesc = desc;
    		this.faultDevice = device;
    	}
    	public String faultIndex;
    	public String faultCode;
    	public String customerCode;
    	public String faultDesc;
    	public String faultDevice;
    }
    
    
}
