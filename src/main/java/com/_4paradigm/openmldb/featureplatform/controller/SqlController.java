package com._4paradigm.openmldb.featureplatform.controller;

import com._4paradigm.openmldb.featureplatform.dao.model.OfflineJobInfo;
import com._4paradigm.openmldb.featureplatform.service.SqlService;
import com._4paradigm.openmldb.featureplatform.dao.model.SqlRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.sql.SQLException;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/sql")
public class SqlController {

    private final SqlService sqlService;

    @Autowired
    public SqlController(SqlService sqlService) {
        this.sqlService = sqlService;
    }

    @PostMapping("/online")
    public ResponseEntity<List<List<String>>> executeOnline(@RequestBody SqlRequest sqlRequest) throws SQLException {
        return ResponseEntity.ok(sqlService.executeOnlineSql(sqlRequest.getSql()));
    }

    @PostMapping("/offline")
    public ResponseEntity<OfflineJobInfo> executeOffline(@RequestBody SqlRequest sqlRequest) throws SQLException {
        System.out.println("----------------- tobe111");
        return ResponseEntity.ok(sqlService.executeOfflineSql(sqlRequest.getSql()));
    }

    @PostMapping("/validate")
    public ResponseEntity<String> validateSql(@RequestBody SqlRequest sqlRequest) throws SQLException {
        List<String> result = sqlService.validateSql(sqlRequest.getSql());
        if (result.size() == 0) {
            return new ResponseEntity<>("Success to validate sql", HttpStatus.OK);
        } else {
            return new ResponseEntity<>(String.join(",", result), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/import")
    public int importSql(@RequestBody SqlRequest sqlRequest) throws SQLException {
        int jobId = sqlService.importData(sqlRequest.getSql(), false);
        return jobId;
    }

}