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

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(DropwizardExtensionsSupport.class)
public class AuthIntegrationTest {

    private static final String CONFIG_PATH = "config.yml";
    public DropwizardAppExtension<DropBookmarksUpdatedConfiguration> EXT
            = new DropwizardAppExtension<>(
                    DropBookmarksUpdatedApplication.class,
            ResourceHelpers.resourceFilePath(CONFIG_PATH)
    );

    private static final HttpAuthenticationFeature FEATURE
            = HttpAuthenticationFeature.basic("username", "password");
    private static final String TARGET = "http://localhost:8080";
    private static final String PATH = "/hello/secured";
    private static final String HTTPS_TARGET = "https://localhost:9000";
    private static final String TRUST_STORE_FILE_NAME = "dropbookmarks.keystore";
    private static final String TRUST_STORE_PASSWORD = "password";
    private static long testUserID;

    public static DAOTestExtension database = DAOTestExtension.newBuilder().addEntityClass(User.class).build();
    public UserDAO userDAO = new UserDAO(database.getSessionFactory());

    @Test
    @DisplayName("testInvalidCallToSecuredWorld")
    public void testSadPath(){
        Client client = EXT.client();

        Response response = client
                .target(TARGET)
                .path(PATH)
                .request()
                .get();

        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
    }


    @Test
    @DisplayName("(╯°□°)╯︵ ┻━┻")
    public void flipTable(){    }

    @Nested
    @DisplayName("HTTPS enabled")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class HTTPSEnabled{

//        @BeforeEach
//        public void setUp () {
//            User user = new User("username", "password", "AuthIntegrationTest@test.test");
//            try {
//                database.inTransaction(() -> userDAO.save(user));
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        }

        @BeforeEach
        public void setUp () {
            Client client = EXT.client();

            Response response = client
                    .target(TARGET)
                    .path("hello/standard")
                    .request()
                    .get();
        }

        @Test
        @DisplayName("testValidCallToSecuredWorld")
        public void testHappyPath(){

            Client client = ClientBuilder
                    .newBuilder()
                    .register(FEATURE)
                    .build();

            String expected = "Hello secured world!";
            String actual = client
                    .target(TARGET)
                    .path(PATH)
                    .request(MediaType.TEXT_PLAIN)
                    .get(String.class);

            assertEquals(expected, actual);
        }

        @Test
        public void testHappyHTTPSPath(){

            SslConfigurator configurator = SslConfigurator.newInstance();
            configurator.trustStoreFile(TRUST_STORE_FILE_NAME)
                    .trustStorePassword(TRUST_STORE_PASSWORD);
            SSLContext context = configurator.createSSLContext();

            Client client = ClientBuilder
                    .newBuilder()
                    .register(FEATURE)
                    .sslContext(context)
                    .build();

            String expected = "Hello secured world!";
            String actual = client
                    .target(HTTPS_TARGET)
                    .path(PATH)
                    .request(MediaType.TEXT_PLAIN)
                    .get(String.class);

            assertEquals(expected, actual);
        }

        @Test
        @Order(1)
        public void addUserToDB(){

            SslConfigurator configurator = SslConfigurator.newInstance();
            configurator.trustStoreFile(TRUST_STORE_FILE_NAME)
                    .trustStorePassword(TRUST_STORE_PASSWORD);
            SSLContext context = configurator.createSSLContext();

            Client client = ClientBuilder
                    .newBuilder()
                    .register(FEATURE)
                    .sslContext(context)
                    .build();

            User user = new User("jan", "delete", "delete@delete.delete");

            Response response = client
                    .target(HTTPS_TARGET)
                    .path("/hello/saveUser")
                    .request()
                    .put(Entity.json(user));

            testUserID = response.readEntity(User.class).getId();
            assert(0 != testUserID);
            assertEquals(200, response.getStatus());
        }

        @Test
        @Order(2)
        public void addUserToDBIfItExists(){

            SslConfigurator configurator = SslConfigurator.newInstance();
            configurator.trustStoreFile(TRUST_STORE_FILE_NAME)
                    .trustStorePassword(TRUST_STORE_PASSWORD);
            SSLContext context = configurator.createSSLContext();

            Client client = ClientBuilder
                    .newBuilder()
                    .register(FEATURE)
                    .sslContext(context)
                    .build();

            User user = new User("jan", "delete", "delete@delete.delete");

            Response response = client
                    .target(HTTPS_TARGET)
                    .path("/hello/saveUser")
                    .request()
                    .put(Entity.json(user));

            assertEquals(0, response.readEntity(User.class).getId());
        }

        @Test()
        @Order(3)
        public void TestRetrieveUserById(){
            SslConfigurator configurator = SslConfigurator.newInstance();
            configurator.trustStoreFile(TRUST_STORE_FILE_NAME)
                    .trustStorePassword(TRUST_STORE_PASSWORD);
            SSLContext context = configurator.createSSLContext();

            Client client = ClientBuilder
                    .newBuilder()
                    .register(FEATURE)
                    .sslContext(context)
                    .build();

            Response response = client
                    .target(HTTPS_TARGET)
                    .path("/hello/getUserByID/" + testUserID)
                    .request()
                    .get();

            assertEquals(testUserID, response.readEntity(User.class).getId());
        }

        @Test
        @Order(4)
        public void TestRetrieveUserByIdDoesNotExist(){
            SslConfigurator configurator = SslConfigurator.newInstance();
            configurator.trustStoreFile(TRUST_STORE_FILE_NAME)
                    .trustStorePassword(TRUST_STORE_PASSWORD);
            SSLContext context = configurator.createSSLContext();

            Client client = ClientBuilder
                    .newBuilder()
                    .register(FEATURE)
                    .sslContext(context)
                    .build();

            Response response = client
                    .target(HTTPS_TARGET)
                    .path("/hello/getUserByID/" + testUserID+1)
                    .request()
                    .get();

            assertEquals(null, response.readEntity(User.class));
        }


        @Test
        @Order(5)
        public void DeleteUserFromDatabaseByName(){

            SslConfigurator configurator = SslConfigurator.newInstance();
            configurator.trustStoreFile(TRUST_STORE_FILE_NAME)
                    .trustStorePassword(TRUST_STORE_PASSWORD);
            SSLContext context = configurator.createSSLContext();

            Client client = ClientBuilder
                    .newBuilder()
                    .register(FEATURE)
                    .sslContext(context)
                    .build();

            Response response = client
                    .target(HTTPS_TARGET)
                    .path("/hello/deleteUserByName/jan")
                    .request()
                    .delete();

            assertEquals("User succesfully deleted.", response.readEntity(String.class));
        }

        @Test
        @Order(6)
        public void DeleteUserFromDatabaseByNameUserDoesNotExist(){

            SslConfigurator configurator = SslConfigurator.newInstance();
            configurator.trustStoreFile(TRUST_STORE_FILE_NAME)
                    .trustStorePassword(TRUST_STORE_PASSWORD);
            SSLContext context = configurator.createSSLContext();

            Client client = ClientBuilder
                    .newBuilder()
                    .register(FEATURE)
                    .sslContext(context)
                    .build();

            Response response = client
                    .target(HTTPS_TARGET)
                    .path("/hello/deleteUserByName/jan")
                    .request()
                    .delete();

            assertEquals("User with name jan does not exist", response.readEntity(String.class));
        }
    }
}
