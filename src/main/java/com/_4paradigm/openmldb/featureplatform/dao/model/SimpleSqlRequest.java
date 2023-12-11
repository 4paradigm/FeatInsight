package com._4paradigm.openmldb.featureplatform.dao.model;

import lombok.Data;

@Data
public class SimpleSqlRequest {

    private String sql;
    private String sparkConfig = "";

    public SimpleSqlRequest() {
    }

    public SimpleSqlRequest(String sql) {
        this(sql, "");
    }

    public SimpleSqlRequest(String sql, String sparkConfig) {
        this.sql = sql;
        this.sparkConfig = sparkConfig;
    }

}
