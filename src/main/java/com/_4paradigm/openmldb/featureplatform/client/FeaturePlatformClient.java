package com._4paradigm.openmldb.featureplatform.client;

import com._4paradigm.openmldb.featureplatform.dao.FeatureServiceService;
import com._4paradigm.openmldb.featureplatform.dao.model.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.util.StringEscapeUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
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

    private final CloseableHttpClient httpClient;
    private final String apiEndpoint;

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

        if (response.getStatusLine().getStatusCode() == 200) {
            return objectMapper.readValue(responseBody, new TypeReference<List<SimpleTableInfo>>() {});
        } else {
            throw new IOException("Fail to request server and get error: " + responseBody);
        }
    }

    public SimpleTableInfo getTable(String db, String table) throws IOException {
        String endpoint = this.apiEndpoint + "tables/" + db + "/" + table;
        HttpGet request = new HttpGet(endpoint);
        HttpResponse response = httpClient.execute(request);
        HttpEntity entity = response.getEntity();
        String responseBody = EntityUtils.toString(entity);

        if (response.getStatusLine().getStatusCode() == 200) {
            return objectMapper.readValue(responseBody, SimpleTableInfo.class);
        } else {
            throw new IOException("Fail to request server and get error: " + responseBody);
        }
    }

    public List<FeatureView> getTableRelatedFeatureViews(String db, String table) throws IOException {
        String endpoint = this.apiEndpoint + "tables/" + db + "/" + table + "/featureviews";
        HttpGet request = new HttpGet(endpoint);
        HttpResponse response = httpClient.execute(request);
        HttpEntity entity = response.getEntity();
        String responseBody = EntityUtils.toString(entity);

        if (response.getStatusLine().getStatusCode() == 200) {
            return objectMapper.readValue(responseBody, new TypeReference<List<FeatureView>>() {});
        } else {
            throw new IOException("Fail to request server and get error: " + responseBody);
        }
    }

    public List<FeatureService> getTableRelatedFeatureServices(String db, String table) throws IOException {
        String endpoint = this.apiEndpoint + "tables/" + db + "/" + table + "/featureservices";
        HttpGet request = new HttpGet(endpoint);
        HttpResponse response = httpClient.execute(request);
        HttpEntity entity = response.getEntity();
        String responseBody = EntityUtils.toString(entity);

        if (response.getStatusLine().getStatusCode() == 200) {
            return objectMapper.readValue(responseBody, new TypeReference<List<FeatureService>>() {});
        } else {
            throw new IOException("Fail to request server and get error: " + responseBody);
        }
    }

    public List<String> getDatabases() throws IOException {
        String endpoint = this.apiEndpoint + "databases";
        HttpGet request = new HttpGet(endpoint);
        HttpResponse response = httpClient.execute(request);
        HttpEntity entity = response.getEntity();
        String responseBody = EntityUtils.toString(entity);

        if (response.getStatusLine().getStatusCode() == 200) {
            return objectMapper.readValue(responseBody, new TypeReference<List<String>>() {});
        } else {
            throw new IOException("Fail to request server and get error: " + responseBody);
        }
    }

    public List<SimpleTableInfo> getDatabaseTables(String db) throws IOException {
        String endpoint = this.apiEndpoint + "databases/" + db + "/tables";
        HttpGet request = new HttpGet(endpoint);
        HttpResponse response = httpClient.execute(request);
        HttpEntity entity = response.getEntity();
        String responseBody = EntityUtils.toString(entity);

        if (response.getStatusLine().getStatusCode() == 200) {
            return objectMapper.readValue(responseBody, new TypeReference<List<SimpleTableInfo>>() {});
        } else {
            throw new IOException("Fail to request server and get error: " + responseBody);
        }
    }

    public List<Entity> getEntities() throws IOException {
        String endpoint = this.apiEndpoint + "entities";
        HttpGet request = new HttpGet(endpoint);
        HttpResponse response = httpClient.execute(request);
        HttpEntity entity = response.getEntity();
        String responseBody = EntityUtils.toString(entity);

        if (response.getStatusLine().getStatusCode() == 200) {
            return objectMapper.readValue(responseBody, new TypeReference<List<Entity>>() {});
        } else {
            throw new IOException("Fail to request server and get error: " + responseBody);
        }
    }

    public Entity createEntity(String name, String primaryKeys) throws IOException {
        String endpoint = this.apiEndpoint + "entities";
        HttpPost postRequest = new HttpPost(endpoint);
        postRequest.setHeader("Content-Type", "application/json");
        postRequest.setEntity(new StringEntity(String.format("{\"name\":\"%s\", \"primaryKeys\":\"%s\"}", name, primaryKeys), "UTF-8"));
        HttpResponse response = httpClient.execute(postRequest);
        HttpEntity entity = response.getEntity();
        String responseBody = EntityUtils.toString(entity);

        if (response.getStatusLine().getStatusCode() == 200) {
            return objectMapper.readValue(responseBody, Entity.class);
        } else {
            throw new IOException("Fail to request server and get error: " + responseBody);
        }
    }

    public Entity getEntity(String name) throws IOException {
        String endpoint = this.apiEndpoint + "entities/" + name;
        HttpGet request = new HttpGet(endpoint);
        HttpResponse response = httpClient.execute(request);
        HttpEntity entity = response.getEntity();
        String responseBody = EntityUtils.toString(entity);

        if (response.getStatusLine().getStatusCode() == 200) {
            return objectMapper.readValue(responseBody, Entity.class);
        } else {
            throw new IOException("Fail to request server and get error: " + responseBody);
        }
    }

    public void deleteEntity(String name) throws IOException {
        String endpoint = this.apiEndpoint + "entities/" + name;
        HttpDelete deleteRequest = new HttpDelete(endpoint);
        HttpResponse response = httpClient.execute(deleteRequest);

        if (response.getStatusLine().getStatusCode() == 200) {

        } else {
            HttpEntity entity = response.getEntity();
            String responseBody = EntityUtils.toString(entity);
            throw new IOException("Fail to request server and get error: " + responseBody);
        }
    }

    public List<FeatureView> getFeatureViews() throws IOException {
        String endpoint = this.apiEndpoint + "featureviews";
        HttpGet request = new HttpGet(endpoint);
        HttpResponse response = httpClient.execute(request);
        HttpEntity entity = response.getEntity();
        String responseBody = EntityUtils.toString(entity);

        if (response.getStatusLine().getStatusCode() == 200) {
            return objectMapper.readValue(responseBody, new TypeReference<List<FeatureView>>() {});
        } else {
            throw new IOException("Fail to request server and get error: " + responseBody);
        }
    }

    public List<String> getFeatureViewDependentTables(String name) throws IOException {
        String endpoint = this.apiEndpoint + "featureviews/" + name + "/tables";
        HttpGet request = new HttpGet(endpoint);
        HttpResponse response = httpClient.execute(request);
        HttpEntity entity = response.getEntity();
        String responseBody = EntityUtils.toString(entity);

        if (response.getStatusLine().getStatusCode() == 200) {
            return objectMapper.readValue(responseBody, new TypeReference<List<String>>() {});
        } else {
            throw new IOException("Fail to request server and get error: " + responseBody);
        }
    }

    public List<Feature> getFeatures() throws IOException {
        String endpoint = this.apiEndpoint + "features";
        HttpGet request = new HttpGet(endpoint);
        HttpResponse response = httpClient.execute(request);
        HttpEntity entity = response.getEntity();
        String responseBody = EntityUtils.toString(entity);

        if (response.getStatusLine().getStatusCode() == 200) {
            return objectMapper.readValue(responseBody, new TypeReference<List<Feature>>() {});
        } else {
            throw new IOException("Fail to request server and get error: " + responseBody);
        }
    }

    public List<Feature> getFeaturesFromFeatureView(String featureViewName) throws IOException {
        String endpoint = this.apiEndpoint + "features/" + featureViewName;
        HttpGet request = new HttpGet(endpoint);
        HttpResponse response = httpClient.execute(request);
        HttpEntity entity = response.getEntity();
        String responseBody = EntityUtils.toString(entity);

        if (response.getStatusLine().getStatusCode() == 200) {
            return objectMapper.readValue(responseBody, new TypeReference<List<Feature>>() {});
        } else {
            throw new IOException("Fail to request server and get error: " + responseBody);
        }
    }

    public List<Feature> getFeaturesFromFeatureService(String featureServiceName) throws IOException {
        String endpoint = this.apiEndpoint + "features?featureServiceName=" + featureServiceName;
        HttpGet request = new HttpGet(endpoint);
        HttpResponse response = httpClient.execute(request);
        HttpEntity entity = response.getEntity();
        String responseBody = EntityUtils.toString(entity);

        if (response.getStatusLine().getStatusCode() == 200) {
            return objectMapper.readValue(responseBody, new TypeReference<List<Feature>>() {});
        } else {
            throw new IOException("Fail to request server and get error: " + responseBody);
        }
    }

    public Feature getFeature(String featureViewName, String featureName) throws IOException {
        String endpoint = this.apiEndpoint + "features/" + featureViewName + "/" + featureName;
        HttpGet request = new HttpGet(endpoint);
        HttpResponse response = httpClient.execute(request);
        HttpEntity entity = response.getEntity();
        String responseBody = EntityUtils.toString(entity);

        if (response.getStatusLine().getStatusCode() == 200) {
            return objectMapper.readValue(responseBody, Feature.class);
        } else {
            throw new IOException("Fail to request server and get error: " + responseBody);
        }
    }

    public FeatureView createFeatureView(String name, String entityNames, String db, String sql) throws IOException {
        return this.createFeatureView(name, entityNames, db, sql, "");
    }

    public FeatureView createFeatureView(String name, String entityNames, String db, String sql, String description) throws IOException {
        String escapedSql = StringEscapeUtils.escapeJson(sql);
        String endpoint = this.apiEndpoint + "featureviews";
        HttpPost postRequest = new HttpPost(endpoint);
        postRequest.setHeader("Content-Type", "application/json");
        postRequest.setEntity(new StringEntity(String.format("{\"name\":\"%s\", \"entityNames\":\"%s\", \"db\":\"%s\", \"sql\":\"%s\", \"description\":\"%s\"}", name, entityNames, db, escapedSql, description), "UTF-8"));
        HttpResponse response = httpClient.execute(postRequest);
        HttpEntity entity = response.getEntity();
        String responseBody = EntityUtils.toString(entity);

        if (response.getStatusLine().getStatusCode() == 200) {
            return objectMapper.readValue(responseBody, FeatureView.class);
        } else {
            throw new IOException("Fail to request server and get error: " + responseBody);
        }
    }

    public FeatureView getFeatureView(String name) throws IOException {
        String endpoint = this.apiEndpoint + "featureviews/" + name;
        HttpGet request = new HttpGet(endpoint);
        HttpResponse response = httpClient.execute(request);
        HttpEntity entity = response.getEntity();
        String responseBody = EntityUtils.toString(entity);

        if (response.getStatusLine().getStatusCode() == 200) {
            return objectMapper.readValue(responseBody, FeatureView.class);
        } else {
            throw new IOException("Fail to request server and get error: " + responseBody);
        }
    }

    public void deleteFeatureView(String name) throws IOException {
        String endpoint = this.apiEndpoint + "featureviews/" + name;
        HttpDelete deleteRequest = new HttpDelete(endpoint);
        HttpResponse response = httpClient.execute(deleteRequest);

        if (response.getStatusLine().getStatusCode() == 200) {

        } else {
            HttpEntity entity = response.getEntity();
            String responseBody = EntityUtils.toString(entity);
            throw new IOException("Fail to request server and get error: " + responseBody);
        }
    }

    public List<FeatureService> getFeatureServices() throws IOException {
        String endpoint = this.apiEndpoint + "featureservices";
        HttpGet request = new HttpGet(endpoint);
        HttpResponse response = httpClient.execute(request);
        HttpEntity entity = response.getEntity();
        String responseBody = EntityUtils.toString(entity);

        if (response.getStatusLine().getStatusCode() == 200) {
            return objectMapper.readValue(responseBody, new TypeReference<List<FeatureService>>() {});
        } else {
            throw new IOException("Fail to request server and get error: " + responseBody);
        }
    }

    public List<FeatureService> getLatestFeatureServices() throws IOException {
        String endpoint = this.apiEndpoint + "featureservices/latest";
        HttpGet request = new HttpGet(endpoint);
        HttpResponse response = httpClient.execute(request);
        HttpEntity entity = response.getEntity();
        String responseBody = EntityUtils.toString(entity);

        if (response.getStatusLine().getStatusCode() == 200) {
            return objectMapper.readValue(responseBody, new TypeReference<List<FeatureService>>() {});
        } else {
            throw new IOException("Fail to request server and get error: " + responseBody);
        }
    }

    public FeatureService createFeatureService(String name, String featureList) throws IOException {
        return this.createFeatureService(name, "v1", featureList);
    }

    public FeatureService createFeatureService(String name, String version, String featureList) throws IOException {
        String endpoint = this.apiEndpoint + "featureservices";
        HttpPost postRequest = new HttpPost(endpoint);
        postRequest.setHeader("Content-Type", "application/json");
        postRequest.setEntity(new StringEntity(String.format("{\"name\":\"%s\", \"version\": \"%s\", \"featureList\":\"%s\"}", name, version, featureList), "UTF-8"));
        HttpResponse response = httpClient.execute(postRequest);
        HttpEntity entity = response.getEntity();
        String responseBody = EntityUtils.toString(entity);

        if (response.getStatusLine().getStatusCode() == 200) {
            return objectMapper.readValue(responseBody, FeatureService.class);
        } else {
            throw new IOException("Fail to request server and get error: " + responseBody);
        }
    }

    public FeatureService createFeatureServiceFromDeployment(String name, String db, String deploymentName) throws IOException {
        String endpoint = this.apiEndpoint + "featureservices/deployments";
        HttpPost postRequest = new HttpPost(endpoint);
        postRequest.setHeader("Content-Type", "application/json");
        postRequest.setEntity(new StringEntity(String.format("{\"name\":\"%s\", \"db\":\"%s\", \"deploymentName\":\"%s\"}", name, db, deploymentName), "UTF-8"));
        HttpResponse response = httpClient.execute(postRequest);
        HttpEntity entity = response.getEntity();
        String responseBody = EntityUtils.toString(entity);

        if (response.getStatusLine().getStatusCode() == 200) {
            return objectMapper.readValue(responseBody, FeatureService.class);
        } else {
            throw new IOException("Fail to request server and get error: " + responseBody);
        }
    }

    public FeatureService createFeatureServiceFromDeployment(String name, String version, String db, String deploymentName) throws IOException {
        String endpoint = this.apiEndpoint + "featureservices/deployments";
        HttpPost postRequest = new HttpPost(endpoint);
        postRequest.setHeader("Content-Type", "application/json");
        postRequest.setEntity(new StringEntity(String.format("{\"name\":\"%s\", \"version\":\"%s\", \"db\":\"%s\", \"deploymentName\":\"%s\"}", name, version, db, deploymentName), "UTF-8"));
        HttpResponse response = httpClient.execute(postRequest);
        HttpEntity entity = response.getEntity();
        String responseBody = EntityUtils.toString(entity);

        if (response.getStatusLine().getStatusCode() == 200) {
            return objectMapper.readValue(responseBody, FeatureService.class);
        } else {
            throw new IOException("Fail to request server and get error: " + responseBody);
        }
    }

    public FeatureService getFeatureService(String name) throws IOException {
        String endpoint = this.apiEndpoint + "featureservices/" + name;
        HttpGet request = new HttpGet(endpoint);
        HttpResponse response = httpClient.execute(request);
        HttpEntity entity = response.getEntity();
        String responseBody = EntityUtils.toString(entity);

        if (response.getStatusLine().getStatusCode() == 200) {
            return objectMapper.readValue(responseBody, FeatureService.class);
        } else {
            throw new IOException("Fail to request server and get error: " + responseBody);
        }
    }

    public FeatureService getFeatureService(String name, String version) throws IOException {
        String endpoint = this.apiEndpoint + "featureservices/" + name + "/" + version;
        HttpGet request = new HttpGet(endpoint);
        HttpResponse response = httpClient.execute(request);
        HttpEntity entity = response.getEntity();
        String responseBody = EntityUtils.toString(entity);

        if (response.getStatusLine().getStatusCode() == 200) {
            return objectMapper.readValue(responseBody, FeatureService.class);
        } else {
            throw new IOException("Fail to request server and get error: " + responseBody);
        }
    }

    public List<String> getFeatureServiceDependentTables(String name) throws IOException {
        String endpoint = this.apiEndpoint + "featureservices/" + name + "/tables";
        HttpGet request = new HttpGet(endpoint);
        HttpResponse response = httpClient.execute(request);
        HttpEntity entity = response.getEntity();
        String responseBody = EntityUtils.toString(entity);

        if (response.getStatusLine().getStatusCode() == 200) {
            return objectMapper.readValue(responseBody, new TypeReference<List<String>>() {});
        } else {
            throw new IOException("Fail to request server and get error: " + responseBody);
        }
    }

    public List<String> getFeatureServiceDependentTables(String name, String version) throws IOException {
        String endpoint = this.apiEndpoint + "featureservices/" + name + "/" + version + "/tables";
        HttpGet request = new HttpGet(endpoint);
        HttpResponse response = httpClient.execute(request);
        HttpEntity entity = response.getEntity();
        String responseBody = EntityUtils.toString(entity);

        if (response.getStatusLine().getStatusCode() == 200) {
            return objectMapper.readValue(responseBody, new TypeReference<List<String>>() {});
        } else {
            throw new IOException("Fail to request server and get error: " + responseBody);
        }
    }

    public void deleteFeatureService(String name) throws IOException {
        String endpoint = this.apiEndpoint + "featureservices/" + name;
        HttpDelete deleteRequest = new HttpDelete(endpoint);
        HttpResponse response = httpClient.execute(deleteRequest);

        if (response.getStatusLine().getStatusCode() == 200) {

        } else {
            HttpEntity entity = response.getEntity();
            String responseBody = EntityUtils.toString(entity);
            throw new IOException("Fail to request server and get error: " + responseBody);
        }
    }

    public void deleteFeatureService(String name, String version) throws IOException {
        String endpoint = this.apiEndpoint + "featureservices/" + name + "/" + version;
        HttpDelete deleteRequest = new HttpDelete(endpoint);
        HttpResponse response = httpClient.execute(deleteRequest);

        if (response.getStatusLine().getStatusCode() == 200) {

        } else {
            HttpEntity entity = response.getEntity();
            String responseBody = EntityUtils.toString(entity);
            throw new IOException("Fail to request server and get error: " + responseBody);
        }
    }

    public String getFeatureServiceRequestSchema(String name) throws IOException {
        String endpoint = this.apiEndpoint + "featureservices/" + name + "/request/schema";
        HttpGet getRequest = new HttpGet(endpoint);
        HttpResponse response = httpClient.execute(getRequest);
        HttpEntity entity = response.getEntity();
        String responseBody = EntityUtils.toString(entity);

        if (response.getStatusLine().getStatusCode() == 200) {
            return responseBody;
        } else {
            throw new IOException("Fail to request server and get error: " + responseBody);
        }
    }

    public String getFeatureServiceRequestSchema(String name, String version) throws IOException {
        String endpoint = this.apiEndpoint + "featureservices/" + name + "/" + version + "/request/schema";
        HttpGet getRequest = new HttpGet(endpoint);
        HttpResponse response = httpClient.execute(getRequest);
        HttpEntity entity = response.getEntity();
        String responseBody = EntityUtils.toString(entity);

        if (response.getStatusLine().getStatusCode() == 200) {
            return responseBody;
        } else {
            throw new IOException("Fail to request server and get error: " + responseBody);
        }
    }

    public String getFeatureServiceOutputSchema(String name) throws IOException {
        String endpoint = this.apiEndpoint + "featureservices/" + name + "/output/schema";
        HttpGet getRequest = new HttpGet(endpoint);
        HttpResponse response = httpClient.execute(getRequest);
        HttpEntity entity = response.getEntity();
        String responseBody = EntityUtils.toString(entity);

        if (response.getStatusLine().getStatusCode() == 200) {
            return responseBody;
        } else {
            throw new IOException("Fail to request server and get error: " + responseBody);
        }
    }

    public String getFeatureServiceOutputSchema(String name, String version) throws IOException {
        String endpoint = this.apiEndpoint + "featureservices/" + name + "/" + version + "/output/schema";
        HttpGet getRequest = new HttpGet(endpoint);
        HttpResponse response = httpClient.execute(getRequest);
        HttpEntity entity = response.getEntity();
        String responseBody = EntityUtils.toString(entity);

        if (response.getStatusLine().getStatusCode() == 200) {
            return responseBody;
        } else {
            throw new IOException("Fail to request server and get error: " + responseBody);
        }
    }

    public String getFeatureServiceRequestDemoData(String name) throws IOException {
        String endpoint = this.apiEndpoint + "featureservices/" + name + "/request/demo";
        HttpGet getRequest = new HttpGet(endpoint);
        HttpResponse response = httpClient.execute(getRequest);
        HttpEntity entity = response.getEntity();
        String responseBody = EntityUtils.toString(entity);

        if (response.getStatusLine().getStatusCode() == 200) {
            return responseBody;
        } else {
            throw new IOException("Fail to request server and get error: " + responseBody);
        }
    }

    public String getFeatureServiceRequestDemoData(String name, String version) throws IOException {
        String endpoint = this.apiEndpoint + "featureservices/" + name + "/" + version + "/request/demo";
        HttpGet getRequest = new HttpGet(endpoint);
        HttpResponse response = httpClient.execute(getRequest);
        HttpEntity entity = response.getEntity();
        String responseBody = EntityUtils.toString(entity);

        if (response.getStatusLine().getStatusCode() == 200) {
            return responseBody;
        } else {
            throw new IOException("Fail to request server and get error: " + responseBody);
        }
    }

    public String executeSql(String sql) throws IOException {
        String endpoint = this.apiEndpoint + "sql/execute";
        HttpPost postRequest = new HttpPost(endpoint);
        postRequest.setHeader("Content-Type", "application/json");
        postRequest.setEntity(new StringEntity(String.format("{\"sql\":\"%s\"}", sql), "UTF-8"));
        HttpResponse response = httpClient.execute(postRequest);
        HttpEntity entity = response.getEntity();
        String responseBody = EntityUtils.toString(entity);

        if (response.getStatusLine().getStatusCode() == 200) {
            return responseBody;
        } else {
            throw new IOException("Fail to request server and get error: " + responseBody);
        }
    }

    public void validateSql(String sql) throws IOException {
        String endpoint = this.apiEndpoint + "sql/validate";
        HttpPost postRequest = new HttpPost(endpoint);
        postRequest.setHeader("Content-Type", "application/json");
        postRequest.setEntity(new StringEntity(String.format("{\"sql\":\"%s\"}", sql), "UTF-8"));
        HttpResponse response = httpClient.execute(postRequest);

        if (response.getStatusLine().getStatusCode() == 200) {

        } else {
            HttpEntity entity = response.getEntity();
            String responseBody = EntityUtils.toString(entity);
            throw new IOException("Fail to request server and get error: " + responseBody);
        }
    }

    public HttpResponse requestFeatureService(String featureService, String requestData) throws IOException {
        String endpoint = this.apiEndpoint + "featureservices/" + featureService + "/request";
        HttpPost postRequest = new HttpPost(endpoint);
        postRequest.setHeader("Content-Type", "application/json");
        postRequest.setEntity(new StringEntity(requestData, ContentType.APPLICATION_JSON));
        HttpResponse response = httpClient.execute(postRequest);

        if (response.getStatusLine().getStatusCode() == 200) {
            return response;
        } else {
            HttpEntity entity = response.getEntity();
            String responseBody = EntityUtils.toString(entity);
            throw new IOException("Fail to request server and get error: " + responseBody);
        }
    }

    public HttpResponse requestFeatureService(String featureService, String version, String requestData) throws IOException {
        String endpoint = this.apiEndpoint + "featureservices/" + featureService + "/" + version + "/request";
        HttpPost postRequest = new HttpPost(endpoint);
        postRequest.setHeader("Content-Type", "application/json");
        postRequest.setEntity(new StringEntity(requestData, ContentType.APPLICATION_JSON));
        HttpResponse response = httpClient.execute(postRequest);

        if (response.getStatusLine().getStatusCode() == 200) {
            return response;
        } else {
            HttpEntity entity = response.getEntity();
            String responseBody = EntityUtils.toString(entity);
            throw new IOException("Fail to request server and get error: " + responseBody);
        }
    }

    public HttpResponse requestApiServer(String apiServerEndpoint, String featureServiceName, String requestData) throws IOException {
        FeatureService featureService = getFeatureService(featureServiceName);
        String endpoint = String.format("%s/dbs/%s/deployments/%s", apiServerEndpoint, featureService.getDb(), featureService.getDeployment());
        HttpPost postRequest = new HttpPost(endpoint);
        postRequest.setHeader("Content-Type", "application/json");
        postRequest.setEntity(new StringEntity(requestData, "UTF-8"));
        HttpResponse response = httpClient.execute(postRequest);


        if (response.getStatusLine().getStatusCode() == 200) {
            return response;
        } else {
            HttpEntity entity = response.getEntity();
            String responseBody = EntityUtils.toString(entity);
            throw new IOException("Fail to request server and get error: " + responseBody);
        }
    }

    public HttpResponse requestApiServer(String apiServerEndpoint, String featureServiceName, String featureServiceVersion, String requestData) throws IOException {
        FeatureService featureService = getFeatureService(featureServiceName, featureServiceVersion);
        String endpoint = String.format("%s/dbs/%s/deployments/%s", apiServerEndpoint, featureService.getDb(), featureService.getDeployment());
        HttpPost postRequest = new HttpPost(endpoint);
        postRequest.setHeader("Content-Type", "application/json");
        postRequest.setEntity(new StringEntity(requestData, "UTF-8"));
        HttpResponse response = httpClient.execute(postRequest);

        if (response.getStatusLine().getStatusCode() == 200) {
            return response;
        } else {
            HttpEntity entity = response.getEntity();
            String responseBody = EntityUtils.toString(entity);
            throw new IOException("Fail to request server and get error: " + responseBody);
        }
    }

    public void close() throws IOException {
        this.httpClient.close();
    }

}
