package com.xsm.easydb;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.xsm.easydb.bean.People;
import com.xsm.easydb.bean.User;
import com.xsm.easydb.db.BaseDao;
import com.xsm.easydb.db.BaseDaoFactory;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void insertObject(View view) {
//        User user = new User();
//        user.setId(2);
//        user.setName("夏胜明");
//        user.setPhone("18736045070");
//        BaseDao<User> dao = BaseDaoFactory.getInstance().getDao(User.class);
//        long insert = dao.insert(user);
//        Toast.makeText(this, "插入成功 行数=" + insert, Toast.LENGTH_SHORT).show();

        BaseDao<People> dao = BaseDaoFactory.getInstance().getDao(People.class);
        long insert = dao.insert(new People(1));
        Toast.makeText(this, "插入成功 行数=" + insert, Toast.LENGTH_SHORT).show();
    }
}
