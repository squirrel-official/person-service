package com.squirrel.persons.job;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.HttpURLConnection;
import java.net.URL;

@Component
public class MotionHealthCheckJob {
    String preUrl = "http://my-security.local:7776/";
    String motionRestartCommand = "sh /usr/local/person-service/src/main/resources/motion-restart.sh";
    private static final Logger LOGGER = LogManager.getLogger(com.squirrel.persons.job.MotionHealthCheckJob.class);

    @Scheduled(fixedDelay = 30000)
    public void triggerJob() {
        if (!isMotionServiceUp()) {
            try {
                LOGGER.info("Motion software is being restarted");
                Runtime.getRuntime().exec(motionRestartCommand);
                Process process = Runtime.getRuntime().exec(motionRestartCommand);
                LOGGER.info("Motion software restarted {}", process);
            } catch (Exception e) {
                LOGGER.error("An error happened", e);
            }
        }
    }

    private boolean isMotionServiceUp() {
        LOGGER.debug("Motion health check job Start");
        for (int i = 1; i < 4; i++) {
            String url = preUrl + i + "/stream/";
            if (!isServerReachable(url)) {
                return false;
            }
        }
        return true;
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