package com._4paradigm.openmldb.featureplatform.dao.model;

import lombok.Data;

@Data
public class SimpleTableInfo {
    private String db;
    private String table;
    private String schema;

    public SimpleTableInfo() {

    }

    public SimpleTableInfo(String db, String table, String schema) {
        this.db = db;
        this.table = table;
        this.schema = schema;
    }
}
