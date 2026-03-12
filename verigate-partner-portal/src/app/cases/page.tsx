import CasesTable from "./CasesTable.client";

export default function CasesPage() {
  return (
    <div className="max-w-6xl mx-auto space-y-6">
      <div>
        <h1 className="text-xl font-semibold text-text">Case Management</h1>
        <p className="text-sm text-text-muted mt-1">
          Review and triage verifications flagged for manual review.
        </p>
      </div>
      <CasesTable />
    </div>
  );
}
