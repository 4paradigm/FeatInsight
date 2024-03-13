package com._4paradigm.openmldb.featureplatform.service;

import com._4paradigm.openmldb.featureplatform.dao.model.Feature;
import com._4paradigm.openmldb.featureplatform.dao.model.FeatureService;
import com._4paradigm.openmldb.featureplatform.dao.model.FeatureView;
import com._4paradigm.openmldb.featureplatform.utils.ResultSetUtil;
import com._4paradigm.openmldb.jdbc.SQLResultSet;
import com._4paradigm.openmldb.sdk.impl.SqlClusterExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Repository
public class FeaturesService {

    @Autowired
    private SqlClusterExecutor sqlExecutor;

    @Autowired
    public FeaturesService(SqlClusterExecutor sqlExecutor) {
        this.sqlExecutor = sqlExecutor;
    }

    public static Feature resultSetToFeature(ResultSet resultSet) throws SQLException {
        return new Feature(resultSet.getString(1), resultSet.getString(2), resultSet.getString(3),
                resultSet.getString(4));
    }

    public List<Feature> getFeatures() throws SQLException {
        Statement statement = sqlExecutor.getStatement();
        statement.execute("SET @@execute_mode='online'");

        ArrayList<Feature> features = new ArrayList<>();

        String sql = "SELECT feature_view_name, feature_name, type, description FROM SYSTEM_FEATURE_PLATFORM.features";
        statement.execute(sql);
        ResultSet resultSet = statement.getResultSet();

        while (resultSet.next()) {
            Feature feature = resultSetToFeature(resultSet);
            features.add(feature);
        }

        statement.close();
        return features;
    }

    public Feature getFeature(String featureViewName, String featureName) throws SQLException {
        Statement statement = sqlExecutor.getStatement();
        statement.execute("SET @@execute_mode='online'");

        String sql = String.format("SELECT feature_view_name, feature_name, type, description FROM " +
                        "SYSTEM_FEATURE_PLATFORM.features WHERE feature_view_name='%s' AND feature_name='%s'",
                featureViewName, featureName);
        statement.execute(sql);
        ResultSet resultSet = statement.getResultSet();

        ResultSetUtil.assertSizeIsOne(resultSet);
        resultSet.next();

        Feature feature = resultSetToFeature(resultSet);

        statement.close();
        return feature;
    }

    public List<Feature> getFeaturesByFeatureView(String featureViewName) throws SQLException {
        List<Feature> outputFeatures = new ArrayList<>();

        FeatureViewService featureViewService = new FeatureViewService(sqlExecutor);

        // Get the feature service with latest version
        FeatureView featureView = featureViewService.getFeatureViewByName(featureViewName);
        List<String> featureNames = featureView.getFeatureNames();

        for (String featureName : featureNames) {
            Feature feature = getFeature(featureViewName, featureName);
            outputFeatures.add(feature);
        }

        return outputFeatures;
    }

    public List<Feature> getFeaturesByFeatureService(String featureServiceName) throws SQLException {
        // Get the feature service with latest version
        FeatureServiceService featureServiceService = new FeatureServiceService(sqlExecutor);
        String version = featureServiceService.getLatestVersion(featureServiceName);

        return getFeaturesByFeatureServiceAndVersion(featureServiceName, version);
    }

    public List<Feature> getFeaturesByFeatureServiceAndVersion(String featureServiceName, String version) throws SQLException {
        List<Feature> outputFeatures = new ArrayList<>();

        FeatureServiceService featureServiceService = new FeatureServiceService(sqlExecutor);

        FeatureService featureService = featureServiceService.getFeatureServiceByNameAndVersion(featureServiceName, version);
        String featureNames = featureService.getFeatureNames();

        for (String fullFeatureName : featureNames.split(",")) {
            String featureViewName = fullFeatureName.split(":")[0];
            String featureName = fullFeatureName.split(":")[1];
            Feature feature = getFeature(featureViewName, featureName);
            outputFeatures.add(feature);
        }

        return outputFeatures;
    }

    public Feature addFeature(Feature feature) throws SQLException {
        Statement statement = sqlExecutor.getStatement();
        statement.execute("SET @@execute_mode='online'");

        String sql = String.format("INSERT INTO SYSTEM_FEATURE_PLATFORM.features " +
                "(feature_view_name, feature_name, type, description) values ('%s', '%s', '%s', '%s')",
                feature.getFeatureViewName(), feature.getFeatureName(), feature.getType(), feature.getDescription());
        statement.execute(sql);

        statement.close();
        return feature;
    }

    public void deleteFeature(Feature feature) throws SQLException {
        Statement statement = sqlExecutor.getStatement();
        statement.execute("SET @@execute_mode='online'");

        String sql = String.format("DELETE FROM SYSTEM_FEATURE_PLATFORM.features " +
                "WHERE feature_view_name='%s' AND feature_name='%s'", feature.getFeatureViewName(),
                feature.getFeatureName());
        statement.execute(sql);

        statement.close();
    }

    public String getSourceSql(String featureViewName, String featureName) throws SQLException {
        FeatureViewService featureViewService = new FeatureViewService(sqlExecutor);
        FeatureView featureView = featureViewService.getFeatureViewByName(featureViewName);

        String querySql = featureView.getSql();
        String db = featureView.getDb();

        String sourceSql = String.format("SELECT %s FROM (%s)", featureName, querySql);

        return sourceSql;
    }
    public List<List<String>> requestOnlineQueryModeSamples(String featureViewName, String featureName) throws SQLException {
        FeatureViewService featureViewService = new FeatureViewService(sqlExecutor);
        FeatureView featureView = featureViewService.getFeatureViewByName(featureViewName);

        String querySql = featureView.getSql();
        String db = featureView.getDb();

        String finalSql = String.format("SELECT %s FROM (%s) limit 10", featureName, querySql);

        Statement statement = sqlExecutor.getStatement();
        statement.execute("SET @@execute_mode='online'");
        statement.execute(String.format("USE %s", db));

        statement.execute(finalSql);

        // TODO: Check if has result set
        SQLResultSet resultSet = (SQLResultSet) statement.getResultSet();
        List<List<String>> returnList = ResultSetUtil.resultSetToStringArray(resultSet);
        resultSet.close();

        return returnList;
    }

}