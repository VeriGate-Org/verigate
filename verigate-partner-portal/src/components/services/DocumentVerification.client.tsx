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
import { type DocumentVerificationResponse, type ValidationCheck } from "@/lib/mock-services";
import { executeVerification } from "@/lib/services/verification-service";
import { exportPdf } from "@/lib/utils/export-pdf";
import {
  getDocumentPresignedUrl,
  uploadFileToS3,
} from "@/lib/bff-client";
import { FileCheck, CheckCircle2, XCircle } from "lucide-react";
import { DOCUMENT_TYPE_GROUPS, DOCUMENT_FIELD_CONFIGS } from "@/components/services/document-verification/documentFieldConfigs";
import { validateField, validateAllFields } from "@/lib/validations/document-validation";

function ValidationBadge({ check }: { check: ValidationCheck }) {
  const pass = check.status === "PASS";
  return (
    <div
      className={`inline-flex items-center gap-1.5 rounded-full px-2.5 py-1 text-xs font-medium ${
        pass ? "bg-green-500/10 text-green-600" : "bg-red-500/10 text-red-600"
      }`}
      title={check.detail}
    >
      {pass ? (
        <CheckCircle2 className="h-3 w-3" />
      ) : (
        <XCircle className="h-3 w-3" />
      )}
      {check.name.replace(/_/g, " ")}
    </div>
  );
}

export default function DocumentVerification() {
  const [documentType, setDocumentType] = useState<string>(
    DOCUMENT_TYPE_GROUPS[0]?.types[0]?.value ?? ""
  );
  const [additionalFields, setAdditionalFields] = useState<Record<string, string>>({});
  const [fieldErrors, setFieldErrors] = useState<Record<string, string | null>>({});
  const [result, setResult] = useState<DocumentVerificationResponse | null>(
    null
  );
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const resultRef = useRef<HTMLDivElement>(null);
  const resultCardRef = useRef<HTMLDivElement>(null);
  const { toast } = useToast();

  // Single-file upload state
  const [, setSelectedFile] = useState<File | null>(null);
  const [uploadProgress, setUploadProgress] = useState<number>(0);
  const [s3ObjectKey, setS3ObjectKey] = useState<string>("");
  const [s3BucketName, setS3BucketName] = useState<string>("");
  const [isUploading, setIsUploading] = useState(false);
  const [uploadError, setUploadError] = useState<string | null>(null);

  const fieldConfigs = DOCUMENT_FIELD_CONFIGS[documentType] ?? [];
  const primaryFieldName = fieldConfigs[0]?.name ?? "documentNumber";

  const handleDocumentTypeChange = useCallback((newType: string) => {
    setDocumentType(newType);
    setAdditionalFields({});
    setFieldErrors({});
    setResult(null);
    setUploadError(null);
  }, []);

  const handleFieldChange = useCallback(
    (fieldName: string, value: string) => {
      setAdditionalFields((prev) => ({ ...prev, [fieldName]: value }));
      const err = validateField(documentType, fieldName, value);
      setFieldErrors((prev) => ({ ...prev, [fieldName]: err }));
    },
    [documentType]
  );

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
    // Validate all fields before submission
    const errors = validateAllFields(documentType, additionalFields);
    const hasErrors = Object.values(errors).some((e) => e !== null);
    if (hasErrors) {
      setFieldErrors(errors);
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const documentNumber = additionalFields[primaryFieldName] ?? "";
      const metadata: Record<string, unknown> = {
        documentType,
        documentNumber,
        documentReference: documentNumber,
        additionalFields,
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
  }, [documentType, additionalFields, primaryFieldName, s3ObjectKey, s3BucketName, toast]);

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    await doVerification();
  };

  const primaryValue = additionalFields[primaryFieldName] ?? "";
  const submitDisabled =
    loading || primaryValue.trim().length < 1 || isUploading;

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

      <div className="grid gap-4 lg:grid-cols-[minmax(0,420px)_minmax(0,1fr)]">
        <div className="console-card">
          <div className="console-card-header">
            <div>
              <div className="text-sm font-semibold text-text">
                Document details
              </div>
              <div className="text-xs text-text-muted">
                Provide the document type and details to verify.
              </div>
            </div>
          </div>

          <form
            className="console-card-body space-y-4"
            onSubmit={handleSubmit}
          >
            <ServiceField
              label="Document type"
              description="Select the type of document to verify."
            >
              <select
                id="documentType"
                name="documentType"
                className="aws-select w-full select-input"
                value={documentType}
                onChange={(event) => handleDocumentTypeChange(event.target.value)}
              >
                {DOCUMENT_TYPE_GROUPS.map((group) => (
                  <optgroup key={group.label} label={group.label}>
                    {group.types.map((item) => (
                      <option key={item.value} value={item.value}>
                        {item.label}
                      </option>
                    ))}
                  </optgroup>
                ))}
              </select>
            </ServiceField>

            {fieldConfigs.map((config) => (
              <ServiceField
                key={config.name}
                label={config.label}
                description={config.description}
                error={fieldErrors[config.name]}
              >
                {config.type === "select" && config.options ? (
                  <select
                    id={config.name}
                    name={config.name}
                    className={`aws-select w-full select-input ${fieldErrors[config.name] ? "border-danger" : ""}`}
                    value={additionalFields[config.name] ?? ""}
                    onChange={(e) => handleFieldChange(config.name, e.target.value)}
                    aria-invalid={!!fieldErrors[config.name]}
                  >
                    <option value="">Select...</option>
                    {config.options.map((opt) => (
                      <option key={opt.value} value={opt.value}>
                        {opt.label}
                      </option>
                    ))}
                  </select>
                ) : (
                  <input
                    id={config.name}
                    name={config.name}
                    value={additionalFields[config.name] ?? ""}
                    onChange={(e) => handleFieldChange(config.name, e.target.value)}
                    className={`aws-input w-full ${fieldErrors[config.name] ? "border-danger" : ""}`}
                    placeholder={config.placeholder}
                    inputMode={config.inputMode}
                    maxLength={config.maxLength}
                    aria-invalid={!!fieldErrors[config.name]}
                  />
                )}
              </ServiceField>
            ))}

            <ServiceField
              label="Document image (optional)"
              description="Upload a supporting document for record-keeping purposes."
              error={uploadError ?? undefined}
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
                Analysed using Document AI.
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

        <div ref={resultRef} tabIndex={-1} className="outline-none space-y-4">
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
                      {[...Array(6)].map((_, i) => (
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
              <>
                <VerificationResultCard
                  ref={resultCardRef}
                  title="Document results"
                  reference={result.reference}
                  status={result.document.verified ? "verified" : "not_verified"}
                  confidenceScore={Math.round(result.overallConfidence * 100)}
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
                  matchFields={result.validationChecks.map((c) => ({
                    label: c.name.replace(/_/g, " "),
                    matched: c.status === "PASS",
                  }))}
                />

                {/* Validation Checks */}
                {result.validationChecks.length > 0 && (
                  <div className="console-card">
                    <div className="console-card-header">
                      <span className="text-sm font-semibold text-text">
                        Validation checks
                      </span>
                    </div>
                    <div className="console-card-body">
                      <div className="flex flex-wrap gap-2">
                        {result.validationChecks.map((check) => (
                          <ValidationBadge key={check.name} check={check} />
                        ))}
                      </div>
                    </div>
                  </div>
                )}

              </>
            ) : (
              <VerificationEmptyState
                icon={FileCheck}
                heading="No results yet"
                description="Enter document details and click Verify to analyse using Document AI."
              />
            )}
          </AnimatedResult>
        </div>
      </div>

      <ProcessingDialog
        open={loading}
        title="Verifying document"
        message="Analysing document."
      />
    </div>
  );
}
