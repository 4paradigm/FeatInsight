package com._4paradigm.openmldb.featureplatform.dao;

import com._4paradigm.openmldb.common.Pair;
import com._4paradigm.openmldb.featureplatform.dao.model.FeatureService;
import com._4paradigm.openmldb.featureplatform.dao.model.FeatureView;
import com._4paradigm.openmldb.featureplatform.dao.model.SimpleTableInfo;
import com._4paradigm.openmldb.featureplatform.utils.OpenmldbTableUtil;
import com._4paradigm.openmldb.sdk.Schema;
import com._4paradigm.openmldb.sdk.impl.SqlClusterExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TableService {

    private final Statement openmldbStatement;

    private final Connection openmldbConnection;
    private final SqlClusterExecutor openmldbSqlExecutor;

    @Autowired
    public TableService(Statement openmldbStatement, Connection openmldbConnection, SqlClusterExecutor openmldbSqlExecutor) {
        this.openmldbStatement = openmldbStatement;
        this.openmldbConnection = openmldbConnection;
        this.openmldbSqlExecutor = openmldbSqlExecutor;
    }

    public List<SimpleTableInfo> getTables() throws SQLException {
        ArrayList<SimpleTableInfo> simpleTableInfos = new ArrayList<>();

        List<String> databases = openmldbSqlExecutor.showDatabases();
        for (String database: databases) {
            List<String> tables = openmldbSqlExecutor.getTableNames(database);
            for (String table: tables) {
                Schema schema = openmldbSqlExecutor.getTableSchema(database, table);
                String schemaString = schema.toString();
                SimpleTableInfo simpleTableInfo = new SimpleTableInfo(database, table, schemaString);
                simpleTableInfos.add(simpleTableInfo);
            }
        }

        return simpleTableInfos;
    }

    public SimpleTableInfo getTable(String db, String table) throws SQLException {
        Schema schema = openmldbSqlExecutor.getTableSchema(db, table);
        String schemaString = schema.toString();
        SimpleTableInfo simpleTableInfo = new SimpleTableInfo(db, table, schemaString);
        return simpleTableInfo;
    }

    public List<FeatureService> getRelatedFeatureServices(String db, String table) throws SQLException {
        List<FeatureService> relatedFeatureServices = new ArrayList<>();

        // Get all feature services
        FeatureServiceService featureServiceService = new FeatureServiceService(this.openmldbConnection, this.openmldbSqlExecutor);
        List<FeatureService> allFeatureServices = featureServiceService.getFeatureServices();

        for (FeatureService featureService: allFeatureServices) {
            List<Pair<String, String>> dependentTables = SqlClusterExecutor.getDependentTables(featureService.getSql(), featureService.getDb(), OpenmldbTableUtil.getSystemSchemaMaps(openmldbSqlExecutor));

            for (Pair<String, String> tableItem: dependentTables) {
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
        List<FeatureView> relatedFeatureViews = new ArrayList<>();

        // Get all feature services
        FeatureViewService featureViewService = new FeatureViewService(this.openmldbConnection, this.openmldbSqlExecutor);
        List<FeatureView> allFeatureViews = featureViewService.getFeatureViews();

        for (FeatureView featureView: allFeatureViews) {
            List<Pair<String, String>> dependentTables = SqlClusterExecutor.getDependentTables(featureView.getSql(), featureView.getDb(), OpenmldbTableUtil.getSystemSchemaMaps(openmldbSqlExecutor));

            for (Pair<String, String> tableItem: dependentTables) {
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

}