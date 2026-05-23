"use client";

import { useState, useCallback } from "react";
import { PageHeader } from "@/components/ui/PageHeader";
import { Card, CardHeader, CardBody } from "@/components/ui/Card";
import { Button } from "@/components/ui/Button";
import { Input } from "@/components/ui/Input";
import { Select } from "@/components/ui/Select";
import { EmptyState } from "@/components/ui/EmptyState";
import { cn } from "@/lib/cn";
import { BarChart, Loader2, Shield } from "lucide-react";

/* ------------------------------------------------------------------ */
/*  Types                                                              */
/* ------------------------------------------------------------------ */

interface Comparable {
  addr: string;
  size: string;
  sold: string;
  when: string;
}

interface ValuationResult {
  estimate: string;
  low: string;
  high: string;
  confidence: number;
  comparables: Comparable[];
}

const DEMO_RESULT: ValuationResult = {
  estimate: "R 2,850,000",
  low: "R 2,650,000",
  high: "R 3,050,000",
  confidence: 92,
  comparables: [
    { addr: "18 Cinnebar Street", size: "590 m\u00B2", sold: "R 2,750,000", when: "2025-11" },
    { addr: "7 Coastal Lane", size: "640 m\u00B2", sold: "R 3,100,000", when: "2025-09" },
    { addr: "22 Stellenbosch Way", size: "580 m\u00B2", sold: "R 2,680,000", when: "2026-01" },
  ],
};

const CONDITIONS = [
  { value: "excellent", label: "Excellent" },
  { value: "good", label: "Good" },
  { value: "fair", label: "Fair" },
  { value: "poor", label: "Needs work" },
];

/* ------------------------------------------------------------------ */
/*  Main component                                                     */
/* ------------------------------------------------------------------ */

export function PropertyValuationPage() {
  const [form, setForm] = useState({
    id: "",
    size: "",
    bed: "",
    bath: "",
    year: "",
    condition: "good",
  });
  const [status, setStatus] = useState<"idle" | "loading" | "result">("idle");
  const [result, setResult] = useState<ValuationResult | null>(null);

  const set = useCallback(
    (patch: Partial<typeof form>) => setForm((f) => ({ ...f, ...patch })),
    [],
  );

  const handleSubmit = useCallback(
    (e: React.FormEvent) => {
      e.preventDefault();
      if (!form.id) return;
      setStatus("loading");
      const timer = setTimeout(() => {
        setResult(DEMO_RESULT);
        setStatus("result");
      }, 1200);
      return () => clearTimeout(timer);
    },
    [form.id],
  );

  return (
    <div className="space-y-3.5">
      <PageHeader
        category="Enterprise \u00B7 Deeds"
        title="Property Valuation"
        description="Automated valuation model \u2014 combines deeds data, recent comparables, and area trends to estimate market value."
      />

      <div className="grid grid-cols-[minmax(0,440px)_minmax(0,1fr)] gap-3.5 items-start max-lg:grid-cols-1">
        {/* Form */}
        <form onSubmit={handleSubmit}>
          <Card>
            <CardHeader>
              <div className="text-sm font-semibold">Property details</div>
            </CardHeader>
            <CardBody>
              <div className="space-y-3">
                <Input
                  label="ERF number or address"
                  value={form.id}
                  onChange={(e) => set({ id: e.target.value })}
                  placeholder="ERF 12453"
                  className="font-mono"
                  required
                />
                <div className="grid grid-cols-2 gap-2.5">
                  <Input
                    label="Erf size (m\u00B2)"
                    value={form.size}
                    onChange={(e) => set({ size: e.target.value })}
                    placeholder="612"
                    className="font-mono"
                  />
                  <Input
                    label="Bedrooms"
                    value={form.bed}
                    onChange={(e) => set({ bed: e.target.value })}
                    placeholder="3"
                    className="font-mono"
                  />
                </div>
                <div className="grid grid-cols-2 gap-2.5">
                  <Input
                    label="Bathrooms"
                    value={form.bath}
                    onChange={(e) => set({ bath: e.target.value })}
                    placeholder="2"
                    className="font-mono"
                  />
                  <Input
                    label="Year built"
                    value={form.year}
                    onChange={(e) => set({ year: e.target.value })}
                    placeholder="2008"
                    className="font-mono"
                  />
                </div>
                <Select
                  label="Condition"
                  value={form.condition}
                  onChange={(e) => set({ condition: e.target.value })}
                  options={CONDITIONS}
                />
              </div>
            </CardBody>
            <div className="px-[18px] py-3 border-t border-border bg-[#F8FAFC] flex items-center justify-between">
              <span className="text-[11px] text-text-muted">R 350.00 &middot; AVM result in 15 seconds</span>
              <Button
                variant="cta"
                type="submit"
                disabled={!form.id || status === "loading"}
                icon={status === "loading" ? <Loader2 size={13} className="animate-spin" /> : undefined}
              >
                {status === "loading" ? "Valuing\u2026" : "Value property \u2192"}
              </Button>
            </div>
          </Card>
        </form>

        {/* Result */}
        <div className="space-y-3">
          {status === "idle" && (
            <EmptyState
              icon={BarChart}
              title="Awaiting property details"
            />
          )}

          {status === "result" && result && (
            <>
              {/* Valuation card */}
              <Card>
                <div className="flex h-[3px]">
                  <div className="flex-[3] bg-[#E23D36]" />
                  <div className="flex-[5] bg-primary" />
                  <div className="flex-[2] bg-accent" />
                </div>
                <div className="py-[22px] px-6 text-center">
                  <div className="text-[11px] text-text-muted uppercase tracking-wider font-semibold">
                    Estimated market value
                  </div>
                  <div className="text-[42px] font-bold text-primary mt-1.5 tracking-tight leading-none">
                    {result.estimate}
                  </div>
                  {/* Range bar */}
                  <div className="flex items-center justify-center gap-2.5 mt-2 text-xs text-text-muted">
                    <span>{result.low}</span>
                    <span className="relative w-40 h-1.5 bg-[#F2F3F3] rounded-[3px] overflow-hidden">
                      <span
                        className="absolute top-0 bottom-0 rounded-[3px]"
                        style={{
                          left: "20%",
                          right: "20%",
                          background: "linear-gradient(90deg, #2C974B, #00B3D9, #2C974B)",
                        }}
                      />
                      <span className="absolute left-1/2 -top-0.5 -bottom-0.5 w-0.5 bg-primary" />
                    </span>
                    <span>{result.high}</span>
                  </div>
                  {/* Confidence */}
                  <div className="mt-3.5 inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full bg-[rgba(44,151,75,0.10)] text-[#2C974B] text-[11px] font-semibold">
                    <Shield size={11} />
                    {result.confidence}% confidence &middot; 3 comparables within 500m
                  </div>
                </div>
              </Card>

              {/* Comparables */}
              <Card>
                <CardHeader>
                  <div className="text-[13px] font-semibold">Recent comparables</div>
                </CardHeader>
                <div>
                  {result.comparables.map((c, i) => (
                    <div
                      key={i}
                      className={cn(
                        "flex items-center justify-between px-[18px] py-2.5 text-xs",
                        i < result.comparables.length - 1 && "border-b border-[#f1f5f9]",
                      )}
                    >
                      <div>
                        <div className="font-medium">{c.addr}</div>
                        <div className="text-[11px] text-text-muted">
                          {c.size} &middot; sold {c.when}
                        </div>
                      </div>
                      <div className="font-mono font-semibold text-accent">{c.sold}</div>
                    </div>
                  ))}
                </div>
              </Card>
            </>
          )}
        </div>
      </div>
    </div>
  );
}
