package com._4paradigm.openmldb.featureplatform;

import com._4paradigm.openmldb.featureplatform.service.SqlService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.sql.SQLException;

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
