package com._4paradigm.openmldb.featureplatform.controller;

import com._4paradigm.openmldb.featureplatform.dao.FeatureServiceService;
import com._4paradigm.openmldb.featureplatform.dao.OfflineJobService;
import com._4paradigm.openmldb.featureplatform.dao.model.FeatureService;
import com._4paradigm.openmldb.featureplatform.dao.model.FeatureServiceDeploymentRequest;
import com._4paradigm.openmldb.featureplatform.dao.model.OfflineJobInfo;
import com._4paradigm.openmldb.featureplatform.dao.model.UpdateLatestVersionRequest;
import com._4paradigm.openmldb.sdk.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/offlinejobs")
public class OfflineJobController {

    private final OfflineJobService offlineJobService;

    @Autowired
    public OfflineJobController(OfflineJobService offlineJobService) {
        this.offlineJobService = offlineJobService;
    }

    @GetMapping
    public List<OfflineJobInfo> getOfflineJobInfos() throws SQLException {
        return offlineJobService.getOfflineJobInfos();
    }


    @GetMapping("/{id}")
    public OfflineJobInfo getOfflineJobInfo(@PathVariable int id) throws SQLException {
        return offlineJobService.getOfflineJobInfo(id);
    }



}