package com._4paradigm.openmldb.featureplatform.dao.model;

import lombok.Data;

@Data
public class UpdateLatestVersionRequest {
    private String version;

    public UpdateLatestVersionRequest() {

    }

    public UpdateLatestVersionRequest(String version) {
        this.version = version;
    }
}
