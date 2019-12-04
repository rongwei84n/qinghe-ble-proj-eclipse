package com.example.utils;

import android.text.TextUtils;

import com.example.logs.ILogControl;
import com.example.logs.LogLevel;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class LogUtils {

    private static LogLevel minLogLevel=LogLevel.V;
    private static List<ILogControl> logService=new ArrayList<ILogControl>();

    public static void setLogLevel(String value)
    {
        minLogLevel=LogLevel.valueOf(value.toUpperCase());
    }



    public static void addLogControl(ILogControl iLogControl)
    {
        if(!logService.contains(iLogControl))
        {
            logService.add(iLogControl);
        }
    }

    public static void removeLogControl(ILogControl iLogControl)
    {
        if(logService.contains(iLogControl))
        {
            logService.remove(iLogControl);
        }
    }


    private static void log(LogLevel logLevel,String tag,String message)
    {
        if (TextUtils.isEmpty(message)){
            return;
        }
        if(logService.size()==0)
        {
            return;
        }
        //不输出低于mimLogLevel级别的日志
        if(minLogLevel!=null&&minLogLevel.getValue()>logLevel.getValue())
        {
            return;
        }
        for (ILogControl iLogControl :logService) {
            iLogControl.print(logLevel,tag, iLogControl.buildMessage(logLevel,tag,message));
        }
    }



    public static void d(String tag,String message)
    {
        log(LogLevel.D,tag,message);
    }

    public static void v(String tag,String message)
    {
        log(LogLevel.V,tag,message);
    }

    public static void i(String tag,String message)
    {
        log(LogLevel.I,tag,message);
    }
    public static void w(String tag,String message)
    {
        log(LogLevel.W,tag,message);
    }
    public static void e(String tag,String message)
    {
        log(LogLevel.E,tag,message);
    }
    public static void e(String tag,Throwable e)
    {
        if(e!=null)
        {
            String printMsg = getStackTrace(e);
            if (TextUtils.isEmpty(printMsg)){
                return;
            }
            log(LogLevel.E,tag,printMsg);
        }
    }

    private static String getStackTrace(Throwable throwable){
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        try{
            throwable.printStackTrace(pw);
            return sw.toString();
        } finally{
            pw.close();
        }
    }
}
