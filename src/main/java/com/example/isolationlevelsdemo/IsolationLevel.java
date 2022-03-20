package com.example.isolationlevelsdemo;

import lombok.Getter;

@Getter
public enum IsolationLevel {
    READ_UNCOMMITTED("TRANSACTION_READ_UNCOMMITTED", "Read Uncommitted"),
    TRANSACTION_READ_COMMITTED("TRANSACTION_READ_COMMITTED", "Read Committed"),
    TRANSACTION_REPEATABLE_READ("TRANSACTION_REPEATABLE_READ", "Repeatable Read"),
    TRANSACTION_SERIALIZABLE("TRANSACTION_SERIALIZABLE", "Serializable");

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
