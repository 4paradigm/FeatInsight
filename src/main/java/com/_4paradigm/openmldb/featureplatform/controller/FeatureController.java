package com._4paradigm.openmldb.featureplatform.controller;

import com._4paradigm.openmldb.featureplatform.service.FeaturesService;
import com._4paradigm.openmldb.featureplatform.dao.model.Feature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.sql.SQLException;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/features")
public class FeatureController {
    private static final Logger logger = LoggerFactory.getLogger(FeatureController.class);

    private final FeaturesService featureService;

    @Autowired
    public FeatureController(FeaturesService featureService) {
        this.featureService = featureService;
    }

    @GetMapping
    public List<Feature> getFeatures(@RequestParam(value = "featureServiceName", required = false) String featureServiceName, @RequestParam(value = "featureServiceVersion", required = false) String featureServiceVersion) throws SQLException {
        try {
            if (featureServiceName == null) {
                return featureService.getFeatures();
            } else {
                if (featureServiceVersion == null) {
                    return featureService.getFeaturesByFeatureService(featureServiceName);
                } else {
                    return featureService.getFeaturesByFeatureServiceAndVersion(featureServiceName, featureServiceVersion);
                }
            }
        } catch (SQLException e) {
            logger.info(String.format("Call getFeatures with %s and %s but get exception: %s", featureServiceName, featureServiceVersion, e.getMessage()));
            throw e;

        }
    }

    @GetMapping("/{feature_view_name}")
    public List<Feature> getFeaturesByFeatureView(@PathVariable String feature_view_name) throws SQLException {
        try {
            return featureService.getFeaturesByFeatureView(feature_view_name);
        } catch (SQLException e) {
            logger.info(String.format("Call getFeaturesByFeatureView with %s but get exception: %s", feature_view_name, e.getMessage()));
            throw e;
        }
    }

    @GetMapping("/{feature_view_name}/{feature_name}")
    public Feature getFeatureViewByName(@PathVariable String feature_view_name, @PathVariable String feature_name) throws SQLException {
        try {
            return featureService.getFeature(feature_view_name, feature_name);
        } catch (SQLException e) {
            logger.info(String.format("Call getFeatureViewByName with %s and %s but get exception: %s", feature_view_name, feature_name, e.getMessage()));
            throw e;
        }
    }

    @GetMapping(value = "/{feature_view_name}/{feature_name}/sourcesql")
    public ResponseEntity<String> getSourceSql(@PathVariable String feature_view_name, @PathVariable String feature_name) throws SQLException {
        try {
            return ResponseEntity.ok(featureService.getSourceSql(feature_view_name, feature_name));
        } catch (Exception e) {
            logger.info(String.format("Call requestOnlineQueryModeSamples with %s and %s but get exception: %s", feature_view_name, feature_name, e.getMessage()));
            throw new SQLException(e.getMessage());
        }
    }

    @PostMapping(value = "/{feature_view_name}/{feature_name}/onlinequerymode/samples")
    public ResponseEntity<List<List<String>>> requestOnlineQueryModeSamples(@PathVariable String feature_view_name, @PathVariable String feature_name) throws SQLException {
        try {
            return ResponseEntity.ok(featureService.requestOnlineQueryModeSamples(feature_view_name, feature_name));
        } catch (Exception e) {
            logger.info(String.format("Call requestOnlineQueryModeSamples with %s and %s but get exception: %s", feature_view_name, feature_name, e.getMessage()));
            throw new SQLException(e.getMessage());
        }
    }

}