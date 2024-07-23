package com._4paradigm.openmldb.featureplatform.client;

import com._4paradigm.openmldb.featureplatform.controller.FesqlPredictorController;
import com._4paradigm.openmldb.featureplatform.dao.model.FesqlTableColumn;
import com._4paradigm.openmldb.featureplatform.dao.model.SimpleTableInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProphetTelamonClient {
    private static final Logger logger = LoggerFactory.getLogger(ProphetTelamonClient.class);

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final String apiEndpoint;
    private final Map<String, String> headers;

    public ProphetTelamonClient(String apiEndpoint, Map<String, String> headers) {
        this.apiEndpoint = apiEndpoint;
        this.headers = headers;
    }

    public List<FesqlTableColumn> getTableSchema(String tablePrn) throws Exception {
        long startTime = System.currentTimeMillis();
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String url = String.format("%s/telamon/v1/tables?prn=%s", this.apiEndpoint, tablePrn);
            HttpGet httpGet = new HttpGet(url);
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpGet.setHeader(entry.getKey(), entry.getValue());
            }
            HttpResponse response = httpClient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                throw new RuntimeException(String.format("Fail get table prn: %s, http status code: %s", tablePrn, statusCode));
            }
            HttpEntity entity = response.getEntity();
            String responseBody = EntityUtils.toString(entity);
            QueryTableResponse tableResponse = objectMapper.readValue(responseBody, QueryTableResponse.class);
            if (!"0".equals(tableResponse.getStatus()) || tableResponse.getData().getList().isEmpty()) {
                throw new Exception(String.format("Fail get table prn: %s, responseBody: %s", tablePrn, response));
            }
            return tableResponse.getData().getList().get(0).getSchema();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            long endTime = System.currentTimeMillis();
            logger.info("get table times: {} ms, prn: {}", endTime - startTime, tablePrn);
        }
    }

//    /**
//     * Get Prophet table-schema.
//     *
//     * @return
//     * @throws Exception
//     */
//    public List<FesqlTableColumn> getTableSchema(String tablePrn) throws Exception {
//        String endpoint = String.format("%s/v1/tables?prn=%s", this.apiEndpoint, tablePrn);
//        HttpGet request = new HttpGet(endpoint);
//        for (Map.Entry<String, String> entry : headers.entrySet()) {
//            request.setHeader(entry.getKey(), entry.getValue());
//        }
//        HttpResponse response = httpClient.execute(request);
//        HttpEntity entity = response.getEntity();
//        String responseBody = EntityUtils.toString(entity);
//
//        if (response.getStatusLine().getStatusCode() != 200) {
//            throw new IOException("Fail to request server and get error: " + responseBody);
//        }
//        QueryTableResponse tableResponse = objectMapper.readValue(responseBody, QueryTableResponse.class);
//        if (!"0".equals(tableResponse.getStatus()) || tableResponse.getData().getList().isEmpty()) {
//            throw new Exception(String.format("Fail get table prn: %s, responseBody: %s", tablePrn, response));
//        }
//        return tableResponse.getData().getList().get(0).getSchema();
//    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class QueryTableResponse {
        private String status;
        private String msg;
        private TableList data;

    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class TableList {
        private List<TableInfo> list;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class TableInfo {
        private String prn;
        private String name;
        private List<FesqlTableColumn> schema;
    }
}
