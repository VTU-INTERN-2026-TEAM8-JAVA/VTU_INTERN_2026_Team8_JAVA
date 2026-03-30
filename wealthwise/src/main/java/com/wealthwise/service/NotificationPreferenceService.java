package com.wealthwise.service;

import com.wealthwise.model.NotificationPreference;
import com.wealthwise.repository.NotificationPreferenceRepository;
import org.springframework.stereotype.Service;

@Service
public class NotificationPreferenceService {

    private final NotificationPreferenceRepository repository;

    public NotificationPreferenceService(
            NotificationPreferenceRepository repository) {

        this.repository = repository;
    }

    public NotificationPreference savePreferences(
            NotificationPreference preferences) {

        return repository.save(preferences);
    }

    public NotificationPreference getPreferences(Long userId) {

        return repository.findById(userId).orElse(null);
    }
}