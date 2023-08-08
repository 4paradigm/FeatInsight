package com._4paradigm.openmldb.featureplatform.dao.model;

import lombok.Data;

@Data
public class FeatureServiceDeploymentRequest {
    private String name;
    private String version;
    private String db;
    private String deploymentName;
    private String description;

    public FeatureServiceDeploymentRequest(String name, String version, String db, String deploymentName, String description) {
        this.name = name;
        this.version = version;
        this.db = db;
        this.deploymentName = deploymentName;
        this.description = description;
    }
}
