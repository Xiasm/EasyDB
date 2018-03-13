package com.xsm.easydb.db;

import java.util.List;

/**
 * Author: 夏胜明
 * Date: 2018/3/8 0008
 * Email: xiasem@163.com
 * Description:
 */

public interface IBaseDao<T> {

    long insert(T entity);

    long update(T entity,T where);

    int delete(T where);

    List<T> query(T where);

    List<T> query(T where, String orderBy, Integer startIndex, Integer limit);

}
