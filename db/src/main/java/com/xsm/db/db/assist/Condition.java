package com.xsm.db.db.assist;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Author: 夏胜明
 * Date: 2018/7/31 0031
 * Email: xiasem@163.com
 * Description:
 */
public class Condition {
    public String whereCause;
    public String[] whereArgs;

    public Condition(Map<String, String> whereCause) {
        ArrayList<String> list = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        builder.append("1=1");
        Set<String> keys = whereCause.keySet();
        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            String value = whereCause.get(key);
            if (value != null) {
                builder.append(" and " + key + "=?");
                list.add(value);
            }
        }
        this.whereCause = builder.toString();
        this.whereArgs = list.toArray(new String[list.size()]);

    }
}
