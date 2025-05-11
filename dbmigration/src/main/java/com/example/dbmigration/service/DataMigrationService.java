// src/main/java/com/example/demo/service/DataMigrationService.java

package com.example.demo.service;

import com.example.demo.model.DatabaseColumn;
import com.example.demo.model.DatabaseTable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DataMigrationService {

    @Value("${migration.batch-size:1000}")
    private int batchSize;
    
    public int migrateTableData(Connection sourceConn, Connection targetConn,
                              DatabaseTable table, String sourceSchema, String targetSchema)
                             throws SQLException {
        String tableName = table.getName();
        log.info("Migrating data for table: {}", tableName);
        
        String sourceSchemaPrefix = sourceSchema != null && !sourceSchema.isEmpty() ? 
                                   sourceSchema + "." : "";
        String targetSchemaPrefix = targetSchema != null && !targetSchema.isEmpty() ? 
                                   targetSchema + "." : "";
        
        List<String> columnNames = table.getColumns().stream()
            .map(DatabaseColumn::getName)
            .collect(Collectors.toList());
        
        String columnsStr = String.join(", ", columnNames);
        String placeholders = columnNames.stream()
            .map(col -> "?")
            .collect(Collectors.joining(", "));
        
        String selectSql = "SELECT " + columnsStr + " FROM " + sourceSchemaPrefix + tableName;
        String insertSql = "INSERT INTO " + targetSchemaPrefix + tableName + 
                          " (" + columnsStr + ") VALUES (" + placeholders + ")";
        
        int totalRowsMigrated = 0;
        
        try (Statement selectStmt = sourceConn.createStatement(
                 ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
             ResultSet rs = selectStmt.executeQuery(selectSql);
             PreparedStatement insertStmt = targetConn.prepareStatement(insertSql)) {
            
            // Get column metadata from result set
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            
            targetConn.setAutoCommit(false);
            
            List<Object[]> batch = new ArrayList<>(batchSize);
            
            while (rs.next()) {
                Object[] row = new Object[columnCount];
                
                // Extract data from the result set
                for (int i = 1; i <= columnCount; i++) {
                    row[i-1] = rs.getObject(i);
                }
                
                batch.add(row);
                
                // If batch is full or this is the last row, execute batch
                if (batch.size() >= batchSize) {
                    totalRowsMigrated += executeBatch(insertStmt, batch, columnCount);
                    batch.clear();
                }
            }
            
            // Process any remaining rows
            if (!batch.isEmpty()) {
                totalRowsMigrated += executeBatch(insertStmt, batch, columnCount);
            }
            
            targetConn.commit();
            log.info("Migrated {} rows for table {}", totalRowsMigrated, tableName);
        } catch (SQLException e) {
            targetConn.rollback();
            log.error("Error migrating data for table {}: {}", tableName, e.getMessage());
            throw e;
        } finally {
            targetConn.setAutoCommit(true);
        }
        
        return totalRowsMigrated;
    }
    
    private int executeBatch(PreparedStatement stmt, List<Object[]> batch, int columnCount) 
            throws SQLException {
        for (Object[] row : batch) {
            for (int i = 0; i < columnCount; i++) {
                stmt.setObject(i + 1, row[i]);
            }
            stmt.addBatch();
        }
        
        int[] results = stmt.executeBatch();
        return countSuccessfulInserts(results);
    }
    
    private int countSuccessfulInserts(int[] results) {
        int count = 0;
        for (int result : results) {
            if (result >= 0 || result == Statement.SUCCESS_NO_INFO) {
                count++;
            }
        }
        return count;
    }
}