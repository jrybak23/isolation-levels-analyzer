package com.example.isolationlevelsdemo;

import com.example.isolationlevelsdemo.dto.DatabaseAnalysisResult;
import com.example.isolationlevelsdemo.service.IsolationLevelAnalyzer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Set;

import static com.example.isolationlevelsdemo.testutil.JSONAssertUtil.assertJSON;
import static com.example.isolationlevelsdemo.testutil.TestUtil.readFileContent;
import static java.util.stream.Collectors.toList;

@SpringBootTest
@ActiveProfiles("test")
class IsolationLevelAnalyzerTest {
    public static final Set<String> KNOWN_DATABASES = Set.of("mysql:8", "postgres:14");
    public static final String BASE_PATH = "com/example/isolationlevelsdemo/";

    @Autowired
    private IsolationLevelAnalyzer isolationLevelAnalyzer;

    @Test
    void assertKnowAnalysisResults() throws JsonProcessingException {
        List<DatabaseAnalysisResult> results = isolationLevelAnalyzer.analyzeDatabases().stream()
                .filter(result -> KNOWN_DATABASES.contains(result.getDatabaseName()))
                .collect(toList());

        String expectedJSON = readFileContent(BASE_PATH + "com/example/isolationlevelsdemo/assertKnowAnalysisResults.json");
        String actualJSON = new ObjectMapper().writeValueAsString(results);
        assertJSON(expectedJSON, actualJSON);
    }
}