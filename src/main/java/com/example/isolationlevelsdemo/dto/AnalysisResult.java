package com.example.isolationlevelsdemo.dto;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class AnalysisResult {

    private final String effectName;
    private final boolean reproduced;

    public AnalysisResult(String effectName, boolean reproduced) {
        this.effectName = effectName;
        this.reproduced = reproduced;
    }
}
