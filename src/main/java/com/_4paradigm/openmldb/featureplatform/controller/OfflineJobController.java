package com._4paradigm.openmldb.featureplatform.controller;

import com._4paradigm.openmldb.featureplatform.service.OfflineJobService;
import com._4paradigm.openmldb.featureplatform.dao.model.OfflineJobInfo;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping("/{id}/log")
    public String getOfflineJobLog(@PathVariable int id) throws SQLException {
        return offlineJobService.getOfflineJobLog(id);
    }



}