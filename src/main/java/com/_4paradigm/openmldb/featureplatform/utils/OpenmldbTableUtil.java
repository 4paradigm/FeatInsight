package com._4paradigm.openmldb.featureplatform.utils;

import com._4paradigm.openmldb.common.Pair;
import com._4paradigm.openmldb.sdk.Schema;
import com._4paradigm.openmldb.sdk.impl.SqlClusterExecutor;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OpenmldbTableUtil {

    public static Map<String, Map<String, Schema>> getSystemSchemaMaps(SqlClusterExecutor openmldbSqlExecutor) throws SQLException {
        Map<String, Map<String, Schema>> schemaMaps = new HashMap<>();

        List<String> databases = openmldbSqlExecutor.showDatabases();
        for (String database : databases) {
            List<String> tables = openmldbSqlExecutor.getTableNames(database);
            Map<String, Schema> dbSchemaMap = new HashMap<>();

            for (String table : tables) {
                Schema schema = openmldbSqlExecutor.getTableSchema(database, table);
                dbSchemaMap.put(table, schema);
            }
            schemaMaps.put(database, dbSchemaMap);
        }

        return schemaMaps;
    }

    public static Schema getMainTableSchema(SqlClusterExecutor sqlExecutor, String sql, String db) throws SQLException {
        List<Pair<String, String>> tables = SqlClusterExecutor.getDependentTables(sql, db,
                OpenmldbTableUtil.getSystemSchemaMaps(sqlExecutor));

        Pair<String, String> mainTablePair = tables.get(0);

        String mainDb = mainTablePair.getKey();
        String mainTable = mainTablePair.getValue();

        Schema schema = sqlExecutor.getTableSchema(mainDb, mainTable);
        return schema;
    }

    public static String getMainTableName(SqlClusterExecutor sqlExecutor, String sql, String db) throws SQLException {
        List<Pair<String, String>> tables = SqlClusterExecutor.getDependentTables(sql, db,
                OpenmldbTableUtil.getSystemSchemaMaps(sqlExecutor));

        Pair<String, String> mainTablePair = tables.get(0);

        String mainTable = mainTablePair.getValue();
        return mainTable;
    }

}