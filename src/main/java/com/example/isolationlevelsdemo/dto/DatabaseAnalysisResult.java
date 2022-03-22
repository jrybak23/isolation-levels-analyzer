package com.example.isolationlevelsdemo.dto;

import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString
@Getter
public class DatabaseAnalysisResult {
    private final String databaseName;
    private final List<IsolationLevelAnalysisResult> isolationLevels = new ArrayList<>();

    public DatabaseAnalysisResult(String databaseName) {
        this.databaseName = databaseName;
    }

    public void addIsolationLevelAnalysis(IsolationLevelAnalysisResult analysis) {
        this.isolationLevels.add(analysis);
    }

}
