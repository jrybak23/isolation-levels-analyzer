package com.example.isolationlevelsdemo;

import com.example.isolationlevelsdemo.dto.DatabaseAnalysisResult;

import java.util.List;

public interface IsolationLevelAnalyzer {
    List<DatabaseAnalysisResult> analyzeDatabases();
}
