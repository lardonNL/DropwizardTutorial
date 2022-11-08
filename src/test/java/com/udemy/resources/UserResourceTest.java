package com.udemy.resources;

import com.udemy.auth.DBAuthenticator;
import com.udemy.auth.HelloAuthenticator;
import com.udemy.core.User;
import com.udemy.db.UserDAO;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.auth.basic.BasicCredentials;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.dropwizard.testing.junit5.ResourceExtension;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Base64;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(DropwizardExtensionsSupport.class)
public class UserResourceTest {

    private static final UserDAO userDAO = mock(UserDAO.class);

    private static String password = "password";

    private static final HttpAuthenticationFeature FEATURE
            = HttpAuthenticationFeature.basic("username", password);

    private static final Authenticator<BasicCredentials, User> AUTHENTICATOR
            = new Authenticator<BasicCredentials, User>() {
        @Override
        public Optional<User> authenticate(BasicCredentials basicCredentials) throws AuthenticationException {
            return Optional.of(new User());
        }
    };

    public static final ResourceExtension RULE
            = ResourceExtension
            .builder()
            .addProvider(
                    new AuthDynamicFeature(
                            new BasicCredentialAuthFilter.Builder<User>()
                                    .setAuthenticator(new DBAuthenticator(userDAO))
                                    .setRealm("Secret stuff")
                                    .buildAuthFilter()
                    )
            )
            .setTestContainerFactory(
                    new GrizzlyWebTestContainerFactory()
            )
            .addProvider(
                    new AuthValueFactoryProvider.Binder<>(User.class)
            )
            .addResource(new UserResource(userDAO))
            .build();

    private static String base64UsernameAndPassword() { // "dXNlcm5hbWU6cGFzc3dvcmQ="
        return Base64.getEncoder().encodeToString("username:password".getBytes());
    }

    @BeforeAll
    public static void setUpClass(){
        RULE.getJerseyTest().client().register(FEATURE);
        when(userDAO.findByUsernamePassword(anyString(), anyString())).thenReturn(Optional.of(new User("", "", "")));
    }

    @Test
    public void getGreeting() {
        String expected = "Hello world!";

        String actual = RULE
                .target("/users")
                .request(MediaType.TEXT_PLAIN)
                .get(String.class);

        assertEquals(expected, actual);
    }

    @Test
    public void testGetSecuredGreeting(){
        Response response = RULE
                .target("/users/secured")
                .request()
                .header(HttpHeaders.AUTHORIZATION, "Basic " + base64UsernameAndPassword())
                .get();

        assertEquals("Hello secured world!", response.readEntity(String.class));
    }
}