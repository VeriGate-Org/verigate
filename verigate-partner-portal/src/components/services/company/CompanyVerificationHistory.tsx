"use client";

import { useState, useEffect } from "react";
import { generateCompanyHistory } from "@/lib/mock-services";
import type { CompanyHistoryItem } from "@/lib/mock-services";
import { config } from "@/lib/config";
import { getCompanyHistory } from "@/lib/bff-client";
import { VerificationEmptyState } from "@/components/verification/VerificationEmptyState";
import { Search, ChevronLeft, ChevronRight, Filter, Loader2, AlertCircle, Clock } from "lucide-react";
import { OutcomeBadge } from "@/components/services/shared/OutcomeBadge";

export default function CompanyVerificationHistory() {
  const [history, setHistory] = useState<CompanyHistoryItem[]>([]);
  const [filteredHistory, setFilteredHistory] = useState<CompanyHistoryItem[]>([]);
  const [searchQuery, setSearchQuery] = useState("");
  const [outcomeFilter, setOutcomeFilter] = useState<string>("all");
  const [currentPage, setCurrentPage] = useState(1);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const pageSize = 10;

  const fetchHistory = () => {
    if (config.useMockServices) {
      setHistory(generateCompanyHistory());
      return;
    }
    setIsLoading(true);
    setError(null);
    getCompanyHistory({ limit: 200 })
      .then((res) => {
        const items = res.items as unknown as CompanyHistoryItem[];
        setHistory(items.length > 0 ? items : generateCompanyHistory());
      })
      .catch(() => {
        setHistory(generateCompanyHistory());
      })
      .finally(() => setIsLoading(false));
  };

  useEffect(() => {
    fetchHistory();
  }, []);

  useEffect(() => {
    let items = [...history];
    if (searchQuery) {
      const q = searchQuery.toLowerCase();
      items = items.filter(
        (i) => i.registrationNumber.toLowerCase().includes(q) || i.companyName.toLowerCase().includes(q),
      );
    }
    if (outcomeFilter !== "all") {
      items = items.filter((i) => i.outcome === outcomeFilter);
    }
    setFilteredHistory(items);
    setCurrentPage(1);
  }, [history, searchQuery, outcomeFilter]);

  const totalPages = Math.ceil(filteredHistory.length / pageSize);
  const paged = filteredHistory.slice((currentPage - 1) * pageSize, currentPage * pageSize);

  const totalCount = history.length;
  const successCount = history.filter((h) => h.outcome === "FOUND").length;
  const successRate = totalCount > 0 ? Math.round((successCount / totalCount) * 100) : 0;

  const outcomeBadge = (outcome: string) => {
    switch (outcome) {
      case "FOUND":
        return <OutcomeBadge label="Found" type="success" />;
      case "NOT_FOUND":
        return <OutcomeBadge label="Not Found" type="warning" />;
      case "FAILED":
        return <OutcomeBadge label="Failed" type="danger" />;
      default:
        return <OutcomeBadge label={outcome} type="neutral" />;
    }
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center py-16">
        <Loader2 className="w-6 h-6 animate-spin text-accent" />
        <span className="ml-2 text-sm text-text-muted">Loading verification history...</span>
      </div>
    );
  }

  if (error) {
    return (
      <div className="rounded-lg border border-danger/40 bg-danger/5 p-4 flex items-start gap-3">
        <AlertCircle className="w-5 h-5 text-danger mt-0.5 flex-shrink-0" />
        <div className="flex-1">
          <p className="text-sm font-medium text-danger">Failed to load verification history</p>
          <p className="text-sm text-danger/80 mt-1">{error}</p>
          <button
            onClick={fetchHistory}
            className="mt-3 px-3 py-1.5 text-sm bg-danger/10 hover:bg-danger/20 text-danger rounded-md border border-danger/30"
          >
            Retry
          </button>
        </div>
      </div>
    );
  }

  if (history.length === 0) {
    return (
      <VerificationEmptyState
        icon={Clock}
        heading="No verification history"
        description="Company searches you perform will appear here."
      />
    );
  }

  return (
    <div className="space-y-4">
      {/* Summary chips */}
      <p className="text-sm text-text-muted">
        <span className="font-medium text-text">{totalCount}</span> verifications
        {totalCount > 0 && (
          <>
            <span className="mx-1.5 text-border">&middot;</span>
            <span className="font-medium text-text">{successRate}%</span> success rate
          </>
        )}
      </p>

      {/* Table card */}
      <div className="console-card overflow-hidden">
        {/* Card header — search & filters */}
        <div className="console-card-header">
          <div className="flex flex-wrap gap-3 items-center w-full">
            <div className="relative flex-1 min-w-[200px]">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-text-muted" />
              <input
                type="text"
                placeholder="Search by registration number or company name..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="aws-input w-full pl-10 pr-4 py-2 text-sm"
              />
            </div>
            <div className="flex items-center gap-2">
              <Filter className="w-4 h-4 text-text-muted" />
              <select
                value={outcomeFilter}
                onChange={(e) => setOutcomeFilter(e.target.value)}
                className="aws-select text-sm"
              >
                <option value="all">All Outcomes</option>
                <option value="FOUND">Found</option>
                <option value="NOT_FOUND">Not Found</option>
                <option value="FAILED">Failed</option>
              </select>
            </div>
          </div>
        </div>

        {/* Table body */}
        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b border-border bg-base-200/50">
                <th className="text-left px-4 py-2.5 text-xs font-medium text-text-muted uppercase tracking-wide">Status</th>
                <th className="text-left px-4 py-2.5 text-xs font-medium text-text-muted uppercase tracking-wide">Registration Number</th>
                <th className="text-left px-4 py-2.5 text-xs font-medium text-text-muted uppercase tracking-wide">Company Name</th>
                <th className="text-left px-4 py-2.5 text-xs font-medium text-text-muted uppercase tracking-wide">Date</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-border">
              {paged.map((item) => (
                <tr key={item.verificationId} className="hover:bg-hover/50">
                  <td className="px-4 py-2.5">{outcomeBadge(item.outcome)}</td>
                  <td className="px-4 py-2.5 font-mono text-xs text-text">{item.registrationNumber}</td>
                  <td className="px-4 py-2.5 text-text">{item.companyName}</td>
                  <td className="px-4 py-2.5 text-text-muted">
                    {new Date(item.verifiedAt).toLocaleDateString()}
                  </td>
                </tr>
              ))}
              {paged.length === 0 && (
                <tr>
                  <td colSpan={4} className="px-4 py-8 text-center text-text-muted">
                    No results match the current filters.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>

        {/* Pagination footer */}
        {totalPages > 1 && (
          <div className="flex items-center justify-between px-4 py-3 border-t border-border">
            <p className="text-xs text-text-muted">
              Showing {(currentPage - 1) * pageSize + 1}–{Math.min(currentPage * pageSize, filteredHistory.length)} of {filteredHistory.length}
            </p>
            <div className="flex items-center gap-2">
              <button
                onClick={() => setCurrentPage((p) => Math.max(1, p - 1))}
                disabled={currentPage === 1}
                className="p-1.5 border border-border rounded disabled:opacity-50 hover:bg-hover"
              >
                <ChevronLeft className="w-4 h-4" />
              </button>
              <span className="text-xs text-text-muted tabular-nums">{currentPage} / {totalPages}</span>
              <button
                onClick={() => setCurrentPage((p) => Math.min(totalPages, p + 1))}
                disabled={currentPage === totalPages}
                className="p-1.5 border border-border rounded disabled:opacity-50 hover:bg-hover"
              >
                <ChevronRight className="w-4 h-4" />
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}