"use client";

import { useState } from "react";
import { cn } from "@/lib/cn";
import { ZoomIn, ZoomOut, FileText } from "lucide-react";

interface DocumentPreviewProps {
  url: string;
  type: "image" | "pdf";
  documentType: string;
}

const DOCUMENT_TYPE_LABELS: Record<string, string> = {
  id_card: "SA ID Card",
  passport: "Passport",
  drivers_license: "Driver's License",
};

export function DocumentPreview({ url, type, documentType }: DocumentPreviewProps) {
  const [zoomed, setZoomed] = useState(false);
  const label = DOCUMENT_TYPE_LABELS[documentType] ?? "Document";

  return (
    <div className="console-card">
      <div className="console-card-header">
        <div className="flex items-center gap-2">
          <FileText className="h-4 w-4 text-text-muted" />
          <span className="text-sm font-semibold text-text">
            Uploaded {label}
          </span>
        </div>
        {type === "image" && (
          <button
            type="button"
            onClick={() => setZoomed((z) => !z)}
            className="inline-flex items-center gap-1.5 rounded-md border border-border px-2.5 py-1.5 text-xs font-medium text-text-muted hover:bg-background-hover transition-colors"
          >
            {zoomed ? (
              <>
                <ZoomOut className="h-3.5 w-3.5" />
                Zoom out
              </>
            ) : (
              <>
                <ZoomIn className="h-3.5 w-3.5" />
                Zoom in
              </>
            )}
          </button>
        )}
      </div>
      <div className="console-card-body p-4">
        {type === "image" ? (
          <div
            className={cn(
              "overflow-auto rounded border border-border bg-background",
              zoomed ? "max-h-[600px]" : "max-h-[300px]"
            )}
          >
            {/* eslint-disable-next-line @next/next/no-img-element */}
            <img
              src={url}
              alt={`Uploaded ${label}`}
              className={cn(
                "block mx-auto transition-all",
                zoomed ? "w-full" : "max-h-[280px] object-contain"
              )}
            />
          </div>
        ) : (
          <div className="space-y-2">
            <embed
              src={url}
              type="application/pdf"
              className="w-full h-[400px] rounded border border-border"
            />
            <a
              href={url}
              target="_blank"
              rel="noopener noreferrer"
              className="inline-flex items-center gap-1 text-xs text-link hover:underline"
            >
              <FileText className="h-3 w-3" />
              Open in new tab
            </a>
          </div>
        )}
      </div>
    </div>
  );
}
