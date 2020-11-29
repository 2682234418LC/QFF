package dao;

import domain.User;


/**
 * 持久层
 */
public interface UserDao {
    /**
     * 登录
     * @param username
     * @param password
     * @return
     */
   User login(String username, String password);

    /**
     * 注测方法
     * @param user
     * @return
     */
    void add(User user);
}
