package com._4paradigm.openmldb.featureplatform.dao.model;

import lombok.Data;

@Data
public class TableJob {
    private int jobId;
    private String jobType;
    private String db;
    private String table;
    private String sql;
}
