"use client";

import { useEffect, useState } from "react";
import { createClient } from "@/lib/supabase/client";
import { formatCurrency, formatDate, formatPercent } from "@/lib/format";
import { getGoalProgress, type FinancialGoal } from "@/lib/planning";
import Link from "next/link";

const supabase = createClient();

export default function GoalsPage() {
  const [goals, setGoals] = useState<FinancialGoal[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    async function loadGoals() {
      const {
        data: { user },
      } = await supabase.auth.getUser();

      if (!user) {
        setError("Please sign in again to load goals.");
        setLoading(false);
        return;
      }

      const { data, error } = await supabase
        .from("financial_goals")
        .select("*")
        .order("created_at", { ascending: false });

      if (error) {
        setError(error.message);
      } else {
        setGoals((data as FinancialGoal[]) ?? []);
      }

      setLoading(false);
    }

    loadGoals();
  }, []);

  return (
    <section className="space-y-6">
      <header className="rounded-[28px] bg-white p-7 shadow-[0_24px_70px_rgba(15,23,42,0.06)]">
        <div className="flex flex-col gap-4 lg:flex-row lg:items-end lg:justify-between">
          <div>
            <p className="text-sm font-medium uppercase tracking-[0.24em] text-rose-700">
              Goals
            </p>
            <h1 className="mt-2 text-3xl font-semibold text-slate-900">
              Turn long-term plans into contribution targets
            </h1>
            <p className="mt-2 max-w-2xl text-slate-600">
              Each goal shows current progress, target timelines, and the monthly amount needed to stay on course.
            </p>
          </div>
          <Link
            href="/dashboard/goals/new"
            className="inline-flex items-center justify-center rounded-2xl bg-rose-700 px-5 py-3 text-sm font-semibold text-white transition hover:bg-rose-800"
          >
            Create goal
          </Link>
        </div>
      </header>

      <div className="grid gap-5 xl:grid-cols-3">
        {loading ? <StateBox message="Loading goals..." /> : null}
        {error ? <StateBox message={error} tone="error" /> : null}
        {!loading && !error && goals.length === 0 ? (
          <StateBox message="No goals have been created yet." />
        ) : null}
        {goals.map((goal) => {
          const progress = getGoalProgress(goal);

          return (
            <article
              key={goal.id}
              className="rounded-[28px] bg-white p-6 shadow-[0_24px_70px_rgba(15,23,42,0.06)]"
            >
              <div className="flex items-center justify-between">
                <span className="rounded-full bg-rose-100 px-3 py-1 text-xs font-semibold text-rose-800">
                  {goal.priority}
                </span>
                <span className="text-sm text-slate-500">{formatDate(goal.target_date)}</span>
              </div>
              <h2 className="mt-4 text-2xl font-semibold text-slate-900">{goal.name}</h2>
              <p className="mt-2 text-sm text-slate-600">
                Target corpus {formatCurrency(goal.target_amount)}
              </p>

              <div className="mt-5 h-3 overflow-hidden rounded-full bg-slate-100">
                <div
                  className="h-full rounded-full bg-gradient-to-r from-rose-500 to-amber-400"
                  style={{ width: `${Math.min(progress, 100)}%` }}
                />
              </div>

              <div className="mt-5 grid gap-4 sm:grid-cols-2">
                <GoalData label="Progress" value={formatPercent(progress)} />
                <GoalData label="Invested" value={formatCurrency(goal.invested_amount)} />
                <GoalData label="Monthly need" value={formatCurrency(goal.monthly_need)} />
                <GoalData
                  label="Remaining gap"
                  value={formatCurrency(goal.target_amount - goal.invested_amount)}
                />
              </div>
            </article>
          );
        })}
      </div>
    </section>
  );
}

function StateBox({
  message,
  tone = "default",
}: {
  message: string;
  tone?: "default" | "error";
}) {
  return (
    <div
      className={`xl:col-span-3 rounded-2xl px-4 py-3 text-sm ${
        tone === "error" ? "bg-rose-50 text-rose-700" : "bg-slate-50 text-slate-600"
      }`}
    >
      {message}
    </div>
  );
}

function GoalData({ label, value }: { label: string; value: string }) {
  return (
    <div className="rounded-2xl bg-slate-50 p-4">
      <p className="text-xs uppercase tracking-[0.18em] text-slate-400">{label}</p>
      <p className="mt-1 text-sm font-semibold text-slate-900">{value}</p>
    </div>
  );
}
