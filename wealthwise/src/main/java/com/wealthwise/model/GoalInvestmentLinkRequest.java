package com.wealthwise.model;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * DTO for linking an investment to a goal.
 */
public class GoalInvestmentLinkRequest {

    private Long goalId;

    @NotBlank(message = "Investment type must not be blank")
    private String investmentType;

    @NotNull(message = "Monthly amount is required")
    @DecimalMin(value = "0.01", message = "Monthly amount must be greater than 0")
    private BigDecimal monthlyAmount;

    @Size(max = 255)
    private String description;

    // ─── Getters & Setters ───────────────────────────────────────────────────

    public Long getGoalId() { return goalId; }
    public void setGoalId(Long goalId) { this.goalId = goalId; }

    public String getInvestmentType() { return investmentType; }
    public void setInvestmentType(String investmentType) { this.investmentType = investmentType; }

    public BigDecimal getMonthlyAmount() { return monthlyAmount; }
    public void setMonthlyAmount(BigDecimal monthlyAmount) { this.monthlyAmount = monthlyAmount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
