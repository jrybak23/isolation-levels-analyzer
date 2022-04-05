package com.example.isolationlevelsdemo.databases;

import com.example.isolationlevelsdemo.IsolationLevel;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.Oracle12cDialect;
import org.springframework.stereotype.Component;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.OracleContainer;

import java.util.List;

import static com.example.isolationlevelsdemo.IsolationLevel.READ_COMMITTED;
import static com.example.isolationlevelsdemo.IsolationLevel.SERIALIZABLE;

@Component
public class OracleXE18 implements DatabaseToAnalyze {

    public static final List<IsolationLevel> SUPPORTED_ISOLATION_LEVELS = List.of(READ_COMMITTED, SERIALIZABLE);

    @Override
    public JdbcDatabaseContainer<?> createContainer() {
        return new OracleContainer("gvenzl/oracle-xe:18");
    }

    @Override
    public Class<? extends Dialect> getDialect() {
        return Oracle12cDialect.class;
    }

    @Override
    public List<IsolationLevel> getSupportedIsolationLevels() {
        return SUPPORTED_ISOLATION_LEVELS;
    }
}
