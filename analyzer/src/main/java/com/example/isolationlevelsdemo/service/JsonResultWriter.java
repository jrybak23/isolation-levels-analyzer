package com.example.isolationlevelsdemo.service;

import com.example.isolationlevelsdemo.config.AppProperties;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;


@Component
@Slf4j
public class JsonResultWriter {

    @Autowired
    private AppProperties appProperties;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    ResultFileGenerator resultFileGenerator;

    @SneakyThrows
    public void writeToFile(List<DatabaseAnalysisResultTable> tables) {
        log.info("Writing tables to json file.");
        String json = objectMapper.writeValueAsString(tables);
        resultFileGenerator.writeToFile(json,appProperties.getResultsOutputPath() + "result.json");
    }
}
