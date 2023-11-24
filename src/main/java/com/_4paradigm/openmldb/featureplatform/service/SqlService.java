package com._4paradigm.openmldb.featureplatform.service;

import com._4paradigm.openmldb.featureplatform.dao.model.OfflineJobInfo;
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
import java.util.ArrayList;
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
                "feature_name string, type string, description string, INDEX(KEY=(feature_view_name, feature_name)))";
        statement.execute(sql);

        sql = "CREATE TABLE IF NOT EXISTS SYSTEM_FEATURE_PLATFORM.feature_views (name string, db string, sql string, " +
                "description string, feature_names string, INDEX(KEY=name))";
        statement.execute(sql);

        sql = "CREATE TABLE IF NOT EXISTS SYSTEM_FEATURE_PLATFORM.feature_services (name string, version string, " +
                "feature_names string, description string, db string, sql string, deployment string, " +
                "INDEX(KEY=(name, version)))";
        statement.execute(sql);

        sql = "CREATE TABLE IF NOT EXISTS SYSTEM_FEATURE_PLATFORM.latest_feature_services (name string, " +
                "version string, db string, deployment string, INDEX(KEY=(name, version)))";
        statement.execute(sql);

        sql = "CREATE TABLE IF NOT EXISTS SYSTEM_FEATURE_PLATFORM.offline_samples (feature_names string, path string, " +
                "options string, job_id int, db string, sql string, INDEX(KEY=job_id))";
        statement.execute(sql);

        statement.close();
    }

    private boolean isDql(String sqlString) {
        // TODO: Use better methods to check
        String sql = sqlString.toLowerCase();
        return sql.startsWith("select") || sql.startsWith("show") || sql.startsWith("with");
    }

    public OfflineJobInfo executeOfflineSql(String sql) throws SQLException {
        Statement statement = sqlExecutor.getStatement();
        statement.execute("SET @@execute_mode='offline'");

        String[] sqls = sql.split(";");
        for (String singleLineSql: sqls) {
            if (!singleLineSql.trim().isEmpty()) {
                statement.execute(singleLineSql.trim());
            }
        }

        ResultSet resultSet = statement.getResultSet();

        ResultSetUtil.assertSizeIsOne(resultSet);
        resultSet.next();

        OfflineJobInfo offlineJobInfo = OfflineJobService.resultSetToOfflineJobInfo(resultSet);

        statement.close();
        return offlineJobInfo;
    }

    /**
     *
     *
     * @param sql
     * @return the first element is schema(list of column name), other elements are row data(list of string value)
     * @throws SQLException
     */
    public List<List<String>> executeOnlineSql(String sql) throws SQLException {
        Statement statement = sqlExecutor.getStatement();
        statement.execute("SET @@execute_mode='online'");

        String[] sqls = sql.split(";");
        for (String singleLineSql: sqls) {
            if (!singleLineSql.trim().isEmpty()) {
                statement.execute(singleLineSql.trim());
            }
        }

        String lastSql = sqls[sqls.length - 1].trim();
        if (lastSql.isEmpty()) {
            lastSql = sqls[sqls.length - 2].trim();
        }

        List<List<String>> returnList = new ArrayList<>();
        // TODO: Check if has result set
        if (isDql(lastSql)) {
            SQLResultSet resultSet = (SQLResultSet) statement.getResultSet();
            returnList = ResultSetUtil.resultSetToStringArray(resultSet);
            resultSet.close();
        }

        return returnList;
    }

    public int importData(String sql, boolean isOnline) throws SQLException {
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