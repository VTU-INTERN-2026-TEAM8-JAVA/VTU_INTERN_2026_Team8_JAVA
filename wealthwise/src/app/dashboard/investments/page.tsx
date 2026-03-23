"use client";

import { useEffect, useState } from "react";
import { createClient } from "@/lib/supabase/client";
import { formatCompactCurrency, formatCurrency } from "@/lib/format";
import type { InvestmentPlan } from "@/lib/planning";
import Link from "next/link";

const supabase = createClient();

export default function InvestmentsPage() {
  const [investments, setInvestments] = useState<InvestmentPlan[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    async function loadInvestments() {
      const {
        data: { user },
      } = await supabase.auth.getUser();

      if (!user) {
        setLoading(false);
        setError("Please sign in again to load investments.");
        return;
      }

      const { data, error } = await supabase
        .from("investment_plans")
        .select("*")
        .order("created_at", { ascending: false });

      if (error) {
        setError(error.message);
      } else {
        setInvestments((data as InvestmentPlan[]) ?? []);
      }

      setLoading(false);
    }

    loadInvestments();
  }, []);

  const monthlySip = investments
    .filter((item) => item.investment_mode === "SIP")
    .reduce((sum, item) => sum + item.amount, 0);

  const normalizedInvestments = investments.map((item) => ({
    ...item,
    mode: item.investment_mode,
    risk: item.risk_profile,
  }));

  return (
    <section className="space-y-6">
      <header className="rounded-[28px] bg-white p-7 shadow-[0_24px_70px_rgba(15,23,42,0.06)]">
        <div className="flex flex-col gap-4 lg:flex-row lg:items-end lg:justify-between">
          <div>
            <p className="text-sm font-medium uppercase tracking-[0.24em] text-emerald-700">
              Investments
            </p>
            <h1 className="mt-2 text-3xl font-semibold text-slate-900">
              Manage active SIPs and planned contributions
            </h1>
            <p className="mt-2 max-w-2xl text-slate-600">
              Save and review actual investment plans tied to the signed-in account.
            </p>
          </div>
          <Link
            href="/dashboard/investments/new"
            className="inline-flex items-center justify-center rounded-2xl bg-emerald-700 px-5 py-3 text-sm font-semibold text-white transition hover:bg-emerald-800"
          >
            Add investment
          </Link>
        </div>
      </header>

      <div className="grid gap-4 md:grid-cols-3">
        <MetricCard label="Active plans" value={String(investments.length)} />
        <MetricCard label="Monthly SIP outflow" value={formatCompactCurrency(monthlySip)} />
        <MetricCard
          label="Largest one-time order"
          value={formatCompactCurrency(
            normalizedInvestments.length > 0
              ? Math.max(...normalizedInvestments.map((item) => item.amount))
              : 0,
          )}
        />
      </div>

      <div className="rounded-[28px] bg-white p-7 shadow-[0_24px_70px_rgba(15,23,42,0.06)]">
        <div className="mb-5">
          <h2 className="text-xl font-semibold text-slate-900">Saved investments</h2>
          <p className="text-sm text-slate-500">
            These rows are loaded from Supabase for the signed-in user.
          </p>
        </div>

        {loading ? <StateBox message="Loading investments..." /> : null}
        {error ? <StateBox message={error} tone="error" /> : null}

        <div className="space-y-4">
          {!loading && !error && normalizedInvestments.length === 0 ? (
            <StateBox message="No investment plans saved yet." />
          ) : null}

          {normalizedInvestments.map((item) => (
            <div
              key={item.id}
              className="rounded-3xl border border-slate-100 bg-slate-50 p-5"
            >
              <div className="flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
                <div>
                  <div className="flex flex-wrap gap-2">
                    <span className="rounded-full bg-emerald-100 px-3 py-1 text-xs font-semibold text-emerald-800">
                      {item.category}
                    </span>
                    <span className="rounded-full bg-amber-100 px-3 py-1 text-xs font-semibold text-amber-800">
                      {item.mode}
                    </span>
                    <span className="rounded-full bg-slate-200 px-3 py-1 text-xs font-semibold text-slate-700">
                      {item.risk} risk
                    </span>
                  </div>
                  <h3 className="mt-3 text-lg font-semibold text-slate-900">{item.fund_name}</h3>
                  <p className="text-sm text-slate-500">
                    {item.frequency} contribution planned for {formatCurrency(item.amount)}
                  </p>
                </div>

                <div className="grid grid-cols-2 gap-4 text-sm sm:grid-cols-3">
                  <DataPoint label="Ticket size" value={formatCurrency(item.amount)} />
                  <DataPoint label="Frequency" value={item.frequency} />
                  <DataPoint label="Suitability" value={item.risk} />
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}

function MetricCard({ label, value }: { label: string; value: string }) {
  return (
    <div className="rounded-[24px] bg-white p-5 shadow-[0_24px_70px_rgba(15,23,42,0.06)]">
      <p className="text-sm text-slate-500">{label}</p>
      <p className="mt-2 text-2xl font-semibold text-slate-900">{value}</p>
    </div>
  );
}

function DataPoint({ label, value }: { label: string; value: string }) {
  return (
    <div>
      <p className="text-xs uppercase tracking-[0.18em] text-slate-400">{label}</p>
      <p className="mt-1 font-semibold text-slate-900">{value}</p>
    </div>
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
      className={`mb-4 rounded-2xl px-4 py-3 text-sm ${
        tone === "error" ? "bg-rose-50 text-rose-700" : "bg-slate-50 text-slate-600"
      }`}
    >
      {message}
    </div>
  );
}
