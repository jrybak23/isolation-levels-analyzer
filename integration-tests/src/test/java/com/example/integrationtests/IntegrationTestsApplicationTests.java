package com.example.integrationtests;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;

import static com.example.integrationtests.TestUtil.readClasspathFileContent;
import static com.example.integrationtests.TestUtil.readFileContent;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class IntegrationTestsApplicationTests {

    public static final String RESULTS_OUTPUT_PATH = ".." + File.separator + "results" + File.separator;

    @ParameterizedTest
    @ValueSource(strings = {"mysql_8", "postgres_14"})
    void assertKnowDatabaseResult(String database) {
        String actualFileContent = readFileContent(RESULTS_OUTPUT_PATH + database + ".csv");
        String expectedFileContent = readClasspathFileContent("results/"  + database + ".csv");
        assertEquals(expectedFileContent, actualFileContent);
    }

}
