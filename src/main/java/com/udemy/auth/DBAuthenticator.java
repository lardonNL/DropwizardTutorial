package com.udemy.auth;

import com.udemy.core.User;
import com.udemy.db.UserDAO;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;
import io.dropwizard.hibernate.UnitOfWork;

import java.util.Optional;

public class DBAuthenticator
        implements Authenticator<BasicCredentials, User> {

    private UserDAO userDAO;

    public DBAuthenticator(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @UnitOfWork
    @Override
    public Optional<User> authenticate(BasicCredentials credentials) throws AuthenticationException {

        return userDAO.findByUsernamePassword(credentials.getUsername(), credentials.getPassword());
    }

}

