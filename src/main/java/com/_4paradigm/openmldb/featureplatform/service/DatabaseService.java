package com._4paradigm.openmldb.featureplatform.service;

import com._4paradigm.openmldb.featureplatform.dao.model.SimpleTableInfo;
import com._4paradigm.openmldb.featureplatform.utils.OpenmldbSdkUtil;
import com._4paradigm.openmldb.sdk.Schema;
import com._4paradigm.openmldb.sdk.impl.SqlClusterExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class DatabaseService {

    @Autowired
    private SqlClusterExecutor sqlExecutor;

    public DatabaseService() {

    }

    public List<String> getDatabases() {
        List<String> databases = sqlExecutor.showDatabases();
        return databases;
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

}