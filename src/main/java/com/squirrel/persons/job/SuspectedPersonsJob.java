package com.squirrel.persons.job;

import com.itextpdf.text.DocumentException;
import com.squirrel.persons.service.EmailService;
import com.squirrel.persons.util.FilesUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.io.IOException;

@Component
public class SuspectedPersonsJob {
    public final EmailService emailService;

    @Value("${mail.recipient}")
    private String toEmailAddress;
    @Autowired
    public SuspectedPersonsJob(EmailService emailService) {
        this.emailService = emailService;
    }

    @Scheduled(fixedDelay = 300000)
    public void triggerJob() throws MessagingException, DocumentException, IOException {
        emailService.attachImagesAndSendEmail(toEmailAddress, "/usr/local/squirrel-ai/captured/");
        FilesUtils.copyAllFiles("/usr/local/squirrel-ai/captured/", "/usr/local/squirrel-ai/archives/captured");

    }

}
