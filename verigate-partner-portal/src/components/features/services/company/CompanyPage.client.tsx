"use client";

import { useState, useMemo, useCallback } from "react";
import { ServicePageLayout } from "../ServicePageLayout";
import { Card, CardHeader, CardBody } from "@/components/ui/Card";
import { Badge } from "@/components/ui/Badge";
import { Button } from "@/components/ui/Button";
import { Input } from "@/components/ui/Input";
import { DataTable } from "@/components/ui/DataTable";
import { EmptyState } from "@/components/ui/EmptyState";
import { Skeleton } from "@/components/ui/Skeleton";
import { cn } from "@/lib/cn";
import {
  Building2,
  CheckCircle,
  X,
  Download,
  FileSearch,
  Loader2,
  Users,
} from "lucide-react";
import type { ColumnDef } from "@tanstack/react-table";

/* ------------------------------------------------------------------ */
/*  Types                                                              */
/* ------------------------------------------------------------------ */

interface Director {
  name: string;
  idNumber: string;
  role: string;
  appointedDate: string;
  status: "Active" | "Resigned";
}

interface CompanyResult {
  runId: string;
  registrationStatus: string;
  beeLevel: string;
  registrationDate: string;
  companyType: string;
  directors: Director[];
  meta: [string, string][];
}

/**
 * A single candidate returned by a name search, before the full profile is loaded.
 * Company names aren't unique in CIPC, so a name search can (and often does) return
 * multiple candidates -- this is deliberately a separate, lighter type from
 * CompanyResult so the UI can render a selectable list rather than silently resolving
 * to one company.
 */
interface CompanySearchMatch {
  regNumber: string;
  companyName: string;
  companyType: string;
  status: "success" | "warning" | "danger";
  statusLabel: string;
}

interface CompanyHistoryRow {
  id: string;
  companyName: string;
  regNumber: string;
  regStatus: string;
  status: "success" | "warning" | "danger";
  statusLabel: string;
  checkedAt: string;
}

/* ------------------------------------------------------------------ */
/*  Demo data                                                          */
/* ------------------------------------------------------------------ */

const DEMO_RESULT: CompanyResult = {
  runId: "CIPC-2026-0091",
  registrationStatus: "In business",
  beeLevel: "Level 2",
  registrationDate: "2015-08-22",
  companyType: "Private Company (Pty) Ltd",
  directors: [
    { name: "Mandla Tshabalala", idNumber: "7304180500081", role: "Director", appointedDate: "2015-08-22", status: "Active" },
    { name: "Naledi Nkosi", idNumber: "9304117500084", role: "Director", appointedDate: "2018-03-15", status: "Active" },
    { name: "Pieter van der Merwe", idNumber: "8511256100089", role: "Director", appointedDate: "2015-08-22", status: "Resigned" },
  ],
  meta: [
    ["Company name", "Mzansi Tech Solutions (Pty) Ltd"],
    ["Registration number", "2015/123456/07"],
    ["Company type", "Private Company (Pty) Ltd"],
    ["Registration date", "2015-08-22"],
  ],
};

/* ------------------------------------------------------------------ */
/*  Name search demo data                                              */
/* ------------------------------------------------------------------ */

/**
 * IMPORTANT: this is demo data standing in for a real gap, not just an unwired fetch
 * call like most of this page's other demo data. CIPC's own government API (see
 * verigate-adapter-cipc) has no name-search endpoint at all -- every endpoint
 * (/companyprofile, /information, /directors, etc.) is keyed by enterprise number
 * only. Real "search by name" needs a different data source entirely; Datanamix's
 * CIPC Search/Plus products were identified as a candidate during the story 2.1 gap
 * analysis, but have zero code integration anywhere in this repo today. Don't treat
 * wiring this page up as "just add a fetch()" -- it depends on that vendor decision.
 */
const COMPANY_PROFILES: Record<string, CompanyResult> = {
  "2015/123456/07": DEMO_RESULT,
  "CK2011/045678": {
    runId: "CIPC-2026-0092",
    registrationStatus: "Deregistered",
    beeLevel: "Not rated",
    registrationDate: "2011-11-03",
    companyType: "Close Corporation",
    directors: [
      { name: "Sipho Dlamini", idNumber: "8002145600082", role: "Member", appointedDate: "2011-11-03", status: "Resigned" },
    ],
    meta: [
      ["Company name", "Mzansi Trading CC"],
      ["Registration number", "CK2011/045678"],
      ["Company type", "Close Corporation"],
      ["Registration date", "2011-11-03"],
    ],
  },
  "2019/876543/07": {
    runId: "CIPC-2026-0093",
    registrationStatus: "In business",
    beeLevel: "Level 4",
    registrationDate: "2019-06-14",
    companyType: "Private Company (Pty) Ltd",
    directors: [
      { name: "Thandeka Mokoena", idNumber: "9110225800083", role: "Director", appointedDate: "2019-06-14", status: "Active" },
      { name: "Riaan Botha", idNumber: "8709035100087", role: "Director", appointedDate: "2021-02-01", status: "Active" },
      // Same person as the Mzansi Tech Solutions director below (same idNumber) -- deliberate,
      // so the director-search demo has a realistic multi-company case to show.
      { name: "Mandla Tshabalala", idNumber: "7304180500081", role: "Non-Executive Director", appointedDate: "2022-09-01", status: "Active" },
    ],
    meta: [
      ["Company name", "Mzansi Holdings (Pty) Ltd"],
      ["Registration number", "2019/876543/07"],
      ["Company type", "Private Company (Pty) Ltd"],
      ["Registration date", "2019-06-14"],
    ],
  },
};

const COMPANY_SEARCH_MATCHES: CompanySearchMatch[] = [
  { regNumber: "2015/123456/07", companyName: "Mzansi Tech Solutions (Pty) Ltd", companyType: "Private Company (Pty) Ltd", status: "success", statusLabel: "Active" },
  { regNumber: "CK2011/045678", companyName: "Mzansi Trading CC", companyType: "Close Corporation", status: "danger", statusLabel: "Deregistered" },
  { regNumber: "2019/876543/07", companyName: "Mzansi Holdings (Pty) Ltd", companyType: "Private Company (Pty) Ltd", status: "success", statusLabel: "Active" },
];

function searchCompaniesByName(query: string): CompanySearchMatch[] {
  const normalized = query.trim().toLowerCase();
  if (!normalized) return [];
  return COMPANY_SEARCH_MATCHES.filter((m) =>
    m.companyName.toLowerCase().includes(normalized),
  );
}

const DEMO_HISTORY: CompanyHistoryRow[] = [
  { id: "CIPC-2026-0090", companyName: "Cape Digital (Pty) Ltd", regNumber: "2018/654321/07", regStatus: "In business", status: "success", statusLabel: "Active", checkedAt: "2026-05-18T14:50:00Z" },
  { id: "CIPC-2026-0089", companyName: "Joburg Investments CC", regNumber: "CK2010/098765", regStatus: "Deregistered", status: "danger", statusLabel: "Deregistered", checkedAt: "2026-05-18T13:15:00Z" },
  { id: "CIPC-2026-0088", companyName: "Pretoria Logistics (Pty) Ltd", regNumber: "2020/234567/07", regStatus: "In business", status: "success", statusLabel: "Active", checkedAt: "2026-05-18T10:40:00Z" },
  { id: "CIPC-2026-0087", companyName: "Durban Exports NPC", regNumber: "2019/345678/08", regStatus: "In business", status: "warning", statusLabel: "AR overdue", checkedAt: "2026-05-17T16:30:00Z" },
  { id: "CIPC-2026-0086", companyName: "Eastern Cape Mining (Pty) Ltd", regNumber: "2012/456789/07", regStatus: "In business", status: "success", statusLabel: "Active", checkedAt: "2026-05-17T11:20:00Z" },
];

/* ------------------------------------------------------------------ */
/*  History columns                                                    */
/* ------------------------------------------------------------------ */

const historyColumns: ColumnDef<CompanyHistoryRow, unknown>[] = [
  {
    accessorKey: "id",
    header: "ID",
    cell: ({ getValue }) => (
      <span className="font-mono text-xs text-accent">{getValue<string>()}</span>
    ),
  },
  {
    accessorKey: "companyName",
    header: "Company",
    cell: ({ getValue }) => (
      <span className="font-medium">{getValue<string>()}</span>
    ),
  },
  {
    accessorKey: "regNumber",
    header: "Reg Number",
    cell: ({ getValue }) => (
      <span className="font-mono text-xs text-text-muted">{getValue<string>()}</span>
    ),
  },
  {
    accessorKey: "regStatus",
    header: "Reg Status",
  },
  {
    accessorKey: "status",
    header: "Status",
    cell: ({ row }) => (
      <Badge variant={row.original.status}>{row.original.statusLabel}</Badge>
    ),
  },
  {
    accessorKey: "checkedAt",
    header: "Checked",
    cell: ({ getValue }) =>
      new Date(getValue<string>()).toLocaleString("en-ZA", {
        dateStyle: "short",
        timeStyle: "short",
      }),
  },
];

/* ------------------------------------------------------------------ */
/*  Result panel                                                       */
/* ------------------------------------------------------------------ */

function MatchListCard({
  matches,
  query,
  onSelect,
}: {
  matches: CompanySearchMatch[];
  query: string;
  onSelect: (match: CompanySearchMatch) => void;
}) {
  if (matches.length === 0) {
    return (
      <EmptyState
        icon={FileSearch}
        title="No companies found"
        body={`No CIPC-registered companies matched "${query}". Try a shorter or different part of the name.`}
      />
    );
  }

  return (
    <Card>
      <CardHeader>
        <div className="flex items-center justify-between w-full">
          <span className="text-sm font-semibold">
            {matches.length} {matches.length === 1 ? "match" : "matches"} for &quot;{query}&quot;
          </span>
          <span className="text-[11px] text-text-muted">Select one to view full details</span>
        </div>
      </CardHeader>
      <div>
        {matches.map((m, i) => (
          <button
            key={m.regNumber}
            type="button"
            onClick={() => onSelect(m)}
            className={cn(
              "w-full text-left px-4 py-3 flex items-center justify-between gap-3 hover:bg-surface-alt transition-colors",
              i > 0 && "border-t border-[#f1f5f9]",
            )}
          >
            <div>
              <div className="text-[13px] font-semibold text-text">{m.companyName}</div>
              <div className="flex gap-4 text-[11px] text-text-muted mt-0.5">
                <span className="font-mono">{m.regNumber}</span>
                <span>{m.companyType}</span>
              </div>
            </div>
            <Badge variant={m.status} size="sm">
              {m.statusLabel}
            </Badge>
          </button>
        ))}
      </div>
    </Card>
  );
}

function ResultPanel({
  status,
  result,
  matches,
  query,
  onSelectMatch,
}: {
  status: "idle" | "loading" | "matches" | "result";
  result: CompanyResult | null;
  matches: CompanySearchMatch[];
  query: string;
  onSelectMatch: (match: CompanySearchMatch) => void;
}) {
  if (status === "idle") {
    return (
      <EmptyState
        icon={FileSearch}
        title="No results yet"
        body="Enter a CIPC registration number or company name on the left to verify company details and retrieve the directors list."
      />
    );
  }

  if (status === "loading") {
    return (
      <Card>
        <CardBody>
          <div className="flex items-center gap-2 text-accent text-xs font-semibold mb-4">
            <Loader2 size={14} className="animate-spin" />
            Querying CIPC records...
          </div>
          <div className="space-y-3">
            <Skeleton className="h-3 w-4/5" />
            <Skeleton className="h-3 w-3/5" />
            <Skeleton className="h-3 w-[70%]" />
            <Skeleton className="h-3 w-2/4" />
          </div>
        </CardBody>
      </Card>
    );
  }

  if (status === "matches") {
    return <MatchListCard matches={matches} query={query} onSelect={onSelectMatch} />;
  }

  if (!result) return null;

  const active = result.registrationStatus === "In business";

  return (
    <div className="space-y-3">
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
                Company verification result
              </span>
              <Badge variant={active ? "success" : "danger"}>
                {result.registrationStatus}
              </Badge>
            </div>
            <span className="font-mono text-xs text-accent">
              {result.runId}
            </span>
          </div>
          <Button variant="secondary" size="sm" icon={<Download size={12} />}>
            Export PDF
          </Button>
        </div>

        <div className="grid grid-cols-2 border-t border-border">
          {result.meta.map(([key, val], i) => (
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

      {/* Company details */}
      <Card>
        <CardHeader>
          <span className="text-[13px] font-semibold">Company details</span>
        </CardHeader>
        <div>
          {[
            { label: "Registration status", value: result.registrationStatus, pass: active },
            { label: "BEE level", value: result.beeLevel, pass: true },
            { label: "Registration date", value: result.registrationDate, pass: true },
            { label: "Company type", value: result.companyType, pass: true },
          ].map((row) => (
            <div
              key={row.label}
              className="flex items-center gap-2.5 px-4 py-2.5 border-b border-[#f1f5f9] last:border-b-0 text-xs"
            >
              {row.pass ? (
                <CheckCircle size={14} className="text-[#2C974B] shrink-0" />
              ) : (
                <X size={14} className="text-[#E23D36] shrink-0" />
              )}
              <span className="flex-1 text-text">{row.label}</span>
              <span className="font-mono text-text-muted">{row.value}</span>
            </div>
          ))}
        </div>
      </Card>

      {/* Directors */}
      <Card>
        <CardHeader>
          <div className="flex items-center gap-2">
            <Users size={14} className="text-accent" />
            <span className="text-[13px] font-semibold">
              Directors ({result.directors.length})
            </span>
          </div>
        </CardHeader>
        <div>
          {result.directors.map((d, i) => (
            <div
              key={d.idNumber}
              className={cn(
                "px-4 py-3",
                i > 0 && "border-t border-[#f1f5f9]",
              )}
            >
              <div className="flex items-center justify-between mb-1">
                <span className="text-[13px] font-semibold text-text">
                  {d.name}
                </span>
                <Badge
                  variant={d.status === "Active" ? "success" : "neutral"}
                  size="sm"
                >
                  {d.status}
                </Badge>
              </div>
              <div className="flex gap-4 text-[11px] text-text-muted">
                <span>
                  ID: <span className="font-mono">{d.idNumber}</span>
                </span>
                <span>Role: {d.role}</span>
                <span>Appointed: {d.appointedDate}</span>
              </div>
            </div>
          ))}
        </div>
      </Card>
    </div>
  );
}

/* ------------------------------------------------------------------ */
/*  Director search (story 2.3) — reverse lookup: ID number -> every   */
/*  company that person is or was a director of                       */
/* ------------------------------------------------------------------ */

interface DirectorCompanyEntry {
  companyName: string;
  regNumber: string;
  companyType: string;
  role: string;
  appointedDate: string;
  status: "Active" | "Resigned";
}

function metaValue(profile: CompanyResult, key: string): string {
  return profile.meta.find(([k]) => k === key)?.[1] ?? "";
}

/**
 * IMPORTANT: demo data standing in for a real gap, same treatment as story 2.2's name
 * search. Neither CIPC's own government API nor verigate-adapter-cipc's
 * DirectorshipValidationService supports this reverse direction -- CIPC's endpoints are
 * all keyed by enterprise number, and the existing service only validates one specific
 * company+ID pair at a time (it can't answer "every company for this ID"). Datanamix's
 * CIPC Director Search product was flagged during the story 2.1 gap analysis as
 * returning director records by ID number and is the real candidate source, but has
 * zero code integration in this repo. Don't treat this as "just needs a fetch()".
 */
function searchDirectorshipsByIdNumber(idNumber: string): DirectorCompanyEntry[] {
  const entries: DirectorCompanyEntry[] = [];
  for (const profile of Object.values(COMPANY_PROFILES)) {
    for (const director of profile.directors) {
      if (director.idNumber === idNumber) {
        entries.push({
          companyName: metaValue(profile, "Company name"),
          regNumber: metaValue(profile, "Registration number"),
          companyType: profile.companyType,
          role: director.role,
          appointedDate: director.appointedDate,
          status: director.status,
        });
      }
    }
  }
  return entries;
}

function DirectorSearchTab() {
  const [idNumber, setIdNumber] = useState("");
  const [status, setStatus] = useState<"idle" | "loading" | "result">("idle");
  const [results, setResults] = useState<DirectorCompanyEntry[]>([]);
  const [searchedId, setSearchedId] = useState("");

  const isValid = idNumber.trim().length === 13;

  const handleSubmit = useCallback(
    (e: React.FormEvent) => {
      e.preventDefault();
      if (!isValid) return;
      setStatus("loading");
      const id = idNumber.trim();
      const timer = setTimeout(() => {
        setSearchedId(id);
        setResults(searchDirectorshipsByIdNumber(id));
        setStatus("result");
      }, 900);
      return () => clearTimeout(timer);
    },
    [idNumber, isValid],
  );

  return (
    <div className="grid grid-cols-1 lg:grid-cols-[minmax(0,440px)_minmax(0,1fr)] gap-4 items-start">
      <Card>
        <CardHeader>
          <div>
            <div className="text-sm font-semibold text-text">Director search</div>
            <div className="text-[11px] text-text-muted mt-0.5">
              Search by South African ID number to list every company the person is or
              was a director of.
            </div>
          </div>
        </CardHeader>
        <CardBody>
          <form onSubmit={handleSubmit} className="space-y-4">
            <Input
              label="ID number *"
              placeholder="13-digit SA ID"
              value={idNumber}
              onChange={(e) => setIdNumber(e.target.value.replace(/\D/g, "").slice(0, 13))}
              hint="We'll list every CIPC-registered company with a matching director record."
              className="font-mono"
            />
            <div className="flex items-center justify-between pt-2 border-t border-border">
              <span className="text-[11px] text-text-muted">
                R 10.00 per lookup -- result in ~3 seconds
              </span>
              <Button
                variant="cta"
                type="submit"
                disabled={!isValid || status === "loading"}
                icon={<Users size={13} />}
              >
                {status === "loading" ? "Searching..." : "Search"}
              </Button>
            </div>
          </form>
        </CardBody>
      </Card>

      {status === "idle" && (
        <EmptyState
          icon={FileSearch}
          title="No results yet"
          body="Enter a 13-digit ID number on the left to list every company that person directs."
        />
      )}

      {status === "loading" && (
        <Card>
          <CardBody>
            <div className="flex items-center gap-2 text-accent text-xs font-semibold mb-4">
              <Loader2 size={14} className="animate-spin" />
              Searching CIPC director records...
            </div>
            <div className="space-y-3">
              <Skeleton className="h-3 w-4/5" />
              <Skeleton className="h-3 w-3/5" />
              <Skeleton className="h-3 w-[70%]" />
            </div>
          </CardBody>
        </Card>
      )}

      {status === "result" && results.length === 0 && (
        <EmptyState
          icon={FileSearch}
          title="No directorship records found"
          body={`No CIPC-registered companies have a director record matching ID ${searchedId}.`}
        />
      )}

      {status === "result" && results.length > 0 && (
        <Card>
          <CardHeader>
            <span className="text-sm font-semibold">
              {results.length} {results.length === 1 ? "company" : "companies"} found for
              ID <span className="font-mono">{searchedId}</span>
            </span>
          </CardHeader>
          <div>
            {results.map((r, i) => (
              <div
                key={r.regNumber}
                className={cn("px-4 py-3", i > 0 && "border-t border-[#f1f5f9]")}
              >
                <div className="flex items-center justify-between mb-1">
                  <span className="text-[13px] font-semibold text-text">{r.companyName}</span>
                  <Badge variant={r.status === "Active" ? "success" : "neutral"} size="sm">
                    {r.status}
                  </Badge>
                </div>
                <div className="flex flex-wrap gap-4 text-[11px] text-text-muted">
                  <span className="font-mono">{r.regNumber}</span>
                  <span>{r.companyType}</span>
                  <span>Role: {r.role}</span>
                  <span>Appointed: {r.appointedDate}</span>
                </div>
              </div>
            ))}
          </div>
        </Card>
      )}
    </div>
  );
}

/* ------------------------------------------------------------------ */
/*  Main page                                                          */
/* ------------------------------------------------------------------ */

export function CompanyPage() {
  const [tab, setTab] = useState("new");
  const [searchMode, setSearchMode] = useState<"regNumber" | "name">("regNumber");
  const [regNumber, setRegNumber] = useState("");
  const [nameQuery, setNameQuery] = useState("");
  const [status, setStatus] = useState<"idle" | "loading" | "matches" | "result">("idle");
  const [result, setResult] = useState<CompanyResult | null>(null);
  const [matches, setMatches] = useState<CompanySearchMatch[]>([]);
  const [submittedQuery, setSubmittedQuery] = useState("");

  const isValid = useMemo(
    () =>
      searchMode === "regNumber"
        ? regNumber.trim().length >= 10
        : nameQuery.trim().length >= 2,
    [searchMode, regNumber, nameQuery],
  );

  const handleSubmit = useCallback(
    (e: React.FormEvent) => {
      e.preventDefault();
      if (!isValid) return;
      setStatus("loading");

      if (searchMode === "regNumber") {
        const timer = setTimeout(() => {
          setResult(COMPANY_PROFILES[regNumber.trim()] ?? DEMO_RESULT);
          setStatus("result");
        }, 1200);
        return () => clearTimeout(timer);
      }

      const query = nameQuery.trim();
      const timer = setTimeout(() => {
        setSubmittedQuery(query);
        setMatches(searchCompaniesByName(query));
        setStatus("matches");
      }, 1200);
      return () => clearTimeout(timer);
    },
    [isValid, searchMode, regNumber, nameQuery],
  );

  const handleSelectMatch = useCallback((match: CompanySearchMatch) => {
    setStatus("loading");
    const timer = setTimeout(() => {
      setResult(COMPANY_PROFILES[match.regNumber] ?? DEMO_RESULT);
      setStatus("result");
    }, 600);
    return () => clearTimeout(timer);
  }, []);

  const tabs = [
    { label: "New verification", value: "new" },
    { label: "Director search", value: "director-search" },
    { label: "History", value: "history", count: DEMO_HISTORY.length },
  ];

  return (
    <ServicePageLayout
      category="Corporate & Business"
      title="Company & Directors Verification"
      description="Verify company registration with CIPC. Retrieve registration status, directors list, BEE level, and company type."
      tabs={tabs}
      activeTab={tab}
      onTabChange={setTab}
    >
      {tab === "new" && (
        <div className="grid grid-cols-1 lg:grid-cols-[minmax(0,440px)_minmax(0,1fr)] gap-4 items-start">
          <Card>
            <CardHeader>
              <div>
                <div className="text-sm font-semibold text-text">
                  Company details
                </div>
                <div className="text-[11px] text-text-muted mt-0.5">
                  Required fields are marked with{" "}
                  <span className="text-[#E23D36]">*</span>.
                </div>
              </div>
            </CardHeader>
            <CardBody>
              <form onSubmit={handleSubmit} className="space-y-4">
                <div className="flex items-center rounded-aws-token border border-border overflow-hidden text-xs font-medium">
                  <button
                    type="button"
                    onClick={() => setSearchMode("regNumber")}
                    className={cn(
                      "flex-1 px-3 py-1.5 transition-colors",
                      searchMode === "regNumber"
                        ? "bg-accent text-white"
                        : "bg-surface text-text-muted hover:bg-surface-alt",
                    )}
                  >
                    By registration number
                  </button>
                  <button
                    type="button"
                    onClick={() => setSearchMode("name")}
                    className={cn(
                      "flex-1 px-3 py-1.5 transition-colors border-l border-border",
                      searchMode === "name"
                        ? "bg-accent text-white"
                        : "bg-surface text-text-muted hover:bg-surface-alt",
                    )}
                  >
                    By company name
                  </button>
                </div>

                {searchMode === "regNumber" ? (
                  <Input
                    label="Company registration number *"
                    placeholder="e.g. 2015/123456/07"
                    value={regNumber}
                    onChange={(e) => setRegNumber(e.target.value)}
                    hint="CIPC format: YYYY/NNNNNN/NN or CK format."
                    className="font-mono"
                  />
                ) : (
                  <Input
                    label="Company name *"
                    placeholder="e.g. Mzansi Tech Solutions"
                    value={nameQuery}
                    onChange={(e) => setNameQuery(e.target.value)}
                    hint="Company names aren't unique -- we'll show every match to choose from."
                  />
                )}

                <div className="flex items-center justify-between pt-2 border-t border-border">
                  <span className="text-[11px] text-text-muted">
                    R 10.00 per lookup -- result in ~5 seconds
                  </span>
                  <Button
                    variant="cta"
                    type="submit"
                    disabled={!isValid || status === "loading"}
                    icon={<Building2 size={13} />}
                  >
                    {status === "loading"
                      ? "Querying..."
                      : searchMode === "regNumber"
                        ? "Verify company"
                        : "Search"}
                  </Button>
                </div>
              </form>
            </CardBody>
          </Card>

          <ResultPanel
            status={status}
            result={result}
            matches={matches}
            query={submittedQuery}
            onSelectMatch={handleSelectMatch}
          />
        </div>
      )}

      {tab === "director-search" && <DirectorSearchTab />}

      {tab === "history" && (
        <Card>
          <CardHeader>
            <div>
              <div className="text-sm font-semibold text-text">
                Verification history
              </div>
              <div className="text-[11px] text-text-muted mt-0.5">
                {DEMO_HISTORY.length} past verifications
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
