"use client";

import { useState, useCallback } from "react";
import { PageHeader } from "@/components/ui/PageHeader";
import { Card, CardHeader, CardBody } from "@/components/ui/Card";
import { Badge } from "@/components/ui/Badge";
import { Button } from "@/components/ui/Button";
import { Input } from "@/components/ui/Input";
import { Select } from "@/components/ui/Select";
import { EmptyState } from "@/components/ui/EmptyState";
import { cn } from "@/lib/cn";
import { Search, Loader2, Home } from "lucide-react";

/* ------------------------------------------------------------------ */
/*  Constants                                                          */
/* ------------------------------------------------------------------ */

const SA_PROVINCES = [
  { value: "WC", label: "Western Cape" },
  { value: "GP", label: "Gauteng" },
  { value: "KZN", label: "KwaZulu-Natal" },
  { value: "EC", label: "Eastern Cape" },
  { value: "FS", label: "Free State" },
  { value: "LP", label: "Limpopo" },
  { value: "MP", label: "Mpumalanga" },
  { value: "NW", label: "North West" },
  { value: "NC", label: "Northern Cape" },
];

/* ------------------------------------------------------------------ */
/*  Types                                                              */
/* ------------------------------------------------------------------ */

interface LinkedProperty {
  erf: string;
  address: string;
  size: string;
  type: string;
  municipal: string;
  bond: string;
  registered: string;
}

interface ConversionResult {
  erfNumber: string;
  address: string;
  municipality: string;
  suburb: string;
  postalCode: string;
  zoning: string;
  linkedProperty: LinkedProperty;
}

const DEMO_RESULT: ConversionResult = {
  erfNumber: "ERF 12453",
  address: "14 Cinnebar Street, Table View, 7441",
  municipality: "City of Cape Town",
  suburb: "Table View",
  postalCode: "7441",
  zoning: "Residential",
  linkedProperty: {
    erf: "ERF 12453",
    address: "14 Cinnebar Street, Table View, 7441",
    size: "612 m\u00B2",
    type: "Residential",
    municipal: "R 2,400,000",
    bond: "R 1,240,000",
    registered: "2018-04-12",
  },
};

/* ------------------------------------------------------------------ */
/*  PropertyCard — matches reference Deeds.jsx                         */
/* ------------------------------------------------------------------ */

function PropertyCard({ p }: { p: LinkedProperty }) {
  return (
    <Card>
      <CardBody>
        <div className="flex gap-3.5 items-start">
          <div
            className="w-14 h-14 rounded-md shrink-0 inline-flex items-center justify-center"
            style={{ background: "linear-gradient(160deg, #1A2E4B, #1a3a5c)" }}
          >
            <Home size={24} className="text-accent" />
          </div>
          <div className="flex-1 min-w-0">
            <div className="flex items-center gap-2 mb-1">
              <span className="font-mono text-[11px] text-accent">{p.erf}</span>
              <Badge variant="info" size="sm">{p.type}</Badge>
            </div>
            <div className="text-[13px] font-semibold text-text">{p.address}</div>
            <div className="flex gap-3.5 mt-2 text-[11px] text-text-muted flex-wrap">
              <span><b className="text-text">Size:</b> {p.size}</span>
              <span><b className="text-text">Municipal value:</b> {p.municipal}</span>
              <span><b className="text-text">Bond:</b> {p.bond}</span>
              <span><b className="text-text">Registered:</b> {p.registered}</span>
            </div>
          </div>
          <Button variant="ghost" size="sm">Details &rarr;</Button>
        </div>
      </CardBody>
    </Card>
  );
}

/* ------------------------------------------------------------------ */
/*  Main component                                                     */
/* ------------------------------------------------------------------ */

export function PropertyConversionPage() {
  const [direction, setDirection] = useState<"addr_to_erf" | "erf_to_addr">("addr_to_erf");
  const [value, setValue] = useState("");
  const [province, setProvince] = useState("WC");
  const [status, setStatus] = useState<"idle" | "loading" | "result">("idle");
  const [result, setResult] = useState<ConversionResult | null>(null);

  const handleSubmit = useCallback(
    (e: React.FormEvent) => {
      e.preventDefault();
      if (!value) return;
      setStatus("loading");
      const timer = setTimeout(() => {
        setResult(DEMO_RESULT);
        setStatus("result");
      }, 900);
      return () => clearTimeout(timer);
    },
    [value],
  );

  return (
    <div className="space-y-3.5">
      <PageHeader
        category="Enterprise \u00B7 Deeds"
        title="Street \u2194 ERF Conversion"
        description="Translate physical street addresses into ERF numbers and back. Useful for cross-referencing municipal records."
      />

      <div className="grid grid-cols-[minmax(0,440px)_minmax(0,1fr)] gap-3.5 items-start max-lg:grid-cols-1">
        {/* Form */}
        <form onSubmit={handleSubmit}>
          <Card>
            <CardHeader>
              <div className="text-sm font-semibold">Convert</div>
            </CardHeader>
            <CardBody>
              <div className="space-y-3">
                {/* Direction toggle */}
                <div className="flex border border-border rounded overflow-hidden">
                  {(
                    [
                      { id: "addr_to_erf", label: "Address \u2192 ERF" },
                      { id: "erf_to_addr", label: "ERF \u2192 Address" },
                    ] as const
                  ).map((o, i) => (
                    <button
                      key={o.id}
                      type="button"
                      onClick={() => setDirection(o.id)}
                      className={cn(
                        "flex-1 py-[7px] text-xs font-medium transition-colors",
                        i > 0 && "border-l border-border",
                        direction === o.id
                          ? "bg-primary text-white"
                          : "bg-surface text-text hover:bg-[#F8FAFC]",
                      )}
                    >
                      {o.label}
                    </button>
                  ))}
                </div>

                <Input
                  label={direction === "addr_to_erf" ? "Street address" : "ERF number"}
                  value={value}
                  onChange={(e) => setValue(e.target.value)}
                  placeholder={
                    direction === "addr_to_erf"
                      ? "14 Cinnebar Street, Table View"
                      : "ERF 12453"
                  }
                  className={cn(direction === "erf_to_addr" && "font-mono")}
                  required
                />

                <Select
                  label="Province / municipality"
                  value={province}
                  onChange={(e) => setProvince(e.target.value)}
                  options={SA_PROVINCES}
                />
              </div>
            </CardBody>
            <div className="px-[18px] py-3 border-t border-border bg-[#F8FAFC] flex items-center justify-between">
              <span className="text-[11px] text-text-muted">R 12.00 &middot; municipal records</span>
              <Button
                variant="cta"
                type="submit"
                disabled={!value || status === "loading"}
                icon={status === "loading" ? <Loader2 size={13} className="animate-spin" /> : undefined}
              >
                {status === "loading" ? "Looking up\u2026" : "Convert \u2192"}
              </Button>
            </div>
          </Card>
        </form>

        {/* Result */}
        <div className="space-y-3">
          {status === "idle" && (
            <EmptyState
              icon={Search}
              title="Enter an address or ERF"
              body="We\u2019ll cross-reference against municipal records."
            />
          )}

          {status === "result" && result && (
            <>
              <Card>
                <CardBody className="p-5">
                  <div className="text-[11px] text-text-muted uppercase tracking-wide font-semibold mb-2">
                    {direction === "addr_to_erf" ? "Resolved ERF" : "Resolved address"}
                  </div>
                  {direction === "addr_to_erf" ? (
                    <div className="font-mono text-2xl font-bold text-accent">
                      {result.erfNumber}
                    </div>
                  ) : (
                    <div className="text-[17px] font-semibold">{result.address}</div>
                  )}
                  <div className="grid grid-cols-2 gap-2 mt-3.5 pt-3.5 border-t border-[#f1f5f9]">
                    {[
                      ["Municipality", result.municipality],
                      ["Suburb", result.suburb],
                      ["Postal code", result.postalCode],
                      ["Zoning", result.zoning],
                    ].map(([k, v]) => (
                      <div key={k} className="text-xs">
                        <span className="text-text-muted">{k}: </span>
                        <b>{v}</b>
                      </div>
                    ))}
                  </div>
                </CardBody>
              </Card>

              <PropertyCard p={result.linkedProperty} />
            </>
          )}
        </div>
      </div>
    </div>
  );
}
