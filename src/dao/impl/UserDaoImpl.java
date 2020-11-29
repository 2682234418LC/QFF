package dao.impl;

import dao.UserDao;
import domain.User;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import utils.JdbcUtils;

import java.util.List;

/**
 * 持久层实现类
 * List<Account> accounts = super.getJdbcTemplate().query("select * from account1 where id=?", new BeanPropertyRowMapper<Account>(Account.class), accountId);
 * return accounts.isEmpty() ? null : accounts.get(0);
 */
public class UserDaoImpl implements UserDao {

    private JdbcTemplate jdbcTemplate = new JdbcTemplate(JdbcUtils.getdataSource());


    @Override
    public User login(String username, String password) {
        List<User> users = jdbcTemplate.query("select * from user where username=? and password=?",
                new BeanPropertyRowMapper<User>(User.class), username, password);
        return users.isEmpty() ? null : users.get(0);
    }

    @Override
    public void add(User user) {
        jdbcTemplate.update("insert into user(username,password) values(?,?)", user.getUsername(), user.getPassword());
    }
}

