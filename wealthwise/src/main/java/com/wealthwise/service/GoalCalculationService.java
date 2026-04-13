package com.wealthwise.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Pure math service for goal financial calculations.
 * All methods are stateless — no DB access.
 * Uses BigDecimal throughout for precision.
 */
@Service
public class GoalCalculationService {

    private static final int SCALE = 4;
    private static final RoundingMode RM = RoundingMode.HALF_UP;

    /**
     * Compound Future Value of a present value.
     * FV = PV × (1 + r/100)^n
     *
     * @param presentValue current corpus / savings
     * @param annualRatePct annual return rate in % (e.g. 12.0)
     * @param years         number of years
     * @return future value
     */
    public BigDecimal futureValue(BigDecimal presentValue, BigDecimal annualRatePct, double years) {
        if (presentValue == null || presentValue.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        // rate as decimal
        double r = annualRatePct.doubleValue() / 100.0;
        double fv = presentValue.doubleValue() * Math.pow(1 + r, years);
        return BigDecimal.valueOf(fv).setScale(SCALE, RM);
    }

    /**
     * Inflation-adjusted (real) value of a target amount.
     * adjustedTarget = target / (1 + inflationRate/100)^n
     *
     * @param targetAmount  nominal target
     * @param inflationRate annual inflation in % (e.g. 6.0)
     * @param years         number of years
     * @return inflation-adjusted target in today's money
     */
    public BigDecimal inflationAdjustedTarget(BigDecimal targetAmount,
                                              BigDecimal inflationRate,
                                              double years) {
        if (targetAmount == null) return BigDecimal.ZERO;
        double i = inflationRate == null ? 0 : inflationRate.doubleValue() / 100.0;
        double adjusted = targetAmount.doubleValue() / Math.pow(1 + i, years);
        return BigDecimal.valueOf(adjusted).setScale(SCALE, RM);
    }

    /**
     * Required monthly SIP to accumulate a future target.
     * PMT = FV × r_m / ((1 + r_m)^n_months - 1)
     * where r_m = annualRate / 12 / 100
     *
     * @param futureValueNeeded amount needed at end-of-period
     * @param annualRatePct     annual return rate in %
     * @param months            total investment horizon in months
     * @return required monthly SIP amount
     */
    public BigDecimal requiredMonthlySIP(BigDecimal futureValueNeeded,
                                         BigDecimal annualRatePct,
                                         int months) {
        if (futureValueNeeded == null || futureValueNeeded.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        double fv = futureValueNeeded.doubleValue();
        double rm = annualRatePct.doubleValue() / 12.0 / 100.0;

        if (rm == 0) {
            // degenerate case — no returns
            double pmt = fv / months;
            return BigDecimal.valueOf(pmt).setScale(SCALE, RM);
        }

        double denominator = Math.pow(1 + rm, months) - 1;
        double pmt = fv * rm / denominator;
        return BigDecimal.valueOf(pmt).setScale(SCALE, RM);
    }

    /**
     * Progress percentage: current / target × 100, clamped to [0, 100].
     *
     * @param currentAmount amount saved so far
     * @param targetAmount  goal target
     * @return progress in % (BigDecimal, 2dp)
     */
    public BigDecimal progressPercent(BigDecimal currentAmount, BigDecimal targetAmount) {
        if (targetAmount == null || targetAmount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        if (currentAmount == null) return BigDecimal.ZERO;

        BigDecimal pct = currentAmount
                .multiply(BigDecimal.valueOf(100))
                .divide(targetAmount, 2, RM);

        // clamp 0–100
        if (pct.compareTo(BigDecimal.ZERO) < 0) return BigDecimal.ZERO;
        if (pct.compareTo(BigDecimal.valueOf(100)) > 0) return BigDecimal.valueOf(100);
        return pct;
    }

    /**
     * Determines goal status based on projected future value vs. inflation-adjusted target.
     *
     * Rules:
     *   - progressPercent >= 100           → COMPLETED
     *   - projectedFV >= inflationTarget   → ON_TRACK
     *   - projectedFV <  inflationTarget   → AT_RISK
     *
     * @param progressPercent    0–100
     * @param projectedFV        future value of current savings at given rate
     * @param inflationAdjTarget inflation-adjusted target in today's money
     * @return status string
     */
    public String computeGoalStatus(BigDecimal progressPercent,
                                    BigDecimal projectedFV,
                                    BigDecimal inflationAdjTarget) {
        if (progressPercent != null && progressPercent.compareTo(BigDecimal.valueOf(100)) >= 0) {
            return "COMPLETED";
        }
        if (projectedFV != null && inflationAdjTarget != null
                && projectedFV.compareTo(inflationAdjTarget) >= 0) {
            return "ON_TRACK";
        }
        if (projectedFV != null && inflationAdjTarget != null
                && projectedFV.compareTo(inflationAdjTarget) < 0) {
            return "AT_RISK";
        }
        return "ACTIVE";
    }

    /**
     * Converts years as a double — handles partial years safely.
     * @param days total days between today and target date
     */
    public double daysToYears(long days) {
        return days / 365.25;
    }
}
