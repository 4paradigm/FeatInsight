package com._4paradigm.openmldb.featureplatform.client.examples;

import com._4paradigm.openmldb.featureplatform.client.FeaturePlatformClient;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import java.io.IOException;

public class MultiTableSingleFeatureViewExample {

    public static void main(String[] args) {
        FeaturePlatformClient client = new FeaturePlatformClient("172.27.234.77", 8888);

        try {
            // Create test db and tables
            client.executeSql("CREATE DATABASE IF NOT EXISTS t2v1");
            client.executeSql("CREATE TABLE IF NOT EXISTS t2v1.user (name string, age int)");
            client.executeSql("CREATE TABLE IF NOT EXISTS t2v1.trade (user_name string, amount float)");

            // Create feature view
            client.createFeatureView("t2v1_v1", "", "t2v1", "SELECT name, age, amount FROM user LAST JOIN trade ON user.name = trade.user_name");

            // Create feature service
            client.createFeatureService("t2v1_s1", "t2v1_v1");

            // Insert test data for trade table
            // client.executeSql("INSERT INTO t2v1.trade VALUES (\"foo\", 11.1)");

            // Test feature service
            HttpResponse response = client.requestFeatureService("t2v1_s1", "{\"input\": [[\"abc\", 22]]}");
            System.out.println(EntityUtils.toString(response.getEntity()));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
