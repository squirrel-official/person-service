package com.squirrel.persons.job;

import com.google.common.util.concurrent.Uninterruptibles;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class MotionHealthCheckJob {
    String preUrl = "http://my-security.local:7776/";
    String motionRestartCommand = "sh /usr/local/person-service/src/main/resources/motion-restart.sh";
    private static final Logger LOGGER = LogManager.getLogger(com.squirrel.persons.job.MotionHealthCheckJob.class);

    @Scheduled(fixedDelay = 180000)
    public void triggerJob() {
        if (!isMotionServiceUp()) {
            try {
                LOGGER.info("Motion software is being restarted");
                Runtime.getRuntime().exec(motionRestartCommand);
                Process process = Runtime.getRuntime().exec(motionRestartCommand);
                LOGGER.info("Motion software restarted {}", process);
                Uninterruptibles.sleepUninterruptibly(1, TimeUnit.MINUTES);
            } catch (Exception e) {
                LOGGER.error("An error happened", e);
            }
        }
        if (isAIServiceDown()) {
            try {
                Process detectionProcess = Runtime.getRuntime().exec("sh /usr/local/person-service/src/main/resources/detection.sh");
                LOGGER.info("detection process started {}", detectionProcess);
                Uninterruptibles.sleepUninterruptibly(1, TimeUnit.MINUTES);
            } catch (Exception e) {
                LOGGER.error("An error happened", e);
            }
        }
    }

    private boolean isMotionServiceUp() {
        for (int i = 1; i < 3; i++) {
            String url = preUrl + i + "/stream/";
            if (!isServerReachable(url)) {
                return false;
            }
        }
        return true;
    }

    private  boolean isAIServiceDown(){

        List<String> list = ProcessHandle.allProcesses()
                .map(p -> Arrays.stream(unwrap(p.info().arguments()))
                        .filter(
                                a -> a.contains("/usr/local/squirrel-ai/service/motionDetection.py"))
                        .collect(Collectors.toList()))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        return  list.isEmpty();
    }
    private  String[] unwrap(Optional<String[]> optional){
        if(optional.isPresent()){
            return optional.get();
        }else {
            return new String[0];
        }
    }

    public boolean isServerReachable(String url) {
        try {
            URL urlServer = new URL(url);
            HttpURLConnection urlConn = (HttpURLConnection) urlServer.openConnection();
            urlConn.setConnectTimeout(5000);
            urlConn.connect();
            if (urlConn.getResponseCode() == 200) {
                return true;
            } else {
                LOGGER.error("The  url {} is not available", url);
            }
        } catch (Exception e) {
            LOGGER.error("The  url {} is not available", url,  e);
        }
        return false;
    }
}