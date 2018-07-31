package com.xsm.easydb;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.xsm.db.db.BaseDao;
import com.xsm.db.db.BaseDaoFactory;
import com.xsm.db.utils.Utils;
import com.xsm.easydb.bean.User;

import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String packageCodePath = getPackageName();
        if (Utils.isEmpty(packageCodePath) ) {

        }
    }

    public void insert(View view) {
        User user = new User();
        user.setId(new Random().nextInt(1000));
        user.setName("李四");
        user.setPhone("123456789");
        BaseDao dao = BaseDaoFactory.getInstance().getDao(BaseDao.class, User.class);
        long insert = dao.insert(user);
        Toast.makeText(this, "插入成功 行数=" + insert, Toast.LENGTH_SHORT).show();

    }

    public void update(View view) {
        BaseDao dao = BaseDaoFactory.getInstance().getDao(BaseDao.class, User.class);
        User user = new User();
        user.setPhone("15514087661");
        User where = new User();
        where.setName("张三");
        long update = dao.update(user, where);
        Toast.makeText(this, "更新成功 行数=" + update, Toast.LENGTH_SHORT).show();
    }

    public void delete(View view) {
        BaseDao dao = BaseDaoFactory.getInstance().getDao(BaseDao.class, User.class);
        User user = new User();
        user.setName("李四");
        int delete = dao.delete(user);
        Toast.makeText(this, "删除成功 行数=" + delete, Toast.LENGTH_SHORT).show();

    }

    public void query(View view) {
        BaseDao dao = BaseDaoFactory.getInstance().getDao(BaseDao.class, User.class);
        User where = new User();
        where.setName("张三");
        List<User> query = dao.query(where);
        Log.d(TAG, "query: " + query.toString());
    }

    private static final String TAG = "MainActivity";

    public void nextActivity(View view) {
        startActivity(new Intent(MainActivity.this, SecondActivity.class));
    }
}
