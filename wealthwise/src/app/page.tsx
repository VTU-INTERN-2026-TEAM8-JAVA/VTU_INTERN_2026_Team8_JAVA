import { createClient } from "@/lib/supabase/server";
import Link from "next/link";
import { redirect } from "next/navigation";

export default async function Home() {
  const supabase = await createClient();
  const {
    data: { user },
  } = await supabase.auth.getUser();

  if (user) {
    redirect("/dashboard");
  }

  return (
    <main className="min-h-screen bg-[radial-gradient(circle_at_top_left,_rgba(21,128,61,0.2),_transparent_24%),radial-gradient(circle_at_bottom_right,_rgba(245,158,11,0.18),_transparent_28%),linear-gradient(180deg,#fbfffc_0%,#edf8ef_100%)] px-4 py-6 sm:px-6">
      <div className="mx-auto flex min-h-[calc(100vh-3rem)] max-w-7xl flex-col rounded-[32px] border border-white/70 bg-white/75 p-6 shadow-[0_30px_120px_rgba(15,23,42,0.09)] backdrop-blur md:p-8">
        <header className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
          <div className="flex items-center gap-3">
            <div className="flex h-12 w-12 items-center justify-center rounded-2xl bg-emerald-700 text-sm font-bold text-white">
              WW
            </div>
            <div>
              <p className="text-lg font-semibold text-slate-900">WealthWise</p>
              <p className="text-sm text-slate-500">Mutual fund planning for real goals</p>
            </div>
          </div>
          <div className="flex gap-3">
            <Link
              href="/login"
              className="rounded-2xl border border-slate-200 px-5 py-3 text-sm font-semibold text-slate-700 transition hover:bg-slate-50"
            >
              Sign in
            </Link>
            <Link
              href="/signup"
              className="rounded-2xl bg-emerald-700 px-5 py-3 text-sm font-semibold text-white transition hover:bg-emerald-800"
            >
              Create account
            </Link>
          </div>
        </header>

        <div className="grid flex-1 items-center gap-8 py-10 lg:grid-cols-[1.15fr,0.85fr]">
          <section>
            <p className="text-sm font-medium uppercase tracking-[0.28em] text-emerald-700">
              Portfolio operating system
            </p>
            <h1 className="mt-5 max-w-3xl text-5xl font-semibold leading-tight text-slate-900 sm:text-6xl">
              Track investments, tie them to goals, and see what to do next.
            </h1>
            <p className="mt-5 max-w-2xl text-lg leading-8 text-slate-600">
              WealthWise brings portfolio summaries, goal planning, analytics, and
              alerts into one clean workflow for mutual fund investors.
            </p>

            <div className="mt-8 flex flex-wrap gap-4">
              <FeaturePill label="Portfolio dashboard" />
              <FeaturePill label="Goal planning" />
              <FeaturePill label="Analytics snapshots" />
              <FeaturePill label="Actionable alerts" />
            </div>
          </section>

          <section className="grid gap-4">
            <HeroCard
              eyebrow="Today"
              title="Current value"
              value="Rs 12.8L"
              accent="from-emerald-500 to-emerald-700"
            />
            <div className="grid gap-4 sm:grid-cols-2">
              <HeroCard
                eyebrow="Goals on track"
                title="Retirement corpus"
                value="28%"
                accent="from-rose-500 to-amber-500"
              />
              <HeroCard
                eyebrow="Monthly SIP"
                title="Planned outflow"
                value="Rs 13K"
                accent="from-sky-500 to-indigo-600"
              />
            </div>
          </section>
        </div>
      </div>
    </main>
  );
}

function FeaturePill({ label }: { label: string }) {
  return (
    <span className="rounded-full border border-emerald-100 bg-emerald-50 px-4 py-2 text-sm font-medium text-emerald-800">
      {label}
    </span>
  );
}

function HeroCard({
  eyebrow,
  title,
  value,
  accent,
}: {
  eyebrow: string;
  title: string;
  value: string;
  accent: string;
}) {
  return (
    <div className="rounded-[28px] bg-slate-900 p-6 text-white shadow-[0_24px_80px_rgba(15,23,42,0.18)]">
      <div className={`h-1.5 w-24 rounded-full bg-gradient-to-r ${accent}`} />
      <p className="mt-5 text-sm text-slate-400">{eyebrow}</p>
      <h2 className="mt-2 text-xl font-medium">{title}</h2>
      <p className="mt-6 text-4xl font-semibold">{value}</p>
    </div>
  );
}
