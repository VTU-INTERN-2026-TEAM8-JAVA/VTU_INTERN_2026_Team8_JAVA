"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { createClient } from "@/lib/supabase/client";
import { formatCompactCurrency, formatPercent } from "@/lib/format";

const supabase = createClient();

interface PortfolioSummary {
  total_invested: number;
  current_value: number;
  total_gain: number;
  gain_pct: number;
  fund_count: number;
  active_sips: number;
}

interface FundPerformance {
  fund_id: string;
  fund_name: string;
  category: string;
  amc: string;
  total_units: number;
  current_value: number;
  gain_pct: number;
}

interface AllocationItem {
  category: string;
  current_value: number;
  pct: number;
}

export default function DashboardPage() {
  const router = useRouter();

  const [summary, setSummary] = useState<PortfolioSummary | null>(null);
  const [funds, setFunds] = useState<FundPerformance[]>([]);
  const [allocation, setAllocation] = useState<AllocationItem[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function loadDashboard() {
      try {
        const {
          data: { user },
        } = await supabase.auth.getUser();

        if (!user) {
          router.push("/login");
          return;
        }

        const [summaryResult, fundResult, allocationResult] = await Promise.all([
          supabase.rpc("get_portfolio_summary", { p_user_id: user.id }),
          supabase.rpc("get_fund_performance", { p_user_id: user.id }),
          supabase.rpc("get_asset_allocation", { p_user_id: user.id }),
        ]);

        if (summaryResult.data) {
          const row = Array.isArray(summaryResult.data)
            ? summaryResult.data[0]
            : summaryResult.data;
          setSummary(row as PortfolioSummary);
        }

        if (Array.isArray(fundResult.data)) {
          setFunds(fundResult.data as FundPerformance[]);
        }

        if (Array.isArray(allocationResult.data)) {
          setAllocation(allocationResult.data as AllocationItem[]);
        }
      } catch (error) {
        console.error("Failed to load dashboard", error);
      } finally {
        setLoading(false);
      }
    }

    loadDashboard();
  }, [router]);

  async function handleSignOut() {
    await supabase.auth.signOut();
    router.push("/login");
  }

  if (loading) {
    return (
      <div className="flex min-h-[60vh] items-center justify-center rounded-[28px] bg-white">
        <div className="text-center">
          <div className="mx-auto h-12 w-12 animate-spin rounded-full border-4 border-emerald-600 border-t-transparent" />
          <p className="mt-4 text-sm text-slate-500">Loading portfolio overview...</p>
        </div>
      </div>
    );
  }

  const currentValue = summary?.current_value ?? 0;
  const totalInvested = summary?.total_invested ?? 0;
  const gain = summary?.total_gain ?? 0;
  const gainPct = summary?.gain_pct ?? 0;
  const fundCount = summary?.fund_count ?? 0;
  const activeSips = summary?.active_sips ?? 0;

  return (
    <section className="space-y-6">
      <header className="rounded-[28px] bg-white p-7 shadow-[0_24px_70px_rgba(15,23,42,0.06)]">
        <div className="flex flex-col gap-4 lg:flex-row lg:items-end lg:justify-between">
          <div>
            <p className="text-sm font-medium uppercase tracking-[0.24em] text-emerald-700">
              Overview
            </p>
            <h1 className="mt-2 text-3xl font-semibold text-slate-900">
              Portfolio dashboard
            </h1>
            <p className="mt-2 max-w-2xl text-slate-600">
              Your authenticated overview is now the hub for the remaining product areas.
            </p>
          </div>
          <button
            onClick={handleSignOut}
            className="rounded-2xl border border-slate-200 px-5 py-3 text-sm font-semibold text-slate-700 transition hover:border-slate-300 hover:bg-slate-50"
          >
            Sign out
          </button>
        </div>
      </header>

      <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
        <MetricCard label="Current value" value={formatCompactCurrency(currentValue)} />
        <MetricCard label="Total invested" value={formatCompactCurrency(totalInvested)} />
        <MetricCard label="Portfolio gain" value={formatCompactCurrency(gain)} />
        <MetricCard label="Return" value={formatPercent(gainPct)} />
      </div>

      <div className="grid gap-6 xl:grid-cols-[1.3fr,0.9fr]">
        <div className="rounded-[28px] bg-white p-7 shadow-[0_24px_70px_rgba(15,23,42,0.06)]">
          <div className="flex items-center justify-between">
            <div>
              <h2 className="text-xl font-semibold text-slate-900">Top holdings</h2>
              <p className="text-sm text-slate-500">Highest current value across your funds.</p>
            </div>
            <Link href="/dashboard/investments" className="text-sm font-semibold text-emerald-700">
              View all
            </Link>
          </div>

          <div className="mt-5 space-y-4">
            {funds.length === 0 ? (
              <EmptyState message="No holding data returned yet. The overview is ready and waiting on portfolio rows." />
            ) : (
              funds.slice(0, 4).map((fund) => (
                <div key={fund.fund_id} className="rounded-3xl border border-slate-100 bg-slate-50 p-5">
                  <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
                    <div>
                      <p className="text-lg font-semibold text-slate-900">{fund.fund_name}</p>
                      <p className="text-sm text-slate-500">
                        {fund.amc} | {fund.category} | {fund.total_units.toFixed(3)} units
                      </p>
                    </div>
                    <div className="text-left sm:text-right">
                      <p className="text-base font-semibold text-slate-900">
                        {formatCompactCurrency(fund.current_value)}
                      </p>
                      <p className={`text-sm font-semibold ${fund.gain_pct >= 0 ? "text-emerald-700" : "text-rose-700"}`}>
                        {formatPercent(fund.gain_pct)}
                      </p>
                    </div>
                  </div>
                </div>
              ))
            )}
          </div>
        </div>

        <div className="space-y-6">
          <div className="rounded-[28px] bg-white p-7 shadow-[0_24px_70px_rgba(15,23,42,0.06)]">
            <h2 className="text-xl font-semibold text-slate-900">Health snapshot</h2>
            <div className="mt-5 space-y-3">
              <HealthRow label="Funds tracked" value={String(fundCount)} />
              <HealthRow label="Active SIPs" value={String(activeSips)} />
              <HealthRow label="Allocation buckets" value={String(allocation.length)} />
            </div>
          </div>

          <div className="rounded-[28px] bg-white p-7 shadow-[0_24px_70px_rgba(15,23,42,0.06)]">
            <h2 className="text-xl font-semibold text-slate-900">Quick jump</h2>
            <div className="mt-5 grid gap-3">
              <QuickLink href="/dashboard/investments/new" label="Add investment" />
              <QuickLink href="/dashboard/goals/new" label="Create goal" />
              <QuickLink href="/dashboard/analytics" label="Review analytics" />
              <QuickLink href="/dashboard/notifications" label="Check alerts" />
            </div>
          </div>
        </div>
      </div>

      <div className="rounded-[28px] bg-white p-7 shadow-[0_24px_70px_rgba(15,23,42,0.06)]">
        <div className="flex items-center justify-between">
          <div>
            <h2 className="text-xl font-semibold text-slate-900">Allocation mix</h2>
            <p className="text-sm text-slate-500">Category weights based on the current portfolio snapshot.</p>
          </div>
        </div>
        <div className="mt-5 space-y-4">
          {allocation.length === 0 ? (
            <EmptyState message="Allocation data has not been returned yet." />
          ) : (
            allocation.map((item) => (
              <div key={item.category}>
                <div className="mb-2 flex items-center justify-between">
                  <span className="text-sm font-medium text-slate-700">{item.category}</span>
                  <span className="text-sm text-slate-500">{item.pct.toFixed(1)}%</span>
                </div>
                <div className="h-3 overflow-hidden rounded-full bg-slate-100">
                  <div
                    className="h-full rounded-full bg-gradient-to-r from-emerald-500 to-amber-400"
                    style={{ width: `${Math.min(item.pct, 100)}%` }}
                  />
                </div>
              </div>
            ))
          )}
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

function HealthRow({ label, value }: { label: string; value: string }) {
  return (
    <div className="flex items-center justify-between rounded-2xl bg-slate-50 px-4 py-3">
      <span className="text-sm text-slate-600">{label}</span>
      <span className="text-sm font-semibold text-slate-900">{value}</span>
    </div>
  );
}

function QuickLink({ href, label }: { href: string; label: string }) {
  return (
    <Link
      href={href}
      className="rounded-2xl border border-slate-200 px-4 py-3 text-sm font-semibold text-slate-700 transition hover:border-emerald-200 hover:bg-emerald-50 hover:text-emerald-800"
    >
      {label}
    </Link>
  );
}

function EmptyState({ message }: { message: string }) {
  return (
    <div className="rounded-3xl border border-dashed border-slate-200 bg-slate-50 p-6 text-sm text-slate-500">
      {message}
    </div>
  );
}
