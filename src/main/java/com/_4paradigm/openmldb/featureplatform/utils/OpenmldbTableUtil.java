package com._4paradigm.openmldb.featureplatform.utils;

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


}