package com._4paradigm.openmldb.featureplatform.controller;

import com._4paradigm.openmldb.featureplatform.service.DagToolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@RestController
@RequestMapping("/api/dag_conversion")
public class DagToolController {

    private final DagToolService dagToolService;

    @Autowired
    public DagToolController(DagToolService dagToolService) {
        this.dagToolService = dagToolService;
    }

    @GetMapping
    public String testDagConversion() throws SQLException {
        return "Hello DAG Conversion: /forward for DAG to SQL; /backward for SQL to DAG";
    }

    @PostMapping("/forward")
    public String converttoSQL(@RequestBody String dagJSON) throws SQLException {
        return dagToolService.convertDagtoSQL(dagJSON);
    }

    @PostMapping("/backward")
    public String converttoDAG(@RequestBody String sqlCode) throws SQLException {
        return dagToolService.convertSQLtoDag(sqlCode);
    }

}