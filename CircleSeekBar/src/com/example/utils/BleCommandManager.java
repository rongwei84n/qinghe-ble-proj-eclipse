package com.example.utils;

/** Ble命令的封装类 */
public class BleCommandManager {

    public static final String CMD_ENDD_TAG = "\\r";

    public static class Sender{
        /** 写设备号 */
        public static String composeDeviceNumCommand(String moduleId){
            return "F10_0000_" + moduleId + CMD_ENDD_TAG;
        }

        //故障
        public static final String COMMAND_READ_ERROR_COUNT = "F03_0106" + CMD_ENDD_TAG;//读取故障总数
        public static final String COMMAND_START_READ_ERROR = "F10_0001_3" + CMD_ENDD_TAG;//开始读取故障
        public static final String COMMAND_READ_ERROR_1 = "F03_0108" + CMD_ENDD_TAG;//读取故障1
        public static final String COMMAND_READ_ERROR_2 = "F03_010A" + CMD_ENDD_TAG;//读取故障2
        public static final String COMMAND_READ_ERROR_3 = "F03_010C" + CMD_ENDD_TAG;//读取故障3
        public static final String COMMAND_READ_ERROR_4 = "F03_010E" + CMD_ENDD_TAG;//读取故障4
        public static final String COMMAND_READ_ERROR_5 = "F03_0110" + CMD_ENDD_TAG;//读取故障5
        public static final String COMMAND_READ_ERROR_6 = "F03_0112" + CMD_ENDD_TAG;//读取故障6
        public static final String COMMAND_READ_ERROR_7 = "F03_0114" + CMD_ENDD_TAG;//读取故障7
        public static final String COMMAND_READ_ERROR_8 = "F03_0116" + CMD_ENDD_TAG;//读取故障8
        public static final String COMMAND_READ_ERROR_9 = "F03_0118" + CMD_ENDD_TAG;//读取故障9
        public static final String COMMAND_READ_ERROR_10 = "F03_0120" + CMD_ENDD_TAG;//读取故障10
        
        //读取车辆信息
        public static final String COMMAND_READ_CARINFO = "F10_0001_5" + CMD_ENDD_TAG;
        public static final String COMMAND_CAR_VID = "F03_2000" + CMD_ENDD_TAG;//车辆识别号VID
        public static final String COMMAND_STANDARD_ID = "F03_2001" + CMD_ENDD_TAG;//标定识别ID

        //结束指令
        public static final String COMMAND_FINISH = "F10_0001_0" + CMD_ENDD_TAG;//停止指令
    }

    public static class Receiver{

    }
}
