package com._4paradigm.openmldb.featureplatform.dao;

import com._4paradigm.openmldb.featureplatform.dao.model.Feature;
import com._4paradigm.openmldb.featureplatform.dao.model.FeatureService;
import com._4paradigm.openmldb.sdk.impl.SqlClusterExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Repository
public class FeaturesService {

    private final Connection openmldbConnection;

    private final SqlClusterExecutor openmldbSqlExecutor;

    @Autowired
    public FeaturesService(Connection openmldbConnection, SqlClusterExecutor openmldbSqlExecutor) {
        this.openmldbConnection = openmldbConnection;
        this.openmldbSqlExecutor = openmldbSqlExecutor;
    }

    public List<Feature> getFeatures() throws SQLException {
        String sql = "SELECT feature_view_name, feature_name, type, description FROM SYSTEM_FEATURE_PLATFORM.features";

        ArrayList<Feature> features = new ArrayList<>();

        Statement openmldbStatement = openmldbConnection.createStatement();
        openmldbStatement.execute(sql);
        ResultSet result = openmldbStatement.getResultSet();

        while (result.next()) {
            Feature feature = new Feature(result.getString(1), result.getString(2), result.getString(3), result.getString(4));
            features.add(feature);
        }

        return features;
    }

    public Feature getFeatureByName(String featureViewName, String featureName) throws SQLException {

        String sql = String.format("SELECT feature_view_name, feature_name, type, description FROM SYSTEM_FEATURE_PLATFORM.features WHERE feature_view_name='%s' AND feature_name='%s'", featureViewName, featureName);
        Statement openmldbStatement = openmldbConnection.createStatement();
        openmldbStatement.execute(sql);
        ResultSet result = openmldbStatement.getResultSet();

        if (result.getFetchSize() == 0) {
            throw new SQLException("Can not get FeatureView with the feature view name: " + featureViewName + ", feature name: " + featureName);

        } else if (result.getFetchSize() > 1) {
            throw new SQLException("Get more FeatureView with the same name: " + featureViewName + ", feature name: " + featureName);

        } else {
            while (result.next()) {
                Feature feature = new Feature(result.getString(1), result.getString(2), result.getString(3), result.getString(4));
                System.out.print("Get feature: " + feature);
                openmldbStatement.close();
                return feature;
            }
            return null;
        }
    }

    public List<Feature> getFeaturesByFeatureView(String featureViewName) throws SQLException {
        String sql = String.format("SELECT feature_view_name, feature_name, type, description FROM SYSTEM_FEATURE_PLATFORM.features WHERE feature_view_name='%s'", featureViewName);

        ArrayList<Feature> features = new ArrayList<>();

        Statement openmldbStatement = openmldbConnection.createStatement();
        openmldbStatement.execute(sql);
        ResultSet result = openmldbStatement.getResultSet();

        while (result.next()) {
            Feature feature = new Feature(result.getString(1), result.getString(2), result.getString(3), result.getString(4));
            features.add(feature);
        }

        return features;
    }

    public List<Feature> getFeaturesByFeatureService(String featureServiceName) throws SQLException {
        // tobe1: Get latest version

        List<Feature> outputFeatures = new ArrayList<>();

        FeatureServiceService featureServiceService = new FeatureServiceService(openmldbConnection, openmldbSqlExecutor);

        FeatureService featureService = featureServiceService.getFeatureServiceByName(featureServiceName);
        String featureList = featureService.getFeatureList();

        for (String featureListItem : featureList.split(",")) {
            // TODO: Only support for feautre view name now
            String featureViewName = featureListItem.trim();
            List<Feature> features = getFeaturesByFeatureView(featureViewName);
            outputFeatures.addAll(features);
        }

        return outputFeatures;
    }

    public List<Feature> getFeaturesByFeatureServiceAndVersion(String featureServiceName, String version) throws SQLException {
        List<Feature> outputFeatures = new ArrayList<>();

        FeatureServiceService featureServiceService = new FeatureServiceService(openmldbConnection, openmldbSqlExecutor);

        FeatureService featureService = featureServiceService.getFeatureServiceByNameAndVersion(featureServiceName, version);
        String featureList = featureService.getFeatureList();

        for (String featureListItem : featureList.split(",")) {
            // TODO: Only support for feautre view name now
            String featureViewName = featureListItem.trim();
            List<Feature> features = getFeaturesByFeatureView(featureViewName);
            outputFeatures.addAll(features);
        }

        return outputFeatures;
    }

    public Feature addFeature(Feature feature) throws SQLException {
        // TODO: It would be better to use JDBC prepared statement from connection
        String sql = String.format("INSERT INTO SYSTEM_FEATURE_PLATFORM.features (feature_view_name, feature_name, type, description) values ('%s', '%s', '%s', '%s')", feature.getFeatureViewName(), feature.getFeatureName(), feature.getType(), feature.getDescription());

        Statement openmldbStatement = openmldbConnection.createStatement();
        openmldbStatement.execute(sql);
        openmldbStatement.close();

        return feature;
    }

    public void deleteFeature(Feature feature) throws SQLException {
        // TODO: It would be better to use JDBC prepared statement from connection
        String sql = String.format("DELETE FROM SYSTEM_FEATURE_PLATFORM.features WHERE feature_view_name='%s' AND feature_name='%s'", feature.getFeatureViewName(), feature.getFeatureName());

        Statement openmldbStatement = openmldbConnection.createStatement();
        openmldbStatement.execute(sql);
        openmldbStatement.close();
    }
}