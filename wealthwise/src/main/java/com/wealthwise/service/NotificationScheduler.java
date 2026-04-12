package com.wealthwise.service;

import com.wealthwise.model.Notification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class NotificationScheduler {

    private final NotificationService notificationService;

    public NotificationScheduler(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // AUTH MODULE DAILY REMINDER (8 AM)
    @Scheduled(cron = "0 0 8 * * ?")
    public void welcomeNotificationDemo() {

        notificationService.createNotification(
                new Notification(
                        1L,
                        "Welcome to WealthWise! Your account has been created successfully.",
                        "SYSTEM_ALERT",
                        "LOW"
                )
        );

    }


    // SIP MODULE DAILY REMINDER (8 AM)
    @Scheduled(cron = "0 0 8 * * ?")
    public void sipReminderNotificationDemo() {

        notificationService.createNotification(
                new Notification(
                        1L,
                        "Reminder: Your SIP investment is due soon.",
                        "SIP_REMINDER",
                        "HIGH"
                )
        );

    }
}