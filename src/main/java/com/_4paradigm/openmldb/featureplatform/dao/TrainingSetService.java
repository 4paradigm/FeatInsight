package com._4paradigm.openmldb.featureplatform.dao;

import com._4paradigm.openmldb.featureplatform.dao.model.Entity;
import com._4paradigm.openmldb.featureplatform.dao.model.FeatureView;
import com._4paradigm.openmldb.featureplatform.dao.model.OfflineJobInfo;
import com._4paradigm.openmldb.featureplatform.dao.model.TrainingSet;
import com._4paradigm.openmldb.featureplatform.utils.OpenmldbTableUtil;
import com._4paradigm.openmldb.sdk.impl.SqlClusterExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TrainingSetService {

    @Autowired
    private Environment env;

    private final Connection openmldbConnection;
    private final SqlClusterExecutor openmldbSqlExecutor;

    @Autowired
    public TrainingSetService(Connection openmldbConnection, SqlClusterExecutor openmldbSqlExecutor) {
        this.openmldbConnection = openmldbConnection;
        this.openmldbSqlExecutor = openmldbSqlExecutor;
    }


    public List<TrainingSet> getTrainingSets() throws SQLException {
        String sql = "SELECT job_id, feature_list, format, path, options FROM SYSTEM_FEATURE_PLATFORM.training_sets";

        ArrayList<TrainingSet> tainingSets = new ArrayList<>();

        Statement openmldbStatement = openmldbConnection.createStatement();
        openmldbStatement.execute(sql);
        ResultSet result = openmldbStatement.getResultSet();

        while (result.next()) {
            TrainingSet trainingSet = new TrainingSet(result.getInt(1), result.getString(2), result.getString(3), result.getString(4), result.getString(5));
            tainingSets.add(trainingSet);
        }

        return tainingSets;
    }

    public TrainingSet getTrainingSet(int id) throws SQLException {
        String sql = String.format("SELECT job_id, feature_list, format, path, options FROM SYSTEM_FEATURE_PLATFORM.training_sets WHERE job_id=%d", id);

        Statement openmldbStatement = openmldbConnection.createStatement();
        openmldbStatement.execute(sql);
        ResultSet result = openmldbStatement.getResultSet();

        while (result.next()) {
            TrainingSet trainingSet = new TrainingSet(result.getInt(1), result.getString(2), result.getString(3), result.getString(4), result.getString(5));
            return trainingSet;
        }

        // TODO: throw exception if not found
        return null;
    }

    public TrainingSet createTrainingSet(TrainingSet trainingSet) throws SQLException {

        FeatureViewService featureViewService = new FeatureViewService(openmldbConnection, openmldbSqlExecutor);

        boolean isChooseSingleFeature = false;
        List<String> choosedFeatureNames = new ArrayList<>();
        List<String> choosedFeautreViewNames = new ArrayList<>();

        // Merge SQL from FeatureViews
        List<String> sqlList = new ArrayList<>();
        String[] featureList = trainingSet.getFeatureList().split(",");;

        for (String splitFeatureList : featureList) {
            String featureListItem = splitFeatureList.trim();
            // Only handle the valid string
            if (!featureListItem.equals("")) {

                String featureViewName = featureListItem;
                if (featureListItem.contains(":")) {
                    // Handle as single feature
                    isChooseSingleFeature = true;

                    // Update feature view name
                    featureViewName = featureListItem.split(":")[0];

                    // Add feature to output list
                    String featureName = featureListItem.split(":")[1];
                    String fixedFeatureName = featureName.trim();
                    if (!choosedFeatureNames.contains(fixedFeatureName)) {
                        choosedFeatureNames.add(fixedFeatureName);
                    }

                } else {
                    // Handle as feature view
                    FeatureView featureView = featureViewService.getFeatureViewByName(featureViewName);
                    if (featureView == null) {
                        throw new SQLException("Can not get feature view by name: " + featureViewName);
                    }

                    String[] featureNameLists = featureView.getFeatureNames().split(",");
                    for (String featureName: featureNameLists) {
                        String fixedFeatureName = featureName.trim();
                        if (!choosedFeatureNames.contains(fixedFeatureName)) {
                            choosedFeatureNames.add(fixedFeatureName);
                        }
                    }

                }

                if (!choosedFeautreViewNames.contains(featureViewName)) {
                    choosedFeautreViewNames.add(featureViewName);
                }
            }
        }

        String db = "";
        String entities = "";

        for (String featureViewName: choosedFeautreViewNames) {
            FeatureView featureView = featureViewService.getFeatureViewByName(featureViewName);
            if (featureView == null) {
                System.out.println("Can not get feature view by name: " + featureViewName);
                return null;
            }
            sqlList.add(featureView.getSql());
            db = featureView.getDb();
            entities = featureView.getEntityNames();
        }


        if (sqlList.size() == 0) {
            System.out.println("Can not get sql from feature views: " + String.join(",", featureList));
            return null;
        }



        EntityService entityService = new EntityService(openmldbConnection);
        List<String> joinKeys = new ArrayList<>();
        for (String rawEntityName : entities.split(",")) {
            if (rawEntityName != null && !rawEntityName.trim().equals("")) {
                String entityName = rawEntityName.trim();
                Entity entity = entityService.getEntityByName(entityName);
                if (entity != null) {
                    for (String rawPrimaryKey : entity.getPrimaryKeys().split(",")) {
                        joinKeys.add(rawPrimaryKey.trim());
                    }
                }
            }
        }


        String mergedSql = sqlList.get(0);
        if (sqlList.size() > 1) {
            mergedSql = SqlClusterExecutor.mergeSQL(sqlList, db, joinKeys, OpenmldbTableUtil.getSystemSchemaMaps(openmldbSqlExecutor));
        }

        //String mergedSql = mergeSqlList(openmldbSqlExecutor, sqlList, db, joinKeys, OpenmldbTableUtil.getSystemSchemaMaps(openmldbSqlExecutor));
        if (mergedSql.endsWith(";")) {
            mergedSql = mergedSql.substring(0, mergedSql.length()-1);
        }
        //System.out.println("Generate merged SQL: " + mergedSql);

        String generatedSql = mergedSql;

        if (isChooseSingleFeature) {
            generatedSql = String.format("SELECT %s FROM (%s)", String.join(",", choosedFeatureNames), mergedSql);
            //System.out.println("Generate SQL: " + generatedSql);
        }


        //System.out.println("Generated sql: " + generatedSql);


        // TODO: Get format and update options
        String outfileSql = String.format("%s INTO OUTFILE '%s' OPTIONS (%s)", generatedSql,
                trainingSet.getPath(), trainingSet.getOptions());

        //System.out.println("Outfile sql: " + outfileSql);

        Statement stmt = this.openmldbConnection.createStatement();
        stmt.execute("SET @@execute_mode='offline'"); // 切换为离线模式
        stmt.execute(String.format("USE %s", db)); // 切换为离线模式
        stmt.execute(outfileSql); // 离线 select
        ResultSet res = stmt.getResultSet(); // 上一次 execute 的 ResultSet 结果
        res.next();

        int jobId = res.getInt(1);

        OfflineJobInfo offlineJobInfo = new OfflineJobInfo(res.getInt(1), res.getString(2), res.getString(3),
                res.getTimestamp(4), res.getTimestamp(5), res.getString(6), res.getString(7),
                res.getString(8), res.getString(9));

        //System.out.println("job info: " + offlineJobInfo);


        // TODO: Reset mode
        stmt.execute("SET @@execute_mode='online'"); // 切换为离线模式


        // TODO: It would be better to use JDBC prepared statement from connection
        String insertSql = String.format("INSERT INTO SYSTEM_FEATURE_PLATFORM.training_sets (job_id, feature_list, format, path, options) values (%d, '%s', '%s', '%s', '%s')",
                offlineJobInfo.getId(), trainingSet.getFeatureList(), trainingSet.getFormat(), trainingSet.getPath(), trainingSet.getOptions());

        //System.out.println("Insert sql: " + insertSql);

        stmt.execute(insertSql);

        TrainingSet newTrainingSet = new TrainingSet(jobId, trainingSet.getFeatureList(),
                trainingSet.getFormat(), trainingSet.getPath(), trainingSet.getOptions());

        return newTrainingSet;
    }

}