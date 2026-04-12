package com.wealthwise.service;

import com.wealthwise.model.Notification;
import com.wealthwise.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository repository;

    public NotificationService(NotificationRepository repository) {
        this.repository = repository;
    }

    // CREATE NOTIFICATION

    public Notification createNotification(Notification notification) {
        return repository.save(notification);
    }

    // GET ALL USER NOTIFICATIONS

    public List<Notification> getUserNotifications(Long userId) {
        return repository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    // GET LATEST 5 NOTIFICATIONS (Bell dropdown support)

    public List<Notification> getLatestFiveNotifications(Long userId) {
        return repository.findTop5ByUserIdOrderByCreatedAtDesc(userId);
    }

    // MARK SINGLE NOTIFICATION READ

    public Notification markAsRead(Long id) {

        Notification notification = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        notification.setRead(true);

        return repository.save(notification);
    }

    // MARK ALL NOTIFICATIONS READ

    public void markAllAsRead(Long userId) {

        List<Notification> notifications =
                repository.findByUserIdAndIsReadFalse(userId);

        for (Notification n : notifications) {
            n.setRead(true);
        }

        repository.saveAll(notifications);
    }

    // DELETE NOTIFICATION

    public void deleteNotification(Long id) {
        repository.deleteById(id);
    }

    // GET UNREAD COUNT (Bell badge support)

    public long getUnreadCount(Long userId) {
        return repository.countByUserIdAndIsReadFalse(userId);
    }

    // FILTER BY PRIORITY

    public List<Notification> getNotificationsByPriority(
            Long userId,
            String priority
    ) {

        return repository.findByUserIdAndPriorityIgnoreCase(
                userId,
                priority
        );
    }

    // FILTER BY TYPE

    public List<Notification> getNotificationsByType(
            Long userId,
            String type
    ) {

        return repository.findByUserIdAndTypeIgnoreCase(
                userId,
                type
        );
    }


    // ===============================
    // SUPPORTER MODULE INTEGRATIONS
    // ===============================


    // GOAL CREATED

    public void sendGoalCreatedNotification(Long userId) {

        Notification notification = new Notification(
                userId,
                "Your financial goal has been created successfully.",
                "GOAL_UPDATE",
                "MEDIUM"
        );

        repository.save(notification);
    }


    // GOAL DEADLINE REMINDER

    public void sendGoalDeadlineReminder(Long userId) {

        Notification notification = new Notification(
                userId,
                "Reminder: Your goal deadline is approaching.",
                "GOAL_REMINDER",
                "HIGH"
        );

        repository.save(notification);
    }


    // GOAL ACHIEVED

    public void sendGoalAchievedNotification(Long userId) {

        Notification notification = new Notification(
                userId,
                "Congratulations! You achieved your goal.",
                "GOAL_COMPLETED",
                "HIGH"
        );

        repository.save(notification);
    }


    // PORTFOLIO CHANGE ALERT

    public void sendPortfolioChangeNotification(Long userId) {

        Notification notification = new Notification(
                userId,
                "Your portfolio value has changed.",
                "PORTFOLIO_ALERT",
                "MEDIUM"
        );

        repository.save(notification);
    }


    // SIP CREATED ALERT

    public void sendSipCreatedNotification(Long userId) {

        Notification notification = new Notification(
                userId,
                "Your SIP investment has been created successfully.",
                "SIP_CREATED",
                "MEDIUM"
        );

        repository.save(notification);
    }

}