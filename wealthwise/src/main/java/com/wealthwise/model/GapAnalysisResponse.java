package com.wealthwise.model;

import java.math.BigDecimal;

/**
 * Response for the /gap-analysis endpoint.
 * Tells the user how much more monthly SIP they need to reach their goal.
 */
public class GapAnalysisResponse {

    private Long goalId;
    private String goalName;
    private BigDecimal targetAmount;
    private BigDecimal inflationAdjustedTarget;
    private BigDecimal projectedFutureValue;
    private BigDecimal gap;                        // inflationAdjustedTarget - projectedFutureValue (≥0 = shortfall)
    private BigDecimal currentMonthlyContribution; // sum of linked investments
    private BigDecimal requiredMonthlySIP;         // SIP needed to close the gap
    private BigDecimal additionalSIPNeeded;        // requiredMonthlySIP - currentMonthlyContribution
    private String status;

    public GapAnalysisResponse() {}

    // ─── Getters & Setters ───────────────────────────────────────────────────

    public Long getGoalId() { return goalId; }
    public void setGoalId(Long goalId) { this.goalId = goalId; }

    public String getGoalName() { return goalName; }
    public void setGoalName(String goalName) { this.goalName = goalName; }

    public BigDecimal getTargetAmount() { return targetAmount; }
    public void setTargetAmount(BigDecimal targetAmount) { this.targetAmount = targetAmount; }

    public BigDecimal getInflationAdjustedTarget() { return inflationAdjustedTarget; }
    public void setInflationAdjustedTarget(BigDecimal inflationAdjustedTarget) { this.inflationAdjustedTarget = inflationAdjustedTarget; }

    public BigDecimal getProjectedFutureValue() { return projectedFutureValue; }
    public void setProjectedFutureValue(BigDecimal projectedFutureValue) { this.projectedFutureValue = projectedFutureValue; }

    public BigDecimal getGap() { return gap; }
    public void setGap(BigDecimal gap) { this.gap = gap; }

    public BigDecimal getCurrentMonthlyContribution() { return currentMonthlyContribution; }
    public void setCurrentMonthlyContribution(BigDecimal currentMonthlyContribution) { this.currentMonthlyContribution = currentMonthlyContribution; }

    public BigDecimal getRequiredMonthlySIP() { return requiredMonthlySIP; }
    public void setRequiredMonthlySIP(BigDecimal requiredMonthlySIP) { this.requiredMonthlySIP = requiredMonthlySIP; }

    public BigDecimal getAdditionalSIPNeeded() { return additionalSIPNeeded; }
    public void setAdditionalSIPNeeded(BigDecimal additionalSIPNeeded) { this.additionalSIPNeeded = additionalSIPNeeded; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
