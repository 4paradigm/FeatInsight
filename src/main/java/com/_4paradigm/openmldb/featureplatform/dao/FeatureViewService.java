package com._4paradigm.openmldb.featureplatform.dao;

import com._4paradigm.openmldb.common.Pair;
import com._4paradigm.openmldb.featureplatform.dao.model.Feature;
import com._4paradigm.openmldb.featureplatform.dao.model.FeatureView;
import com._4paradigm.openmldb.featureplatform.utils.OpenmldbTableUtil;
import com._4paradigm.openmldb.featureplatform.utils.TypeUtil;
import com._4paradigm.openmldb.sdk.Column;
import com._4paradigm.openmldb.sdk.Schema;
import com._4paradigm.openmldb.sdk.impl.SqlClusterExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class FeatureViewService {

    private final Connection openmldbConnection;
    private final SqlClusterExecutor openmldbSqlExecutor;

    @Autowired
    public FeatureViewService(Connection openmldbConnection, SqlClusterExecutor openmldbSqlExecutor) {
        this.openmldbConnection = openmldbConnection;
        this.openmldbSqlExecutor = openmldbSqlExecutor;
    }

    public List<FeatureView> getFeatureViews() throws SQLException {
        String sql = "SELECT name, entity_names, db, sql, feature_names, description FROM SYSTEM_FEATURE_PLATFORM.feature_views";

        ArrayList<FeatureView> featureViews = new ArrayList<>();

        Statement openmldbStatement = openmldbConnection.createStatement();
        openmldbStatement.execute(sql);
        ResultSet result = openmldbStatement.getResultSet();

        while (result.next()) {
            FeatureView featureView = new FeatureView(result.getString(1), result.getString(2), result.getString(3), result.getString(4), result.getString(5), result.getString(6));
            featureViews.add(featureView);
        }

        return featureViews;
    }

    public FeatureView getFeatureViewByName(String name) throws SQLException {
        // TODO: Set database before
        String sql = String.format("SELECT name, entity_names, db, sql, feature_names, description FROM SYSTEM_FEATURE_PLATFORM.feature_views WHERE name='%s'", name);
        Statement openmldbStatement = openmldbConnection.createStatement();
        openmldbStatement.execute(sql);
        ResultSet result = openmldbStatement.getResultSet();

        if (result.getFetchSize() == 0) {
            throw new SQLException("Can not get FeatureView with the name: " + name);
        } else if (result.getFetchSize() > 1) {
            throw new SQLException("Get more FeatureView with the same name: " + name);
        } else {
            while (result.next()) {
                FeatureView featureView = new FeatureView(result.getString(1), result.getString(2), result.getString(3), result.getString(4), result.getString(5), result.getString(6));
                //System.out.print("Get feature view: " + featureView);
                openmldbStatement.close();
                return featureView;
            }
            return null;
        }
    }

    public String validateFeatureView(FeatureView featureView) throws SQLException {

        if (featureView.getName() == "") {
            throw new SQLException("The feature view name is empty");
        }

        if (featureView.getDb() == "") {
            throw new SQLException("The database of feature view is empty");
        }

        if (featureView.getSql() == "") {
            throw new SQLException("The sql of feature view is empty");
        }


        Map<String, Map<String, Schema>> schemaMaps = OpenmldbTableUtil.getSystemSchemaMaps(openmldbSqlExecutor);

        String sql = featureView.getSql();

        StringBuilder featureNamesBuilder = new StringBuilder();

        // TODO: Use for package with openmldb-0.8
        List<Column> outputSchemaColumns = SqlClusterExecutor.genOutputSchema(sql, featureView.getDb(), schemaMaps).getColumnList();
        //List<Column> outputSchemaColumns = SqlClusterExecutor.genOutputSchema(sql, schemaMaps).getColumnList();
        for (Column outputSchemaColumn : outputSchemaColumns) {
            String name = outputSchemaColumn.getColumnName();
            int intType = outputSchemaColumn.getSqlType();
            String stringType = TypeUtil.javaSqlTypeToString(intType);

            if (featureNamesBuilder.length() == 0) {
                featureNamesBuilder.append(name);
            } else {
                featureNamesBuilder.append(", " + name);
            }
        }

        return featureNamesBuilder.toString();
    }

    public FeatureView addFeatureView(FeatureView featureView) throws SQLException {
        validateFeatureView(featureView);

        Map<String, Map<String, Schema>> schemaMaps = OpenmldbTableUtil.getSystemSchemaMaps(openmldbSqlExecutor);

        String sql = featureView.getSql();

        StringBuilder featureNamesBuilder = new StringBuilder();

        List<Column> outputSchemaColumns = SqlClusterExecutor.genOutputSchema(sql, featureView.getDb(), schemaMaps).getColumnList();
        for (Column outputSchemaColumn : outputSchemaColumns) {
            String name = outputSchemaColumn.getColumnName();
            int intType = outputSchemaColumn.getSqlType();
            String stringType = TypeUtil.javaSqlTypeToString(intType);

            FeaturesService featuresService = new FeaturesService(openmldbConnection, openmldbSqlExecutor);

            String featureDescription = "";
            if (featureView.getFeatureDescriptionMap() != null && !featureView.getFeatureDescriptionMap().isEmpty()) {
                if (featureView.getFeatureDescriptionMap().containsKey(name)) {
                    featureDescription = featureView.getFeatureDescriptionMap().get(name);
                }
            }

            Feature feature = new Feature(featureView.getName(), name, stringType, featureDescription);
            featuresService.addFeature(feature);

            if (featureNamesBuilder.length() == 0) {
                featureNamesBuilder.append(name);
            } else {
                featureNamesBuilder.append(", " + name);
            }
        }

        // TODO: It would be better to use JDBC prepared statement from connection
        String featureNames = featureNamesBuilder.toString();

        FeatureView newFeatureView = new FeatureView(featureView.getName(), featureView.getEntityNames(), featureView.getDb(), featureView.getSql(), featureNames, featureView.getDescription());

        String insertSql = String.format("INSERT INTO SYSTEM_FEATURE_PLATFORM.feature_views (name, entity_names, db, sql, feature_names, description) values ('%s', '%s', '%s', '%s', '%s', '%s')", newFeatureView.getName(), newFeatureView.getEntityNames(), newFeatureView.getDb(), newFeatureView.getSql(), newFeatureView.getFeatureNames(), newFeatureView.getDescription());

        Statement openmldbStatement = openmldbConnection.createStatement();
        openmldbStatement.execute(insertSql);
        openmldbStatement.close();

        return newFeatureView;
    }

    public void deleteFeatureView(String name) throws SQLException {
        // Delete the features
        FeaturesService featuresService = new FeaturesService(openmldbConnection, openmldbSqlExecutor);
        List<Feature> features = featuresService.getFeaturesByFeatureView(name);
        for (Feature feature : features) {
            featuresService.deleteFeature(feature);
        }

        // Delete the feature view
        // TODO: It would be better to use JDBC prepared statement from connection
        String sql = String.format("DELETE FROM SYSTEM_FEATURE_PLATFORM.feature_views WHERE name='%s'", name);

        Statement openmldbStatement = openmldbConnection.createStatement();
        openmldbStatement.execute(sql);
        openmldbStatement.close();
    }

    public List<String> getDependentTables(String name) throws SQLException {
        FeatureView featureView = getFeatureViewByName(name);
        List<Pair<String, String>> tables = SqlClusterExecutor.getDependentTables(featureView.getSql(), featureView.getDb(), OpenmldbTableUtil.getSystemSchemaMaps(openmldbSqlExecutor));

        List<String> fullNameTables = new ArrayList<>();
        for (Pair<String, String> tableItem : tables) {
            fullNameTables.add(tableItem.getKey() + "." + tableItem.getValue());
        }
        return fullNameTables;
    }
}