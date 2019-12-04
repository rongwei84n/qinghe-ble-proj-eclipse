package com.example.logs;

import com.example.utils.LogUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 文件写入控制器，写入前请先确认app有写文件权限
 */
public  class LogFileControl implements ILogControl{


    private String logFilePath;
    private String logFileName;
    private LinkedBlockingQueue<String> queue=new LinkedBlockingQueue();
    private SimpleDateFormat logSdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private WriteThread writeThread;

    /**
     * 创建文件日志写入器
     * @param filePath 日志文件路径
     * @param fileName 日志文件名
     */
    public LogFileControl(String filePath, String fileName)
    {
        this.logFilePath=filePath;
        this.logFileName=fileName;
    }

    @Override
    public String buildMessage(LogLevel logLevel, String tag, String message) {

        String writeMsg=logSdf.format( new Date()) + "    " + logLevel + "    " + tag + "    " + message;
        return writeMsg;
    }

    @Override
    public void print(LogLevel logLevel, String tag, String message) {
        queue.offer(message);
        if(writeThread==null)
        {
            writeThread=new WriteThread();
            writeThread.start();
        }
    }


    private  void createLogFile()
    {
        File dirsFile = new File( logFilePath);
        if (!dirsFile.exists()){
            dirsFile.mkdir();
        }
        File logFile = new File(dirsFile,logFileName);
        if (!logFile.exists()){
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }


    private  class WriteThread extends Thread
    {
        @Override
        public void run() {
            createLogFile();
            while (true) {
                try {
                    File parentFile = new File(logFilePath);
                    if (!parentFile.exists()){
                        parentFile.mkdirs();
                    }
                    File file = new File(logFilePath,logFileName);
//                    if (!file.exists()){
//                        file.createNewFile();
//                    }
                    FileWriter filerWriter = new FileWriter(file, true);// 后面这个参数代表是不是要接上文件中原来的数据，不进行覆盖
                    BufferedWriter bufWriter = new BufferedWriter(filerWriter);
                    String message = queue.take();
                    bufWriter.write(message);
                    bufWriter.newLine();
                    bufWriter.close();
                    filerWriter.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    };
}
