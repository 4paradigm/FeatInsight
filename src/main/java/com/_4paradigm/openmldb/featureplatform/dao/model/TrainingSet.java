package com._4paradigm.openmldb.featureplatform.dao.model;

import lombok.Data;

@Data
public class TrainingSet {
    private int jobId;
    private String featureList;
    private String format;
    private String path;
    private String options;

    public TrainingSet() {

    }

    public TrainingSet(int jobId, String featureList, String format, String path, String options) {
        this.jobId = jobId;
        this.featureList = featureList;
        this.format = format;
        this.path = path;
        this.options = options;
    }

    public TrainingSet(String featureList, String format, String path, String options) {
       this(-1, featureList, format, path, options);
    }

}
