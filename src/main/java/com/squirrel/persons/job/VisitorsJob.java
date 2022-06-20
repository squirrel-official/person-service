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
public class VisitorsJob {
    public static final String VISITOR_PATH = "/usr/local/squirrel-ai/result/unknown-visitors/";
    public static final String VISITOR_ARCHIVE_PATH = "/usr/local/squirrel-ai/data/archives/unknown-visitors/";
    @Value("${mail.recipient}")
    private String toEmailAddress;

    public final EmailService emailService;

    private static final Logger LOGGER = LogManager.getLogger(VisitorsJob.class);

    @Autowired
    public VisitorsJob(EmailService emailService) {
        this.emailService = emailService;
    }

    @Scheduled(fixedDelay = 60000)
    public void triggerJob() throws MessagingException, DocumentException, IOException {
        LOGGER.info("Triggering unknown visitors job");
        emailService.attachImagesAndSendEmail(toEmailAddress, VISITOR_PATH,"Unknown visitors",
                "People who were near your property today");
        FileUtils.copyAllFiles(VISITOR_PATH, VISITOR_ARCHIVE_PATH);
        FileUtils.deleteImages(VISITOR_ARCHIVE_PATH);
    }

}
