package com.udemy.db;

import com.udemy.core.User;
import io.dropwizard.testing.junit5.DAOTestExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(DropwizardExtensionsSupport.class)
public class UserDAOTest {
    public DAOTestExtension database = DAOTestExtension.newBuilder().addEntityClass(User.class).build();

    private UserDAO userDAO = new UserDAO(database.getSessionFactory());

    @BeforeEach
    public void setUp() {
        userDAO = new UserDAO(database.getSessionFactory());
    }

    @Test
    public void createsUser() {
        User savedUser = new User("jan", "wachtwoord", "jan@jan.jan");
        database.inTransaction(() -> userDAO.save(savedUser));
        assertTrue(savedUser.getId() != 0);
    }

    @Test
    public void createsUserIfItExists() {
        database.inTransaction(() -> userDAO.save(new User("jan", "wachtwoord", "jan@jan.jan")));

        User duplicateUser = database.inTransaction(() -> userDAO.save(new User("jan", "wachtwoord", "piet@jan.jan")));

        assertEquals(0, duplicateUser.getId());
    }


    @Test
    public void getByID() {
        User savedUser = database.inTransaction(() -> userDAO.save(new User("jan", "wachtwoord", "jan@jan.jan")));

        User retrievedUser = userDAO.getByID(savedUser.getId());

        assertEquals("jan", retrievedUser.getName());
    }

    @Test
    public void getByIDDoesNotExist() {
        User savedUser = database.inTransaction(() -> userDAO.save(new User("jan", "wachtwoord", "jan@jan.jan")));

        User retrievedUser = userDAO.getByID(savedUser.getId()+1);

        assertNull(retrievedUser);
    }

    @Test
    public void findAll() {
        User savedUser1 = database.inTransaction(() -> userDAO.save(new User("jan", "wachtwoord", "jan@jan.jan")));
        User savedUser2 = database.inTransaction(() -> userDAO.save(new User("Sjon", "wachtwoord", "sjon@sjon.sjon")));

        User retrievedUser1 = userDAO.findAll().get(0);
        User retrievedUser2 = userDAO.findAll().get(1);

        assertAll(
                ()-> assertEquals(savedUser1, retrievedUser1),
                ()-> assertEquals(savedUser2, retrievedUser2)
        );
    }

    @Test
    public void findByUsernamePassword() {
        User savedUser = database.inTransaction(() -> userDAO.save(new User("jan", "wachtwoord", "jan@jan.jan")));
        database.inTransaction(() -> userDAO.save(new User("Sjon", "wachtwoord", "sjon@sjon.sjon")));

        User retrievedUser = userDAO.findByUsernamePassword(savedUser.getName(), savedUser.getPassword()).orElse(new User());

        assertAll(
                () -> assertEquals("jan", retrievedUser.getName()),
                () -> assertEquals("wachtwoord", retrievedUser.getPassword())
        );
    }

    @ParameterizedTest(name = "[{index}] name = {0}, email = {1}, expectedResults = {2}")
    @CsvSource({
            ",      ,                   0",
            ",      jan@jan.jan,        1",
            "jan,   ,                   1",
            "Piet,  Piet@Piet.Piet,     0",
            "jan,   jan@jan.jan,        1",
            "jan,   Sjon@Sjon.Sjon,     2"
    })
    public void checkUserUniquenessWithCSV(String name, String email, int expectedResults) {
        database.inTransaction(() -> userDAO.save(new User("jan", "wachtwoord", "jan@jan.jan")));
        database.inTransaction(() -> userDAO.save(new User("Sjon", "wachtwoord", "Sjon@Sjon.Sjon")));

        List<User> users = database.inTransaction(() -> userDAO.checkUserUniqueness(name, email));
        assertEquals(users.size(), expectedResults);
    }
}

