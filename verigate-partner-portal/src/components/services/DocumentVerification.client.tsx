"use client";

import { useCallback, useRef, useState } from "react";
import type { FormEvent } from "react";
import { ProcessingDialog } from "@/components/ui/ProcessingDialog";
import { Button } from "@/components/ui/Button";
import { Skeleton } from "@/components/ui/Loading/Skeleton";
import FileUpload from "@/components/ui/FileUpload/FileUpload";
import { VerificationResultCard } from "@/components/verification/VerificationResultCard";
import { VerificationEmptyState } from "@/components/verification/VerificationEmptyState";
import { AnimatedResult } from "@/components/verification/AnimatedResult";
import { RetryButton } from "@/components/verification/RetryButton";
import { ScreenReaderAnnounce } from "@/components/ui/ScreenReaderAnnounce";
import { ServiceField } from "@/components/services/shared/ServiceField";
import { useToast } from "@/components/ui/Toast";
import { type DocumentVerificationResponse } from "@/lib/mock-services";
import { executeVerification } from "@/lib/services/verification-service";
import { exportPdf } from "@/lib/utils/export-pdf";
import {
  getDocumentPresignedUrl,
  uploadFileToS3,
} from "@/lib/bff-client";
import { FileCheck } from "lucide-react";

const DOCUMENT_TYPES = [
  { value: "passport", label: "Passport" },
  { value: "drivers_license", label: "Driver's License" },
  { value: "id_card", label: "ID Card" },
  { value: "work_permit", label: "Work Permit" },
];

export default function DocumentVerification() {
  const [documentType, setDocumentType] = useState<string>(
    DOCUMENT_TYPES[0]?.value ?? ""
  );
  const [documentNumber, setDocumentNumber] = useState("");
  const [result, setResult] = useState<DocumentVerificationResponse | null>(
    null
  );
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const resultRef = useRef<HTMLDivElement>(null);
  const resultCardRef = useRef<HTMLDivElement>(null);
  const { toast } = useToast();

  const [, setSelectedFile] = useState<File | null>(null);
  const [uploadProgress, setUploadProgress] = useState<number>(0);
  const [s3ObjectKey, setS3ObjectKey] = useState<string>("");
  const [s3BucketName, setS3BucketName] = useState<string>("");
  const [isUploading, setIsUploading] = useState(false);
  const [uploadError, setUploadError] = useState<string | null>(null);

  const handleExport = useCallback(async () => {
    if (resultCardRef.current) {
      await exportPdf(resultCardRef.current, "verigate-document-verification.pdf");
    }
  }, []);

  const handleFileSelect = useCallback(
    async (file: File) => {
      setSelectedFile(file);
      setUploadError(null);
      setUploadProgress(0);
      setS3ObjectKey("");
      setS3BucketName("");
      setIsUploading(true);

      try {
        const presigned = await getDocumentPresignedUrl({
          fileName: file.name,
          contentType: file.type,
          documentType,
        });

        await uploadFileToS3(presigned.uploadUrl, file, (percent) => {
          setUploadProgress(percent);
        });

        setS3ObjectKey(presigned.s3ObjectKey);
        setS3BucketName(presigned.s3BucketName);
      } catch (err) {
        const message =
          err instanceof Error ? err.message : "File upload failed";
        setUploadError(message);
      } finally {
        setIsUploading(false);
      }
    },
    [documentType]
  );

  const handleFileClear = useCallback(() => {
    setSelectedFile(null);
    setUploadProgress(0);
    setS3ObjectKey("");
    setS3BucketName("");
    setIsUploading(false);
    setUploadError(null);
  }, []);

  const doVerification = useCallback(async () => {
    setLoading(true);
    setError(null);

    try {
      const metadata: Record<string, unknown> = {
        documentType,
        documentNumber,
      };

      if (s3ObjectKey && s3BucketName) {
        metadata.s3BucketName = s3BucketName;
        metadata.s3ObjectKey = s3ObjectKey;
      }

      const data = (await executeVerification(
        "DOCUMENT_VERIFICATION",
        metadata
      )) as DocumentVerificationResponse;
      setResult(data);
      toast({ title: "Verification complete", variant: "success" });
      setTimeout(() => resultRef.current?.focus(), 100);
    } catch (err) {
      const message =
        err instanceof Error ? err.message : "Verification failed";
      setError(message);
      setResult(null);
      toast({ title: "Verification failed", description: message, variant: "error" });
    } finally {
      setLoading(false);
    }
  }, [documentType, documentNumber, s3ObjectKey, s3BucketName, toast]);

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    await doVerification();
  };

  const submitDisabled =
    loading || documentNumber.trim().length < 1 || isUploading;

  const srMessage = loading
    ? "Loading verification results"
    : error
      ? "Verification failed"
      : result
        ? "Verification complete"
        : "";

  return (
    <div className="space-y-6">
      <ScreenReaderAnnounce message={srMessage} />
      <header className="space-y-1">
        <h1 className="text-xl font-semibold text-text">
          Document verification
        </h1>
        <p className="text-sm text-text-muted">
          Verify the authenticity and validity of identity documents.
        </p>
      </header>

      <div className="grid gap-4 lg:grid-cols-[minmax(0,420px)_minmax(0,1fr)]">
        <div className="console-card">
          <div className="console-card-header">
            <div>
              <div className="text-sm font-semibold text-text">
                Document details
              </div>
              <div className="text-xs text-text-muted">
                Provide the document type and number to verify.
              </div>
            </div>
          </div>

          <form
            className="console-card-body space-y-4"
            onSubmit={handleSubmit}
          >
            <ServiceField
              label="Document type"
              description="Select the type of identity document."
            >
              <select
                id="documentType"
                name="documentType"
                className="aws-select w-full select-input"
                value={documentType}
                onChange={(event) => setDocumentType(event.target.value)}
              >
                {DOCUMENT_TYPES.map((item) => (
                  <option key={item.value} value={item.value}>
                    {item.label}
                  </option>
                ))}
              </select>
            </ServiceField>

            <ServiceField
              label="Document number"
              description="The unique number printed on the document."
            >
              <input
                required
                id="documentNumber"
                name="documentNumber"
                value={documentNumber}
                onChange={(event) => setDocumentNumber(event.target.value)}
                className="aws-input w-full"
              />
            </ServiceField>

            <ServiceField
              label="Document image (optional)"
              description="Upload a scan or photo of the document for enhanced verification."
            >
              <FileUpload
                accept="image/*,.pdf"
                maxSize={10 * 1024 * 1024}
                progress={uploadProgress}
                uploading={isUploading}
                error={uploadError ?? undefined}
                onFileSelect={handleFileSelect}
                onClear={handleFileClear}
              />
            </ServiceField>

            <div className="flex items-center justify-between gap-3 pt-2">
              <p className="text-xs text-text-muted">
                Documents are verified against issuing authority databases.
              </p>
              <Button
                type="submit"
                variant="cta"
                size="md"
                disabled={submitDisabled}
                loading={loading}
              >
                Verify
              </Button>
            </div>

            {error && (
              <div role="alert" className="rounded border border-danger/40 bg-danger/5 px-3 py-2 text-xs text-danger">
                {error}
              </div>
            )}
          </form>
        </div>

        <div ref={resultRef} tabIndex={-1} className="outline-none">
          <AnimatedResult>
            {loading ? (
              <div className="console-card">
                <div className="console-card-header">
                  <div>
                    <Skeleton className="h-5 w-32 mb-2" />
                    <Skeleton className="h-3 w-48" />
                  </div>
                  <Skeleton className="h-9 w-28 rounded-full" />
                </div>
                <div className="console-card-body space-y-6 p-4">
                  <div className="border border-border rounded overflow-hidden">
                    <div className="divide-y divide-border">
                      {[...Array(4)].map((_, i) => (
                        <div key={i} className="flex items-center px-4 py-2.5">
                          <Skeleton className="h-4 w-32 bg-background/50" />
                          <Skeleton className="h-4 w-24 ml-auto" />
                        </div>
                      ))}
                    </div>
                  </div>
                </div>
              </div>
            ) : error && !result ? (
              <div className="console-card">
                <div className="console-card-body flex items-center justify-between">
                  <span className="text-sm text-danger">{error}</span>
                  <RetryButton onRetry={doVerification} />
                </div>
              </div>
            ) : result ? (
              <VerificationResultCard
                ref={resultCardRef}
                title="Document results"
                reference={result.reference}
                status={result.document.verified ? "verified" : "not_verified"}
                onExport={handleExport}
                fields={[
                  { label: "Provider", value: result.provider },
                  { label: "Status", value: result.document.status },
                  {
                    label: "Issued date",
                    value: new Date(result.document.issuedDate).toLocaleDateString(),
                  },
                  {
                    label: "Expiry date",
                    value: result.document.expiryDate
                      ? new Date(result.document.expiryDate).toLocaleDateString()
                      : "No expiry",
                  },
                ]}
              />
            ) : (
              <VerificationEmptyState
                icon={FileCheck}
                heading="No results yet"
                description="Enter document details and click Verify to see results."
              />
            )}
          </AnimatedResult>
        </div>
      </div>

      <ProcessingDialog
        open={loading}
        title="Verifying document"
        message="Checking document authenticity against issuing authority records."
      />
    </div>
  );
}
