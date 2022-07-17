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

    private DateTime suspendedNotificationsEndTime;

    private DateTime visitorNotificationsEndTime;

    public final NotificationService notificationService;

    @Autowired
    public NotificationsController(NotificationService notificationService) {
        this.notificationService = notificationService;
        this.suspendedNotificationsEndTime = DateTime.now();
    }

    @PostMapping("/pause")
    public void pauseAllNotifications(@RequestParam("duration") int duration, @RequestParam("visitor-notification-duration") int visitorDuration) {
        LOGGER.info("Pausing notifications for {} minutes", duration);
        suspendedNotificationsEndTime = DateTime.now().plusMinutes(duration);
        this.visitorNotificationsEndTime = DateTime.now().plusMinutes(visitorDuration);
        notificationService.notification("Notifications Suspended", String.format("Notification system is inactive for next %s minutes", duration));
    }

    @PostMapping("/resume-now")
    public void resumeNotification() {
        LOGGER.info("resuming all notifications ");
        suspendedNotificationsEndTime = DateTime.now();
        visitorNotificationsEndTime = DateTime.now();
        notificationService.notification("Notifications", "Notification system is active now");
    }

    @PostMapping("/notification")
    public void sendNotification(@RequestParam("camera-id") String cameraId) {

        if (isCoolDownExpired()) {
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
        if (isVisitorCoolDownExpired()) {
            LOGGER.info("received visitor notification ");
            String subjectMessage = "Unknown visitors";
            String emailMessage = "People who were near your property today";
            notificationService.notificationWithAttachments(VISITOR_PATH, subjectMessage, emailMessage);
        } else {
            LOGGER.info("received visitor notification during cool down period {}", suspendedNotificationsEndTime );
            notificationService.archiveImages(VISITOR_PATH);
        }
    }

    @PostMapping("/criminal")
    public void sendCriminalNotificationWithAttachment() {
        LOGGER.info("received criminal notification ");
        String subjectMessage = "Suspected Person found";
        String emailMessage = "Following suspected criminal persons were seen near your house";
        notificationService.notificationWithAttachments(CRIMINALS_PATH, subjectMessage, emailMessage);

    }

    @PostMapping("/friend")
    public void sendFriendNotificationWithAttachment() {
        LOGGER.info("received Friend Notification ");
        String subjectMessage = "Familiar person found";
        String emailMessage = "Attached familiar faces were found near your house";
        notificationService.notificationWithAttachments(FRIENDS_PATH, subjectMessage, emailMessage);
    }

    private boolean isCoolDownExpired() {
        return suspendedNotificationsEndTime.isBefore(DateTime.now());
    }
    private boolean isVisitorCoolDownExpired() {
        return visitorNotificationsEndTime.isBefore(DateTime.now());
    }
}
