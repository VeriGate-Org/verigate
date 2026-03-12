"use client";

import { useCallback, useRef, useState } from "react";
import type { DragEvent, ChangeEvent } from "react";
import { Upload, X, FileText } from "lucide-react";
import { Button } from "@/components/ui/Button";
import { cn } from "@/lib/cn";

export interface FileUploadProps {
  /** Accepted file types (e.g. "image/*,.pdf") */
  accept?: string;
  /** Maximum file size in bytes (default 10 MB) */
  maxSize?: number;
  /** Upload progress 0-100, controlled by parent */
  progress?: number;
  /** Error message to display */
  error?: string;
  /** Whether an upload is in progress */
  uploading?: boolean;
  /** Called when a valid file is selected */
  onFileSelect?: (file: File) => void;
  /** Called when the file is cleared */
  onClear?: () => void;
  /** Additional class names for the root element */
  className?: string;
}

const DEFAULT_MAX_SIZE = 10 * 1024 * 1024; // 10 MB

function formatBytes(bytes: number): string {
  if (bytes === 0) return "0 B";
  const units = ["B", "KB", "MB", "GB"];
  const i = Math.floor(Math.log(bytes) / Math.log(1024));
  return `${(bytes / Math.pow(1024, i)).toFixed(1)} ${units[i]}`;
}

function isImageFile(file: File): boolean {
  return file.type.startsWith("image/");
}

export default function FileUpload({
  accept,
  maxSize = DEFAULT_MAX_SIZE,
  progress,
  error: externalError,
  uploading = false,
  onFileSelect,
  onClear,
  className,
}: FileUploadProps) {
  const inputRef = useRef<HTMLInputElement>(null);
  const [dragOver, setDragOver] = useState(false);
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [previewUrl, setPreviewUrl] = useState<string | null>(null);
  const [validationError, setValidationError] = useState<string | null>(null);

  const displayError = externalError || validationError;

  const clearFile = useCallback(() => {
    setSelectedFile(null);
    setPreviewUrl(null);
    setValidationError(null);
    if (inputRef.current) {
      inputRef.current.value = "";
    }
    onClear?.();
  }, [onClear]);

  const handleFile = useCallback(
    (file: File) => {
      setValidationError(null);

      if (file.size > maxSize) {
        setValidationError(
          `File too large. Maximum size is ${formatBytes(maxSize)}.`
        );
        return;
      }

      setSelectedFile(file);

      if (isImageFile(file)) {
        const url = URL.createObjectURL(file);
        setPreviewUrl(url);
      } else {
        setPreviewUrl(null);
      }

      onFileSelect?.(file);
    },
    [maxSize, onFileSelect]
  );

  const handleDragOver = useCallback((e: DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    e.stopPropagation();
    setDragOver(true);
  }, []);

  const handleDragLeave = useCallback((e: DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    e.stopPropagation();
    setDragOver(false);
  }, []);

  const handleDrop = useCallback(
    (e: DragEvent<HTMLDivElement>) => {
      e.preventDefault();
      e.stopPropagation();
      setDragOver(false);

      const file = e.dataTransfer.files[0];
      if (file) {
        handleFile(file);
      }
    },
    [handleFile]
  );

  const handleInputChange = useCallback(
    (e: ChangeEvent<HTMLInputElement>) => {
      const file = e.target.files?.[0];
      if (file) {
        handleFile(file);
      }
    },
    [handleFile]
  );

  const openFilePicker = useCallback(() => {
    inputRef.current?.click();
  }, []);

  // --- Render selected file state ---
  if (selectedFile) {
    return (
      <div className={cn("space-y-2", className)}>
        <div className="flex items-center gap-3 rounded border border-border bg-background/50 px-3 py-2.5">
          {/* Preview / icon */}
          {previewUrl ? (
            <img
              src={previewUrl}
              alt="Preview"
              className="h-10 w-10 rounded object-cover"
            />
          ) : (
            <div className="flex h-10 w-10 items-center justify-center rounded bg-background text-text-muted">
              <FileText className="h-5 w-5" />
            </div>
          )}

          {/* File info */}
          <div className="min-w-0 flex-1">
            <p className="truncate text-sm font-medium text-text">
              {selectedFile.name}
            </p>
            <p className="text-xs text-text-muted">
              {formatBytes(selectedFile.size)}
            </p>
          </div>

          {/* Remove button */}
          {!uploading && (
            <button
              type="button"
              onClick={clearFile}
              className="rounded p-1 text-text-muted hover:bg-background hover:text-text"
              aria-label="Remove file"
            >
              <X className="h-4 w-4" />
            </button>
          )}
        </div>

        {/* Progress bar */}
        {uploading && typeof progress === "number" && (
          <div className="space-y-1">
            <div className="h-1.5 w-full overflow-hidden rounded-full bg-border">
              <div
                className="h-full rounded-full bg-accent transition-all duration-300"
                style={{ width: `${Math.min(progress, 100)}%` }}
              />
            </div>
            <p className="text-xs text-text-muted">{progress}% uploaded</p>
          </div>
        )}

        {/* Error */}
        {displayError && (
          <div className="rounded border border-danger/40 bg-danger/5 px-3 py-2 text-xs text-danger">
            {displayError}
          </div>
        )}
      </div>
    );
  }

  // --- Render drop zone ---
  return (
    <div className={cn("space-y-2", className)}>
      <div
        onDragOver={handleDragOver}
        onDragLeave={handleDragLeave}
        onDrop={handleDrop}
        className={cn(
          "flex cursor-pointer flex-col items-center justify-center gap-2 rounded border-2 border-dashed px-4 py-6 text-center transition-colors",
          dragOver
            ? "border-accent bg-accent/5"
            : "border-border hover:border-accent/50 hover:bg-background/50"
        )}
        onClick={openFilePicker}
        role="button"
        tabIndex={0}
        onKeyDown={(e) => {
          if (e.key === "Enter" || e.key === " ") {
            e.preventDefault();
            openFilePicker();
          }
        }}
      >
        <div className="flex h-10 w-10 items-center justify-center rounded-full bg-background text-text-muted">
          <Upload className="h-5 w-5" />
        </div>

        <div>
          <p className="text-sm font-medium text-text">
            Drop a file here, or click to browse
          </p>
          <p className="mt-0.5 text-xs text-text-muted">
            Max {formatBytes(maxSize)}
            {accept && ` \u00b7 ${accept}`}
          </p>
        </div>

        <Button type="button" variant="secondary" size="sm">
          Choose file
        </Button>
      </div>

      <input
        ref={inputRef}
        type="file"
        accept={accept}
        onChange={handleInputChange}
        className="hidden"
        aria-label="File upload"
      />

      {/* Error */}
      {displayError && (
        <div className="rounded border border-danger/40 bg-danger/5 px-3 py-2 text-xs text-danger">
          {displayError}
        </div>
      )}
    </div>
  );
}
