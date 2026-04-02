"use client";

import { useCallback, useRef, useState } from "react";
import type { FormEvent } from "react";
import { ProcessingDialog } from "@/components/ui/ProcessingDialog";
import { Button } from "@/components/ui/Button";
import { Skeleton } from "@/components/ui/Loading/Skeleton";
import { VerificationResultCard } from "@/components/verification/VerificationResultCard";
import { VerificationEmptyState } from "@/components/verification/VerificationEmptyState";
import { AnimatedResult } from "@/components/verification/AnimatedResult";
import { RetryButton } from "@/components/verification/RetryButton";
import { ScreenReaderAnnounce } from "@/components/ui/ScreenReaderAnnounce";
import { ServiceField } from "@/components/services/shared/ServiceField";
import { useToast } from "@/components/ui/Toast";
import { executeVerification } from "@/lib/services/verification-service";
import { validateSaId, extractDateOfBirth, extractGender } from "@/lib/utils/sa-id-validation";
import { exportPdf } from "@/lib/utils/export-pdf";
import type { HanisIdentityVerificationResponse } from "@/lib/types/identity-verification";
import { type IdentityResponse } from "@/lib/mock-services";
import { Fingerprint, ShieldCheck, FileText, Heart, Users, Camera } from "lucide-react";

type VerificationResult = HanisIdentityVerificationResponse | IdentityResponse;

function isHanisResponse(r: VerificationResult): r is HanisIdentityVerificationResponse {
  return "citizenDetails" in r;
}

export default function IdentityVerification() {
  const [idNumber, setIdNumber] = useState("");
  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [includePhoto, setIncludePhoto] = useState(true);
  const [useHanis, setUseHanis] = useState(true);
  const [result, setResult] = useState<VerificationResult | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [idError, setIdError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const resultRef = useRef<HTMLDivElement>(null);
  const resultCardRef = useRef<HTMLDivElement>(null);
  const { toast } = useToast();

  // Derived fields from ID number
  const derivedDob = idNumber.length === 13 ? extractDateOfBirth(idNumber) : null;
  const derivedGender = idNumber.length === 13 ? extractGender(idNumber) : null;

  const handleIdChange = useCallback((value: string) => {
    const digits = value.replace(/\D/g, "").slice(0, 13);
    setIdNumber(digits);
    if (digits.length === 13) {
      const validation = validateSaId(digits);
      setIdError(validation.valid ? null : validation.errors[0]);
    } else {
      setIdError(null);
    }
  }, []);

  const handleExport = useCallback(async () => {
    if (resultCardRef.current) {
      await exportPdf(resultCardRef.current, "verigate-identity-verification.pdf");
    }
  }, []);

  const doVerification = useCallback(async () => {
    setLoading(true);
    setError(null);

    try {
      const data = await executeVerification("IDENTITY_VERIFICATION", {
        idNumber,
        firstName,
        lastName,
        includePhoto,
        useHanis,
      }) as VerificationResult;
      setResult(data);
      toast({ title: "Verification complete", variant: "success" });
      setTimeout(() => resultRef.current?.focus(), 100);
    } catch (err) {
      const message = err instanceof Error ? err.message : "Verification failed";
      setError(message);
      setResult(null);
      toast({ title: "Verification failed", description: message, variant: "error" });
    } finally {
      setLoading(false);
    }
  }, [idNumber, firstName, lastName, includePhoto, useHanis, toast]);

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    const validation = validateSaId(idNumber);
    if (!validation.valid) {
      setIdError(validation.errors[0]);
      return;
    }

    await doVerification();
  };

  const submitDisabled = loading || idNumber.length !== 13 || idError !== null || !firstName.trim() || !lastName.trim();

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
        <h1 className="text-xl font-semibold text-text">Identity verification</h1>
        <p className="text-sm text-text-muted">
          Verify identity against the DHA/HANIS National Population Register with photo and biometric data.
        </p>
      </header>

      <div className="grid gap-4 lg:grid-cols-[minmax(0,420px)_minmax(0,1fr)]">
        <div className="console-card">
          <div className="console-card-header">
            <div>
              <div className="text-sm font-semibold text-text">Subject details</div>
              <div className="text-xs text-text-muted">Enter the individual&apos;s identity information.</div>
            </div>
          </div>

          <form className="console-card-body space-y-4" onSubmit={handleSubmit}>
            <ServiceField label="ID number" description="13-digit South African ID number." error={idError}>
              <input
                required
                id="idNumber"
                name="idNumber"
                value={idNumber}
                onChange={(event) => handleIdChange(event.target.value)}
                className={`aws-input w-full ${idError ? "border-danger" : ""}`}
                inputMode="numeric"
                maxLength={13}
                aria-invalid={!!idError}
                aria-describedby={idError ? "idNumber-error" : undefined}
              />
            </ServiceField>

            {derivedDob && (
              <div className="flex gap-4 text-xs text-text-muted">
                <span>DOB: {derivedDob}</span>
                {derivedGender && <span>Gender: {derivedGender}</span>}
              </div>
            )}

            <ServiceField label="First name">
              <input
                required
                id="firstName"
                name="firstName"
                value={firstName}
                onChange={(event) => setFirstName(event.target.value)}
                className="aws-input w-full"
                autoComplete="given-name"
              />
            </ServiceField>

            <ServiceField label="Last name">
              <input
                required
                id="lastName"
                name="lastName"
                value={lastName}
                onChange={(event) => setLastName(event.target.value)}
                className="aws-input w-full"
                autoComplete="family-name"
              />
            </ServiceField>

            <div className="flex items-center gap-3">
              <label className="flex items-center gap-2 text-sm text-text cursor-pointer">
                <input
                  type="checkbox"
                  checked={includePhoto}
                  onChange={(e) => setIncludePhoto(e.target.checked)}
                  className="rounded border-border"
                />
                Include photo
              </label>
              <label className="flex items-center gap-2 text-sm text-text cursor-pointer">
                <input
                  type="checkbox"
                  checked={useHanis}
                  onChange={(e) => setUseHanis(e.target.checked)}
                  className="rounded border-border"
                />
                HANIS/NPR (enhanced)
              </label>
            </div>

            <div className="flex items-center justify-between gap-3 pt-2">
              <p className="text-xs text-text-muted">
                Identity matched against DHA records and NPR data.
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
              <LoadingSkeleton />
            ) : error && !result ? (
              <div className="console-card">
                <div className="console-card-body flex items-center justify-between">
                  <span className="text-sm text-danger">{error}</span>
                  <RetryButton onRetry={doVerification} />
                </div>
              </div>
            ) : result && isHanisResponse(result) ? (
              <HanisResultDisplay result={result} ref={resultCardRef} onExport={handleExport} />
            ) : result ? (
              <VerificationResultCard
                ref={resultCardRef}
                title="Identity results"
                reference={(result as IdentityResponse).reference}
                status={(result as IdentityResponse).identity.verified ? "verified" : "not_verified"}
                confidenceScore={(result as IdentityResponse).identity.matchScore}
                onExport={handleExport}
                fields={[
                  { label: "Provider", value: (result as IdentityResponse).provider },
                ]}
                matchFields={[
                  { label: (result as IdentityResponse).identity.photoMatch ? "Photo matched" : "Photo not matched", matched: (result as IdentityResponse).identity.photoMatch },
                  { label: (result as IdentityResponse).identity.livenessCheck ? "Liveness passed" : "Liveness failed", matched: (result as IdentityResponse).identity.livenessCheck },
                ]}
              />
            ) : (
              <VerificationEmptyState
                icon={Fingerprint}
                heading="No results yet"
                description="Enter identity details and click Verify to see results."
              />
            )}
          </AnimatedResult>
        </div>
      </div>

      <ProcessingDialog
        open={loading}
        title="Verifying identity"
        message="Running identity verification against the DHA/HANIS National Population Register."
      />
    </div>
  );
}

function LoadingSkeleton() {
  return (
    <div className="console-card">
      <div className="console-card-header">
        <div>
          <Skeleton className="h-5 w-32 mb-2" />
          <Skeleton className="h-3 w-48" />
        </div>
        <Skeleton className="h-9 w-28 rounded-full" />
      </div>
      <div className="console-card-body space-y-6 p-4">
        {[...Array(3)].map((_, i) => (
          <div key={i} className="border border-border rounded overflow-hidden">
            <div className="divide-y divide-border">
              {[...Array(4)].map((_, j) => (
                <div key={j} className="flex items-center px-4 py-2.5">
                  <Skeleton className="h-4 w-32 bg-background/50" />
                  <Skeleton className="h-4 w-24 ml-auto" />
                </div>
              ))}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

// ----- HANIS Result Display -----

interface HanisResultDisplayProps {
  result: HanisIdentityVerificationResponse;
  onExport: () => void;
}

const HanisResultDisplay = ({ result, onExport, ...props }: HanisResultDisplayProps & { ref?: React.Ref<HTMLDivElement> }) => {
  const ref = (props as { ref?: React.Ref<HTMLDivElement> }).ref;
  const statusMap: Record<string, { text: string; className: string }> = {
    VERIFIED: { text: "Verified", className: "bg-success/10 text-success" },
    DECEASED: { text: "Deceased", className: "bg-danger/10 text-danger" },
    BLOCKED: { text: "Blocked", className: "bg-danger/10 text-danger" },
    NOT_FOUND: { text: "Not Found", className: "bg-warning/10 text-warning" },
    MISMATCH: { text: "Mismatch", className: "bg-warning/10 text-warning" },
    ERROR: { text: "Error", className: "bg-danger/10 text-danger" },
  };

  const badge = statusMap[result.status] ?? statusMap.ERROR;
  const resultStatus = result.status === "VERIFIED" ? "verified" as const : "not_verified" as const;

  return (
    <div ref={ref as React.Ref<HTMLDivElement>} className="space-y-4">
      {/* Header card */}
      <VerificationResultCard
        title="HANIS Identity Verification"
        reference={result.reference}
        status={resultStatus}
        onExport={onExport}
        fields={[
          { label: "Provider", value: result.provider },
          { label: "Source", value: result.source.toUpperCase() },
          { label: "Transaction ID", value: result.transactionId },
        ]}
        matchFields={[
          { label: result.citizenDetails.onHanis ? "On HANIS" : "Not on HANIS", matched: result.citizenDetails.onHanis },
          { label: result.citizenDetails.onNpr ? "On NPR" : "Not on NPR", matched: result.citizenDetails.onNpr },
          { label: result.photoAvailable ? "Photo available" : "No photo", matched: result.photoAvailable },
        ]}
      />

      {/* Citizen Details */}
      {result.citizenDetails && (result.citizenDetails.name || result.citizenDetails.surname) && (
        <DetailCard title="Citizen Details" icon={<ShieldCheck className="h-4 w-4" />}>
          <DetailRow label="Name" value={result.citizenDetails.name} />
          <DetailRow label="Surname" value={result.citizenDetails.surname} />
          <DetailRow label="ID Number" value={result.citizenDetails.idNumber} />
          <DetailRow label="Citizenship" value={result.citizenDetails.citizenshipStatus} />
          <DetailRow label="Birth Country" value={result.citizenDetails.birthCountry} />
        </DetailCard>
      )}

      {/* Photo */}
      {result.photoAvailable && result.photoBase64 && (
        <DetailCard title="Photo" icon={<Camera className="h-4 w-4" />}>
          <div className="flex justify-center p-4">
            <img
              src={`data:image/png;base64,${result.photoBase64}`}
              alt="NPR photo"
              className="rounded border border-border max-w-[200px] max-h-[250px] object-contain bg-background"
            />
          </div>
        </DetailCard>
      )}

      {/* Document Info */}
      {result.documentInfo && (
        <DetailCard title="Document Information" icon={<FileText className="h-4 w-4" />}>
          <DetailRow label="Smart Card Issued" value={result.documentInfo.smartCardIssued ? "Yes" : "No"} />
          <DetailRow label="ID Issue Date" value={result.documentInfo.idIssueDate} />
          <DetailRow label="ID Sequence No" value={result.documentInfo.idSequenceNo} />
          <DetailRow label="ID Blocked" value={result.documentInfo.idnBlocked ? "Yes" : "No"} highlight={result.documentInfo.idnBlocked} />
        </DetailCard>
      )}

      {/* Marital Info */}
      {result.maritalInfo && result.maritalInfo.maritalStatus && (
        <DetailCard title="Marital Status" icon={<Users className="h-4 w-4" />}>
          <DetailRow label="Status" value={result.maritalInfo.maritalStatus} />
          <DetailRow label="Date of Marriage" value={result.maritalInfo.dateOfMarriage} />
        </DetailCard>
      )}

      {/* Vital Status */}
      {result.vitalStatus && (
        <DetailCard title="Vital Status" icon={<Heart className="h-4 w-4" />}>
          <DetailRow label="Status" value={result.vitalStatus.status} highlight={result.vitalStatus.deceased} />
          {result.vitalStatus.deceased && (
            <DetailRow label="Date of Death" value={result.vitalStatus.dateOfDeath} />
          )}
        </DetailCard>
      )}
    </div>
  );
};

function DetailCard({ title, icon, children }: { title: string; icon?: React.ReactNode; children: React.ReactNode }) {
  return (
    <div className="console-card">
      <div className="console-card-header">
        <div className="flex items-center gap-2">
          {icon}
          <span className="text-sm font-semibold text-text">{title}</span>
        </div>
      </div>
      <div className="console-card-body">
        <div className="border border-border rounded overflow-hidden divide-y divide-border">
          {children}
        </div>
      </div>
    </div>
  );
}

function DetailRow({ label, value, highlight = false }: { label: string; value?: string; highlight?: boolean }) {
  if (!value) return null;
  return (
    <div className="flex items-center justify-between px-4 py-2.5 text-sm">
      <span className="text-text-muted">{label}</span>
      <span className={`font-medium ${highlight ? "text-danger" : "text-text"}`}>{value}</span>
    </div>
  );
}
