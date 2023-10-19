package com._4paradigm.openmldb.featureplatform.dao.model;

import lombok.Data;

@Data
public class SqlRequest {

    private String sql;
    private boolean isOnline;

    public SqlRequest() {

    }

    public SqlRequest(String sql, boolean isOnline) {
        this.sql = sql;
        this.isOnline = isOnline;
    }

    public SqlRequest(String sql) {
        this(sql, true);
    }

}
