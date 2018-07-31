package com.xsm.db;

import android.content.Context;

/**
 * Author: 夏胜明
 * Date: 2018/7/31 0031
 * Email: xiasem@163.com
 * Github：https://github.com/Xiasm
 * Description:
 */
public class EasyDB {
    private static Context mApplicationContext;
    private static String mPackageName;
    private static boolean init = false;

    public static void init(Context context) {
        if (context == null) {
            throw new RuntimeException("init context is null");
        }
        mApplicationContext = context.getApplicationContext();
        mPackageName = mApplicationContext.getPackageName();
        init = true;
    }

    public static Context getApplicationContext() {
        return mApplicationContext;
    }

    public static String getPackageName() {
        return mPackageName;
    }

    public static boolean isInit() {
        return init;
    }
}
