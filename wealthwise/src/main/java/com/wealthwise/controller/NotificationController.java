package com.wealthwise.controller;

import com.wealthwise.model.Notification;
import com.wealthwise.service.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    @PostMapping
    public Notification createNotification(
            @RequestBody Notification notification) {

        return service.createNotification(notification);
    }

    @GetMapping("/user/{userId}")
    public List<Notification> getNotifications(
            @PathVariable Long userId) {

        return service.getUserNotifications(userId);
    }

    @PutMapping("/read/{id}")
    public Notification markAsRead(@PathVariable Long id) {

        return service.markAsRead(id);
    }

    @GetMapping("/unread-count/{userId}")
    public long unreadCount(@PathVariable Long userId) {

        return service.getUnreadCount(userId);
    }
}