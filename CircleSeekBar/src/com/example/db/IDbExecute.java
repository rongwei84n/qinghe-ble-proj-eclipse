package com.example.db;

import android.database.sqlite.SQLiteDatabase;

/**
 * @author kaven
 * 数据库执行操作代理接口
 */
public interface IDbExecute {

    /**
     * @param db 数据访问
     * @throws Exception
     */
    public void Execute(SQLiteDatabase db) throws Exception;

}
