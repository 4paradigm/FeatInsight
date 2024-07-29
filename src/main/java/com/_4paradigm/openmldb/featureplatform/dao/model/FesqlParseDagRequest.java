package com._4paradigm.openmldb.featureplatform.dao.model;

import lombok.Data;

import java.util.List;

@Data
public class FesqlParseDagRequest {
    private Dag dag;

    @Data
    public static class Dag {
        private String id;
        private Integer version;
        private String schemaVersion;
        private String protocolVersion;
        private String status;
        private List<Node> nodes;
        private List<Edge> edges;
    }

    @Data
    public static class Node {
        private String id;
        private String name;
        private String operatorName;
        private String classify;
        private String title;
        private String type;
        private String subType;
        private String version;
        private String status;
        private NodeConfig config;
        private List<Slot> inputSlots;
        private List<Slot> outputSlots;
    }

    @Data
    public static class NodeConfig {
        private String sqlStatement;
    }

    @Data
    public static class Slot {
        private String id;
        private String name;
        private String elementType;
        private List<SlotElement> elements;
    }

    @Data
    public static class SlotElement {
        private String prn;
    }

    @Data
    public static class Edge {
        private String id;
        private String srcNodeId;
        private String destNodeId;
        private Integer srcNodeSlotIndex;
        private Integer destNodeSlotIndex;
        private String srcSlotId;
        private String destSlotId;
    }
}
