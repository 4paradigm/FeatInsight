package com._4paradigm.openmldb.featureplatform.dao.model;

import lombok.Data;

@Data
public class SqlRequest {

    private String sql;
    private boolean online;
    private String sparkConfig;

    public SqlRequest() {
    }

    public SqlRequest(String sql, boolean online, String sparkConfig) {
        this.sql = sql;
        this.online = online;
        this.sparkConfig = sparkConfig;
    }

    public SqlRequest(String sql, boolean online) {
        this(sql, online, "");
    }

}
