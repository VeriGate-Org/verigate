import { Suspense } from "react";
import Filters from "./Filters.client";
import VerificationsTable from "./VerificationsTable.client";

export default function VerificationsPage() {
  return (
    <div className="space-y-6">
      <header className="space-y-1">
        <h1 className="text-xl font-semibold text-text">Verification log</h1>
        <p className="text-sm text-text-muted">Inspect recent checks and filter by status, provider, or type.</p>
      </header>
      <Suspense fallback={<div className="console-card p-4 text-sm text-text-muted">Loading filters...</div>}>
        <Filters />
      </Suspense>
      <Suspense fallback={<div className="console-card p-4 text-sm text-text-muted">Loading verifications…</div>}>
        <VerificationsTable />
      </Suspense>
    </div>
  );
}
