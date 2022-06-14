package com.squirrel.persons.job;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


@Component
public class MotionHealthCheckJob {
    String url = "http://localhost:7777/";
    private static final Logger LOGGER = LogManager.getLogger(com.squirrel.persons.job.MotionHealthCheckJob.class);
    private RestTemplate restTemplate;

    @Autowired
    public MotionHealthCheckJob(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    @Scheduled(fixedDelay = 30000)
    public void triggerJob() {
       if(! isMotionServiceUp()){
           try {
               Runtime.getRuntime().exec("sudo motion restart");
               LOGGER.error("Motion software is being restarted");
           }catch (Exception e){
               LOGGER.error("An error happened",e);
           }
       }
    }

    private boolean isMotionServiceUp() {
        try {
            LOGGER.debug("Motion health check job Start");
            restTemplate.getForObject(url, String.class);
            LOGGER.debug("Motion health check job complete");
            return  Boolean.TRUE;
        }catch (Exception ex){
            LOGGER.error("The Url {} is not available, scheduling restart of system", url);
            return  Boolean.FALSE;

        }
    }
}