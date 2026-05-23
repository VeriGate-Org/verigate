"use client";

import { PageHeader } from "@/components/ui/PageHeader";
import { Card, CardHeader, CardBody } from "@/components/ui/Card";
import { Badge } from "@/components/ui/Badge";
import { Button } from "@/components/ui/Button";
import { cn } from "@/lib/cn";

/* ------------------------------------------------------------------ */
/*  Types                                                              */
/* ------------------------------------------------------------------ */

interface Provider {
  name: string;
  tone: "success" | "warning" | "danger";
  label: string;
  uptime: string;
  avg: string;
  sla: number;
  last: string;
}

interface Incident {
  date: string;
  service: string;
  severity: string;
  tone: "success" | "warning" | "danger" | "info";
  msg: string;
}

/* ------------------------------------------------------------------ */
/*  Demo data                                                          */
/* ------------------------------------------------------------------ */

const PROVIDERS: Provider[] = [
  { name: "Department of Home Affairs (DHA)", tone: "warning", label: "Watch", uptime: "99.21%", avg: "42 min", sla: 92.1, last: "12 s ago" },
  { name: "SAPS-AFIS", tone: "success", label: "Healthy", uptime: "99.94%", avg: "1.2 hr", sla: 98.4, last: "4 s ago" },
  { name: "TransUnion", tone: "success", label: "Healthy", uptime: "99.99%", avg: "6.2 s", sla: 100, last: "2 s ago" },
  { name: "OpenSanctions", tone: "success", label: "Healthy", uptime: "99.98%", avg: "1.1 s", sla: 100, last: "1 s ago" },
  { name: "CIPC", tone: "success", label: "Healthy", uptime: "99.87%", avg: "3.4 s", sla: 99.2, last: "8 s ago" },
  { name: "Umalusi", tone: "danger", label: "Degraded", uptime: "94.20%", avg: "3.4 hr", sla: 71.2, last: "3 m ago" },
  { name: "SARS", tone: "success", label: "Healthy", uptime: "99.91%", avg: "8.2 s", sla: 99.6, last: "6 s ago" },
];

const INCIDENTS: Incident[] = [
  { date: "2026-05-18 14:42", service: "Umalusi", severity: "SEV2", tone: "danger", msg: "Response times exceeding 3 hours. Investigating upstream." },
  { date: "2026-05-18 09:11", service: "DHA", severity: "SEV3", tone: "warning", msg: "Elevated response times (42m vs target 30m). Monitoring." },
  { date: "2026-05-15 16:28", service: "All", severity: "INFO", tone: "info", msg: "Scheduled maintenance window 22:00\u201323:00 SAST." },
  { date: "2026-05-12 11:04", service: "OpenSanctions", severity: "RESOLVED", tone: "success", msg: "Intermittent timeouts resolved at 11:48 SAST." },
];

const TONE_COLORS: Record<string, string> = {
  success: "#2C974B",
  warning: "#C28B0B",
  danger: "#E23D36",
  info: "#00B3D9",
};

/* ------------------------------------------------------------------ */
/*  Uptime mini bars                                                   */
/* ------------------------------------------------------------------ */

function UptimeBars({ providerName, tone }: { providerName: string; tone: string }) {
  return (
    <div className="flex gap-px items-end">
      {Array.from({ length: 30 }).map((_, d) => {
        const seed = (providerName.charCodeAt(0) + d * 7) % 100;
        const dayTone =
          (d === 0 || d === 5) && tone !== "success"
            ? tone
            : seed > 95
              ? "warning"
              : "success";
        return (
          <div
            key={d}
            className="w-[3px] h-[18px] rounded-[1px] opacity-85"
            style={{ background: TONE_COLORS[dayTone] }}
          />
        );
      })}
    </div>
  );
}

/* ------------------------------------------------------------------ */
/*  Main component                                                     */
/* ------------------------------------------------------------------ */

export function SystemHealthPage() {
  const healthy = PROVIDERS.filter((p) => p.tone === "success").length;

  return (
    <div className="space-y-3.5">
      <PageHeader
        category="Admin \u00B7 System"
        title="System Health"
        description="Real-time status of every verification provider VeriGate integrates with."
        actions={<Button variant="secondary">Subscribe to status updates</Button>}
      />

      {/* Status banner */}
      <Card>
        <CardBody>
          <div className="flex items-center justify-between flex-wrap gap-4">
            <div className="flex items-center gap-4">
              <span
                className="w-3.5 h-3.5 rounded-full shrink-0"
                style={{ background: "#2C974B", boxShadow: "0 0 12px #2C974B" }}
              />
              <div>
                <div className="text-[17px] font-semibold">
                  {healthy === PROVIDERS.length
                    ? "All systems operational"
                    : `${PROVIDERS.length - healthy} service(s) degraded`}
                </div>
                <div className="text-xs text-text-muted mt-0.5">
                  {healthy}/{PROVIDERS.length} providers healthy &middot; Last check 12 s ago
                </div>
              </div>
            </div>
            <div className="flex gap-4 text-[11px] text-text-muted">
              <div>
                <div className="text-lg font-bold text-text">99.84%</div>
                <div>30-day uptime</div>
              </div>
              <div>
                <div className="text-lg font-bold text-accent">1.4 s</div>
                <div>P50 response</div>
              </div>
              <div>
                <div className="text-lg font-bold text-text">4</div>
                <div>Incidents (7d)</div>
              </div>
            </div>
          </div>
        </CardBody>
      </Card>

      {/* Provider status */}
      <Card>
        <CardHeader>
          <div>
            <div className="text-sm font-semibold">Provider status</div>
            <div className="text-[11px] text-text-muted mt-0.5">
              Last 30 days uptime &middot; SLA target 99.5%
            </div>
          </div>
        </CardHeader>
        <div>
          {PROVIDERS.map((p, i) => (
            <div
              key={p.name}
              className={cn(
                "flex items-center gap-3.5 px-[18px] py-3",
                i > 0 && "border-t border-[#f1f5f9]",
              )}
            >
              <span
                className="w-2.5 h-2.5 rounded-full shrink-0"
                style={{
                  background: TONE_COLORS[p.tone],
                  boxShadow: `0 0 8px ${TONE_COLORS[p.tone]}`,
                }}
              />
              <div className="flex-1 min-w-0">
                <div className="text-[13px] font-semibold truncate">{p.name}</div>
                <div className="flex gap-3.5 text-[11px] text-text-muted mt-0.5">
                  <span>
                    Avg{" "}
                    <b className="font-mono text-text">{p.avg}</b>
                  </span>
                  <span>
                    SLA{" "}
                    <b className="font-mono text-text">{p.sla}%</b>
                  </span>
                  <span>
                    Last check <b className="text-text">{p.last}</b>
                  </span>
                </div>
              </div>
              <UptimeBars providerName={p.name} tone={p.tone} />
              <div className="w-20 text-right shrink-0">
                <div
                  className="text-sm font-bold font-mono"
                  style={{ color: TONE_COLORS[p.tone] }}
                >
                  {p.uptime}
                </div>
                <div className="text-[10px] text-text-muted">30 days</div>
              </div>
              <Badge variant={p.tone}>{p.label}</Badge>
            </div>
          ))}
        </div>
      </Card>

      {/* Recent incidents */}
      <Card>
        <CardHeader>
          <div className="text-sm font-semibold">Recent incidents</div>
        </CardHeader>
        <div>
          {INCIDENTS.map((inc, i) => (
            <div
              key={i}
              className={cn(
                "flex gap-3.5 px-[18px] py-3",
                i > 0 && "border-t border-[#f1f5f9]",
              )}
            >
              <span
                className="w-2 h-2 rounded-full mt-1.5 shrink-0"
                style={{ background: TONE_COLORS[inc.tone] }}
              />
              <div className="flex-1 min-w-0">
                <div className="flex justify-between mb-1">
                  <span className="text-xs font-semibold">{inc.service}</span>
                  <span className="text-[10px] text-text-muted font-mono">
                    {inc.date} SAST
                  </span>
                </div>
                <div className="flex items-center gap-2 text-[11px] text-text-muted">
                  <span
                    className="font-mono px-1.5 py-px rounded text-[10px] font-semibold"
                    style={{
                      background: "#F2F3F3",
                      color: TONE_COLORS[inc.tone],
                    }}
                  >
                    {inc.severity}
                  </span>
                  {inc.msg}
                </div>
              </div>
            </div>
          ))}
        </div>
      </Card>
    </div>
  );
}
