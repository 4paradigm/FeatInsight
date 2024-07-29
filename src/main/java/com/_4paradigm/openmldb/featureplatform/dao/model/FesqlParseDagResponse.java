package com._4paradigm.openmldb.featureplatform.dao.model;

import lombok.Data;

@Data
public class FesqlParseDagResponse {
    private Integer status;
    private String msg;
    private FesqlConfig data;
}
