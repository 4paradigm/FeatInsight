package com._4paradigm.openmldb.featureplatform.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Catch the SQLException and set actual error message in body.
     *
     * @param e
     * @return
     */
    @ExceptionHandler(SQLException.class)
    public ResponseEntity<String> handleInternalServerErrorException(SQLException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }

}
