package com._4paradigm.openmldb.featureplatform.controller;

import com._4paradigm.openmldb.featureplatform.dao.model.OfflineJobInfo;
import com._4paradigm.openmldb.featureplatform.service.OfflineJobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/offlinejobs")
public class OfflineJobController {
    private static final Logger logger = LoggerFactory.getLogger(FeatureController.class);

    private final OfflineJobService offlineJobService;

    @Autowired
    public OfflineJobController(OfflineJobService offlineJobService) {
        this.offlineJobService = offlineJobService;
    }

    @GetMapping
    public List<OfflineJobInfo> getOfflineJobInfos() throws SQLException {
        try {
            return offlineJobService.getOfflineJobInfos();
        } catch (SQLException e) {
            logger.info(String.format("Call getOfflineJobInfos but get exception: %s", e.getMessage()));
            throw e;
        }
    }

    @GetMapping("/{id}")
    public OfflineJobInfo getOfflineJobInfo(@PathVariable int id) throws SQLException {
        try {
            return offlineJobService.getOfflineJobInfo(id);
        } catch (SQLException e) {
            logger.info(String.format("Call getOfflineJobInfo with %d but get exception: %s", id, e.getMessage()));
            throw e;
        }
    }

    @GetMapping("/{id}/log")
    public String getOfflineJobLog(@PathVariable int id) throws SQLException {
        try {
            return offlineJobService.getOfflineJobLog(id);
        } catch (SQLException e) {
            logger.info(String.format("Call getOfflineJobLog with %d but get exception: %s", id, e.getMessage()));
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    public void deleteFeatureService(@PathVariable int id) throws SQLException {
        try {
            offlineJobService.deleteOfflineJob(id);
        } catch (SQLException e) {
            logger.info(String.format("Call deleteFeatureService with %d but get exception: %s", id, e.getMessage()));
            throw e;
        }
    }

}