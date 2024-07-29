package com._4paradigm.openmldb.featureplatform.dao.model;

import lombok.Data;

import java.util.List;

@Data
public class FesqlTable {
    private String id;
    private String db;
    private String table;
    private List<FesqlTableColumn> schema;
    private Integer replica;
    private Integer partition;
    private Integer partitionUnalive;
    private String tableTag; // 可选：INPUT_TABLE
    private Long rows;
    private List<String> columnKey;
    private Double useMemory;
}
