package com.example.isolationlevelsdemo.dto;

import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString
@Getter
public class DatabaseAnalysisResult {
    private final String databaseName;
    private final List<AnalysisResult> analysis = new ArrayList<>();

    public DatabaseAnalysisResult(String databaseName) {
        this.databaseName = databaseName;
    }

    public void addAnalysis(AnalysisResult analysisResult) {
        analysis.add(analysisResult);
    }
}
