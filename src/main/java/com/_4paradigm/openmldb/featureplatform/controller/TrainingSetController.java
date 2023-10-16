package com._4paradigm.openmldb.featureplatform.controller;

import com._4paradigm.openmldb.featureplatform.dao.TrainingSetService;
import com._4paradigm.openmldb.featureplatform.dao.model.TrainingSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/trainingsets")
public class TrainingSetController {

    private final TrainingSetService trainingSetService;

    @Autowired
    public TrainingSetController(TrainingSetService trainingSetService) {
        this.trainingSetService = trainingSetService;
    }

    @GetMapping
    public List<TrainingSet> getTrainingSets() throws SQLException {
        return trainingSetService.getTrainingSets();
    }


    @GetMapping("/{id}")
    public TrainingSet getTrainingSet(@PathVariable int id) throws SQLException {
        return trainingSetService.getTrainingSet(id);
    }

    @PostMapping
    public TrainingSet createFeatureService(@RequestBody TrainingSet trainingSet) throws SQLException {
        return trainingSetService.createTrainingSet(trainingSet);
    }

}