"use client";

import { useState, type FormEvent } from "react";
import { useRouter } from "next/navigation";
import { createClient } from "@/lib/supabase/client";

export default function NewInvestmentPage() {
  const router = useRouter();
  const supabase = createClient();
  const [form, setForm] = useState({
    fundName: "",
    amc: "",
    category: "",
    investmentMode: "SIP",
    amount: "",
    frequency: "Monthly",
    startDate: "",
    note: "",
    riskProfile: "Moderate",
  });
  const [submitted, setSubmitted] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    void submitInvestment();
  }

  async function submitInvestment() {
    setSubmitting(true);
    setError(null);

    const {
      data: { user },
    } = await supabase.auth.getUser();

    if (!user) {
      setError("Please sign in again before saving an investment.");
      setSubmitting(false);
      return;
    }

    const amount = Number(form.amount);
    if (!form.fundName || !form.amc || !form.category || !amount) {
      setError("Fund name, AMC, category, and amount are required.");
      setSubmitting(false);
      return;
    }

    const { data, error } = await supabase
      .from("investment_plans")
      .insert({
        user_id: user.id,
        fund_name: form.fundName,
        amc: form.amc,
        category: form.category,
        investment_mode: form.investmentMode,
        amount,
        frequency: form.frequency,
        start_date: form.startDate || null,
        note: form.note || null,
        risk_profile: form.riskProfile,
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
      title: `${form.investmentMode} created`,
      description: `${form.fundName} was saved with an amount of Rs ${amount.toLocaleString("en-IN")}.`,
      severity: form.investmentMode === "SIP" ? "Info" : "Watch",
      source_type: "investment_plan",
      source_id: data.id,
    });

    setSubmitted(true);
    setSubmitting(false);
    router.push("/dashboard/investments");
  }

  return (
    <section className="space-y-6">
      <header className="rounded-[28px] bg-white p-7 shadow-[0_24px_70px_rgba(15,23,42,0.06)]">
        <p className="text-sm font-medium uppercase tracking-[0.24em] text-emerald-700">
          New investment
        </p>
        <h1 className="mt-2 text-3xl font-semibold text-slate-900">
          Capture a SIP or lump-sum order
        </h1>
        <p className="mt-2 max-w-2xl text-slate-600">
          The form is live in the UI and ready for Supabase insert wiring once the final schema is set.
        </p>
      </header>

      <form
        onSubmit={handleSubmit}
        className="grid gap-6 rounded-[28px] bg-white p-7 shadow-[0_24px_70px_rgba(15,23,42,0.06)] lg:grid-cols-2"
      >
        <Field
          label="Fund name"
          placeholder="ICICI Prudential Bluechip Fund"
          value={form.fundName}
          onChange={(value) => setForm((current) => ({ ...current, fundName: value }))}
        />
        <Field
          label="AMC"
          placeholder="ICICI Prudential"
          value={form.amc}
          onChange={(value) => setForm((current) => ({ ...current, amc: value }))}
        />
        <Field
          label="Category"
          placeholder="Equity / Debt / Hybrid"
          value={form.category}
          onChange={(value) => setForm((current) => ({ ...current, category: value }))}
        />
        <SelectField
          label="Investment mode"
          value={form.investmentMode}
          options={["SIP", "Lumpsum"]}
          onChange={(value) =>
            setForm((current) => ({
              ...current,
              investmentMode: value as "SIP" | "Lumpsum",
            }))
          }
        />
        <Field
          label="Amount"
          placeholder="10000"
          type="number"
          value={form.amount}
          onChange={(value) => setForm((current) => ({ ...current, amount: value }))}
        />
        <Field
          label="Frequency"
          placeholder="Monthly / One time"
          value={form.frequency}
          onChange={(value) => setForm((current) => ({ ...current, frequency: value }))}
        />
        <Field
          label="Start date"
          type="date"
          value={form.startDate}
          onChange={(value) => setForm((current) => ({ ...current, startDate: value }))}
        />
        <SelectField
          label="Risk profile"
          value={form.riskProfile}
          options={["Low", "Moderate", "High"]}
          onChange={(value) =>
            setForm((current) => ({
              ...current,
              riskProfile: value as "Low" | "Moderate" | "High",
            }))
          }
        />

        <div className="lg:col-span-2">
          <label className="mb-2 block text-sm font-medium text-slate-700">Why this fund</label>
          <textarea
            rows={4}
            placeholder="Capture risk appetite, time horizon, or advisor rationale."
            value={form.note}
            onChange={(event) =>
              setForm((current) => ({ ...current, note: event.target.value }))
            }
            className="w-full rounded-2xl border border-slate-200 px-4 py-3 text-sm text-slate-900 outline-none transition focus:border-emerald-500"
          />
        </div>

        <div className="lg:col-span-2 flex flex-wrap gap-3">
          <button
            type="submit"
            disabled={submitting}
            className="rounded-2xl bg-emerald-700 px-5 py-3 text-sm font-semibold text-white transition hover:bg-emerald-800"
          >
            {submitting ? "Saving..." : "Save investment"}
          </button>
          <button
            type="button"
            onClick={() => router.push("/dashboard/investments")}
            className="rounded-2xl border border-slate-200 px-5 py-3 text-sm font-semibold text-slate-700 transition hover:border-slate-300 hover:bg-slate-50"
          >
            Back to investments
          </button>
        </div>

        {submitted ? (
          <div className="lg:col-span-2 rounded-3xl bg-emerald-50 p-4 text-sm text-emerald-900">
            Investment saved successfully.
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
        className="w-full rounded-2xl border border-slate-200 px-4 py-3 text-sm text-slate-900 outline-none transition focus:border-emerald-500"
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
        className="w-full rounded-2xl border border-slate-200 px-4 py-3 text-sm text-slate-900 outline-none transition focus:border-emerald-500"
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
