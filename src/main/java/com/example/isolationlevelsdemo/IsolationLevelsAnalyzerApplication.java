package com.example.isolationlevelsdemo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
@Slf4j
public class IsolationLevelsAnalyzerApplication implements CommandLineRunner {

    @Autowired
    private IsolationLevelAnalyzer isolationLevelAnalyzer;

    public static void main(String[] args) {
        SpringApplication.run(IsolationLevelsAnalyzerApplication.class, args);
    }

    @Override
    public void run(String... args) {
        List<DatabaseAnalysisResult> databaseAnalysisResults = isolationLevelAnalyzer.analyzeDatabases();
        System.out.println(databaseAnalysisResults);
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
