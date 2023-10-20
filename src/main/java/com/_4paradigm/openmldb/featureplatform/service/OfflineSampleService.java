package com._4paradigm.openmldb.featureplatform.service;

import com._4paradigm.openmldb.featureplatform.dao.model.OfflineJobInfo;
import com._4paradigm.openmldb.featureplatform.dao.model.OfflineSample;
import com._4paradigm.openmldb.featureplatform.utils.FeatureSetUtil;
import com._4paradigm.openmldb.featureplatform.utils.ResultSetUtil;
import com._4paradigm.openmldb.sdk.impl.SqlClusterExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Repository
public class OfflineSampleService {

    @Autowired
    private SqlClusterExecutor sqlExecutor;

    public OfflineSampleService() {

    }

    public OfflineSample resultSetToOfflineSample(ResultSet resultSet) throws SQLException {
        String featureNames = resultSet.getString(1);
        String path = resultSet.getString(2);
        String options = resultSet.getString(3);
        int jobId = resultSet.getInt(4);
        String db = resultSet.getString(5);
        String sql = resultSet.getString(6);

        return new OfflineSample(featureNames, path, options, jobId, db, sql);
    }

    public List<OfflineSample> getOfflineSamples() throws SQLException {
        Statement statement = sqlExecutor.getStatement();
        statement.execute("SET @@execute_mode='online'");

        ArrayList<OfflineSample> offlineSamples = new ArrayList<>();

        String sql = "SELECT feature_names, path, options, job_id, sql FROM SYSTEM_FEATURE_PLATFORM.offline_samples";
        statement.execute(sql);
        ResultSet resultSet = statement.getResultSet();

        while (resultSet.next()) {
            OfflineSample offlineSample = resultSetToOfflineSample(resultSet);
            offlineSamples.add(offlineSample);
        }

        statement.close();
        return offlineSamples;
    }

    public OfflineSample getOfflineSample(int id) throws SQLException {
        Statement statement = sqlExecutor.getStatement();
        statement.execute("SET @@execute_mode='online'");

        String sql = String.format("SELECT feature_names, path, options, job_id, sql FROM SYSTEM_FEATURE_PLATFORM.offline_samples WHERE job_id=%d", id);
        statement.execute(sql);
        ResultSet resultSet = statement.getResultSet();

        ResultSetUtil.assertSizeIsOne(resultSet);

        OfflineSample offlineSample = resultSetToOfflineSample(resultSet);

        statement.close();
        return offlineSample;
    }

    public OfflineSample createOfflineSample(OfflineSample offlineSample) throws SQLException {
        Statement statement = sqlExecutor.getStatement();
        statement.execute("SET @@execute_mode='online'");

        FeatureViewService featureViewService = new FeatureViewService();

        List<String> joinKeys = new ArrayList<>(Arrays.asList(offlineSample.getMainTableKeys().split(",")));
        String mergedSql = FeatureSetUtil.featureSetToSql(sqlExecutor, featureViewService,
                offlineSample.getFeatureNames(), joinKeys);

        // TODO: Get format and update options
        String outfileSql = String.format("%s INTO OUTFILE '%s' OPTIONS (%s)", mergedSql,
                offlineSample.getPath(), offlineSample.getOptions());

        System.out.println("----------------- tobe");
        System.out.println(outfileSql);

        String db = FeatureSetUtil.getDbFromFeatureSet(featureViewService, offlineSample.getFeatureNames());
        statement.execute("SET @@execute_mode='offline'");
        statement.execute(String.format("USE %s", db));
        statement.execute(outfileSql);
        ResultSet resultSet = statement.getResultSet();

        ResultSetUtil.assertSizeIsOne(resultSet);
        resultSet.next();

        OfflineJobInfo offlineJobInfo = OfflineJobService.resultSetToOfflineJobInfo(resultSet);

        // Generate the new offline sample with new data
        String newFeatureNames = String.join(",", FeatureSetUtil.featureSetToFeatureNames(featureViewService, offlineSample.getFeatureNames()));
        int jobId = resultSet.getInt(1);
        OfflineSample newOfflineSample = new OfflineSample(newFeatureNames, offlineSample.getPath(), offlineSample.getOptions(), jobId, db, outfileSql);

        // Insert offline sample in database
        statement.execute("SET @@execute_mode='online'");
        String insertSql = String.format("INSERT INTO SYSTEM_FEATURE_PLATFORM.offline_samples " +
                        "(feature_names, path, options, job_id, sql) values ('%s', '%s', '%s', '%d', '%s')",
                newOfflineSample.getFeatureNames(), newOfflineSample.getPath(), newOfflineSample.getOptions(),
                newOfflineSample.getJobId(), newOfflineSample.getSql());
        statement.execute(insertSql);

        return newOfflineSample;
    }

}