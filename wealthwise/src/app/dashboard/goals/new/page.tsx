"use client";

import { useState, type FormEvent } from "react";
import { useRouter } from "next/navigation";
import { createClient } from "@/lib/supabase/client";
import { calculateMonthlyNeed } from "@/lib/planning";

export default function NewGoalPage() {
  const router = useRouter();
  const supabase = createClient();
  const [form, setForm] = useState({
    goalName: "",
    targetAmount: "",
    targetDate: "",
    investedAmount: "",
    priority: "Important",
    expectedReturn: "12",
  });
  const [created, setCreated] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setSubmitting(true);
    setError(null);

    const {
      data: { user },
    } = await supabase.auth.getUser();

    if (!user) {
      setError("Please sign in again before saving a goal.");
      setSubmitting(false);
      return;
    }

    const targetAmount = Number(form.targetAmount);
    const investedAmount = Number(form.investedAmount || 0);
    const expectedReturn = Number(form.expectedReturn || 0);

    if (!form.goalName || !form.targetDate || !targetAmount) {
      setError("Goal name, target amount, and target date are required.");
      setSubmitting(false);
      return;
    }

    const monthlyNeed = calculateMonthlyNeed(
      targetAmount,
      investedAmount,
      form.targetDate,
      expectedReturn,
    );

    const { data, error } = await supabase
      .from("financial_goals")
      .insert({
        user_id: user.id,
        name: form.goalName,
        target_amount: targetAmount,
        invested_amount: investedAmount,
        target_date: form.targetDate,
        priority: form.priority,
        expected_return: expectedReturn,
        monthly_need: monthlyNeed,
      })
      .select("id")
      .single();

    if (error) {
      setError(error.message);
      setSubmitting(false);
      return;
    }

    await supabase.from("user_alerts").insert({
      user_id: user.id,
      title: "Goal created",
      description: `${form.goalName} has been added with a monthly requirement of Rs ${Math.round(monthlyNeed).toLocaleString("en-IN")}.`,
      severity: monthlyNeed > 20000 ? "Action" : "Info",
      source_type: "financial_goal",
      source_id: data.id,
    });

    setCreated(true);
    setSubmitting(false);
    router.push("/dashboard/goals");
  }

  return (
    <section className="space-y-6">
      <header className="rounded-[28px] bg-white p-7 shadow-[0_24px_70px_rgba(15,23,42,0.06)]">
        <p className="text-sm font-medium uppercase tracking-[0.24em] text-rose-700">
          New goal
        </p>
        <h1 className="mt-2 text-3xl font-semibold text-slate-900">
          Define a goal and the contribution pace needed
        </h1>
      </header>

      <form
        onSubmit={handleSubmit}
        className="grid gap-6 rounded-[28px] bg-white p-7 shadow-[0_24px_70px_rgba(15,23,42,0.06)] lg:grid-cols-2"
      >
        <Field
          label="Goal name"
          placeholder="Child education fund"
          value={form.goalName}
          onChange={(value) => setForm((current) => ({ ...current, goalName: value }))}
        />
        <Field
          label="Target amount"
          type="number"
          placeholder="2500000"
          value={form.targetAmount}
          onChange={(value) => setForm((current) => ({ ...current, targetAmount: value }))}
        />
        <Field
          label="Target date"
          type="date"
          value={form.targetDate}
          onChange={(value) => setForm((current) => ({ ...current, targetDate: value }))}
        />
        <Field
          label="Current invested amount"
          type="number"
          placeholder="500000"
          value={form.investedAmount}
          onChange={(value) => setForm((current) => ({ ...current, investedAmount: value }))}
        />
        <SelectField
          label="Priority"
          value={form.priority}
          options={["Essential", "Important", "Aspirational"]}
          onChange={(value) => setForm((current) => ({ ...current, priority: value }))}
        />
        <Field
          label="Expected annual return (%)"
          type="number"
          placeholder="12"
          value={form.expectedReturn}
          onChange={(value) => setForm((current) => ({ ...current, expectedReturn: value }))}
        />

        <div className="lg:col-span-2 flex flex-wrap gap-3">
          <button
            type="submit"
            disabled={submitting}
            className="rounded-2xl bg-rose-700 px-5 py-3 text-sm font-semibold text-white transition hover:bg-rose-800"
          >
            {submitting ? "Saving..." : "Save goal"}
          </button>
          <button
            type="button"
            onClick={() => router.push("/dashboard/goals")}
            className="rounded-2xl border border-slate-200 px-5 py-3 text-sm font-semibold text-slate-700 transition hover:border-slate-300 hover:bg-slate-50"
          >
            Back to goals
          </button>
        </div>

        {created ? (
          <div className="lg:col-span-2 rounded-3xl bg-rose-50 p-4 text-sm text-rose-900">
            Goal saved successfully.
          </div>
        ) : null}
        {error ? (
          <div className="lg:col-span-2 rounded-3xl bg-rose-50 p-4 text-sm text-rose-900">
            {error}
          </div>
        ) : null}
      </form>
    </section>
  );
}

function Field({
  label,
  placeholder,
  type = "text",
  value,
  onChange,
}: {
  label: string;
  placeholder?: string;
  type?: string;
  value: string;
  onChange: (value: string) => void;
}) {
  return (
    <div>
      <label className="mb-2 block text-sm font-medium text-slate-700">{label}</label>
      <input
        type={type}
        placeholder={placeholder}
        value={value}
        onChange={(event) => onChange(event.target.value)}
        className="w-full rounded-2xl border border-slate-200 px-4 py-3 text-sm text-slate-900 outline-none transition focus:border-rose-500"
      />
    </div>
  );
}

function SelectField({
  label,
  value,
  options,
  onChange,
}: {
  label: string;
  value: string;
  options: string[];
  onChange: (value: string) => void;
}) {
  return (
    <div>
      <label className="mb-2 block text-sm font-medium text-slate-700">{label}</label>
      <select
        value={value}
        onChange={(event) => onChange(event.target.value)}
        className="w-full rounded-2xl border border-slate-200 px-4 py-3 text-sm text-slate-900 outline-none transition focus:border-rose-500"
      >
        {options.map((option) => (
          <option key={option} value={option}>
            {option}
          </option>
        ))}
      </select>
    </div>
  );
}
