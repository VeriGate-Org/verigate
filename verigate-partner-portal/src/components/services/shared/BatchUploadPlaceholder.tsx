"use client";
import { Upload } from "lucide-react";

export default function BatchUploadPlaceholder({ serviceName }: { serviceName: string }) {
  return (
    <div className="console-card">
      <div className="console-card-body flex flex-col items-center justify-center py-16 text-center">
        <Upload className="h-10 w-10 text-text-muted/40 mb-3" />
        <div className="text-sm font-medium text-text-muted mb-1">Batch upload coming soon</div>
        <div className="text-xs text-text-muted max-w-xs">
          Bulk {serviceName.toLowerCase()} via CSV upload is under development.
        </div>
      </div>
    </div>
  );
}
