"use client";

import { useState, useCallback } from "react";
import { ServicePageLayout } from "../ServicePageLayout";
import { Card, CardHeader, CardBody } from "@/components/ui/Card";
import { Badge } from "@/components/ui/Badge";
import { Button } from "@/components/ui/Button";
import { Input } from "@/components/ui/Input";
import { Select } from "@/components/ui/Select";
import { DataTable } from "@/components/ui/DataTable";
import { EmptyState } from "@/components/ui/EmptyState";
import { Skeleton } from "@/components/ui/Skeleton";
import { cn } from "@/lib/cn";
import {
  Search,
  MapPin,
  Building,
  Download,
  FileSearch,
  Loader2,
  Landmark,
  ArrowRight,
} from "lucide-react";
import type { ColumnDef } from "@tanstack/react-table";

/* ------------------------------------------------------------------ */
/*  Types                                                              */
/* ------------------------------------------------------------------ */

interface PropertySnapshot {
  erfNumber: string;
  portion: string;
  township: string;
  extent: string;
  registrationDate: string;
  titleDeedNumber: string;
  province: string;
  localAuthority: string;
}

interface OwnershipTransfer {
  date: string;
  transferType: string;
  buyer: string;
  seller: string;
  price: string;
  titleDeedNumber: string;
}

interface Bond {
  bondholder: string;
  amount: string;
  registrationDate: string;
  bondNumber: string;
  status: "active" | "cancelled";
}

interface DeedsResult {
  searchId: string;
  property: PropertySnapshot;
  ownershipTimeline: OwnershipTransfer[];
  bonds: Bond[];
}

interface DeedsHistoryRow {
  id: string;
  searchType: string;
  query: string;
  erfNumber: string;
  province: string;
  status: "success" | "warning" | "danger";
  statusLabel: string;
  searchedAt: string;
}

/* ------------------------------------------------------------------ */
/*  SA provinces                                                       */
/* ------------------------------------------------------------------ */

const SA_PROVINCES = [
  { value: "", label: "All provinces" },
  { value: "GP", label: "Gauteng" },
  { value: "WC", label: "Western Cape" },
  { value: "KZN", label: "KwaZulu-Natal" },
  { value: "EC", label: "Eastern Cape" },
  { value: "FS", label: "Free State" },
  { value: "LP", label: "Limpopo" },
  { value: "MP", label: "Mpumalanga" },
  { value: "NW", label: "North West" },
  { value: "NC", label: "Northern Cape" },
];

/* ------------------------------------------------------------------ */
/*  Demo data                                                          */
/* ------------------------------------------------------------------ */

const DEMO_RESULT: DeedsResult = {
  searchId: "DEEDS-2026-0087",
  property: {
    erfNumber: "ERF 1234/5",
    portion: "Portion 0 (Remaining Extent)",
    township: "Sandton Ext 14",
    extent: "1 250 m\u00B2",
    registrationDate: "2019-03-15",
    titleDeedNumber: "T12345/2019",
    province: "Gauteng",
    localAuthority: "City of Johannesburg Metropolitan Municipality",
  },
  ownershipTimeline: [
    {
      date: "2019-03-15",
      transferType: "Sale",
      buyer: "Lerato Mokoena",
      seller: "Sandton Property Holdings (Pty) Ltd",
      price: "R 3 450 000",
      titleDeedNumber: "T12345/2019",
    },
    {
      date: "2012-07-22",
      transferType: "Sale",
      buyer: "Sandton Property Holdings (Pty) Ltd",
      seller: "Johan van Wyk",
      price: "R 2 100 000",
      titleDeedNumber: "T78901/2012",
    },
    {
      date: "2005-11-03",
      transferType: "Transfer (Estate)",
      buyer: "Johan van Wyk",
      seller: "Estate Late Anna van Wyk",
      price: "R 0",
      titleDeedNumber: "T45678/2005",
    },
    {
      date: "1998-04-18",
      transferType: "Sale",
      buyer: "Anna van Wyk",
      seller: "Sandton Township Board",
      price: "R 680 000",
      titleDeedNumber: "T11223/1998",
    },
  ],
  bonds: [
    {
      bondholder: "Standard Bank of South Africa Ltd",
      amount: "R 2 760 000",
      registrationDate: "2019-03-15",
      bondNumber: "B56789/2019",
      status: "active",
    },
    {
      bondholder: "First National Bank Ltd",
      amount: "R 1 680 000",
      registrationDate: "2012-07-22",
      bondNumber: "B34567/2012",
      status: "cancelled",
    },
  ],
};

const DEMO_HISTORY: DeedsHistoryRow[] = [
  {
    id: "DEEDS-2026-0087",
    searchType: "ERF Number",
    query: "ERF 1234/5, Sandton Ext 14",
    erfNumber: "ERF 1234/5",
    province: "Gauteng",
    status: "success",
    statusLabel: "Found",
    searchedAt: "2026-05-20T09:15:00Z",
  },
  {
    id: "DEEDS-2026-0086",
    searchType: "Title Deed",
    query: "T98765/2024",
    erfNumber: "ERF 8901/0",
    province: "Western Cape",
    status: "success",
    statusLabel: "Found",
    searchedAt: "2026-05-19T16:42:00Z",
  },
  {
    id: "DEEDS-2026-0085",
    searchType: "Owner Name",
    query: "Thabo Khumalo",
    erfNumber: "ERF 4567/2",
    province: "KwaZulu-Natal",
    status: "success",
    statusLabel: "Found",
    searchedAt: "2026-05-19T14:08:00Z",
  },
  {
    id: "DEEDS-2026-0084",
    searchType: "Address",
    query: "14 Protea Rd, Centurion",
    erfNumber: "\u2014",
    province: "Gauteng",
    status: "danger",
    statusLabel: "Not found",
    searchedAt: "2026-05-18T11:30:00Z",
  },
  {
    id: "DEEDS-2026-0083",
    searchType: "ERF Number",
    query: "ERF 7890/1, Paarl",
    erfNumber: "ERF 7890/1",
    province: "Western Cape",
    status: "warning",
    statusLabel: "Partial",
    searchedAt: "2026-05-17T10:05:00Z",
  },
  {
    id: "DEEDS-2026-0082",
    searchType: "Owner Name",
    query: "Anele Zulu",
    erfNumber: "ERF 3210/0",
    province: "Eastern Cape",
    status: "success",
    statusLabel: "Found",
    searchedAt: "2026-05-16T15:48:00Z",
  },
];

/* ------------------------------------------------------------------ */
/*  History columns                                                    */
/* ------------------------------------------------------------------ */

const historyColumns: ColumnDef<DeedsHistoryRow, unknown>[] = [
  {
    accessorKey: "id",
    header: "Search ID",
    cell: ({ getValue }) => (
      <span className="font-mono text-xs text-accent">
        {getValue<string>()}
      </span>
    ),
  },
  {
    accessorKey: "searchType",
    header: "Type",
    cell: ({ getValue }) => (
      <span className="text-xs font-medium">{getValue<string>()}</span>
    ),
  },
  {
    accessorKey: "query",
    header: "Query",
    cell: ({ getValue }) => (
      <span className="text-xs text-text-muted">{getValue<string>()}</span>
    ),
  },
  {
    accessorKey: "erfNumber",
    header: "ERF",
    cell: ({ getValue }) => (
      <span className="font-mono text-xs">{getValue<string>()}</span>
    ),
  },
  {
    accessorKey: "province",
    header: "Province",
  },
  {
    accessorKey: "status",
    header: "Result",
    cell: ({ row }) => (
      <Badge variant={row.original.status}>{row.original.statusLabel}</Badge>
    ),
  },
  {
    accessorKey: "searchedAt",
    header: "Searched",
    cell: ({ getValue }) =>
      new Date(getValue<string>()).toLocaleString("en-ZA", {
        dateStyle: "short",
        timeStyle: "short",
      }),
  },
];

/* ------------------------------------------------------------------ */
/*  Property snapshot card                                             */
/* ------------------------------------------------------------------ */

function PropertySnapshotCard({ property }: { property: PropertySnapshot }) {
  const fields: [string, string][] = [
    ["ERF Number", property.erfNumber],
    ["Portion", property.portion],
    ["Township", property.township],
    ["Extent", property.extent],
    ["Title Deed No.", property.titleDeedNumber],
    ["Registration Date", new Date(property.registrationDate).toLocaleDateString("en-ZA", { dateStyle: "long" })],
    ["Province", property.province],
    ["Local Authority", property.localAuthority],
  ];

  return (
    <Card>
      <CardHeader>
        <div className="flex items-center gap-2">
          <Building size={14} className="text-accent" />
          <span className="text-[13px] font-semibold">Property snapshot</span>
        </div>
      </CardHeader>
      <div className="grid grid-cols-2">
        {fields.map(([key, val], i) => (
          <div
            key={key}
            className={cn(
              "px-4 py-2.5",
              i >= 2 && "border-t border-[#f1f5f9]",
              i % 2 === 1 && "border-l border-border",
            )}
          >
            <div className="text-[10px] font-semibold uppercase tracking-wide text-text-muted">
              {key}
            </div>
            <div className="text-[13px] font-medium text-text mt-0.5">
              {val}
            </div>
          </div>
        ))}
      </div>
    </Card>
  );
}

/* ------------------------------------------------------------------ */
/*  Ownership timeline                                                 */
/* ------------------------------------------------------------------ */

function OwnershipTimeline({ transfers }: { transfers: OwnershipTransfer[] }) {
  return (
    <Card>
      <CardHeader>
        <div className="flex items-center gap-2">
          <Landmark size={14} className="text-accent" />
          <span className="text-[13px] font-semibold">Ownership timeline</span>
        </div>
        <Badge variant="info" noGlyph>
          {transfers.length} transfers
        </Badge>
      </CardHeader>
      <div>
        {transfers.map((transfer, i) => (
          <div
            key={i}
            className="flex items-start gap-3 px-4 py-3 border-b border-[#f1f5f9] last:border-b-0"
          >
            {/* Timeline dot + line */}
            <div className="flex flex-col items-center pt-0.5 shrink-0">
              <div
                className={cn(
                  "w-2.5 h-2.5 rounded-full border-2",
                  i === 0
                    ? "bg-accent border-accent"
                    : "bg-white border-[#d5dbdb]",
                )}
              />
              {i < transfers.length - 1 && (
                <div className="w-px flex-1 bg-[#e9ebed] min-h-[28px]" />
              )}
            </div>

            {/* Transfer detail */}
            <div className="flex-1 min-w-0">
              <div className="flex items-center gap-2 mb-0.5">
                <span className="text-xs font-semibold text-text">
                  {new Date(transfer.date).toLocaleDateString("en-ZA", {
                    dateStyle: "medium",
                  })}
                </span>
                <Badge
                  variant={transfer.transferType === "Sale" ? "info" : "neutral"}
                  noGlyph
                >
                  {transfer.transferType}
                </Badge>
              </div>
              <div className="text-xs text-text-muted flex items-center gap-1.5 flex-wrap">
                <span className="font-medium text-text">{transfer.seller}</span>
                <ArrowRight size={10} className="text-[#a0a7ad] shrink-0" />
                <span className="font-medium text-text">{transfer.buyer}</span>
              </div>
              <div className="flex items-center gap-3 mt-1 text-[11px] text-text-muted">
                {transfer.price !== "R 0" && (
                  <span className="font-semibold text-text">
                    {transfer.price}
                  </span>
                )}
                <span className="font-mono">{transfer.titleDeedNumber}</span>
              </div>
            </div>
          </div>
        ))}
      </div>
    </Card>
  );
}

/* ------------------------------------------------------------------ */
/*  Bonds summary card                                                 */
/* ------------------------------------------------------------------ */

function BondsSummaryCard({ bonds }: { bonds: Bond[] }) {
  return (
    <Card>
      <CardHeader>
        <div className="flex items-center gap-2">
          <Landmark size={14} className="text-accent" />
          <span className="text-[13px] font-semibold">Bonds & mortgages</span>
        </div>
        <Badge variant="info" noGlyph>
          {bonds.filter((b) => b.status === "active").length} active
        </Badge>
      </CardHeader>
      <div>
        {bonds.map((bond, i) => (
          <div
            key={i}
            className="px-4 py-3 border-b border-[#f1f5f9] last:border-b-0"
          >
            <div className="flex items-center justify-between mb-1">
              <span className="text-xs font-semibold text-text">
                {bond.bondholder}
              </span>
              <Badge
                variant={bond.status === "active" ? "warning" : "neutral"}
              >
                {bond.status === "active" ? "Active" : "Cancelled"}
              </Badge>
            </div>
            <div className="grid grid-cols-3 gap-3 mt-2">
              <div>
                <div className="text-[10px] font-semibold uppercase tracking-wide text-text-muted">
                  Amount
                </div>
                <div className="text-[13px] font-semibold text-text mt-0.5">
                  {bond.amount}
                </div>
              </div>
              <div>
                <div className="text-[10px] font-semibold uppercase tracking-wide text-text-muted">
                  Bond No.
                </div>
                <div className="text-[13px] font-mono text-text mt-0.5">
                  {bond.bondNumber}
                </div>
              </div>
              <div>
                <div className="text-[10px] font-semibold uppercase tracking-wide text-text-muted">
                  Registered
                </div>
                <div className="text-[13px] text-text mt-0.5">
                  {new Date(bond.registrationDate).toLocaleDateString("en-ZA", {
                    dateStyle: "medium",
                  })}
                </div>
              </div>
            </div>
          </div>
        ))}
      </div>
    </Card>
  );
}

/* ------------------------------------------------------------------ */
/*  Result panel                                                       */
/* ------------------------------------------------------------------ */

function ResultPanel({
  status,
  result,
}: {
  status: "idle" | "loading" | "result";
  result: DeedsResult | null;
}) {
  if (status === "idle") {
    return (
      <EmptyState
        icon={FileSearch}
        title="No results yet"
        body="Enter an ERF number, title deed number, owner name, or property address to search the South African Deeds Office registry."
      />
    );
  }

  if (status === "loading") {
    return (
      <Card>
        <CardBody>
          <div className="flex items-center gap-2 text-accent text-xs font-semibold mb-4">
            <Loader2 size={14} className="animate-spin" />
            Searching Deeds Office registry...
          </div>
          <div className="space-y-3">
            <Skeleton className="h-3 w-4/5" />
            <Skeleton className="h-3 w-3/5" />
            <Skeleton className="h-3 w-[70%]" />
            <Skeleton className="h-3 w-2/4" />
            <Skeleton className="h-3 w-[60%]" />
          </div>
        </CardBody>
      </Card>
    );
  }

  if (!result) return null;

  return (
    <div className="space-y-3">
      {/* Summary header */}
      <Card>
        <div className="h-[3px] flex">
          <div className="flex-[3] bg-[#E23D36]" />
          <div className="flex-[5] bg-[#1A2E4B]" />
          <div className="flex-[2] bg-accent" />
        </div>
        <div className="p-4 flex items-start justify-between">
          <div>
            <div className="flex items-center gap-2.5 mb-1">
              <span className="text-sm font-semibold text-text">
                Deeds search result
              </span>
              <Badge variant="success">Found</Badge>
            </div>
            <span className="font-mono text-xs text-accent">
              {result.searchId}
            </span>
          </div>
          <Button
            variant="secondary"
            size="sm"
            icon={<Download size={12} />}
          >
            Export PDF
          </Button>
        </div>
      </Card>

      {/* Property snapshot */}
      <PropertySnapshotCard property={result.property} />

      {/* Ownership timeline */}
      <OwnershipTimeline transfers={result.ownershipTimeline} />

      {/* Bonds summary */}
      <BondsSummaryCard bonds={result.bonds} />
    </div>
  );
}

/* ------------------------------------------------------------------ */
/*  Main Deeds page                                                    */
/* ------------------------------------------------------------------ */

export function DeedsPage() {
  const [tab, setTab] = useState("new");
  const [erfNumber, setErfNumber] = useState("");
  const [titleDeedNumber, setTitleDeedNumber] = useState("");
  const [ownerName, setOwnerName] = useState("");
  const [address, setAddress] = useState("");
  const [province, setProvince] = useState("");
  const [status, setStatus] = useState<"idle" | "loading" | "result">("idle");
  const [result, setResult] = useState<DeedsResult | null>(null);

  const hasInput =
    erfNumber.trim().length > 0 ||
    titleDeedNumber.trim().length > 0 ||
    ownerName.trim().length > 0 ||
    address.trim().length > 0;

  const handleSubmit = useCallback(
    (e: React.FormEvent) => {
      e.preventDefault();
      if (!hasInput) return;
      setStatus("loading");
      const timer = setTimeout(() => {
        setResult(DEMO_RESULT);
        setStatus("result");
      }, 1800);
      return () => clearTimeout(timer);
    },
    [hasInput],
  );

  const handleReset = useCallback(() => {
    setErfNumber("");
    setTitleDeedNumber("");
    setOwnerName("");
    setAddress("");
    setProvince("");
    setStatus("idle");
    setResult(null);
  }, []);

  const tabs = [
    { label: "New search", value: "new" },
    { label: "History", value: "history", count: DEMO_HISTORY.length },
  ];

  return (
    <ServicePageLayout
      category="Enterprise \u00B7 Deeds"
      title="Deeds & Property Search"
      description="Search the South African Deeds Office registry for property ownership, title deeds, transfers, and bond information."
      tabs={tabs}
      activeTab={tab}
      onTabChange={setTab}
    >
      {tab === "new" && (
        <div className="grid grid-cols-1 lg:grid-cols-[minmax(0,440px)_minmax(0,1fr)] gap-4 items-start">
          {/* Search form */}
          <Card>
            <CardHeader>
              <div>
                <div className="text-sm font-semibold text-text">
                  Search criteria
                </div>
                <div className="text-[11px] text-text-muted mt-0.5">
                  Enter at least one field to search the registry.
                </div>
              </div>
            </CardHeader>
            <CardBody>
              <form onSubmit={handleSubmit} className="space-y-4">
                <Input
                  label="ERF Number"
                  placeholder="e.g. ERF 1234/5"
                  value={erfNumber}
                  onChange={(e) => setErfNumber(e.target.value)}
                  hint="ERF or stand number with extension."
                />

                <Input
                  label="Title Deed Number"
                  placeholder="e.g. T12345/2019"
                  value={titleDeedNumber}
                  onChange={(e) => setTitleDeedNumber(e.target.value)}
                  hint="As registered at the Deeds Office."
                />

                <Input
                  label="Owner Name"
                  placeholder="e.g. Lerato Mokoena"
                  value={ownerName}
                  onChange={(e) => setOwnerName(e.target.value)}
                  hint="Current or historic registered owner."
                />

                <Input
                  label="Property Address"
                  placeholder="e.g. 14 Protea Rd, Sandton"
                  value={address}
                  onChange={(e) => setAddress(e.target.value)}
                  hint="Street address or physical location."
                />

                <Select
                  label="Province"
                  options={SA_PROVINCES}
                  value={province}
                  onChange={(e) => setProvince(e.target.value)}
                />

                <div className="flex items-center justify-between pt-3 border-t border-border">
                  <span className="text-[11px] text-text-muted">
                    R 25.00 per search &mdash; result in ~5 seconds
                  </span>
                  <div className="flex items-center gap-2">
                    {(hasInput || status === "result") && (
                      <Button
                        variant="secondary"
                        size="sm"
                        type="button"
                        onClick={handleReset}
                      >
                        Clear
                      </Button>
                    )}
                    <Button
                      variant="cta"
                      type="submit"
                      disabled={!hasInput || status === "loading"}
                      icon={<Search size={13} />}
                    >
                      {status === "loading" ? "Searching..." : "Search deeds"}
                    </Button>
                  </div>
                </div>
              </form>
            </CardBody>
          </Card>

          {/* Result panel */}
          <ResultPanel status={status} result={result} />
        </div>
      )}

      {tab === "history" && (
        <Card>
          <CardHeader>
            <div>
              <div className="text-sm font-semibold text-text">
                Search history
              </div>
              <div className="text-[11px] text-text-muted mt-0.5">
                {DEMO_HISTORY.length} past searches
              </div>
            </div>
          </CardHeader>
          <CardBody compact>
            <DataTable
              data={DEMO_HISTORY}
              columns={historyColumns}
              pageSize={10}
            />
          </CardBody>
        </Card>
      )}
    </ServicePageLayout>
  );
}
