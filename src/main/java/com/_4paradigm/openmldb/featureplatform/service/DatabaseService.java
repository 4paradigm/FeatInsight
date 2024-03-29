package com._4paradigm.openmldb.featureplatform.service;

import com._4paradigm.openmldb.featureplatform.dao.model.SimpleTableInfo;
import com._4paradigm.openmldb.sdk.Schema;
import com._4paradigm.openmldb.sdk.impl.SqlClusterExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Repository
public class DatabaseService {

    @Autowired
    private SqlClusterExecutor sqlExecutor;

    @Autowired
    public DatabaseService(SqlClusterExecutor sqlExecutor) {
        this.sqlExecutor = sqlExecutor;
    }

    public List<String> getDatabases() {
        List<String> databases = sqlExecutor.showDatabases();
        List<String> outputDatabases = new ArrayList<>();
        for (String database: databases) {
            if (!database.equals("SYSTEM_FEATURE_PLATFORM")) { // Ignore the system database
                outputDatabases.add(database);
            }
        }
        return outputDatabases;
    }

    public List<SimpleTableInfo> getDatabaseTables(String database) throws SQLException {
        ArrayList<SimpleTableInfo> simpleTableInfos = new ArrayList<>();

        List<String> tables = sqlExecutor.getTableNames(database);

        for (String table : tables) {
            Schema schema = sqlExecutor.getTableSchema(database, table);
            String schemaString = schema.toString();
            SimpleTableInfo simpleTableInfo = new SimpleTableInfo(database, table, schemaString);
            simpleTableInfos.add(simpleTableInfo);
        }

        return simpleTableInfos;
    }

    public void deleteDatabase(String db) throws SQLException {
        Statement statement = sqlExecutor.getStatement();
        statement.execute("SET @@execute_mode='online'");

        String sql = String.format("DROP DATABASE %s", db);
        statement.execute(sql);

        statement.close();
    }

}