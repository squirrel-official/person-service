package com.squirrel.persons.controller;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController("/ai")
@OpenAPIDefinition(info = @Info(
        title = "Switch Modes",
        version = "1.0"
))
public class IntelligentSwitchController {

    @PostMapping("/switch-to-video")
    public boolean switchToVideo() throws IOException {
        ProcessHandle
                .allProcesses()
                .filter(p -> p.info().commandLine().map(c -> c.contains("detection.sh") ||
                        c.contains("motionDetection.py")).orElse(false))
                .forEach(processHandle -> processHandle.destroy());

        Process process = Runtime.getRuntime().exec("sh /usr/local/person-service/src/main/resources/motion-start.sh");
        return process.isAlive();
    }

    @PostMapping("/switch-to-detection")
    public boolean switchToAdvancedMode() throws IOException {
        ProcessHandle
                .allProcesses()
                .filter(p -> p.info().commandLine().map(c -> c.contains("motion-start.sh") ||
                        c.contains("sudo -u pi motion start") || c.contains("motion start")).orElse(false))
                .forEach(processHandle -> processHandle.destroy());

        Process process = Runtime.getRuntime().exec("sh /usr/local/person-service/src/main/resources/detection.sh");
        return process.isAlive();
    }
}
