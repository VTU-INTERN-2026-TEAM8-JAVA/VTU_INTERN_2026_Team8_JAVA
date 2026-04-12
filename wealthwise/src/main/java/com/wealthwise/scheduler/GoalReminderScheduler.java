package com.wealthwise.scheduler;

import com.wealthwise.model.Notification;
import com.wealthwise.repository.NotificationRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class GoalReminderScheduler {

    private final NotificationRepository repository;

    public GoalReminderScheduler(NotificationRepository repository) {
        this.repository = repository;
    }

    /*
     DEMO MODE
     Runs every 2 minutes automatically
    */

    @Scheduled(fixedRate = 120000)

    public void sendGoalDeadlineReminder() {

        Notification notification = new Notification(
                1L,
                "Reminder: Your goal deadline is approaching.",
                "GOAL_REMINDER",
                "HIGH"
        );

        repository.save(notification);

        System.out.println(
                "Goal deadline reminder triggered automatically at "
                        + LocalDateTime.now()
        );
    }
}