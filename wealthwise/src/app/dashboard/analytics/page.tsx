"use client";

import { useEffect, useMemo, useState } from "react";
import { createClient } from "@/lib/supabase/client";
import { formatCompactCurrency, formatPercent } from "@/lib/format";
import { getGoalProgress, type FinancialGoal, type InvestmentPlan } from "@/lib/planning";

const supabase = createClient();

export default function AnalyticsPage() {
  const [goals, setGoals] = useState<FinancialGoal[]>([]);
  const [investments, setInvestments] = useState<InvestmentPlan[]>([]);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    async function loadAnalytics() {
      const [{ data: goalsData, error: goalsError }, { data: plansData, error: plansError }] =
        await Promise.all([
          supabase.from("financial_goals").select("*"),
          supabase.from("investment_plans").select("*"),
        ]);

      if (goalsError || plansError) {
        setError(goalsError?.message || plansError?.message || "Failed to load analytics.");
        return;
      }

      setGoals((goalsData as FinancialGoal[]) ?? []);
      setInvestments((plansData as InvestmentPlan[]) ?? []);
    }

    loadAnalytics();
  }, []);

  const sipCommitment = useMemo(
    () =>
      investments
        .filter((item) => item.investment_mode === "SIP")
        .reduce((sum, item) => sum + item.amount, 0),
    [investments],
  );

  const goalCoverage = useMemo(() => {
    const invested = goals.reduce((sum, goal) => sum + goal.invested_amount, 0);
    const target = goals.reduce((sum, goal) => sum + goal.target_amount, 0);
    return target > 0 ? invested / target : 0;
  }, [goals]);

  const lumpsumAmount = useMemo(
    () =>
      investments
        .filter((item) => item.investment_mode === "Lumpsum")
        .reduce((sum, item) => sum + item.amount, 0),
    [investments],
  );

  const analytics = [
    {
      label: "Lumpsum capital",
      value: formatCompactCurrency(lumpsumAmount),
      tone: "emerald",
      summary: "Total one-time capital committed through saved investment plans.",
    },
    {
      label: "Monthly commitment",
      value: formatCompactCurrency(sipCommitment),
      tone: "amber",
      summary: "Recurring SIP outflow currently planned.",
    },
    {
      label: "Goal coverage",
      value: formatPercent(goalCoverage * 100),
      tone: "sky",
      summary: "Share of target corpus already funded.",
    },
  ];

  const toneClasses: Record<string, string> = {
    emerald: "from-emerald-500/15 to-emerald-50 text-emerald-900",
    amber: "from-amber-500/15 to-amber-50 text-amber-900",
    sky: "from-sky-500/15 to-sky-50 text-sky-900",
  };

  return (
    <section className="space-y-6">
      <header className="rounded-[28px] bg-white p-7 shadow-[0_24px_70px_rgba(15,23,42,0.06)]">
        <p className="text-sm font-medium uppercase tracking-[0.24em] text-amber-700">
          Analytics
        </p>
        <h1 className="mt-2 text-3xl font-semibold text-slate-900">
          Snapshot of portfolio momentum and goal efficiency
        </h1>
      </header>

      {error ? (
        <div className="rounded-2xl bg-rose-50 px-4 py-3 text-sm text-rose-700">{error}</div>
      ) : null}

      <div className="grid gap-5 lg:grid-cols-3">
        {analytics.map((item) => (
          <article
            key={item.label}
            className={`rounded-[28px] bg-gradient-to-br p-6 shadow-[0_24px_70px_rgba(15,23,42,0.06)] ${toneClasses[item.tone]}`}
          >
            <p className="text-sm font-medium">{item.label}</p>
            <p className="mt-3 text-3xl font-semibold">{item.value}</p>
            <p className="mt-3 text-sm opacity-80">{item.summary}</p>
          </article>
        ))}
      </div>

      <div className="grid gap-6 xl:grid-cols-[1.4fr,1fr]">
        <div className="rounded-[28px] bg-white p-7 shadow-[0_24px_70px_rgba(15,23,42,0.06)]">
          <h2 className="text-xl font-semibold text-slate-900">Goal funding pressure</h2>
          <div className="mt-5 space-y-4">
            {goals.map((goal) => {
              const ratio = getGoalProgress(goal);
              return (
                <div key={goal.id}>
                  <div className="mb-2 flex items-center justify-between">
                    <span className="text-sm font-medium text-slate-700">{goal.name}</span>
                    <span className="text-sm text-slate-500">{ratio.toFixed(1)}%</span>
                  </div>
                  <div className="h-3 overflow-hidden rounded-full bg-slate-100">
                    <div
                      className="h-full rounded-full bg-gradient-to-r from-amber-500 to-rose-500"
                      style={{ width: `${Math.min(ratio, 100)}%` }}
                    />
                  </div>
                </div>
              );
            })}
          </div>
        </div>

        <div className="rounded-[28px] bg-white p-7 shadow-[0_24px_70px_rgba(15,23,42,0.06)]">
          <h2 className="text-xl font-semibold text-slate-900">What to do next</h2>
          <ul className="mt-5 space-y-4 text-sm text-slate-600">
            {goals.length === 0 && investments.length === 0 ? (
              <li className="rounded-2xl bg-slate-50 p-4">
                Add at least one investment or goal to unlock personalized analytics.
              </li>
            ) : null}
            {goals
              .filter((goal) => goal.monthly_need > 0)
              .sort((left, right) => right.monthly_need - left.monthly_need)
              .slice(0, 2)
              .map((goal) => (
                <li key={goal.id} className="rounded-2xl bg-slate-50 p-4">
                  {goal.name} needs about {formatCompactCurrency(goal.monthly_need)} per month to stay on track.
                </li>
              ))}
            {investments.filter((item) => item.investment_mode === "Lumpsum").length > 0 ? (
              <li className="rounded-2xl bg-slate-50 p-4">
                Review your lumpsum-heavy positions to make sure they still match the planned risk profile.
              </li>
            ) : null}
          </ul>
        </div>
      </div>
    </section>
  );
}
