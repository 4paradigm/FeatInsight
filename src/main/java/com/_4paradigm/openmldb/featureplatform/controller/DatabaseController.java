package com._4paradigm.openmldb.featureplatform.controller;

import com._4paradigm.openmldb.featureplatform.service.DatabaseService;
import com._4paradigm.openmldb.featureplatform.dao.model.SimpleTableInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.sql.SQLException;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/databases")
public class DatabaseController {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseController.class);

    private final DatabaseService databaseService;

    @Autowired
    public DatabaseController(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @GetMapping
    public List<String> getDatabases() {
        return databaseService.getDatabases();
    }

    @GetMapping("/{db}/tables")
    public ResponseEntity<List<SimpleTableInfo>> getDatabaseTables(@PathVariable String db) throws SQLException {
        try {
            return ResponseEntity.ok(databaseService.getDatabaseTables(db));
        } catch (SQLException e) {
            logger.info(String.format("Call getDatabaseTables with %s but get exception: %s", db, e.getMessage()));
            throw e;
        }
    }

    @DeleteMapping("/{db}")
    public void deleteDatabase(@PathVariable String db) throws SQLException {
        try {
            databaseService.deleteDatabase(db);
        } catch (SQLException e) {
            logger.info(String.format("Call deleteDatabase with %s but get exception: %s", db, e.getMessage()));
            throw e;
        }
    }

}