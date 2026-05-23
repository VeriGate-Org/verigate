"use client";

import { PageHeader } from "@/components/ui/PageHeader";
import { Card, CardHeader } from "@/components/ui/Card";
import { cn } from "@/lib/cn";

/* ------------------------------------------------------------------ */
/*  Data                                                               */
/* ------------------------------------------------------------------ */

interface DocType {
  name: string;
  count: number;
  tampering: number;
  pass: number;
}

const DOC_TYPES: DocType[] = [
  { name: "SA ID Card", count: 4820, tampering: 96, pass: 98.4 },
  { name: "Passport", count: 1240, tampering: 91, pass: 96.8 },
  { name: "Driver\u2019s License", count: 892, tampering: 84, pass: 93.2 },
  { name: "Utility Bill", count: 638, tampering: 72, pass: 88.4 },
  { name: "CIPC Registration", count: 312, tampering: 89, pass: 95.1 },
  { name: "Tax Clearance", count: 184, tampering: 94, pass: 97.6 },
];

const KPI_STATS = [
  { label: "Docs processed (30d)", value: "8,086", tone: "#1A2024" },
  { label: "Authentic rate", value: "94.2%", tone: "#2C974B" },
  { label: "Tampering flagged", value: "4.1%", tone: "#C28B0B" },
  { label: "Hard fraud caught", value: "1.7%", tone: "#E23D36" },
];

function integrityColor(value: number) {
  if (value >= 90) return "#2C974B";
  if (value >= 75) return "#C28B0B";
  return "#E23D36";
}

/* ------------------------------------------------------------------ */
/*  Main component                                                     */
/* ------------------------------------------------------------------ */

export function DocumentInsightsPage() {
  return (
    <div className="space-y-3.5">
      <PageHeader
        category="Identity \u00B7 Insights"
        title="Document Insights"
        description="Cross-document analytics: tampering rates by type, fraud trends, and processing performance."
      />

      {/* KPI cards */}
      <div className="grid grid-cols-4 gap-3.5 max-lg:grid-cols-2">
        {KPI_STATS.map((s) => (
          <div key={s.label} className="console-card p-4">
            <div className="text-[10px] font-semibold uppercase tracking-wide text-text-muted">
              {s.label}
            </div>
            <div
              className="text-2xl font-bold mt-1"
              style={{ color: s.tone }}
            >
              {s.value}
            </div>
          </div>
        ))}
      </div>

      {/* By document type */}
      <Card>
        <CardHeader>
          <div>
            <div className="text-sm font-semibold">By document type</div>
            <div className="text-[11px] text-text-muted mt-0.5">
              Volume &middot; tampering integrity &middot; pass rate
            </div>
          </div>
        </CardHeader>
        <div>
          {DOC_TYPES.map((t, i) => (
            <div
              key={t.name}
              className={cn(
                "flex items-center gap-3.5 px-[18px] py-3",
                i > 0 && "border-t border-[#f1f5f9]",
              )}
            >
              <div className="flex-1 min-w-0">
                <div className="text-[13px] font-semibold">{t.name}</div>
                <div className="text-[11px] text-text-muted">
                  {t.count.toLocaleString("en-ZA")} processed
                </div>
              </div>
              <div className="flex gap-4 items-center">
                {/* Integrity bar */}
                <div className="w-[110px]">
                  <div className="text-[10px] text-text-muted mb-[3px]">Integrity</div>
                  <div className="h-[5px] bg-[#F2F3F3] rounded-[3px] overflow-hidden">
                    <div
                      className="h-full"
                      style={{
                        width: `${t.tampering}%`,
                        background: integrityColor(t.tampering),
                      }}
                    />
                  </div>
                  <div className="text-[10px] text-text-muted font-mono mt-0.5">
                    {t.tampering}%
                  </div>
                </div>
                {/* Pass rate bar */}
                <div className="w-[110px]">
                  <div className="text-[10px] text-text-muted mb-[3px]">Pass rate</div>
                  <div className="h-[5px] bg-[#F2F3F3] rounded-[3px] overflow-hidden">
                    <div
                      className="h-full bg-primary"
                      style={{ width: `${t.pass}%` }}
                    />
                  </div>
                  <div className="text-[10px] text-text-muted font-mono mt-0.5">
                    {t.pass}%
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>
      </Card>
    </div>
  );
}
