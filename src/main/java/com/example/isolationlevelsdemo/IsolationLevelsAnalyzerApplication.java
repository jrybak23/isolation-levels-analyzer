package com.example.isolationlevelsdemo;

import com.example.isolationlevelsdemo.dto.DatabaseAnalysisResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@Slf4j
public class IsolationLevelsAnalyzerApplication implements CommandLineRunner {

    @Autowired
    private IsolationLevelAnalyzer isolationLevelAnalyzer;

    @Autowired
    private Environment environment;

    public static void main(String[] args) {
        SpringApplication.run(IsolationLevelsAnalyzerApplication.class, args);
    }

    @Override
    public void run(String... args) {
        boolean testProfileIsNotActive = !Arrays.asList(environment.getActiveProfiles()).contains("test");
        if (testProfileIsNotActive) {
            List<DatabaseAnalysisResult> databaseAnalysisResults = isolationLevelAnalyzer.analyzeDatabases();
            System.out.println(databaseAnalysisResults);
        }
    }

}
