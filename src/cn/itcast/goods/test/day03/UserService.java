package cn.itcast.goods.test.day03;

import org.junit.Test;

/**
 * Created by yanke on 2016/12/8.
 */
public class UserService {

    private UserDao userDao = new UserDao();
//    public void setUserDao(UserDao userDao){
//        this.userDao = userDao;
//    }
    @Test
    public void add(){
        userDao.add();
    }

}
