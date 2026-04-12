package com.wealthwise.repository;

import com.wealthwise.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Get all notifications sorted newest first
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);


    // Get unread notifications
    List<Notification> findByUserIdAndIsReadFalse(Long userId);


    // Count unread notifications
    long countByUserIdAndIsReadFalse(Long userId);


    // Filter by priority
    List<Notification> findByUserIdAndPriorityIgnoreCase(
            Long userId,
            String priority
    );


    // Filter by type
    List<Notification> findByUserIdAndTypeIgnoreCase(
            Long userId,
            String type
    );


    // Latest 5 notifications (dropdown bell UI)
    List<Notification> findTop5ByUserIdOrderByCreatedAtDesc(Long userId);

}