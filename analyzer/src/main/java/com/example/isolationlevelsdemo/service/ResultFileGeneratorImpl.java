package com.example.isolationlevelsdemo.service;

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.io.FileWriter;

@Component
public class ResultFileGeneratorImpl implements ResultFileGenerator {
    @Override
    @SneakyThrows
    public void writeToFile(String fileContent, String fileName) {
        FileWriter fileWriter = new FileWriter(fileName);
        try (fileWriter) {
            fileWriter.write(fileContent);
            fileWriter.flush();
        }
    }
}
