### EasyDB是一款轻量级的ORM框架

&nbsp;&nbsp;&nbsp;&nbsp;EasyDB是从项目里抽离出来的一款数据库操作框架，可以方便的实现将java对象映射到SQLite数据库中，并且提供了快捷的增删改查功能，同时你也可以很方便的扩展出基于EasyDB的数据库功能操作。

#### 如何使用

* 初始化<br/>
初始化操作不会损耗任何性能，只是为了拿到包名，方便在使用的时候建立或打开数据库表，因此建议在application的onCreate()方法里进行初始化。
```
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        EasyDB.init(this);
    }
}
```

* 插入操作<br/>
```
public void insert(View view) {
    User user = new User();
    user.setId(new Random().nextInt(1000));
    user.setName("李四");
    user.setPhone("123456789");
    BaseDao dao = BaseDaoFactory.getInstance().getDao(BaseDao.class, User.class);
    long insert = dao.insert(user);
}
```

* 更新操作<br/>
```
public void update(View view) {
    BaseDao dao = BaseDaoFactory.getInstance().getDao(BaseDao.class, User.class);
    User user = new User();
    user.setPhone("15514087661");
    User where = new User();
    where.setName("张三");
    long update = dao.update(user, where);
}
```

* 删除操作<br/>
```
public void delete(View view) {
    BaseDao dao = BaseDaoFactory.getInstance().getDao(BaseDao.class, User.class);
    User user = new User();
    user.setName("李四");
    int delete = dao.delete(user);
}
```

* 查询操作<br/>
```
public void query(View view) {
    BaseDao dao = BaseDaoFactory.getInstance().getDao(BaseDao.class, User.class);
    User where = new User();
    where.setName("张三");
    List<User> query = dao.query(where);
}
```

#### 关于我

email:xiasem@163.com<br/>

如果对你有收获，欢迎点击star。
