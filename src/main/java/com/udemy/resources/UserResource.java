package com.udemy.resources;

import com.codahale.metrics.annotation.Timed;
import com.udemy.core.ModelGetTest;
import com.udemy.core.User;
import com.udemy.db.UserDAO;
import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Optional;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    private UserDAO userDAO;

    public UserResource(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/add")
    @UnitOfWork
    public String pushStandard() throws Exception {
        userDAO.save(new User("username", "password", "test@test.test"));
        return "pushed standard data";
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @UnitOfWork
    public String getGreeting(){
        return "Hello world!";
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/secured")
    @Timed
    @UnitOfWork
    public String getSecuredGreeting(@Auth User user) {
        return "Hello secured world!";
    }

    @GET
    @Path("/model")
    @UnitOfWork
    public ModelGetTest modelGetTest(){
        return new ModelGetTest();
    }

    @PUT
    @Path("/saveUser")
    @UnitOfWork
    public User saveUser(@NotNull @Valid User userToSave, @Auth User user) throws Exception {
        System.out.println(userToSave.getName());
        return userDAO.save(userToSave);
    }

    @GET
    @Path("/getUserByName/{name}")
    @Produces(MediaType.TEXT_PLAIN)
    @UnitOfWork
    public String getUserByName(@PathParam("name") String username, @Auth User user){
        User foundUser = userDAO.findByUsername(username).orElse(null);
        if (foundUser == null){
            return "";
        }
        return foundUser.getName();
    }

    @GET
    @Path("/getUserByID/{id}")
    @UnitOfWork
    public User getUserByID(@PathParam("id") int id, @Auth User user){
        return userDAO.getByID(id);
    }

    @DELETE
    @Path("/deleteUserByName/{name}")
    @UnitOfWork
    public String deleteUserByName(@PathParam("name") String username, @Auth User user){
        Optional<User> userToDelete = userDAO.findByUsername(username);
        if (userToDelete.isPresent()){
            userDAO.deleteByName(userToDelete.get().getName());
            return "User succesfully deleted.";
        }
        return "User with name " + username + " does not exist";
    }
}
