package com.example.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


/**
 * @author kaven
 * 数据库访问类，使用前请先初始化
 * 静态方法直接使用，动态方法可以提高效率
 * 动态方法非线程安全
 */

/**
 * @author kaven
 *
 */
public class SqliteDAL {

    public DBHelper mDbHelper = null;
    private SqliteDAL(Context context){
        mDbHelper = new DBHelper(context);
    }

    private static SqliteDAL _sqlite = null;
    /**
     * 单件实例
     * @param context
     * @return
     */
    public static SqliteDAL getInstance(Context context){
        if (_sqlite == null){
            synchronized (SqliteDAL.class) {
                _sqlite = new SqliteDAL(context);
            }
        }
        return _sqlite;
    }

    /**
     * 查询第一个结果值
     * @param queryCmd
     * @return
     */
    public String ExecObject(String queryCmd) throws Exception{
        String _r = null;
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cur = db.rawQuery(queryCmd, null);
        try{
            if (cur.moveToNext()){
                _r = cur.getString(0);
            }
        }
        catch(Exception ex){
            throw ex;
        }
        finally{
            cur.close();
            db.close();
        }
        return _r;
    }

    /**
     * 读取数据，不存在并发问题。不关闭数据库连接
     * @param queryCmd
     * @param iQuery
     */
    public void Query(String queryCmd, IDbQuery iQuery) throws Exception{
        if (iQuery == null)return;
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cur = db.rawQuery(queryCmd, null);
        try{
            iQuery.Query(cur);
        }
        catch(Exception ex){
            throw ex;
        }
        finally{
            cur.close();
            db.close();
        }
    }
    /**
     * 操作数据库，多线程存在并发问题。不关闭数据库连接
     * @param iExec
     */
    public void Exec(IDbExecute iExec) throws Exception{
        if (iExec == null)return;
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        try{
            iExec.Execute(db);
        }
        catch(Exception ex){
            throw ex;
        }
        finally{
            db.close();
        }
    }
    /**
     * 操作数据库，多线程存在并发问题。不关闭数据库连接
     * @param sqlCommand
     */
    public void Exec(String sqlCommand){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.execSQL(sqlCommand);
        db.close();
    }


    /**
     * 销毁数据库连接
     */
    public void Destory(){
        mDbHelper.close();
    }
}
