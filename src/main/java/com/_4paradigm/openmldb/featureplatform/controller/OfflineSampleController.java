package com._4paradigm.openmldb.featureplatform.controller;

import com._4paradigm.openmldb.featureplatform.dao.model.OfflineSample;
import com._4paradigm.openmldb.featureplatform.service.OfflineSampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.sql.SQLException;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/offlinesamples")
public class OfflineSampleController {

    private final OfflineSampleService offlineSampleService;

    @Autowired
    public OfflineSampleController(OfflineSampleService offlineSampleService) {
        this.offlineSampleService = offlineSampleService;
    }

    @GetMapping
    public List<OfflineSample> getOfflineSamples() throws SQLException {
        return offlineSampleService.getOfflineSamples();
    }


    @GetMapping("/{id}")
    public OfflineSample getOfflineSample(@PathVariable int id) throws SQLException {
        return offlineSampleService.getOfflineSample(id);
    }

    @PostMapping
    public OfflineSample createOfflineSample(@RequestBody OfflineSample offlineSample) throws SQLException {
        return offlineSampleService.createOfflineSample(offlineSample);
    }

    @DeleteMapping("/{id}")
    public void deleteOfflineSample(@PathVariable int id) throws SQLException {
        offlineSampleService.deleteOfflineSample(id);
    }

}