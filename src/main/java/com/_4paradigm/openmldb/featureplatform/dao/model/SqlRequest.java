package com._4paradigm.openmldb.featureplatform.dao.model;

import lombok.Data;

@Data
public class SqlRequest {

    private String sql;
    private boolean online;

    public SqlRequest() {
    }

    public SqlRequest(String sql, boolean online) {
        this.sql = sql;
        this.online = online;

        System.out.println("call tobe2");
    }

    public SqlRequest(String sql) {
        this(sql, true);

        System.out.println("call tobe3");
    }

}
