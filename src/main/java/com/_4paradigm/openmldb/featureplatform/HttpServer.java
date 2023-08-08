package com._4paradigm.openmldb.featureplatform;

import java.sql.SQLException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com._4paradigm.openmldb.featureplatform.dao.SqlService;

@SpringBootApplication
public class HttpServer {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(HttpServer.class, args);

        SqlService sqlService = context.getBean(SqlService.class);
        try {
            // Init the OpenMLDB system tables
            sqlService.initDbAndTables();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
