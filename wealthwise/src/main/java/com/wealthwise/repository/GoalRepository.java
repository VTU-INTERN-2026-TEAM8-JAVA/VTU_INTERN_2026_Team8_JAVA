package com.wealthwise.repository;

import com.wealthwise.model.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface GoalRepository extends JpaRepository<Goal, Long> {

    /** Fetch all goals for a user, newest first. */
    List<Goal> findByUserIdOrderByCreatedAtDesc(Long userId);

    /** Fetch goals for a user filtered by status. */
    List<Goal> findByUserIdAndStatus(Long userId, String status);

    /** Count goals by user. */
    long countByUserId(Long userId);

    /** Sum of all currentAmounts for a user's goals. */
    @Query("SELECT COALESCE(SUM(g.currentAmount), 0) FROM Goal g WHERE g.userId = :userId")
    BigDecimal sumCurrentAmountByUserId(@Param("userId") Long userId);

    /** Sum of all targetAmounts for a user's goals. */
    @Query("SELECT COALESCE(SUM(g.targetAmount), 0) FROM Goal g WHERE g.userId = :userId")
    BigDecimal sumTargetAmountByUserId(@Param("userId") Long userId);
}
