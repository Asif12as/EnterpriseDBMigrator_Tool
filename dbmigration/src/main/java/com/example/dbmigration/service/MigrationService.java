// src/main/java/com/yourname/dbmigration/service/MigrationService.java

package com.yourname.dbmigration.service;

import com.yourname.dbmigration.connection.DatabaseConnectionManager;
import com.yourname.dbmigration.mapping.DataTypeMapper;
import com.yourname.dbmigration.model.DatabaseTable;
import com.yourname.dbmigration.model.MigrationConfig;
import com.yourname.dbmigration.model.MigrationResult;
import com.yourname.dbmigration.schema.SchemaAnalyzer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MigrationService {
    
    private final DatabaseConnectionManager connectionManager;
    private final SchemaAnalyzer schemaAnalyzer;
    private final DataTypeMapper dataTypeMapper;
    private final SchemaGenerationService schemaGenerationService;
    private final DataMigrationService dataMigrationService;
    
    public MigrationResult migrateDatabases(MigrationConfig config) {
        MigrationResult result = new MigrationResult();
        Connection sourceConn = null;
        Connection targetConn = null;
        
        try {
            // Step 1: Establish connections
            sourceConn = connectionManager.getConnection(
                config.getSourceType(), 
                config.getSourceHost(), 
                config.getSourcePort(),
                config.getSourceDatabase(), 
                config.getSourceUsername(), 
                config.getSourcePassword()
            );
            
            targetConn = connectionManager.getConnection(
                config.getTargetType(), 
                config.getTargetHost(), 
                config.getTargetPort(),
                config.getTargetDatabase(), 
                config.getTargetUsername(), 
                config.getTargetPassword()
            );
            
            // Step 2: Analyze source schema
            log.info("Analyzing source schema...");
            List<DatabaseTable> sourceTables = schemaAnalyzer.analyzeSchema(
                sourceConn, config.getSourceSchema());
            result.setTotalTables(sourceTables.size());
            
            // Step 3: Generate target schema
            log.info("Generating target schema...");
            List<String> createTableStatements = sourceTables.stream()
                .map(table -> schemaGenerationService.generateCreateTableStatement(
                    table, config.getSourceType(), config.getTargetType(), 
                    config.getTargetSchema(), dataTypeMapper))
                .collect(Collectors.toList());
            
            // Step 4: Create target schema
            try (Statement stmt = targetConn.createStatement()) {
                for (String createTableStmt : createTableStatements) {
                    log.debug("Executing: {}", createTableStmt);
                    stmt.execute(createTableStmt);
                    result.setTablesCreated(result.getTablesCreated() + 1);
                }
            }
            
            // Step 5: Migrate data
            log.info("Migrating data...");
            for (DatabaseTable table : sourceTables) {
                int rowsMigrated = dataMigrationService.migrateTableData(
                    sourceConn, targetConn, table, 
                    config.getSourceSchema(), config.getTargetSchema());
                result.setTotalRowsMigrated(result.getTotalRowsMigrated() + rowsMigrated);
            }
            
            result.setSuccess(true);
            log.info("Migration completed successfully");
            
        } catch (Exception e) {
            log.error("Migration failed", e);
            result.setSuccess(false);
            result.setErrorMessage(e.getMessage());
        } finally {
            // Ensure connections are closed properly
            try {
                if (sourceConn != null && !sourceConn.isClosed()) {
                    sourceConn.close();
                }
                if (targetConn != null && !targetConn.isClosed()) {
                    targetConn.close();
                }
            } catch (SQLException e) {
                log.error("Error closing database connections", e);
            }
        }
        
        return result;
    }
}