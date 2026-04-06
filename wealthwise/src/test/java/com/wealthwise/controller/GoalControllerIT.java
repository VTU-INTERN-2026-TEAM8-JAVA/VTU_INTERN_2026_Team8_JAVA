package com.wealthwise.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.wealthwise.model.*;
import com.wealthwise.service.GoalMilestoneService;
import com.wealthwise.service.GoalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration (slice) test for GoalController.
 * Uses @WebMvcTest — no real DB needed, services are mocked.
 */
@WebMvcTest(GoalController.class)
class GoalControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GoalService goalService;

    @MockBean
    private GoalMilestoneService milestoneService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    // ─── POST /api/goals ─────────────────────────────────────────────────────

    @Test
    void createGoal_shouldReturn201_withValidRequest() throws Exception {
        GoalRequest req = buildGoalRequest();
        Goal savedGoal  = buildGoal(1L);

        when(goalService.createGoal(any(GoalRequest.class))).thenReturn(savedGoal);

        mockMvc.perform(post("/api/goals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.goalId").value(1));
    }

    // ─── GET /api/goals/user/{userId} ────────────────────────────────────────

    @Test
    void getGoalsByUser_shouldReturn200_withList() throws Exception {
        when(goalService.getGoalsByUser(10L)).thenReturn(List.of(buildGoal(1L), buildGoal(2L)));

        mockMvc.perform(get("/api/goals/user/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    // ─── GET /api/goals/{goalId} ─────────────────────────────────────────────

    @Test
    void getGoalById_shouldReturn200_whenExists() throws Exception {
        when(goalService.getGoalById(1L)).thenReturn(buildGoal(1L));

        mockMvc.perform(get("/api/goals/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Retirement Fund"));
    }

    @Test
    void getGoalById_shouldReturn404_whenNotFound() throws Exception {
        when(goalService.getGoalById(999L)).thenThrow(new RuntimeException("Goal not found"));

        mockMvc.perform(get("/api/goals/999"))
                .andExpect(status().isNotFound());
    }

    // ─── GET /api/goals/{goalId}/progress ────────────────────────────────────

    @Test
    void getProgress_shouldReturn200() throws Exception {
        GoalProgressResponse resp = new GoalProgressResponse(
                1L, "Retirement Fund",
                new BigDecimal("100000"), new BigDecimal("5000000"),
                new BigDecimal("2.00"), "ACTIVE", 3L, 1L);

        when(goalService.getGoalProgress(1L)).thenReturn(resp);

        mockMvc.perform(get("/api/goals/1/progress"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.progressPercent").value(2.00));
    }

    // ─── GET /api/goals/{goalId}/projection ──────────────────────────────────

    @Test
    void getProjection_shouldReturn200() throws Exception {
        GoalProjectionResponse resp = new GoalProjectionResponse();
        resp.setGoalId(1L);
        resp.setProjectedFutureValue(new BigDecimal("9646293.0000"));
        resp.setStatus("ON_TRACK");

        when(goalService.getGoalProjection(1L)).thenReturn(resp);

        mockMvc.perform(get("/api/goals/1/projection"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ON_TRACK"));
    }

    // ─── GET /api/goals/{goalId}/gap-analysis ────────────────────────────────

    @Test
    void getGapAnalysis_shouldReturn200() throws Exception {
        GapAnalysisResponse resp = new GapAnalysisResponse();
        resp.setGoalId(1L);
        resp.setRequiredMonthlySIP(new BigDecimal("2345.00"));
        resp.setStatus("AT_RISK");

        when(goalService.getGapAnalysis(1L)).thenReturn(resp);

        mockMvc.perform(get("/api/goals/1/gap-analysis"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("AT_RISK"));
    }

    // ─── DELETE /api/goals/{goalId} ──────────────────────────────────────────

    @Test
    void deleteGoal_shouldReturn200() throws Exception {
        mockMvc.perform(delete("/api/goals/1"))
                .andExpect(status().isOk());
    }

    // ─── GET /api/goals/{goalId}/milestones ──────────────────────────────────

    @Test
    void getMilestones_shouldReturn200_withList() throws Exception {
        when(milestoneService.getMilestones(1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/goals/1/milestones"))
                .andExpect(status().isOk());
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private GoalRequest buildGoalRequest() {
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

    private Goal buildGoal(Long id) {
        Goal g = new Goal();
        g.setGoalId(id);
        g.setUserId(10L);
        g.setName("Retirement Fund");
        g.setTargetAmount(new BigDecimal("5000000"));
        g.setCurrentAmount(new BigDecimal("100000"));
        g.setTargetDate(LocalDate.now().plusYears(20));
        g.setExpectedReturnRate(new BigDecimal("12.00"));
        g.setInflationRate(new BigDecimal("6.00"));
        g.setStatus("ACTIVE");
        return g;
    }
}
