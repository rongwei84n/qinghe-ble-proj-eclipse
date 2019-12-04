package com.example.model;

/** 发送指令封装 */
public class BleSendCommandModel {
    private String command;//指令内容
    private int delayTime; //执行完这个指令休眠指令,单位ms
    private SendCmdStatus status;

    public BleSendCommandModel(BleSendCommandModel originModel){
        this.command = originModel.getCommand();
        this.delayTime = originModel.getDelayTime();
        this.status = originModel.getStatus();
    }

    public BleSendCommandModel(String cmd, int delayTime){
        this.command = cmd;
        this.delayTime = delayTime;
        this.status = SendCmdStatus.STATUS_INIT;
    }

    public String getCommand() {
        return command;
    }

    public int getDelayTime() {
        return delayTime;
    }

    public SendCmdStatus getStatus() {
        return status;
    }

    public void setStatus(SendCmdStatus status) {
        this.status = status;
    }

    public boolean notSend(){
        return this.status != SendCmdStatus.STATUS_SENDED;
    }

    public enum SendCmdStatus{
        STATUS_INIT, STATUS_SENDED
    }
}
