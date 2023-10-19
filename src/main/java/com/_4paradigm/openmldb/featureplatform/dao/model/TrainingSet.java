package com._4paradigm.openmldb.featureplatform.dao.model;

import lombok.Data;

@Data
public class TrainingSet {

    private String featureNames;
    private String path;
    private String options;
    private String joinKeys;
    private int jobId;
    private String db;
    private String sql;

    public TrainingSet() {

    }

    public TrainingSet(String featureNames, String path, String options, String joinKeys, int jobId, String db,
                       String sql) {
        this.featureNames = featureNames;
        this.path = path;
        this.options = options;
        this.joinKeys = joinKeys;
        this.jobId = jobId;
        this.db = db;
        this.sql = sql;
    }

    // Request data to create training set will provide joinKeys
    public TrainingSet(String featureNames, String path, String options, String joinKeys) {
       this(featureNames, path, options, joinKeys, -1, "", "");
    }

    public TrainingSet(String featureNames, String path, String options, int jobId, String db, String sql) {
        this(featureNames, path, options, "", jobId, db, sql);
    }

}
