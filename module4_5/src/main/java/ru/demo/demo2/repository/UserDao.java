package ru.demo.demo2.repository;

import ru.demo.demo2.model.User;

public class UserDao extends BaseDao<User> {
    public UserDao() { super(User.class); }
}
