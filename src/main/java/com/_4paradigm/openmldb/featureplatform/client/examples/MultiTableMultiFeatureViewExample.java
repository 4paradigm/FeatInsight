package com._4paradigm.openmldb.featureplatform.client.examples;

import com._4paradigm.openmldb.featureplatform.client.FeaturePlatformClient;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import java.io.IOException;

public class MultiTableMultiFeatureViewExample {

    public static void main(String[] args) {
        FeaturePlatformClient client = new FeaturePlatformClient("127.0.0.1", 8888);

        try {
            // Create test db and tables
            client.executeSql("CREATE DATABASE IF NOT EXISTS t2v2");
            client.executeSql("CREATE TABLE IF NOT EXISTS t2v2.user (name string, age int)");
            client.executeSql("CREATE TABLE IF NOT EXISTS t2v2.trade (user_name string, amount float)");

            // Create entity
            client.createEntity("t2v2_name", "name");

            // Create feature view
            client.createFeatureView("t2v2_v1", "t2v2_name", "t2v2", "SELECT age + 10 AS new_age FROM user");
            client.createFeatureView("t2v2_v2", "t2v2_name", "t2v2", "SELECT name, age, amount FROM user LAST JOIN trade ON user.name = trade.user_name");

            // Create feature service
            client.createFeatureService("t2v2_s1", "t2v2_v1, t2v2_v2");

            // Insert test data for trade table
            // client.executeSql("INSERT INTO t2v2.trade VALUES (\"foo\", 11.1)");

            // Test feature service
            HttpResponse response = client.requestFeatureService("t2v2_s1", "{\"input\": [[\"foo\", 22]]}");
            System.out.println(EntityUtils.toString(response.getEntity()));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
