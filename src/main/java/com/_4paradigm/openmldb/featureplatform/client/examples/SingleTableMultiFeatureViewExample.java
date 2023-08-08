package com._4paradigm.openmldb.featureplatform.client.examples;

import com._4paradigm.openmldb.featureplatform.client.FeaturePlatformClient;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import java.io.IOException;

public class SingleTableMultiFeatureViewExample {

    public static void main(String[] args) {
        FeaturePlatformClient client = new FeaturePlatformClient("127.0.0.1", 8888);

        try {
            // Create test db and tables
            client.executeSql("CREATE DATABASE IF NOT EXISTS t1v2");
            client.executeSql("CREATE TABLE IF NOT EXISTS t1v2.user (name string, age int)");

            // Create entity
            client.createEntity("t1v2_name", "name");

            // Create feature view
            client.createFeatureView("t1v2_v1", "t1v2_name","t1v2", "SELECT name FROM user");
            client.createFeatureView("t1v2_v2", "t1v2_name", "t1v2", "SELECT age + 10 AS new_age FROM user");

            // Create feature service
            client.createFeatureService("t1v2_s1", "t1v2_v1, t1v2_v2");

            // Test feature service
            HttpResponse response = client.requestFeatureService("t1v2_s1", "{\"input\": [[\"abc\", 22]]}");
            System.out.println(EntityUtils.toString(response.getEntity()));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
