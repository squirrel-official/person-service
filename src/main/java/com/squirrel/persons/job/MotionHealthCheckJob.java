package com.squirrel.persons.job;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.*;

@Component
public class MotionHealthCheckJob {
    String preUrl = "http://my-security.local:7776/";
    String motionRestartCommand = "/usr/local/person-service/src/main/resources/motion-restart.sh";
    private static final Logger LOGGER = LogManager.getLogger(com.squirrel.persons.job.MotionHealthCheckJob.class);
    private RestTemplate restTemplate;

    @Autowired
    public MotionHealthCheckJob(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Scheduled(fixedDelay = 30000)
    public void triggerJob() {
        if (!isMotionServiceUp()) {
            try {
                Runtime.getRuntime().exec(motionRestartCommand);
                LOGGER.error("Motion software is being restarted");
            } catch (Exception e) {
                LOGGER.error("An error happened", e);
            }
        }
    }

    private boolean isMotionServiceUp() {
        LOGGER.debug("Motion health check job Start");
        boolean result = true;
        for (int i = 1; i < 4; i++) {
            try {
                String url = preUrl + i + "/stream/";
                boolean reachable = isServerReachable(url);
                LOGGER.debug(reachable);
            } catch (Exception ex) {
                LOGGER.error("The stream id {} is not available, scheduling restart of motion system", i, ex);
                return false;
            }
        }
        return result;
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
                return false;
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return false;
    }
}