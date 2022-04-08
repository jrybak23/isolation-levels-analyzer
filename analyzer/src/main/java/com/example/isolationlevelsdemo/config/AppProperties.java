package com.example.isolationlevelsdemo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties(prefix = "app")
@ConstructorBinding
public class AppProperties {
    private final String resultsOutputPath;
    private final int lockTimeout;

    public AppProperties(String resultsOutputPath, int lockTimeout) {
        this.resultsOutputPath = resultsOutputPath;
        this.lockTimeout = lockTimeout;
    }

    public String getResultsOutputPath() {
        return resultsOutputPath;
    }

    public int getLockTimeout() {
        return lockTimeout;
    }
}
