package com.xsm.easydb.db;

/**
 * Author: 夏胜明
 * Date: 2018/3/8 0008
 * Email: xiasem@163.com
 * Description:
 */

public interface IBaseDao<T> {
    long insert(T entity);
}
