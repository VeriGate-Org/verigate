"use client";

import * as React from "react";
import { estimateDeedsValue, type BffDeedsValuationResponse } from "@/lib/bff-client";
import { useToast } from "@/components/ui/Toast";
import { Button } from "@/components/ui/Button";

export default function PropertyValuationPage() {
  const { toast } = useToast();
  const [propertyId, setPropertyId] = React.useState("");
  const [province, setProvince] = React.useState("Gauteng");
  const [township, setTownship] = React.useState("");
  const [erfNumber, setErfNumber] = React.useState("");
  const [portion, setPortion] = React.useState("");
  const [result, setResult] = React.useState<BffDeedsValuationResponse | null>(null);
  const [loading, setLoading] = React.useState(false);

  const handleEstimate = async () => {
    setLoading(true);
    try {
      setResult(await estimateDeedsValue({
        propertyId: propertyId || undefined,
        province,
        township,
        erfNumber: erfNumber ? Number(erfNumber) : undefined,
        portion: portion ? Number(portion) : undefined,
      }));
    } catch (err) {
      toast({ title: "Valuation failed", description: err instanceof Error ? err.message : "Could not estimate value.", variant: "error" });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="space-y-6">
      <header className="space-y-1">
        <h1 className="text-xl font-semibold text-text">Property valuation</h1>
        <p className="text-sm text-text-muted">Indicative valuation using cached transfer activity and provider-independent comparables.</p>
      </header>

      <div className="console-card">
        <div className="console-card-body grid gap-4 md:grid-cols-5">
          <input className="aws-input" value={propertyId} onChange={(e) => setPropertyId(e.target.value)} placeholder="Property ID (optional)" />
          <input className="aws-input" value={province} onChange={(e) => setProvince(e.target.value)} placeholder="Province" />
          <input className="aws-input" value={township} onChange={(e) => setTownship(e.target.value)} placeholder="Township" />
          <input className="aws-input" value={erfNumber} onChange={(e) => setErfNumber(e.target.value)} placeholder="ERF number" />
          <input className="aws-input" value={portion} onChange={(e) => setPortion(e.target.value)} placeholder="Portion" />
        </div>
        <div className="console-card-body pt-0">
          <Button variant="cta" onClick={handleEstimate} loading={loading}>Estimate value</Button>
        </div>
      </div>

      <div className="grid gap-6 xl:grid-cols-[minmax(0,0.75fr)_minmax(0,1.25fr)]">
        <div className="console-card">
          <div className="console-card-header">
            <div className="text-sm font-semibold text-text">Estimate</div>
          </div>
          <div className="console-card-body space-y-3">
            {!result ? (
              <div className="text-sm text-text-muted">Request an estimate to see indicative value, range, and confidence.</div>
            ) : (
              <>
                <Metric label="Estimated value" value={formatCurrency(result.estimatedValue)} />
                <Metric label="Range" value={`${formatCurrency(result.lowerBound)} - ${formatCurrency(result.upperBound)}`} />
                <Metric label="Confidence" value={result.confidenceBand} />
                <p className="text-xs text-text-muted">{result.methodology}</p>
                <p className="rounded border border-border bg-background p-3 text-xs text-text-muted">{result.disclaimer}</p>
              </>
            )}
          </div>
        </div>

        <div className="console-card">
          <div className="console-card-header">
            <div className="text-sm font-semibold text-text">Comparable sales</div>
          </div>
          <div className="console-card-body space-y-3">
            {!result || result.comparableSales.length === 0 ? (
              <div className="text-sm text-text-muted">No comparable sales available from the cached portfolio.</div>
            ) : (
              result.comparableSales.map((sale) => (
                <div key={sale.propertyId} className="rounded border border-border bg-background p-3">
                  <div className="flex flex-wrap items-start justify-between gap-3">
                    <div>
                      <div className="text-sm font-medium text-text">{sale.titleDeed}</div>
                      <div className="text-xs text-text-muted">{sale.township} • {sale.transferDate ?? "No transfer date"}</div>
                    </div>
                    <div className="text-right">
                      <div className="text-sm font-medium text-text">{formatCurrency(sale.transferAmount ?? 0)}</div>
                      <div className="text-xs text-text-muted">Similarity {(sale.similarityScore ?? 0).toFixed(2)}</div>
                    </div>
                  </div>
                </div>
              ))
            )}
          </div>
        </div>
      </div>
    </div>
  );
}

function Metric({ label, value }: { label: string; value: string }) {
  return (
    <div className="rounded border border-border bg-background p-3">
      <div className="text-[10px] uppercase tracking-wide text-text-muted">{label}</div>
      <div className="mt-1 text-sm font-medium text-text">{value}</div>
    </div>
  );
}

function formatCurrency(value: number) {
  return new Intl.NumberFormat("en-ZA", { style: "currency", currency: "ZAR", maximumFractionDigits: 0 }).format(value);
}
