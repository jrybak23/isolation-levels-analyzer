package com.example.isolationlevelsdemo.service;

import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static com.example.isolationlevelsdemo.Constants.RESULTS_OUTPUT_PATH;
import static java.util.stream.Collectors.joining;

@Component
public class CSVResultWriter {
    private static final int CELL_SIZE = 20;

    @SneakyThrows
    public void writeToFiles(List<DatabaseAnalysisResultTable> tables) {
        for (DatabaseAnalysisResultTable table : tables) {
            String fileName = table.getDatabaseName().replace(":", "_");
            String filePath = RESULTS_OUTPUT_PATH + fileName + ".csv";
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
            try (writer) {
                writeLines(table, writer);
                writer.flush();
            }
        }
    }

    private void writeLines(DatabaseAnalysisResultTable table, BufferedWriter writer) throws IOException {
        for (List<String> row : table.getTable()) {
            String line = createLine(row);
            writer.write(line);
            writer.newLine();
        }
    }

    @NotNull
    private String createLine(List<String> row) {
        return row.stream()
                .map(this::addSpaces)
                .collect(joining(","));
    }

    @NotNull
    private String addSpaces(String cellValue) {
        if (cellValue.length() < CELL_SIZE) {
            int size = CELL_SIZE - cellValue.length();
            String spaces = " ".repeat(size);
            return cellValue + spaces;
        } else {
            return cellValue;
        }
    }
}
