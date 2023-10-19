package com._4paradigm.openmldb.featureplatform.dao;

import com._4paradigm.openmldb.featureplatform.utils.OpenmldbSdkUtil;
import com._4paradigm.openmldb.featureplatform.utils.OpenmldbTableUtil;
import com._4paradigm.openmldb.jdbc.SQLResultSet;
import com._4paradigm.openmldb.sdk.Schema;
import com._4paradigm.openmldb.sdk.impl.SqlClusterExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

@Repository
public class SqlService {

    private final Environment env;

    @Autowired
    public SqlService(Environment env) {
        this.env = env;
    }

    public void initDbAndTables() throws SQLException {
        Connection connection = OpenmldbSdkUtil.getConnection(env);
        Statement openmldbStatement = connection.createStatement();

        String sql = "CREATE DATABASE IF NOT EXISTS SYSTEM_FEATURE_PLATFORM";
        openmldbStatement.execute(sql);

        sql = "CREATE TABLE IF NOT EXISTS SYSTEM_FEATURE_PLATFORM.features (feature_view_name String, feature_name string, type string, description string)";
        openmldbStatement.execute(sql);

        sql = "CREATE TABLE IF NOT EXISTS SYSTEM_FEATURE_PLATFORM.feature_views (name string, db string, sql string, description string, feature_names string)";
        openmldbStatement.execute(sql);

        sql = "CREATE TABLE IF NOT EXISTS SYSTEM_FEATURE_PLATFORM.feature_services (name string, version string, feature_names string, description string, db string, sql string, deployment string, INDEX(KEY=(name, version)))";
        openmldbStatement.execute(sql);

        sql = "CREATE TABLE IF NOT EXISTS SYSTEM_FEATURE_PLATFORM.latest_feature_services (name string, version string, db string, deployment string, INDEX(KEY=(name, version)))";
        openmldbStatement.execute(sql);

        sql = "CREATE TABLE IF NOT EXISTS SYSTEM_FEATURE_PLATFORM.training_sets (feature_names string, path string, options string, job_id int, db string, sql string)";
        openmldbStatement.execute(sql);
    }

    private boolean isDql(String sqlString) {
        // TODO: Use better methods to check
        String sql = sqlString.toLowerCase();
        return sql.startsWith("select") || sql.startsWith("show");
    }


    public SQLResultSet executeSql(String sql, boolean isOnline) throws SQLException {
        Connection connection = OpenmldbSdkUtil.getConnection(env);
        Statement openmldbStatement = connection.createStatement();

        if (isOnline) {
            openmldbStatement.execute("SET @@execute_mode='online'");
        } else {
            openmldbStatement.execute("SET @@execute_mode='offline'");
        }

        openmldbStatement.execute(sql);

        if (isDql(sql)) {
            return (SQLResultSet) openmldbStatement.getResultSet();
        } else {
            return null;
        }

    }

    public List<String> validateSql(String sql) throws SQLException {
        SqlClusterExecutor sqlExecutor = OpenmldbSdkUtil.getSqlExecutor(env);

        Map<String, Map<String, Schema>> schemaMaps = OpenmldbTableUtil.getSystemSchemaMaps(sqlExecutor);
        List<String> result = SqlClusterExecutor.validateSQLInRequest(sql, schemaMaps);

        return result;
    }

}