package com.cwang.service;

import com.cwang.dao.UserDao;
import com.cwang.domain.User;

import java.sql.SQLException;

public class UserService {

    public boolean regist(User user) {
        UserDao dao = new UserDao();
        int row = 0;
        try {
            row = dao.regist(user);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return row > 0 ? true : false;
    }
}