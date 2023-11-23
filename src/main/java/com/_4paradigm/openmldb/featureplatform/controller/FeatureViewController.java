package com._4paradigm.openmldb.featureplatform.controller;

import com._4paradigm.openmldb.featureplatform.service.FeatureViewService;
import com._4paradigm.openmldb.featureplatform.dao.model.FeatureView;
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

    private final FeatureViewService featureViewService;

    @Autowired
    public FeatureViewController(FeatureViewService featureViewService) {
        this.featureViewService = featureViewService;
    }

    @GetMapping
    public List<FeatureView> getFeatureViews() throws SQLException {
        return featureViewService.getFeatureViews();
    }

    @GetMapping("/{name}")
    public ResponseEntity<FeatureView> getFeatureViewByName(@PathVariable String name) throws SQLException {
        return ResponseEntity.ok(featureViewService.getFeatureViewByName(name));
    }

    @PostMapping
    public FeatureView addFeatureView(@RequestBody FeatureView featureView) throws SQLException {
        return featureViewService.createFeatureView(featureView);
    }

    @PostMapping("/validate")
    public ResponseEntity<String> validateFeatureView(@RequestBody FeatureView featureView) throws SQLException {
        List<String> featureNames = featureViewService.getOutputFeatureNames(featureView);
        String featureNamesString = String.join(",", featureNames);
        return new ResponseEntity<>(featureNamesString, HttpStatus.OK);
    }

    @DeleteMapping("/{name}")
    public void deleteFeatureView(@PathVariable String name) throws SQLException {
        featureViewService.deleteFeatureView(name);
    }

    @GetMapping("/{name}/tables")
    public List<String> getFeatureViewDependentTables(@PathVariable String name) throws SQLException {
        return featureViewService.getDependentTables(name);
    }
}