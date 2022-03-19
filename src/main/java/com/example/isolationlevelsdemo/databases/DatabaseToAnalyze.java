package com.example.isolationlevelsdemo.databases;

import org.hibernate.dialect.Dialect;
import org.testcontainers.containers.JdbcDatabaseContainer;

public interface DatabaseToAnalyze {
    JdbcDatabaseContainer<?> getContainer();

    Class<? extends Dialect> getDialect();
}
