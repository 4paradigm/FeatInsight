package com._4paradigm.openmldb.featureplatform.client;

import com._4paradigm.openmldb.common.Pair;
import com._4paradigm.openmldb.featureplatform.dao.model.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.util.StringEscapeUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import java.io.IOException;
import java.util.List;

public class FeaturePlatformClient {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private CloseableHttpClient httpClient;
    private String apiEndpoint;

    public FeaturePlatformClient(String host, int port) {
        this.httpClient = HttpClients.custom().setMaxConnPerRoute(10).setMaxConnTotal(10).build();
        this.apiEndpoint = String.format("http://%s:%d/api/", host, port);
    }

    public static void printResponse(HttpResponse response) throws IOException {
        int responseCode = response.getStatusLine().getStatusCode();
        HttpEntity entity = response.getEntity();
        String responseBody = EntityUtils.toString(entity);
        System.out.println("Response Code: " + responseCode);
        System.out.println("Response Body: " + responseBody);
        EntityUtils.consume(entity);
    }

    /**
     * Get all OpenMLDB tables.
     * TODO: Notice that now it can get system tables and no table schema.
     *
     * @return
     * @throws IOException
     */
    public List<SimpleTableInfo> getTables() throws IOException {
        String endpoint = this.apiEndpoint + "tables";
        HttpGet request = new HttpGet(endpoint);
        HttpResponse response = httpClient.execute(request);

        HttpEntity entity = response.getEntity();
        String responseBody = EntityUtils.toString(entity);
        return objectMapper.readValue(responseBody, new TypeReference<List<SimpleTableInfo>>() {});
    }

    public SimpleTableInfo getTable(String db, String table) throws IOException {
        String endpoint = this.apiEndpoint + "tables/" + db + "/" + table;
        HttpGet request = new HttpGet(endpoint);
        HttpResponse response = httpClient.execute(request);

        HttpEntity entity = response.getEntity();
        String responseBody = EntityUtils.toString(entity);
        return objectMapper.readValue(responseBody, SimpleTableInfo.class);
    }

    public List<FeatureView> getTableRelatedFeatureViews(String db, String table) throws IOException {
        String endpoint = this.apiEndpoint + "tables/" + db + "/" + table + "/featureviews";
        HttpGet request = new HttpGet(endpoint);
        HttpResponse response = httpClient.execute(request);

        HttpEntity entity = response.getEntity();
        String responseBody = EntityUtils.toString(entity);
        return objectMapper.readValue(responseBody, new TypeReference<List<FeatureView>>() {});
    }

    public List<FeatureService> getTableRelatedFeatureServices(String db, String table) throws IOException {
        String endpoint = this.apiEndpoint + "tables/" + db + "/" + table + "/featureservices";
        HttpGet request = new HttpGet(endpoint);
        HttpResponse response = httpClient.execute(request);

        HttpEntity entity = response.getEntity();
        String responseBody = EntityUtils.toString(entity);
        return objectMapper.readValue(responseBody, new TypeReference<List<FeatureService>>() {});
    }

    public List<String> getDatabases() throws IOException {
        String endpoint = this.apiEndpoint + "databases";
        HttpGet request = new HttpGet(endpoint);
        HttpResponse response = httpClient.execute(request);

        HttpEntity entity = response.getEntity();
        String responseBody = EntityUtils.toString(entity);
        return objectMapper.readValue(responseBody, new TypeReference<List<String>>() {});
    }

    public List<SimpleTableInfo> getDatabaseTables(String db) throws IOException {
        String endpoint = this.apiEndpoint + "databases/" + db + "/tables";
        HttpGet request = new HttpGet(endpoint);
        HttpResponse response = httpClient.execute(request);

        HttpEntity entity = response.getEntity();
        String responseBody = EntityUtils.toString(entity);
        return objectMapper.readValue(responseBody, new TypeReference<List<SimpleTableInfo>>() {});
    }

    public List<Entity> getEntities() throws IOException {
        String endpoint = this.apiEndpoint + "entities";
        HttpGet request = new HttpGet(endpoint);
        HttpResponse response = httpClient.execute(request);

        HttpEntity entity = response.getEntity();
        String responseBody = EntityUtils.toString(entity);
        return objectMapper.readValue(responseBody, new TypeReference<List<Entity>>() {});
    }

    public boolean createEntity(String name, String primaryKeys) throws IOException {
        String endpoint = this.apiEndpoint + "entities";
        HttpPost postRequest = new HttpPost(endpoint);
        postRequest.setHeader("Content-Type", "application/json");
        postRequest.setEntity(new StringEntity(String.format("{\"name\":\"%s\", \"primaryKeys\":\"%s\"}", name, primaryKeys)));
        HttpResponse postResponse = httpClient.execute(postRequest);
        printResponse(postResponse);
        // TODO: Check response status code
        return true;
    }

    public Entity getEntity(String name) throws IOException {
        String endpoint = this.apiEndpoint + "entities/" + name;
        HttpGet request = new HttpGet(endpoint);
        HttpResponse response = httpClient.execute(request);

        HttpEntity entity = response.getEntity();
        String responseBody = EntityUtils.toString(entity);
        return objectMapper.readValue(responseBody, Entity.class);
    }

    public boolean deleteEntity(String name) throws IOException {
        String endpoint = this.apiEndpoint + "entities/" + name;
        HttpDelete deleteRequest = new HttpDelete(endpoint);
        HttpResponse deleteResponse = httpClient.execute(deleteRequest);
        printResponse(deleteResponse);
        return true;
    }

    public List<FeatureView> getFeatureViews() throws IOException {
        String endpoint = this.apiEndpoint + "featureviews";
        HttpGet request = new HttpGet(endpoint);
        HttpResponse response = httpClient.execute(request);

        HttpEntity entity = response.getEntity();
        String responseBody = EntityUtils.toString(entity);
        return objectMapper.readValue(responseBody, new TypeReference<List<FeatureView>>() {});
    }

    public List<String> getFeatureViewDependentTables(String name) throws IOException {
        String endpoint = this.apiEndpoint + "featureviews/" + name + "/tables";
        HttpGet request = new HttpGet(endpoint);
        HttpResponse response = httpClient.execute(request);

        HttpEntity entity = response.getEntity();
        String responseBody = EntityUtils.toString(entity);
        return objectMapper.readValue(responseBody, new TypeReference<List<String>>() {});
    }

    public List<Feature> getFeatures() throws IOException {
        String endpoint = this.apiEndpoint + "features";
        HttpGet request = new HttpGet(endpoint);
        HttpResponse response = httpClient.execute(request);

        HttpEntity entity = response.getEntity();
        String responseBody = EntityUtils.toString(entity);
        return objectMapper.readValue(responseBody, new TypeReference<List<Feature>>() {});
    }

    public List<Feature> getFeaturesFromFeatureView(String featureViewName) throws IOException {
        String endpoint = this.apiEndpoint + "features/" + featureViewName;
        HttpGet request = new HttpGet(endpoint);
        HttpResponse response = httpClient.execute(request);

        HttpEntity entity = response.getEntity();
        String responseBody = EntityUtils.toString(entity);
        return objectMapper.readValue(responseBody, new TypeReference<List<Feature>>() {});
    }

    public List<Feature> getFeaturesFromFeatureService(String featureServiceName) throws IOException {
        String endpoint = this.apiEndpoint + "features?featureServiceName=" + featureServiceName;
        HttpGet request = new HttpGet(endpoint);
        HttpResponse response = httpClient.execute(request);

        HttpEntity entity = response.getEntity();
        String responseBody = EntityUtils.toString(entity);
        return objectMapper.readValue(responseBody, new TypeReference<List<Feature>>() {});
    }

    public Feature getFeature(String featureViewName, String featureName) throws IOException {
        String endpoint = this.apiEndpoint + "features/" + featureViewName + "/" + featureName;
        HttpGet request = new HttpGet(endpoint);
        HttpResponse response = httpClient.execute(request);

        HttpEntity entity = response.getEntity();
        String responseBody = EntityUtils.toString(entity);
        return objectMapper.readValue(responseBody, Feature.class);
    }

    public boolean createFeatureView(String name, String entityNames, String db, String sql) throws IOException {
        String escapedSql = StringEscapeUtils.escapeJson(sql);
        String endpoint = this.apiEndpoint + "featureviews";
        HttpPost postRequest = new HttpPost(endpoint);
        postRequest.setHeader("Content-Type", "application/json");
        postRequest.setEntity(new StringEntity(String.format("{\"name\":\"%s\", \"entityNames\":\"%s\", \"db\":\"%s\", \"sql\":\"%s\"}", name, entityNames, db, escapedSql)));
        HttpResponse postResponse = httpClient.execute(postRequest);
        printResponse(postResponse);
        // TODO: Check response status code
        return true;
    }

    public FeatureView getFeatureView(String name) throws IOException {
        String endpoint = this.apiEndpoint + "featureviews/" + name;
        HttpGet request = new HttpGet(endpoint);
        HttpResponse response = httpClient.execute(request);

        HttpEntity entity = response.getEntity();
        String responseBody = EntityUtils.toString(entity);
        return objectMapper.readValue(responseBody, FeatureView.class);
    }

    public boolean deleteFeatureView(String name) throws IOException {
        String endpoint = this.apiEndpoint + "featureviews/" + name;
        HttpDelete deleteRequest = new HttpDelete(endpoint);
        HttpResponse deleteResponse = httpClient.execute(deleteRequest);
        printResponse(deleteResponse);
        return true;
    }

    public List<FeatureService> getFeatureServices() throws IOException {
        String endpoint = this.apiEndpoint + "featureservices";
        HttpGet request = new HttpGet(endpoint);
        HttpResponse response = httpClient.execute(request);

        HttpEntity entity = response.getEntity();
        String responseBody = EntityUtils.toString(entity);
        return objectMapper.readValue(responseBody, new TypeReference<List<FeatureService>>() {});
    }

    public List<FeatureService> getLatestFeatureServices() throws IOException {
        String endpoint = this.apiEndpoint + "featureservices/latest";
        HttpGet request = new HttpGet(endpoint);
        HttpResponse response = httpClient.execute(request);

        HttpEntity entity = response.getEntity();
        String responseBody = EntityUtils.toString(entity);
        return objectMapper.readValue(responseBody, new TypeReference<List<FeatureService>>() {});
    }

    public boolean createFeatureService(String name, String featureList) throws IOException {
        String endpoint = this.apiEndpoint + "featureservices";
        HttpPost postRequest = new HttpPost(endpoint);
        postRequest.setHeader("Content-Type", "application/json");
        postRequest.setEntity(new StringEntity(String.format("{\"name\":\"%s\", \"featureList\":\"%s\"}", name, featureList)));
        HttpResponse postResponse = httpClient.execute(postRequest);
        printResponse(postResponse);
        // TODO: Check response status code
        return true;
    }

    public boolean createFeatureService(String name, String version, String featureList) throws IOException {
        String endpoint = this.apiEndpoint + "featureservices";
        HttpPost postRequest = new HttpPost(endpoint);
        postRequest.setHeader("Content-Type", "application/json");
        postRequest.setEntity(new StringEntity(String.format("{\"name\":\"%s\", \"version\": \"%s\", \"featureList\":\"%s\"}", name, version, featureList)));
        HttpResponse postResponse = httpClient.execute(postRequest);
        printResponse(postResponse);
        // TODO: Check response status code
        return true;
    }

    public boolean createFeatureServiceFromDeployment(String name, String db, String deploymentName) throws IOException {
        String endpoint = this.apiEndpoint + "featureservices/deployments";
        HttpPost postRequest = new HttpPost(endpoint);
        postRequest.setHeader("Content-Type", "application/json");
        postRequest.setEntity(new StringEntity(String.format("{\"name\":\"%s\", \"db\":\"%s\", \"deploymentName\":\"%s\"}", name, db, deploymentName)));
        HttpResponse postResponse = httpClient.execute(postRequest);
        printResponse(postResponse);
        return true;
    }

    public boolean createFeatureServiceFromDeployment(String name, String version, String db, String deploymentName) throws IOException {
        String endpoint = this.apiEndpoint + "featureservices/deployments";
        HttpPost postRequest = new HttpPost(endpoint);
        postRequest.setHeader("Content-Type", "application/json");
        postRequest.setEntity(new StringEntity(String.format("{\"name\":\"%s\", \"version\":\"%s\", \"db\":\"%s\", \"deploymentName\":\"%s\"}", name, version, db, deploymentName)));
        HttpResponse postResponse = httpClient.execute(postRequest);
        printResponse(postResponse);
        return true;
    }

    public FeatureService getFeatureService(String name) throws IOException {
        String endpoint = this.apiEndpoint + "featureservices/" + name;
        HttpGet request = new HttpGet(endpoint);
        HttpResponse response = httpClient.execute(request);

        HttpEntity entity = response.getEntity();
        String responseBody = EntityUtils.toString(entity);
        return objectMapper.readValue(responseBody, FeatureService.class);
    }

    public FeatureService getFeatureService(String name, String version) throws IOException {
        String endpoint = this.apiEndpoint + "featureservices/" + name + "/" + version;
        HttpGet request = new HttpGet(endpoint);
        HttpResponse response = httpClient.execute(request);

        HttpEntity entity = response.getEntity();
        String responseBody = EntityUtils.toString(entity);
        return objectMapper.readValue(responseBody, FeatureService.class);
    }

    public List<String> getFeatureServiceDependentTables(String name) throws IOException {
        String endpoint = this.apiEndpoint + "featureservices/" + name + "/tables";
        HttpGet request = new HttpGet(endpoint);
        HttpResponse response = httpClient.execute(request);

        HttpEntity entity = response.getEntity();
        String responseBody = EntityUtils.toString(entity);
        return objectMapper.readValue(responseBody, new TypeReference<List<String>>() {});
    }

    public List<String> getFeatureServiceDependentTables(String name, String version) throws IOException {
        String endpoint = this.apiEndpoint + "featureservices/" + name + "/" + version + "/tables";
        HttpGet request = new HttpGet(endpoint);
        HttpResponse response = httpClient.execute(request);

        HttpEntity entity = response.getEntity();
        String responseBody = EntityUtils.toString(entity);
        return objectMapper.readValue(responseBody, new TypeReference<List<String>>() {});
    }

    public boolean deleteFeatureService(String name) throws IOException {
        String endpoint = this.apiEndpoint + "featureservices/" + name;
        HttpDelete deleteRequest = new HttpDelete(endpoint);
        HttpResponse deleteResponse = httpClient.execute(deleteRequest);
        printResponse(deleteResponse);
        return true;
    }

    public boolean deleteFeatureService(String name, String version) throws IOException {
        String endpoint = this.apiEndpoint + "featureservices/" + name + "/" + version;
        HttpDelete deleteRequest = new HttpDelete(endpoint);
        HttpResponse deleteResponse = httpClient.execute(deleteRequest);
        printResponse(deleteResponse);
        return true;
    }

    public String getFeatureServiceRequestSchema(String name) throws IOException {
        String endpoint = this.apiEndpoint + "featureservices/" + name + "/request/schema";
        HttpGet getRequest = new HttpGet(endpoint);
        HttpResponse getResponse = httpClient.execute(getRequest);
        return EntityUtils.toString(getResponse.getEntity());
    }

    public String getFeatureServiceRequestSchema(String name, String version) throws IOException {
        String endpoint = this.apiEndpoint + "featureservices/" + name + "/" + version + "/request/schema";
        HttpGet getRequest = new HttpGet(endpoint);
        HttpResponse getResponse = httpClient.execute(getRequest);
        return EntityUtils.toString(getResponse.getEntity());
    }

    public String getFeatureServiceOutputSchema(String name) throws IOException {
        String endpoint = this.apiEndpoint + "featureservices/" + name + "/output/schema";
        HttpGet getRequest = new HttpGet(endpoint);
        HttpResponse getResponse = httpClient.execute(getRequest);
        return EntityUtils.toString(getResponse.getEntity());
    }

    public String getFeatureServiceOutputSchema(String name, String version) throws IOException {
        String endpoint = this.apiEndpoint + "featureservices/" + name + "/" + version + "/output/schema";
        HttpGet getRequest = new HttpGet(endpoint);
        HttpResponse getResponse = httpClient.execute(getRequest);
        return EntityUtils.toString(getResponse.getEntity());
    }

    public String getFeatureServiceRequestDemoData(String name) throws IOException {
        String endpoint = this.apiEndpoint + "featureservices/" + name + "/request/demo";
        HttpGet getRequest = new HttpGet(endpoint);
        HttpResponse getResponse = httpClient.execute(getRequest);
        return EntityUtils.toString(getResponse.getEntity());
    }

    public String getFeatureServiceRequestDemoData(String name, String version) throws IOException {
        String endpoint = this.apiEndpoint + "featureservices/" + name + "/" + version + "/request/demo";
        HttpGet getRequest = new HttpGet(endpoint);
        HttpResponse getResponse = httpClient.execute(getRequest);
        return EntityUtils.toString(getResponse.getEntity());
    }

    public HttpResponse executeSql(String sql) throws IOException {
        String endpoint = this.apiEndpoint + "sql/execute";
        HttpPost postRequest = new HttpPost(endpoint);
        postRequest.setHeader("Content-Type", "application/json");
        postRequest.setEntity(new StringEntity(String.format("{\"sql\":\"%s\"}", sql)));
        HttpResponse postResponse = httpClient.execute(postRequest);
        return postResponse;
    }

    public HttpResponse validateSql(String sql) throws IOException {
        String endpoint = this.apiEndpoint + "sql/validate";
        HttpPost postRequest = new HttpPost(endpoint);
        postRequest.setHeader("Content-Type", "application/json");
        postRequest.setEntity(new StringEntity(String.format("{\"sql\":\"%s\"}", sql)));
        HttpResponse postResponse = httpClient.execute(postRequest);
        return postResponse;
    }

    public HttpResponse requestFeatureService(String featureService, String requestData) throws IOException {
        String endpoint = this.apiEndpoint + "featureservices/" + featureService + "/request";
        HttpPost postRequest = new HttpPost(endpoint);
        postRequest.setHeader("Content-Type", "application/json");
        postRequest.setEntity(new StringEntity(requestData, ContentType.APPLICATION_JSON));
        HttpResponse postResponse = httpClient.execute(postRequest);
        return postResponse;
    }

    public HttpResponse requestFeatureService(String featureService, String version, String requestData) throws IOException {
        String endpoint = this.apiEndpoint + "featureservices/" + featureService + "/" + version + "/request";
        HttpPost postRequest = new HttpPost(endpoint);
        postRequest.setHeader("Content-Type", "application/json");
        postRequest.setEntity(new StringEntity(requestData, ContentType.APPLICATION_JSON));
        HttpResponse postResponse = httpClient.execute(postRequest);
        return postResponse;
    }

    public HttpResponse requestApiServer(String apiServerEndpoint, String featureServiceName, String requestData) throws IOException {
        FeatureService featureService = getFeatureService(featureServiceName);
        String endpoint = String.format("%s/dbs/%s/deployments/%s", apiServerEndpoint, featureService.getDb(), featureService.getDeployment());
        HttpPost postRequest = new HttpPost(endpoint);
        postRequest.setHeader("Content-Type", "application/json");
        postRequest.setEntity(new StringEntity(requestData));
        HttpResponse postResponse = httpClient.execute(postRequest);
        return postResponse;
    }

    public HttpResponse requestApiServer(String apiServerEndpoint, String featureServiceName, String featureServiceVersion, String requestData) throws IOException {
        FeatureService featureService = getFeatureService(featureServiceName, featureServiceVersion);
        String endpoint = String.format("%s/dbs/%s/deployments/%s", apiServerEndpoint, featureService.getDb(), featureService.getDeployment());
        HttpPost postRequest = new HttpPost(endpoint);
        postRequest.setHeader("Content-Type", "application/json");
        postRequest.setEntity(new StringEntity(requestData));
        HttpResponse postResponse = httpClient.execute(postRequest);
        return postResponse;
    }

    public void close() throws IOException {
        this.httpClient.close();
    }

}
