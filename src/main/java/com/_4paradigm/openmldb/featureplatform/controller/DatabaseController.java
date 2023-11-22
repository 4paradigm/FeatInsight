package com._4paradigm.openmldb.featureplatform.controller;

import com._4paradigm.openmldb.featureplatform.service.DatabaseService;
import com._4paradigm.openmldb.featureplatform.dao.model.SimpleTableInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/databases")
public class DatabaseController {

    private final DatabaseService databaseService;

    @Autowired
    public DatabaseController(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @GetMapping
    public List<String> getDatabases() throws SQLException {
        return databaseService.getDatabases();
    }

    @GetMapping("/{db}/tables")
    public ResponseEntity<List<SimpleTableInfo>> getDatabaseTables(@PathVariable String db) throws SQLException {
        return ResponseEntity.ok(databaseService.getDatabaseTables(db));
    }

    @DeleteMapping("/{db}")
    public void deleteDatabase(@PathVariable String db) throws SQLException {
        databaseService.deleteDatabase(db);
    }

}