package com.example.isolationlevelsdemo.databases;

import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.MariaDB106Dialect;
import org.springframework.stereotype.Component;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MariaDBContainer;

@Component
public class MariaDB10 implements DatabaseToAnalyze {
    @Override
    public JdbcDatabaseContainer<?> createContainer() {
        return new MariaDBContainer<>("mariadb:10.7");
    }

    @Override
    public Class<? extends Dialect> getDialect() {
        return MariaDB106Dialect.class;
    }
}
