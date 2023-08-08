package com._4paradigm.openmldb.featureplatform.dao.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class FeatureView {
    private String name;
    private String entityNames;
    private String db;
    private String sql;
    private String featureNames;
    private String description;
    private Map<String, String> featureDescriptionMap;

    public FeatureView() {

    }

    public FeatureView(String name, String entityNames, String db, String sql) {
        this.name = name;
        this.entityNames = entityNames;
        this.db = db;
        this.sql = sql;
        this.featureNames = "";
        this.description = "";
        this.featureDescriptionMap = new HashMap<>();
    }

    public FeatureView(String name, String entityNames, String db, String sql, Map<String, String> featureDescriptionMap) {
        this.name = name;
        this.entityNames = entityNames;
        this.db = db;
        this.sql = sql;
        this.featureNames = "";
        this.description = "";
        this.featureDescriptionMap = featureDescriptionMap;
    }

    public FeatureView(String name, String entityNames, String db, String sql, String featureNames) {
        this.name = name;
        this.entityNames = entityNames;
        this.db = db;
        this.sql = sql;
        this.featureNames = featureNames;
        this.description = "";
        this.featureDescriptionMap = new HashMap<>();
    }

    public FeatureView(String name, String entityNames, String db, String sql, String featureNames, String description) {
        this.name = name;
        this.entityNames = entityNames;
        this.db = db;
        this.sql = sql;
        this.featureNames = featureNames;
        this.description = description;
        this.featureDescriptionMap = new HashMap<>();
    }
}
