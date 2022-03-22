package com.example.isolationlevelsdemo.service;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.List;

@Component
public class JsonResultWriter {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    ResultFileGenerator resultFileGenerator;

    @SneakyThrows
    public void writeToFile(List<DatabaseAnalysisResultTable> tables) {
        String json = objectMapper.writeValueAsString(tables);
        resultFileGenerator.writeToFile(json, "results" + File.separator + "result.json");
    }
}
