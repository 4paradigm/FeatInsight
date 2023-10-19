package com._4paradigm.openmldb.featureplatform.dao;

import com._4paradigm.openmldb.featureplatform.dao.model.Feature;
import com._4paradigm.openmldb.featureplatform.dao.model.FeatureService;
import com._4paradigm.openmldb.featureplatform.dao.model.FeatureView;
import com._4paradigm.openmldb.featureplatform.utils.OpenmldbSdkUtil;
import com._4paradigm.openmldb.featureplatform.utils.ResultSetUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Repository
public class FeaturesService {

    private final Environment env;

    @Autowired
    public FeaturesService(Environment env) throws SQLException {
        this.env = env;
    }

    public static Feature resultSetToFeature(ResultSet resultSet) throws SQLException {
        return new Feature(resultSet.getString(1), resultSet.getString(2), resultSet.getString(3),
                resultSet.getString(4));
    }

    public List<Feature> getFeatures() throws SQLException {
        Connection connection = OpenmldbSdkUtil.getConnection(env);

        String sql = "SELECT feature_view_name, feature_name, type, description FROM SYSTEM_FEATURE_PLATFORM.features";

        ArrayList<Feature> features = new ArrayList<>();

        Statement openmldbStatement = connection.createStatement();
        openmldbStatement.execute(sql);
        ResultSet resultSet = openmldbStatement.getResultSet();

        while (resultSet.next()) {
            Feature feature = resultSetToFeature(resultSet);
            features.add(feature);
        }

        return features;
    }

    public Feature getFeatureByName(String featureViewName, String featureName) throws SQLException {
        Connection connection = OpenmldbSdkUtil.getConnection(env);
        return getFeature(connection, featureViewName, featureName);
    }

    public Feature getFeature(Connection connection, String featureViewName, String featureName) throws SQLException {
        String sql = String.format("SELECT feature_view_name, feature_name, type, description FROM " +
                        "SYSTEM_FEATURE_PLATFORM.features WHERE feature_view_name='%s' AND feature_name='%s'",
                featureViewName, featureName);
        Statement openmldbStatement = connection.createStatement();
        openmldbStatement.execute(sql);
        ResultSet resultSet = openmldbStatement.getResultSet();

        ResultSetUtil.assertSizeIsOne(resultSet);

        return resultSetToFeature(resultSet);
    }

    public List<Feature> getFeaturesByFeatureView(String featureViewName) throws SQLException {
        Connection connection = OpenmldbSdkUtil.getConnection(env);

        List<Feature> outputFeatures = new ArrayList<>();

        FeatureViewService featureViewService = new FeatureViewService(env);

        // Get the feature service with latest version
        FeatureView featureView = featureViewService.getFeatureViewByName(featureViewName);
        List<String> featureNames = featureView.getFeatureNames();

        for (String featureName : featureNames) {
            Feature feature = getFeature(connection, featureViewName, featureName);
            outputFeatures.add(feature);
        }

        return outputFeatures;
    }

    public List<Feature> getFeaturesByFeatureService(String featureServiceName) throws SQLException {
        // Get the feature service with latest version
        FeatureServiceService featureServiceService = new FeatureServiceService(env);
        String version = featureServiceService.getLatestVersion(featureServiceName);

        return getFeaturesByFeatureServiceAndVersion(featureServiceName, version);
    }

    public List<Feature> getFeaturesByFeatureServiceAndVersion(String featureServiceName, String version) throws SQLException {
        Connection connection = OpenmldbSdkUtil.getConnection(env);

        List<Feature> outputFeatures = new ArrayList<>();

        FeatureServiceService featureServiceService = new FeatureServiceService(env);

        FeatureService featureService = featureServiceService.getFeatureServiceByNameAndVersion(featureServiceName, version);
        String featureNames = featureService.getFeatureNames();

        for (String fullFeatureName : featureNames.split(",")) {
            String featureViewName = fullFeatureName.split(":")[0];
            String featureName = fullFeatureName.split(":")[1];
            Feature feature = getFeature(connection, featureViewName, featureName);
            outputFeatures.add(feature);
        }

        return outputFeatures;
    }

    public Feature addFeature(Feature feature) throws SQLException {
        Connection connection = OpenmldbSdkUtil.getConnection(env);

        String sql = String.format("INSERT INTO SYSTEM_FEATURE_PLATFORM.features " +
                "(feature_view_name, feature_name, type, description) values ('%s', '%s', '%s', '%s')",
                feature.getFeatureViewName(), feature.getFeatureName(), feature.getType(), feature.getDescription());

        Statement openmldbStatement = connection.createStatement();
        openmldbStatement.execute(sql);
        openmldbStatement.close();

        return feature;
    }

    public void deleteFeature(Feature feature) throws SQLException {
        Connection connection = OpenmldbSdkUtil.getConnection(env);

        String sql = String.format("DELETE FROM SYSTEM_FEATURE_PLATFORM.features " +
                "WHERE feature_view_name='%s' AND feature_name='%s'", feature.getFeatureViewName(),
                feature.getFeatureName());

        Statement openmldbStatement = connection.createStatement();
        openmldbStatement.execute(sql);
        openmldbStatement.close();
    }

}