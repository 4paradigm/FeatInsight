package com._4paradigm.openmldb.featureplatform.controller;

import com._4paradigm.openmldb.featureplatform.service.FeatureViewService;
import com._4paradigm.openmldb.featureplatform.dao.model.FeatureView;
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
@RequestMapping("/api/featureviews")
public class FeatureViewController {
    private static final Logger logger = LoggerFactory.getLogger(FeatureController.class);
    private final FeatureViewService featureViewService;

    @Autowired
    public FeatureViewController(FeatureViewService featureViewService) {
        this.featureViewService = featureViewService;
    }

    @GetMapping
    public List<FeatureView> getFeatureViews() throws SQLException {
        try {
            return featureViewService.getFeatureViews();
        } catch (SQLException e) {
            logger.info(String.format("Call getFeatureViews but get exception: %s", e.getMessage()));
            throw e;
        }
    }

    @GetMapping("/{name}")
    public ResponseEntity<FeatureView> getFeatureViewByName(@PathVariable String name) throws SQLException {
        try {
            return ResponseEntity.ok(featureViewService.getFeatureViewByName(name));
        } catch (SQLException e) {
            logger.info(String.format("Call getFeatureViewByName with %s but get exception: %s", name, e.getMessage()));
            throw e;
        }
    }

    @PostMapping
    public FeatureView addFeatureView(@RequestBody FeatureView featureView) throws SQLException {
        try {
            return featureViewService.createFeatureView(featureView);
        } catch (SQLException e) {
            logger.info(String.format("Call addFeatureView with %s but get exception: %s", featureView, e.getMessage()));
            throw e;
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<String> validateFeatureView(@RequestBody FeatureView featureView) throws SQLException {
        try {
            List<String> featureNames = featureViewService.getOutputFeatureNames(featureView);
            String featureNamesString = String.join(",", featureNames);

            return new ResponseEntity<>(featureNamesString, HttpStatus.OK);
        } catch (SQLException e) {
            logger.info(String.format("Call validateFeatureView with %s but get exception: %s", featureView, e.getMessage()));
            throw e;
        }
    }

    @DeleteMapping("/{name}")
    public void deleteFeatureView(@PathVariable String name) throws SQLException {
        try {
            featureViewService.deleteFeatureView(name);
        } catch (SQLException e) {
            logger.info(String.format("Call deleteFeatureView with %s but get exception: %s", name, e.getMessage()));
            throw e;
        }
    }

    @GetMapping("/{name}/tables")
    public List<String> getFeatureViewDependentTables(@PathVariable String name) throws SQLException {
        try {
            return featureViewService.getDependentTables(name);
        } catch (SQLException e) {
            logger.info(String.format("Call getFeatureViewDependentTables with %s but get exception: %s", name, e.getMessage()));
            throw e;
        }
    }
}