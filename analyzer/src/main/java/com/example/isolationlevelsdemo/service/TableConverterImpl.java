package com.example.isolationlevelsdemo.service;

import com.example.isolationlevelsdemo.analises.Analysis;
import com.example.isolationlevelsdemo.dto.AnalysisResult;
import com.example.isolationlevelsdemo.dto.DatabaseAnalysisResult;
import com.example.isolationlevelsdemo.dto.IsolationLevelAnalysisResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;


@Component
public class TableConverterImpl implements TableConverter {

    @Autowired
    private List<Analysis> analyses;

    @Override
    public DatabaseAnalysisResultTable convertToTable(DatabaseAnalysisResult databaseAnalysisResult) {
        DatabaseAnalysisResultTable table = new DatabaseAnalysisResultTable(databaseAnalysisResult.getDatabaseName(), getColumnHeaders());
        for (IsolationLevelAnalysisResult isolationLevel : databaseAnalysisResult.getIsolationLevels()) {
            List<String> cellValues = getRowCellValues(isolationLevel);
            table.addRow(isolationLevel.getIsolationLevel(), cellValues);
        }
        return table;
    }

    private List<String> getRowCellValues(IsolationLevelAnalysisResult result) {
        return result.getAnalyses().stream()
                .map(AnalysisResult::getResult)
                .collect(toList());
    }

    private List<String> getColumnHeaders() {
        return analyses.stream()
                .map(Analysis::getEffectName)
                .collect(toList());
    }
}

