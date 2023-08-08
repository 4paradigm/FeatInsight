package com._4paradigm.openmldb.featureplatform.utils;

import com._4paradigm.openmldb.DataType;
import com._4paradigm.openmldb.jdbc.SQLResultSet;
import com._4paradigm.openmldb.sdk.Schema;
import com._4paradigm.openmldb.sdk.impl.SqlClusterExecutor;

import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OpenmldbTableUtil {

    public static Map<String, Map<String, Schema>> getSystemSchemaMaps(SqlClusterExecutor openmldbSqlExecutor) {
        Map<String, Map<String, Schema>> schemaMaps = new HashMap<>();

        List<String> databases = openmldbSqlExecutor.showDatabases();
        for (String database: databases) {
            List<String> tables = openmldbSqlExecutor.getTableNames(database);
            Map<String, Schema> dbSchemaMap = new HashMap<>();

            for (String table: tables) {
                try {
                    Schema schema = openmldbSqlExecutor.getTableSchema(database, table);
                    dbSchemaMap.put(table, schema);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            schemaMaps.put(database, dbSchemaMap);
        }

        return schemaMaps;
    }


}