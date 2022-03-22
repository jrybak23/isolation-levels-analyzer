package com.example.isolationlevelsdemo.service;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

@Component
public class TableModelJsonConvertor implements TableModelConvertor {
    @Autowired
    private ObjectMapper objectMapper;

    @SneakyThrows
    public String convert(List<DatabaseAnalysisResultTable> tables) {
        return objectMapper.writeValueAsString(tables);
    }
}
