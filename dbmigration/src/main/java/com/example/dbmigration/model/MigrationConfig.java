// src/main/java/com/yourname/dbmigration/model/MigrationConfig.java

package com.yourname.dbmigration.model;

import lombok.Data;

@Data
public class MigrationConfig {
    private String sourceType;
    private String sourceHost;
    private int sourcePort;
    private String sourceDatabase;
    private String sourceSchema;
    private String sourceUsername;
    private String sourcePassword;
    
    private String targetType;
    private String targetHost;
    private int targetPort;
    private String targetDatabase;
    private String targetSchema;
    private String targetUsername;
    private String targetPassword;
    
    private boolean includeData = true;
    private int batchSize = 1000;
    private boolean validateData = true;
}