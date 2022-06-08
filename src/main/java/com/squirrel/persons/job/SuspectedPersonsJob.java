package com.squirrel.persons.job;

import com.itextpdf.text.DocumentException;
import com.squirrel.persons.service.EmailService;
import com.squirrel.persons.util.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.io.IOException;

@Component
public class SuspectedPersonsJob {
    public static final String CAPTURED_CRIMINALS = "/usr/local/squirrel-ai/result/captured-criminals/";
    public static final String ARCHIVES_CAPTURED = "/usr/local/squirrel-ai/data/archives/captured-criminals/";
    public final EmailService emailService;

    @Value("${mail.recipient}")
    private String toEmailAddress;
    @Autowired
    public SuspectedPersonsJob(EmailService emailService) {
        this.emailService = emailService;
    }

    private static final Logger LOGGER = LogManager.getLogger(SuspectedPersonsJob.class);

    @Scheduled(fixedDelay = 240000)
    public void triggerJob() throws MessagingException, DocumentException, IOException {
        LOGGER.info("Triggering captured job");
        emailService.attachImagesAndSendEmail(toEmailAddress, CAPTURED_CRIMINALS,"Suspected Criminal found",
                "Following suspected criminal persons appears to be captured by your camera");
        FileUtils.copyAllFiles(CAPTURED_CRIMINALS, ARCHIVES_CAPTURED);
    }

}
