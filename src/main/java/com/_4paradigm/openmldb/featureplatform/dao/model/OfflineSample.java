package com._4paradigm.openmldb.featureplatform.dao.model;

import lombok.Data;

@Data
public class OfflineSample {

    private String featureNames;
    private String path;
    private String options;
    private String mainTableKeys;
    private int jobId;
    private String db;
    private String sql;

    public OfflineSample() {

    }

    public OfflineSample(String featureNames, String path, String options, String mainTableKeys, int jobId, String db,
                         String sql) {
        this.featureNames = featureNames;
        this.path = path;
        this.options = options;
        this.mainTableKeys = mainTableKeys;
        this.jobId = jobId;
        this.db = db;
        this.sql = sql;
    }

    public OfflineSample(String featureNames, String path, String options, String mainTableKeys) {
       this(featureNames, path, options, mainTableKeys, -1, "", "");
    }

    public OfflineSample(String featureNames, String path, String options, int jobId, String db, String sql) {
        this(featureNames, path, options, "", jobId, db, sql);
    }

}
