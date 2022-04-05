package com.example.isolationlevelsdemo.service;

import org.junit.jupiter.api.Test;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DatabaseAnalysisResultTableTest {

    @Test
    void testDockerHubLink() {
        String imageName = "mcr.microsoft.com/mssql/server:2019-latest";
        DatabaseAnalysisResultTable table = new DatabaseAnalysisResultTable(imageName, emptyList());
        String link = table.getDockerHubLink();
        String expectedLink = "https://www.google.com/search?q=docker+hub+mcr.microsoft.com%2Fmssql%2Fserver&num=1&as_sitesearch=hub.docker.com";
        assertEquals(expectedLink, link);
    }

}