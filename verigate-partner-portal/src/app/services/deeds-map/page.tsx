"use client";

import * as React from "react";
import { searchDeedsMap, type BffDeedsMapSearchResponse } from "@/lib/bff-client";
import { useToast } from "@/components/ui/Toast";
import { Button } from "@/components/ui/Button";

export default function DeedsMapPage() {
  const { toast } = useToast();
  const [province, setProvince] = React.useState("Gauteng");
  const [township, setTownship] = React.useState("");
  const [streetName, setStreetName] = React.useState("");
  const [result, setResult] = React.useState<BffDeedsMapSearchResponse | null>(null);
  const [loading, setLoading] = React.useState(false);

  const handleSearch = async () => {
    setLoading(true);
    try {
      setResult(await searchDeedsMap({ province, township, streetName }));
    } catch (err) {
      toast({ title: "Map search failed", description: err instanceof Error ? err.message : "Could not search map data.", variant: "error" });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="space-y-6">
      <header className="space-y-1">
        <h1 className="text-xl font-semibold text-text">Deeds spatial workspace</h1>
        <p className="text-sm text-text-muted">Provider-independent map shell using cached deeds results and mock municipal boundaries.</p>
      </header>

      <div className="console-card">
        <div className="console-card-body grid gap-4 md:grid-cols-4">
          <input className="aws-input" value={province} onChange={(e) => setProvince(e.target.value)} placeholder="Province" />
          <input className="aws-input" value={township} onChange={(e) => setTownship(e.target.value)} placeholder="Township" />
          <input className="aws-input" value={streetName} onChange={(e) => setStreetName(e.target.value)} placeholder="Street name" />
          <Button variant="cta" onClick={handleSearch} loading={loading}>Search map</Button>
        </div>
      </div>

      <div className="grid gap-6 xl:grid-cols-[minmax(0,1.2fr)_minmax(0,0.8fr)]">
        <div className="console-card">
          <div className="console-card-header">
            <div className="text-sm font-semibold text-text">Map shell</div>
          </div>
          <div className="console-card-body">
            <div className="relative min-h-[420px] overflow-hidden rounded border border-border bg-[radial-gradient(circle_at_top_left,_rgba(37,99,235,0.12),_transparent_28%),linear-gradient(135deg,#f7f7ef,#eef4ff)]">
              {(result?.boundaries ?? []).map((boundary, index) => (
                <div
                  key={boundary.boundaryId}
                  className="absolute rounded-full border border-sky-400/40 bg-sky-400/10"
                  style={{
                    left: `${10 + index * 12}%`,
                    top: `${8 + index * 9}%`,
                    width: `${48 - index * 4}%`,
                    height: `${42 - index * 3}%`,
                  }}
                />
              ))}
              {(result?.properties ?? []).map((property, index) => (
                <button
                  key={property.propertyId}
                  className="absolute h-3 w-3 rounded-full bg-emerald-600 ring-4 ring-emerald-200"
                  style={{
                    left: `${18 + (index % 6) * 11}%`,
                    top: `${20 + (index % 5) * 12}%`,
                  }}
                  title={property.label}
                />
              ))}
              <div className="absolute left-4 top-4 rounded border border-border bg-background/85 px-3 py-2 text-xs text-text-muted">
                Boundaries: {result?.boundaries.length ?? 0} • Properties: {result?.properties.length ?? 0}
              </div>
            </div>
          </div>
        </div>

        <div className="console-card">
          <div className="console-card-header">
            <div className="text-sm font-semibold text-text">Search results</div>
          </div>
          <div className="console-card-body space-y-3">
            {!result ? (
              <div className="text-sm text-text-muted">Run a map search to inspect cached parcels and boundaries.</div>
            ) : (
              result.properties.slice(0, 8).map((property) => (
                <div key={property.propertyId} className="rounded border border-border bg-background p-3">
                  <div className="text-sm font-medium text-text">{property.label}</div>
                  <div className="text-xs text-text-muted">{property.streetAddress}</div>
                  <div className="mt-2 text-xs text-text-muted">{property.ownerName} • {property.titleDeed}</div>
                </div>
              ))
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
