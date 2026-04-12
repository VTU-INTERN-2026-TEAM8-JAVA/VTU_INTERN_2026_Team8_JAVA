package com.wealthwise.controller;

import com.wealthwise.model.Notification;
import com.wealthwise.service.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    // CREATE NOTIFICATION

    @PostMapping
    public Notification createNotification(
            @RequestBody Notification notification) {

        return service.createNotification(notification);
    }


    // GET ALL USER NOTIFICATIONS

    @GetMapping("/user/{userId}")
    public List<Notification> getNotifications(
            @PathVariable Long userId) {

        return service.getUserNotifications(userId);
    }


    // GET LATEST 5 (Bell dropdown)

    @GetMapping("/latest/{userId}")
    public List<Notification> getLatestFive(
            @PathVariable Long userId) {

        return service.getLatestFiveNotifications(userId);
    }


    // MARK SINGLE READ

    @PutMapping("/read/{id}")
    public Notification markAsRead(
            @PathVariable Long id) {

        return service.markAsRead(id);
    }


    // MARK ALL READ

    @PutMapping("/read-all/{userId}")
    public String markAllAsRead(
            @PathVariable Long userId) {

        service.markAllAsRead(userId);

        return "All notifications marked as read successfully";
    }


    // DELETE NOTIFICATION

    @DeleteMapping("/{id}")
    public String deleteNotification(
            @PathVariable Long id) {

        service.deleteNotification(id);

        return "Notification deleted successfully";
    }


    // UNREAD COUNT (Bell badge)

    @GetMapping("/unread-count/{userId}")
    public long unreadCount(
            @PathVariable Long userId) {

        return service.getUnreadCount(userId);
    }


    // FILTER BY PRIORITY

    @GetMapping("/priority/{userId}/{priority}")
    public List<Notification> getByPriority(
            @PathVariable Long userId,
            @PathVariable String priority) {

        return service.getNotificationsByPriority(
                userId,
                priority
        );
    }


    // FILTER BY TYPE

    @GetMapping("/type/{userId}/{type}")
    public List<Notification> getByType(
            @PathVariable Long userId,
            @PathVariable String type) {

        return service.getNotificationsByType(
                userId,
                type
        );
    }


    // ===============================
    // SUPPORTER TEST ENDPOINTS
    // ===============================


    @PostMapping("/test/goal-created/{userId}")
    public String goalCreatedTest(
            @PathVariable Long userId) {

        service.sendGoalCreatedNotification(userId);

        return "Goal created notification sent";
    }


    @PostMapping("/test/goal-deadline/{userId}")
    public String goalDeadlineTest(
            @PathVariable Long userId) {

        service.sendGoalDeadlineReminder(userId);

        return "Goal deadline reminder sent";
    }


    @PostMapping("/test/goal-achieved/{userId}")
    public String goalAchievedTest(
            @PathVariable Long userId) {

        service.sendGoalAchievedNotification(userId);

        return "Goal achieved notification sent";
    }


    @PostMapping("/test/portfolio/{userId}")
    public String portfolioAlertTest(
            @PathVariable Long userId) {

        service.sendPortfolioChangeNotification(userId);

        return "Portfolio alert notification sent";
    }


    @PostMapping("/test/sip-created/{userId}")
    public String sipCreatedTest(
            @PathVariable Long userId) {

        service.sendSipCreatedNotification(userId);

        return "SIP created notification sent";
    }

}