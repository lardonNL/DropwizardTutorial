package com.udemy;

import com.udemy.core.User;
import com.udemy.db.UserDAO;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit5.DAOTestExtension;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import org.glassfish.jersey.SslConfigurator;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.net.ssl.SSLContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(DropwizardExtensionsSupport.class)
final class AuthIntegrationTest {

    private static final String CONFIG_PATH = "tst-config.yml";
    private static final DropwizardAppExtension<DropBookmarksUpdatedConfiguration> EXT
            = new DropwizardAppExtension<>(
                    DropBookmarksUpdatedApplication.class,
            ResourceHelpers.resourceFilePath(CONFIG_PATH)
    );

    private static final String TARGET = "http://localhost:9999";
    private static final String PATH = "/users/secured";
    private static final String HTTPS_TARGET = "https://localhost:9990"; // TODO USE DIFFERENT PORT FOR TEST DONE
    // TODO TEST ONCE TO INSURE ENCRYPTION WORKS AS INTENDED
    private static final String TRUST_STORE_FILE_NAME = "dropbookmarks.keystore";
    private static final String TRUST_STORE_PASSWORD = "password";
    private static long testUserID;

    public static DAOTestExtension database = DAOTestExtension.newBuilder().addEntityClass(User.class).build();
    public static UserDAO userDAO = new UserDAO(database.getSessionFactory());

//    @BeforeAll
//    public static void setUp () {
////        database.inTransaction(() -> userDAO.save(new User("", "", "")));
//
//        Client client = EXT.client();
//
//        Response response = client
//                .target(TARGET)
//                .path("users/add")
//                .request()
//                .post(Entity.json(new User("username", "password", "delete@delete.delete")));
//    }

    private static String base64UsernameAndPassword() { // "dXNlcm5hbWU6cGFzc3dvcmQ="
        return Base64.getEncoder().encodeToString("username:password".getBytes());
    }

    @Test
    @DisplayName("testInvalidCallToSecuredWorld")
    public void testInvalidCallToSecuredWorld(){
        // before oftewlwe begin scenario
        //        database.inTransaction(() -> userDAO.save(new User("", "", "")));


        Client client = EXT.client();

        Response response = client
                .target(TARGET)
                .path(PATH)
                .request()
                .get();

        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
    }

    @Test
    public void testValidCallToSecuredWorld(){

        database.inTransaction(() -> userDAO.save(new User("username", "password", "delete@delete.delete"))); //TODO werkt niet

        String expected = "Hello secured world!";
        String actual = EXT.client()
                .target(TARGET)
                .path(PATH)
                .request(MediaType.TEXT_PLAIN)
                .header("Authorization", "Basic " + base64UsernameAndPassword())
                .get(String.class);

        assertEquals(expected, actual);
    }

//    @Test
//    @DisplayName("(╯°□°)╯︵ ┻━┻")
//    public void flipTable(){    }

//    @Test
//    @Order(1)
//    public void addUserToDB(){
//
//        User user = new User("jan", "delete", "delete@delete.delete");
//
//        Response response = EXT.client()
//                .target(TARGET)
//                .path("/users/saveUser")
//                .request()
//                .header("Authorization", "Basic " + base64UsernameAndPassword())
//                .put(Entity.json(user));
//
//        testUserID = response.readEntity(User.class).getId();
//        assert(0 != testUserID);
//        assertEquals(200, response.getStatus());
//    }
//
//    @Test
//    @Order(2)
//    public void addUserToDBIfItExists(){
//
//        SslConfigurator configurator = SslConfigurator.newInstance();
//        configurator.trustStoreFile(TRUST_STORE_FILE_NAME)
//                .trustStorePassword(TRUST_STORE_PASSWORD);
//        SSLContext context = configurator.createSSLContext();
//
//        Client client = ClientBuilder
//                .newBuilder()
//                .register(FEATURE)
//                .sslContext(context)
//                .build();
//
//        User user = new User("jan", "delete", "delete@delete.delete");
//
//        Response response = client
//                .target(HTTPS_TARGET)
//                .path("/users/saveUser")
//                .request()
//                .put(Entity.json(user));
//
//        assertEquals(0, response.readEntity(User.class).getId());
//    }
//
//    @Test()
//    @Order(3)
//    public void TestRetrieveUserById(){
//        SslConfigurator configurator = SslConfigurator.newInstance();
//        configurator.trustStoreFile(TRUST_STORE_FILE_NAME)
//                .trustStorePassword(TRUST_STORE_PASSWORD);
//        SSLContext context = configurator.createSSLContext();
//
//        Client client = ClientBuilder
//                .newBuilder()
//                .register(FEATURE)
//                .sslContext(context)
//                .build();
//
//        Response response = client
//                .target(HTTPS_TARGET)
//                .path("/users/getUserByID/" + testUserID)
//                .request()
//                .get();
//
//        assertEquals(testUserID, response.readEntity(User.class).getId());
//    }
//
//    @Test
//    @Order(4)
//    public void TestRetrieveUserByIdDoesNotExist(){
//        SslConfigurator configurator = SslConfigurator.newInstance();
//        configurator.trustStoreFile(TRUST_STORE_FILE_NAME)
//                .trustStorePassword(TRUST_STORE_PASSWORD);
//        SSLContext context = configurator.createSSLContext();
//
//        Client client = ClientBuilder
//                .newBuilder()
//                .register(FEATURE)
//                .sslContext(context)
//                .build();
//
//        Response response = client
//                .target(HTTPS_TARGET)
//                .path("/users/getUserByID/" + testUserID+1)
//                .request()
//                .get();
//
//        assertNull(response.readEntity(User.class));
//    }
//
//
//    @Test
//    @Order(5)
//    public void DeleteUserFromDatabaseByName(){
//
//        SslConfigurator configurator = SslConfigurator.newInstance();
//        configurator.trustStoreFile(TRUST_STORE_FILE_NAME)
//                .trustStorePassword(TRUST_STORE_PASSWORD);
//        SSLContext context = configurator.createSSLContext();
//
//        Client client = ClientBuilder
//                .newBuilder()
//                .register(FEATURE)
//                .sslContext(context)
//                .build();
//
//        Response response = client
//                .target(HTTPS_TARGET)
//                .path("/users/deleteUserByName/jan")
//                .request()
//                .delete();
//
//        assertEquals("User succesfully deleted.", response.readEntity(String.class));
//    }
//
//    @Test
//    @Order(6)
//    public void DeleteUserFromDatabaseByNameUserDoesNotExist(){
//
//        SslConfigurator configurator = SslConfigurator.newInstance();
//        configurator.trustStoreFile(TRUST_STORE_FILE_NAME)
//                .trustStorePassword(TRUST_STORE_PASSWORD);
//        SSLContext context = configurator.createSSLContext();
//
//        Client client = ClientBuilder
//                .newBuilder()
//                .register(FEATURE)
//                .sslContext(context)
//                .build();
//
//        Response response = client
//                .target(TARGET)
//                .path("/users/deleteUserByName/jan")
//                .request()
//                .delete();
//
//        assertEquals("User with name jan does not exist", response.readEntity(String.class));
//    }
}

