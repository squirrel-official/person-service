package com.squirrel.persons.controller;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController("/ai")
@OpenAPIDefinition(info = @Info(
        title = "Switch Modes",
        version = "1.0"
))
public class IntelligentSwitchController {

    private static final Logger LOGGER = LogManager.getLogger(IntelligentSwitchController.class);

    @PostMapping("/intelligent-detection")
    public boolean switchToAdvancedMode() throws IOException {
        Process process = Runtime.getRuntime().exec("sh /usr/local/person-service/src/main/resources/detection.sh");
        LOGGER.debug("detection process started {}", process);
        return process.isAlive();
    }
}
