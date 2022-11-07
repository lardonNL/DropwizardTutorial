package com.udemy.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.checkerframework.common.aliasing.qual.Unique;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.security.Principal;
import java.util.Objects;

@Entity
@Table(name = "users")
@NamedQueries({
        @NamedQuery(name = "com.udemy.dropbookmarks.core.User.findAll",
                query = "select u from User u"),
        @NamedQuery(name = "com.udemy.dropbookmarks.core.User.findByUsernamePassword",
                query = "select u from User u " +
                        "where u.name = :name " +
                        "and u.password = :password"),
        @NamedQuery(name = "com.udemy.dropbookmarks.core.User.checkUserUniqueness",
                query = "select u from User u " +
                        "where u.name = :name " +
                        "or u.email = :email"),
        @NamedQuery(name = "com.udemy.dropbookmarks.core.User.findByName",
                query = "select u from User u " +
                        "where u.name = :name "),
        @NamedQuery(name = "com.udemy.dropbookmarks.core.User.deleteByName",
                query = "delete from User u " +
                        "where u.name = :name")
})
public class User implements Principal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    private long id;

    @Column(name = "name", unique = true)
    @JsonProperty
    @NotNull
    @Unique
    private String name;

    @JsonProperty
    @NotNull
    private String password;

    @JsonProperty
    private String email;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id && name.equals(user.name) && password.equals(user.password) && email.equals(user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, password, email);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

//    public String getUsername(){
//        return username;
//    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public User(String name, String password, String email) {
        this.name = name;
        this.password = password;
        this.email = email;
    }

    public User(){

    }
}
