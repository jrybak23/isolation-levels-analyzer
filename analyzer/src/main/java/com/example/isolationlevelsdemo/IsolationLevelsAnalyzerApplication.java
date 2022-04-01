package com.example.isolationlevelsdemo;

import com.example.isolationlevelsdemo.dto.DatabaseAnalysisResult;
import com.example.isolationlevelsdemo.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

@SpringBootApplication
@Slf4j
public class IsolationLevelsAnalyzerApplication implements CommandLineRunner, ExitCodeGenerator {

    @Autowired
    private IsolationLevelAnalyzer isolationLevelAnalyzer;

    @Autowired
    private Environment environment;

    @Autowired
    private TableConverter tableConverter;

    @Autowired
    private JsonResultWriter jsonResultWriter;

    @Autowired
    private CSVResultWriter csvResultWriter;

    public int exitCode = 0;

    public static void main(String[] args) {
        var context = SpringApplication.run(IsolationLevelsAnalyzerApplication.class, args);
        int exitCode = SpringApplication.exit(context);
        System.exit(exitCode);
    }

    @Override
    public void run(String... args) {
        boolean testProfileIsNotActive = !Arrays.asList(environment.getActiveProfiles()).contains("test");
        if (testProfileIsNotActive) {
            try {
                List<DatabaseAnalysisResult> databaseAnalysisResults = isolationLevelAnalyzer.analyzeDatabases();
                log.info("Converting results of {} databases to table format.", databaseAnalysisResults.size());
                List<DatabaseAnalysisResultTable> tables = databaseAnalysisResults.stream()
                        .map(databaseAnalysisResult -> tableConverter.convertToTable(databaseAnalysisResult))
                        .collect(toList());
                jsonResultWriter.writeToFile(tables);
                csvResultWriter.writeToFiles(tables);
                log.info("Successfully finished analyzing DBs.");
            } catch (Exception e) {
                log.error("Analyzer finished with error.", e);
                this.exitCode = 1;
            }
        }
    }

    @Override
    public int getExitCode() {
        return exitCode;
    }
}
