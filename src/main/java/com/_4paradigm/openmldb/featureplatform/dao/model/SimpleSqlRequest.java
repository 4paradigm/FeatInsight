package com._4paradigm.openmldb.featureplatform.dao.model;

import lombok.Data;

@Data
public class SimpleSqlRequest {

    private String sql;

    public SimpleSqlRequest() {
    }

    public SimpleSqlRequest(String sql) {
        this.sql = sql;
    }

}
