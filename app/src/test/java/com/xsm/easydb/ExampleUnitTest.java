package com.xsm.easydb;

import android.util.Log;
import android.widget.Toast;

import com.xsm.easydb.bean.People;
import com.xsm.easydb.db.BaseDao;
import com.xsm.easydb.db.BaseDaoFactory;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    private static final String TAG = "ExampleUnitTest";
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void insert() {
        BaseDao<People> dao = BaseDaoFactory.getInstance().getDao(People.class);
        long insert = dao.insert(new People(1));
        Log.d(TAG, "insert: " + insert);
    }
}