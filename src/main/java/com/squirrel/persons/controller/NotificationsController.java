package com.squirrel.persons.controller;

import com.squirrel.persons.service.EmailService;
import com.squirrel.persons.util.FileUtils;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.squirrel.persons.Constant.*;

@RestController("/notification")
@OpenAPIDefinition(info = @Info(
        title = "Notifications controllerr",
        version = "1.0"
))
public class NotificationsController {

    private static final Logger LOGGER = LogManager.getLogger(NotificationsController.class);

    @Value("${mail.recipient}")
    private String toEmailAddress;

    public final EmailService emailService;

    @Autowired
    public NotificationsController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/notification")
    public void sendNotification(@RequestBody String cameraName) {
        String subjectMessage = String.format("A notification received from %s", cameraName);
        String emailMessage = "you can access the camera feed using link http://my-security.local:7777" +
                " If there is any human activity then you will be getting images shortly.";
        try {
            emailService.triggerNotification(toEmailAddress, subjectMessage, emailMessage);
        } catch (Exception exception) {
            LOGGER.error("Trigger notifications failed", exception);
        }
    }

    @PostMapping("/visitor")
    public void sendVisitorNotificationWithAttachment() {
        LOGGER.debug("Visitor Notification ");
        try {
            String subjectMessage = "Unknown visitors";
            String emailMessage = "People who were near your property today";
            if (emailService.attachImagesAndSendEmail(toEmailAddress, VISITOR_PATH, subjectMessage, emailMessage)) {
                FileUtils.copyAllFiles(VISITOR_PATH, VISITOR_ARCHIVE_PATH);
//                FileUtils.deleteImages(VISITOR_ARCHIVE_PATH);
            }
        } catch (Exception exception) {
            LOGGER.error("Trigger notifications failed", exception);
        }
    }

    @PostMapping("/criminal")
    public void sendCriminalNotificationWithAttachment() {
        LOGGER.debug("Criminal Notification ");
        try {
            String subjectMessage = "Suspected Person found";
            String emailMessage = "Following suspected criminal persons were seen near your house";
            if (emailService.attachImagesAndSendEmail(toEmailAddress, CAPTURED_CRIMINALS, subjectMessage, emailMessage)) {
                FileUtils.copyAllFiles(CAPTURED_CRIMINALS, ARCHIVES_CAPTURED);
            }
        } catch (Exception exception) {
            LOGGER.error("Trigger notifications failed", exception);
        }
    }

    @PostMapping("/friend")
    public void sendFriendNotificationWithAttachment() {
        LOGGER.debug("Friend Notification ");
        String subjectMessage = "Familiar person found";
        String emailMessage = "Attached familiar faces were found near your house";
        try {
            if (emailService.attachImagesAndSendEmail(toEmailAddress, VISITOR_PATH, subjectMessage, emailMessage)) {
                FileUtils.copyAllFiles(VISITOR_PATH, VISITOR_ARCHIVE_PATH);
            }
        } catch (Exception exception) {
            LOGGER.error("Trigger notifications failed", exception);
        }
    }
}
