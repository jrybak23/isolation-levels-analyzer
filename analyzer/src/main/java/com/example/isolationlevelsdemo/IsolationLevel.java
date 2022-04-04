package com.example.isolationlevelsdemo;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Getter
public enum IsolationLevel {
    READ_UNCOMMITTED("TRANSACTION_READ_UNCOMMITTED", "Read Uncommitted"),
    READ_COMMITTED("TRANSACTION_READ_COMMITTED", "Read Committed"),
    REPEATABLE_READ("TRANSACTION_REPEATABLE_READ", "Repeatable Read"),
    SERIALIZABLE("TRANSACTION_SERIALIZABLE", "Serializable");

    public static final List<IsolationLevel> ALL_ISOLATION_LEVELS = Arrays.stream(IsolationLevel.values())
            .collect(toList());

    private final String jdbcName;
    private final String displayName;

    IsolationLevel(String jdbcName, String displayName) {
        this.jdbcName = jdbcName;
        this.displayName = displayName;
    }


    @Override
    public String toString() {
        return displayName;
    }
}
