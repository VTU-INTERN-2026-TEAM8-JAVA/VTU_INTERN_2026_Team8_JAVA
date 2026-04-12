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

    // AUTH MODULE SIMULATION
    @Scheduled(fixedRate = 180000)
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

    // INVESTMENT MODULE SIMULATION
    @Scheduled(fixedRate = 120000)
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