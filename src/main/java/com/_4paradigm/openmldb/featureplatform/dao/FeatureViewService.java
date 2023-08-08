package com._4paradigm.openmldb.featureplatform.dao;

import com._4paradigm.openmldb.common.Pair;
import com._4paradigm.openmldb.featureplatform.dao.model.Feature;
import com._4paradigm.openmldb.featureplatform.dao.model.FeatureService;
import com._4paradigm.openmldb.featureplatform.dao.model.FeatureView;
import com._4paradigm.openmldb.featureplatform.utils.OpenmldbTableUtil;
import com._4paradigm.openmldb.featureplatform.utils.TypeUtil;
import com._4paradigm.openmldb.sdk.Column;
import com._4paradigm.openmldb.sdk.Schema;
import com._4paradigm.openmldb.sdk.impl.SqlClusterExecutor;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.*;

@Repository
public class FeatureViewService {

    private final Connection openmldbConnection;
    private final SqlClusterExecutor openmldbSqlExecutor;

    @Autowired
    public FeatureViewService(Connection openmldbConnection, SqlClusterExecutor openmldbSqlExecutor) {
        this.openmldbConnection = openmldbConnection;
        this.openmldbSqlExecutor = openmldbSqlExecutor;
    }

    public List<FeatureView> getFeatureViews() {
        String sql = "SELECT name, entity_names, db, sql, feature_names, description FROM SYSTEM_FEATURE_PLATFORM.feature_views";

        ArrayList<FeatureView> featureViews = new ArrayList<>();

        try {
            Statement openmldbStatement = openmldbConnection.createStatement();
            openmldbStatement.execute(sql);
            ResultSet result = openmldbStatement.getResultSet();

            while (result.next()) {
                FeatureView featureView = new FeatureView(result.getString(1), result.getString(2), result.getString(3), result.getString(4), result.getString(5), result.getString(6));
                featureViews.add(featureView);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return featureViews;
    }

    public FeatureView getFeatureViewByName(String name) throws SQLException {

        throw new SQLException("tobe , sql exception");

            // TODO: Set database before
            /*
            String sql = "SELECT name, entity_names, sql FROM SYSTEM_FEATURE_PLATFORM.feature_views WHERE name=?";
            PreparedStatement openmldbStatement = openmldbConnection.prepareStatement(sql);
            openmldbStatement.setString(1, name);
            ResultSet result = openmldbStatement.executeQuery();
            */
/*
            String sql = String.format("SELECT name, entity_names, db, sql, feature_names, description FROM SYSTEM_FEATURE_PLATFORM.feature_views WHERE name='%s'", name);
            Statement openmldbStatement = openmldbConnection.createStatement();
            openmldbStatement.execute(sql);
            ResultSet result = openmldbStatement.getResultSet();

            if (result.getFetchSize() == 0) {
                System.out.print("Can not get FeatureView with the name: " + name);
                return null;
            } else if (result.getFetchSize() > 1) {
                System.out.print("Get more FeatureView with the same name");
                return null;
            } else {
                while (result.next()) {
                    FeatureView featureView = new FeatureView(result.getString(1), result.getString(2), result.getString(3), result.getString(4), result.getString(5), result.getString(6));
                    System.out.print("Get feature view: " + featureView);
                    return featureView;
                }
            }

            openmldbStatement.close();

            return null;*/
    }

    public String validateFeatureView(FeatureView featureView) throws Exception {

        if (featureView.getName() == "") {
            throw new Exception("The feature view name is empty");
        }

        if (featureView.getDb() == "") {
            throw new Exception("The database of feature view is empty");
        }

        if (featureView.getSql() == "") {
            throw new Exception("The sql of feature view is empty");
        }


        Map<String, Map<String, Schema>> schemaMaps = OpenmldbTableUtil.getSystemSchemaMaps(openmldbSqlExecutor);

        String sql = featureView.getSql();

        StringBuilder featureNamesBuilder = new StringBuilder();

        // TODO: Use for package with openmldb-0.8
        List<Column> outputSchemaColumns = SqlClusterExecutor.genOutputSchema(sql, featureView.getDb(), schemaMaps).getColumnList();
        //List<Column> outputSchemaColumns = SqlClusterExecutor.genOutputSchema(sql, schemaMaps).getColumnList();
        for (Column outputSchemaColumn: outputSchemaColumns) {
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

    public boolean addFeatureView(FeatureView featureView) {
        // TODO: Throw exception if the feature view is invalid
        try {
            validateFeatureView(featureView);

            Map<String, Map<String, Schema>> schemaMaps = OpenmldbTableUtil.getSystemSchemaMaps(openmldbSqlExecutor);

            String sql = featureView.getSql();

            StringBuilder featureNamesBuilder = new StringBuilder();

            try {
                // TODO: Use for package with openmldb-0.8
                List<Column> outputSchemaColumns = SqlClusterExecutor.genOutputSchema(sql, featureView.getDb(), schemaMaps).getColumnList();
                //List<Column> outputSchemaColumns = SqlClusterExecutor.genOutputSchema(sql, schemaMaps).getColumnList();
                for (Column outputSchemaColumn: outputSchemaColumns) {
                    String name = outputSchemaColumn.getColumnName();
                    int intType = outputSchemaColumn.getSqlType();
                    String stringType = TypeUtil.javaSqlTypeToString(intType);

                    FeaturesService featuresService = new FeaturesService(openmldbConnection, openmldbSqlExecutor);

                    String featureDescription = "";
                    if (featureView.getFeatureDescriptionMap().containsKey(name)) {
                        featureDescription = featureView.getFeatureDescriptionMap().get(name);
                    }

                    Feature feature = new Feature(featureView.getName(), name, stringType, featureDescription);
                    featuresService.addFeature(feature);

                    if (featureNamesBuilder.length() == 0) {
                        featureNamesBuilder.append(name);
                    } else {
                        featureNamesBuilder.append(", " + name);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Notice that we can not pass the SQL with database now, skip creating Feature objects");
            }

            // TODO: It would be better to use JDBC prepared statement from connection
            String featureNames = featureNamesBuilder.toString();
            String insertSql = String.format("INSERT INTO SYSTEM_FEATURE_PLATFORM.feature_views (name, entity_names, db, sql, feature_names, description) values ('%s', '%s', '%s', '%s', '%s', '%s')", featureView.getName(), featureView.getEntityNames(), featureView.getDb(), featureView.getSql(), featureNames, featureView.getDescription());

            System.out.println("Try to insert with SQL: " + insertSql);
            Statement openmldbStatement = openmldbConnection.createStatement();
            openmldbStatement.execute(insertSql);
            openmldbStatement.close();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean deleteFeatureView(String name) {
        try {
            // Delete the features
            FeaturesService featuresService = new FeaturesService(openmldbConnection, openmldbSqlExecutor);
            List<Feature> features = featuresService.getFeaturesByFeatureView(name);
            for (Feature feature: features) {
                 featuresService.deleteFeature(feature);
            }

            // Delete the feature view
            // TODO: It would be better to use JDBC prepared statement from connection
            String sql = String.format("DELETE FROM SYSTEM_FEATURE_PLATFORM.feature_views WHERE name='%s'", name);

            Statement openmldbStatement = openmldbConnection.createStatement();
            openmldbStatement.execute(sql);
            openmldbStatement.close();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<String> getDependentTables(String name) throws SQLException {
        FeatureView featureView = getFeatureViewByName(name);
        List<Pair<String, String>> tables = SqlClusterExecutor.getDependentTables(featureView.getSql(), featureView.getDb(), OpenmldbTableUtil.getSystemSchemaMaps(openmldbSqlExecutor));

        List<String> fullNameTables = new ArrayList<>();
        for (Pair<String, String> tableItem: tables) {
            fullNameTables.add(tableItem.getKey() + "." + tableItem.getValue());
        }
        return fullNameTables;
    }
}