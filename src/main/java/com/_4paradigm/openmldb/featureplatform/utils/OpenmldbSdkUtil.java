package com._4paradigm.openmldb.featureplatform.utils;

import com._4paradigm.openmldb.sdk.SdkOption;
import com._4paradigm.openmldb.sdk.SqlException;
import com._4paradigm.openmldb.sdk.impl.SqlClusterExecutor;
import org.springframework.core.env.Environment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class OpenmldbSdkUtil {

    public static Connection getConnection(Environment env) throws SQLException {
        // Read config from spring config file
        String zkHost = env.getProperty("openmldb.zk_cluster");
        String zkPath = env.getProperty("openmldb.zk_path");

        try {
            Class.forName("com._4paradigm.openmldb.jdbc.SQLDriver");
            return DriverManager.getConnection("jdbc:openmldb:///?zk=" + zkHost + "&zkPath=" + zkPath);
        } catch (ClassNotFoundException e) {
            throw new SQLException(e.getMessage());
        }
    }

    public static SqlClusterExecutor getSqlExecutor(Environment env) throws SQLException {
        String zkHost = env.getProperty("openmldb.zk_cluster");
        String zkPath = env.getProperty("openmldb.zk_path");

        SdkOption option = new SdkOption();
        option.setZkCluster(zkHost);
        option.setZkPath(zkPath);
        try {
            return new SqlClusterExecutor(option);
        } catch (SqlException e) {
            throw new SQLException(e.getMessage());
        }
    }

}