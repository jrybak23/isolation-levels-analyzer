package com.example.isolationlevelsdemo;

import com.example.isolationlevelsdemo.analises.Analysis;
import com.example.isolationlevelsdemo.config.EntityManagerFactoryFactory;
import com.example.isolationlevelsdemo.databases.DatabaseToAnalyze;
import com.example.isolationlevelsdemo.model.TestModel;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.testcontainers.containers.JdbcDatabaseContainer;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;

import static com.example.isolationlevelsdemo.TransactionUtils.runInTransaction;

@SpringBootApplication
@Slf4j
public class IsolationLevelsAnalyzerApplication implements CommandLineRunner {

    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String DB_NAME = "db_name";

    @Autowired
    List<Analysis> analyses;

    @Autowired
    List<DatabaseToAnalyze> databaseConfigs;

    @Autowired
    EntityManagerFactoryFactory entityManagerFactoryFactory;

    public static void main(String[] args) {
        SpringApplication.run(IsolationLevelsAnalyzerApplication.class, args);
    }

    @Override
    public void run(String... args) {
        for (DatabaseToAnalyze databaseConfig : databaseConfigs) {
            JdbcDatabaseContainer<?> container = databaseConfig.getContainer();
            String dockerImageName = container.getDockerImageName();
            log.info("Analyzing database " + dockerImageName);
            startContainer(container);
            for (IsolationLevel isolationLevel : IsolationLevel.values()) {
                DataSource dataSource = createDatasource(container, isolationLevel);
                EntityManagerFactory emFactory = entityManagerFactoryFactory.getEntityManagerFactory(dataSource,
                        databaseConfig.getDialect());

                for (Analysis analysis : analyses) {
                    cleanTable(emFactory);
                    populateDB(emFactory);
                    log.info("Performing " + analysis.getEffectName() + " with isolation level " + isolationLevel + " for DB " + dockerImageName);
                    boolean reproduced = analysis.isReproducible(emFactory);
                    String out = analysis.getEffectName() + " is" + (reproduced ? "" : " not") + " reproduced for " + dockerImageName;
                    log.info(out);
                }
            }
        }

    }

    private void populateDB(EntityManagerFactory emFactory) {
        log.info("Populating table with test data.");
        runInTransaction(emFactory, (entityManager) -> {
            TestModel model = new TestModel();
            model.setId(1);
            model.setValue("initial value");
            entityManager.persist(model);
        });
    }

    private void cleanTable(EntityManagerFactory entityManagerFactory) {
        log.info("Cleaning table.");
        runInTransaction(entityManagerFactory, entityManager -> {
            entityManager.createQuery("delete from TestModel t").executeUpdate();
        });
    }

    @NotNull
    private DataSource createDatasource(JdbcDatabaseContainer<?> db, IsolationLevel isolationLevel) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(db.getJdbcUrl());
        config.setUsername(USERNAME);
        config.setPassword(PASSWORD);
        config.setDriverClassName(db.getJdbcDriverInstance().getClass().getName());
        int i = Connection.TRANSACTION_SERIALIZABLE;
        config.setTransactionIsolation(isolationLevel.getJdbcName());
        config.setAutoCommit(false);
        return new HikariDataSource(config);
    }

    private void startContainer(JdbcDatabaseContainer<?> container) {
        container
                .withDatabaseName(DB_NAME)
                .withUsername(USERNAME)
                .withPassword(PASSWORD)
                .start();
    }


        /*private EntityManagerFactory entityManagerFactory(DataSource dataSource) {
        Map<String, Object> props = new HashMap<>();
        props.put("javax.persistence.nonJtaDataSource", dataSource);
        props.put("javax.persistence.transactionType", "RESOURCE_LOCAL");
        props.put("hibernate.hbm2ddl.auto", "create-drop");
        props.put("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
        props.put("hibernate.archive.autodetection", "class, hbm");
        props.put("provider", "org.hibernate.ejb.HibernatePersistence");
        return Persistence.createEntityManagerFactory("JPAEXAMPLE", props);
    }
*/

}
