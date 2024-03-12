package com._4paradigm.openmldb.featureplatform.service;

import com._4paradigm.openmldb.featureplatform.controller.DatabaseController;
import com._4paradigm.openmldb.featureplatform.dao.model.SimpleTableInfo;
import com._4paradigm.openmldb.featureplatform.utils.ResultSetUtil;
import com._4paradigm.openmldb.sdk.Schema;
import com._4paradigm.openmldb.sdk.SdkOption;
import com._4paradigm.openmldb.sdk.SqlException;
import com._4paradigm.openmldb.sdk.impl.SqlClusterExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Repository
public class LoginService {

    private static final Logger logger = LoggerFactory.getLogger(LoginService.class);

    @Autowired
    private SqlClusterExecutor sqlExecutor;

    @Autowired
    private Environment env;

    @Autowired
    public LoginService(SqlClusterExecutor sqlExecutor) {
        this.sqlExecutor = sqlExecutor;
    }

    public boolean login(String username, String password) throws SQLException {
        String zkHost = env.getProperty("openmldb.zk_cluster");
        String zkPath = env.getProperty("openmldb.zk_path");

        SdkOption option = new SdkOption();
        option.setZkCluster(zkHost);
        option.setZkPath(zkPath);
        option.setUser(username);
        option.setPassword(password);

        SqlClusterExecutor sqlExecutor = null;
        try {
            sqlExecutor = new SqlClusterExecutor(option);
        } catch (SqlException e) {
            logger.error(String.format("Try to login with username: %s and password: %s but get exception: %s",
                    username,
                    password,
                    e.getMessage()));
            return false;
        }
        return true;
    }

}