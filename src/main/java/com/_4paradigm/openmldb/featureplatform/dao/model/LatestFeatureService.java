package com._4paradigm.openmldb.featureplatform.dao.model;

import lombok.Data;

@Data
public class LatestFeatureService {
    private String name;
    private String version;
    private String db;
    private String deployment;

    public LatestFeatureService(String name, String version, String db, String deployment) {
        this.name = name;
        this.version = version;
        this.db = db;
        this.deployment = deployment;
    }
}
