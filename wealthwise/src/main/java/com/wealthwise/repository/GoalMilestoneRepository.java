package com.wealthwise.repository;

import com.wealthwise.model.GoalMilestone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GoalMilestoneRepository extends JpaRepository<GoalMilestone, Long> {

    List<GoalMilestone> findByGoalIdOrderByTargetDateAsc(Long goalId);

    long countByGoalId(Long goalId);

    long countByGoalIdAndIsAchievedTrue(Long goalId);
}
