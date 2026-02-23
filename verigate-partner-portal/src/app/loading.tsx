export default function Loading() {
  return (
    <div className="space-y-6 animate-pulse">
      <div className="h-8 w-48 rounded bg-[color:var(--color-base-300)]" />
      <div className="h-4 w-96 rounded bg-[color:var(--color-base-300)]" />
      <div className="grid gap-4 lg:grid-cols-3">
        {Array.from({ length: 3 }).map((_, i) => (
          <div key={i} className="console-card">
            <div className="console-card-body space-y-3">
              <div className="h-4 w-24 rounded bg-[color:var(--color-base-300)]" />
              <div className="h-8 w-16 rounded bg-[color:var(--color-base-300)]" />
            </div>
          </div>
        ))}
      </div>
      <div className="console-card">
        <div className="console-card-body space-y-3">
          {Array.from({ length: 5 }).map((_, i) => (
            <div key={i} className="h-4 rounded bg-[color:var(--color-base-300)]" style={{ width: `${80 - i * 10}%` }} />
          ))}
        </div>
      </div>
    </div>
  );
}
