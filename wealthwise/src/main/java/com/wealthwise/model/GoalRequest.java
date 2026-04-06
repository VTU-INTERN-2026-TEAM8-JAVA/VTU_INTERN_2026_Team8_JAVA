package com.wealthwise.model;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for creating or updating a Goal.
 */
public class GoalRequest {

    private Long userId;

    @NotBlank(message = "Goal name must not be blank")
    @Size(max = 150)
    private String name;

    private String category;

    @NotNull(message = "Target amount is required")
    @DecimalMin(value = "0.01")
    private BigDecimal targetAmount;

    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal currentAmount = BigDecimal.ZERO;

    @NotNull(message = "Target date is required")
    private LocalDate targetDate;

    @DecimalMin(value = "1.0")
    @DecimalMax(value = "50.0")
    private BigDecimal expectedReturnRate = new BigDecimal("12.00");

    @DecimalMin(value = "0.0", inclusive = true)
    @DecimalMax(value = "20.0")
    private BigDecimal inflationRate = new BigDecimal("6.00");

    private String notes;

    // ─── Getters & Setters ───────────────────────────────────────────────────

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

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
