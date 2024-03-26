package com._4paradigm.openmldb.featureplatform.dao.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class FeatureView {
    private String name;
    private String db;
    private String sql;
    private String description;
    private List<String> featureNames;
    private Map<String, String> featureDescriptionMap;

    public FeatureView() {

    }

    public FeatureView(String name, String db, String sql, String description, List<String> featureNames, Map<String, String> featureDescriptionMap) {
        this.name = name;
        this.db = db;
        this.sql = sql;
        this.description = description;
        this.featureNames = featureNames;
        this.featureDescriptionMap = featureDescriptionMap;
    }
}
