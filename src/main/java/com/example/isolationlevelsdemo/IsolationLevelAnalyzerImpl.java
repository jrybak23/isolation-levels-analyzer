package com.example.isolationlevelsdemo;

import com.example.isolationlevelsdemo.analises.Analysis;
import com.example.isolationlevelsdemo.config.EntityManagerFactoryFactory;
import com.example.isolationlevelsdemo.databases.DatabaseToAnalyze;
import com.example.isolationlevelsdemo.model.TestModel;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.testcontainers.containers.JdbcDatabaseContainer;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

import static com.example.isolationlevelsdemo.TransactionUtils.runInTransaction;

@Component
@Slf4j
public class IsolationLevelAnalyzerImpl implements IsolationLevelAnalyzer {
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String DB_NAME = "db_name";

    @Autowired
    List<Analysis> analyses;

    @Autowired
    List<DatabaseToAnalyze> databaseConfigs;

    @Autowired
    EntityManagerFactoryFactory entityManagerFactoryFactory;

    @Override
    public List<DatabaseAnalysisResult> analyzeDatabases() {
        List<DatabaseAnalysisResult> results = new ArrayList<>();
        for (DatabaseToAnalyze databaseConfig : databaseConfigs) {
            JdbcDatabaseContainer<?> container = databaseConfig.getContainer();
            String dockerImageName = container.getDockerImageName();
            DatabaseAnalysisResult databaseAnalysisResult = new DatabaseAnalysisResult(dockerImageName);
            results.add(databaseAnalysisResult);
            log.info("Analyzing database " + dockerImageName);
            startContainer(container);
            for (IsolationLevel isolationLevel : IsolationLevel.values()) {
                DataSource dataSource = createDatasource(container, isolationLevel);
                EntityManagerFactory emFactory = entityManagerFactoryFactory.getEntityManagerFactory(dataSource,
                        databaseConfig.getDialect());
                for (Analysis analysis : analyses) {
                    cleanTable(emFactory);
                    populateDB(emFactory);
                    String effectName = analysis.getEffectName();
                    log.info("Performing " + effectName + " with isolation level " + isolationLevel + " for DB " + dockerImageName);
                    boolean reproduced = analysis.isReproducible(emFactory);
                    String out = effectName + " is" + (reproduced ? "" : " not") + " reproduced for " + dockerImageName;
                    log.info(out);
                    AnalysisResult analysisResult = new AnalysisResult(isolationLevel.getDisplayName(),effectName, reproduced);
                    databaseAnalysisResult.addAnalysis(analysisResult);
                }
            }
        }
        return results;
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

    private DataSource createDatasource(JdbcDatabaseContainer<?> db, IsolationLevel isolationLevel) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(db.getJdbcUrl());
        config.setUsername(USERNAME);
        config.setPassword(PASSWORD);
        config.setDriverClassName(db.getJdbcDriverInstance().getClass().getName());
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
}
