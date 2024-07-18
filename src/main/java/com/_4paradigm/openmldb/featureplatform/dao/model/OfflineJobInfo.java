package com._4paradigm.openmldb.featureplatform.dao.model;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class OfflineJobInfo {
    private int id;
    private String jobType;
    private String state;
    private Timestamp startTime;
    private Timestamp endTime;
    private String parameter;
    private String cluster;
    private String applicationId;
    private String error;
    private String db;
    private String table;

    public OfflineJobInfo() {

    }

    public OfflineJobInfo(int id, String jobType, String state, Timestamp startTime, Timestamp endTime, String parameter, String cluster, String applicationId, String error) {
        this.id = id;
        this.jobType = jobType;
        this.state = state;
        this.startTime = startTime;
        this.endTime = endTime;
        this.parameter = parameter;
        this.cluster = cluster;
        this.applicationId = applicationId;
        this.error = error;
    }
}
