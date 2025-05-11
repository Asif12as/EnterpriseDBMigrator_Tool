// src/main/java/com/example/demo/service/SchemaGenerationService.java

package com.example.demo.service;

import com.example.demo.mapping.DataTypeMapper;
import com.example.demo.model.DatabaseColumn;
import com.example.demo.model.DatabaseTable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SchemaGenerationService {

    public String generateCreateTableStatement(DatabaseTable table, String sourceDbType, 
                                            String targetDbType, String targetSchema, 
                                            DataTypeMapper dataTypeMapper) {
        StringBuilder sb = new StringBuilder();
        String tableName = table.getName();
        String schemaPrefix = targetSchema != null && !targetSchema.isEmpty() ? 
                             targetSchema + "." : "";
        
        sb.append("CREATE TABLE ").append(schemaPrefix).append(tableName).append(" (\n");
        
        List<String> columnDefinitions = table.getColumns().stream()
            .map(column -> generateColumnDefinition(column, sourceDbType, 
                                                  targetDbType, dataTypeMapper))
            .collect(Collectors.toList());
        
        sb.append(String.join(",\n", columnDefinitions));
        
        // Add primary key constraint if exists
        if (!table.getPrimaryKeys().isEmpty()) {
            sb.append(",\n");
            sb.append("PRIMARY KEY (")
              .append(String.join(", ", table.getPrimaryKeys()))
              .append(")");
        }
        
        // Add foreign key constraints if exist
        if (!table.getForeignKeys().isEmpty()) {
            for (String fkColumn : table.getForeignKeys().keySet()) {
                String[] fkInfo = table.getForeignKeys().get(fkColumn);
                String pkTable = fkInfo[0];
                String pkColumn = fkInfo[1];
                
                sb.append(",\n");
                sb.append("FOREIGN KEY (").append(fkColumn).append(") ")
                  .append("REFERENCES ").append(schemaPrefix).append(pkTable)
                  .append("(").append(pkColumn).append(")");
            }
        }
        
        sb.append("\n)");
        
        // Add any database specific table options
        if ("mysql".equalsIgnoreCase(targetDbType)) {
            sb.append(" ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
        }
        
        sb.append(";");
        
        log.debug("Generated create table statement: {}", sb.toString());
        
        return sb.toString();
    }
    
    private String generateColumnDefinition(DatabaseColumn column, String sourceDbType,
                                          String targetDbType, DataTypeMapper dataTypeMapper) {
        StringBuilder sb = new StringBuilder();
        
        // Column name
        sb.append("  ").append(column.getName()).append(" ");
        
        // Data type
        sb.append(dataTypeMapper.mapDataType(sourceDbType, targetDbType, column));
        
        // Nullability
        if (!column.isNullable()) {
            sb.append(" NOT NULL");
        }
        
        // Default value
        if (column.getDefault() != null && !column.getDefault().isEmpty()) {
            sb.append(" DEFAULT ").append(column.getDefault());
        }
        
        return sb.toString();
    }
}