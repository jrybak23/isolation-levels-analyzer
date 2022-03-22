package com.example.isolationlevelsdemo.dto;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class AnalysisResult {

    private final String effectName;
    private final String result;

    public AnalysisResult(String effectName, String result) {
        this.effectName = effectName;
        this.result = result;
    }
}
