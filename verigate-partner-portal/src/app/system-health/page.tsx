import SystemHealthDashboard from "./SystemHealthDashboard.client";

export default function SystemHealthPage() {
  return (
    <div className="max-w-6xl mx-auto space-y-6">
      <div>
        <h1 className="text-xl font-semibold text-text">System Health</h1>
        <p className="text-sm text-text-muted mt-1">
          Active connectivity probing for external integrations and AWS infrastructure status.
        </p>
      </div>
      <SystemHealthDashboard />
    </div>
  );
}
