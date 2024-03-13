package com._4paradigm.openmldb.featureplatform.service;

import com._4paradigm.openmldb.featureplatform.utils.DatabaseConnectionUtil;
import com._4paradigm.openmldb.sdk.impl.SqlClusterExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;

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

    public String login(String username, String password) {
        return DatabaseConnectionUtil.createSqlExecutor(username, password);
    }

}