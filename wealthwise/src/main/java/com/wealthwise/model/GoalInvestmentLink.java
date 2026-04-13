package com.wealthwise.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Links a recurring investment (SIP / MF / LUMPSUM) to a parent Goal.
 * The sum of linked monthlyAmounts represents the current monthly contribution.
 */
@Entity
@Table(name = "goal_investment_links", indexes = {
        @Index(name = "idx_invest_links_goal_id", columnList = "goal_id")
})
public class GoalInvestmentLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long linkId;

    @Column(name = "goal_id", nullable = false)
    private Long goalId;

    /**
     * Investment type: SIP, MF, LUMPSUM, RD, FD, STOCKS, OTHER
     */
    @NotBlank(message = "Investment type must not be blank")
    @Column(nullable = false, length = 50)
    private String investmentType;

    @NotNull(message = "Monthly amount is required")
    @DecimalMin(value = "0.01", message = "Monthly amount must be greater than 0")
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal monthlyAmount;

    @Size(max = 255)
    @Column(length = 255)
    private String description;

    @Column(nullable = false, updatable = false)
    private LocalDateTime linkedAt;

    @PrePersist
    protected void onLink() {
        linkedAt = LocalDateTime.now();
    }

    // ─── Getters & Setters ───────────────────────────────────────────────────

    public Long getLinkId() { return linkId; }

    public void setLinkId(Long linkId) { this.linkId = linkId; }

    public Long getGoalId() { return goalId; }

    public void setGoalId(Long goalId) { this.goalId = goalId; }

    public String getInvestmentType() { return investmentType; }

    public void setInvestmentType(String investmentType) { this.investmentType = investmentType; }

    public BigDecimal getMonthlyAmount() { return monthlyAmount; }

    public void setMonthlyAmount(BigDecimal monthlyAmount) { this.monthlyAmount = monthlyAmount; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getLinkedAt() { return linkedAt; }
}
