package com._4paradigm.openmldb.featureplatform.dao;

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

    private final Statement openmldbStatement;
    private final SqlClusterExecutor openmldbSqlExecutor;

    @Autowired
    public DatabaseService(Statement openmldbStatement, SqlClusterExecutor openmldbSqlExecutor) {
        this.openmldbStatement = openmldbStatement;
        this.openmldbSqlExecutor = openmldbSqlExecutor;
    }

    public List<String> getDatabases() {
        List<String> databases = openmldbSqlExecutor.showDatabases();
        return databases;
    }

    public List<SimpleTableInfo> getDatabaseTables(String database) throws SQLException {
        ArrayList<SimpleTableInfo> simpleTableInfos = new ArrayList<>();

        List<String> tables = openmldbSqlExecutor.getTableNames(database);
        for (String table : tables) {
            Schema schema = openmldbSqlExecutor.getTableSchema(database, table);
            String schemaString = schema.toString();
            SimpleTableInfo simpleTableInfo = new SimpleTableInfo(database, table, schemaString);
            simpleTableInfos.add(simpleTableInfo);
        }

        return simpleTableInfos;
    }

}