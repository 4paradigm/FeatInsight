package com._4paradigm.openmldb.featureplatform.dao.model;

import lombok.Data;

@Data
public class Entity {
    private String name;
    private String primaryKeys;

    public Entity() {

    }

    public Entity(String name, String primaryKeys) {
        this.name = name;
        this.primaryKeys = primaryKeys;
    }
}
