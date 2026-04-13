package com.wealthwise.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents a financial goal created by a user.
 * e.g. Retirement, Education, House purchase, etc.
 */
@Entity
@Table(name = "goals", indexes = {
        @Index(name = "idx_goals_user_id", columnList = "user_id")
})
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long goalId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotBlank(message = "Goal name must not be blank")
    @Size(max = 150, message = "Goal name must not exceed 150 characters")
    @Column(nullable = false, length = 150)
    private String name;

    /**
     * Category: RETIREMENT, EDUCATION, HOUSE, VEHICLE, TRAVEL,
     *           EMERGENCY_FUND, MARRIAGE, BUSINESS, OTHER
     */
    @Column(length = 50)
    private String category;

    @NotNull(message = "Target amount is required")
    @DecimalMin(value = "0.01", message = "Target amount must be greater than 0")
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal targetAmount;

    @DecimalMin(value = "0.0", inclusive = true, message = "Current amount cannot be negative")
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal currentAmount = BigDecimal.ZERO;

    @NotNull(message = "Target date is required")
    @Column(nullable = false)
    private LocalDate targetDate;

    /**
     * Annual expected return rate in % (e.g. 12.00 for 12%)
     */
    @DecimalMin(value = "1.0", message = "Expected return rate must be at least 1%")
    @DecimalMax(value = "50.0", message = "Expected return rate must not exceed 50%")
    @Column(precision = 5, scale = 2)
    private BigDecimal expectedReturnRate = new BigDecimal("12.00");

    /**
     * Annual inflation rate in % (e.g. 6.00 for 6%)
     */
    @DecimalMin(value = "0.0", inclusive = true, message = "Inflation rate cannot be negative")
    @DecimalMax(value = "20.0", message = "Inflation rate must not exceed 20%")
    @Column(precision = 5, scale = 2)
    private BigDecimal inflationRate = new BigDecimal("6.00");

    /**
     * ACTIVE, ON_TRACK, AT_RISK, COMPLETED
     */
    @Column(length = 20)
    private String status = "ACTIVE";

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // ─── Getters & Setters ───────────────────────────────────────────────────

    public Long getGoalId() { return goalId; }

    public void setGoalId(Long goalId) { this.goalId = goalId; }

    public Long getUserId() { return userId; }

    public void setUserId(Long userId) { this.userId = userId; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }

    public void setCategory(String category) { this.category = category; }

    public BigDecimal getTargetAmount() { return targetAmount; }

    public void setTargetAmount(BigDecimal targetAmount) { this.targetAmount = targetAmount; }

    public BigDecimal getCurrentAmount() { return currentAmount; }

    public void setCurrentAmount(BigDecimal currentAmount) { this.currentAmount = currentAmount; }

    public LocalDate getTargetDate() { return targetDate; }

    public void setTargetDate(LocalDate targetDate) { this.targetDate = targetDate; }

    public BigDecimal getExpectedReturnRate() { return expectedReturnRate; }

    public void setExpectedReturnRate(BigDecimal expectedReturnRate) { this.expectedReturnRate = expectedReturnRate; }

    public BigDecimal getInflationRate() { return inflationRate; }

    public void setInflationRate(BigDecimal inflationRate) { this.inflationRate = inflationRate; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }

    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
