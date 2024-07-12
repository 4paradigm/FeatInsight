package com._4paradigm.openmldb.featureplatform.dao.model;

import lombok.Data;

import java.util.List;

@Data
public class FesqlConfig {
    private List<FesqlTable> tables;
    private String fesql;
}
