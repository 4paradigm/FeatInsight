package com._4paradigm.openmldb.featureplatform.dao.model;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class FesqlDagMeta {
    private List<MetaNode> nodes = new ArrayList<>();
    private List<MetaSchema> schemas = new ArrayList<>();

    @Data
    public static class MetaNode {
        private String uuid;
        private String script;
        private ArrayList<String> parents = new ArrayList<>();
        private ArrayList<String> inputTables = new ArrayList<>();
        private Map<String, String> tableNameMap = new HashMap<>();
        private String nodeFlag;    // 可选：InputNode/OutputNode

    }

    @Data
    public static class MetaSchema {
        private String uuid;
        private String prn;
        private List<FesqlTableColumn> cols = new ArrayList<>();
    }
}
