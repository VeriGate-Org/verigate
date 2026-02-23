import { type VerificationEvent } from "@/lib/types";

const statusColor: Record<string, string> = {
  VerificationRequested: "bg-neutral-300",
  VerificationSucceeded: "bg-green-500",
  VerificationSoftFail: "bg-amber-500",
  VerificationHardFail: "bg-red-500",
  VerificationSystemOutage: "bg-red-800",
  DomainSpecific: "bg-blue-500",
};

export default function EventTimeline({ events }: { events: VerificationEvent[] }) {
  return (
    <ul className="space-y-3">
      {events.map((e, i) => (
        <li key={i} className="flex items-start gap-3">
          <span className={`mt-1 inline-flex h-2.5 w-2.5 shrink-0 rounded-full ${statusColor[e.eventType] || "bg-neutral-400"}`} />
          <div>
            <div className="text-sm font-medium">{e.eventType}</div>
            <div className="text-xs text-neutral-600">{new Date(e.ts).toLocaleString()} • {e.source}</div>
            {e.detail ? (
              <pre className="mt-1 overflow-x-auto rounded-md bg-neutral-50 p-2 text-xs text-neutral-800">{JSON.stringify(e.detail, null, 2)}</pre>
            ) : null}
          </div>
        </li>
      ))}
    </ul>
  );
}

