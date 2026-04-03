"use client";

import { useState, useCallback } from "react";
import { Upload, Download, FileText, Play, Loader2 } from "lucide-react";

interface BatchJob {
  batchId: string;
  fileName: string;
  subjects: number;
  hits: number;
  status: "PENDING" | "PROCESSING" | "COMPLETED" | "FAILED";
  createdAt: string;
}

export default function SanctionsBatch() {
  const [dragOver, setDragOver] = useState(false);
  const [file, setFile] = useState<File | null>(null);
  const [dataset, setDataset] = useState("sanctions");
  const [algorithm, setAlgorithm] = useState("logic-v2");
  const [threshold, setThreshold] = useState(0.7);
  const [excludeDismissed, setExcludeDismissed] = useState(false);
  const [processing, setProcessing] = useState(false);
  const [recentBatches] = useState<BatchJob[]>([
    { batchId: "batch-001", fileName: "clients_q1.csv", subjects: 250, hits: 3, status: "COMPLETED", createdAt: "2025-03-15T10:00:00Z" },
    { batchId: "batch-002", fileName: "vendors_2025.csv", subjects: 89, hits: 1, status: "COMPLETED", createdAt: "2025-03-10T14:30:00Z" },
    { batchId: "batch-003", fileName: "onboarding_batch.csv", subjects: 500, hits: 0, status: "PROCESSING", createdAt: "2025-03-20T09:15:00Z" },
  ]);

  const handleDrop = useCallback((e: React.DragEvent) => {
    e.preventDefault();
    setDragOver(false);
    const droppedFile = e.dataTransfer.files[0];
    if (droppedFile && (droppedFile.name.endsWith(".csv") || droppedFile.name.endsWith(".xlsx"))) {
      setFile(droppedFile);
    }
  }, []);

  const handleFileSelect = (e: React.ChangeEvent<HTMLInputElement>) => {
    const selectedFile = e.target.files?.[0];
    if (selectedFile) {
      setFile(selectedFile);
    }
  };

  const handleStartScreening = async () => {
    if (!file) return;
    setProcessing(true);
    // Mock processing
    await new Promise((r) => setTimeout(r, 2000));
    setProcessing(false);
    setFile(null);
  };

  const statusBadge = (status: BatchJob["status"]) => {
    switch (status) {
      case "COMPLETED":
        return <span className="px-2 py-0.5 text-xs rounded-full bg-green-100 text-green-800">Completed</span>;
      case "PROCESSING":
        return <span className="px-2 py-0.5 text-xs rounded-full bg-blue-100 text-blue-800">Processing</span>;
      case "FAILED":
        return <span className="px-2 py-0.5 text-xs rounded-full bg-red-100 text-red-800">Failed</span>;
      default:
        return <span className="px-2 py-0.5 text-xs rounded-full bg-gray-100 text-gray-800">Pending</span>;
    }
  };

  return (
    <div className="space-y-8">
      {/* Upload area */}
      <div className="bg-white border border-gray-200 rounded-lg p-6 space-y-6">
        <h3 className="text-lg font-semibold">Bulk Screening Upload</h3>

        <div
          onDragOver={(e) => { e.preventDefault(); setDragOver(true); }}
          onDragLeave={() => setDragOver(false)}
          onDrop={handleDrop}
          className={`border-2 border-dashed rounded-lg p-8 text-center transition-colors ${
            dragOver ? "border-blue-500 bg-blue-50" : "border-gray-300 bg-gray-50"
          }`}
        >
          <Upload className="w-10 h-10 mx-auto mb-3 text-gray-400" />
          {file ? (
            <div>
              <p className="font-medium text-gray-800">{file.name}</p>
              <p className="text-sm text-gray-500 mt-1">{(file.size / 1024).toFixed(1)} KB</p>
              <button onClick={() => setFile(null)} className="text-sm text-red-600 mt-2 hover:underline">
                Remove
              </button>
            </div>
          ) : (
            <div>
              <p className="text-gray-600 mb-2">Drag and drop a CSV or XLSX file here</p>
              <label className="text-sm text-blue-600 hover:underline cursor-pointer">
                or browse files
                <input type="file" accept=".csv,.xlsx" className="hidden" onChange={handleFileSelect} />
              </label>
            </div>
          )}
        </div>

        {/* Template downloads */}
        <div className="flex gap-3">
          <button className="flex items-center gap-1.5 text-sm text-blue-600 hover:underline">
            <FileText className="w-4 h-4" /> Download CSV Template
          </button>
          <button className="flex items-center gap-1.5 text-sm text-blue-600 hover:underline">
            <FileText className="w-4 h-4" /> Download XLSX Template
          </button>
        </div>

        <div className="text-xs text-gray-500 bg-gray-50 rounded-lg p-3">
          <p className="font-medium mb-1">Required columns:</p>
          <p>name (required), dateOfBirth, nationality, entityType (Person/Company/Organization/Vessel)</p>
        </div>

        {/* Configuration */}
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Dataset</label>
            <select value={dataset} onChange={(e) => setDataset(e.target.value)} className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm">
              <option value="sanctions">Sanctions</option>
              <option value="default">Default (All)</option>
              <option value="peps">PEPs</option>
            </select>
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Algorithm</label>
            <select value={algorithm} onChange={(e) => setAlgorithm(e.target.value)} className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm">
              <option value="logic-v2">logic-v2 (Recommended)</option>
              <option value="logic-v1">logic-v1</option>
            </select>
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Threshold: {threshold}</label>
            <input type="range" min="0" max="1" step="0.05" value={threshold} onChange={(e) => setThreshold(Number(e.target.value))} className="w-full" />
          </div>
        </div>

        <label className="flex items-center gap-2 text-sm">
          <input type="checkbox" checked={excludeDismissed} onChange={(e) => setExcludeDismissed(e.target.checked)} className="rounded" />
          Exclude previously dismissed entities
        </label>

        <button
          onClick={handleStartScreening}
          disabled={!file || processing}
          className="flex items-center gap-2 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          {processing ? <Loader2 className="w-4 h-4 animate-spin" /> : <Play className="w-4 h-4" />}
          {processing ? "Processing..." : "Start Screening"}
        </button>
      </div>

      {/* Recent batches */}
      <div className="bg-white border border-gray-200 rounded-lg p-6">
        <h3 className="text-lg font-semibold mb-4">Recent Batches</h3>
        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead className="bg-gray-50 border-b border-gray-200">
              <tr>
                <th className="text-left px-4 py-3 font-medium text-gray-600">Batch ID</th>
                <th className="text-left px-4 py-3 font-medium text-gray-600">File</th>
                <th className="text-left px-4 py-3 font-medium text-gray-600">Subjects</th>
                <th className="text-left px-4 py-3 font-medium text-gray-600">Hits</th>
                <th className="text-left px-4 py-3 font-medium text-gray-600">Status</th>
                <th className="text-left px-4 py-3 font-medium text-gray-600">Date</th>
                <th className="text-left px-4 py-3 font-medium text-gray-600">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-200">
              {recentBatches.map((batch) => (
                <tr key={batch.batchId} className="hover:bg-gray-50">
                  <td className="px-4 py-3 font-mono text-xs">{batch.batchId}</td>
                  <td className="px-4 py-3">{batch.fileName}</td>
                  <td className="px-4 py-3 text-gray-600">{batch.subjects}</td>
                  <td className="px-4 py-3 text-gray-600">{batch.hits}</td>
                  <td className="px-4 py-3">{statusBadge(batch.status)}</td>
                  <td className="px-4 py-3 text-gray-600">{new Date(batch.createdAt).toLocaleDateString()}</td>
                  <td className="px-4 py-3">
                    {batch.status === "COMPLETED" && (
                      <button className="flex items-center gap-1 text-sm text-blue-600 hover:underline">
                        <Download className="w-3 h-3" /> Results
                      </button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
