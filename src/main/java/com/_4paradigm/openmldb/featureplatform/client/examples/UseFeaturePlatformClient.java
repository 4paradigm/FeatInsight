package com._4paradigm.openmldb.featureplatform.client.examples;

import java.io.IOException;
import java.util.List;

import com._4paradigm.openmldb.common.Pair;
import com._4paradigm.openmldb.featureplatform.client.FeaturePlatformClient;
import com._4paradigm.openmldb.featureplatform.dao.model.*;
import org.apache.http.HttpResponse;

public class UseFeaturePlatformClient {

    private static FeaturePlatformClient client = new FeaturePlatformClient("127.0.0.1", 8888);

    public static void useEntity() throws IOException {
        // List all entities
        List<Entity> entities = client.getEntities();
        System.out.println(entities);

        // Create an entity
        client.createEntity("entity1", "pk1, pk2");

        // Get an entity
        Entity entity = client.getEntity("entity1");
        System.out.println(entity);

        // Delete an entity
        client.deleteEntity("entity1");
    }

    public static void accessFeatures() throws IOException {
                /*
        // List all features
        List<Feature> features = client.getFeatures();
        System.out.println(features);

        // Get a feature
        Feature feature = client.getFeature("featureview1", "feature1");
        System.out.println(feature);


        // List all features of feature view
        List<Feature> features2 = client.getFeaturesFromFeatureView("featureview1");
        System.out.println(features2);
         */

        // List all features of feature service
        List<Feature> features3 = client.getFeaturesFromFeatureService("CLTV_S1");
        System.out.println(features3);


    }

    public static void accessFeatureViews() throws IOException {
        /*
        // List all feature views
        List<FeatureView> featureViews = client.getFeatureViews();
        System.out.println(featureViews);

        // Create a feature view
        client.createFeatureView("featureview1", "", "", "SELECT name, age + 10 AS new_age FROM user");
        */
        // Get a feature view
        FeatureView featureView = client.getFeatureView("t1v1_v2");
        System.out.println(featureView);

        /*
        // Delete a feature view
        client.deleteFeatureView("featureview1");

        List<String> tables = client.getFeatureViewDependentTables("featureview1");
        System.out.println(tables);

         */
    }

    public static void accessFeatureServices() throws IOException {
        /*
        // List all feature services
        List<FeatureService> featureServices = client.getFeatureServices();
        System.out.println(featureServices);

        List<FeatureService> featureServices = client.getLatestFeatureServices();
        System.out.println(featureServices);



        // Create a feature service
        client.createFeatureService("feature_service_1", "feature_view1, feature_view2");

        // Get a feature view
        FeatureService featureService = client.getFeatureService("feature_service_1");
        System.out.println(featureService);
*/
        // Delete a feature view
        client.deleteFeatureService("s1", "v2");

/*
        HttpResponse response = client.requestFeatureService("s1", "v1", "{\"input\": [[\"abc\", 22]]}");
        client.printResponse(response);


        HttpResponse response = client.requestFeatureService("service1", "{\"input\": [[\"abc\", 22]]}");
        client.printResponse(response);

        //client.createFeatureServiceFromDeployment("deploy8", "db1", "demo_deploy8");

        String schema = client.getFeatureServiceRequestSchema("s1");
        client.printResponse(schema);

        List<String> tables = client.getFeatureServiceDependentTables("featureservice1");
        System.out.println(tables);

        String demoData = client.getFeatureServiceRequestDemoData("featureservice1");
        System.out.println(demoData);

        String schema = client.getFeatureServiceOutputSchema("demo_table_deploy");
        System.out.println(schema);
        */
    }

    public static void requestApiServer() throws IOException {
        HttpResponse response = client.requestApiServer("http://127.0.0.1:8090", "t2v1_s1", "{\"input\": [[\"abc\", 22]]}");
        client.printResponse(response);
    }


    public static void validateSql() throws IOException {
        String sql = "SELECT 100";
        // TODO: Fail and need to upgrade the internal validate logic
        HttpResponse response = client.validateSql(sql);
        client.printResponse(response);

        String sql2 = "SELECT * from SYSTEM_FEATURE_PLATFORM.entities";
        HttpResponse response2 = client.validateSql(sql2);
        client.printResponse(response2);

        String sql3 = "SELECT col from db1.t100";
        HttpResponse response3 = client.validateSql(sql3);
        client.printResponse(response3);
    }

    public static void accessExecuteSql() throws IOException {
        String sql = "CREATE DATABASE db1";
        HttpResponse response = client.executeSql(sql);
        client.printResponse(response);

        String sql2 = "SELECT 'abc', 100";
        HttpResponse response2 = client.executeSql(sql2);
        client.printResponse(response2);
    }

    public static void accessTables() throws IOException {
        /*
        List<SimpleTableInfo> tables = client.getTables();
        System.out.println(tables);

        SimpleTableInfo table = client.getTable("db1", "t1");
        System.out.println(table);
        */

        List<FeatureView> featureViews = client.getTableRelatedFeatureViews("t1v1", "user");
        System.out.println(featureViews);

        List<FeatureService> featureServices = client.getTableRelatedFeatureServices("t1v1", "user");
        System.out.println(featureServices);
    }

    public static void accessDatabases() throws IOException {
        List<String> databases = client.getDatabases();
        System.out.println(databases);

        List<SimpleTableInfo> tables = client.getDatabaseTables("SYSTEM_FEATURE_PLATFORM");
        System.out.println(tables);
    }

    public static void main(String[] args) {
        try {
            //accessFeatureServices();
            //accessFeatureViews();
            //accessFeatureServices();
            //accessFeatures();
            //accessFeatureServices();
            accessTables();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
