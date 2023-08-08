package com._4paradigm.openmldb.featureplatform.controller;

import com._4paradigm.openmldb.common.Pair;
import com._4paradigm.openmldb.featureplatform.dao.model.FeatureView;
import com._4paradigm.openmldb.featureplatform.dao.FeatureViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.server.ResponseStatusException;

import java.sql.SQLException;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/featureviews")
public class FeatureViewController {

    private final FeatureViewService featureViewService;

    @Autowired
    public FeatureViewController(FeatureViewService featureViewService) {
        this.featureViewService = featureViewService;
    }

    @GetMapping
    public List<FeatureView> getFeatureViews() {
        return featureViewService.getFeatureViews();
    }

    @GetMapping("/{name}")
    public ResponseEntity<FeatureView> getFeatureViewByName(@PathVariable String name) throws SQLException {

            return ResponseEntity.ok(featureViewService.getFeatureViewByName(name));

    }

    @PostMapping
    public FeatureView addFeatureView(@RequestBody FeatureView featureView) {
        if(featureViewService.addFeatureView(featureView)) {
            return featureView;
        } else {
            return null;
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<String> validateFeatureView(@RequestBody FeatureView featureView) {
        try {
            String featureNames = featureViewService.validateFeatureView(featureView);
            return new ResponseEntity<>(featureNames, HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>("Fail to validate, exception: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<String> deleteFeatureView(@PathVariable String name) {
        if (featureViewService.deleteFeatureView(name)) {
            return new ResponseEntity<>("Success to delete", HttpStatus.OK);
        } else {
            // TODO: Handle for different error code
            return new ResponseEntity<>("Success to delete", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{name}/tables")
    public List<String> getFeatureViewDependentTables(@PathVariable String name) {
        try {
            return featureViewService.getDependentTables(name);
        } catch (SQLException e) {
            return null;
        }
    }
}