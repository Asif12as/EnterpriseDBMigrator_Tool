// src/main/java/com/yourname/dbmigration/model/MigrationResult.java

package com.yourname.dbmigration.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MigrationResult {
    private boolean success;
    private int totalTables;
    private int tablesCreated;
    private long totalRowsMigrated;
    private String errorMessage;
    private LocalDateTime startTime = LocalDateTime.now();
    private LocalDateTime endTime;
    
    public long getDurationInSeconds() {
        if (endTime == null) {
            endTime = LocalDateTime.now();
        }
        return java.time.Duration.between(startTime, endTime).getSeconds();
    }
}