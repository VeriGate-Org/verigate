import { StatCard } from "@/components/ui/StatCard";

export function KpiStrip() {
  return (
    <div className="grid grid-cols-2 lg:grid-cols-4 gap-3.5">
      <StatCard
        label="Verifications today"
        value="72"
        delta="+12%"
        deltaTone="success"
        helper="vs yesterday"
      />
      <StatCard
        label="Pass rate"
        value="94.2%"
        delta="+0.3pt"
        deltaTone="success"
        helper="Industry avg 91.0%"
      />
      <StatCard
        label="Avg. turnaround"
        value="2.4m"
        delta="-18s"
        deltaTone="success"
        helper="P95 under 9m"
      />
      <StatCard
        label="Spend (mo.)"
        value="R 18,420"
        helper="Of R 25,000 budget"
      />
    </div>
  );
}
