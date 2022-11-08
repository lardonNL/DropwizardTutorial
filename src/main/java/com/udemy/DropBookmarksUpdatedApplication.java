package com.udemy;

import com.udemy.auth.DBAuthenticator;
import com.udemy.core.User;
import com.udemy.db.UserDAO;
import com.udemy.resources.UserResource;
import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class DropBookmarksUpdatedApplication extends Application<DropBookmarksUpdatedConfiguration> {

    public static void main(final String[] args) throws Exception {
        new DropBookmarksUpdatedApplication().run(args);
    }

    @Override
    public String getName() {
        return "DropBookmarksUpdated";
    }

    private final HibernateBundle<DropBookmarksUpdatedConfiguration> hibernate = new HibernateBundle<DropBookmarksUpdatedConfiguration>(User.class) {
        @Override
        public DataSourceFactory getDataSourceFactory(DropBookmarksUpdatedConfiguration configuration) {
            return configuration.getDataSourceFactory();
        }
    };

    @Override
    public void initialize(final Bootstrap<DropBookmarksUpdatedConfiguration> bootstrap) {
        bootstrap.addBundle(hibernate);
    }

    @Override
    public void run(final DropBookmarksUpdatedConfiguration configuration,
                    final Environment environment) {
        final UserDAO userDAO = new UserDAO(hibernate.getSessionFactory());

        DBAuthenticator dbAuth = new UnitOfWorkAwareProxyFactory(hibernate)
                .create(DBAuthenticator.class, UserDAO.class, userDAO);

        environment.jersey().register(new UserResource(userDAO));
        environment.jersey().register(
                new AuthDynamicFeature(
                        new BasicCredentialAuthFilter.Builder<User>()
                                .setAuthenticator(dbAuth)
                                .buildAuthFilter()
                )
        );
        environment.jersey().register(new AuthValueFactoryProvider.Binder<>(User.class));
//        environment.jersey().register(new UserResource(userDAO));
    }
}
