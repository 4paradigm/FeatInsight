package com._4paradigm.openmldb.featureplatform.dao;

import com._4paradigm.openmldb.featureplatform.dao.model.*;
import com._4paradigm.openmldb.sdk.impl.SqlClusterExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class OfflineJobService {

    @Autowired
    private Environment env;

    private final Connection openmldbConnection;
    private final SqlClusterExecutor openmldbSqlExecutor;

    @Autowired
    public OfflineJobService(Connection openmldbConnection, SqlClusterExecutor openmldbSqlExecutor) {
        this.openmldbConnection = openmldbConnection;
        this.openmldbSqlExecutor = openmldbSqlExecutor;
    }


    public List<OfflineJobInfo> getOfflineJobInfos() throws SQLException {
        String sql = "SELECT id, job_type, state, start_time, end_time, parameter, cluster, application_id, error FROM __INTERNAL_DB.JOB_INFO";

        ArrayList<OfflineJobInfo> offlineJobInfos = new ArrayList<>();

        Statement openmldbStatement = openmldbConnection.createStatement();
        openmldbStatement.execute(sql);
        ResultSet result = openmldbStatement.getResultSet();

        while (result.next()) {
            OfflineJobInfo offlineJobInfo = new OfflineJobInfo(result.getInt(1), result.getString(2), result.getString(3), result.getTimestamp(4), result.getTimestamp(5), result.getString(6), result.getString(7), result.getString(8), result.getString(9));
            offlineJobInfos.add(offlineJobInfo);
        }

        return offlineJobInfos;
    }

    public OfflineJobInfo getOfflineJobInfo(int id) throws SQLException {
        String sql = "SELECT id, job_type, state, start_time, end_time, parameter, cluster, application_id, error FROM __INTERNAL_DB.JOB_INFO WHERE id = " + id;

        Statement openmldbStatement = openmldbConnection.createStatement();
        openmldbStatement.execute(sql);
        ResultSet result = openmldbStatement.getResultSet();

        while (result.next()) {
            OfflineJobInfo offlineJobInfo = new OfflineJobInfo(result.getInt(1), result.getString(2), result.getString(3), result.getTimestamp(4), result.getTimestamp(5), result.getString(6), result.getString(7), result.getString(8), result.getString(9));
            return offlineJobInfo;
        }

        // TODO: throw exception if not found
        return null;
    }

}