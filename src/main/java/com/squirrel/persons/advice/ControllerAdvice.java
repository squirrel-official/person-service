package com.squirrel.persons.advice;

import com.squirrel.persons.controller.NotificationsController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvice {

    private static final Logger logger = LogManager.getLogger(NotificationsController.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity handleNotFoundException(Exception ex) {
        logger.error("Error while processing ", ex);
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
