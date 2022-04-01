package com.example.isolationlevelsdemo.service;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.List;

import static com.example.isolationlevelsdemo.Constants.RESULTS_OUTPUT_PATH;

@Component
@Slf4j
public class JsonResultWriter {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    ResultFileGenerator resultFileGenerator;

    @SneakyThrows
    public void writeToFile(List<DatabaseAnalysisResultTable> tables) {
        log.info("Writing tables to json file.");
        String json = objectMapper.writeValueAsString(tables);
        resultFileGenerator.writeToFile(json,RESULTS_OUTPUT_PATH + "result.json");
    }
}
