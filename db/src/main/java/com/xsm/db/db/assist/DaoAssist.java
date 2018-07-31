package com.xsm.db.db.assist;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.xsm.db.annotation.DbFiled;
import com.xsm.db.annotation.DbTable;
import com.xsm.db.db.assist.IDaoAssist;
import com.xsm.db.utils.Utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Author: 夏胜明
 * Date: 2018/7/31 0031
 * Email: xiasem@163.com
 * Github：https://github.com/Xiasm
 * Description:
 */
public class DaoAssist<T>  implements IDaoAssist {
    protected SQLiteDatabase mSQLiteDatabase;
    protected Class<T> mEntityClass;
    protected String mTableName;
    protected boolean isInit = false;

    /**
     * 为了避免多次调用反射损耗性能，这里定义一个缓存空间
     *
     * （key—表的列字段名，value—成员变量）
     */
    protected HashMap<String, Field> mCacheMap;

    public boolean init(SQLiteDatabase sqLiteDatabase, Class<T> entityClass) {
        this.mSQLiteDatabase = sqLiteDatabase;
        this.mEntityClass = entityClass;
        if (!isInit) {
            DbTable dbTable = mEntityClass.getAnnotation(DbTable.class);
            if (dbTable != null && !Utils.isEmpty(dbTable.value())) {
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

    /**
     * 反射获取表的列名和实体类字段名映射关系并缓存
     */
    protected void initCacheMap() {
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
                if (dbFiled != null && !Utils.isEmpty(dbFiled.value())) {
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
    protected String getCreateTableSql() {
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
            } else if (type == Float.class || type == float.class) {
                buffer.append(columnName + " DOUBLE,");
            } else if (type == byte[].class) {
                buffer.append(columnName + " BLOB,");
            } else {
                //不支持的类型
                Log.e(TAG, "getCreateTableSql: 不支持的类型 ClassName=" + mEntityClass.getSimpleName() + ", columnName=" + columnName);
                continue;
            }
        }
        if (buffer.charAt(buffer.length() - 1) == ',') {
            buffer.deleteCharAt(buffer.length() - 1);
        }
        buffer.append(")");
        return buffer.toString();
    }

    /**
     * 获取对象不为空的成员变量和成员变量的值
     * @param entity 实体类
     * @return 实体类entity的字段名和值的集合
     */
    protected HashMap<String, String> getEntityKeyValues(T entity) {
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
                if (dbFiled != null && !Utils.isEmpty(dbFiled.value())) {
                    key = dbFiled.value();
                } else {
                    key = field.getName();
                }
                if (!Utils.isEmpty(key) && !Utils.isEmpty(value)) {
                    map.put(key, value);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    /**
     * 根据map里字段的键值对添加ContentValues
     * @param map key=字段名 value=字段值
     * @return
     */
    protected ContentValues getContentValues(HashMap<String, String> map) {
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
     * 取得cursor里查询到的数据，组装成实体类list
     * @param cursor
     * @param obj
     * @return 返回实体类List
     */
    protected List<T> getResult(Cursor cursor, T obj) {
        ArrayList list = new ArrayList<>();
        Object item = null;
        while (cursor.moveToNext()) {
            try {
                item = obj.getClass().newInstance();
                //表字段-成员变量
                Iterator<Map.Entry<String, Field>> iterator = mCacheMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, Field> entry = iterator.next();
                    //取列名
                    String columnName = entry.getKey();
                    int columnIndex = cursor.getColumnIndex(columnName);
                    if (columnIndex == -1) {
                        continue;
                    }
                    Field field = entry.getValue();
                    Class<?> type = field.getType();
                    if (type == String.class) {
                        field.set(item, cursor.getString(columnIndex));
                    } else if (type == Double.class || type == double.class) {
                        field.set(item, cursor.getDouble(columnIndex));
                    } else if (type == Integer.class || type == int.class) {
                        field.set(item, cursor.getInt(columnIndex));
                    } else if (type == Long.class || type == long.class) {
                        field.set(item, cursor.getLong(columnIndex));
                    } else if (type == byte[].class) {
                        field.set(item, cursor.getBlob(columnIndex));
                    } else {
                        continue;
                    }
                }
                list.add(item);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        return list;
    }

    private static final String TAG = "DaoAssist";
}
