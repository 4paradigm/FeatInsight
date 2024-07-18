package com._4paradigm.openmldb.featureplatform.service;

import com._4paradigm.openmldb.common.Pair;
import com._4paradigm.openmldb.featureplatform.dao.model.*;
import com._4paradigm.openmldb.featureplatform.utils.OpenmldbSqlUtil;
import com._4paradigm.openmldb.featureplatform.utils.OpenmldbTableUtil;
import com._4paradigm.openmldb.featureplatform.utils.ProphetUtil;
import com._4paradigm.openmldb.featureplatform.utils.ResultSetUtil;
import com._4paradigm.openmldb.proto.Common;
import com._4paradigm.openmldb.proto.NS;
import com._4paradigm.openmldb.sdk.Schema;
import com._4paradigm.openmldb.sdk.impl.SqlClusterExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class TableService {
    @Autowired
    private SqlService sqlService;

    public List<SimpleTableInfo> getTables() throws SQLException {
        SqlClusterExecutor sqlExecutor = ThreadLocalSqlExecutor.getSqlExecutor();
        ArrayList<SimpleTableInfo> simpleTableInfos = new ArrayList<>();

        List<String> databases = sqlExecutor.showDatabases();
        for (String database : databases) {
            if (!database.equals("SYSTEM_FEATURE_PLATFORM")) { // Ignore the system tables
                simpleTableInfos.addAll(this.getTablesByDB(database));
            }
        }

        return simpleTableInfos;
    }


    public List<SimpleTableInfo> getTablesByDB(String dbName) throws SQLException {
        List<SimpleTableInfo> tableInfoList = new ArrayList<>();
        String sqlString = String.format("USE %s;SHOW TABLE STATUS;", dbName);
        List<List<String>> resList = sqlService.executeOnlineSql(sqlString, "");
        for (int i = 1; i < resList.size(); i++) {
            List<String> lineStr = resList.get(i);
            String tableName = lineStr.get(1);
            SimpleTableInfo tableInfo = new SimpleTableInfo();
            tableInfo.setId(lineStr.get(0));
            tableInfo.setDb(dbName);
            tableInfo.setTable(lineStr.get(1));
            tableInfo.setSchema(this.getTableSchema(dbName, tableName));
            tableInfo.setReplica(Integer.parseInt(lineStr.get(9)));
            tableInfo.setPartition(Integer.parseInt(lineStr.get(7)));
            tableInfo.setPartitionUnalive(Integer.parseInt(lineStr.get(8)));
            tableInfo.setRows(Long.parseLong(lineStr.get(4)));
            tableInfo.setUseMemory(Double.parseDouble(lineStr.get(5)));
            tableInfo.setColumnKey(this.getTableColumnKey(dbName, tableName));
            tableInfoList.add(tableInfo);
        }
        return tableInfoList;
    }

    public SimpleTableInfo getTable(String db, String table) throws SQLException {
        for (SimpleTableInfo tableInfo : this.getTablesByDB(db)) {
            if (table.equalsIgnoreCase(tableInfo.getTable())) {
                return tableInfo;
            }
        }
        return null;
    }

    public NS.TableInfo getTableInfo(String db, String table) throws SQLException {
        SqlClusterExecutor sqlExecutor = ThreadLocalSqlExecutor.getSqlExecutor();
        return sqlExecutor.getTableInfo(db, table);
    }

    public String getTableSchema(String db, String table) throws SQLException {
        SqlClusterExecutor sqlExecutor = ThreadLocalSqlExecutor.getSqlExecutor();
        Schema schema = sqlExecutor.getTableSchema(db, table);
        return schema.toString();
    }

    public boolean isExistTable(String db, String table) throws SQLException {
        try {
            SqlClusterExecutor sqlExecutor = ThreadLocalSqlExecutor.getSqlExecutor();
            Schema schema = sqlExecutor.getTableSchema(db, table);
            return null != schema;
        } catch (SQLException e) {
            if (e.getMessage().contains("not found")) {
                return false;
            } else {
                throw e;
            }
        }
    }

    public boolean createDB(String db) throws SQLException {
        SqlClusterExecutor sqlExecutor = ThreadLocalSqlExecutor.getSqlExecutor();
        return sqlExecutor.createDB(db);
    }

    public List<FeatureService> getRelatedFeatureServices(String db, String table) throws SQLException {
        SqlClusterExecutor sqlExecutor = ThreadLocalSqlExecutor.getSqlExecutor();
        List<FeatureService> relatedFeatureServices = new ArrayList<>();

        // Get all feature services
        FeatureServiceService featureServiceService = new FeatureServiceService();
        List<FeatureService> allFeatureServices = featureServiceService.getFeatureServices();

        for (FeatureService featureService : allFeatureServices) {

            String selectSql = OpenmldbSqlUtil.removeDeployFromSql(featureService.getSql());

            List<Pair<String, String>> dependentTables = SqlClusterExecutor.getDependentTables(selectSql, featureService.getDb(), OpenmldbTableUtil.getSystemSchemaMaps(sqlExecutor));

            for (Pair<String, String> tableItem : dependentTables) {
                String currentDb = tableItem.getKey();
                String currentTable = tableItem.getValue();

                if (db.equals(currentDb) && table.equals(currentTable)) {
                    // Add to result if equal
                    relatedFeatureServices.add(featureService);
                }
            }
        }

        return relatedFeatureServices;
    }

    public List<FeatureView> getRelatedFeatureViews(String db, String table) throws SQLException {
        SqlClusterExecutor sqlExecutor = ThreadLocalSqlExecutor.getSqlExecutor();
        List<FeatureView> relatedFeatureViews = new ArrayList<>();

        // Get all feature services
        FeatureViewService featureViewService = new FeatureViewService();
        List<FeatureView> allFeatureViews = featureViewService.getFeatureViews();

        for (FeatureView featureView : allFeatureViews) {
            List<Pair<String, String>> dependentTables = SqlClusterExecutor.getDependentTables(featureView.getSql(), featureView.getDb(), OpenmldbTableUtil.getSystemSchemaMaps(sqlExecutor));

            for (Pair<String, String> tableItem : dependentTables) {
                String currentDb = tableItem.getKey();
                String currentTable = tableItem.getValue();

                if (db.equals(currentDb) && table.equals(currentTable)) {
                    // Add to result if equal
                    relatedFeatureViews.add(featureView);
                }
            }
        }

        return relatedFeatureViews;
    }

    public void deleteTable(String db, String table) throws SQLException {
        SqlClusterExecutor sqlExecutor = ThreadLocalSqlExecutor.getSqlExecutor();
        Statement statement = sqlExecutor.getStatement();
        statement.execute("SET @@execute_mode='online'");

        String sql = String.format("DROP TABLE %s.%s", db, table);
        statement.execute(sql);

        statement.close();
    }

    public List<String> getIndexNames(String db, String table) throws SQLException {
        SqlClusterExecutor sqlExecutor = ThreadLocalSqlExecutor.getSqlExecutor();
        NS.TableInfo tableInfo = sqlExecutor.getTableInfo(db, table);

        // For example: ["name", "name,age"]
        List<String> indexColumnNames = new ArrayList<>();

        for (Common.ColumnKey columnKey : tableInfo.getColumnKeyList()) {
            if (columnKey.getFlag() == 0) {
                List<String> columnNameList = new ArrayList<>();
                for (String columnName : columnKey.getColNameList()) {
                    columnNameList.add(columnName);
                }

                indexColumnNames.add(String.join(",", columnNameList));
            }
        }

        return indexColumnNames;
    }

    public List<String> getTableColumnKey(String db, String table) {
        SqlClusterExecutor sqlExecutor = ThreadLocalSqlExecutor.getSqlExecutor();
        NS.TableInfo tableInfo = sqlExecutor.getTableInfo(db, table);
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
        return columnKeyStringList;
    }

}