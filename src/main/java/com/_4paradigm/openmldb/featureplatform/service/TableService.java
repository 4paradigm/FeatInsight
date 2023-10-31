package com._4paradigm.openmldb.featureplatform.service;

import com._4paradigm.openmldb.common.Pair;
import com._4paradigm.openmldb.featureplatform.dao.model.FeatureService;
import com._4paradigm.openmldb.featureplatform.dao.model.FeatureView;
import com._4paradigm.openmldb.featureplatform.dao.model.SimpleTableInfo;
import com._4paradigm.openmldb.featureplatform.utils.OpenmldbTableUtil;
import com._4paradigm.openmldb.sdk.Schema;
import com._4paradigm.openmldb.sdk.impl.SqlClusterExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TableService {

    @Autowired
    private SqlClusterExecutor sqlExecutor;

    @Autowired
    public TableService(SqlClusterExecutor sqlExecutor) {
        this.sqlExecutor = sqlExecutor;
    }

    public List<SimpleTableInfo> getTables() throws SQLException {
        ArrayList<SimpleTableInfo> simpleTableInfos = new ArrayList<>();

        List<String> databases = sqlExecutor.showDatabases();
        for (String database : databases) {
            List<String> tables = sqlExecutor.getTableNames(database);
            for (String table : tables) {
                Schema schema = sqlExecutor.getTableSchema(database, table);
                String schemaString = schema.toString();
                SimpleTableInfo simpleTableInfo = new SimpleTableInfo(database, table, schemaString);
                simpleTableInfos.add(simpleTableInfo);
            }
        }

        return simpleTableInfos;
    }

    public SimpleTableInfo getTable(String db, String table) throws SQLException {
        Schema schema = sqlExecutor.getTableSchema(db, table);
        String schemaString = schema.toString();
        SimpleTableInfo simpleTableInfo = new SimpleTableInfo(db, table, schemaString);
        return simpleTableInfo;
    }

    public List<FeatureService> getRelatedFeatureServices(String db, String table) {
        List<FeatureService> relatedFeatureServices = new ArrayList<>();

        try {
            // Get all feature services
            FeatureServiceService featureServiceService = new FeatureServiceService(sqlExecutor);
            List<FeatureService> allFeatureServices = featureServiceService.getFeatureServices();

            for (FeatureService featureService : allFeatureServices) {

                String selectSql = FeatureServiceService.removeDeploySubstring(featureService.getSql());

                List<Pair<String, String>> dependentTables = SqlClusterExecutor.getDependentTables(selectSql,
                        featureService.getDb(), OpenmldbTableUtil.getSystemSchemaMaps(sqlExecutor));

                for (Pair<String, String> tableItem : dependentTables) {
                    String currentDb = tableItem.getKey();
                    String currentTable = tableItem.getValue();

                    if (db.equals(currentDb) && table.equals(currentTable)) {
                        // Add to result if equal
                        relatedFeatureServices.add(featureService);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        return relatedFeatureServices;
    }

    public List<FeatureView> getRelatedFeatureViews(String db, String table) throws SQLException {
        List<FeatureView> relatedFeatureViews = new ArrayList<>();

        // Get all feature services
        FeatureViewService featureViewService = new FeatureViewService(sqlExecutor);
        List<FeatureView> allFeatureViews = featureViewService.getFeatureViews();

        for (FeatureView featureView : allFeatureViews) {
            List<Pair<String, String>> dependentTables = SqlClusterExecutor.getDependentTables(featureView.getSql(),
                    featureView.getDb(), OpenmldbTableUtil.getSystemSchemaMaps(sqlExecutor));

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

}