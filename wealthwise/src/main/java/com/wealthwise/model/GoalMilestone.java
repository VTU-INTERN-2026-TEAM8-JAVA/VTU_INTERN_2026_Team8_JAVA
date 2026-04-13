package com.wealthwise.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * A milestone (sub-goal checkpoint) belonging to a parent Goal.
 */
@Entity
@Table(name = "goal_milestones", indexes = {
        @Index(name = "idx_milestones_goal_id", columnList = "goal_id")
})
public class GoalMilestone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long milestoneId;

    @Column(name = "goal_id", nullable = false)
    private Long goalId;

    @NotBlank(message = "Milestone name must not be blank")
    @Size(max = 150, message = "Milestone name must not exceed 150 characters")
    @Column(nullable = false, length = 150)
    private String name;

    @NotNull(message = "Milestone target amount is required")
    @DecimalMin(value = "0.01", message = "Target amount must be greater than 0")
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal targetAmount;

    @NotNull(message = "Milestone target date is required")
    @Column(nullable = false)
    private LocalDate targetDate;

    @Column(nullable = false)
    private boolean isAchieved = false;

    // ─── Getters & Setters ───────────────────────────────────────────────────

    public Long getMilestoneId() { return milestoneId; }

    public void setMilestoneId(Long milestoneId) { this.milestoneId = milestoneId; }

    public Long getGoalId() { return goalId; }

    public void setGoalId(Long goalId) { this.goalId = goalId; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public BigDecimal getTargetAmount() { return targetAmount; }

    public void setTargetAmount(BigDecimal targetAmount) { this.targetAmount = targetAmount; }

    public LocalDate getTargetDate() { return targetDate; }

    public void setTargetDate(LocalDate targetDate) { this.targetDate = targetDate; }

    public boolean isAchieved() { return isAchieved; }

    public void setAchieved(boolean achieved) { isAchieved = achieved; }
}
