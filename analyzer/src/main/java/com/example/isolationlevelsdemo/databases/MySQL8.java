package com.example.isolationlevelsdemo.databases;

import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.MySQL8Dialect;
import org.springframework.stereotype.Component;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MySQLContainer;

@Component
public class MySQL8 implements DatabaseToAnalyze {
    @Override
    public JdbcDatabaseContainer<?> createContainer() {
        return new MySQLContainer<>("mysql:8");
    }

    @Override
    public Class<? extends Dialect> getDialect() {
        return MySQL8Dialect.class;
    }
}
