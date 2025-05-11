// src/main/java/com/yourname/dbmigration/mapping/DataTypeMapper.java

package com.yourname.dbmigration.mapping;

import com.yourname.dbmigration.model.DatabaseColumn;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class DataTypeMapper {
    
    private final Map<String, Map<Integer, String>> typeConversionMap;
    
    public DataTypeMapper() {
        typeConversionMap = new HashMap<>();
        initializeMySQLToOracleMapping();
        initializePostgreSQLToOracleMapping();
        // Add other mappings as needed
    }
    
    private void initializeMySQLToOracleMapping() {
        Map<Integer, String> mysqlToOracle = new HashMap<>();
        mysqlToOracle.put(Types.VARCHAR, "VARCHAR2(%d)");
        mysqlToOracle.put(Types.CHAR, "CHAR(%d)");
        mysqlToOracle.put(Types.LONGVARCHAR, "CLOB");
        mysqlToOracle.put(Types.NUMERIC, "NUMBER(%d,%d)");
        mysqlToOracle.put(Types.DECIMAL, "NUMBER(%d,%d)");
        mysqlToOracle.put(Types.INTEGER, "NUMBER(10,0)");
        mysqlToOracle.put(Types.SMALLINT, "NUMBER(5,0)");
        mysqlToOracle.put(Types.FLOAT, "FLOAT(%d)");
        mysqlToOracle.put(Types.DOUBLE, "FLOAT(24)");
        mysqlToOracle.put(Types.DATE, "DATE");
        mysqlToOracle.put(Types.TIME, "TIMESTAMP");
        mysqlToOracle.put(Types.TIMESTAMP, "TIMESTAMP");
        mysqlToOracle.put(Types.BOOLEAN, "NUMBER(1,0)");
        mysqlToOracle.put(Types.BLOB, "BLOB");
        mysqlToOracle.put(Types.CLOB, "CLOB");
        
        typeConversionMap.put("mysql-oracle", mysqlToOracle);
    }
    
    private void initializePostgreSQLToOracleMapping() {
        Map<Integer, String> postgresqlToOracle = new HashMap<>();
        // Similar mappings for PostgreSQL to Oracle
        
        typeConversionMap.put("postgresql-oracle", postgresqlToOracle);
    }
    
    public String mapDataType(String sourceDb, String targetDb, DatabaseColumn column) {
        String mappingKey = sourceDb.toLowerCase() + "-" + targetDb.toLowerCase();
        Map<Integer, String> mappings = typeConversionMap.get(mappingKey);
        
        if (mappings == null) {
            log.warn("No mapping found for {} to {}", sourceDb, targetDb);
            return column.getTypeName(); // Default to source type name
        }
        
        String targetType = mappings.get(column.getDataType());
        if (targetType == null) {
            log.warn("No mapping found for type {} when converting from {} to {}", 
                    column.getTypeName(), sourceDb, targetDb);
            return column.getTypeName();
        }
        
        // Format the target type with size if needed
        if (targetType.contains("%d")) {
            if (targetType.contains(",%d")) {
                // For types like NUMBER(p,s)
                return String.format(targetType, column.getSize(), 0); // Default scale to 0
            } else {
                // For types like VARCHAR2(n)
                return String.format(targetType, column.getSize());
            }
        }
        
        return targetType;
    }
}