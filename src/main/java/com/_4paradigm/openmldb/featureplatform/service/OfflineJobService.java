package com._4paradigm.openmldb.featureplatform.service;

import com._4paradigm.openmldb.featureplatform.dao.model.FesqlDagMeta;
import com._4paradigm.openmldb.featureplatform.dao.model.OfflineJobInfo;
import com._4paradigm.openmldb.featureplatform.dao.model.TableJob;
import com._4paradigm.openmldb.featureplatform.dao.model.ThreadLocalSqlExecutor;
import com._4paradigm.openmldb.featureplatform.utils.ResultSetUtil;
import com._4paradigm.openmldb.sdk.impl.SqlClusterExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
public class OfflineJobService {

    public static OfflineJobInfo resultSetToOfflineJobInfo(ResultSet resultSet) throws SQLException {
        return new OfflineJobInfo(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3), resultSet.getTimestamp(4), resultSet.getTimestamp(5), resultSet.getString(6), resultSet.getString(7), resultSet.getString(8), resultSet.getString(9));
    }

    public List<OfflineJobInfo> getOfflineJobInfos() throws SQLException {
        SqlClusterExecutor sqlExecutor = ThreadLocalSqlExecutor.getSqlExecutor();
        Statement statement = sqlExecutor.getStatement();
        statement.execute("SET @@execute_mode='online'");

        ArrayList<OfflineJobInfo> offlineJobInfos = new ArrayList<>();

        String sql = "SELECT id, job_type, state, start_time, end_time, parameter, cluster, application_id, error " + "FROM __INTERNAL_DB.JOB_INFO";
        statement.execute(sql);
        ResultSet resultSet = statement.getResultSet();

        while (resultSet.next()) {
            OfflineJobInfo offlineJobInfo = resultSetToOfflineJobInfo(resultSet);
            offlineJobInfos.add(offlineJobInfo);
        }

        statement.close();
        return this.supplyOfflineJobList(offlineJobInfos);
    }

    public OfflineJobInfo getOfflineJobInfoOfDbAndTable(int id) throws SQLException {
        OfflineJobInfo offlineJobInfo = this.getOfflineJobInfo(id);
        return this.supplyOfflineJobInfo(offlineJobInfo);
    }

    public OfflineJobInfo getOfflineJobInfo(int id) throws SQLException {
        SqlClusterExecutor sqlExecutor = ThreadLocalSqlExecutor.getSqlExecutor();
        Statement statement = sqlExecutor.getStatement();
        statement.execute("SET @@execute_mode='online'");

        String sql = "SELECT id, job_type, state, start_time, end_time, parameter, cluster, application_id, error " + "FROM __INTERNAL_DB.JOB_INFO WHERE id = " + id;
        statement.execute(sql);
        ResultSet resultSet = statement.getResultSet();

        ResultSetUtil.assertSizeIsOne(resultSet);
        resultSet.next();

        OfflineJobInfo offlineJobInfo = resultSetToOfflineJobInfo(resultSet);

        statement.close();
        return offlineJobInfo;
    }

    private List<OfflineJobInfo> supplyOfflineJobList(List<OfflineJobInfo> offlineJobInfos) throws SQLException {
        if (offlineJobInfos.isEmpty()) {
            return offlineJobInfos;
        }
        Map<Integer, TableJob> tableJobMap = new HashMap<>();
        for (TableJob tableJob : this.getAllTableJob()) {
            tableJobMap.put(tableJob.getJobId(), tableJob);
        }
        for (OfflineJobInfo offlineJobInfo : offlineJobInfos) {
            TableJob tableJob = tableJobMap.get(offlineJobInfo.getId());
            if (null != tableJob) {
                offlineJobInfo.setDb(tableJob.getDb());
                offlineJobInfo.setTable(tableJob.getTable());
            }
        }
        return offlineJobInfos;
    }

    private OfflineJobInfo supplyOfflineJobInfo(OfflineJobInfo offlineJobInfo) throws SQLException {
        TableJob tableJob = this.getTableJobById(offlineJobInfo.getId());
        if (null != tableJob) {
            offlineJobInfo.setDb(tableJob.getDb());
            offlineJobInfo.setTable(tableJob.getTable());
        }
        return offlineJobInfo;
    }

    public String getOfflineJobLog(int id) throws SQLException {
        SqlClusterExecutor sqlExecutor = ThreadLocalSqlExecutor.getSqlExecutor();
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
        SqlClusterExecutor sqlExecutor = ThreadLocalSqlExecutor.getSqlExecutor();
        Statement statement = sqlExecutor.getStatement();
        statement.execute("SET @@execute_mode='online'");

        String sql = String.format("DELETE FROM __INTERNAL_DB.JOB_INFO WHERE id = %d", id);
        statement.execute(sql);

        statement.close();
    }


    public TableJob getTableJobById(int jobId) throws SQLException {
        SqlClusterExecutor sqlExecutor = ThreadLocalSqlExecutor.getSqlExecutor();
        Statement statement = sqlExecutor.getStatement();
        statement.execute("SET @@execute_mode='online'");
        String sql = String.format("SELECT job_id, job_type, db, table, sql from SYSTEM_FEATURE_PLATFORM.table_jobs WHERE job_id=%d", jobId);
        statement.execute(sql);
        ResultSet resultSet = statement.getResultSet();
        TableJob tableJob = null;
        if (resultSet.next()) {
            tableJob = resultSetToTableJob(resultSet);
        }
        statement.close();
        return tableJob;
    }

    public List<TableJob> getAllTableJob() throws SQLException {
        SqlClusterExecutor sqlExecutor = ThreadLocalSqlExecutor.getSqlExecutor();
        Statement statement = sqlExecutor.getStatement();
        statement.execute("SET @@execute_mode='online'");
        String sql = "SELECT job_id, job_type, db, table, sql from SYSTEM_FEATURE_PLATFORM.table_jobs";
        statement.execute(sql);
        ResultSet resultSet = statement.getResultSet();
        List<TableJob> tableJobList = new ArrayList<>();
        while (resultSet.next()) {
            tableJobList.add(resultSetToTableJob(resultSet));
        }
        statement.close();
        return tableJobList;
    }

    public TableJob resultSetToTableJob(ResultSet resultSet) throws SQLException {
        TableJob tableJob = new TableJob();
        tableJob.setJobId(resultSet.getInt(1));
        tableJob.setJobType(resultSet.getString(2));
        tableJob.setDb(resultSet.getString(3));
        tableJob.setTable(resultSet.getString(4));
        tableJob.setSql(resultSet.getString(5));
        return tableJob;
    }

    public int addTableJob(int jobId, String sqlString) throws SQLException {
        SqlClusterExecutor sqlExecutor = ThreadLocalSqlExecutor.getSqlExecutor();
        Statement statement = sqlExecutor.getStatement();

        OfflineJobInfo jobInfo = this.getOfflineJobInfo(jobId);
        String[] dbAndTable = this.parseDbTableBySql(sqlString);
        if (null != dbAndTable && dbAndTable.length == 2) {
            sqlString = sqlString.replace("'", "\\'");
            String sql = String.format("INSERT INTO SYSTEM_FEATURE_PLATFORM.table_jobs (job_id, job_type, db, table, sql) " + "values (%s, '%s', '%s', '%s', '%s')", jobInfo.getId(), jobInfo.getJobType(), dbAndTable[0], dbAndTable[1], sqlString);
            statement.execute(sql);
        }
        return jobId;
    }

    public String[] parseDbTableBySql(String sql) {
        String sqlString = new String(sql);
        sqlString = sqlString.replaceAll("\\s+", " ").toLowerCase();
        String tagString = null;
        if (sqlString.contains("into table")) {
            tagString = "into table";
        } else if (sqlString.contains("insert into")) {
            tagString = "insert into";
        }
        if (null != tagString) {
            int index = sqlString.indexOf(tagString);
            index += tagString.length();
            String dbAndTableString = sqlString.substring(index).trim().split(" ")[0];
            if (dbAndTableString.contains(".")) {
                return dbAndTableString.split("\\.");
            } else {
                return new String[]{"", dbAndTableString};
            }
        }
        return null;
    }

}