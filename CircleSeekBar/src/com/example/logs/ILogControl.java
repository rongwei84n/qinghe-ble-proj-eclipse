package com.example.logs;

public interface ILogControl {

    /**
     * 根据不同的输出介质组织日志消息
     * @param logLevel 日志级别
     * @param tag  日志TAG
     * @param message 具体的日志信息
     */
    public String buildMessage(LogLevel logLevel, String tag, String message);

    /**
     * 日志输出到具体的介质中
     * @param logLevel 日志级别
     * @param tag  日志TAG
     * @param message 具体的日志信息
     */
    public void print(LogLevel logLevel, String tag, String message);


}
