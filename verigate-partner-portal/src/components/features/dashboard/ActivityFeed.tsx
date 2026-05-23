const TONE_COLORS: Record<string, string> = {
  info: "bg-accent",
  success: "bg-[#2C974B]",
  warning: "bg-[#C28B0B]",
  danger: "bg-[#E23D36]",
};

const ACTIVITY = [
  {
    id: "VG-2026-0142",
    actor: "You",
    verb: "started verification for",
    target: "John M. Doe",
    tone: "info",
    when: "2 min ago",
  },
  {
    id: "VG-2026-0141",
    actor: "System",
    verb: "flagged for review",
    target: "Acme Corp Ltd",
    tone: "warning",
    when: "15 min ago",
  },
  {
    id: "VG-2026-0140",
    actor: "Bureau",
    verb: "returned a fail on",
    target: "Jane Smith",
    tone: "danger",
    when: "1 hr ago",
  },
  {
    id: "VG-2026-0138",
    actor: "SAPS",
    verb: "returned clean record for",
    target: "Lerato Mokoena",
    tone: "success",
    when: "2 hr ago",
  },
  {
    id: "VG-2026-0136",
    actor: "Naledi N.",
    verb: "completed biometric for",
    target: "her own onboard",
    tone: "success",
    when: "Yesterday",
  },
];

export function ActivityFeed() {
  return (
    <div className="console-card overflow-hidden">
      <div className="px-[18px] py-3.5 border-b border-[#e9ebed]">
        <div className="text-sm font-semibold text-text">Activity</div>
        <div className="text-[11px] text-text-muted mt-0.5">Last 24 hours</div>
      </div>

      <div className="flex flex-col py-2">
        {ACTIVITY.map((a, i) => (
          <div
            key={a.id + i}
            className="flex gap-3 px-[18px] py-2 items-start"
          >
            <span
              className={`w-1.5 h-1.5 rounded-full mt-1.5 shrink-0 ${TONE_COLORS[a.tone] || "bg-text-muted"}`}
            />
            <div className="flex-1 min-w-0">
              <div className="text-xs text-text leading-relaxed">
                <span className="font-semibold">{a.actor}</span> {a.verb}{" "}
                <span className="font-semibold">{a.target}</span>
              </div>
              <div className="text-[10px] text-text-muted mt-0.5 font-mono">
                {a.id} &middot; {a.when}
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
