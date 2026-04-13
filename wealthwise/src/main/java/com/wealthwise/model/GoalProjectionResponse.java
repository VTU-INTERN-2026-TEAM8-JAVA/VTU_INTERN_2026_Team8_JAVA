package com.wealthwise.model;

import java.math.BigDecimal;

/**
 * Response for the /projection endpoint.
 */
public class GoalProjectionResponse {

    private Long goalId;
    private String goalName;
    private BigDecimal projectedFutureValue;
    private BigDecimal inflationAdjustedTarget;
    private BigDecimal currentAmount;
    private double yearsToGoal;
    private BigDecimal annualReturnRate;
    private BigDecimal inflationRate;
    private String status;

    public GoalProjectionResponse() {}

    // ─── Getters & Setters ───────────────────────────────────────────────────

    public Long getGoalId() { return goalId; }
    public void setGoalId(Long goalId) { this.goalId = goalId; }

    public String getGoalName() { return goalName; }
    public void setGoalName(String goalName) { this.goalName = goalName; }

    public BigDecimal getProjectedFutureValue() { return projectedFutureValue; }
    public void setProjectedFutureValue(BigDecimal projectedFutureValue) { this.projectedFutureValue = projectedFutureValue; }

    public BigDecimal getInflationAdjustedTarget() { return inflationAdjustedTarget; }
    public void setInflationAdjustedTarget(BigDecimal inflationAdjustedTarget) { this.inflationAdjustedTarget = inflationAdjustedTarget; }

    public BigDecimal getCurrentAmount() { return currentAmount; }
    public void setCurrentAmount(BigDecimal currentAmount) { this.currentAmount = currentAmount; }

    public double getYearsToGoal() { return yearsToGoal; }
    public void setYearsToGoal(double yearsToGoal) { this.yearsToGoal = yearsToGoal; }

    public BigDecimal getAnnualReturnRate() { return annualReturnRate; }
    public void setAnnualReturnRate(BigDecimal annualReturnRate) { this.annualReturnRate = annualReturnRate; }

    public BigDecimal getInflationRate() { return inflationRate; }
    public void setInflationRate(BigDecimal inflationRate) { this.inflationRate = inflationRate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
