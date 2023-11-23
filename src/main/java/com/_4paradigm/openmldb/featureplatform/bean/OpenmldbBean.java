package com._4paradigm.openmldb.featureplatform.bean;

import com._4paradigm.openmldb.sdk.SdkOption;
import com._4paradigm.openmldb.sdk.SqlException;
import com._4paradigm.openmldb.sdk.impl.SqlClusterExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import java.sql.SQLException;

@Configuration
public class OpenmldbBean {

    @Autowired
    private Environment env;

    @Bean
    public SqlClusterExecutor sqlExecutor() throws SQLException {
        String zkHost = env.getProperty("openmldb.zk_cluster");
        String zkPath = env.getProperty("openmldb.zk_path");

        SdkOption option = new SdkOption();
        option.setZkCluster(zkHost);
        option.setZkPath(zkPath);

        SqlClusterExecutor sqlExecutor = null;
        try {
            sqlExecutor = new SqlClusterExecutor(option);
        } catch (SqlException e) {
            throw new SQLException(e);
        }
        return sqlExecutor;
    }

}
