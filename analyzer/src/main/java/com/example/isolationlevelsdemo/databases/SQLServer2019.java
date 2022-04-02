package com.example.isolationlevelsdemo.databases;

import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.SQLServer2016Dialect;
import org.springframework.stereotype.Component;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MSSQLServerContainer;

@Component
public class SQLServer2019 implements DatabaseToAnalyze {
    @Override
    public JdbcDatabaseContainer<?> createContainer() {
        return new MSSQLServerContainer<>("mcr.microsoft.com/mssql/server:2019-latest")
                .acceptLicense()
                .withUrlParam("encrypt", "true")
                .withUrlParam("trustServerCertificate", "true");
    }

    @Override
    public Class<? extends Dialect> getDialect() {
        return SQLServer2016Dialect.class;
    }
}
