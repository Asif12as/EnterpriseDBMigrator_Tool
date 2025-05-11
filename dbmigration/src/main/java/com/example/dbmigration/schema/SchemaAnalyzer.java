// src/main/java/com/yourname/dbmigration/schema/SchemaAnalyzer.java

package com.yourname.dbmigration.schema;

import com.yourname.dbmigration.model.DatabaseColumn;
import com.yourname.dbmigration.model.DatabaseTable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class SchemaAnalyzer {
    
    public List<DatabaseTable> analyzeSchema(Connection connection, String schema) 
            throws SQLException {
        List<DatabaseTable> tables = new ArrayList<>();
        DatabaseMetaData metaData = connection.getMetaData();
        
        try (ResultSet tablesRS = metaData.getTables(
                connection.getCatalog(), schema, null, new String[]{"TABLE"})) {
            
            while (tablesRS.next()) {
                String tableName = tablesRS.getString("TABLE_NAME");
                log.info("Analyzing table: {}", tableName);
                
                DatabaseTable table = new DatabaseTable();
                table.setName(tableName);
                table.setColumns(analyzeColumns(metaData, connection.getCatalog(), 
                                               schema, tableName));
                
                // Get primary keys
                try (ResultSet primaryKeysRS = metaData.getPrimaryKeys(
                        connection.getCatalog(), schema, tableName)) {
                    while (primaryKeysRS.next()) {
                        String columnName = primaryKeysRS.getString("COLUMN_NAME");
                        table.getPrimaryKeys().add(columnName);
                    }
                }
                
                // Get foreign keys
                try (ResultSet foreignKeysRS = metaData.getImportedKeys(
                        connection.getCatalog(), schema, tableName)) {
                    while (foreignKeysRS.next()) {
                        String fkColumnName = foreignKeysRS.getString("FKCOLUMN_NAME");
                        String pkTableName = foreignKeysRS.getString("PKTABLE_NAME");
                        String pkColumnName = foreignKeysRS.getString("PKCOLUMN_NAME");
                        
                        table.getForeignKeys().put(fkColumnName, 
                                             new String[]{pkTableName, pkColumnName});
                    }
                }
                
                tables.add(table);
            }
        }
        
        return tables;
    }
    
    private List<DatabaseColumn> analyzeColumns(DatabaseMetaData metaData, 
                                              String catalog, String schema, 
                                              String tableName) throws SQLException {
        List<DatabaseColumn> columns = new ArrayList<>();
        
        try (ResultSet columnsRS = metaData.getColumns(catalog, schema, tableName, null)) {
            while (columnsRS.next()) {
                DatabaseColumn column = new DatabaseColumn();
                column.setName(columnsRS.getString("COLUMN_NAME"));
                column.setDataType(columnsRS.getInt("DATA_TYPE"));
                column.setTypeName(columnsRS.getString("TYPE_NAME"));
                column.setSize(columnsRS.getInt("COLUMN_SIZE"));
                column.setNullable(columnsRS.getInt("NULLABLE") == DatabaseMetaData.columnNullable);
                column.setDefault(columnsRS.getString("COLUMN_DEF"));
                
                columns.add(column);
            }
        }
        
        return columns;
    }
}