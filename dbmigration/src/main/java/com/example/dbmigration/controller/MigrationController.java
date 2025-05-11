// src/main/java/com/yourname/dbmigration/controller/MigrationController.java

package com.yourname.dbmigration.controller;

import com.yourname.dbmigration.model.MigrationConfig;
import com.yourname.dbmigration.model.MigrationResult;
import com.yourname.dbmigration.service.MigrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/migrations")
@RequiredArgsConstructor
public class MigrationController {
    
    private final MigrationService migrationService;
    
    @PostMapping
    public ResponseEntity<MigrationResult> migrateDatabases(@RequestBody MigrationConfig config) {
        log.info("Received migration request: {} to {}", 
                config.getSourceType(), config.getTargetType());
        
        MigrationResult result = migrationService.migrateDatabases(config);
        
        if (result.isSuccess()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    @GetMapping("/supported-databases")
    public ResponseEntity<String[]> getSupportedDatabases() {
        return ResponseEntity.ok(new String[]{"mysql", "postgresql", "oracle", "sqlserver"});
    }
}