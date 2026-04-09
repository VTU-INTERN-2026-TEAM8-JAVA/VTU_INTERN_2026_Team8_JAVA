package com.wealthwise.service;

import com.wealthwise.model.Notification;
import com.wealthwise.model.NotificationPreference;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class NotificationScheduler {

    private final NotificationService notificationService;
    private final NotificationPreferenceService preferenceService;

    public NotificationScheduler(
            NotificationService notificationService,
            NotificationPreferenceService preferenceService) {

        this.notificationService = notificationService;
        this.preferenceService = preferenceService;
    }

    @Scheduled(fixedRate = 60000)
    public void sendNotifications() {
        System.out.println("Notification scheduler triggered");

        generateDailyNotification();
    }
    public void generateDailyNotification() {

        Long userId = 1L;

        NotificationPreference preference =
                preferenceService.getPreferences(userId);

        if (preference != null && preference.isSipEnabled()) {

            Notification notification =
                    new Notification(
                            userId,
                            "SIP reminder: Your SIP is due soon",
                            "SIP_REMINDER",
                            "HIGH"
                    );

            notificationService.createNotification(notification);
        }
    }
}