package com._4paradigm.openmldb.featureplatform.controller;

import com._4paradigm.openmldb.featureplatform.dao.model.OfflineSample;
import com._4paradigm.openmldb.featureplatform.service.OfflineSampleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.sql.SQLException;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/offlinesamples")
public class OfflineSampleController {
    private static final Logger logger = LoggerFactory.getLogger(FeatureController.class);

    private final OfflineSampleService offlineSampleService;

    @Autowired
    public OfflineSampleController(OfflineSampleService offlineSampleService) {
        this.offlineSampleService = offlineSampleService;
    }

    @GetMapping
    public List<OfflineSample> getOfflineSamples() throws SQLException {
        try {
            return offlineSampleService.getOfflineSamples();
        } catch (SQLException e) {
            logger.info(String.format("Call getOfflineSamples but get exception: %s", e.getMessage()));
            throw e;
        }
    }


    @GetMapping("/{id}")
    public OfflineSample getOfflineSample(@PathVariable int id) throws SQLException {
        try {
            return offlineSampleService.getOfflineSample(id);
        } catch (SQLException e) {
            logger.info(String.format("Call getOfflineSample with %d but get exception: %s", id, e.getMessage()));
            throw e;
        }
    }

    @PostMapping
    public OfflineSample createOfflineSample(@RequestBody OfflineSample offlineSample) throws SQLException {
        try {
            return offlineSampleService.createOfflineSample(offlineSample);
        } catch (SQLException e) {
            logger.info(String.format("Call createOfflineSample with %s but get exception: %s", offlineSample, e.getMessage()));
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    public void deleteOfflineSample(@PathVariable int id) throws SQLException {
        try {
            offlineSampleService.deleteOfflineSample(id);
        } catch (SQLException e) {
            logger.info(String.format("Call deleteOfflineSample with %d but get exception: %s", id, e.getMessage()));
            throw e;
        }
    }

}