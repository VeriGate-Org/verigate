"use client";

import { useState, useEffect } from "react";
import { generateScreeningHistory } from "@/lib/mock-services";
import type { ScreeningHistoryItem } from "@/lib/types/sanctions-screening";
import { Search, Download, ChevronLeft, ChevronRight, Filter } from "lucide-react";

interface SanctionsHistoryProps {
  onViewScreening?: (screeningId: string) => void;
}

export default function SanctionsHistory({ onViewScreening }: SanctionsHistoryProps) {
  const [history, setHistory] = useState<ScreeningHistoryItem[]>([]);
  const [filteredHistory, setFilteredHistory] = useState<ScreeningHistoryItem[]>([]);
  const [searchQuery, setSearchQuery] = useState("");
  const [outcomeFilter, setOutcomeFilter] = useState<string>("all");
  const [entityTypeFilter, setEntityTypeFilter] = useState<string>("all");
  const [currentPage, setCurrentPage] = useState(1);
  const pageSize = 10;

  useEffect(() => {
    setHistory(generateScreeningHistory());
  }, []);

  useEffect(() => {
    let items = [...history];
    if (searchQuery) {
      const q = searchQuery.toLowerCase();
      items = items.filter((i) => i.subjectName.toLowerCase().includes(q));
    }
    if (outcomeFilter !== "all") {
      items = items.filter((i) => i.outcome === outcomeFilter);
    }
    if (entityTypeFilter !== "all") {
      items = items.filter((i) => i.entityType === entityTypeFilter);
    }
    setFilteredHistory(items);
    setCurrentPage(1);
  }, [history, searchQuery, outcomeFilter, entityTypeFilter]);

  const totalPages = Math.ceil(filteredHistory.length / pageSize);
  const paged = filteredHistory.slice((currentPage - 1) * pageSize, currentPage * pageSize);

  const pendingCount = history.filter((h) => h.disposition === "PENDING_REVIEW").length;
  const clearedCount = history.filter((h) => h.outcome === "SUCCEEDED").length;
  const blockedCount = history.filter((h) => h.outcome === "HARD_FAIL").length;
  const reviewCount = history.filter((h) => h.outcome === "SOFT_FAIL").length;

  const outcomeBadge = (outcome: string) => {
    switch (outcome) {
      case "SUCCEEDED":
        return <span className="px-2 py-0.5 text-xs rounded-full bg-green-100 text-green-800">Clear</span>;
      case "HARD_FAIL":
        return <span className="px-2 py-0.5 text-xs rounded-full bg-red-100 text-red-800">Blocked</span>;
      case "SOFT_FAIL":
        return <span className="px-2 py-0.5 text-xs rounded-full bg-amber-100 text-amber-800">Review</span>;
      default:
        return <span className="px-2 py-0.5 text-xs rounded-full bg-gray-100 text-gray-800">{outcome}</span>;
    }
  };

  return (
    <div className="space-y-6">
      {/* Summary chips */}
      <div className="flex gap-3 flex-wrap">
        <div className="px-4 py-2 bg-amber-50 border border-amber-200 rounded-lg text-sm">
          <span className="font-medium text-amber-800">Pending:</span> {pendingCount}
        </div>
        <div className="px-4 py-2 bg-blue-50 border border-blue-200 rounded-lg text-sm">
          <span className="font-medium text-blue-800">Review:</span> {reviewCount}
        </div>
        <div className="px-4 py-2 bg-green-50 border border-green-200 rounded-lg text-sm">
          <span className="font-medium text-green-800">Cleared:</span> {clearedCount}
        </div>
        <div className="px-4 py-2 bg-red-50 border border-red-200 rounded-lg text-sm">
          <span className="font-medium text-red-800">Blocked:</span> {blockedCount}
        </div>
      </div>

      {/* Filters */}
      <div className="flex flex-wrap gap-3 items-center">
        <div className="relative flex-1 min-w-[200px]">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
          <input
            type="text"
            placeholder="Search by name..."
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
            <option value="SUCCEEDED">Clear</option>
            <option value="SOFT_FAIL">Review</option>
            <option value="HARD_FAIL">Blocked</option>
          </select>
          <select
            value={entityTypeFilter}
            onChange={(e) => setEntityTypeFilter(e.target.value)}
            className="border border-gray-300 rounded-lg px-3 py-2 text-sm"
          >
            <option value="all">All Types</option>
            <option value="Person">Person</option>
            <option value="Company">Company</option>
            <option value="Organization">Organization</option>
            <option value="Vessel">Vessel</option>
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
              <th className="text-left px-4 py-3 font-medium text-gray-600">Subject</th>
              <th className="text-left px-4 py-3 font-medium text-gray-600">Type</th>
              <th className="text-left px-4 py-3 font-medium text-gray-600">Outcome</th>
              <th className="text-left px-4 py-3 font-medium text-gray-600">Matches</th>
              <th className="text-left px-4 py-3 font-medium text-gray-600">Date</th>
              <th className="text-left px-4 py-3 font-medium text-gray-600">Disposition</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-200">
            {paged.map((item) => (
              <tr
                key={item.screeningId}
                className="hover:bg-gray-50 cursor-pointer"
                onClick={() => onViewScreening?.(item.screeningId)}
              >
                <td className="px-4 py-3 font-mono text-xs text-gray-500">{item.screeningId.slice(0, 12)}...</td>
                <td className="px-4 py-3 font-medium">{item.subjectName}</td>
                <td className="px-4 py-3 text-gray-600">{item.entityType}</td>
                <td className="px-4 py-3">{outcomeBadge(item.outcome)}</td>
                <td className="px-4 py-3 text-gray-600">{item.matchCount}</td>
                <td className="px-4 py-3 text-gray-600">{new Date(item.screenedAt).toLocaleDateString()}</td>
                <td className="px-4 py-3">
                  {item.disposition ? (
                    <span className="px-2 py-0.5 text-xs rounded-full bg-gray-100 text-gray-700">
                      {item.disposition.replace("_", " ")}
                    </span>
                  ) : (
                    <span className="text-gray-400 text-xs">-</span>
                  )}
                </td>
              </tr>
            ))}
            {paged.length === 0 && (
              <tr>
                <td colSpan={7} className="px-4 py-8 text-center text-gray-500">
                  No screening history found
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
