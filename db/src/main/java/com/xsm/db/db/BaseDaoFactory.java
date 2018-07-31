package com.xsm.db.db;

import android.database.sqlite.SQLiteDatabase;

import com.xsm.db.EasyDB;

/**
 * Author: 夏胜明
 * Date: 2018/3/8 0008
 * Email: xiasem@163.com
 * Description:
 */

public class BaseDaoFactory {
    private static BaseDaoFactory instance = null;

    public static BaseDaoFactory getInstance() {
        if (instance == null) {
            synchronized (BaseDaoFactory.class) {
                if (instance == null) {
                    instance = new BaseDaoFactory();
                }
            }
        }
        return instance;
    }

    private SQLiteDatabase mSQLiteDatabase;

    private BaseDaoFactory() {
        if (!EasyDB.isInit()) {
            throw new RuntimeException("easy db not init !");
        }
        String dbPath = "data/data/" + EasyDB.getPackageName() + "/easydb.db";
        mSQLiteDatabase = SQLiteDatabase.openOrCreateDatabase(dbPath, null);
    }

    public <T extends BaseDao<V>, V> T getDao(Class<T> daoClass, Class<V> entityClass) {
        BaseDao baseDao = null;
        try {
            baseDao = daoClass.newInstance();
            baseDao.init(mSQLiteDatabase, entityClass);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return (T) baseDao;
    }



}
