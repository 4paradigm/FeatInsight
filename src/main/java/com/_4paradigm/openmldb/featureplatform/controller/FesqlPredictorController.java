package com._4paradigm.openmldb.featureplatform.controller;

import com._4paradigm.openmldb.featureplatform.client.ProphetTelamonClient;
import com._4paradigm.openmldb.featureplatform.dao.model.*;
import com._4paradigm.openmldb.featureplatform.exception.MsgExcepiton;
import com._4paradigm.openmldb.featureplatform.service.SqlService;
import com._4paradigm.openmldb.featureplatform.service.TableService;
import com._4paradigm.openmldb.featureplatform.utils.ProphetUtil;
import com._4paradigm.openmldb.proto.Common;
import com._4paradigm.openmldb.proto.NS;
import com._4paradigm.openmldb.sdk.DAGNode;
import com._4paradigm.openmldb.sdk.Schema;
import com._4paradigm.openmldb.sdk.impl.SqlClusterExecutor;
import com._4paradigm.openmldb.sdk.utils.AIOSUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/fesqlPredictor")
public class FesqlPredictorController {
    private static final Gson uglyGson = new Gson();
    private static final Logger logger = LoggerFactory.getLogger(FesqlPredictorController.class);
    @Value("${prophet.pdms_uri}")
    private String telnetUri;
    @Autowired
    private TableService tableService;
    @Autowired
    private SqlService sqlService;

    @PostMapping("/parseDag")
    public FesqlParseDagResponse parseDag(@RequestBody FesqlParseDagRequest dagRequest, @RequestHeader Map<String, String> headers) {
        FesqlParseDagResponse response = new FesqlParseDagResponse();
        try {
            String workspaceId = headers.get("x-prophet-workspace-id");
            if (null == workspaceId) {
                throw new MsgExcepiton("userToken或workspaceId为空，无法获取数据schema");
            }
            String dbName = String.format("aios_ws_%s", workspaceId);
            ProphetTelamonClient telamonClient = new ProphetTelamonClient(telnetUri, headers);
            if (!this.isRunSucceedByDag(dagRequest.getDag())) {
                throw new MsgExcepiton("dag未执行成功，无法获取数据schema");
            }
            FesqlParseDagRequest.Node outputNode = this.getOutputFesqlNode(dagRequest.getDag());
            if (null == outputNode) {
                throw new MsgExcepiton("输出节点为空或不唯一");
            }
            FesqlDagMeta dagMeta = this.getFesqlDagMeta(dagRequest.getDag(), outputNode, telamonClient);
            if (null == dagMeta) {
                throw new MsgExcepiton("解析dagMeta内容失败");
            }
            List<FesqlParseDagRequest.Node> isolatedfesqlTableList = this.getIsolatedFesqlNode(dagRequest.getDag(), dagMeta);
            if (!CollectionUtils.isEmpty(isolatedfesqlTableList)) {
                throw new MsgExcepiton("dag中存在孤立的fesql节点");
            }
            List<FesqlTable> fesqlTableList = this.getFesqlTableList(dagMeta, outputNode.getId(), dbName);
            fesqlTableList = this.redupFesqlTableList(fesqlTableList);
            if (CollectionUtils.isEmpty(fesqlTableList)) {
                throw new MsgExcepiton("解析mldb建表定义失败");
            }
            String fesqlString = this.getFesqlString(dagMeta, dbName);
            if (null == fesqlString) {
                throw new MsgExcepiton("解析fesql脚本失败");
            }
            FesqlConfig fesqlConfig = new FesqlConfig();
            fesqlConfig.setTables(fesqlTableList);
            fesqlConfig.setFesql(fesqlString);
            response.setStatus(0);
            response.setData(fesqlConfig);
        } catch (MsgExcepiton e) {
            logger.error("parse dag error: {}", dagRequest.getDag(), e);
            response.setStatus(1);
            response.setMsg(e.getMessage());
        } catch (Exception e) {
            logger.error("parse dag error: {}", dagRequest.getDag(), e);
            response.setStatus(1);
            response.setMsg("未知异常");
        }
        return response;
    }

    /**
     * 判断dag是否执行成功
     *
     * @param dag
     * @return
     */
    private boolean isRunSucceedByDag(FesqlParseDagRequest.Dag dag) {
        if (!"SUCCEEDED".equals(dag.getStatus())) {
            return false;
        }
        for (FesqlParseDagRequest.Node node : dag.getNodes()) {
            if (!"SUCCEEDED".equals(node.getStatus())) {
                return false;
            }
        }
        return true;
    }

    /**
     * 解析dag获取输出节点
     *
     * @param dag
     * @return
     */
    private FesqlParseDagRequest.Node getOutputFesqlNode(FesqlParseDagRequest.Dag dag) {
        List<String> srcNodeIdList = dag.getEdges().stream().map(FesqlParseDagRequest.Edge::getSrcNodeId).collect(Collectors.toList());
        List<String> destNodeIdList = dag.getEdges().stream().map(FesqlParseDagRequest.Edge::getDestNodeId).collect(Collectors.toList());
        List<String> outputNodeIdList = new ArrayList<>();
        for (String destNodeId : destNodeIdList) {
            if (!srcNodeIdList.contains(destNodeId)) {
                outputNodeIdList.add(destNodeId);
            }
        }
        if (outputNodeIdList.size() == 0) {
            return null;
        }
        List<FesqlParseDagRequest.Node> targetNodeList = dag.getNodes().stream().filter(p -> outputNodeIdList.contains(p.getId()) && "NativeFeSql".equals(p.getOperatorName())).collect(Collectors.toList());
        if (targetNodeList.size() != 1) {
            return null;
        }
        return targetNodeList.get(0);
    }


    /**
     * 解析dag获取预估fesql-meta信息
     *
     * @param dag
     * @param outputNode
     * @return
     */
    private FesqlDagMeta getFesqlDagMeta(FesqlParseDagRequest.Dag dag, FesqlParseDagRequest.Node outputNode, ProphetTelamonClient telamonClient) {
        Map<String, List<FesqlParseDagRequest.Edge>> parentEdgeMap = dag.getEdges().stream().collect(Collectors.groupingBy(FesqlParseDagRequest.Edge::getDestNodeId));
        for (List<FesqlParseDagRequest.Edge> edges : parentEdgeMap.values()) {
            edges.sort(Comparator.comparingInt(FesqlParseDagRequest.Edge::getDestNodeSlotIndex));
        }
        Map<String, FesqlParseDagRequest.Node> nodeMap = dag.getNodes().stream().collect(Collectors.toMap(FesqlParseDagRequest.Node::getId, Function.identity()));
        Map<String, FesqlParseDagRequest.Slot> slotMap = new HashMap<>();
        for (FesqlParseDagRequest.Node node : dag.getNodes()) {
            node.getOutputSlots().forEach(solt -> slotMap.put(solt.getId(), solt));
            node.getInputSlots().forEach(solt -> slotMap.put(solt.getId(), solt));
        }
        FesqlDagMeta fesqlDagMeta = new FesqlDagMeta();
        fesqlDagMeta.setNodes(new ArrayList<>());
        fesqlDagMeta.setSchemas(new ArrayList<>());
        Set<String> tablePrnList = new HashSet<>();
        Set<String> notInputNodeIds = new HashSet<>();
        List<FesqlParseDagRequest.Node> nodeProcessQueue = new ArrayList<>();
        nodeProcessQueue.add(outputNode);
        while (!nodeProcessQueue.isEmpty()) {
            FesqlDagMeta.MetaNode metaNode = new FesqlDagMeta.MetaNode();
            FesqlParseDagRequest.Node node = nodeProcessQueue.remove(0);
            metaNode.setUuid(node.getId());
            metaNode.setScript(node.getConfig().getSqlStatement());
            metaNode.setParents(new ArrayList<>());
            metaNode.setInputTables(new ArrayList<>());
            metaNode.setTableNameMap(new HashMap<>());
            List<FesqlParseDagRequest.Edge> parentNodes = parentEdgeMap.get(node.getId());
            for (FesqlParseDagRequest.Edge pEdge : parentNodes) {
                FesqlParseDagRequest.Slot srcEdge = slotMap.get(pEdge.getSrcSlotId());
                FesqlParseDagRequest.Slot destEdge = slotMap.get(pEdge.getDestSlotId());
                metaNode.getParents().add(pEdge.getSrcNodeId());
                metaNode.getInputTables().add(destEdge.getName());
                String srcTablePrn = srcEdge.getElements().get(0).getPrn();
                metaNode.getTableNameMap().put(destEdge.getName(), srcTablePrn);
                tablePrnList.add(srcTablePrn);
                FesqlParseDagRequest.Node srcNode = nodeMap.get(pEdge.getSrcNodeId());
                if ("NativeFeSql".equals(srcNode.getOperatorName())) {
                    nodeProcessQueue.add(srcNode);
                    notInputNodeIds.add(node.getId());
                }
            }
            fesqlDagMeta.getNodes().add(metaNode);
        }
        // 标记输入节点和输出节点
        for (FesqlDagMeta.MetaNode metaNode : fesqlDagMeta.getNodes()) {
            if (!notInputNodeIds.contains(metaNode.getUuid())) {
                metaNode.getNodeFlags().add("INPUT_NODE");
            }
            if (outputNode.getId().equals(metaNode.getUuid())) {
                metaNode.getNodeFlags().add("OUTPUT_NODE");
            }
        }
        // 获取fesql算子输入表的schema信息
        for (String tablePrn : tablePrnList) {
            FesqlDagMeta.MetaSchema metaSchema = new FesqlDagMeta.MetaSchema();
            metaSchema.setPrn(tablePrn);
            try {
                metaSchema.setCols(telamonClient.getTableSchema(tablePrn));
            } catch (Exception e) {
                logger.error("get table schema, error: ", e);
                throw new MsgExcepiton(String.format("查询数据表Schema失败, tablePrn: %s", tablePrn));
            }
            fesqlDagMeta.getSchemas().add(metaSchema);
        }
        return fesqlDagMeta;
    }

    /**
     * 表定义去重
     *
     * @param fesqlTableList
     * @return
     */
    private List<FesqlTable> redupFesqlTableList(List<FesqlTable> fesqlTableList) {
        Map<String, List<FesqlTable>> parentEdgeMap = fesqlTableList.stream().collect(Collectors.groupingBy(FesqlTable::getTable));
        List<FesqlTable> resFesqlTableList = new ArrayList<>();
        for (Map.Entry<String, List<FesqlTable>> entry : parentEdgeMap.entrySet()) {
            if (entry.getValue().size() > 1) {
                Map<String, String> nextTableSchema = null;
                for (FesqlTable fesqlTable : entry.getValue()) {
                    Map<String, String> currTableSchema = fesqlTable.getSchema().stream().collect(Collectors.toMap(FesqlTableColumn::getName, FesqlTableColumn::getType));
                    if (null == nextTableSchema) {
                        nextTableSchema = currTableSchema;
                    } else {
                        if (!nextTableSchema.equals(currTableSchema)) {
                            logger.error(String.format("duplicate table[%s] schema error, schema1: %s, schema2: %s", fesqlTable.getTable(), nextTableSchema, currTableSchema));
                            throw new MsgExcepiton(String.format("存在相同表名[%s]，但Schema不一致的记录", fesqlTable.getTable()));
                        }
                    }
                }
            }
            resFesqlTableList.add(entry.getValue().get(0));
        }
        return resFesqlTableList;
    }

    /**
     * 解析预估fesql-meta信息获取mldb建表信息
     *
     * @param fesqlDagMeta
     * @return
     */
    private List<FesqlTable> getFesqlTableList(FesqlDagMeta fesqlDagMeta, String outputNodeId, String dbName) {
        Map<String, FesqlDagMeta.MetaSchema> metaSchemaMap = fesqlDagMeta.getSchemas().stream().collect(Collectors.toMap(FesqlDagMeta.MetaSchema::getPrn, Function.identity()));
        List<FesqlTable> fesqlTableList = new ArrayList<>();
        for (FesqlDagMeta.MetaNode metaNode : fesqlDagMeta.getNodes()) {
            if (metaNode.getNodeFlags().contains("INPUT_NODE")) {
                for (Map.Entry<String, String> entry : metaNode.getTableNameMap().entrySet()) {
                    FesqlTable fesqlTable = new FesqlTable();
                    fesqlTable.setDb(dbName);
                    fesqlTable.setTable(entry.getKey());
                    fesqlTable.setSchema(metaSchemaMap.get(entry.getValue()).getCols());
                    fesqlTableList.add(fesqlTable);
                }
            }
        }
        Map<String, FesqlDagMeta.MetaNode> metaNodeMap = fesqlDagMeta.getNodes().stream().collect(Collectors.toMap(FesqlDagMeta.MetaNode::getUuid, Function.identity()));
        FesqlDagMeta.MetaNode inputNode = null;
        FesqlDagMeta.MetaNode nextNode = null;
        String currentNodeId = outputNodeId;
        while (true) {
            FesqlDagMeta.MetaNode currentNode = metaNodeMap.get(currentNodeId);
            if (null == currentNode) {
                inputNode = nextNode;
                break;
            } else if (currentNode.getParents().isEmpty()) {
                inputNode = currentNode;
                break;
            } else {
                currentNodeId = currentNode.getParents().get(0);
                nextNode = currentNode;
            }
        }
        // 标记输入表记录
        for (FesqlTable fesqlTable : fesqlTableList) {
            if (inputNode != null && fesqlTable.getTable().equals(inputNode.getInputTables().get(0))) {
                fesqlTable.setTableTag("REQUEST_TABLE");
            }
        }
        return fesqlTableList;
    }

    /**
     * 解析预估fesql-meta信息获取fesql脚本
     *
     * @param fesqlDagMeta
     * @return
     */
    private String getFesqlString(FesqlDagMeta fesqlDagMeta, String dbName) throws SQLException {
        String input = uglyGson.toJson(fesqlDagMeta);
        DAGNode dag = AIOSUtil.parseAIOSDAG(input);
        Map<String, Map<String, Schema>> tableSchema = AIOSUtil.parseAIOSTableSchema(input, dbName);
        String merged = SqlClusterExecutor.mergeDAGSQL(dag);
        List<String> errors = SqlClusterExecutor.validateSQLInRequest(merged, dbName, tableSchema);
        if (!errors.isEmpty()) {
            logger.error("merged sql is invalid: " + errors + "\n, merged sql: " + merged + "\n, table schema: " + tableSchema);
            return null;
        }
        return merged;
    }

    /**
     * 获取孤立的fesql节点
     *
     * @param dag
     * @param fesqlDagMeta
     */
    private List<FesqlParseDagRequest.Node> getIsolatedFesqlNode(FesqlParseDagRequest.Dag dag, FesqlDagMeta fesqlDagMeta) {
        List<String> fesqlNodeIdList = fesqlDagMeta.getNodes().stream().map(FesqlDagMeta.MetaNode::getUuid).collect(Collectors.toList());
        List<FesqlParseDagRequest.Node> isolatedFesqlNode = new ArrayList<>();
        for (FesqlParseDagRequest.Node node : dag.getNodes()) {
            if (!fesqlNodeIdList.contains(node.getId())) {
                if ("NativeFeSql".equals(node.getOperatorName())) {
                    isolatedFesqlNode.add(node);
                }
            }
        }
        return isolatedFesqlNode;
    }


    @PostMapping("/createTable")
    public FesqlCreateTableResponse createTable(@RequestBody FesqlConfig config) {
        FesqlCreateTableResponse response = new FesqlCreateTableResponse();
        List<FesqlTable> newCreateTableList = new ArrayList<>();
        try {

            for (FesqlTable cTable : config.getTables()) {
                String newDbSchema = ProphetUtil.prophetSchemaToDbSchema(cTable.getSchema());
                if (tableService.isExistTable(cTable.getDb(), cTable.getTable())) {
                    String dbSchema = tableService.getTableSchema(cTable.getDb(), cTable.getTable());
                    if (!newDbSchema.equals(dbSchema)) {
                        throw new MsgExcepiton(String.format("[%s.%s]表已存在，但Schema不一致", cTable.getDb(), cTable.getTable()));
                    }
                } else {
                    String dbSchemaForCreateTable = newDbSchema.replace(":", " ").replace(",", ", ");
                    String createTableSql = String.format("CREATE TABLE %s.%s (%s)", cTable.getDb(), cTable.getTable(), dbSchemaForCreateTable);
                    List<String> options = new ArrayList<>();
                    if (cTable.getPartition() != null && cTable.getPartition() > 0) {
                        options.add(String.format("PARTITIONNUM=%s", cTable.getPartition()));
                    }
                    if (cTable.getReplica() != null && cTable.getReplica() > 0) {
                        options.add(String.format("REPLICANUM=%s", cTable.getReplica()));
                    }
                    if (options.size() > 0) {
                        String optionsString = String.join(", ", options);
                        createTableSql = String.format("%s  OPTIONS (%s);", createTableSql, optionsString);
                    }
                    try {
                        sqlService.executeOnlineSql(createTableSql, "");
                    } catch (SQLException e) {
                        if (e.getMessage().contains("database not found")) {
                            tableService.createDB(cTable.getDb());
                            sqlService.executeOnlineSql(createTableSql, "");
                        } else {
                            throw e;
                        }
                    }
                    newCreateTableList.add(cTable);
                }
            }
            response.setStatus(0);
        } catch (MsgExcepiton e) {
            logger.error("create tables error: {}", config.getTables(), e);
            response.setStatus(1);
            response.setMsg(e.getMessage());
        } catch (SQLException e) {
            logger.error("create tables error: {}", config.getTables(), e);
            response.setStatus(1);
            response.setMsg("数据创建失败");
        } catch (Exception e) {
            logger.error("create tables error: {}", config.getTables(), e);
            response.setStatus(1);
            response.setMsg("未知异常");
        } finally {
            if (response.getStatus() != 0) {
                for (FesqlTable cTable : newCreateTableList) {
                    try {
                        tableService.deleteTable(cTable.getDb(), cTable.getTable());
                    } catch (Exception e) {
                        logger.error("clear residual table error, table: {}", cTable, e);
                    }
                }
            }
        }
        return response;
    }

    @PostMapping("/queryTable")
    public FesqlQueryTableResponse queryTable(@RequestBody FesqlConfig config) {
        FesqlQueryTableResponse response = new FesqlQueryTableResponse();
        try {
            String dbName = config.getTables().get(0).getDb();
            Map<String, FesqlTable> tableStatusMap = this.getTableStatus(dbName);
            for (FesqlTable qTable : config.getTables()) {
                String dbSchema = tableService.getTableSchema(qTable.getDb(), qTable.getTable());
                qTable.setSchema(ProphetUtil.dbSchemaToProphetSchema(dbSchema));
                NS.TableInfo tableInfo = tableService.getTableInfo(qTable.getDb(), qTable.getTable());
                qTable.setPartition(tableInfo.getPartitionNum());
                qTable.setReplica(tableInfo.getReplicaNum());
                List<String> columnKeyStringList = new ArrayList<>();
                for (Common.ColumnKey columnKey : tableInfo.getColumnKeyList()) {
                    String colNameList = String.join(",", columnKey.getColNameList());
                    String ttlString = null;
                    if (columnKey.getTtl().hasAbsTtl() && columnKey.getTtl().hasLatTtl()) {
                        ttlString = String.format("%smin&&%s", columnKey.getTtl().getAbsTtl(), columnKey.getTtl().getLatTtl());
                    } else if (columnKey.getTtl().hasAbsTtl()) {
                        ttlString = String.format("%smin", columnKey.getTtl().getAbsTtl());
                    } else if (columnKey.getTtl().hasLatTtl()) {
                        ttlString = String.format("%s", columnKey.getTtl().getLatTtl());
                    }
                    if (null != ttlString) {
                        columnKeyStringList.add(String.format("keys=[%s]:ts=%s:ttl=%s", colNameList, columnKey.getTsName(), ttlString));
                    }
                }
                qTable.setColumnKey(columnKeyStringList);

                FesqlTable tableStatus = tableStatusMap.get(String.format("%s.%s", qTable.getDb(), qTable.getTable()));
                if (null != tableStatus) {
                    qTable.setUseMemory(tableStatus.getUseMemory());
                    qTable.setRows(tableStatus.getRows());
                }
            }
            response.setData(config);
            response.setStatus(0);
        } catch (MsgExcepiton e) {
            logger.error("query tables error: {}", config.getTables(), e);
            response.setStatus(1);
            response.setMsg(e.getMessage());
        } catch (SQLException e) {
            logger.error("query tables error: {}", config.getTables(), e);
            response.setStatus(1);
            response.setMsg("数据查询失败");
        } catch (Exception e) {
            logger.error("query tables error: {}", config.getTables(), e);
            response.setStatus(1);
            response.setMsg("未知异常");
        }
        return response;
    }

    private Map<String, FesqlTable> getTableStatus(String dbName) throws SQLException {
        Map<String, FesqlTable> resMap = new HashMap<>();
        String sqlString = String.format("USE %s;SHOW TABLE STATUS;", dbName);
        List<List<String>> resList = sqlService.executeOnlineSql(sqlString, "");
        for (int i = 1; i < resList.size(); i++) {
            List<String> lineStr = resList.get(i);
            String key = String.format("%s.%s", lineStr.get(2), lineStr.get(1));
            FesqlTable fesqlTable = new FesqlTable();
            fesqlTable.setRows(Long.parseLong(lineStr.get(4)));
            fesqlTable.setUseMemory(Double.parseDouble(lineStr.get(5)));
            resMap.put(key, fesqlTable);
        }
        return resMap;
    }
}