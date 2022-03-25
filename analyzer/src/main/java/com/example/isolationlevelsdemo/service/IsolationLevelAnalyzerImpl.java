package com.example.isolationlevelsdemo.service;

import com.example.isolationlevelsdemo.IsolationLevel;
import com.example.isolationlevelsdemo.analises.Analysis;
import com.example.isolationlevelsdemo.config.EntityManagerFactoryFactory;
import com.example.isolationlevelsdemo.databases.DatabaseToAnalyze;
import com.example.isolationlevelsdemo.dto.AnalysisResult;
import com.example.isolationlevelsdemo.dto.DatabaseAnalysisResult;
import com.example.isolationlevelsdemo.dto.IsolationLevelAnalysisResult;
import com.example.isolationlevelsdemo.model.TestModel;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.testcontainers.containers.JdbcDatabaseContainer;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.List;

import static com.example.isolationlevelsdemo.TransactionUtils.runInTransaction;
import static java.util.stream.Collectors.toList;

@Component
@Slf4j
public class IsolationLevelAnalyzerImpl implements IsolationLevelAnalyzer {
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String DB_NAME = "db_name";

    @Autowired
    List<Analysis> analyses;

    @Autowired
    List<DatabaseToAnalyze> databasesToAnalyze;

    @Autowired
    EntityManagerFactoryFactory entityManagerFactoryFactory;

    @Override
    public List<DatabaseAnalysisResult> analyzeDatabases() {
        return databasesToAnalyze.stream()
                .map(this::analyzeDatabase)
                .collect(toList());
    }

    @NotNull
    private DatabaseAnalysisResult analyzeDatabase(DatabaseToAnalyze databaseToAnalyze) {
        JdbcDatabaseContainer<?> container = databaseToAnalyze.createContainer();
        String dockerImageName = container.getDockerImageName();
        DatabaseAnalysisResult databaseAnalysisResult = new DatabaseAnalysisResult(dockerImageName);
        log.info("Analyzing database " + dockerImageName);
        startContainer(container);
        for (IsolationLevel isolationLevel : IsolationLevel.values()) {
            IsolationLevelAnalysisResult isolationLevelAnalysis = new IsolationLevelAnalysisResult(isolationLevel.getDisplayName());
            databaseAnalysisResult.addIsolationLevelAnalysis(isolationLevelAnalysis);
            DataSource dataSource = createDatasource(container, isolationLevel);
            EntityManagerFactory emFactory = entityManagerFactoryFactory.getEntityManagerFactory(dataSource,
                    databaseToAnalyze.getDialect());
            for (Analysis analysis : analyses) {
                cleanTable(emFactory);
                populateDB(emFactory);
                String effectName = analysis.getEffectName();
                log.info("Performing {} with isolation level {} for DB {}", effectName, isolationLevel, dockerImageName);
                boolean reproduced = analysis.isReproducible(emFactory);
                String result = getResultAsString(reproduced);
                log.info("{} is {} with isolation level {} for {}", effectName, result, isolationLevel, dockerImageName);
                AnalysisResult analysisResult = new AnalysisResult(effectName, result);
                isolationLevelAnalysis.addAnalysis(analysisResult);
            }
        }
        return databaseAnalysisResult;
    }

    private String getResultAsString(boolean reproduced) {
        return (reproduced ? "" : "not ") + "reproduced";
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
