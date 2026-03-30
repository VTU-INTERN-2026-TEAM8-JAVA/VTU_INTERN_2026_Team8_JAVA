package com.wealthwise.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "notification_preferences")
public class NotificationPreference {

    @Id
    private Long userId;

    private boolean sipEnabled = true;

    private boolean goalEnabled = true;

    private boolean portfolioEnabled = true;

    private boolean emailEnabled = false;

    public NotificationPreference() {}

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public boolean isSipEnabled() {
        return sipEnabled;
    }

    public void setSipEnabled(boolean sipEnabled) {
        this.sipEnabled = sipEnabled;
    }

    public boolean isGoalEnabled() {
        return goalEnabled;
    }

    public void setGoalEnabled(boolean goalEnabled) {
        this.goalEnabled = goalEnabled;
    }

    public boolean isPortfolioEnabled() {
        return portfolioEnabled;
    }

    public void setPortfolioEnabled(boolean portfolioEnabled) {
        this.portfolioEnabled = portfolioEnabled;
    }

    public boolean isEmailEnabled() {
        return emailEnabled;
    }

    public void setEmailEnabled(boolean emailEnabled) {
        this.emailEnabled = emailEnabled;
    }
}