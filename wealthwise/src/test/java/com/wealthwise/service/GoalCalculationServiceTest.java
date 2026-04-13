package com.wealthwise.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for GoalCalculationService — verifies all financial formulas.
 */
class GoalCalculationServiceTest {

    private GoalCalculationService calcService;

    @BeforeEach
    void setUp() {
        calcService = new GoalCalculationService();
    }

    // ─── futureValue ─────────────────────────────────────────────────────────

    @Test
    void futureValue_shouldCompoundCorrectly() {
        // FV = 10000 × (1 + 0.12)^5 ≈ 17,623.41
        BigDecimal fv = calcService.futureValue(
                new BigDecimal("10000"), new BigDecimal("12.0"), 5);
        assertTrue(fv.compareTo(new BigDecimal("17000")) > 0,
                "FV should be greater than 17,000");
        assertTrue(fv.compareTo(new BigDecimal("18000")) < 0,
                "FV should be less than 18,000");
    }

    @Test
    void futureValue_withZeroPresentValue_shouldReturnZero() {
        BigDecimal fv = calcService.futureValue(
                BigDecimal.ZERO, new BigDecimal("12.0"), 5);
        assertEquals(BigDecimal.ZERO, fv);
    }

    @Test
    void futureValue_withNullPresentValue_shouldReturnZero() {
        BigDecimal fv = calcService.futureValue(null, new BigDecimal("12.0"), 5);
        assertEquals(BigDecimal.ZERO, fv);
    }

    // ─── inflationAdjustedTarget ─────────────────────────────────────────────

    @Test
    void inflationAdjustedTarget_shouldDeflateCorrectly() {
        // 1,000,000 / (1.06)^10 ≈ 558,395
        BigDecimal adj = calcService.inflationAdjustedTarget(
                new BigDecimal("1000000"), new BigDecimal("6.0"), 10);
        assertTrue(adj.compareTo(new BigDecimal("500000")) > 0,
                "Adjusted target should be > 500,000");
        assertTrue(adj.compareTo(new BigDecimal("700000")) < 0,
                "Adjusted target should be < 700,000");
    }

    @Test
    void inflationAdjustedTarget_withZeroInflation_shouldReturnNominalTarget() {
        BigDecimal adj = calcService.inflationAdjustedTarget(
                new BigDecimal("500000"), BigDecimal.ZERO, 10);
        assertEquals(0, adj.compareTo(new BigDecimal("500000")));
    }

    // ─── requiredMonthlySIP ──────────────────────────────────────────────────

    @Test
    void requiredMonthlySIP_shouldReturnPositiveAmount() {
        // SIP needed to grow ₹5,00,000 in 120 months at 12% annual
        BigDecimal sip = calcService.requiredMonthlySIP(
                new BigDecimal("500000"), new BigDecimal("12.0"), 120);
        assertTrue(sip.compareTo(BigDecimal.ZERO) > 0,
                "Required SIP should be positive");
        // Sanity: SIP should be well below 5,00,000/120 = 4,166 (compounding helps)
        assertTrue(sip.compareTo(new BigDecimal("5000")) < 0,
                "Required SIP should be less than ₹5,000");
    }

    @Test
    void requiredMonthlySIP_withZeroFV_shouldReturnZero() {
        BigDecimal sip = calcService.requiredMonthlySIP(
                BigDecimal.ZERO, new BigDecimal("12.0"), 120);
        assertEquals(BigDecimal.ZERO, sip);
    }

    // ─── progressPercent ─────────────────────────────────────────────────────

    @Test
    void progressPercent_shouldCalculateCorrectly() {
        BigDecimal pct = calcService.progressPercent(
                new BigDecimal("50000"), new BigDecimal("200000"));
        assertEquals(0, pct.compareTo(new BigDecimal("25.00")));
    }

    @Test
    void progressPercent_shouldClampAt100() {
        BigDecimal pct = calcService.progressPercent(
                new BigDecimal("300000"), new BigDecimal("200000"));
        assertEquals(0, pct.compareTo(BigDecimal.valueOf(100)));
    }

    @Test
    void progressPercent_withZeroTarget_shouldReturnZero() {
        BigDecimal pct = calcService.progressPercent(
                new BigDecimal("10000"), BigDecimal.ZERO);
        assertEquals(BigDecimal.ZERO, pct);
    }

    // ─── computeGoalStatus ───────────────────────────────────────────────────

    @Test
    void computeGoalStatus_shouldReturnCompleted_whenProgressIs100() {
        String status = calcService.computeGoalStatus(
                BigDecimal.valueOf(100), new BigDecimal("100"), new BigDecimal("90"));
        assertEquals("COMPLETED", status);
    }

    @Test
    void computeGoalStatus_shouldReturnOnTrack_whenProjectedExceedsTarget() {
        String status = calcService.computeGoalStatus(
                new BigDecimal("50"), new BigDecimal("120"), new BigDecimal("100"));
        assertEquals("ON_TRACK", status);
    }

    @Test
    void computeGoalStatus_shouldReturnAtRisk_whenProjectedBelowTarget() {
        String status = calcService.computeGoalStatus(
                new BigDecimal("30"), new BigDecimal("60"), new BigDecimal("100"));
        assertEquals("AT_RISK", status);
    }

    @Test
    void computeGoalStatus_shouldReturnActive_whenNullProjection() {
        String status = calcService.computeGoalStatus(null, null, null);
        assertEquals("ACTIVE", status);
    }
}
