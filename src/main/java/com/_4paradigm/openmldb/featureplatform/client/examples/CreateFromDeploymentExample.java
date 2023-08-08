package com._4paradigm.openmldb.featureplatform.client.examples;

import com._4paradigm.openmldb.featureplatform.client.FeaturePlatformClient;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class CreateFromDeploymentExample {

    public static void main(String[] args) {
        FeaturePlatformClient client = new FeaturePlatformClient("127.0.0.1", 8888);

        try {
            // Create test db and table
            client.executeSql("CREATE DATABASE IF NOT EXISTS demo_db");
            client.executeSql("CREATE TABLE IF NOT EXISTS demo_db.demo_table (c1 int, c2 int, c3 bigint)");

            // TODO: Required DEPLOY to support db name
            // Create test deployment
            client.executeSql("USE demo_db");
            client.executeSql("DEPLOY demo_table_deploy SELECT c1 + 10 AS c1, c2 + 100 AS c2, c3 + 1000 AS c3 FROM demo_db.demo_table");

            // Create feature service
            client.createFeatureServiceFromDeployment("demo_table_deploy", "demo_db", "demo_table_deploy");

            // Test feature service
            String demoData  = client.getFeatureServiceRequestDemoData("demo_table_deploy");
            HttpResponse response = client.requestFeatureService("demo_table_deploy", demoData);
            System.out.println(EntityUtils.toString(response.getEntity()));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
