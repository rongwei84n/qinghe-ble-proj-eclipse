package com.example.logs;

import android.util.Log;

/**
 * 控制台日志输出器
 */
public class LogConsoleControl implements ILogControl{
    @Override
    public String buildMessage(LogLevel logLevel, String tag, String message) {
        return message;
    }

    @Override
    public void print(LogLevel logLevel, String tag, String message) {

        switch (logLevel)
        {
            case D:
                Log.d(tag,message);
                break;
            case W:
                Log.w(tag,message);
                break;
            case E:
                Log.e(tag,message);
                break;
            case V:
                Log.v(tag,message);
                break;
            case I:
                Log.i(tag,message);
                break;
        }
    }
}
