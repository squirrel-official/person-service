package com.squirrel.persons.job;

import com.google.common.util.concurrent.Uninterruptibles;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class MotionHealthCheckJob {
    private static final Logger LOGGER = LogManager.getLogger(com.squirrel.persons.job.MotionHealthCheckJob.class);

    @Scheduled(fixedDelay = 300000)
    public void triggerJob() {
        List<String> list = ProcessHandle.allProcesses()
                .map(p -> Arrays.stream(unwrap(p.info().arguments()))
                        .filter(
                                a -> a.contains("/usr/local/squirrel-ai/service/motionDetection.py"))
                        .collect(Collectors.toList()))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        if (list.size() < 2) {
            try {
                logProcesses(list);
                Process detectionProcess = Runtime.getRuntime().exec("sh /usr/local/person-service/src/main/resources/detection.sh");
                LOGGER.info("detection process started {}", detectionProcess);
                Uninterruptibles.sleepUninterruptibly(2, TimeUnit.MINUTES);
            } catch (Exception e) {
                LOGGER.error("An error happened", e);
            }
        }else if(list.size() > 2){
            logProcesses(list);
        }
    }

    private void logProcesses(List<String> list) {
        LOGGER.warn("Process size : "+list.size());
        for(String each: list) {
            LOGGER.warn("Processes : "+each);
        }
    }

    private  String[] unwrap(Optional<String[]> optional){
        if(optional.isPresent()){
            return optional.get();
        }else {
            return new String[0];
        }
    }
}