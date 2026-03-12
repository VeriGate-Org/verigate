"use client";

import { useCallback, useState } from "react";
import type { FormEvent, ReactNode } from "react";
import JsonViewer from "@/components/code/JsonViewer";
import { ProcessingDialog } from "@/components/ui/ProcessingDialog";
import { Button } from "@/components/ui/Button";
import { Skeleton } from "@/components/ui/Loading/Skeleton";
import FileUpload from "@/components/ui/FileUpload/FileUpload";
import { type DocumentVerificationResponse } from "@/lib/mock-services";
import { executeVerification } from "@/lib/services/verification-service";
import {
  getDocumentPresignedUrl,
  uploadFileToS3,
} from "@/lib/bff-client";

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

  // File upload state
  const [, setSelectedFile] = useState<File | null>(null);
  const [uploadProgress, setUploadProgress] = useState<number>(0);
  const [s3ObjectKey, setS3ObjectKey] = useState<string>("");
  const [s3BucketName, setS3BucketName] = useState<string>("");
  const [isUploading, setIsUploading] = useState(false);
  const [uploadError, setUploadError] = useState<string | null>(null);

  const handleExport = useCallback(() => {
    if (typeof window !== "undefined") {
      window.print();
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

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setLoading(true);
    setError(null);

    try {
      // Build metadata, adding S3 info only when a file was uploaded
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
    } catch (err) {
      const message =
        err instanceof Error ? err.message : "Verification failed";
      setError(message);
      setResult(null);
    } finally {
      setLoading(false);
    }
  };

  const submitDisabled =
    loading || documentNumber.trim().length < 1 || isUploading;

  return (
    <div className="space-y-6">
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
            <Field
              label="Document type"
              description="Select the type of identity document."
            >
              <select
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
            </Field>

            <Field
              label="Document number"
              description="The unique number printed on the document."
            >
              <input
                required
                value={documentNumber}
                onChange={(event) => setDocumentNumber(event.target.value)}
                className="aws-input w-full"
                autoComplete="off"
              />
            </Field>

            <Field
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
            </Field>

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
              <div className="rounded border border-danger/40 bg-danger/5 px-3 py-2 text-xs text-danger">
                {error}
              </div>
            )}
          </form>
        </div>

        {/* Results Panel */}
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
              <div>
                <Skeleton className="h-4 w-36 mb-2" />
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
          </div>
        ) : result ? (
          <div className="console-card">
            <div className="console-card-header">
              <div>
                <div className="text-sm font-semibold text-text">
                  Verification results
                </div>
                <div className="text-xs text-text-muted">
                  Reference {result.reference}
                </div>
              </div>
              <button
                onClick={handleExport}
                className="rounded-full border border-[color:var(--color-cta)] bg-[color:var(--color-base-100)] text-[color:var(--color-cta)] hover:bg-[color:var(--color-cta)] hover:text-white px-aws-l py-aws-s text-sm transition-all shadow-sm"
              >
                Export PDF
              </button>
            </div>
            <div className="console-card-body space-y-6 p-4">
              {/* Document Details Section */}
              <div>
                <h3 className="text-sm font-medium text-text mb-2">
                  Document Details
                </h3>
                <div className="border border-border rounded overflow-hidden">
                  <table className="w-full">
                    <tbody className="divide-y divide-border">
                      <tr className="hover:bg-background/50">
                        <td className="px-4 py-2.5 text-sm text-text-muted bg-background/30 w-1/3">
                          Verified
                        </td>
                        <td className="px-4 py-2.5 text-sm">
                          <span
                            className={`inline-flex items-center px-2 py-0.5 rounded text-xs font-medium ${
                              result.document.verified
                                ? "bg-success/10 text-success"
                                : "bg-danger/10 text-danger"
                            }`}
                          >
                            {result.document.verified ? "Yes" : "No"}
                          </span>
                        </td>
                      </tr>
                      <tr className="hover:bg-background/50">
                        <td className="px-4 py-2.5 text-sm text-text-muted bg-background/30">
                          Status
                        </td>
                        <td className="px-4 py-2.5 text-sm font-medium text-text">
                          {result.document.status}
                        </td>
                      </tr>
                      <tr className="hover:bg-background/50">
                        <td className="px-4 py-2.5 text-sm text-text-muted bg-background/30">
                          Issued date
                        </td>
                        <td className="px-4 py-2.5 text-sm font-medium text-text">
                          {new Date(
                            result.document.issuedDate
                          ).toLocaleDateString()}
                        </td>
                      </tr>
                      <tr className="hover:bg-background/50">
                        <td className="px-4 py-2.5 text-sm text-text-muted bg-background/30">
                          Expiry date
                        </td>
                        <td className="px-4 py-2.5 text-sm font-medium text-text">
                          {result.document.expiryDate
                            ? new Date(
                                result.document.expiryDate
                              ).toLocaleDateString()
                            : "No expiry"}
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          </div>
        ) : (
          <div className="console-card">
            <div className="console-card-header">
              <div className="text-sm font-semibold text-text">
                Verification results
              </div>
            </div>
            <div className="console-card-body flex items-center justify-center py-12">
              <div className="text-center text-sm text-text-muted">
                <div className="mb-2">No results yet</div>
                <div className="text-xs">
                  Enter document details and click Verify to see results
                </div>
              </div>
            </div>
          </div>
        )}
      </div>

      {result && (
        <div className="console-card">
          <div className="console-card-header">
            <div className="text-sm font-semibold text-text">Raw response</div>
          </div>
          <div className="console-card-body bg-background">
            <JsonViewer data={result} />
          </div>
        </div>
      )}
      <ProcessingDialog
        open={loading}
        title="Verifying document"
        message="Checking document authenticity against issuing authority records."
      />
    </div>
  );
}

interface FieldProps {
  label: string;
  description?: string;
  children: ReactNode;
}

function Field({ label, description, children }: FieldProps) {
  return (
    <label className="block space-y-1 text-sm">
      <span className="font-medium text-text">{label}</span>
      {description && (
        <span className="block text-xs text-text-muted">{description}</span>
      )}
      {children}
    </label>
  );
}
