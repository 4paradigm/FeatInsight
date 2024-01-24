package com._4paradigm.openmldb.featureplatform.controller;

import com._4paradigm.openmldb.featureplatform.dao.model.LoginRequest;
import com._4paradigm.openmldb.featureplatform.dao.model.OfflineSample;
import com._4paradigm.openmldb.featureplatform.dao.model.SimpleTableInfo;
import com._4paradigm.openmldb.featureplatform.service.DatabaseService;
import com._4paradigm.openmldb.featureplatform.service.LoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/login")
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    private final LoginService loginService;

    @Autowired
    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping
    public ResponseEntity login(@RequestBody LoginRequest loginRequest) throws SQLException {
        try {
            boolean loginResult = loginService.login(loginRequest.getUsername(), loginRequest.getPassword());
            if (loginResult) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(401).body("Invalid username or password");
            }
        } catch (SQLException e) {
            logger.info(String.format("Call login with %s but get exception: %s", loginRequest, e.getMessage()));
            throw e;
        }
    }

}