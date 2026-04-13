package com.wealthwise.model;

import java.math.BigDecimal;

/**
 * Response for the /progress endpoint — current savings vs target.
 */
public class GoalProgressResponse {

    private Long goalId;
    private String goalName;
    private BigDecimal currentAmount;
    private BigDecimal targetAmount;
    private BigDecimal progressPercent;   // 0.00 – 100.00
    private String status;                // ACTIVE / ON_TRACK / AT_RISK / COMPLETED
    private long milestonesTotal;
    private long milestonesAchieved;

    // ─── Constructors ────────────────────────────────────────────────────────

    public GoalProgressResponse() {}

    public GoalProgressResponse(Long goalId, String goalName,
                                BigDecimal currentAmount, BigDecimal targetAmount,
                                BigDecimal progressPercent, String status,
                                long milestonesTotal, long milestonesAchieved) {
        this.goalId = goalId;
        this.goalName = goalName;
        this.currentAmount = currentAmount;
        this.targetAmount = targetAmount;
        this.progressPercent = progressPercent;
        this.status = status;
        this.milestonesTotal = milestonesTotal;
        this.milestonesAchieved = milestonesAchieved;
    }

    // ─── Getters & Setters ───────────────────────────────────────────────────

    public Long getGoalId() { return goalId; }
    public void setGoalId(Long goalId) { this.goalId = goalId; }

    public String getGoalName() { return goalName; }
    public void setGoalName(String goalName) { this.goalName = goalName; }

    public BigDecimal getCurrentAmount() { return currentAmount; }
    public void setCurrentAmount(BigDecimal currentAmount) { this.currentAmount = currentAmount; }

    public BigDecimal getTargetAmount() { return targetAmount; }
    public void setTargetAmount(BigDecimal targetAmount) { this.targetAmount = targetAmount; }

    public BigDecimal getProgressPercent() { return progressPercent; }
    public void setProgressPercent(BigDecimal progressPercent) { this.progressPercent = progressPercent; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public long getMilestonesTotal() { return milestonesTotal; }
    public void setMilestonesTotal(long milestonesTotal) { this.milestonesTotal = milestonesTotal; }

    public long getMilestonesAchieved() { return milestonesAchieved; }
    public void setMilestonesAchieved(long milestonesAchieved) { this.milestonesAchieved = milestonesAchieved; }
}
