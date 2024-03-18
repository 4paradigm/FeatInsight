package com._4paradigm.openmldb.featureplatform.controller;

import com._4paradigm.openmldb.featureplatform.dao.model.LoginRequest;
import com._4paradigm.openmldb.featureplatform.dao.model.SqlExecutorWrapper;
import com._4paradigm.openmldb.featureplatform.service.LoginService;
import com._4paradigm.openmldb.featureplatform.utils.DatabaseConnectionUtil;
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
    public ResponseEntity login(@RequestBody LoginRequest loginRequest) throws SQLException {
        String uuid = loginService.login(loginRequest.getUsername(), loginRequest.getPassword());
        if (uuid != null) {
            return ResponseEntity.ok(uuid);
        } else {
            return ResponseEntity.status(401).body("Invalid username or password");
        }
    }

    @Autowired
    private SqlExecutorWrapper sqlExecutorWrapper;

    @GetMapping("/test")
    public ResponseEntity test() {
        return ResponseEntity.ok(sqlExecutorWrapper.getSqlExecutor().toString());
    }


    @GetMapping("/logout")
    public ResponseEntity logout() {
        if(DatabaseConnectionUtil.closeSqlExecutor(sqlExecutorWrapper.getUuid())) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(500).body("Failed to close sql executor");
    }

}