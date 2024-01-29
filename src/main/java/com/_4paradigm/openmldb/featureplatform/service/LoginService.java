package com._4paradigm.openmldb.featureplatform.service;

import com._4paradigm.openmldb.featureplatform.dao.model.SimpleTableInfo;
import com._4paradigm.openmldb.sdk.Schema;
import com._4paradigm.openmldb.sdk.impl.SqlClusterExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Repository
public class LoginService {

    @Autowired
    private SqlClusterExecutor sqlExecutor;

    @Autowired
    private Environment env;

    @Autowired
    public LoginService(SqlClusterExecutor sqlExecutor) {
        this.sqlExecutor = sqlExecutor;
    }

    public boolean login(String username, String password) throws SQLException {
        String mockUsername = env.getProperty("mockuser.username", String.class, "admin");
        String mockPassword = env.getProperty("mockuser.password", String.class, "admin");

        if (username.equals(mockUsername) && password.equals(mockPassword)) {
            return true;
        } else {
            return false;
        }
    }

}