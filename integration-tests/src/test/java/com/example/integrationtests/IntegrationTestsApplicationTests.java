package com.example.integrationtests;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static com.example.integrationtests.TestUtil.readClasspathFileContent;
import static com.example.integrationtests.TestUtil.readFileContent;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class IntegrationTestsApplicationTests {

    public static final String RESULTS_OUTPUT_PATH = ".." + File.separator + "results-ui/src/assets/results" + File.separator;

    @ParameterizedTest
    @ValueSource(strings = {"mysql_8", "postgres_14"})
    void assertKnowDatabaseResult(String database) {
        String actualFileContent = readFileContent(RESULTS_OUTPUT_PATH + database + ".csv");
        String expectedFileContent = readClasspathFileContent("results/"  + database + ".csv");
        List<List<String>> actualContentTable = parseTable(actualFileContent);
        List<List<String>> expectedContentTable = parseTable(expectedFileContent);
        if (!expectedContentTable.equals(actualContentTable)) {
            assertEquals(expectedFileContent, actualFileContent);
        }
    }

    private List<List<String>> parseTable(String csvFileContent) {
        return csvFileContent.lines()
                .map(this::parseCells)
                .collect(toList());
    }

    private List<String> parseCells(String line) {
       return Arrays.stream(line.split(","))
               .map(String::trim)
               .collect(toList());
    }

}
