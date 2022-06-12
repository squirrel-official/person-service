package com.squirrel.persons.job;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class MotionHealthCheckJob {

    String url = "http://localhost::7777";
    private static final Logger LOGGER = LogManager.getLogger(MotionHealthCheckJob.class);
    private RestTemplate restTemplate;

    @Autowired
    public MotionHealthCheckJob(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    @Scheduled(fixedDelay = 300000)
    public void triggerJob() {
        LOGGER.info("Motion health check job Start");
        String result = restTemplate.getForObject(url, String.class);
        LOGGER.info("Motion health check job End {}",result);
    }

}
