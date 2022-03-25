package com.example.isolationlevelsdemo.dto;

import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;


@ToString
@Getter
public class IsolationLevelAnalysisResult {
    private final String isolationLevel;
    private final List<AnalysisResult> analyses = new ArrayList<>();

    public IsolationLevelAnalysisResult(String isolationLevel) {
        this.isolationLevel = isolationLevel;
    }

    public void addAnalysis(AnalysisResult analysisResult) {
        analyses.add(analysisResult);
    }

}
