package com.example.isolationlevelsdemo.service;

import com.example.isolationlevelsdemo.dto.DatabaseAnalysisResult;

public interface TableConverter {
    DatabaseAnalysisResultTable convertToTable(DatabaseAnalysisResult databaseAnalysisResult);
}
