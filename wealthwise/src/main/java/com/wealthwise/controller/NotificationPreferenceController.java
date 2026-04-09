package com.wealthwise.controller;

import com.wealthwise.model.NotificationPreference;
import com.wealthwise.service.NotificationPreferenceService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/preferences")
public class NotificationPreferenceController {

    private final NotificationPreferenceService service;

    public NotificationPreferenceController(
            NotificationPreferenceService service) {

        this.service = service;
    }

    @PostMapping
    public NotificationPreference savePreferences(
            @RequestBody NotificationPreference preferences) {

        return service.savePreferences(preferences);
    }

    @GetMapping("/{userId}")
    public NotificationPreference getPreferences(
            @PathVariable Long userId) {

        return service.getPreferences(userId);
    }
}