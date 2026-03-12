import MonitoringDashboard from "./MonitoringDashboard.client";

export default function MonitoringPage() {
  return (
    <div className="max-w-6xl mx-auto space-y-6">
      <div>
        <h1 className="text-xl font-semibold text-text">Ongoing Monitoring</h1>
        <p className="text-sm text-text-muted mt-1">
          Monitor subjects for risk changes with scheduled re-verification.
        </p>
      </div>
      <MonitoringDashboard />
    </div>
  );
}
