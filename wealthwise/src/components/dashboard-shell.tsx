"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import type { ReactNode } from "react";

const navItems = [
  { href: "/dashboard", label: "Overview" },
  { href: "/dashboard/investments", label: "Investments" },
  { href: "/dashboard/goals", label: "Goals" },
  { href: "/dashboard/analytics", label: "Analytics" },
  { href: "/dashboard/notifications", label: "Alerts" },
];

type DashboardShellProps = {
  children: ReactNode;
  userLabel: string;
};

export function DashboardShell({ children, userLabel }: DashboardShellProps) {
  const pathname = usePathname();

  return (
    <div className="min-h-screen bg-[radial-gradient(circle_at_top_left,_rgba(21,128,61,0.18),_transparent_28%),radial-gradient(circle_at_top_right,_rgba(245,158,11,0.12),_transparent_24%),linear-gradient(180deg,#f7fdf8_0%,#eff8f1_100%)]">
      <div className="mx-auto flex min-h-screen max-w-7xl flex-col px-4 py-4 sm:px-6 lg:flex-row lg:gap-6 lg:px-8">
        <aside className="mb-4 rounded-[28px] border border-emerald-100 bg-white/90 p-5 shadow-[0_24px_80px_rgba(21,128,61,0.08)] backdrop-blur lg:sticky lg:top-4 lg:mb-0 lg:h-[calc(100vh-2rem)] lg:w-72">
          <div className="mb-8 flex items-center gap-3">
            <div className="flex h-12 w-12 items-center justify-center rounded-2xl bg-emerald-700 text-lg font-semibold text-white">
              WW
            </div>
            <div>
              <p className="font-semibold text-slate-900">WealthWise</p>
              <p className="text-sm text-slate-500">Smart mutual fund cockpit</p>
            </div>
          </div>

          <nav className="space-y-2">
            {navItems.map((item) => {
              const active =
                pathname === item.href || pathname.startsWith(`${item.href}/`);

              return (
                <Link
                  key={item.href}
                  href={item.href}
                  className={`block rounded-2xl px-4 py-3 text-sm font-medium transition ${
                    active
                      ? "bg-emerald-700 text-white shadow-lg shadow-emerald-700/20"
                      : "text-slate-600 hover:bg-emerald-50 hover:text-emerald-800"
                  }`}
                >
                  {item.label}
                </Link>
              );
            })}
          </nav>

          <div className="mt-8 rounded-3xl bg-slate-900 p-5 text-white">
            <p className="text-sm text-slate-300">Signed in as</p>
            <p className="mt-1 text-base font-semibold">{userLabel}</p>
            <p className="mt-4 text-sm text-slate-400">
              Track SIPs, review goals, and act on allocation changes from one place.
            </p>
          </div>
        </aside>

        <main className="flex-1">{children}</main>
      </div>
    </div>
  );
}
