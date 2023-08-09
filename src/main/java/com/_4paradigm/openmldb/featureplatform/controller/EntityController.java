package com._4paradigm.openmldb.featureplatform.controller;

import com._4paradigm.openmldb.featureplatform.dao.EntityService;
import com._4paradigm.openmldb.featureplatform.dao.model.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/entities")
public class EntityController {

    private final EntityService entityService;

    @Autowired
    public EntityController(EntityService entityService) {
        this.entityService = entityService;
    }

    @GetMapping
    public List<Entity> getEntities() throws SQLException {
        return entityService.getEntities();
    }

    @GetMapping("/{name}")
    public Entity getEntityByName(@PathVariable String name) throws SQLException {
        return entityService.getEntityByName(name);
    }

    @PostMapping
    public Entity addEntity(@RequestBody Entity entity) throws SQLException {
        return entityService.addEntity(entity);
    }

    @DeleteMapping("/{name}")
    public void deleteEntity(@PathVariable String name) throws SQLException {
        entityService.deleteEntity(name);
    }

}