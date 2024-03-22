package com._4paradigm.openmldb.featureplatform.service;

import com._4paradigm.openmldb.featureplatform.dao.model.SqlExecutorWrapper;
import com._4paradigm.openmldb.featureplatform.utils.SqlExecutorPoolManager;
import com._4paradigm.openmldb.sdk.SqlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

@Repository
public class LoginService {

    private static final Logger logger = LoggerFactory.getLogger(LoginService.class);

    @Autowired
    private Environment env;

    @Autowired
    private SqlExecutorPoolManager sqlExecutorPoolManager;

    public String login(String username, String password) throws SqlException {
        return sqlExecutorPoolManager.createSqlExecutor(username, password);
    }

    public String test() {
        return SqlExecutorWrapper.getSqlExecutor().toString();
    }

}