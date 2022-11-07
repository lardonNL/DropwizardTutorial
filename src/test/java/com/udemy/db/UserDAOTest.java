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
        User user = new User("jan", "wachtwoord", "jan@jan.jan");
        database.inTransaction(() -> userDAO.save(user));
        assert(user.getId() != 0);
    }

    @Test
    public void createsUserIfItExists() {
        User user = database.inTransaction(() -> userDAO.save(new User("jan", "wachtwoord", "jan@jan.jan")));
        User user2 = database.inTransaction(() -> userDAO.save(new User("jan", "wachtwoord", "piet@jan.jan")));
        System.out.println(user.getId());
        System.out.println(user2.getId());

        assert(user2.getId() == 0);
    }


    @Test
    public void getByID() {
        User user = database.inTransaction(() -> userDAO.save(new User("jan", "wachtwoord", "jan@jan.jan")));

        User user2 = userDAO.getByID(user.getId());

        assert(user2.getName().equals("jan"));
    }

    @Test
    public void getByIDDoesNotExist() {
        User user = database.inTransaction(() -> userDAO.save(new User("jan", "wachtwoord", "jan@jan.jan")));

        User user2 = userDAO.getByID(user.getId()+1);
        assert(user2 == null);
//        assert(user2.getName().equals("jan"));
    }

    @Test
    public void findAll() {
        User user1 = database.inTransaction(() -> userDAO.save(new User("jan", "wachtwoord", "jan@jan.jan")));
        User user2 = database.inTransaction(() -> userDAO.save(new User("Sjon", "wachtwoord", "sjon@sjon.sjon")));

        User user3 = userDAO.findAll().get(0);
        User user4 = userDAO.findAll().get(1);

        assert(user3.equals(user1) && user4.equals(user2));
    }

    @Test
    public void findByUsernamePassword() {
        User user1 = database.inTransaction(() -> userDAO.save(new User("jan", "wachtwoord", "jan@jan.jan")));
        database.inTransaction(() -> userDAO.save(new User("Sjon", "wachtwoord", "sjon@sjon.sjon")));

        User user3 = userDAO.findByUsernamePassword(user1.getName(), user1.getPassword()).orElse(new User());

        assert(user3.getName().equals("jan") && user3.getPassword().equals("wachtwoord"));
    }

    @Test
    public void TestNameUniqueness() {
        User user = database.inTransaction(() -> userDAO.save(new User("jan", "wachtwoord", "jan@jan.jan")));
        User user2 = database.inTransaction(() -> userDAO.save(new User("jan", "wachtwoord", "jan@jan.jan")));

        assert(user.getId() != 0 && user2.getId() == 0);
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
        assert(users.size() == expectedResults);
    }
}

