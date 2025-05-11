// src/main/java/com/yourname/dbmigration/connection/DatabaseConnectionManager.java

package com.yourname.dbmigration.connection;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class DatabaseConnectionManager {
    
    private final Map<String, Connection> connectionPool = new HashMap<>();
    
    public Connection getConnection(String dbType, String host, int port, 
                                   String database, String username, String password) 
                                   throws SQLException {
        
        String connectionKey = String.format("%s-%s-%d-%s", dbType, host, port, database);
        
        if (connectionPool.containsKey(connectionKey) && 
            !connectionPool.get(connectionKey).isClosed()) {
            return connectionPool.get(connectionKey);
        }
        
        String jdbcUrl;
        switch (dbType.toLowerCase()) {
            case "mysql":
                jdbcUrl = String.format("jdbc:mysql://%s:%d/%s", host, port, database);
                break;
            case "postgresql":
                jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s", host, port, database);
                break;
            case "oracle":
                jdbcUrl = String.format("jdbc:oracle:thin:@%s:%d:%s", host, port, database);
                break;
            case "sqlserver":
                jdbcUrl = String.format("jdbc:sqlserver://%s:%d;databaseName=%s", 
                                        host, port, database);
                break;
            default:
                throw new IllegalArgumentException("Unsupported database type: " + dbType);
        }
        
        log.info("Establishing connection to {} database at {}:{}/{}",
                dbType, host, port, database);
        
        Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
        connectionPool.put(connectionKey, connection);
        
        return connection;
    }
    
    public void closeAllConnections() {
        for (Map.Entry<String, Connection> entry : connectionPool.entrySet()) {
            try {
                log.info("Closing database connection: {}", entry.getKey());
                entry.getValue().close();
            } catch (SQLException e) {
                log.error("Error closing connection: {}", entry.getKey(), e);
            }
        }
        connectionPool.clear();
    }
}