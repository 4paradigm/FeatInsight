package com._4paradigm.openmldb.featureplatform.dao.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FesqlTableColumn {
    private String name;
    private String type;
}
