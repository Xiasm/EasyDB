package com.xsm.easydb;

import android.app.Application;

import com.xsm.db.EasyDB;

/**
 * Author: 夏胜明
 * Date: 2018/7/31 0031
 * Email: xiasem@163.com
 * Description:
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        EasyDB.init(this);
    }
}
