package com._4paradigm.openmldb.featureplatform.dao.model;

import lombok.Data;

@Data
public class FeatureService {
    private String name;
    private String version;
    private String featureList;
    private String db;
    private String sql;
    private String deployment;
    private String description;

    public FeatureService() {

    }

    public FeatureService(String name, String version, String featureList) {
        this(name, version, featureList, "");
    }

    public FeatureService(String name, String version, String featureList, String description) {
        this.name = name;
        this.version = version;
        this.featureList = featureList;
        this.description = description;
    }

    public FeatureService(String name, String version, String featureList, String db, String sql, String deployment, String description) {
        this.name = name;
        this.version = version;
        this.featureList = featureList;
        this.db = db;
        this.sql = sql;
        this.deployment = deployment;
        this.description = description;
    }
}
