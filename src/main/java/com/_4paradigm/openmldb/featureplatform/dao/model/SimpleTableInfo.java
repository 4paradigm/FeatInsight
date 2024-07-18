package com._4paradigm.openmldb.featureplatform.dao.model;

import lombok.Data;

import java.util.List;

@Data
public class SimpleTableInfo {
    private String id;
    private String db;
    private String table;
    private String schema;
    private Integer replica;
    private Integer partition;
    private Integer partitionUnalive;
    private Long rows;
    private Double useMemory;
    private List<String> columnKey;

    public SimpleTableInfo() {

    }

    public SimpleTableInfo(String db, String table, String schema) {
        this.db = db;
        this.table = table;
        this.schema = schema;
    }
}
