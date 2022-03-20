package com.example.isolationlevelsdemo.dto;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class AnalysisResult {

    private final String isolationLevel;
    private final String effectName;
    private final boolean reproduced;

    public AnalysisResult(String isolationLevel, String effectName, boolean reproduced) {
        this.isolationLevel = isolationLevel;
        this.effectName = effectName;
        this.reproduced = reproduced;
    }
}
