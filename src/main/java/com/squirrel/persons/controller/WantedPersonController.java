package com.squirrel.persons.controller;

import com.squirrel.persons.service.WantedPersonService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController("/wanted")
@OpenAPIDefinition(info = @Info(
        title = "Update Wanted Persons",
        version = "1.0"
))
public class WantedPersonController {

    private WantedPersonService wantedPersonService;
    private static final Logger LOGGER = LogManager.getLogger(WantedPersonController.class);

    @Autowired
    public WantedPersonController(WantedPersonService wantedPersonService) {
        this.wantedPersonService = wantedPersonService;
    }

    @PostMapping("/update-from-smart-grid")
    public void refreshWantedPersons() throws IOException {
        LOGGER.info("refresh process started");
        wantedPersonService.refreshWantedPersons();
    }



}
