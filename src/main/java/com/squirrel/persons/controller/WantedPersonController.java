package com.squirrel.persons.controller;

import com.squirrel.persons.service.WantedPersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController("/wanted")
public class WantedPersonController {

    private WantedPersonService wantedPersonService;

    @Autowired
    public WantedPersonController(WantedPersonService wantedPersonService) {
        this.wantedPersonService = wantedPersonService;
    }

    @PostMapping("/refresh")
    public void refreshWantedPersons() throws IOException {
        wantedPersonService.refreshWantedPersons();
    }


    @PostMapping("/switch-to-video")
    public boolean switchToVideo() throws IOException {
        ProcessHandle
                .allProcesses()
                .filter(p -> p.info().commandLine().map(c -> c.contains("detection.sh")).orElse(false))
                .findFirst()
                .ifPresent(ProcessHandle::destroy);


        Process process = Runtime.getRuntime().exec("sh /usr/local/person-service/src/main/resources/motion-start.sh");
        return process.isAlive();
    }

    @PostMapping("/switch-to-detection")
    public boolean switchToAdvancedMode() throws IOException {
        ProcessHandle
                .allProcesses()
                .filter(p -> p.info().commandLine().map(c -> c.contains("motion-start.sh")).orElse(false))
                .findFirst()
                .ifPresent(ProcessHandle::destroy);

        Process process = Runtime.getRuntime().exec("sh /usr/local/person-service/src/main/resources/detection.sh");
        return process.isAlive();
    }
}
