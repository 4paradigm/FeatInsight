package com._4paradigm.openmldb.featureplatform.dao.model;

import lombok.Data;

@Data
public class DatabaseTable {

    private String database;
    private String table;

    public DatabaseTable() {
    }

    public DatabaseTable(String database, String table) {
        this.database = database;
        this.table = table;
    }
}
