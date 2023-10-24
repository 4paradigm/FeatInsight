package com._4paradigm.openmldb.featureplatform.service;

import com._4paradigm.openmldb.common.Pair;
import com._4paradigm.openmldb.featureplatform.dao.model.Feature;
import com._4paradigm.openmldb.featureplatform.dao.model.FeatureView;
import com._4paradigm.openmldb.featureplatform.utils.OpenmldbSdkUtil;
import com._4paradigm.openmldb.featureplatform.utils.OpenmldbTableUtil;
import com._4paradigm.openmldb.featureplatform.utils.ResultSetUtil;
import com._4paradigm.openmldb.featureplatform.utils.TypeUtil;
import com._4paradigm.openmldb.sdk.Column;
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
public class FeatureViewService {

    @Autowired
    private SqlClusterExecutor sqlExecutor;

    @Autowired
    public FeatureViewService(SqlClusterExecutor sqlExecutor) {
        this.sqlExecutor = sqlExecutor;
    }

    // Convert featureNames string in database to list of string
    public static List<String> featureNamesStringToList(String featureNamesString) {
        List<String> featureNames = new ArrayList<>();

        String[] featureNameArray = featureNamesString.split(",");
        for (String featureName: featureNameArray) {
            featureNames.add(featureName.trim());
        }

        return featureNames;
    }

    public Map<String, String> getFeatureDescriptionMap(FeaturesService featuresService, String featureViewName,
                                                        List<String> featureNames) throws SQLException {
        Map<String, String> featureDescriptionMap = new HashMap<>();

        for (String featureName: featureNames) {
            Feature feature = featuresService.getFeature(featureViewName, featureName);
            featureDescriptionMap.put(featureName, feature.getDescription());
        }

        return featureDescriptionMap;
    }

    public FeatureView getFeatureViewFromResultSet(FeaturesService featureService, ResultSet resultSet)
            throws SQLException {
        String featureViewName = resultSet.getString(1);
        String db = resultSet.getString(2);
        String sql = resultSet.getString(3);
        String description = resultSet.getString(4);
        List<String> featureNames = featureNamesStringToList(resultSet.getString(5));
        Map<String, String> featureDescriptionMap = getFeatureDescriptionMap(featureService, featureViewName,
                featureNames);

        return new FeatureView(featureViewName, db, sql, description, featureNames, featureDescriptionMap);
    }

    public List<FeatureView> getFeatureViews() throws SQLException {
        Statement statement = sqlExecutor.getStatement();
        statement.execute("SET @@execute_mode='online'");

        FeaturesService featureService = new FeaturesService(sqlExecutor);
        ArrayList<FeatureView> featureViews = new ArrayList<>();

        String sql = "SELECT name, db, sql, description, feature_names FROM SYSTEM_FEATURE_PLATFORM.feature_views";
        statement.execute(sql);
        ResultSet resultSet = statement.getResultSet();

        while (resultSet.next()) {
            featureViews.add(getFeatureViewFromResultSet(featureService, resultSet));
        }

        statement.close();
        return featureViews;
    }

    public FeatureView getFeatureViewByName(String name) throws SQLException {
        Statement statement = sqlExecutor.getStatement();
        statement.execute("SET @@execute_mode='online'");

        FeaturesService featureService = new FeaturesService(sqlExecutor);

        String sql = String.format("SELECT name, db, sql, description, feature_names " +
                "FROM SYSTEM_FEATURE_PLATFORM.feature_views WHERE name='%s'", name);
        statement.execute(sql);
        ResultSet resultSet = statement.getResultSet();

        ResultSetUtil.assertSizeIsOne(resultSet);
        resultSet.next();

        FeatureView featureView = getFeatureViewFromResultSet(featureService, resultSet);

        statement.close();
        return featureView;
    }

    public void assertFeatureViewValid(FeatureView featureView) throws SQLException {
        if (featureView.getName() == "") {
            throw new SQLException("The feature view name is empty");
        }

        if (featureView.getDb() == "") {
            throw new SQLException("The database of feature view is empty");
        }

        if (featureView.getSql() == "") {
            throw new SQLException("The sql of feature view is empty");
        }
    }

    public List<String> getOutputFeatureNames(FeatureView featureView) throws SQLException {
        assertFeatureViewValid(featureView);

        Map<String, Map<String, Schema>> schemaMaps = OpenmldbTableUtil.getSystemSchemaMaps(sqlExecutor);

        List<Column> outputSchemaColumns = SqlClusterExecutor.genOutputSchema(featureView.getSql(), featureView.getDb(),
                        schemaMaps).getColumnList();

        List<String> outputFeatureNames = new ArrayList<>();
        for (Column outputSchemaColumn : outputSchemaColumns) {
            String name = outputSchemaColumn.getColumnName();
            outputFeatureNames.add(name);
        }

        return outputFeatureNames;
    }

    public FeatureView addFeatureView(FeatureView featureView) throws SQLException {
        assertFeatureViewValid(featureView);
        if (featureView.getFeatureNames().size() == 0) {
            throw new SQLException("The number of features in feature view is 0");
        }

        Statement statement = sqlExecutor.getStatement();
        statement.execute("SET @@execute_mode='online'");

        FeaturesService featureService = new FeaturesService(sqlExecutor);

        Map<String, Map<String, Schema>> schemaMaps = OpenmldbTableUtil.getSystemSchemaMaps(sqlExecutor);

        List<Column> outputSchemaColumns = SqlClusterExecutor.genOutputSchema(featureView.getSql(), featureView.getDb(),
                schemaMaps).getColumnList();

        for (Column outputSchemaColumn : outputSchemaColumns) {
            String name = outputSchemaColumn.getColumnName();
            int intType = outputSchemaColumn.getSqlType();
            String stringType = TypeUtil.javaSqlTypeToString(intType);

            String featureDescription = "";
            if (featureView.getFeatureDescriptionMap() != null) {
                if (featureView.getFeatureDescriptionMap().containsKey(name)) {
                    featureDescription = featureView.getFeatureDescriptionMap().get(name);
                }
            }

            // Create and insert feature in database
            Feature feature = new Feature(featureView.getName(), name, stringType, featureDescription);
            featureService.addFeature(feature);
        }

        String featureNamesString = String.join(",", featureView.getFeatureNames());
        String insertSql = String.format("INSERT INTO SYSTEM_FEATURE_PLATFORM.feature_views (name, db, " +
                "sql, description, feature_names) values ('%s', '%s', '%s', '%s', '%s')",
                featureView.getName(), featureView.getDb(), featureView.getSql(), featureView.getDescription(),
                featureNamesString);
        statement.execute(insertSql);

        statement.close();
        return featureView;
    }

    public void deleteFeatureView(String name) throws SQLException {
        Statement statement = sqlExecutor.getStatement();
        statement.execute("SET @@execute_mode='online'");

        FeaturesService featureService = new FeaturesService(sqlExecutor);

        // Delete the features
        List<Feature> features = featureService.getFeaturesByFeatureView(name);
        for (Feature feature : features) {
            featureService.deleteFeature(feature);
        }

        // Delete the feature view
        String sql = String.format("DELETE FROM SYSTEM_FEATURE_PLATFORM.feature_views WHERE name='%s'", name);
        statement.execute(sql);

        statement.close();
    }

    public List<String> getDependentTables(String name) throws SQLException {
        FeatureView featureView = getFeatureViewByName(name);

        List<Pair<String, String>> tables = SqlClusterExecutor.getDependentTables(featureView.getSql(),
                featureView.getDb(), OpenmldbTableUtil.getSystemSchemaMaps(sqlExecutor));

        List<String> fullNameTables = new ArrayList<>();
        for (Pair<String, String> tableItem : tables) {
            String fullTableName = tableItem.getKey() + "." + tableItem.getValue();
            fullNameTables.add(fullTableName);
        }
        return fullNameTables;
    }
}