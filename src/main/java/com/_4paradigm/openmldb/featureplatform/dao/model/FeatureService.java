package com._4paradigm.openmldb.featureplatform.dao.model;

import lombok.Data;

@Data
public class FeatureService {
    private String name;
    private String version;
    private String featureNames;
    private String description;
    private String joinKeys;
    private String db;
    private String sql;
    private String deployment;

    public FeatureService() {

    }

    public FeatureService(String name, String version, String featureNames, String description, String joinKeys, String db, String sql, String deployment) {
        this.name = name;
        this.version = version;
        this.featureNames = featureNames;
        this.description = description;
        this.joinKeys = joinKeys;
        this.db = db;
        this.sql = sql;
        this.deployment = deployment;
    }

    public FeatureService(String name, String version, String featureNames, String description, String joinKeys) {
        this(name, version, featureNames, description, joinKeys, "", "", "");
    }

    public FeatureService(String name, String version, String featureNames, String description, String db, String sql,
                          String deployment) {
        this(name, version, featureNames, description, "", db, sql, deployment);
    }


}
