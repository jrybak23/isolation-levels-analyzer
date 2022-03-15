package com.example.isolationlevelsdemo;

import com.example.isolationlevelsdemo.analises.Analysis;
import com.example.isolationlevelsdemo.config.EntityManagerFactoryFactory;
import com.example.isolationlevelsdemo.model.TestModel;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MySQLContainer;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;

import static com.example.isolationlevelsdemo.TransactionUtils.runInTransaction;

@SpringBootApplication
public class IsolationLevelsAnalyzerApplication implements CommandLineRunner {

    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String DB_NAME = "db_name";

    @Autowired
    List<Analysis> analyses;

    @Autowired
    EntityManagerFactoryFactory entityManagerFactoryFactory;

    public static void main(String[] args) {
        SpringApplication.run(IsolationLevelsAnalyzerApplication.class, args);
    }

    @Override
    public void run(String... args) {
        JdbcDatabaseContainer<?> db = startPostgresContainer();
        DataSource dataSource = createDatasource(db);
        EntityManagerFactory emFactory = entityManagerFactoryFactory.getEntityManagerFactory(dataSource);

        for (Analysis analysis : analyses) {
            populateDB(emFactory);
            boolean reproduced = analysis.isReproducible(emFactory);
            String out = analysis.getEffectName() + " is" + (reproduced ? "" : " not") + " reproduced.";
            System.out.println(out);
            cleanTable(emFactory);
        }
    }

    private void populateDB(EntityManagerFactory emFactory) {
        runInTransaction(emFactory, (entityManager) -> {
            TestModel model = new TestModel();
            model.setId(1);
            model.setValue("initial value");
            entityManager.persist(model);
        });
    }

    @SneakyThrows
    private void cleanTable(EntityManagerFactory entityManagerFactory) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.createQuery("delete from TestModel t");
    }

    @NotNull
    private DataSource createDatasource(JdbcDatabaseContainer<?> db) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(db.getJdbcUrl());
        config.setUsername(USERNAME);
        config.setPassword(PASSWORD);
        config.setDriverClassName(db.getJdbcDriverInstance().getClass().getName());
        int i = Connection.TRANSACTION_SERIALIZABLE;
        config.setTransactionIsolation("TRANSACTION_SERIALIZABLE");
        config.setAutoCommit(false);
        return new HikariDataSource(config);
    }

    @NotNull
    private JdbcDatabaseContainer<?> startPostgresContainer() {
        JdbcDatabaseContainer<?> db = new MySQLContainer<>("mysql:8")
                .withDatabaseName(DB_NAME)
                .withUsername(USERNAME)
                .withPassword(PASSWORD);
        /*PostgreSQLContainer<?> db = new PostgreSQLContainer<>("postgres:13")
                .withDatabaseName(DB_NAME)
                .withUsername(USERNAME)
                .withPassword(PASSWORD);*/
        db.start();
        return db;
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
