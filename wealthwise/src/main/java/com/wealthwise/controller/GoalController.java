package com.wealthwise.controller;

import com.wealthwise.model.*;
import com.wealthwise.service.GoalMilestoneService;
import com.wealthwise.service.GoalService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Goal-Based Financial Planning (Module 5).
 *
 * All endpoints under /api/goals/**
 * Cross-origin allowed for all origins (consistent with existing controllers).
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/goals")
public class GoalController {

    private final GoalService goalService;
    private final GoalMilestoneService milestoneService;

    public GoalController(GoalService goalService,
                          GoalMilestoneService milestoneService) {
        this.goalService      = goalService;
        this.milestoneService = milestoneService;
    }

    // ─── Goal CRUD ───────────────────────────────────────────────────────────

    /**
     * POST /api/goals
     * Create a new financial goal.
     */
    @PostMapping
    public ResponseEntity<?> createGoal(@Valid @RequestBody GoalRequest request) {
        try {
            Goal goal = goalService.createGoal(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(goal);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * GET /api/goals/user/{userId}
     * List all goals for a specific user.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Goal>> getGoalsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(goalService.getGoalsByUser(userId));
    }

    /**
     * GET /api/goals/{goalId}
     * Get a single goal by its ID.
     */
    @GetMapping("/{goalId}")
    public ResponseEntity<?> getGoalById(@PathVariable Long goalId) {
        try {
            return ResponseEntity.ok(goalService.getGoalById(goalId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * PUT /api/goals/{goalId}
     * Update goal name, target, dates, rates etc.
     */
    @PutMapping("/{goalId}")
    public ResponseEntity<?> updateGoal(@PathVariable Long goalId,
                                        @Valid @RequestBody GoalRequest request) {
        try {
            Goal updated = goalService.updateGoal(goalId, request);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * DELETE /api/goals/{goalId}
     * Delete a goal along with its milestones and linked investments.
     */
    @DeleteMapping("/{goalId}")
    public ResponseEntity<?> deleteGoal(@PathVariable Long goalId) {
        try {
            goalService.deleteGoal(goalId);
            return ResponseEntity.ok("Goal deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // ─── Progress & Projection ───────────────────────────────────────────────

    /**
     * GET /api/goals/{goalId}/progress
     * Returns current progress %, status, and milestone counts.
     */
    @GetMapping("/{goalId}/progress")
    public ResponseEntity<?> getProgress(@PathVariable Long goalId) {
        try {
            GoalProgressResponse progress = goalService.getGoalProgress(goalId);
            return ResponseEntity.ok(progress);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * GET /api/goals/{goalId}/projection
     * Projects the future value of current savings by the target date.
     */
    @GetMapping("/{goalId}/projection")
    public ResponseEntity<?> getProjection(@PathVariable Long goalId) {
        try {
            GoalProjectionResponse projection = goalService.getGoalProjection(goalId);
            return ResponseEntity.ok(projection);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * GET /api/goals/{goalId}/gap-analysis
     * Returns gap (shortfall), required monthly SIP, and additional SIP needed.
     */
    @GetMapping("/{goalId}/gap-analysis")
    public ResponseEntity<?> getGapAnalysis(@PathVariable Long goalId) {
        try {
            GapAnalysisResponse gap = goalService.getGapAnalysis(goalId);
            return ResponseEntity.ok(gap);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // ─── Investment Linking ──────────────────────────────────────────────────

    /**
     * POST /api/goals/{goalId}/investments
     * Link a SIP / MF / other investment to a goal.
     */
    @PostMapping("/{goalId}/investments")
    public ResponseEntity<?> linkInvestment(@PathVariable Long goalId,
                                            @Valid @RequestBody GoalInvestmentLinkRequest request) {
        try {
            request.setGoalId(goalId);
            GoalInvestmentLink link = goalService.linkInvestment(goalId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(link);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * GET /api/goals/{goalId}/investments
     * List all investments linked to this goal.
     */
    @GetMapping("/{goalId}/investments")
    public ResponseEntity<?> getLinkedInvestments(@PathVariable Long goalId) {
        try {
            List<GoalInvestmentLink> links = goalService.getLinkedInvestments(goalId);
            return ResponseEntity.ok(links);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // ─── Milestones ──────────────────────────────────────────────────────────

    /**
     * POST /api/goals/{goalId}/milestones
     * Add a milestone to a goal.
     */
    @PostMapping("/{goalId}/milestones")
    public ResponseEntity<?> addMilestone(@PathVariable Long goalId,
                                          @RequestBody GoalMilestone milestone) {
        try {
            GoalMilestone saved = milestoneService.addMilestone(goalId, milestone);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * GET /api/goals/{goalId}/milestones
     * List milestones for a goal, ordered by target date.
     */
    @GetMapping("/{goalId}/milestones")
    public ResponseEntity<?> getMilestones(@PathVariable Long goalId) {
        try {
            List<GoalMilestone> milestones = milestoneService.getMilestones(goalId);
            return ResponseEntity.ok(milestones);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * PUT /api/goals/milestones/{milestoneId}
     * Update a milestone (name, target, date, achieved flag).
     */
    @PutMapping("/milestones/{milestoneId}")
    public ResponseEntity<?> updateMilestone(@PathVariable Long milestoneId,
                                             @RequestBody GoalMilestone update) {
        try {
            GoalMilestone updated = milestoneService.updateMilestone(milestoneId, update);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * DELETE /api/goals/milestones/{milestoneId}
     * Delete a milestone.
     */
    @DeleteMapping("/milestones/{milestoneId}")
    public ResponseEntity<?> deleteMilestone(@PathVariable Long milestoneId) {
        try {
            milestoneService.deleteMilestone(milestoneId);
            return ResponseEntity.ok("Milestone deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
