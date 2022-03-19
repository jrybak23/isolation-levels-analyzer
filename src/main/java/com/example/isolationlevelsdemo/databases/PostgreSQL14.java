package com.example.isolationlevelsdemo.databases;

import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.PostgreSQL95Dialect;
import org.springframework.stereotype.Component;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;

@Component
public class PostgreSQL14 implements DatabaseToAnalyze {
    @Override
    public JdbcDatabaseContainer<?> getContainer() {
        return new PostgreSQLContainer<>("postgres:14");
    }

    @Override
    public Class<? extends Dialect> getDialect() {
        return PostgreSQL95Dialect.class;
    }
}
