package com.wealthwise.repository;

import com.wealthwise.model.GoalInvestmentLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface GoalInvestmentLinkRepository extends JpaRepository<GoalInvestmentLink, Long> {

    List<GoalInvestmentLink> findByGoalId(Long goalId);

    /** Sum of all monthly contributions linked to a goal. */
    @Query("SELECT COALESCE(SUM(l.monthlyAmount), 0) FROM GoalInvestmentLink l WHERE l.goalId = :goalId")
    BigDecimal sumMonthlyAmountByGoalId(@Param("goalId") Long goalId);
}
