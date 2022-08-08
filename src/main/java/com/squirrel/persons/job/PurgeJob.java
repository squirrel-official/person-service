package com.squirrel.persons.job;


import com.squirrel.persons.service.FileService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class PurgeJob {

    private static final Logger LOGGER = LogManager.getLogger(PurgeJob.class);

    private FileService fileService;

    @Autowired
    public PurgeJob(FileService fileService) {
        this.fileService = fileService;
    }

    @Scheduled(fixedDelay = 12000000)
    public void triggerJob() throws IOException {
        LOGGER.info("Purge job start");
        fileService.purgeFilesOlderThanNDays("/usr/local/squirrel-ai/data/archives/captured-criminals", 1);
        fileService.purgeFilesOlderThanNDays("/usr/local/squirrel-ai/data/archives/unknown-visitors", 1);
        fileService.purgeFilesOlderThanNDays("/usr/local/squirrel-ai/data/archives/known-visitors", 1);
        fileService.purgeFilesOlderThanNDays("/var/lib/motion", 10);
        fileService.purgeFilesOlderThanNDays("/usr/local/squirrel-ai/data/archives/", 10);
        LOGGER.info("Purge job complete");
    }

}
