"use client";

import * as React from "react";
import { convertDeedsProperty, type BffDeedsConversionResponse } from "@/lib/bff-client";
import { useToast } from "@/components/ui/Toast";
import { Button } from "@/components/ui/Button";

export default function PropertyConversionPage() {
  const { toast } = useToast();
  const [direction, setDirection] = React.useState("street_to_erf");
  const [province, setProvince] = React.useState("Gauteng");
  const [township, setTownship] = React.useState("");
  const [streetName, setStreetName] = React.useState("");
  const [erfNumber, setErfNumber] = React.useState("");
  const [portion, setPortion] = React.useState("");
  const [result, setResult] = React.useState<BffDeedsConversionResponse | null>(null);
  const [loading, setLoading] = React.useState(false);

  const handleConvert = async () => {
    setLoading(true);
    try {
      setResult(await convertDeedsProperty({ direction, province, township, streetName, erfNumber, portion }));
    } catch (err) {
      toast({ title: "Conversion failed", description: err instanceof Error ? err.message : "Could not convert property reference.", variant: "error" });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="space-y-6">
      <header className="space-y-1">
        <h1 className="text-xl font-semibold text-text">Street / ERF conversion</h1>
        <p className="text-sm text-text-muted">Use cached deeds results to convert between street references and ERF/title references.</p>
      </header>

      <div className="console-card">
        <div className="console-card-body grid gap-4 md:grid-cols-3">
          <select className="aws-select" value={direction} onChange={(e) => setDirection(e.target.value)}>
            <option value="street_to_erf">Street to ERF</option>
            <option value="erf_to_street">ERF to Street</option>
          </select>
          <input className="aws-input" value={province} onChange={(e) => setProvince(e.target.value)} placeholder="Province" />
          <input className="aws-input" value={township} onChange={(e) => setTownship(e.target.value)} placeholder="Township" />
          <input className="aws-input" value={streetName} onChange={(e) => setStreetName(e.target.value)} placeholder="Street name" />
          <input className="aws-input" value={erfNumber} onChange={(e) => setErfNumber(e.target.value)} placeholder="ERF number" />
          <input className="aws-input" value={portion} onChange={(e) => setPortion(e.target.value)} placeholder="Portion" />
        </div>
        <div className="console-card-body pt-0">
          <Button variant="cta" onClick={handleConvert} loading={loading}>Convert</Button>
        </div>
      </div>

      <div className="console-card">
        <div className="console-card-header">
          <div className="text-sm font-semibold text-text">Candidates</div>
        </div>
        <div className="console-card-body space-y-3">
          {!result ? (
            <div className="text-sm text-text-muted">Run a conversion to see matched candidates and confidence scores.</div>
          ) : result.candidates.length === 0 ? (
            <div className="text-sm text-text-muted">No cached candidates matched this reference.</div>
          ) : (
            result.candidates.map((candidate) => (
              <div key={candidate.propertyId} className="rounded border border-border bg-background p-3">
                <div className="flex flex-wrap items-start justify-between gap-3">
                  <div>
                    <div className="text-sm font-medium text-text">{candidate.streetAddress}</div>
                    <div className="text-xs text-text-muted">
                      ERF {candidate.erfNumber}/{candidate.portion} • {candidate.titleDeed}
                    </div>
                  </div>
                  <span className="rounded border border-border px-2 py-1 text-[10px] uppercase tracking-wide text-text-muted">
                    {(candidate.confidence * 100).toFixed(0)}%
                  </span>
                </div>
                <div className="mt-2 text-xs text-text-muted">{candidate.reason}</div>
              </div>
            ))
          )}
        </div>
      </div>
    </div>
  );
}
