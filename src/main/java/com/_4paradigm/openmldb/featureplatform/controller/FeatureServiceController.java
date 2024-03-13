package com._4paradigm.openmldb.featureplatform.controller;

import com._4paradigm.openmldb.featureplatform.dao.model.DatabaseTable;
import com._4paradigm.openmldb.featureplatform.dao.model.FeatureService;
import com._4paradigm.openmldb.featureplatform.dao.model.UpdateLatestVersionRequest;
import com._4paradigm.openmldb.featureplatform.service.FeatureServiceService;
import com._4paradigm.openmldb.sdk.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/featureservices")
public class FeatureServiceController {
    private static final Logger logger = LoggerFactory.getLogger(FeatureController.class);
    private final FeatureServiceService featureServiceService;

    @Autowired
    public FeatureServiceController(FeatureServiceService featureServiceService) {
        this.featureServiceService = featureServiceService;
    }

    @GetMapping
    public List<FeatureService> getFeatureServices() throws SQLException {
        try {
            return featureServiceService.getFeatureServices();
        } catch (SQLException e) {
            logger.info(String.format("Call getFeatureServices but get exception: %s", e.getMessage()));
            throw e;
        }
    }

    @GetMapping("/latest")
    public List<FeatureService> getLatestFeatureServices() throws SQLException {
        try {
            return featureServiceService.getLatestFeatureServices();
        } catch (SQLException e) {
            logger.info(String.format("Call getLatestFeatureServices but get exception: %s", e.getMessage()));
            throw e;
        }
    }

    @GetMapping("/{name}")
    public FeatureService getFeatureServiceByName(@PathVariable String name) throws SQLException {
        try {
            return featureServiceService.getFeatureServiceByName(name);
        } catch (SQLException e) {
            logger.info(String.format("Call getFeatureServiceByName with %s but get exception: %s", name, e.getMessage()));
            throw e;
        }
    }

    @PostMapping
    public FeatureService createFeatureService(@RequestBody FeatureService featureService) throws SQLException {
        try {
            return featureServiceService.createFeatureService(featureService);
        } catch (SQLException e) {
            logger.info(String.format("Call createFeatureService with %s but get exception: %s", featureService, e.getMessage()));
            throw e;
        }
    }

    @PutMapping(value = "/{name}/latestversion")
    public ResponseEntity<String> updateLatestVersion(@PathVariable String name, @RequestBody UpdateLatestVersionRequest request) throws SQLException {
        try {
            featureServiceService.updateLatestVersion(name, request.getVersion());
            return new ResponseEntity<>("Success to update latest version", HttpStatus.OK);
        } catch (SQLException e) {
            logger.info(String.format("Call updateLatestVersion with %s and %s but get exception: %s", name, request, e.getMessage()));
            throw e;
        }
    }

    @GetMapping("/{name}/versions")
    public List<String> getFeatureServiceVersions(@PathVariable String name) throws SQLException {
        try {
            return featureServiceService.getFeatureServiceVersions(name);
        } catch (SQLException e) {
            logger.info(String.format("Call getFeatureServiceVersions with %s but get exception: %s", name, e.getMessage()));
            throw e;
        }
    }

    @GetMapping("/{name}/latestversion")
    public String getFeatureServiceLatestVersion(@PathVariable String name) throws SQLException {
        try {
            return featureServiceService.getLatestVersion(name);
        } catch (SQLException e) {
            logger.info(String.format("Call getFeatureServiceLatestVersion with %s but get exception: %s", name, e.getMessage()));
            throw e;
        }
    }


    @PostMapping(value = "/{name}/request", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> requestFeatureService(@PathVariable String name, @RequestBody String dataRequest) throws SQLException {
        try {
            return featureServiceService.requestFeatureService(name, dataRequest);
        } catch (Exception e) {
            logger.info(String.format("Call requestFeatureService with %s and %s but get exception: %s", name, dataRequest, e.getMessage()));
            throw new SQLException(e.getMessage());
        }
    }

    @GetMapping("/{name}/{version}")
    public FeatureService getFeatureServiceByName(@PathVariable String name, @PathVariable String version) throws SQLException {
        try {
            return featureServiceService.getFeatureServiceByNameAndVersion(name, version);
        } catch (SQLException e) {
            logger.info(String.format("Call getFeatureServiceByName with %s and %s but get exception: %s", name, version, e.getMessage()));
            throw e;
        }
    }

    @DeleteMapping("/{name}")
    public void deleteFeatureService(@PathVariable String name) throws SQLException {
        try {
            featureServiceService.deleteFeatureService(name);
        } catch (SQLException e) {
            logger.info(String.format("Call deleteFeatureService with %s but get exception: %s", name, e.getMessage()));
            throw e;
        }
    }

    @DeleteMapping("/{name}/{version}")
    public void deleteFeatureService(@PathVariable String name, @PathVariable String version) throws SQLException {
        try {
            featureServiceService.deleteFeatureService(name, version);
        } catch (SQLException e) {
            logger.info(String.format("Call deleteFeatureService with %s and %s but get exception: %s", name, version, e.getMessage()));
            throw e;
        }
    }

    @PostMapping(value = "/{name}/{version}/request", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> requestFeatureService(@PathVariable String name, @PathVariable String version, @RequestBody String dataRequest) throws SQLException {
        try {
            if (version.isEmpty()) {
                return featureServiceService.requestFeatureService(name, dataRequest);
            } else {
                return featureServiceService.requestFeatureService(name, version, dataRequest);
            }
        } catch (Exception e) {
            logger.info(String.format("Call requestFeatureService with %s, %s and %s but get exception: %s", name, version, dataRequest, e.getMessage()));
            throw new SQLException(e.getMessage());
        }
    }

    @PostMapping(value = "/{name}/{version}/request/onlinequerymode", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<List<String>>> requestOnlineQueryMode(@PathVariable String name, @PathVariable String version, @RequestBody String dataRequest) throws SQLException {
        try {
            return ResponseEntity.ok(featureServiceService.requestOnlineQueryMode(name, version, dataRequest));
        } catch (Exception e) {
            logger.info(String.format("Call requestOnlineQueryMode with %s, %s and %s but get exception: %s", name, version, dataRequest, e.getMessage()));
            throw new SQLException(e.getMessage());
        }
    }

    @PostMapping(value = "/{name}/{version}/request/onlinequerymode/samples")
    public ResponseEntity<List<List<String>>> requestOnlineQueryModeSamples(@PathVariable String name, @PathVariable String version) throws SQLException {
        try {
            return ResponseEntity.ok(featureServiceService.requestOnlineQueryModeSamples(name, version));
        } catch (Exception e) {
            logger.info(String.format("Call requestOnlineQueryModeSample with %s and %s but get exception: %s", name, version, e.getMessage()));
            throw new SQLException(e.getMessage());
        }
    }

    @GetMapping("/{name}/{version}/request/schema")
    public ResponseEntity<String> getRequestSchema(@PathVariable String name, @PathVariable String version) throws SQLException {
        try {
            if (version.isEmpty()) {
                Schema schema = featureServiceService.getRequestSchema(name);
                return new ResponseEntity<>(schema.toString(), HttpStatus.OK);
            } else {
                Schema schema = featureServiceService.getRequestSchema(name, version);
                return new ResponseEntity<>(schema.toString(), HttpStatus.OK);
            }
        } catch (SQLException e) {
            logger.info(String.format("Call getRequestSchema with %s and %s but get exception: %s", name, version, e.getMessage()));
            throw new SQLException(e.getMessage());
        }
    }

    @GetMapping("/{name}/request/schema")
    public ResponseEntity<String> getRequestSchema(@PathVariable String name) throws SQLException {
        try {
            Schema schema = featureServiceService.getRequestSchema(name);
            return new ResponseEntity<>(schema.toString(), HttpStatus.OK);
        } catch (SQLException e) {
            logger.info(String.format("Call getRequestSchema with %s but get exception: %s", name, e.getMessage()));
            throw new SQLException(e.getMessage());
        }
    }

    @GetMapping("/{name}/{version}/request/demo")
    public ResponseEntity<String> getRequestDemoData(@PathVariable String name, @PathVariable String version) throws SQLException {
        try {
            if (version.isEmpty()) {
                String demoData = featureServiceService.getRequestDemoData(name);
                return new ResponseEntity<>(demoData, HttpStatus.OK);
            } else {
                String demoData = featureServiceService.getRequestDemoData(name, version);
                return new ResponseEntity<>(demoData, HttpStatus.OK);
            }
        } catch (SQLException e) {
            logger.info(String.format("Call getRequestDemoData with %s and %s but get exception: %s", name, version, e.getMessage()));
            throw new SQLException(e.getMessage());
        }
    }

    @GetMapping("/{name}/request/demo")
    public ResponseEntity<String> getRequestDemoData(@PathVariable String name) throws SQLException {
        try {
            String demoData = featureServiceService.getRequestDemoData(name);
            return new ResponseEntity<>(demoData, HttpStatus.OK);
        } catch (SQLException e) {
            logger.info(String.format("Call getRequestDemoData with %s but get exception: %s", name, e.getMessage()));
            throw new SQLException(e.getMessage());
        }
    }

    @GetMapping("/{name}/{version}/tables")
    public List<String> getFeatureServiceDependentTables(@PathVariable String name, @PathVariable String version) throws SQLException {
        try {
            if (version.isEmpty()) {
                return featureServiceService.getDependentTables(name);
            } else {
                return featureServiceService.getDependentTables(name, version);
            }
        } catch (SQLException e) {
            logger.info(String.format("Call getFeatureServiceDependentTables with %s and %s but get exception: %s", name, version, e.getMessage()));
            throw new SQLException(e.getMessage());
        }
    }

    @GetMapping("/{name}/tables")
    public List<String> getFeatureServiceDependentTables(@PathVariable String name) throws SQLException {
        try {
            return featureServiceService.getDependentTables(name);
        } catch (SQLException e) {
            logger.info(String.format("Call getFeatureServiceDependentTables with %s but get exception: %s", name, e.getMessage()));
            throw new SQLException(e.getMessage());
        }
    }

    @GetMapping("/{name}/output/schema")
    public ResponseEntity<String> getOutputSchema(@PathVariable String name) throws SQLException {
        try {
            Schema schema = featureServiceService.getOutputSchema(name);
            return new ResponseEntity<>(schema.toString(), HttpStatus.OK);
        } catch (SQLException e) {
            logger.info(String.format("Call getFeatureServiceDependentTables with %s but get exception: %s", name, e.getMessage()));
            throw new SQLException(e.getMessage());
        }
    }

    @GetMapping("/{name}/{version}/output/schema")
    public ResponseEntity<String> getOutputSchema(@PathVariable String name, @PathVariable String version) throws SQLException {
        try {
            if (version.isEmpty()) {
                Schema schema = featureServiceService.getOutputSchema(name);
                return new ResponseEntity<>(schema.toString(), HttpStatus.OK);
            } else {
                Schema schema = featureServiceService.getOutputSchema(name, version);
                return new ResponseEntity<>(schema.toString(), HttpStatus.OK);
            }
        } catch (SQLException e) {
            logger.info(String.format("Call getOutputSchema with %s and %s but get exception: %s", name, version, e.getMessage()));
            throw new SQLException(e.getMessage());
        }
    }

    @GetMapping("/{name}/{version}/indexes")
    public ResponseEntity<List<String>> getIndexNames(@PathVariable String name, @PathVariable String version) throws SQLException {
        try {
            return new ResponseEntity<>(featureServiceService.getIndexNames(name, version), HttpStatus.OK);
        } catch (SQLException e) {
            logger.info(String.format("Call getRequestSchema with %s and %s but get exception: %s", name, version, e.getMessage()));
            throw new SQLException(e.getMessage());
        }
    }

    @GetMapping("/{name}/{version}/maintable")
    public ResponseEntity<DatabaseTable> getMainTable(@PathVariable String name, @PathVariable String version) throws SQLException {
        try {
            return new ResponseEntity<>(featureServiceService.getMainTable(name, version), HttpStatus.OK);
        } catch (SQLException e) {
            logger.info(String.format("Call getRequestSchema with %s and %s but get exception: %s", name, version, e.getMessage()));
            throw new SQLException(e.getMessage());
        }
    }


}