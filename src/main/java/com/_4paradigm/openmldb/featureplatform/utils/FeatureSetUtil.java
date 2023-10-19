package com._4paradigm.openmldb.featureplatform.utils;

import com._4paradigm.openmldb.featureplatform.dao.FeatureViewService;
import com._4paradigm.openmldb.featureplatform.dao.model.FeatureView;
import com._4paradigm.openmldb.sdk.impl.SqlClusterExecutor;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handle the feature set string which may contain feature view and single feature. For example: "v1,v2:c1,v2:c2".
 */
public class FeatureSetUtil {

    /**
     * Convert the feature set to SQL text.
     *
     * @param featureSetString
     * @return
     * @throws SQLException
     */
    public static String featureSetToSql(SqlClusterExecutor sqlExecutor, FeatureViewService featureViewService,
                                         String featureSetString, List<String> joinKeys) throws SQLException {

        // Travel the feature set and get chosen features and feature views
        boolean shouldAddSimpleProject = false;
        List<String> chosenFeatureNames = new ArrayList<>();
        List<String> chosenFeatureViewNames = new ArrayList<>();
        List<FeatureView> chosenFeatureViews = new ArrayList<>();

        String[] featureOrFeatureViewList = featureSetString.split(",");

        for (String featureOrFeatureView : featureOrFeatureViewList) {
            if (featureOrFeatureView.contains(":")) { // Handle as single feature
                // Add simple project if we have chosen single feature
                shouldAddSimpleProject = true;

                String fullFeatureName = featureOrFeatureView;
                String featureViewName = fullFeatureName.split(":")[0];
                String featureName = fullFeatureName.split(":")[1];

                if (!chosenFeatureNames.contains(featureName)) {
                    chosenFeatureNames.add(featureName);
                }

                if (!chosenFeatureViewNames.contains(featureViewName)) {
                    chosenFeatureViewNames.add(featureViewName);
                    chosenFeatureViews.add(featureViewService.getFeatureViewByName(featureViewName));
                }

            } else { // Handle as feature view
                String featureViewName = featureOrFeatureView;
                FeatureView featureView = featureViewService.getFeatureViewByName(featureViewName);

                List<String> featureNames = featureView.getFeatureNames();
                for (String featureName: featureNames) {
                    if (!chosenFeatureNames.contains(featureName)) {
                        chosenFeatureNames.add(featureName);
                    }
                }

                if (!chosenFeatureViewNames.contains(featureViewName)) {
                    chosenFeatureViewNames.add(featureViewName);
                    chosenFeatureViews.add(featureView);
                }
            }
        }

        if (chosenFeatureNames.isEmpty() || chosenFeatureViewNames.isEmpty()) {
            throw new SQLException("The chosen feature and feature view is empty");
        }

        // Get all SQLs to merged
        List<String> sqlList = new ArrayList<>();

        String db = chosenFeatureViews.get(0).getDb();
        for (FeatureView featureView: chosenFeatureViews) {
            if (!featureView.getDb().equals(db)) {
                throw new SQLException(String.format("The chosen feature views are from different db, like %s and %s",
                        db, featureView.getDb()));
            }

            sqlList.add(featureView.getSql());
        }

        // Merge the SQLs
        String mergedSql = sqlList.get(0);
        if (sqlList.size() > 1) {
            mergedSql = SqlClusterExecutor.mergeSQL(sqlList, db, joinKeys,
                    OpenmldbTableUtil.getSystemSchemaMaps(sqlExecutor));
        }

        if (mergedSql.endsWith(";")) {
            mergedSql = mergedSql.substring(0, mergedSql.length()-1);
        }

        // Add simple project for merged SQL
        String outputSql = mergedSql;

        if (shouldAddSimpleProject) {
            outputSql = String.format("SELECT %s FROM (%s)", String.join(",", chosenFeatureNames), mergedSql);
        }

        return outputSql;
    }

    public static List<String> featureSetToFeatureNames(FeatureViewService featureViewService, String featureSetString)
            throws SQLException {

        List<String> chosenFeatureNames = new ArrayList<>();

        String[] featureOrFeatureViewList = featureSetString.split(",");

        for (String featureOrFeatureView : featureOrFeatureViewList) {
            if (featureOrFeatureView.contains(":")) { // Handle as single feature
                String fullFeatureName = featureOrFeatureView;
                chosenFeatureNames.add(fullFeatureName);
            } else { // Handle as feature view
                String featureViewName = featureOrFeatureView;
                FeatureView featureView = featureViewService.getFeatureViewByName(featureViewName);
                List<String> featureNames = featureView.getFeatureNames();
                for (String featureName : featureNames) {
                    String fullFeatureName = String.format("%s:%s", featureViewName, featureName);
                    if (!chosenFeatureNames.contains(fullFeatureName)) {
                        chosenFeatureNames.add(fullFeatureName);
                    }
                }
            }
        }

        return chosenFeatureNames;
    }

    public static String getDbFromFeatureSet(FeatureViewService featureViewService, String featureSetString)
            throws SQLException {

        String featureOrFeatureView = featureSetString.split(",")[0];

        // Get the first feature view name
        String featureViewName = "";
        if (featureOrFeatureView.contains(":")) { // Handle as single feature
            featureViewName = featureOrFeatureView.split(":")[0];
        } else { // Handle as feature view
            featureViewName = featureOrFeatureView;
        }

        FeatureView featureView = featureViewService.getFeatureViewByName(featureViewName);

        return featureView.getDb();
    }

}