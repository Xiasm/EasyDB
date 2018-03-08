package com.xsm.easydb.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.xsm.easydb.annotation.DbFiled;
import com.xsm.easydb.annotation.DbTable;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Author: 夏胜明
 * Date: 2018/3/8 0008
 * Email: xiasem@163.com
 * Description:完成自动建表的功能
 */

public class BaseDao<T> implements IBaseDao<T> {
    private static final String TAG = "BaseDao";
    private boolean isInit = false;
    private SQLiteDatabase mSQLiteDatabase;
    private Class<T> mEntityClass;

    private String mTableName;
    /**
     * 定义一个缓存空间（key—字段名，value—成员变量）
     */
    private HashMap<String, Field> mCacheMap;

    public boolean init(SQLiteDatabase sqLiteDatabase, Class<T> entityClass) {
        this.mSQLiteDatabase = sqLiteDatabase;
        this.mEntityClass = entityClass;
        if (!isInit) {
            DbTable dbTable = mEntityClass.getAnnotation(DbTable.class);
            if (dbTable != null && !TextUtils.isEmpty(dbTable.value())) {
                mTableName = dbTable.value();
            } else {
                mTableName = mEntityClass.getSimpleName();
            }
            if (!mSQLiteDatabase.isOpen()) {
                return false;
            }
            //执行建表操作
            String createTableSql = getCreateTableSql();
            mSQLiteDatabase.execSQL(createTableSql);
            initCacheMap();
            isInit = true;
        }
        return isInit;
    }

    private void initCacheMap() {
        if (mCacheMap == null) {
            mCacheMap = new HashMap<>();
        }
        //取到所有的列名
        String sql = "select * from " + mTableName + " limit 1,0";
        Cursor cursor = mSQLiteDatabase.rawQuery(sql, null);
        String[] columnNames = cursor.getColumnNames();
        cursor.close();
        //取到所有的成员变量
        Field[] columnFields = mEntityClass.getDeclaredFields();
        for (Field columnField : columnFields) {
            columnField.setAccessible(true);
        }
        //进行映射
        for (String columnName : columnNames) {
            Field field = null;
            for (Field columnField : columnFields) {
                String fieldName = null;
                DbFiled dbFiled = columnField.getAnnotation(DbFiled.class);
                if (dbFiled != null && !TextUtils.isEmpty(dbFiled.value())) {
                    fieldName = dbFiled.value();
                } else {
                    fieldName = columnField.getName();
                }
                if (columnName.equals(fieldName)) {
                    field = columnField;
                    break;
                }
            }
            if (field != null) {
                mCacheMap.put(columnName, field);
            }
        }
    }

    /**
     * 根据Class字节码文件获取创建表的语句
     * @return
     */
    private String getCreateTableSql() {
        //create table if not exists tb_user(_id INTEGER,name TEXT,password TEXT)
        StringBuffer buffer = new StringBuffer();
        buffer.append("create table if not exists ");
        buffer.append(mTableName + "(");
        Field[] fields = mEntityClass.getDeclaredFields();
        for (Field field : fields) {
            Class<?> type = field.getType();
            String columnName = field.getName();
            DbFiled dbFiled = field.getAnnotation(DbFiled.class);
            if (dbFiled != null && !TextUtils.isEmpty(dbFiled.value())) {
                columnName = dbFiled.value();
            }
            if (type == String.class) {
                buffer.append(columnName + " TEXT,");
            } else if (type == Integer.class || type == int.class) {
                buffer.append(columnName + " INTEGER,");
            } else if (type == Long.class || type == long.class) {
                buffer.append(columnName + " BIGINT,");
            } else if (type == Double.class || type == double.class) {
                buffer.append(columnName + " DOUBLE,");
            } else if (type == byte[].class) {
                buffer.append(columnName + " BLOB,");
            } else {
                //不支持的类型
                Log.d(TAG, "getCreateTableSql: 不支持的类型 ClassName=" + mEntityClass.getSimpleName() + ", columnName=" + columnName);
                continue;
            }
        }
        if (buffer.charAt(buffer.length() - 1) == ',') {
            buffer.deleteCharAt(buffer.length() - 1);
        }
        buffer.append(")");
        return buffer.toString();
    }

    @Override
    public long insert(T entity) {
        HashMap<String, String> map = getObjectKeyValues(entity);
        ContentValues values = getContentValues(map);
        return mSQLiteDatabase.insert(mTableName, null, values);
    }

    /**
     * 根据map里字段的键值对添加ContentValues
     * @param map
     * @return
     */
    private ContentValues getContentValues(HashMap<String, String> map) {
        ContentValues contentValues = new ContentValues();
        Set<String> keys = map.keySet();
        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            String value = map.get(key);
            if (value != null) {
                contentValues.put(key, value);
            }
        }
        return contentValues;
    }

    /**
     * 获取对象不为空的成员变量和成员变量的值
     * @param entity
     * @return
     */
    private HashMap<String, String> getObjectKeyValues(T entity) {
        HashMap<String, String> map = new HashMap<>();
        for (Field field : mCacheMap.values()) {
            field.setAccessible(true);
            try {
                Object object = field.get(entity);
                if (object == null) {
                    continue;
                }
                String value = object.toString();
                String key = null;
                DbFiled dbFiled = field.getAnnotation(DbFiled.class);
                if (dbFiled != null && !TextUtils.isEmpty(dbFiled.value())) {
                    key = dbFiled.value();
                } else {
                    key = field.getName();
                }
                if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
                    map.put(key, value);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return map;
    }
}
