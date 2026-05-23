"use client";

import { useState } from "react";
import { PageHeader } from "@/components/ui/PageHeader";
import { Card, CardBody } from "@/components/ui/Card";
import { Badge } from "@/components/ui/Badge";
import { Button } from "@/components/ui/Button";

/* ------------------------------------------------------------------ */
/*  Types & data                                                       */
/* ------------------------------------------------------------------ */

interface Property {
  erf: string;
  address: string;
  size: string;
  type: string;
  municipal: string;
  registered: string;
  bond: string;
}

interface Pin {
  p: Property;
  x: number;
  y: number;
  hot: boolean;
}

const PROPERTIES: Property[] = [
  { erf: "ERF 12453", address: "14 Cinnebar Street, Table View", size: "612 m\u00B2", type: "Residential", municipal: "R 2,400,000", registered: "2019-03-15", bond: "R 1,800,000" },
  { erf: "ERF 7821", address: "47 Blouberg Road, Blouberg", size: "848 m\u00B2", type: "Commercial", municipal: "R 4,200,000", registered: "2017-08-22", bond: "R 3,100,000" },
  { erf: "ERF 3340", address: "3 Sunset Close, Milnerton", size: "520 m\u00B2", type: "Residential", municipal: "R 1,950,000", registered: "2021-11-03", bond: "R 1,400,000" },
  { erf: "ERF 99021", address: "12 Lighthouse Cres.", size: "380 m\u00B2", type: "Residential", municipal: "R 1,200,000", registered: "2020-02-18", bond: "R 900,000" },
  { erf: "ERF 5512", address: "5 Marine Drive", size: "720 m\u00B2", type: "Residential", municipal: "R 5,800,000", registered: "2012-07-04", bond: "Bond settled" },
];

const PINS: Pin[] = [
  { p: PROPERTIES[0], x: 38, y: 42, hot: false },
  { p: PROPERTIES[1], x: 62, y: 28, hot: true },
  { p: PROPERTIES[2], x: 50, y: 68, hot: false },
  { p: PROPERTIES[3], x: 24, y: 60, hot: false },
  { p: PROPERTIES[4], x: 72, y: 50, hot: false },
];

/* ------------------------------------------------------------------ */
/*  Map pin SVG                                                        */
/* ------------------------------------------------------------------ */

function MapPin({
  color,
  onClick,
  style,
}: {
  color: string;
  onClick: () => void;
  style: React.CSSProperties;
}) {
  return (
    <button
      onClick={onClick}
      className="absolute p-0 bg-transparent border-none cursor-pointer"
      style={{ ...style, transform: "translate(-50%, -100%)" }}
    >
      <svg viewBox="0 0 28 36" width="28" height="36">
        <path
          d="M 14 0 C 6 0 0 6 0 14 C 0 24 14 36 14 36 C 14 36 28 24 28 14 C 28 6 22 0 14 0 Z"
          fill={color}
        />
        <circle cx="14" cy="14" r="5" fill="#fff" />
      </svg>
    </button>
  );
}

/* ------------------------------------------------------------------ */
/*  Main component                                                     */
/* ------------------------------------------------------------------ */

export function DeedsMapPage() {
  const [selected, setSelected] = useState<Property>(PROPERTIES[0]);

  return (
    <div className="space-y-3.5">
      <PageHeader
        category="Enterprise \u00B7 Deeds"
        title="Deeds Map"
        description="Visual property search. Click any pin to view ERF and ownership details."
      />

      <div className="grid grid-cols-[minmax(0,1fr)_340px] gap-3.5 max-lg:grid-cols-1">
        {/* Map panel */}
        <Card className="overflow-hidden">
          {/* Map header */}
          <div className="px-[18px] py-3 border-b border-border flex items-center justify-between">
            <div>
              <div className="text-[13px] font-semibold">Cape Town &middot; Atlantic Seaboard</div>
              <div className="text-[11px] text-text-muted">
                {PINS.length} properties shown &middot; 1 flagged
              </div>
            </div>
            <div className="flex gap-1.5">
              <Button variant="secondary" size="sm">Filters</Button>
              <Button variant="secondary" size="sm">Layers</Button>
            </div>
          </div>

          {/* Map area */}
          <div
            className="relative overflow-hidden"
            style={{
              height: 460,
              background: "linear-gradient(135deg, #d4e4ec 0%, #b8d0dc 30%, #e8d8c0 30%, #d4c3a8 60%, #c5b594 100%)",
            }}
          >
            {/* Grid overlay */}
            <svg width="100%" height="100%" className="absolute inset-0 opacity-15">
              {Array.from({ length: 14 }).map((_, i) => (
                <line key={`h${i}`} x1="0" y1={i * 40} x2="100%" y2={i * 40} stroke="#1A2E4B" strokeWidth="1" />
              ))}
              {Array.from({ length: 24 }).map((_, i) => (
                <line key={`v${i}`} x1={i * 40} y1="0" x2={i * 40} y2="100%" stroke="#1A2E4B" strokeWidth="1" />
              ))}
            </svg>

            {/* Coastline */}
            <svg width="100%" height="100%" className="absolute inset-0" viewBox="0 0 100 100" preserveAspectRatio="none">
              <path d="M 0 30 Q 20 35 30 40 T 50 38 T 70 35 T 100 30 L 100 0 L 0 0 Z" fill="rgba(0,179,217,0.15)" />
              <path d="M 0 30 Q 20 35 30 40 T 50 38 T 70 35 T 100 30" stroke="#00B3D9" strokeWidth="0.4" fill="none" />
            </svg>

            {/* Coordinates label */}
            <div className="absolute top-3 left-3.5 px-2.5 py-1 bg-[rgba(26,46,75,0.85)] text-white text-[10px] rounded font-mono">
              &minus;33.8&deg; S &middot; 18.5&deg; E &middot; zoom 14
            </div>

            {/* Pins */}
            {PINS.map((pin, i) => {
              const isSelected = selected.erf === pin.p.erf;
              const color = pin.hot ? "#E23D36" : isSelected ? "#00B3D9" : "#1A2E4B";
              return (
                <MapPin
                  key={i}
                  color={color}
                  onClick={() => setSelected(pin.p)}
                  style={{ left: `${pin.x}%`, top: `${pin.y}%` }}
                />
              );
            })}
          </div>

          {/* Legend */}
          <div className="px-3.5 py-2.5 flex gap-3.5 text-[11px] text-text-muted items-center border-t border-border">
            <span className="inline-flex items-center gap-1.5">
              <span className="w-2.5 h-2.5 rounded-full bg-primary" />
              Owned
            </span>
            <span className="inline-flex items-center gap-1.5">
              <span className="w-2.5 h-2.5 rounded-full bg-accent" />
              Selected
            </span>
            <span className="inline-flex items-center gap-1.5">
              <span className="w-2.5 h-2.5 rounded-full bg-[#E23D36]" />
              Flagged &middot; sanctions match
            </span>
          </div>
        </Card>

        {/* Side panel */}
        <div className="flex flex-col gap-2.5">
          <Card className="overflow-hidden">
            {/* Tri-bar accent */}
            <div className="flex h-[3px]">
              <div className="flex-[3] bg-[#E23D36]" />
              <div className="flex-[5] bg-primary" />
              <div className="flex-[2] bg-accent" />
            </div>
            <CardBody>
              <div className="flex items-center justify-between">
                <span className="font-mono text-xs text-accent">{selected.erf}</span>
                <Badge variant="info" size="sm">{selected.type}</Badge>
              </div>
              <div className="text-sm font-semibold mt-1.5">{selected.address}</div>
              <div className="flex flex-col gap-1.5 mt-3 pt-3 border-t border-[#f1f5f9]">
                {(
                  [
                    ["Erf size", selected.size],
                    ["Municipal value", selected.municipal],
                    ["Bond status", selected.bond],
                    ["Registered", selected.registered],
                  ] as const
                ).map(([k, v]) => (
                  <div key={k} className="flex justify-between text-xs">
                    <span className="text-text-muted">{k}</span>
                    <b>{v}</b>
                  </div>
                ))}
              </div>
              <div className="mt-3.5 flex gap-1.5">
                <Button variant="primary" size="sm" className="flex-1">
                  Full deeds report
                </Button>
                <Button variant="secondary" size="sm">
                  Value it
                </Button>
              </div>
            </CardBody>
          </Card>

          <div className="bg-[#F8FAFC] border border-dashed border-[#CBD5E1] rounded-aws-container p-3.5 text-[11px] text-text-muted leading-relaxed">
            <b className="text-primary">Map data:</b> properties shown are illustrative. Live map integrates with Surveyor-General + Municipal GIS feeds.
          </div>
        </div>
      </div>
    </div>
  );
}
