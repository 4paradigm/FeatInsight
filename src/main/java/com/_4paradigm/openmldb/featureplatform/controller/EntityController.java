package com._4paradigm.openmldb.featureplatform.controller;

import com._4paradigm.openmldb.featureplatform.dao.EntityService;
import com._4paradigm.openmldb.featureplatform.dao.model.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    public List<Entity> getEntities() {
        return entityService.getEntities();
    }

    @GetMapping("/{name}")
    public Entity getEntityByName(@PathVariable String name) {
        return entityService.getEntityByName(name);
    }

    @PostMapping
    public Entity addEntity(@RequestBody Entity entity) {
        if(entityService.addEntity(entity)) {
            return entity;
        } else {
            return null;
        }
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<String> deleteEntity(@PathVariable String name) {
        if (entityService.deleteEntity(name)) {
            return new ResponseEntity<>("Success to delete", HttpStatus.OK);
        } else {
            // TODO: Handle for different error code
            return new ResponseEntity<>("Success to delete", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}