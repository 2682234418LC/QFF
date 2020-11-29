package service;

import domain.User;


/**
 * 用户业务层
 */
public interface UserService {

    /**
     * 登录方法
     * @param user
     * @return
     */
    User login(User user);

    /**
     * 注测方法，保存User
     * @param user
     */
    void addUser(User user);
}
