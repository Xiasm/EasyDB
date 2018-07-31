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
    }

    public void update(View view) {
        BaseDao dao = BaseDaoFactory.getInstance().getDao(BaseDao.class, User.class);
        User user = new User();
        user.setPhone("15514087661");
        User where = new User();
        where.setName("张三");
        long update = dao.update(user, where);
    }

    public void delete(View view) {
        BaseDao dao = BaseDaoFactory.getInstance().getDao(BaseDao.class, User.class);
        User user = new User();
        user.setName("李四");
        int delete = dao.delete(user);
    }

    public void query(View view) {
        BaseDao dao = BaseDaoFactory.getInstance().getDao(BaseDao.class, User.class);
        User where = new User();
        where.setName("张三");
        List<User> query = dao.query(where);
    }

    private static final String TAG = "MainActivity";

    public void nextActivity(View view) {
        startActivity(new Intent(MainActivity.this, SecondActivity.class));
    }
}
