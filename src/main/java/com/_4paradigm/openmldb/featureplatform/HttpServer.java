package com._4paradigm.openmldb.featureplatform;

import com._4paradigm.openmldb.featureplatform.service.SqlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.sql.SQLException;

@SpringBootApplication
public class HttpServer {

    private static final Logger logger = LoggerFactory.getLogger(HttpServer.class);

    public static void main(String[] args) {
        logger.info("Try to start FeatInsight http server");

        ApplicationContext context = SpringApplication.run(HttpServer.class, args);

        SqlService sqlService = context.getBean(SqlService.class);
        try {
            // Init the OpenMLDB system tables
            logger.info("Try to create OpenMLDB system tables for FeatInsight");
            sqlService.initDbAndTables();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
