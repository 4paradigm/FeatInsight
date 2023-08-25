package com._4paradigm.openmldb.featureplatform.dao;

import com._4paradigm.openmldb.common.Pair;
import com._4paradigm.openmldb.featureplatform.dao.model.*;
import com._4paradigm.openmldb.featureplatform.utils.OpenmldbTableUtil;
import com._4paradigm.openmldb.featureplatform.utils.TypeUtil;
import com._4paradigm.openmldb.sdk.Column;
import com._4paradigm.openmldb.sdk.Schema;
import com._4paradigm.openmldb.sdk.impl.SqlClusterExecutor;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class FeatureServiceService {

    @Autowired
    private Environment env;

    private final Connection openmldbConnection;
    private final SqlClusterExecutor openmldbSqlExecutor;

    @Autowired
    public FeatureServiceService(Connection openmldbConnection, SqlClusterExecutor openmldbSqlExecutor) {
        this.openmldbConnection = openmldbConnection;
        this.openmldbSqlExecutor = openmldbSqlExecutor;
    }


    public List<FeatureService> getFeatureServices() throws SQLException {
        String sql = "SELECT name, version, feature_list, db, sql, deployment, description FROM SYSTEM_FEATURE_PLATFORM.feature_services";

        ArrayList<FeatureService> featureServices = new ArrayList<>();

        Statement openmldbStatement = openmldbConnection.createStatement();
        openmldbStatement.execute(sql);
        ResultSet result = openmldbStatement.getResultSet();

        while (result.next()) {
            FeatureService featureService = new FeatureService(result.getString(1), result.getString(2), result.getString(3), result.getString(4), result.getString(5), result.getString(6), result.getString(7));
            featureServices.add(featureService);
        }

        return featureServices;
    }

    public List<FeatureService> getLatestFeatureServices() throws SQLException {
        ArrayList<FeatureService> featureServices = new ArrayList<>();

        Map<String, String> latestVersionMap = new HashMap<>();

        String sql = "SELECT name, version FROM SYSTEM_FEATURE_PLATFORM.latest_feature_services";

        Statement openmldbStatement = openmldbConnection.createStatement();
        openmldbStatement.execute(sql);
        ResultSet result = openmldbStatement.getResultSet();

        while (result.next()) {
            String name = result.getString(1);
            String version = result.getString(2);

            latestVersionMap.put(name, version);
        }

        sql = "SELECT name, version, feature_list, db, sql, deployment, description FROM SYSTEM_FEATURE_PLATFORM.feature_services";
        openmldbStatement = openmldbConnection.createStatement();
        openmldbStatement.execute(sql);
        result = openmldbStatement.getResultSet();

        while (result.next()) {

            String name = result.getString(1);
            String version = result.getString(2);

            if (!latestVersionMap.containsKey(name)) {
                System.out.println("Error and fail to find latest version of feature service name: " + name);
            } else {
                if (latestVersionMap.get(name).equals(version)) {
                    FeatureService featureService = new FeatureService(name, version, result.getString(3), result.getString(4), result.getString(5), result.getString(6), result.getString(7));
                    featureServices.add(featureService);
                }
            }
        }

        return featureServices;
    }

    public FeatureService getFeatureServiceByName(String name) throws SQLException {
        String latestVersion = getLatestVersion(name);
        return getFeatureServiceByNameAndVersion(name, latestVersion);
    }

    public LatestFeatureService getLatestFeatureServiceByName(String name) throws SQLException {
        String sql = String.format("SELECT name, version, db, deployment FROM SYSTEM_FEATURE_PLATFORM.latest_feature_services WHERE name='%s'", name);
        Statement openmldbStatement = openmldbConnection.createStatement();
        openmldbStatement.execute(sql);
        ResultSet result = openmldbStatement.getResultSet();

        if (result.getFetchSize() == 0) {
            throw new SQLException("Can not get latest FeatureService with name: %s", name);
        } else if (result.getFetchSize() > 1) {
            throw new SQLException("Get more latest FeatureService with the same name");
        } else {
            result.next();
            LatestFeatureService latestFeatureService = new LatestFeatureService(result.getString(1), result.getString(2), result.getString(3), result.getString(4));
            openmldbStatement.close();
            return latestFeatureService;
        }
    }

    public FeatureService getFeatureServiceByNameAndVersion(String name, String version) throws SQLException {
        String sql = String.format("SELECT name, version, feature_list, db, sql, deployment, description FROM SYSTEM_FEATURE_PLATFORM.feature_services WHERE name='%s' AND version='%s'", name, version);
        Statement openmldbStatement = openmldbConnection.createStatement();
        openmldbStatement.execute(sql);
        ResultSet result = openmldbStatement.getResultSet();

        if (result.getFetchSize() == 0) {
            throw new SQLException(String.format("Can not get FeatureService with name: %s and version: %s", name, version));
        } else if (result.getFetchSize() > 1) {
            throw new SQLException(String.format("Get more FeatureService with the name: %s and version: %s", name, version));
        } else {
            while (result.next()) {
                FeatureService featureService = new FeatureService(result.getString(1), result.getString(2), result.getString(3), result.getString(4), result.getString(5), result.getString(6), result.getString(7));

                openmldbStatement.close();
                return featureService;
            }
            return null;
        }
    }

    public String mergeSqlList(SqlClusterExecutor openmldbSqlExecutor, List<String> sqlList, String db, List<String> joinKeys, Map<String, Map<String, Schema>> tableSchema) throws SQLException {
        if (sqlList.size() == 1) {
            return sqlList.get(0);
        } else {
            System.out.println("Try to merge SQLs: " + sqlList);
            String mergeSql = SqlClusterExecutor.mergeSQL(sqlList, db, joinKeys, tableSchema);
            // TODO: Use for package with openmldb-0.8
            //String mergeSql = sqlList.get(0);
            System.out.println("Try to merge SQLs and get merged SQL: " + mergeSql);
            return mergeSql;
        }
    }

    public FeatureService createFeatureService(FeatureService featureService) throws SQLException {
        Statement openmldbStatement = openmldbConnection.createStatement();

        // Get name and feature_list
        FeatureService newFeatureService = new FeatureService();
        newFeatureService.setName(featureService.getName());
        newFeatureService.setVersion(featureService.getVersion());
        newFeatureService.setFeatureList(featureService.getFeatureList());
        newFeatureService.setDescription(featureService.getDescription());

        // Merge SQL from FeatureViews
        List<String> sqlList = new ArrayList<>();
        String[] featureList = featureService.getFeatureList().split(",");
        FeatureViewService featureViewService = new FeatureViewService(openmldbConnection, openmldbSqlExecutor);
        FeatureView latestFeatureView = null;
        for (String splitFeatureList : featureList) {
            String featureListItem = splitFeatureList.trim();
            if (!featureListItem.equals("")) {
                // TODO: Support get item by feature name instead of all features from feature view
                FeatureView featureView = featureViewService.getFeatureViewByName(featureListItem);
                if (featureView == null) {
                    System.out.println("Can not get feature view by name: " + featureListItem);
                    return null;
                }
                sqlList.add(featureView.getSql());
                // TODO: Maks sure all the features use the same db
                latestFeatureView = featureView;
            }
        }

        String db = latestFeatureView.getDb();
        newFeatureService.setDb(db);

        if (sqlList.size() == 0) {
            System.out.println("Can not get sql from feature views: " + String.join(",", featureList));
            return null;
        }

        EntityService entityService = new EntityService(openmldbConnection);
        List<String> joinKeys = new ArrayList<>();
        for (String rawEntityName : latestFeatureView.getEntityNames().split(",")) {
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

        String mergedSql = mergeSqlList(openmldbSqlExecutor, sqlList, db, joinKeys, OpenmldbTableUtil.getSystemSchemaMaps(openmldbSqlExecutor));
        String deploymentName = String.format("FEATURE_PLATFORM_%s_%s", featureService.getName(), featureService.getVersion());

        // If deployment is provided
        if (featureService.getDeployment() != null && !featureService.getDeployment().isEmpty()) {
            deploymentName = featureService.getDeployment();
        } else {
            if (!db.equals("")) {
                openmldbStatement.execute("USE " + db);
            }

            // Deploy with SQL
            // TODO: Skip index check for compatibility of old OpenMLDB cluster
            String deploymentSql = String.format("DEPLOY %s OPTIONS (SKIP_INDEX_CHECK=\"TRUE\") %s", deploymentName, mergedSql);
            System.out.println("Try to create deployment with SQL: " + deploymentSql);
            openmldbStatement.execute(deploymentSql);
        }

        newFeatureService.setSql(mergedSql);
        newFeatureService.setDeployment(deploymentName);

        // TODO: Check if name and version is existed or not

        // TODO: It would be better to use JDBC prepared statement from connection
        String sql = String.format("INSERT INTO SYSTEM_FEATURE_PLATFORM.feature_services (name, version, feature_list, db, sql, deployment, description) values ('%s', '%s', '%s', '%s', '%s', '%s', '%s')", featureService.getName(), featureService.getVersion(), featureService.getFeatureList(), db, mergedSql, deploymentName, featureService.getDescription());
        openmldbStatement.execute(sql);

        // Add to latest feature service
        sql = String.format("SELECT count(name) FROM SYSTEM_FEATURE_PLATFORM.latest_feature_services WHERE name='%s'", featureService.getName());
        openmldbStatement.execute(sql);
        ResultSet result = openmldbStatement.getResultSet();
        result.next();
        if (result.getLong(1) == 0) { // Has no other versions
            sql = String.format("INSERT INTO SYSTEM_FEATURE_PLATFORM.latest_feature_services (name, version, db, deployment) values ('%s', '%s', '%s', '%s')", featureService.getName(), featureService.getVersion(), db, deploymentName);
            openmldbStatement.execute(sql);
        }

        openmldbStatement.close();
        return newFeatureService;
    }

    public FeatureService createFeatureServiceFromDeployment(FeatureServiceDeploymentRequest request) throws SQLException {
        String featureServiceName = request.getName();
        String version = request.getVersion();
        String db = request.getDb();
        String deploymentName = request.getDeploymentName();
        String description = request.getDescription();

        Statement openmldbStatement = openmldbConnection.createStatement();
        if (!db.equals("")) {
            openmldbStatement.execute("USE " + db);
        }

        String sql = String.format("SHOW DEPLOYMENT %s", deploymentName);
        openmldbStatement.execute(sql);

        ResultSet result = openmldbStatement.getResultSet();

        while (result.next()) {
            /*
             ......
             --------------------------
             SQL
             --------------------------
             DEPLOY demo_deploy SELECT
               col
             FROM
               t1
             ;
             --------------------------
             .......
             */
            String resultsetString = result.getString(1);
            String internalSqlString = resultsetString.split("SQL")[1].trim();
            int sqlStartIndex = -1;
            int sqlEndIndex = -1;
            boolean getStartIndex = false;
            for (int i = 0; i < internalSqlString.length(); ++i) {
                if (internalSqlString.charAt(i) != '-') {
                    if (!getStartIndex) {
                        sqlStartIndex = i;
                        getStartIndex = true;
                    }
                }
                if (getStartIndex && internalSqlString.charAt(i) == '-') {
                    sqlEndIndex = i - 1;
                    break;
                }
            }

            if (sqlStartIndex <= 0 || sqlEndIndex <= 0) {
                System.out.println("Fail to parse from SQL result: " + resultsetString);
            }

            // Remove the DEPLOY keyword
            String deploymentSql = internalSqlString.substring(sqlStartIndex, sqlEndIndex).trim();
            String selectSql = deploymentSql.substring(deploymentSql.indexOf(" ", deploymentSql.indexOf(" ") + 1)).trim().replaceAll("[\r\n]+", " ");

            // Create feature view
            // TODO: Handle the duplicated name
            String featureViewName = featureServiceName;
            FeatureViewService featureViewService = new FeatureViewService(openmldbConnection, openmldbSqlExecutor);
            featureViewService.addFeatureView(new FeatureView(featureViewName, "", db, selectSql));

            // Add to latest feature service
            sql = String.format("SELECT count(name) FROM SYSTEM_FEATURE_PLATFORM.latest_feature_services WHERE name='%s'", featureServiceName);
            openmldbStatement.execute(sql);
            result = openmldbStatement.getResultSet();
            result.next();
            if (result.getLong(1) == 0) { // Has no other versions
                sql = String.format("INSERT INTO SYSTEM_FEATURE_PLATFORM.latest_feature_services (name, version, db, deployment) values ('%s', '%s', '%s', '%s')", featureServiceName, featureServiceName, db, deploymentName);
                openmldbStatement.execute(sql);
            }

            // Create feature service
            String featureList = featureViewName;
            FeatureService featureService = new FeatureService(featureServiceName, version, featureList, db, selectSql, deploymentName, description);
            return createFeatureService(featureService);
        }

        return null;
    }

    public List<String> getFeatureServiceVersions(String name) throws SQLException {
        ArrayList<String> versions = new ArrayList<>();

        String sql = String.format("SELECT version FROM SYSTEM_FEATURE_PLATFORM.feature_services WHERE name='%s'", name);

        Statement openmldbStatement = openmldbConnection.createStatement();
        openmldbStatement.execute(sql);
        ResultSet result = openmldbStatement.getResultSet();
        while (result.next()) {
            String version = result.getString(1);
            versions.add(version);
        }

        return versions;
    }

    public void deleteFeatureService(String name) throws SQLException {
        List<String> versions = getFeatureServiceVersions(name);

        for (String version : versions) {
            deleteFeatureService(name, version);
        }
    }

    public void deleteFeatureService(String name, String version) throws SQLException {
        Statement openmldbStatement = openmldbConnection.createStatement();

        // Delete the deployment
        FeatureService featureService = getFeatureServiceByNameAndVersion(name, version);
        if (!featureService.getDb().equals("")) {
            openmldbStatement.execute("USE " + featureService.getDb());
        }
        String dropDeploymentSql = String.format("DROP DEPLOYMENT %s", featureService.getDeployment());
        System.out.println("Try to drop deployment with sql: " + dropDeploymentSql);
        openmldbStatement.execute(dropDeploymentSql);

        // TODO: It would be better to use JDBC prepared statement from connection
        String sql = String.format("DELETE FROM SYSTEM_FEATURE_PLATFORM.feature_services WHERE name='%s' AND version='%s'", name, version);
        openmldbStatement.execute(sql);

        // Delete if it is the latest version
        if (getLatestVersion(name).equals(version)) {
            sql = String.format("DELETE FROM SYSTEM_FEATURE_PLATFORM.latest_feature_services WHERE name='%s' AND version='%s'", name, version);
            openmldbStatement.execute(sql);
        }

        openmldbStatement.close();
    }

    public void updateLatestVersion(String name, String newVersion) throws SQLException {
        String oldLatestVersion = getLatestVersion(name);
        if (!oldLatestVersion.equals(newVersion)) {
            Statement openmldbStatement = openmldbConnection.createStatement();

            String sql = String.format("DELETE FROM SYSTEM_FEATURE_PLATFORM.latest_feature_services WHERE name='%s' AND version='%s'", name, oldLatestVersion);
            openmldbStatement.execute(sql);

            FeatureService featureService = getFeatureServiceByNameAndVersion(name, newVersion);

            sql = String.format("INSERT INTO SYSTEM_FEATURE_PLATFORM.latest_feature_services (name, version, db, deployment) values ('%s', '%s', '%s', '%s')", featureService.getName(), featureService.getVersion(), featureService.getDb(), featureService.getDeployment());
            openmldbStatement.execute(sql);
        }
    }

    public ResponseEntity<String> requestFeatureService(String name, String requestData) throws IOException, SQLException {
        String apiServerEndpoint = env.getProperty("openmldb.apiserver");
        if (apiServerEndpoint == null || apiServerEndpoint.equals("")) {
            throw new IOException("Need to config openmldb.apiserver in application.yml");
        }

        // TODO: Get the db from feature service
        FeatureServiceService featureServiceService = new FeatureServiceService(openmldbConnection, openmldbSqlExecutor);
        LatestFeatureService featureService = featureServiceService.getLatestFeatureServiceByName(name);
        String db = featureService.getDb();
        String deployment = featureService.getDeployment();

        HttpClient httpClient = HttpClients.createDefault();
        String endpoint = String.format("http://%s/dbs/%s/deployments/%s", apiServerEndpoint, db, deployment);
        HttpPost postRequest = new HttpPost(endpoint);
        postRequest.setHeader("Content-Type", "application/json");
        postRequest.setEntity(new StringEntity(requestData));
        HttpResponse response = httpClient.execute(postRequest);

        int statusCode = response.getStatusLine().getStatusCode();
        HttpEntity responseEntity = response.getEntity();
        String responseBody = EntityUtils.toString(responseEntity);
        return new ResponseEntity<String>(responseBody, HttpStatus.valueOf(statusCode));
    }

    public ResponseEntity<String> requestFeatureService(String name, String version, String requestData) throws IOException, SQLException {
        String apiServerEndpoint = env.getProperty("openmldb.apiserver");
        if (apiServerEndpoint == null || apiServerEndpoint.equals("")) {
            throw new IOException("Need to config openmldb.apiserver in application.yml");
        }

        // TODO: Get the db from feature service
        FeatureServiceService featureServiceService = new FeatureServiceService(openmldbConnection, openmldbSqlExecutor);
        FeatureService featureService = featureServiceService.getFeatureServiceByNameAndVersion(name, version);
        String db = featureService.getDb();
        String deployment = featureService.getDeployment();

        HttpClient httpClient = HttpClients.createDefault();
        String endpoint = String.format("http://%s/dbs/%s/deployments/%s", apiServerEndpoint, db, deployment);
        HttpPost postRequest = new HttpPost(endpoint);
        postRequest.setHeader("Content-Type", "application/json");
        postRequest.setEntity(new StringEntity(requestData));
        HttpResponse response = httpClient.execute(postRequest);

        int statusCode = response.getStatusLine().getStatusCode();
        HttpEntity responseEntity = response.getEntity();
        String responseBody = EntityUtils.toString(responseEntity);
        return new ResponseEntity<String>(responseBody, HttpStatus.valueOf(statusCode));
    }

    public Schema getRequestSchema(String serviceName, String version) throws SQLException {
        FeatureService featureService = getFeatureServiceByNameAndVersion(serviceName, version);
        String sql = featureService.getSql();
        String db = featureService.getDb();

        List<Pair<String, String>> tables = SqlClusterExecutor.getDependentTables(sql, db, OpenmldbTableUtil.getSystemSchemaMaps(openmldbSqlExecutor));

        Pair<String, String> mainTablePair = tables.get(0);

        String mainDb = mainTablePair.getKey();
        String mainTable = mainTablePair.getValue();

        Schema schema = openmldbSqlExecutor.getTableSchema(mainDb, mainTable);
        return schema;
    }

    public Schema getRequestSchema(String serviceName) throws SQLException {
        String latestVersion = getLatestVersion(serviceName);
        return getRequestSchema(serviceName, latestVersion);
    }

    public String getRequestDemoData(String serviceName, String version) throws SQLException {
        Schema schema = getRequestSchema(serviceName, version);

        // "{\"input\": [[\"abc\", 22]]}"
        StringBuilder demoBuilder = new StringBuilder();
        demoBuilder.append("{\"input\": [[");

        for (int i = 0; i < schema.getColumnList().size(); ++i) {
            Column column = schema.getColumnList().get(i);
            column.getSqlType();
            String demoValue = TypeUtil.javaSqlTypeToDemoData(column.getSqlType());

            if (i != 0) {
                demoBuilder.append(", ");
            }

            demoBuilder.append(demoValue);
        }

        demoBuilder.append("]]}");
        return demoBuilder.toString();
    }

    public String getRequestDemoData(String serviceName) throws SQLException {
        String latestVersion = getLatestVersion(serviceName);
        return getRequestDemoData(serviceName, latestVersion);
    }

    public String getLatestVersion(String serviceName) throws SQLException {
        String sql = String.format("SELECT version FROM SYSTEM_FEATURE_PLATFORM.latest_feature_services WHERE name='%s'", serviceName);

        Statement openmldbStatement = openmldbConnection.createStatement();
        openmldbStatement.execute(sql);
        ResultSet result = openmldbStatement.getResultSet();
        while (result.next()) {
            String version = result.getString(1);
            return version;
        }

        throw new SQLException("Fail to find version for feature service: " + serviceName);
    }

    public List<String> getDependentTables(String name, String version) throws SQLException {
        FeatureService featureService = getFeatureServiceByNameAndVersion(name, version);
        List<Pair<String, String>> tables = SqlClusterExecutor.getDependentTables(featureService.getSql(), featureService.getDb(), OpenmldbTableUtil.getSystemSchemaMaps(openmldbSqlExecutor));

        List<String> fullNameTables = new ArrayList<>();
        for (Pair<String, String> tableItem : tables) {
            fullNameTables.add(tableItem.getKey() + "." + tableItem.getValue());
        }
        return fullNameTables;
    }

    public List<String> getDependentTables(String name) throws SQLException {
        String latestVersion = getLatestVersion(name);
        return getDependentTables(name, latestVersion);
    }

    public Schema getOutputSchema(String serviceName, String version) throws SQLException {
        FeatureService featureService = getFeatureServiceByNameAndVersion(serviceName, version);
        String sql = featureService.getSql();
        String db = featureService.getDb();

        Schema schema = SqlClusterExecutor.genOutputSchema(sql, db, OpenmldbTableUtil.getSystemSchemaMaps(openmldbSqlExecutor));
        return schema;
    }

    public Schema getOutputSchema(String serviceName) throws SQLException {
        String latestVersion = getLatestVersion(serviceName);
        return getOutputSchema(serviceName, latestVersion);
    }
}