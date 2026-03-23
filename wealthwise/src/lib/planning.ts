export type InvestmentPlan = {
  id: string;
  user_id: string;
  fund_name: string;
  amc: string;
  category: string;
  investment_mode: "SIP" | "Lumpsum";
  amount: number;
  frequency: string;
  start_date: string | null;
  note: string | null;
  risk_profile: "Low" | "Moderate" | "High";
  created_at: string;
};

export type FinancialGoal = {
  id: string;
  user_id: string;
  name: string;
  target_amount: number;
  invested_amount: number;
  target_date: string;
  priority: "Essential" | "Important" | "Aspirational";
  expected_return: number;
  monthly_need: number;
  created_at: string;
};

export type UserAlert = {
  id: string;
  user_id: string;
  title: string;
  description: string;
  severity: "Info" | "Watch" | "Action";
  is_read: boolean;
  source_type: string | null;
  source_id: string | null;
  created_at: string;
};

export function calculateMonthlyNeed(
  targetAmount: number,
  investedAmount: number,
  targetDate: string,
  expectedReturn: number,
): number {
  const remaining = Math.max(targetAmount - investedAmount, 0);
  if (remaining === 0) {
    return 0;
  }

  const today = new Date();
  const end = new Date(targetDate);
  const months = Math.max(
    (end.getFullYear() - today.getFullYear()) * 12 +
      (end.getMonth() - today.getMonth()),
    1,
  );

  const monthlyRate = expectedReturn / 100 / 12;
  if (monthlyRate <= 0) {
    return remaining / months;
  }

  const growthFactor = (Math.pow(1 + monthlyRate, months) - 1) / monthlyRate;
  if (growthFactor <= 0) {
    return remaining / months;
  }

  return remaining / growthFactor;
}

export function getGoalProgress(goal: FinancialGoal): number {
  if (goal.target_amount <= 0) {
    return 0;
  }

  return (goal.invested_amount / goal.target_amount) * 100;
}
