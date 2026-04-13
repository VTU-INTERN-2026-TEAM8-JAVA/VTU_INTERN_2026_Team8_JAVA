package com.wealthwise.service;

import com.wealthwise.model.GoalMilestone;
import com.wealthwise.repository.GoalMilestoneRepository;
import com.wealthwise.repository.GoalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for managing Goal Milestones (sub-goal checkpoints).
 */
@Service
@Transactional
public class GoalMilestoneService {

    private final GoalMilestoneRepository milestoneRepository;
    private final GoalRepository goalRepository;

    public GoalMilestoneService(GoalMilestoneRepository milestoneRepository,
                                GoalRepository goalRepository) {
        this.milestoneRepository = milestoneRepository;
        this.goalRepository      = goalRepository;
    }

    /**
     * Add a milestone to a goal.
     */
    public GoalMilestone addMilestone(Long goalId, GoalMilestone milestone) {
        // Ensure goal exists
        goalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Goal not found with id: " + goalId));

        validateMilestone(milestone);
        milestone.setGoalId(goalId);
        milestone.setAchieved(false);
        return milestoneRepository.save(milestone);
    }

    /**
     * Retrieve all milestones for a goal, ordered by target date.
     */
    @Transactional(readOnly = true)
    public List<GoalMilestone> getMilestones(Long goalId) {
        return milestoneRepository.findByGoalIdOrderByTargetDateAsc(goalId);
    }

    /**
     * Update milestone fields (name, targetAmount, targetDate, isAchieved).
     */
    public GoalMilestone updateMilestone(Long milestoneId, GoalMilestone update) {
        GoalMilestone existing = milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new RuntimeException("Milestone not found with id: " + milestoneId));

        if (update.getName() != null && !update.getName().isBlank()) {
            existing.setName(update.getName());
        }
        if (update.getTargetAmount() != null) {
            existing.setTargetAmount(update.getTargetAmount());
        }
        if (update.getTargetDate() != null) {
            existing.setTargetDate(update.getTargetDate());
        }
        existing.setAchieved(update.isAchieved());

        return milestoneRepository.save(existing);
    }

    /**
     * Delete a milestone by its id.
     */
    public void deleteMilestone(Long milestoneId) {
        milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new RuntimeException("Milestone not found with id: " + milestoneId));
        milestoneRepository.deleteById(milestoneId);
    }

    // ─── Validation ──────────────────────────────────────────────────────────

    private void validateMilestone(GoalMilestone milestone) {
        if (milestone.getName() == null || milestone.getName().isBlank()) {
            throw new IllegalArgumentException("Milestone name must not be blank");
        }
        if (milestone.getTargetAmount() == null
                || milestone.getTargetAmount().signum() <= 0) {
            throw new IllegalArgumentException("Milestone target amount must be positive");
        }
        if (milestone.getTargetDate() == null) {
            throw new IllegalArgumentException("Milestone target date is required");
        }
    }
}
