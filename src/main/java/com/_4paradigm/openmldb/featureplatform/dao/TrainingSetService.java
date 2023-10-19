package com._4paradigm.openmldb.featureplatform.dao;

import com._4paradigm.openmldb.featureplatform.dao.model.OfflineJobInfo;
import com._4paradigm.openmldb.featureplatform.dao.model.TrainingSet;
import com._4paradigm.openmldb.featureplatform.utils.FeatureSetUtil;
import com._4paradigm.openmldb.featureplatform.utils.OpenmldbSdkUtil;
import com._4paradigm.openmldb.featureplatform.utils.ResultSetUtil;
import com._4paradigm.openmldb.sdk.impl.SqlClusterExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Repository
public class TrainingSetService {

    @Autowired
    private Environment env;

    @Autowired
    public TrainingSetService(Environment env) {
        this.env = env;
    }

    public TrainingSet resultSetToTrainingSet(ResultSet resultSet) throws SQLException {
        String featureNames = resultSet.getString(1);
        String path = resultSet.getString(2);
        String options = resultSet.getString(3);
        int jobId = resultSet.getInt(4);
        String db = resultSet.getString(5);
        String sql = resultSet.getString(6);

        return new TrainingSet(featureNames, path, options, jobId, db, sql);
    }

    public List<TrainingSet> getTrainingSets() throws SQLException {
        Connection connection = OpenmldbSdkUtil.getConnection(env);

        String sql = "SELECT feature_names, path, options, job_id, sql FROM SYSTEM_FEATURE_PLATFORM.training_sets";

        ArrayList<TrainingSet> trainingSets = new ArrayList<>();

        Statement openmldbStatement = connection.createStatement();
        openmldbStatement.execute(sql);
        ResultSet resultSet = openmldbStatement.getResultSet();

        while (resultSet.next()) {
            TrainingSet trainingSet = resultSetToTrainingSet(resultSet);
            trainingSets.add(trainingSet);
        }

        return trainingSets;
    }

    public TrainingSet getTrainingSet(int id) throws SQLException {
        Connection connection = OpenmldbSdkUtil.getConnection(env);

        String sql = String.format("SELECT feature_names, path, options, job_id, sql FROM SYSTEM_FEATURE_PLATFORM.training_sets WHERE job_id=%d", id);

        Statement openmldbStatement = connection.createStatement();
        openmldbStatement.execute(sql);
        ResultSet resultSet = openmldbStatement.getResultSet();

        ResultSetUtil.assertSizeIsOne(resultSet);

        return resultSetToTrainingSet(resultSet);
    }

    public TrainingSet createTrainingSet(TrainingSet trainingSet) throws SQLException {
        Connection connection = OpenmldbSdkUtil.getConnection(env);
        SqlClusterExecutor sqlExecutor = OpenmldbSdkUtil.getSqlExecutor(env);
        FeatureViewService featureViewService = new FeatureViewService(env);

        List<String> joinKeys = new ArrayList<>(Arrays.asList(trainingSet.getJoinKeys().split(",")));
        String mergedSql = FeatureSetUtil.featureSetToSql(sqlExecutor, featureViewService, trainingSet.getFeatureNames(), joinKeys);

        // TODO: Get format and update options
        String outfileSql = String.format("%s INTO OUTFILE '%s' OPTIONS (%s)", mergedSql,
                trainingSet.getPath(), trainingSet.getOptions());

        String db = FeatureSetUtil.getDbFromFeatureSet(featureViewService, trainingSet.getFeatureNames());

        Statement stmt = connection.createStatement();
        stmt.execute("SET @@execute_mode='offline'");
        stmt.execute(String.format("USE %s", db));
        stmt.execute(outfileSql);
        ResultSet resultSet = stmt.getResultSet();

        ResultSetUtil.assertSizeIsOne(resultSet);
        resultSet.next();

        OfflineJobInfo offlineJobInfo = OfflineJobService.resultSetToOfflineJobInfo(resultSet);

        // Generate the new training set with new data
        String newFeatureNames = String.join(",", FeatureSetUtil.featureSetToFeatureNames(featureViewService, trainingSet.getFeatureNames()));
        int jobId = resultSet.getInt(1);
        TrainingSet newTrainingSet = new TrainingSet(newFeatureNames, trainingSet.getPath(), trainingSet.getOptions(), jobId, db, outfileSql);

        // Insert training set in database
        stmt.execute("SET @@execute_mode='online'");
        String insertSql = String.format("INSERT INTO SYSTEM_FEATURE_PLATFORM.training_sets " +
                        "(feature_names, path, options, job_id, sql) values (%s, '%s', '%s', '%d', '%s')",
                newTrainingSet.getFeatureNames(), newTrainingSet.getPath(), newTrainingSet.getOptions(),
                newTrainingSet.getJobId(), newTrainingSet.getSql());
        stmt.execute(insertSql);

        return newTrainingSet;
    }

}