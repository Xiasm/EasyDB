package com.xsm.db.db;

import android.content.ContentValues;
import android.database.Cursor;

import com.xsm.db.db.assist.Condition;
import com.xsm.db.db.assist.DaoAssist;

import java.util.HashMap;
import java.util.List;

/**
 * Author: 夏胜明
 * Date: 2018/3/8 0008
 * Email: xiasem@163.com
 * Description:完成自动建表的功能
 */

public class BaseDao<T> extends DaoAssist<T> implements IBaseDao<T> {

    @Override
    public long insert(T entity) {
        HashMap<String, String> map = getEntityKeyValues(entity);
        ContentValues values = getContentValues(map);
        return mSQLiteDatabase.insert(mTableName, null, values);
    }

    @Override
    public long update(T entity, T where) {
        //sqLiteDatabase.update(tableName,contentValues,"name=",new String[]{"jett"});
        HashMap<String, String> map = getEntityKeyValues(entity);
        ContentValues contentValues = getContentValues(map);
        HashMap<String, String> whereCause = getEntityKeyValues(where);
        Condition condition = new Condition(whereCause);
        return mSQLiteDatabase.update(mTableName, contentValues, condition.whereCause, condition.whereArgs);
    }

    @Override
    public int delete(T where) {
        HashMap<String, String> map = getEntityKeyValues(where);
        Condition condition = new Condition(map);
        return mSQLiteDatabase.delete(mTableName, condition.whereCause, condition.whereArgs);
    }

    @Override
    public List<T> query(T where) {
        return query(where, null, null, null);
    }

    @Override
    public List<T> query(T where, String orderBy, Integer startIndex, Integer limit) {
        //        sqLiteDatabase.query(tableName,null,"id=?",new String[],null,null,orderBy,"1,5");
        HashMap<String, String> map = getEntityKeyValues(where);
        String limitString = null;
        if (startIndex != null && limit != null) {
            limitString = startIndex + " , " + limit;
        }
        Condition condition = new Condition(map);
        Cursor cursor = mSQLiteDatabase.query(mTableName, null, condition.whereCause, condition.whereArgs, null, null, orderBy, limitString);
        return getResult(cursor, where);
    }

}
