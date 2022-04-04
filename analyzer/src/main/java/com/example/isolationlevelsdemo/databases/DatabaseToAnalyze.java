package com.example.isolationlevelsdemo.databases;

import com.example.isolationlevelsdemo.IsolationLevel;
import org.hibernate.dialect.Dialect;
import org.testcontainers.containers.JdbcDatabaseContainer;

import java.util.List;

import static com.example.isolationlevelsdemo.IsolationLevel.ALL_ISOLATION_LEVELS;

public interface DatabaseToAnalyze {
    JdbcDatabaseContainer<?> createContainer();

    Class<? extends Dialect> getDialect();

    default List<IsolationLevel> getSupportedIsolationLevels() {
        return ALL_ISOLATION_LEVELS;
    }
}
