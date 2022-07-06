package com.squirrel.persons.controller;

import com.squirrel.persons.service.NotificationService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import static com.squirrel.persons.Constant.*;

@RestController("/notification")
@OpenAPIDefinition(info = @Info(
        title = "Notifications controller",
        version = "1.0"
))
public class NotificationsController {

    private static final Logger LOGGER = LogManager.getLogger(NotificationsController.class);

    private static final int expirationMinutes = 2;

    private static boolean suspendedNotifications = false;

    private DateTime lastNotificationTime;

    public final NotificationService notificationService;

    @Autowired
    public NotificationsController(NotificationService notificationService) {
        this.notificationService = notificationService;
        this.lastNotificationTime = DateTime.now();
    }

    @PostMapping("/pause")
    public void pauseAllNotifications(@RequestParam("duration") int duration) {
        LOGGER.info("Pausing notifications for {} minutes", duration);
        lastNotificationTime = DateTime.now().plusMinutes(duration);
        suspendedNotifications = true;
        notificationService.notification("Notifications Suspended", String.format("Notification system is inactive for next %s minutes", duration));
    }

    @PostMapping("/resume-now")
    public void resumeNotification() {
        LOGGER.info("resuming all notifications ");
        lastNotificationTime = DateTime.now().minusMinutes(expirationMinutes);
        suspendedNotifications = false;
        notificationService.notification("Notifications", "Notification system is active now");
    }

    @PostMapping("/notification")
    public void sendNotification(@RequestParam("camera-id") String cameraId) {
        DateTime now = DateTime.now();
        if (isCoolDownExpired(now) && !suspendedNotifications) {
            lastNotificationTime = now;
            String cameraName = cameraId != null ? cameraId : "General Camera";
            LOGGER.info("received notification from camera : {}", cameraName);
            String subjectMessage = String.format("A notification received from %s", cameraName);
            String emailMessage = "you can access the camera feed using link http://my-security.local:7777" +
                    ". If there is any human activity then you will be getting images shortly.";
            try {
                notificationService.notification(subjectMessage, emailMessage);
            } catch (Exception exception) {
                LOGGER.error("Trigger notifications failed", exception);
            }
        }
    }

    @PostMapping("/visitor")
    public void sendVisitorNotificationWithAttachment() {
        LOGGER.info("received visitor notification ");
        String subjectMessage = "Unknown visitors";
        String emailMessage = "People who were near your property today";
        notificationService.notificationWithAttachments(VISITOR_PATH, subjectMessage, emailMessage, suspendedNotifications);
    }

    @PostMapping("/criminal")
    public void sendCriminalNotificationWithAttachment() {
        LOGGER.info("received criminal notification ");
        String subjectMessage = "Suspected Person found";
        String emailMessage = "Following suspected criminal persons were seen near your house";
        notificationService.notificationWithAttachments(CRIMINALS_PATH, subjectMessage, emailMessage, suspendedNotifications);

    }

    @PostMapping("/friend")
    public void sendFriendNotificationWithAttachment() {
        LOGGER.info("received Friend Notification ");
        String subjectMessage = "Familiar person found";
        String emailMessage = "Attached familiar faces were found near your house";
        notificationService.notificationWithAttachments(FRIENDS_PATH, subjectMessage, emailMessage, suspendedNotifications);
    }

    private boolean isCoolDownExpired(DateTime now) {
        return now.minusMinutes(expirationMinutes).isAfter(lastNotificationTime);
    }
}
