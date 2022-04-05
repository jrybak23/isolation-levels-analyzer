package com.example.isolationlevelsdemo.service;

import lombok.Getter;
import lombok.ToString;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toList;

@ToString
@Getter
public class DatabaseAnalysisResultTable {
    private final String databaseName;
    private final List<List<String>> table = new ArrayList<>();

    public DatabaseAnalysisResultTable(String databaseName, List<String> columnHeaders) {
        this.databaseName = databaseName;
        List<String> columnHeadersRow = Stream.concat(Stream.of(""), columnHeaders.stream())
                .collect(toList());
        table.add(columnHeadersRow);
    }

    public void addRow(String rowHeader, List<String> cellValues) {
        List<String> row = Stream.concat(Stream.of(rowHeader), cellValues.stream())
                .collect(toList());
        table.add(row);
    }

    @SuppressWarnings("unused")
    public String getDockerHubLink() {
        String baseUrl = "https://www.google.com/search?";
        return baseUrl + "q=" + getSearchParamValue() + "&num=1&as_sitesearch=hub.docker.com";
    }

    private String getSearchParamValue() {
        String imageNameWithoutTag = databaseName.split(":")[0];
        return "docker+hub+" + URLEncoder.encode(imageNameWithoutTag, UTF_8);
    }
}
