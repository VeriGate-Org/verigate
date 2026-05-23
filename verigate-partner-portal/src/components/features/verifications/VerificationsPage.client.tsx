"use client";

import { useState, useMemo, useCallback } from "react";
import { useRouter } from "next/navigation";
import { Download, Plus, Search } from "lucide-react";
import { useVerificationList } from "@/lib/hooks/useVerification";
import { exportVerifications } from "@/lib/bff-client";
import { getTypeInfo } from "@/lib/verification-type-map";
import type { Verification } from "@/lib/types";
import { PageHeader } from "@/components/ui/PageHeader";
import { Button } from "@/components/ui/Button";
import { Badge } from "@/components/ui/Badge";
import { FilterChips } from "@/components/ui/FilterChips";
import { DataTable } from "@/components/ui/DataTable";
import { Skeleton } from "@/components/ui/Skeleton";
import { ErrorState } from "@/components/ui/ErrorState";
import { type ColumnDef } from "@tanstack/react-table";
import { NewVerificationWizard } from "./wizard/NewVerificationWizard.client";

const STATUS_MAP: Record<string, { label: string; variant: "success" | "danger" | "warning" | "info" | "pending" }> = {
  success: { label: "Verified", variant: "success" },
  completed: { label: "Verified", variant: "success" },
  hard_fail: { label: "Failed", variant: "danger" },
  permanent_failure: { label: "Failed", variant: "danger" },
  soft_fail: { label: "Review", variant: "warning" },
  transient_error: { label: "Review", variant: "warning" },
  in_progress: { label: "In Progress", variant: "info" },
  pending: { label: "Pending", variant: "pending" },
};

function statusDisplay(status: string) {
  return STATUS_MAP[status] || { label: status, variant: "neutral" as const };
}

const columns: ColumnDef<Verification, unknown>[] = [
  {
    accessorKey: "correlationId",
    header: "Correlation ID",
    cell: ({ getValue }) => (
      <span className="font-mono text-xs text-accent">{getValue<string>()}</span>
    ),
  },
  {
    accessorKey: "type",
    header: "Type",
    cell: ({ getValue }) => {
      const type = getValue<string>();
      const info = getTypeInfo(type as Verification["type"]);
      return (
        <span className="text-[13px] font-medium">
          {info?.shortLabel || type}
        </span>
      );
    },
  },
  {
    accessorKey: "status",
    header: "Status",
    cell: ({ getValue }) => {
      const s = statusDisplay(getValue<string>());
      return <Badge variant={s.variant}>{s.label}</Badge>;
    },
  },
  {
    accessorKey: "provider",
    header: "Provider",
    cell: ({ getValue }) => (
      <span className="text-[13px] text-text-muted">{getValue<string>()}</span>
    ),
  },
  {
    accessorKey: "startedAt",
    header: "Started",
    cell: ({ getValue }) => {
      const d = getValue<string>();
      if (!d) return <span className="text-text-muted">—</span>;
      return (
        <span className="text-xs text-text-muted">
          {new Date(d).toLocaleString("en-ZA", {
            day: "2-digit",
            month: "short",
            hour: "2-digit",
            minute: "2-digit",
          })}
        </span>
      );
    },
  },
];

const FILTER_CHIPS = [
  { label: "All", value: "all" },
  { label: "In Progress", value: "in_progress" },
  { label: "Verified", value: "success" },
  { label: "Review", value: "soft_fail" },
  { label: "Failed", value: "hard_fail" },
];

export function VerificationsPage() {
  const router = useRouter();
  const [statusFilter, setStatusFilter] = useState("all");
  const [query, setQuery] = useState("");
  const [wizardOpen, setWizardOpen] = useState(false);
  const [exporting, setExporting] = useState(false);

  const params = useMemo(
    () => ({
      q: query || undefined,
      status: statusFilter === "all" ? undefined : statusFilter,
      pageSize: 20,
    }),
    [query, statusFilter],
  );

  const { data, isLoading, isError, error, refetch } = useVerificationList(params);

  const handleExportCsv = useCallback(async () => {
    const ids = data?.items?.map((v) => v.correlationId) ?? [];
    if (ids.length === 0) return;

    setExporting(true);
    try {
      await exportVerifications(ids, "csv");
    } catch {
      // Silently fail — BFF may not support export yet
    } finally {
      setExporting(false);
    }
  }, [data?.items]);

  return (
    <div className="space-y-aws-m">
      <PageHeader
        category="Verifications"
        title="All verifications"
        description="Filter, search, and drill into any verification record."
        actions={
          <>
            <Button
              variant="secondary"
              icon={<Download size={13} />}
              onClick={handleExportCsv}
              disabled={exporting || !data?.items?.length}
            >
              {exporting ? "Exporting\u2026" : "Export CSV"}
            </Button>
            <Button
              variant="cta"
              icon={<Plus size={13} />}
              onClick={() => setWizardOpen(true)}
            >
              New verification
            </Button>
          </>
        }
      />

      <div className="flex justify-between items-center gap-4">
        <FilterChips
          chips={FILTER_CHIPS}
          value={statusFilter}
          onChange={setStatusFilter}
        />
        <div className="relative w-72">
          <span className="absolute left-2.5 top-1/2 -translate-y-1/2 inline-flex">
            <Search size={13} className="text-text-muted" />
          </span>
          <input
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            placeholder="Search subject or ID\u2026"
            className="aws-input w-full pl-8 pr-3 py-1.5 text-xs rounded"
          />
        </div>
      </div>

      {isError ? (
        <ErrorState
          title="Failed to load verifications"
          body={
            error instanceof Error
              ? error.message
              : "We couldn\u2019t load the verification list. Please try again."
          }
          onRetry={() => refetch()}
        />
      ) : isLoading ? (
        <div className="space-y-2">
          <Skeleton className="h-10 w-full" />
          <Skeleton className="h-10 w-full" />
          <Skeleton className="h-10 w-full" />
        </div>
      ) : (
        <DataTable
          data={data?.items || []}
          columns={columns}
          onRowClick={(row) =>
            router.push(`/verifications/${row.correlationId}`)
          }
          pageSize={10}
          emptyMessage="No verifications match these filters."
        />
      )}

      <NewVerificationWizard
        open={wizardOpen}
        onClose={() => setWizardOpen(false)}
      />
    </div>
  );
}
