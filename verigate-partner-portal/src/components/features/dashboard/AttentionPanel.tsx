"use client";

import { Badge } from "@/components/ui/Badge";
import { Button } from "@/components/ui/Button";

const ITEMS = [
  {
    id: "VG-2026-0140",
    who: "Jane Smith",
    why: "Credit bureau returned a hard fail \u2014 manual review required.",
    badge: "danger" as const,
    badgeLabel: "Failed",
    age: "1 hr ago",
  },
  {
    id: "VG-2026-0141",
    who: "Acme Corp Ltd",
    why: "Sanctions list match \u2014 partial name, score 0.78.",
    badge: "warning" as const,
    badgeLabel: "Review",
    age: "15 min ago",
  },
  {
    id: "VG-2026-0139",
    who: "Bob Williams",
    why: "Waiting on Department of Home Affairs (12m elapsed, SLA 30m).",
    badge: "info" as const,
    badgeLabel: "In Progress",
    age: "Just now",
  },
];

export function AttentionPanel({ onViewAll }: { onViewAll: () => void }) {
  return (
    <div className="console-card overflow-hidden">
      {/* Tri-bar accent */}
      <div className="flex h-[3px]">
        <div className="flex-[3] bg-[#E23D36]" />
        <div className="flex-[5] bg-primary" />
        <div className="flex-[2] bg-accent" />
      </div>

      <div className="px-[18px] py-3.5 flex justify-between items-center border-b border-[#e9ebed]">
        <div>
          <div className="text-sm font-semibold text-text">
            Needs your attention
          </div>
          <div className="text-[11px] text-text-muted mt-0.5">
            {ITEMS.length} open &middot; 3 escalated &middot; 1 SLA at risk
          </div>
        </div>
        <Button variant="link" onClick={onViewAll}>
          View all &rarr;
        </Button>
      </div>

      <div className="flex flex-col">
        {ITEMS.map((item) => (
          <button
            key={item.id}
            className="flex items-start gap-3 px-[18px] py-3 text-left hover:bg-[#F8FAFC] transition-colors"
          >
            <Badge variant={item.badge}>{item.badgeLabel}</Badge>
            <div className="flex-1 min-w-0">
              <div className="flex items-baseline gap-2 mb-0.5">
                <span className="font-mono text-[11px] text-accent">
                  {item.id}
                </span>
                <span className="text-[13px] font-semibold text-text">
                  {item.who}
                </span>
              </div>
              <div className="text-xs text-text-muted leading-relaxed">
                {item.why}
              </div>
            </div>
            <span className="text-[11px] text-text-muted whitespace-nowrap shrink-0">
              {item.age}
            </span>
          </button>
        ))}
      </div>
    </div>
  );
}
