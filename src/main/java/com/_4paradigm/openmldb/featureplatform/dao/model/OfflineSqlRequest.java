package com._4paradigm.openmldb.featureplatform.dao.model;

import lombok.Data;

@Data
public class OfflineSqlRequest {

    private String sql;
    private String sparkConfig;

    public OfflineSqlRequest() {
    }

    public OfflineSqlRequest(String sql, String sparkConfig) {
        this.sql = sql;
        this.sparkConfig = sparkConfig;
    }

}
