package com._4paradigm.openmldb.featureplatform.controller;

import com._4paradigm.openmldb.featureplatform.dao.TableService;
import com._4paradigm.openmldb.featureplatform.dao.model.Entity;
import com._4paradigm.openmldb.featureplatform.dao.model.FeatureService;
import com._4paradigm.openmldb.featureplatform.dao.model.FeatureView;
import com._4paradigm.openmldb.featureplatform.dao.model.SimpleTableInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/tables")
public class TableController {

    private final TableService tableService;

    @Autowired
    public TableController(TableService tableService) {
        this.tableService = tableService;
    }

    @GetMapping
    public List<SimpleTableInfo> getTables() throws SQLException {
        return tableService.getTables();
    }

    @GetMapping("/{db}/{table}")
    public SimpleTableInfo getTable(@PathVariable String db, @PathVariable String table) throws SQLException {
        return tableService.getTable(db, table);
    }

    @GetMapping("/{db}/{table}/featureservices")
    public List<FeatureService> getRelatedFeatureServices(@PathVariable String db, @PathVariable String table) throws SQLException {
        return tableService.getRelatedFeatureServices(db, table);
    }

    @GetMapping("/{db}/{table}/featureviews")
    public List<FeatureView> getRelatedFeatureViews(@PathVariable String db, @PathVariable String table) throws SQLException {
        return tableService.getRelatedFeatureViews(db, table);
    }

}