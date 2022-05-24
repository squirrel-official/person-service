package com.squirrel.persons.job;


import com.squirrel.persons.service.FileService;
import com.squirrel.persons.util.FilesUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class PurgeJob {

    private static final Logger LOGGER = LogManager.getLogger(SuspectedPersonsJob.class);

    private FileService fileService;

    @Autowired
    public PurgeJob(FileService fileService) {
        this.fileService = fileService;
    }

    @Scheduled(fixedDelay = 600000)
    public void triggerJob() throws IOException {
        LOGGER.info("Triggering captured job");
        fileService.purgeFilesOlderThanNDays("/usr/local/squirrel-ai/archives/", 1);
        FilesUtils.copyAllFiles("/usr/local/squirrel-ai/captured/", "/usr/local/squirrel-ai/archives/captured");

    }

}
