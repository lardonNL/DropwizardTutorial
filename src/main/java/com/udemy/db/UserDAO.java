package com.udemy.db;

import com.udemy.core.User;
import io.dropwizard.hibernate.AbstractDAO;
import io.dropwizard.hibernate.UnitOfWork;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Optional;

public class UserDAO extends AbstractDAO<User> {
    public UserDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    // ID zal 0 blijven als het item al bestaat in de database.
    @UnitOfWork
    public User save(User user) throws Exception {
        if (checkUserUniqueness(user.getName(), user.getEmail()).size() == 0) {
            return this.persist(user);
        }
        return user;
    }

    @UnitOfWork
    public User persistUser(User user){
        return this.persist(user);
    }

    @UnitOfWork
    public List<User> findAll(){
        return list(
                namedTypedQuery("com.udemy.dropbookmarks.core.User.findAll")
        );
    }

    @UnitOfWork
    public User getByID(long id){
        return get(id);
    }

    @UnitOfWork
    public Optional<User> findByUsername(String name){
        return Optional.ofNullable(
                uniqueResult(
                        namedTypedQuery("com.udemy.dropbookmarks.core.User.findByName")
                                .setParameter("name", name)

                )
        );
    }

    @UnitOfWork
    public Optional<User> findByUsernamePassword(String name, String password){
        return Optional.ofNullable(
            uniqueResult(
                    namedTypedQuery("com.udemy.dropbookmarks.core.User.findByUsernamePassword")
                          .setParameter("name", name)
                          .setParameter("password", password)
            )
        );
    }

    @UnitOfWork
    public List<User> checkUserUniqueness(String name, String email){
        return list(
            namedTypedQuery("com.udemy.dropbookmarks.core.User.checkUserUniqueness")
                    .setParameter("name", name)
                    .setParameter("email", email)
        );
    }


    @UnitOfWork
    public void deleteByName(String name){
        namedQuery("com.udemy.dropbookmarks.core.User.deleteByName")
                .setParameter("name", name)
                .executeUpdate();
    }
}
