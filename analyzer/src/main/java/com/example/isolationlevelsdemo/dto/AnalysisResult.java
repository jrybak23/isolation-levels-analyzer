package com.example.isolationlevelsdemo.dto;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class AnalysisResult {

    private final String phenomenaName;
    private final String result;

    public AnalysisResult(String phenomenaName, String result) {
        this.phenomenaName = phenomenaName;
        this.result = result;
    }
}
