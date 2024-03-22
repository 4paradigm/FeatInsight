package com._4paradigm.openmldb.featureplatform.controller;

import com._4paradigm.openmldb.featureplatform.dao.model.LoginRequest;
import com._4paradigm.openmldb.featureplatform.dao.model.SqlExecutorWrapper;
import com._4paradigm.openmldb.featureplatform.service.LoginService;
import com._4paradigm.openmldb.featureplatform.utils.SqlExecutorPoolManager;
import com._4paradigm.openmldb.sdk.SqlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

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
    public ResponseEntity login(@RequestBody LoginRequest loginRequest) {
        try {
            String uuid = loginService.login(loginRequest.getUsername(), loginRequest.getPassword());
            return ResponseEntity.ok(uuid);
        }
        catch (SqlException e) {
            logger.error(String.format("Try to create sql executor for %s but fail with exception: %s",
                    loginRequest.getUsername(),
                    e.getMessage()));
            return ResponseEntity.status(401).
                    body(String.format("Failed to login with username: %s", loginRequest.getUsername()));
        }
    }


    @GetMapping("/test")
    public ResponseEntity test() {
        return ResponseEntity.ok(loginService.test());
    }


    @GetMapping("/logout")
    public ResponseEntity logout() {
        if(SqlExecutorPoolManager.getInstance().closeSqlExecutor(SqlExecutorWrapper.getUuid())) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(500).body("Failed to close sql executor");
    }

}