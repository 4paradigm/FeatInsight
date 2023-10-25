package com._4paradigm.openmldb.featureplatform.service;

import com._4paradigm.openmldb.featureplatform.utils.OpenmldbTableUtil;
import com._4paradigm.openmldb.featureplatform.utils.ResultSetUtil;
import com._4paradigm.openmldb.jdbc.SQLResultSet;
import com._4paradigm.openmldb.sdk.Schema;
import com._4paradigm.openmldb.sdk.impl.SqlClusterExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

@Repository
public class SqlService {

    @Autowired
    private SqlClusterExecutor sqlExecutor;

    @Autowired
    public SqlService(SqlClusterExecutor sqlExecutor) {
        this.sqlExecutor = sqlExecutor;
    }

    public void initDbAndTables() throws SQLException {
        Statement statement = sqlExecutor.getStatement();
        statement.execute("SET @@execute_mode='online'");

        String sql = "CREATE DATABASE IF NOT EXISTS SYSTEM_FEATURE_PLATFORM";
        statement.execute(sql);

        sql = "CREATE TABLE IF NOT EXISTS SYSTEM_FEATURE_PLATFORM.features (feature_view_name String, " +
                "feature_name string, type string, description string)";
        statement.execute(sql);

        sql = "CREATE TABLE IF NOT EXISTS SYSTEM_FEATURE_PLATFORM.feature_views (name string, db string, sql string, " +
                "description string, feature_names string)";
        statement.execute(sql);

        sql = "CREATE TABLE IF NOT EXISTS SYSTEM_FEATURE_PLATFORM.feature_services (name string, version string, " +
                "feature_names string, description string, db string, sql string, deployment string, " +
                "INDEX(KEY=(name, version)))";
        statement.execute(sql);

        sql = "CREATE TABLE IF NOT EXISTS SYSTEM_FEATURE_PLATFORM.latest_feature_services (name string, " +
                "version string, db string, deployment string, INDEX(KEY=(name, version)))";
        statement.execute(sql);

        sql = "CREATE TABLE IF NOT EXISTS SYSTEM_FEATURE_PLATFORM.offline_samples (feature_names string, path string, " +
                "options string, job_id int, db string, sql string)";
        statement.execute(sql);

        statement.close();
    }

    private boolean isDql(String sqlString) {
        // TODO: Use better methods to check
        String sql = sqlString.toLowerCase();
        return sql.startsWith("select") || sql.startsWith("show");
    }

    public SQLResultSet executeSql(String sql, boolean isOnline) throws SQLException {
        Statement statement = sqlExecutor.getStatement();

        if (isOnline) {
            statement.execute("SET @@execute_mode='online'");
        } else {
            statement.execute("SET @@execute_mode='offline'");
            // TODO: CREATE TABLE LIKE Parquet may not use sync_mode nor raise timeout exception
            //statement.execute("SET @@sync_job=false");
            //statement.execute("SET @@job_timeout=100");
        }

        statement.execute(sql);

        if (isDql(sql)) {
            return (SQLResultSet) statement.getResultSet();
        } else {
            return null;
        }

    }

    public int importSql(String sql, boolean isOnline) throws SQLException {
        Statement statement = sqlExecutor.getStatement();

        if (isOnline) {
            statement.execute("SET @@execute_mode='online'");
        } else {
            statement.execute("SET @@execute_mode='offline'");
        }

        statement.execute(sql);

        ResultSet resultSet = statement.getResultSet();

        ResultSetUtil.assertSizeIsOne(resultSet);
        resultSet.next();

        int jobId = resultSet.getInt(1);
        return jobId;
    }

    public List<String> validateSql(String sql) throws SQLException {
        Map<String, Map<String, Schema>> schemaMaps = OpenmldbTableUtil.getSystemSchemaMaps(sqlExecutor);
        List<String> result = SqlClusterExecutor.validateSQLInRequest(sql, schemaMaps);

        return result;
    }

}