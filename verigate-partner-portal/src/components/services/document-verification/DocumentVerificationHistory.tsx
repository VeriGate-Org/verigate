"use client";

import { useState, useEffect } from "react";
import { generateDocumentVerificationHistory } from "@/lib/mock-services";
import type { DocumentVerificationHistoryItem } from "@/lib/mock-services";
import { config } from "@/lib/config";
import { getDocumentVerificationHistory } from "@/lib/bff-client";
import { DOCUMENT_TYPE_LABELS } from "@/components/services/document-verification/documentFieldConfigs";
import { Search, Download, ChevronLeft, ChevronRight, Filter, Loader2, AlertCircle } from "lucide-react";

export default function DocumentVerificationHistory() {
  const [history, setHistory] = useState<DocumentVerificationHistoryItem[]>([]);
  const [filteredHistory, setFilteredHistory] = useState<DocumentVerificationHistoryItem[]>([]);
  const [searchQuery, setSearchQuery] = useState("");
  const [outcomeFilter, setOutcomeFilter] = useState<string>("all");
  const [docTypeFilter, setDocTypeFilter] = useState<string>("all");
  const [currentPage, setCurrentPage] = useState(1);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const pageSize = 10;

  const fetchHistory = () => {
    if (config.useMockServices) {
      setHistory(generateDocumentVerificationHistory());
      return;
    }
    setIsLoading(true);
    setError(null);
    getDocumentVerificationHistory({ limit: 200 })
      .then((res) => setHistory(res.items as unknown as DocumentVerificationHistoryItem[]))
      .catch((err) => setError(err.message ?? "Failed to load document history"))
      .finally(() => setIsLoading(false));
  };

  useEffect(() => {
    fetchHistory();
  }, []);

  useEffect(() => {
    let items = [...history];
    if (searchQuery) {
      const q = searchQuery.toLowerCase();
      items = items.filter((i) => i.documentNumber.toLowerCase().includes(q));
    }
    if (outcomeFilter !== "all") {
      items = items.filter((i) => i.outcome === outcomeFilter);
    }
    if (docTypeFilter !== "all") {
      items = items.filter((i) => i.documentType === docTypeFilter);
    }
    setFilteredHistory(items);
    setCurrentPage(1);
  }, [history, searchQuery, outcomeFilter, docTypeFilter]);

  const totalPages = Math.ceil(filteredHistory.length / pageSize);
  const paged = filteredHistory.slice((currentPage - 1) * pageSize, currentPage * pageSize);

  const totalCount = history.length;
  const verifiedCount = history.filter((h) => h.outcome === "VERIFIED").length;
  const notVerifiedCount = history.filter((h) => h.outcome === "NOT_VERIFIED").length;
  const failedCount = history.filter((h) => h.outcome === "FAILED").length;

  const uniqueDocTypes = Array.from(new Set(history.map((h) => h.documentType)));

  const outcomeBadge = (outcome: string) => {
    switch (outcome) {
      case "VERIFIED":
        return <span className="px-2 py-0.5 text-xs rounded-full bg-green-100 text-green-800">Verified</span>;
      case "NOT_VERIFIED":
        return <span className="px-2 py-0.5 text-xs rounded-full bg-amber-100 text-amber-800">Not Verified</span>;
      case "FAILED":
        return <span className="px-2 py-0.5 text-xs rounded-full bg-red-100 text-red-800">Failed</span>;
      default:
        return <span className="px-2 py-0.5 text-xs rounded-full bg-gray-100 text-gray-800">{outcome}</span>;
    }
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center py-16">
        <Loader2 className="w-6 h-6 animate-spin text-blue-600" />
        <span className="ml-2 text-sm text-gray-600">Loading verification history...</span>
      </div>
    );
  }

  if (error) {
    return (
      <div className="rounded-lg border border-red-200 bg-red-50 p-4 flex items-start gap-3">
        <AlertCircle className="w-5 h-5 text-red-600 mt-0.5 flex-shrink-0" />
        <div className="flex-1">
          <p className="text-sm font-medium text-red-800">Failed to load verification history</p>
          <p className="text-sm text-red-700 mt-1">{error}</p>
          <button
            onClick={fetchHistory}
            className="mt-3 px-3 py-1.5 text-sm bg-red-100 hover:bg-red-200 text-red-800 rounded-md border border-red-300"
          >
            Retry
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Summary chips */}
      <div className="flex gap-3 flex-wrap">
        <div className="px-4 py-2 bg-blue-50 border border-blue-200 rounded-lg text-sm">
          <span className="font-medium text-blue-800">Total:</span> {totalCount}
        </div>
        <div className="px-4 py-2 bg-green-50 border border-green-200 rounded-lg text-sm">
          <span className="font-medium text-green-800">Verified:</span> {verifiedCount}
        </div>
        <div className="px-4 py-2 bg-amber-50 border border-amber-200 rounded-lg text-sm">
          <span className="font-medium text-amber-800">Not Verified:</span> {notVerifiedCount}
        </div>
        <div className="px-4 py-2 bg-red-50 border border-red-200 rounded-lg text-sm">
          <span className="font-medium text-red-800">Failed:</span> {failedCount}
        </div>
      </div>

      {/* Filters */}
      <div className="flex flex-wrap gap-3 items-center">
        <div className="relative flex-1 min-w-[200px]">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
          <input
            type="text"
            placeholder="Search by document number..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
          />
        </div>
        <div className="flex items-center gap-2">
          <Filter className="w-4 h-4 text-gray-400" />
          <select
            value={outcomeFilter}
            onChange={(e) => setOutcomeFilter(e.target.value)}
            className="border border-gray-300 rounded-lg px-3 py-2 text-sm"
          >
            <option value="all">All Outcomes</option>
            <option value="VERIFIED">Verified</option>
            <option value="NOT_VERIFIED">Not Verified</option>
            <option value="FAILED">Failed</option>
          </select>
          <select
            value={docTypeFilter}
            onChange={(e) => setDocTypeFilter(e.target.value)}
            className="border border-gray-300 rounded-lg px-3 py-2 text-sm"
          >
            <option value="all">All Document Types</option>
            {uniqueDocTypes.map((dt) => (
              <option key={dt} value={dt}>
                {DOCUMENT_TYPE_LABELS[dt] ?? dt}
              </option>
            ))}
          </select>
        </div>
        <button className="flex items-center gap-1.5 px-3 py-2 text-sm border border-gray-300 rounded-lg hover:bg-gray-50">
          <Download className="w-4 h-4" /> Export
        </button>
      </div>

      {/* Table */}
      <div className="overflow-x-auto border border-gray-200 rounded-lg">
        <table className="w-full text-sm">
          <thead className="bg-gray-50 border-b border-gray-200">
            <tr>
              <th className="text-left px-4 py-3 font-medium text-gray-600">Reference</th>
              <th className="text-left px-4 py-3 font-medium text-gray-600">Document Type</th>
              <th className="text-left px-4 py-3 font-medium text-gray-600">Document Number</th>
              <th className="text-left px-4 py-3 font-medium text-gray-600">Outcome</th>
              <th className="text-left px-4 py-3 font-medium text-gray-600">Confidence</th>
              <th className="text-left px-4 py-3 font-medium text-gray-600">Date</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-200">
            {paged.map((item) => (
              <tr key={item.verificationId} className="hover:bg-gray-50">
                <td className="px-4 py-3 font-mono text-xs text-gray-500">
                  {item.verificationId.slice(0, 16)}...
                </td>
                <td className="px-4 py-3 text-gray-600">{item.documentTypeLabel}</td>
                <td className="px-4 py-3 font-mono text-xs">{item.documentNumber}</td>
                <td className="px-4 py-3">{outcomeBadge(item.outcome)}</td>
                <td className="px-4 py-3 text-gray-600 tabular-nums">
                  {Math.round(item.overallConfidence * 100)}%
                </td>
                <td className="px-4 py-3 text-gray-600">
                  {new Date(item.verifiedAt).toLocaleDateString()}
                </td>
              </tr>
            ))}
            {paged.length === 0 && (
              <tr>
                <td colSpan={6} className="px-4 py-8 text-center text-gray-500">
                  No verification history found
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>

      {/* Pagination */}
      {totalPages > 1 && (
        <div className="flex items-center justify-between">
          <p className="text-sm text-gray-600">
            Showing {(currentPage - 1) * pageSize + 1}-{Math.min(currentPage * pageSize, filteredHistory.length)} of {filteredHistory.length}
          </p>
          <div className="flex items-center gap-2">
            <button
              onClick={() => setCurrentPage((p) => Math.max(1, p - 1))}
              disabled={currentPage === 1}
              className="p-2 border border-gray-300 rounded-lg disabled:opacity-50 hover:bg-gray-50"
            >
              <ChevronLeft className="w-4 h-4" />
            </button>
            <span className="text-sm text-gray-600">{currentPage} / {totalPages}</span>
            <button
              onClick={() => setCurrentPage((p) => Math.min(totalPages, p + 1))}
              disabled={currentPage === totalPages}
              className="p-2 border border-gray-300 rounded-lg disabled:opacity-50 hover:bg-gray-50"
            >
              <ChevronRight className="w-4 h-4" />
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
