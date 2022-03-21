package com.example.isolationlevelsdemo.service;

import com.example.isolationlevelsdemo.dto.DatabaseAnalysisResult;

import java.util.List;

public interface IsolationLevelAnalyzer {
    List<DatabaseAnalysisResult> analyzeDatabases();
}
