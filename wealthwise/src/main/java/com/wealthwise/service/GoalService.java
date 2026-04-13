package com.wealthwise.service;

import com.wealthwise.model.*;
import com.wealthwise.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Business logic service for Goal CRUD, progress tracking,
 * projection, gap analysis, and investment linking.
 */
@Service
@Transactional
public class GoalService {

    private final GoalRepository goalRepository;
    private final GoalInvestmentLinkRepository linkRepository;
    private final GoalMilestoneRepository milestoneRepository;
    private final GoalCalculationService calc;

    public GoalService(GoalRepository goalRepository,
                       GoalInvestmentLinkRepository linkRepository,
                       GoalMilestoneRepository milestoneRepository,
                       GoalCalculationService calc) {
        this.goalRepository   = goalRepository;
        this.linkRepository   = linkRepository;
        this.milestoneRepository = milestoneRepository;
        this.calc             = calc;
    }

    // ─── CRUD ────────────────────────────────────────────────────────────────

    /**
     * Create a new goal for a user.
     * Validates that targetDate is at least 30 days in the future.
     */
    public Goal createGoal(GoalRequest request) {
        validateRequest(request);

        Goal goal = new Goal();
        applyRequest(goal, request);
        goal.setStatus("ACTIVE");

        return goalRepository.save(goal);
    }

    /**
     * Update an existing goal's details.
     */
    public Goal updateGoal(Long goalId, GoalRequest request) {
        validateRequest(request);

        Goal goal = findGoalOrThrow(goalId);
        applyRequest(goal, request);

        // Re-evaluate completion status after update
        refreshStatus(goal);

        return goalRepository.save(goal);
    }

    /**
     * Delete a goal and all its linked investments and milestones.
     */
    public void deleteGoal(Long goalId) {
        Goal goal = findGoalOrThrow(goalId);
        // Child records deleted via queries (no cascade in entity to stay compatible)
        List<GoalInvestmentLink> links = linkRepository.findByGoalId(goalId);
        linkRepository.deleteAll(links);

        List<GoalMilestone> milestones = milestoneRepository.findByGoalIdOrderByTargetDateAsc(goalId);
        milestoneRepository.deleteAll(milestones);

        goalRepository.delete(goal);
    }

    @Transactional(readOnly = true)
    public Goal getGoalById(Long goalId) {
        return findGoalOrThrow(goalId);
    }

    @Transactional(readOnly = true)
    public List<Goal> getGoalsByUser(Long userId) {
        return goalRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Transactional(readOnly = true)
    public List<Goal> getGoalsByUserAndStatus(Long userId, String status) {
        return goalRepository.findByUserIdAndStatus(userId, status);
    }

    // ─── PROGRESS TRACKING ───────────────────────────────────────────────────

    /**
     * Compute progress based on currentAmount vs targetAmount.
     * Also refreshes and persists the goal status.
     */
    public GoalProgressResponse getGoalProgress(Long goalId) {
        Goal goal = findGoalOrThrow(goalId);

        BigDecimal progress = calc.progressPercent(goal.getCurrentAmount(), goal.getTargetAmount());

        double years = yearsUntilTarget(goal.getTargetDate());
        BigDecimal projFV = calc.futureValue(goal.getCurrentAmount(), goal.getExpectedReturnRate(), years);
        BigDecimal adjTarget = calc.inflationAdjustedTarget(goal.getTargetAmount(), goal.getInflationRate(), years);
        String status = calc.computeGoalStatus(progress, projFV, adjTarget);

        // Persist updated status
        goal.setStatus(status);
        goalRepository.save(goal);

        long total    = milestoneRepository.countByGoalId(goalId);
        long achieved = milestoneRepository.countByGoalIdAndIsAchievedTrue(goalId);

        return new GoalProgressResponse(
                goal.getGoalId(), goal.getName(),
                goal.getCurrentAmount(), goal.getTargetAmount(),
                progress, status, total, achieved
        );
    }

    // ─── PROJECTION ──────────────────────────────────────────────────────────

    /**
     * Project what the current corpus will grow to by the target date.
     */
    @Transactional(readOnly = true)
    public GoalProjectionResponse getGoalProjection(Long goalId) {
        Goal goal = findGoalOrThrow(goalId);

        double years = yearsUntilTarget(goal.getTargetDate());
        BigDecimal projFV       = calc.futureValue(goal.getCurrentAmount(), goal.getExpectedReturnRate(), years);
        BigDecimal adjTarget    = calc.inflationAdjustedTarget(goal.getTargetAmount(), goal.getInflationRate(), years);
        BigDecimal progress     = calc.progressPercent(goal.getCurrentAmount(), goal.getTargetAmount());
        String status           = calc.computeGoalStatus(progress, projFV, adjTarget);

        GoalProjectionResponse resp = new GoalProjectionResponse();
        resp.setGoalId(goal.getGoalId());
        resp.setGoalName(goal.getName());
        resp.setProjectedFutureValue(projFV);
        resp.setInflationAdjustedTarget(adjTarget);
        resp.setCurrentAmount(goal.getCurrentAmount());
        resp.setYearsToGoal(Math.max(0, years));
        resp.setAnnualReturnRate(goal.getExpectedReturnRate());
        resp.setInflationRate(goal.getInflationRate());
        resp.setStatus(status);
        return resp;
    }

    // ─── GAP ANALYSIS ────────────────────────────────────────────────────────

    /**
     * Compute how much additional monthly SIP is needed to close the gap.
     */
    @Transactional(readOnly = true)
    public GapAnalysisResponse getGapAnalysis(Long goalId) {
        Goal goal = findGoalOrThrow(goalId);

        double years  = Math.max(0, yearsUntilTarget(goal.getTargetDate()));
        int months    = (int) Math.round(years * 12);

        BigDecimal projFV       = calc.futureValue(goal.getCurrentAmount(), goal.getExpectedReturnRate(), years);
        BigDecimal adjTarget    = calc.inflationAdjustedTarget(goal.getTargetAmount(), goal.getInflationRate(), years);

        // Gap = shortfall in future-value terms
        BigDecimal gap = adjTarget.subtract(projFV);
        if (gap.compareTo(BigDecimal.ZERO) < 0) gap = BigDecimal.ZERO;

        BigDecimal requiredSIP = months > 0
                ? calc.requiredMonthlySIP(gap, goal.getExpectedReturnRate(), months)
                : BigDecimal.ZERO;

        BigDecimal currentContrib = linkRepository.sumMonthlyAmountByGoalId(goalId);
        if (currentContrib == null) currentContrib = BigDecimal.ZERO;

        BigDecimal additional = requiredSIP.subtract(currentContrib);
        if (additional.compareTo(BigDecimal.ZERO) < 0) additional = BigDecimal.ZERO;

        BigDecimal progress = calc.progressPercent(goal.getCurrentAmount(), goal.getTargetAmount());
        String status       = calc.computeGoalStatus(progress, projFV, adjTarget);

        GapAnalysisResponse resp = new GapAnalysisResponse();
        resp.setGoalId(goal.getGoalId());
        resp.setGoalName(goal.getName());
        resp.setTargetAmount(goal.getTargetAmount());
        resp.setInflationAdjustedTarget(adjTarget);
        resp.setProjectedFutureValue(projFV);
        resp.setGap(gap);
        resp.setCurrentMonthlyContribution(currentContrib);
        resp.setRequiredMonthlySIP(requiredSIP);
        resp.setAdditionalSIPNeeded(additional);
        resp.setStatus(status);
        return resp;
    }

    // ─── INVESTMENT LINKING ──────────────────────────────────────────────────

    /**
     * Link a new investment stream to a goal.
     */
    public GoalInvestmentLink linkInvestment(Long goalId, GoalInvestmentLinkRequest request) {
        findGoalOrThrow(goalId); // validate goal exists

        GoalInvestmentLink link = new GoalInvestmentLink();
        link.setGoalId(goalId);
        link.setInvestmentType(request.getInvestmentType());
        link.setMonthlyAmount(request.getMonthlyAmount());
        link.setDescription(request.getDescription());

        return linkRepository.save(link);
    }

    @Transactional(readOnly = true)
    public List<GoalInvestmentLink> getLinkedInvestments(Long goalId) {
        findGoalOrThrow(goalId);
        return linkRepository.findByGoalId(goalId);
    }

    // ─── PRIVATE HELPERS ─────────────────────────────────────────────────────

    private Goal findGoalOrThrow(Long goalId) {
        return goalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Goal not found with id: " + goalId));
    }

    private void validateRequest(GoalRequest request) {
        if (request.getTargetDate() == null) {
            throw new IllegalArgumentException("Target date is required");
        }
        long daysUntilTarget = ChronoUnit.DAYS.between(LocalDate.now(), request.getTargetDate());
        if (daysUntilTarget < 30) {
            throw new IllegalArgumentException("Target date must be at least 30 days in the future");
        }
        if (request.getTargetAmount() == null || request.getTargetAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Target amount must be positive");
        }
        if (request.getUserId() == null) {
            throw new IllegalArgumentException("User ID is required");
        }
    }

    private void applyRequest(Goal goal, GoalRequest r) {
        goal.setUserId(r.getUserId());
        goal.setName(r.getName());
        goal.setCategory(r.getCategory());
        goal.setTargetAmount(r.getTargetAmount());
        goal.setCurrentAmount(r.getCurrentAmount() != null ? r.getCurrentAmount() : BigDecimal.ZERO);
        goal.setTargetDate(r.getTargetDate());
        goal.setExpectedReturnRate(r.getExpectedReturnRate() != null
                ? r.getExpectedReturnRate() : new BigDecimal("12.00"));
        goal.setInflationRate(r.getInflationRate() != null
                ? r.getInflationRate() : new BigDecimal("6.00"));
        goal.setNotes(r.getNotes());
    }

    private void refreshStatus(Goal goal) {
        double years   = yearsUntilTarget(goal.getTargetDate());
        BigDecimal projFV    = calc.futureValue(goal.getCurrentAmount(), goal.getExpectedReturnRate(), years);
        BigDecimal adjTarget = calc.inflationAdjustedTarget(goal.getTargetAmount(), goal.getInflationRate(), years);
        BigDecimal progress  = calc.progressPercent(goal.getCurrentAmount(), goal.getTargetAmount());
        goal.setStatus(calc.computeGoalStatus(progress, projFV, adjTarget));
    }

    private double yearsUntilTarget(LocalDate targetDate) {
        long days = ChronoUnit.DAYS.between(LocalDate.now(), targetDate);
        return calc.daysToYears(days);
    }
}
