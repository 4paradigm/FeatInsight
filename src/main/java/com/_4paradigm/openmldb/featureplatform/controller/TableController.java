package com._4paradigm.openmldb.featureplatform.controller;

import com._4paradigm.openmldb.featureplatform.dao.model.FeatureService;
import com._4paradigm.openmldb.featureplatform.dao.model.FeatureView;
import com._4paradigm.openmldb.featureplatform.dao.model.SimpleTableInfo;
import com._4paradigm.openmldb.featureplatform.service.TableService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/tables")
public class TableController {
    private static final Logger logger = LoggerFactory.getLogger(FeatureController.class);

    private final TableService tableService;

    @Autowired
    public TableController(TableService tableService) {
        this.tableService = tableService;
    }

    @GetMapping
    public List<SimpleTableInfo> getTables() throws SQLException {
        try {
            return tableService.getTables();
        } catch (SQLException e) {
            logger.info(String.format("Call getTables but get exception: %s", e.getMessage()));
            throw e;
        }
    }

    @GetMapping("/{db}/{table}")
    public SimpleTableInfo getTable(@PathVariable String db, @PathVariable String table) throws SQLException {
        try {
            return tableService.getTable(db, table);
        } catch (SQLException e) {
            logger.info(String.format("Call getTable with %s and %s but get exception: %s", db, table, e.getMessage()));
            throw e;
        }
    }

    @GetMapping("/{db}/{table}/schema")
    public String getTableSchema(@PathVariable String db, @PathVariable String table) throws SQLException {
        try {
            return tableService.getTableSchema(db, table);
        } catch (SQLException e) {
            logger.info(String.format("Call getTableSchema with %s and %s but get exception: %s", db, table, e.getMessage()));
            throw e;
        }
    }

    @GetMapping("/{db}/{table}/featureservices")
    public List<FeatureService> getRelatedFeatureServices(@PathVariable String db, @PathVariable String table) throws SQLException {
        try {
            return tableService.getRelatedFeatureServices(db, table);
        } catch (SQLException e) {
            logger.info(String.format("Call getRelatedFeatureServices with %s and %s but get exception: %s", db, table, e.getMessage()));
            throw e;
        }
    }

    @GetMapping("/{db}/{table}/featureviews")
    public List<FeatureView> getRelatedFeatureViews(@PathVariable String db, @PathVariable String table) throws SQLException {
        try {
            return tableService.getRelatedFeatureViews(db, table);
        } catch (SQLException e) {
            logger.info(String.format("Call getRelatedFeatureViews with %s and %s but get exception: %s", db, table, e.getMessage()));
            throw e;
        }
    }

    @DeleteMapping("/{db}/{table}")
    public void deleteTable(@PathVariable String db, @PathVariable String table) throws SQLException {
        try {
            tableService.deleteTable(db, table);
        } catch (SQLException e) {
            logger.info(String.format("Call deleteTable with %s and table but get exception: %s", db, table, e.getMessage()));
            throw e;
        }
    }

    @GetMapping("/{db}/{table}/indexes")
    public ResponseEntity<List<String>> getIndexNames(@PathVariable String db, @PathVariable String table) throws SQLException {
        try {
            return new ResponseEntity<>(tableService.getIndexNames(db, table), HttpStatus.OK);
        } catch (SQLException e) {
            logger.info(String.format("Call getIndexNames with %s and %s but get exception: %s", db, table, e.getMessage()));
            throw new SQLException(e.getMessage());
        }
    }

}