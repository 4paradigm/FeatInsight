package com._4paradigm.openmldb.featureplatform.controller;

import com._4paradigm.openmldb.featureplatform.dao.model.FeatureServiceDeploymentRequest;
import com._4paradigm.openmldb.featureplatform.dao.model.UpdateLatestVersionRequest;
import com._4paradigm.openmldb.sdk.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import com._4paradigm.openmldb.featureplatform.dao.model.FeatureService;
import com._4paradigm.openmldb.featureplatform.dao.FeatureServiceService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/featureservices")
public class FeatureServiceController {

    private final FeatureServiceService featureServiceService;

    @Autowired
    public FeatureServiceController(FeatureServiceService featureServiceService) {
        this.featureServiceService = featureServiceService;
    }

    @GetMapping
    public List<FeatureService> getFeatureServices() {
        return featureServiceService.getFeatureServices();
    }

    @GetMapping("/latest")
    public List<FeatureService> getLatestFeatureServices() {
        return featureServiceService.getLatestFeatureServices();
    }

    @GetMapping("/{name}")
    public FeatureService getFeatureServiceByName(@PathVariable String name) {
        return featureServiceService.getFeatureServiceByName(name);
    }

    @PostMapping
    public FeatureService createFeatureService(@RequestBody FeatureService featureService) {
        return featureServiceService.createFeatureService(featureService);
    }

    @PutMapping(value = "/{name}/latestversion")
    public ResponseEntity<String> updateLatestVersion(@PathVariable String name, @RequestBody UpdateLatestVersionRequest request) {
        try {
            featureServiceService.updateLatestVersion(name, request.getVersion());
            return new ResponseEntity<>("Success to update latest version", HttpStatus.OK);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Fail to update latest version", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{name}/versions")
    public List<String> getFeatureServiceVersions(@PathVariable String name) {
        return featureServiceService.getFeatureServiceVersions(name);
    }

    @GetMapping("/{name}/latestversion")
    public String getFeatureServiceLatestVersion(@PathVariable String name) {
        try {
            return featureServiceService.getLatestVersion(name);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    @PostMapping(value = "/{name}/request", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> requestFeatureService(@PathVariable String name, @RequestBody String dataRequest) {
        try {
            return featureServiceService.requestFeatureService(name, dataRequest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/{name}/{version}")
    public FeatureService getFeatureServiceByName(@PathVariable String name, @PathVariable String version) {
        return featureServiceService.getFeatureServiceByNameAndVersion(name, version);
    }

    @DeleteMapping("/{name}/{version}")
    public ResponseEntity<String> deleteFeatureService(@PathVariable String name, @PathVariable String version) {
        if (featureServiceService.deleteFeatureService(name, version)) {
            return new ResponseEntity<>("Success to delete", HttpStatus.OK);
        } else {
            // TODO: Handle for different error code
            return new ResponseEntity<>("Success to delete", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/{name}/{version}/request", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> requestFeatureService(@PathVariable String name, @PathVariable String version, @RequestBody String dataRequest) {
        try {
            if (version.isEmpty()) {
                return featureServiceService.requestFeatureService(name, dataRequest);
            } else {
                return featureServiceService.requestFeatureService(name, version, dataRequest);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping(value = "/deployments")
    public FeatureService createFeatureServiceFromDeployment(@RequestBody FeatureServiceDeploymentRequest request) {
        return featureServiceService.createFeatureServiceFromDeployment(request);
    }

    @GetMapping("/{name}/{version}/request/schema")
    public ResponseEntity<String> getRequestSchema(@PathVariable String name, @PathVariable String version) {
        try {
            if (version.isEmpty()) {
                Schema schema = featureServiceService.getRequestSchema(name);
                return new ResponseEntity<>(schema.toString(), HttpStatus.OK);
            } else {
                Schema schema = featureServiceService.getRequestSchema(name, version);
                return new ResponseEntity<>(schema.toString(), HttpStatus.OK);
            }
        } catch (SQLException e) {
            return new ResponseEntity<>("Success to delete", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{name}/request/schema")
    public ResponseEntity<String> getRequestSchema(@PathVariable String name) {
        try {
            Schema schema = featureServiceService.getRequestSchema(name);
            return new ResponseEntity<>(schema.toString(), HttpStatus.OK);
        } catch (SQLException e) {
            return new ResponseEntity<>("Success to delete", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{name}/{version}/request/demo")
    public ResponseEntity<String> getRequestDemoData(@PathVariable String name, @PathVariable String version) {
        try {
            if (version.isEmpty()) {
                String demoData = featureServiceService.getRequestDemoData(name);
                return new ResponseEntity<>(demoData, HttpStatus.OK);
            } else {
                String demoData = featureServiceService.getRequestDemoData(name, version);
                return new ResponseEntity<>(demoData, HttpStatus.OK);
            }
        } catch (SQLException e) {
            return new ResponseEntity<>("Success to delete", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{name}/request/demo")
    public ResponseEntity<String> getRequestDemoData(@PathVariable String name) {
        try {
            String demoData = featureServiceService.getRequestDemoData(name);
            return new ResponseEntity<>(demoData, HttpStatus.OK);
        } catch (SQLException e) {
            return new ResponseEntity<>("Success to delete", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{name}/{version}/tables")
    public List<String> getFeatureServiceDependentTables(@PathVariable String name, @PathVariable String version) {
        try {
            if (version.isEmpty()) {
                return featureServiceService.getDependentTables(name);
            } else {
                return featureServiceService.getDependentTables(name, version);
            }
        } catch (SQLException e) {
            return null;
        }
    }

    @GetMapping("/{name}/tables")
    public List<String> getFeatureServiceDependentTables(@PathVariable String name) {
        try {
            return featureServiceService.getDependentTables(name);
        } catch (SQLException e) {
            return null;
        }
    }

    @GetMapping("/{name}/output/schema")
    public ResponseEntity<String> getOutputSchema(@PathVariable String name) {
        try {
            Schema schema = featureServiceService.getOutputSchema(name);
            return new ResponseEntity<>(schema.toString(), HttpStatus.OK);
        } catch (SQLException e) {
            return new ResponseEntity<>("Success to delete", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{name}/{version}/output/schema")
    public ResponseEntity<String> getOutputSchema(@PathVariable String name, @PathVariable String version) {
        try {
            if (version.isEmpty()) {
                Schema schema = featureServiceService.getOutputSchema(name);
                return new ResponseEntity<>(schema.toString(), HttpStatus.OK);
            } else {
                Schema schema = featureServiceService.getOutputSchema(name, version);
                return new ResponseEntity<>(schema.toString(), HttpStatus.OK);
            }
        } catch (SQLException e) {
            return new ResponseEntity<>("Success to delete", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}