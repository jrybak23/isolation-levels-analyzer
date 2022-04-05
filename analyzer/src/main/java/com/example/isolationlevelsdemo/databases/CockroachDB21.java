package com.example.isolationlevelsdemo.databases;

import org.hibernate.dialect.CockroachDB201Dialect;
import org.hibernate.dialect.Dialect;
import org.springframework.stereotype.Component;
import org.testcontainers.containers.CockroachContainer;
import org.testcontainers.containers.JdbcDatabaseContainer;

@Component
public class CockroachDB21 implements DatabaseToAnalyze {
    @Override
    public JdbcDatabaseContainer<?> createContainer() {
        return new CockroachContainer("cockroachdb/cockroach:v21.2.8");
    }

    @Override
    public Class<? extends Dialect> getDialect() {
        return CockroachDB201Dialect.class;
    }
}
