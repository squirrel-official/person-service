package com.squirrel.persons.job;

import com.itextpdf.text.DocumentException;
import com.squirrel.persons.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.io.IOException;

@Component
public class SuspectedPersonsJob {
    public final EmailService emailService;

    @Autowired
    public SuspectedPersonsJob(EmailService emailService) {
        this.emailService = emailService;
    }

    @Scheduled(fixedDelay = 1000000)
    public void triggerJob() throws MessagingException, DocumentException, IOException {
        emailService.attachImagesAndSendEmail("anil.kumar.ait09@gmail.com", "/usr/local/squirrel-ai/captured/");
    }

}
