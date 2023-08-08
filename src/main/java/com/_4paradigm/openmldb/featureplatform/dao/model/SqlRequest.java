package com._4paradigm.openmldb.featureplatform.dao.model;

import lombok.Data;

@Data
public class SqlRequest {
    private String sql;

    public SqlRequest() {

    }

    public SqlRequest(String sql) {
        this.sql = sql;
    }
}
