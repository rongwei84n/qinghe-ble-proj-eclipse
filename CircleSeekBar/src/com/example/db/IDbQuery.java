package com.example.db;

import android.database.Cursor;


/**
 * @author kaven
 * 数据库查询操作代理接口
 */
public interface IDbQuery {

    /**
     * @param cursor 游标
     * @throws Exception
     */
    public void Query(Cursor cursor) throws Exception;

}
