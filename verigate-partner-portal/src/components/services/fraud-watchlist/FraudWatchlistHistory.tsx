"use client";

import { useState, useEffect } from "react";
import { generateFraudWatchlistHistory } from "@/lib/mock-services";
import type { FraudWatchlistHistoryItem } from "@/lib/mock-services";
import { config } from "@/lib/config";
import { getFraudWatchlistHistory } from "@/lib/bff-client";
import { VerificationEmptyState } from "@/components/verification/VerificationEmptyState";
import { Search, ChevronLeft, ChevronRight, Filter, Loader2, AlertCircle, Clock } from "lucide-react";
import { OutcomeBadge } from "@/components/services/shared/OutcomeBadge";

export default function FraudWatchlistHistory() {
  const [history, setHistory] = useState<FraudWatchlistHistoryItem[]>([]);
  const [filteredHistory, setFilteredHistory] = useState<FraudWatchlistHistoryItem[]>([]);
  const [searchQuery, setSearchQuery] = useState("");
  const [outcomeFilter, setOutcomeFilter] = useState<string>("all");
  const [currentPage, setCurrentPage] = useState(1);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const pageSize = 10;

  const fetchHistory = () => {
    // BFF history endpoint not yet implemented — always use mock data
    setHistory(generateFraudWatchlistHistory());
  };

  useEffect(() => {
    fetchHistory();
  }, []);

  useEffect(() => {
    let items = [...history];
    if (searchQuery) {
      const q = searchQuery.toLowerCase();
      items = items.filter(
        (i) => i.idNumber.toLowerCase().includes(q) || i.fullName.toLowerCase().includes(q),
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
  const clearCount = history.filter((h) => h.outcome === "CLEAR").length;
  const listedCount = history.filter((h) => h.outcome === "LISTED").length;
  const failedCount = history.filter((h) => h.outcome === "FAILED").length;

  const outcomeBadge = (outcome: string) => {
    switch (outcome) {
      case "CLEAR":
        return <OutcomeBadge label="Clear" type="success" />;
      case "LISTED":
        return <OutcomeBadge label="Listed" type="danger" />;
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
        <span className="ml-2 text-sm text-text-muted">Loading history...</span>
      </div>
    );
  }

  if (error) {
    return (
      <div className="rounded-lg border border-danger/40 bg-danger/5 p-4 flex items-start gap-3">
        <AlertCircle className="w-5 h-5 text-danger mt-0.5 flex-shrink-0" />
        <div className="flex-1">
          <p className="text-sm font-medium text-danger">Failed to load history</p>
          <p className="text-sm text-danger/80 mt-1">{error}</p>
          <button onClick={fetchHistory} className="mt-3 px-3 py-1.5 text-sm bg-danger/10 hover:bg-danger/20 text-danger rounded-md border border-danger/30">Retry</button>
        </div>
      </div>
    );
  }

  if (history.length === 0) {
    return (
      <VerificationEmptyState icon={Clock} heading="No history" description="Fraud watchlist screenings you perform will appear here." />
    );
  }

  return (
    <div className="space-y-4">
      <div className="flex gap-3 flex-wrap">
        <SummaryChip label="Total" count={totalCount} className="bg-accent/10 text-accent" />
        <SummaryChip label="Clear" count={clearCount} className="bg-success/10 text-success" />
        <SummaryChip label="Listed" count={listedCount} className="bg-danger/10 text-danger" />
        <SummaryChip label="Failed" count={failedCount} className="bg-danger/10 text-danger" />
      </div>
      <div className="console-card overflow-hidden">
        <div className="console-card-header">
          <div className="flex flex-wrap gap-3 items-center w-full">
            <div className="relative flex-1 min-w-[200px]">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-text-muted" />
              <input type="text" placeholder="Search by ID number or name..." value={searchQuery} onChange={(e) => setSearchQuery(e.target.value)} className="aws-input w-full pl-10 pr-4 py-2 text-sm" />
            </div>
            <div className="flex items-center gap-2">
              <Filter className="w-4 h-4 text-text-muted" />
              <select value={outcomeFilter} onChange={(e) => setOutcomeFilter(e.target.value)} className="aws-select text-sm">
                <option value="all">All Outcomes</option>
                <option value="CLEAR">Clear</option>
                <option value="LISTED">Listed</option>
                <option value="FAILED">Failed</option>
              </select>
            </div>
          </div>
        </div>
        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b border-border bg-base-200/50">
                <th className="text-left px-4 py-2.5 text-xs font-medium text-text-muted uppercase tracking-wide">ID Number</th>
                <th className="text-left px-4 py-2.5 text-xs font-medium text-text-muted uppercase tracking-wide">Name</th>
                <th className="text-left px-4 py-2.5 text-xs font-medium text-text-muted uppercase tracking-wide">Status</th>
                <th className="text-left px-4 py-2.5 text-xs font-medium text-text-muted uppercase tracking-wide">Date</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-border">
              {paged.map((item) => (
                <tr key={item.verificationId} className="hover:bg-hover/50">
                  <td className="px-4 py-2.5 font-mono text-xs text-text">{item.idNumber}</td>
                  <td className="px-4 py-2.5 text-text">{item.fullName}</td>
                  <td className="px-4 py-2.5">{outcomeBadge(item.outcome)}</td>
                  <td className="px-4 py-2.5 text-text-muted">{new Date(item.screenedAt).toLocaleDateString()}</td>
                </tr>
              ))}
              {paged.length === 0 && (
                <tr><td colSpan={4} className="px-4 py-8 text-center text-text-muted">No results match the current filters.</td></tr>
              )}
            </tbody>
          </table>
        </div>
        {totalPages > 1 && (
          <div className="flex items-center justify-between px-4 py-3 border-t border-border">
            <p className="text-xs text-text-muted">Showing {(currentPage - 1) * pageSize + 1}–{Math.min(currentPage * pageSize, filteredHistory.length)} of {filteredHistory.length}</p>
            <div className="flex items-center gap-2">
              <button onClick={() => setCurrentPage((p) => Math.max(1, p - 1))} disabled={currentPage === 1} className="p-1.5 border border-border rounded disabled:opacity-50 hover:bg-hover"><ChevronLeft className="w-4 h-4" /></button>
              <span className="text-xs text-text-muted tabular-nums">{currentPage} / {totalPages}</span>
              <button onClick={() => setCurrentPage((p) => Math.min(totalPages, p + 1))} disabled={currentPage === totalPages} className="p-1.5 border border-border rounded disabled:opacity-50 hover:bg-hover"><ChevronRight className="w-4 h-4" /></button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

function SummaryChip({ label, count, className }: { label: string; count: number; className: string }) {
  return (
    <div className={`px-3 py-1.5 rounded-lg text-sm font-medium ${className}`}>
      {label}: {count}
    </div>
  );
}
