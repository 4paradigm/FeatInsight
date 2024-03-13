package com._4paradigm.openmldb.featureplatform.service;

import com._4paradigm.openmldb.featureplatform.dao.model.OfflineJobInfo;
import com._4paradigm.openmldb.featureplatform.utils.ResultSetUtil;
import com._4paradigm.openmldb.sdk.impl.SqlClusterExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Repository
public class OfflineJobService {

    @Autowired
    private SqlClusterExecutor sqlExecutor;

    @Autowired
    public OfflineJobService(SqlClusterExecutor sqlExecutor) {
        this.sqlExecutor = sqlExecutor;
    }

    public static OfflineJobInfo resultSetToOfflineJobInfo(ResultSet resultSet) throws SQLException {
        return new OfflineJobInfo(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3),
                resultSet.getTimestamp(4), resultSet.getTimestamp(5), resultSet.getString(6),
                resultSet.getString(7), resultSet.getString(8), resultSet.getString(9));
    }

    public List<OfflineJobInfo> getOfflineJobInfos() throws SQLException {
        Statement statement = sqlExecutor.getStatement();
        statement.execute("SET @@execute_mode='online'");

        ArrayList<OfflineJobInfo> offlineJobInfos = new ArrayList<>();

        String sql = "SELECT id, job_type, state, start_time, end_time, parameter, cluster, application_id, error " +
                "FROM __INTERNAL_DB.JOB_INFO";
        statement.execute(sql);
        ResultSet resultSet = statement.getResultSet();

        while (resultSet.next()) {
            OfflineJobInfo offlineJobInfo = resultSetToOfflineJobInfo(resultSet);
            offlineJobInfos.add(offlineJobInfo);
        }

        statement.close();
        return offlineJobInfos;
    }

    public OfflineJobInfo getOfflineJobInfo(int id) throws SQLException {
        Statement statement = sqlExecutor.getStatement();
        statement.execute("SET @@execute_mode='online'");

        String sql = "SELECT id, job_type, state, start_time, end_time, parameter, cluster, application_id, error " +
                "FROM __INTERNAL_DB.JOB_INFO WHERE id = " + id;
        statement.execute(sql);
        ResultSet resultSet = statement.getResultSet();

        ResultSetUtil.assertSizeIsOne(resultSet);
        resultSet.next();

        OfflineJobInfo offlineJobInfo = resultSetToOfflineJobInfo(resultSet);

        statement.close();
        return offlineJobInfo;
    }

    public String getOfflineJobLog(int id) throws SQLException {
        Statement statement = sqlExecutor.getStatement();
        statement.execute("SET @@execute_mode='online'");

        String sql = "SHOW JOBLOG " + id;
        statement.execute(sql);
        ResultSet resultSet = statement.getResultSet();

        ResultSetUtil.assertSizeIsOne(resultSet);
        resultSet.next();

        String log = resultSet.getString(1);

        statement.close();
        return log;
    }

    public void deleteOfflineJob(int id) throws SQLException {
        Statement statement = sqlExecutor.getStatement();
        statement.execute("SET @@execute_mode='online'");

        String sql = String.format("DELETE FROM __INTERNAL_DB.JOB_INFO WHERE id = %d", id);
        statement.execute(sql);

        statement.close();
    }

}