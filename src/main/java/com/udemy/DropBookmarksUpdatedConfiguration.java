package com.udemy;

import io.dropwizard.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.db.DataSourceFactory;

import javax.validation.Valid;
import javax.validation.constraints.*;
import javax.validation.constraints.NotEmpty;

public class DropBookmarksUpdatedConfiguration extends Configuration {
    @NotEmpty
    private String password;

    @NotNull
    @Valid
    private DataSourceFactory dataSourceFactory
            =new DataSourceFactory();

    @JsonProperty("database")
    public DataSourceFactory getDataSourceFactory() {
        return dataSourceFactory;
    }

    @JsonProperty
    public void setDataSourceFactory(DataSourceFactory dataSourceFactory){
        this.dataSourceFactory = dataSourceFactory;
    }

    @JsonProperty
    public String getPassword() {
        return password;
    }
}
