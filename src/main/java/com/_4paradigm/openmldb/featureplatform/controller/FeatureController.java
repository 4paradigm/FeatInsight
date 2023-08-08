package com._4paradigm.openmldb.featureplatform.controller;

import com._4paradigm.openmldb.featureplatform.dao.FeaturesService;
import com._4paradigm.openmldb.featureplatform.dao.model.Feature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/features")
public class FeatureController {

    private final FeaturesService featureService;

    @Autowired
    public FeatureController(FeaturesService featureService) {
        this.featureService = featureService;
    }

    @GetMapping
    public List<Feature> getFeatures(@RequestParam(value = "featureServiceName", required = false) String featureServiceName, @RequestParam(value = "featureServiceVersion", required = false) String featureServiceVersion) {
        if (featureServiceName == null) {
            return featureService.getFeatures();
        } else {
            if (featureServiceVersion == null) {
                return featureService.getFeaturesByFeatureService(featureServiceName);
            } else {
                return featureService.getFeaturesByFeatureServiceAndVersion(featureServiceName, featureServiceVersion);
            }
        }
    }


    @GetMapping("/{feature_view_name}")
    public List<Feature> getFeaturesByFeatureView(@PathVariable String feature_view_name) {
        return featureService.getFeaturesByFeatureView(feature_view_name);
    }

    @GetMapping("/{feature_view_name}/{feature_name}")
    public Feature getFeatureViewByName(@PathVariable String feature_view_name, @PathVariable String feature_name) {
        return featureService.getFeatureByName(feature_view_name, feature_name);
    }

}