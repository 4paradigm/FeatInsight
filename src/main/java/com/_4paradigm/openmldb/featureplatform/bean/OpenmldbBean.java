package com._4paradigm.openmldb.featureplatform.bean;

import com._4paradigm.openmldb.sdk.SdkOption;
import com._4paradigm.openmldb.sdk.SqlException;
import com._4paradigm.openmldb.sdk.impl.SqlClusterExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@Configuration
public class OpenmldbBean {

    @Autowired
    private Environment env;

    @Bean
    public Connection openmldbConnection() {
        // Read config from yaml file
        String zkHost = env.getProperty("openmldb.zk_cluster");
        String zkPath = env.getProperty("openmldb.zk_path");

        Connection connection = null;

        try {
            Class.forName("com._4paradigm.openmldb.jdbc.SQLDriver");
            System.out.println("Try to connect OpenMLDB with zk host: " + zkHost + ", path: " + zkPath);
            connection = DriverManager.getConnection("jdbc:openmldb:///?zk=" + zkHost + "&zkPath=" + zkPath);
            Statement stmt = connection.createStatement();

            stmt.execute("SET @@execute_mode='online'");
            stmt.execute("CREATE DATABASE IF NOT EXISTS SYSTEM_FEATURE_PLATFORM");
            stmt.execute("USE SYSTEM_FEATURE_PLATFORM");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return connection;
    }

    @Bean
    public Statement openmldbStatement() {
        Connection openmldbConnection = openmldbConnection();
        Statement openmldbStatement = null;

        try {
            openmldbStatement = openmldbConnection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return openmldbStatement;
    }


    @Bean
    public SqlClusterExecutor openmldbSqlExecutor() throws SqlException {
        String zkHost = env.getProperty("openmldb.zk_cluster");
        String zkPath = env.getProperty("openmldb.zk_path");

        SdkOption option = new SdkOption();
        option.setZkCluster(zkHost);
        option.setZkPath(zkPath);
        SqlClusterExecutor sqlExecutor = new SqlClusterExecutor(option);
        return sqlExecutor;
    }


}
