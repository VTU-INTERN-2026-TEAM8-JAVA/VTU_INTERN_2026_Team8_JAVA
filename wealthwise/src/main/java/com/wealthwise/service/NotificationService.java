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

    public Notification createNotification(Notification notification) {
        return repository.save(notification);
    }

    public List<Notification> getUserNotifications(Long userId) {
        return repository.findByUserId(userId);
    }

    public long getUnreadCount(Long userId) {
        return repository.countByUserIdAndIsReadFalse(userId);
    }

    public Notification markAsRead(Long id) {

        Notification notification =
                repository.findById(id).orElseThrow();

        notification.setRead(true);

        return repository.save(notification);
    }
}