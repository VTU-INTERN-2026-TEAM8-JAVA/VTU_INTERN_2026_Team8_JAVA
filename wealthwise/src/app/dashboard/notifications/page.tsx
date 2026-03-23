"use client";

import { useEffect, useState } from "react";
import { createClient } from "@/lib/supabase/client";
import { formatDate } from "@/lib/format";
import type { UserAlert } from "@/lib/planning";

const badgeStyles = {
  Info: "bg-sky-100 text-sky-800",
  Watch: "bg-amber-100 text-amber-800",
  Action: "bg-rose-100 text-rose-800",
} as const;

const supabase = createClient();

export default function NotificationsPage() {
  const [alerts, setAlerts] = useState<UserAlert[]>([]);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    async function loadAlerts() {
      const { data, error } = await supabase
        .from("user_alerts")
        .select("*")
        .order("is_read", { ascending: true })
        .order("created_at", { ascending: false });

      if (error) {
        setError(error.message);
      } else {
        setAlerts((data as UserAlert[]) ?? []);
      }
    }

    loadAlerts();
  }, []);

  async function markReviewed(id: string) {
    const { error } = await supabase
      .from("user_alerts")
      .update({ is_read: true })
      .eq("id", id);

    if (error) {
      setError(error.message);
      return;
    }

    setAlerts((current) =>
      current.map((alert) => (alert.id === id ? { ...alert, is_read: true } : alert)),
    );
  }

  return (
    <section className="space-y-6">
      <header className="rounded-[28px] bg-white p-7 shadow-[0_24px_70px_rgba(15,23,42,0.06)]">
        <p className="text-sm font-medium uppercase tracking-[0.24em] text-sky-700">
          Alerts
        </p>
        <h1 className="mt-2 text-3xl font-semibold text-slate-900">
          Surface important portfolio reminders and goal gaps
        </h1>
      </header>

      {error ? (
        <div className="rounded-2xl bg-rose-50 px-4 py-3 text-sm text-rose-700">{error}</div>
      ) : null}

      <div className="space-y-4">
        {alerts.length === 0 ? (
          <div className="rounded-2xl bg-slate-50 px-4 py-3 text-sm text-slate-600">
            No alerts yet. New investments and goals will generate actionable reminders here.
          </div>
        ) : null}
        {alerts.map((alert) => (
          <article
            key={alert.id}
            className={`rounded-[28px] bg-white p-6 shadow-[0_24px_70px_rgba(15,23,42,0.06)] ${
              alert.is_read ? "opacity-70" : ""
            }`}
          >
            <div className="flex flex-col gap-3 sm:flex-row sm:items-start sm:justify-between">
              <div>
                <div className="flex flex-wrap items-center gap-3">
                  <span
                    className={`rounded-full px-3 py-1 text-xs font-semibold ${badgeStyles[alert.severity]}`}
                  >
                    {alert.severity}
                  </span>
                  <span className="text-sm text-slate-500">{formatDate(alert.created_at)}</span>
                </div>
                <h2 className="mt-3 text-xl font-semibold text-slate-900">{alert.title}</h2>
                <p className="mt-2 max-w-3xl text-sm text-slate-600">{alert.description}</p>
              </div>
              <button
                onClick={() => void markReviewed(alert.id)}
                disabled={alert.is_read}
                className="rounded-2xl border border-slate-200 px-4 py-2 text-sm font-medium text-slate-700 transition hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-60"
              >
                {alert.is_read ? "Reviewed" : "Mark reviewed"}
              </button>
            </div>
          </article>
        ))}
      </div>
    </section>
  );
}
