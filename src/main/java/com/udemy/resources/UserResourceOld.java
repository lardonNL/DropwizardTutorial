package com.udemy.resources;

import com.udemy.db.UserDAO;

public class UserResourceOld {
    public UserDAO dao;

    public UserResourceOld(UserDAO dao) {
        this.dao = dao;
    }
}
