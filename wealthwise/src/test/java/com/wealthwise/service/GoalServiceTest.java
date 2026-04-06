package com.wealthwise.service;

import com.wealthwise.model.*;
import com.wealthwise.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for GoalService — repositories are mocked with Mockito.
 */
class GoalServiceTest {

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private GoalInvestmentLinkRepository linkRepository;

    @Mock
    private GoalMilestoneRepository milestoneRepository;

    @InjectMocks
    private GoalService goalService;

    private GoalCalculationService calcService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        calcService = new GoalCalculationService();
        goalService = new GoalService(goalRepository, linkRepository, milestoneRepository, calcService);
    }

    // ─── createGoal ──────────────────────────────────────────────────────────

    @Test
    void createGoal_shouldSaveAndReturnGoal() {
        GoalRequest req = buildValidRequest();
        Goal savedGoal  = buildGoal(1L, req);

        when(goalRepository.save(any(Goal.class))).thenReturn(savedGoal);

        Goal result = goalService.createGoal(req);

        assertNotNull(result);
        assertEquals("Retirement Fund", result.getName());
        verify(goalRepository, times(1)).save(any(Goal.class));
    }

    @Test
    void createGoal_withPastDate_shouldThrowException() {
        GoalRequest req = buildValidRequest();
        req.setTargetDate(LocalDate.now().minusMonths(1)); // invalid date

        assertThrows(IllegalArgumentException.class, () -> goalService.createGoal(req));
        verify(goalRepository, never()).save(any());
    }

    @Test
    void createGoal_withDateTooSoon_shouldThrowException() {
        GoalRequest req = buildValidRequest();
        req.setTargetDate(LocalDate.now().plusDays(10)); // less than 30 days

        assertThrows(IllegalArgumentException.class, () -> goalService.createGoal(req));
        verify(goalRepository, never()).save(any());
    }

    @Test
    void createGoal_withNullUserId_shouldThrowException() {
        GoalRequest req = buildValidRequest();
        req.setUserId(null);

        assertThrows(IllegalArgumentException.class, () -> goalService.createGoal(req));
    }

    // ─── getGoalById ─────────────────────────────────────────────────────────

    @Test
    void getGoalById_shouldReturnGoal_whenExists() {
        Goal goal = buildGoal(1L, buildValidRequest());
        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));

        Goal result = goalService.getGoalById(1L);
        assertEquals(1L, result.getGoalId());
    }

    @Test
    void getGoalById_shouldThrowException_whenNotFound() {
        when(goalRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> goalService.getGoalById(999L));
    }

    // ─── getGoalsByUser ──────────────────────────────────────────────────────

    @Test
    void getGoalsByUser_shouldReturnList() {
        Goal g1 = buildGoal(1L, buildValidRequest());
        Goal g2 = buildGoal(2L, buildValidRequest());
        when(goalRepository.findByUserIdOrderByCreatedAtDesc(10L))
                .thenReturn(List.of(g1, g2));

        List<Goal> goals = goalService.getGoalsByUser(10L);
        assertEquals(2, goals.size());
    }

    // ─── deleteGoal ──────────────────────────────────────────────────────────

    @Test
    void deleteGoal_shouldDeleteGoalAndChildren() {
        Goal goal = buildGoal(1L, buildValidRequest());
        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));
        when(linkRepository.findByGoalId(1L)).thenReturn(List.of());
        when(milestoneRepository.findByGoalIdOrderByTargetDateAsc(1L)).thenReturn(List.of());

        goalService.deleteGoal(1L);

        verify(goalRepository, times(1)).delete(goal);
    }

    @Test
    void deleteGoal_withNonExistentId_shouldThrow() {
        when(goalRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> goalService.deleteGoal(999L));
    }

    // ─── getGoalProgress ─────────────────────────────────────────────────────

    @Test
    void getGoalProgress_shouldReturnCorrectPercentage() {
        GoalRequest req = buildValidRequest();
        req.setCurrentAmount(new BigDecimal("50000"));
        req.setTargetAmount(new BigDecimal("200000"));

        Goal goal = buildGoal(1L, req);
        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));
        when(goalRepository.save(any())).thenReturn(goal);
        when(milestoneRepository.countByGoalId(1L)).thenReturn(4L);
        when(milestoneRepository.countByGoalIdAndIsAchievedTrue(1L)).thenReturn(1L);

        GoalProgressResponse resp = goalService.getGoalProgress(1L);

        assertEquals(0, resp.getProgressPercent().compareTo(new BigDecimal("25.00")));
        assertEquals(4L, resp.getMilestonesTotal());
        assertEquals(1L, resp.getMilestonesAchieved());
    }

    // ─── linkInvestment ──────────────────────────────────────────────────────

    @Test
    void linkInvestment_shouldSaveLink() {
        Goal goal = buildGoal(1L, buildValidRequest());
        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));

        GoalInvestmentLinkRequest req = new GoalInvestmentLinkRequest();
        req.setInvestmentType("SIP");
        req.setMonthlyAmount(new BigDecimal("5000"));
        req.setDescription("Monthly SIP");

        GoalInvestmentLink savedLink = new GoalInvestmentLink();
        savedLink.setLinkId(1L);
        savedLink.setGoalId(1L);
        savedLink.setMonthlyAmount(new BigDecimal("5000"));

        when(linkRepository.save(any())).thenReturn(savedLink);

        GoalInvestmentLink result = goalService.linkInvestment(1L, req);
        assertNotNull(result);
        assertEquals(0, result.getMonthlyAmount().compareTo(new BigDecimal("5000")));
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private GoalRequest buildValidRequest() {
        GoalRequest req = new GoalRequest();
        req.setUserId(10L);
        req.setName("Retirement Fund");
        req.setCategory("RETIREMENT");
        req.setTargetAmount(new BigDecimal("5000000"));
        req.setCurrentAmount(new BigDecimal("100000"));
        req.setTargetDate(LocalDate.now().plusYears(20));
        req.setExpectedReturnRate(new BigDecimal("12.00"));
        req.setInflationRate(new BigDecimal("6.00"));
        return req;
    }

    private Goal buildGoal(Long id, GoalRequest req) {
        Goal g = new Goal();
        g.setGoalId(id);
        g.setUserId(req.getUserId());
        g.setName(req.getName());
        g.setCategory(req.getCategory());
        g.setTargetAmount(req.getTargetAmount());
        g.setCurrentAmount(req.getCurrentAmount() != null ? req.getCurrentAmount() : BigDecimal.ZERO);
        g.setTargetDate(req.getTargetDate());
        g.setExpectedReturnRate(req.getExpectedReturnRate());
        g.setInflationRate(req.getInflationRate());
        g.setStatus("ACTIVE");
        return g;
    }
}
