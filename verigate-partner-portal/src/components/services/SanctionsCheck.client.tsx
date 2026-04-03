"use client";

import { useCallback, useMemo, useRef, useState } from "react";
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
import SanctionsResultPanel from "@/components/services/sanctions/SanctionsResultPanel";
import { useToast } from "@/components/ui/Toast";
import { executeVerification } from "@/lib/services/verification-service";
import { exportPdf } from "@/lib/utils/export-pdf";
import type {
  EntityType,
  SanctionsScreeningRequest,
  SanctionsScreeningResponse,
} from "@/lib/types/sanctions-screening";
import { ShieldAlert, ChevronDown, ChevronUp } from "lucide-react";

/* ------------------------------------------------------------------ */
/*  Constants                                                          */
/* ------------------------------------------------------------------ */

const ENTITY_TYPES: EntityType[] = ["Person", "Company", "Organization", "Vessel"];

const ALGORITHMS = [
  { value: "logic-v1", label: "Logic v1" },
  { value: "logic-v2", label: "Logic v2 (recommended)" },
];

const TOPIC_OPTIONS = [
  { value: "sanction", label: "Sanctions" },
  { value: "role.pep", label: "Politically Exposed Persons" },
];

/* ------------------------------------------------------------------ */
/*  Component                                                          */
/* ------------------------------------------------------------------ */

export default function SanctionsCheck() {
  /* --- entity type --- */
  const [entityType, setEntityType] = useState<EntityType>("Person");

  /* --- person fields --- */
  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [dateOfBirth, setDateOfBirth] = useState("");
  const [nationality, setNationality] = useState("");
  const [gender, setGender] = useState("");
  const [idNumber, setIdNumber] = useState("");

  /* --- company / org / vessel shared name --- */
  const [name, setName] = useState("");

  /* --- company fields --- */
  const [registrationNumber, setRegistrationNumber] = useState("");
  const [taxId, setTaxId] = useState("");

  /* --- shared fields --- */
  const [jurisdiction, setJurisdiction] = useState("");
  const [address, setAddress] = useState("");

  /* --- vessel fields --- */
  const [imoNumber, setImoNumber] = useState("");
  const [mmsi, setMmsi] = useState("");
  const [flagState, setFlagState] = useState("");

  /* --- advanced options --- */
  const [showAdvanced, setShowAdvanced] = useState(false);
  const [dataset, setDataset] = useState("sanctions");
  const [algorithm, setAlgorithm] = useState("logic-v2");
  const [threshold, setThreshold] = useState(0.7);
  const [topics, setTopics] = useState<string[]>(["sanction", "role.pep"]);

  /* --- result / status --- */
  const [result, setResult] = useState<SanctionsScreeningResponse | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const resultRef = useRef<HTMLDivElement>(null);
  const resultCardRef = useRef<HTMLDivElement>(null);
  const { toast } = useToast();

  /* ---------------------------------------------------------------- */
  /*  Derived state                                                    */
  /* ---------------------------------------------------------------- */

  const requiredFieldsFilled = useMemo(() => {
    switch (entityType) {
      case "Person":
        return firstName.trim().length > 0 && lastName.trim().length > 0;
      case "Company":
      case "Organization":
      case "Vessel":
        return name.trim().length > 0;
      default:
        return false;
    }
  }, [entityType, firstName, lastName, name]);

  const submitDisabled = loading || !requiredFieldsFilled;

  const isClean = result
    ? result.outcome === "SUCCEEDED" && result.totalMatches === 0
    : false;

  const srMessage = loading
    ? "Loading screening results"
    : error
      ? "Screening failed"
      : result
        ? "Screening complete"
        : "";

  /* ---------------------------------------------------------------- */
  /*  Build request payload                                            */
  /* ---------------------------------------------------------------- */

  const buildRequest = useCallback((): SanctionsScreeningRequest => {
    const base: SanctionsScreeningRequest = {
      entityType,
      dataset,
      algorithm,
      threshold,
      topics,
    };

    switch (entityType) {
      case "Person":
        return {
          ...base,
          firstName,
          lastName,
          ...(dateOfBirth && { dateOfBirth }),
          ...(nationality && { nationality }),
          ...(gender && { gender }),
          ...(idNumber && { idNumber }),
          ...(address && { address }),
        };
      case "Company":
        return {
          ...base,
          name,
          ...(registrationNumber && { registrationNumber }),
          ...(jurisdiction && { jurisdiction }),
          ...(taxId && { taxId }),
          ...(address && { address }),
        };
      case "Organization":
        return {
          ...base,
          name,
          ...(jurisdiction && { jurisdiction }),
          ...(address && { address }),
        };
      case "Vessel":
        return {
          ...base,
          name,
          ...(imoNumber && { imoNumber }),
          ...(mmsi && { mmsi }),
          ...(flagState && { flagState }),
        };
      default:
        return base;
    }
  }, [
    entityType, firstName, lastName, dateOfBirth, nationality, gender, idNumber,
    name, registrationNumber, jurisdiction, taxId, address,
    imoNumber, mmsi, flagState,
    dataset, algorithm, threshold, topics,
  ]);

  /* ---------------------------------------------------------------- */
  /*  Handlers                                                         */
  /* ---------------------------------------------------------------- */

  const handleExport = useCallback(async () => {
    if (resultCardRef.current) {
      await exportPdf(resultCardRef.current, "verigate-sanctions-screening.pdf");
    }
  }, []);

  const doVerification = useCallback(async () => {
    setLoading(true);
    setError(null);

    try {
      const request = buildRequest();
      const data = (await executeVerification(
        "SANCTIONS_SCREENING",
        request,
      )) as SanctionsScreeningResponse;
      setResult(data);
      toast({ title: "Screening complete", variant: "success" });
      setTimeout(() => resultRef.current?.focus(), 100);
    } catch (err) {
      const message = err instanceof Error ? err.message : "Screening failed";
      setError(message);
      setResult(null);
      toast({ title: "Screening failed", description: message, variant: "error" });
    } finally {
      setLoading(false);
    }
  }, [buildRequest, toast]);

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    await doVerification();
  };

  const handleEntityTypeChange = (type: EntityType) => {
    setEntityType(type);
    setResult(null);
    setError(null);
  };

  const toggleTopic = (topic: string) => {
    setTopics((prev) =>
      prev.includes(topic) ? prev.filter((t) => t !== topic) : [...prev, topic],
    );
  };

  /* ---------------------------------------------------------------- */
  /*  Render                                                           */
  /* ---------------------------------------------------------------- */

  return (
    <div className="space-y-6">
      <ScreenReaderAnnounce message={srMessage} />
      <header className="space-y-1">
        <h1 className="text-xl font-semibold text-text">Sanctions & PEP screening</h1>
        <p className="text-sm text-text-muted">
          Screen entities against the OpenSanctions database for sanctions designations and
          politically exposed persons.
        </p>
      </header>

      <div className="grid gap-4 lg:grid-cols-[minmax(0,420px)_minmax(0,1fr)]">
        {/* -------- Form panel -------- */}
        <div className="console-card">
          <div className="console-card-header">
            <div>
              <div className="text-sm font-semibold text-text">Subject details</div>
              <div className="text-xs text-text-muted">
                Select entity type and enter screening information.
              </div>
            </div>
          </div>

          {/* Entity type tab bar */}
          <div className="border-b border-border">
            <nav className="flex" aria-label="Entity type">
              {ENTITY_TYPES.map((type) => (
                <button
                  key={type}
                  type="button"
                  onClick={() => handleEntityTypeChange(type)}
                  className={`flex-1 px-3 py-2.5 text-xs font-medium transition-colors border-b-2 ${
                    entityType === type
                      ? "border-cta text-cta"
                      : "border-transparent text-text-muted hover:text-text hover:border-border"
                  }`}
                >
                  {type}
                </button>
              ))}
            </nav>
          </div>

          <form className="console-card-body space-y-4" onSubmit={handleSubmit}>
            {/* ---- Dynamic fields per entity type ---- */}
            {entityType === "Person" && (
              <PersonFields
                firstName={firstName}
                lastName={lastName}
                dateOfBirth={dateOfBirth}
                nationality={nationality}
                gender={gender}
                idNumber={idNumber}
                address={address}
                onFirstNameChange={setFirstName}
                onLastNameChange={setLastName}
                onDateOfBirthChange={setDateOfBirth}
                onNationalityChange={setNationality}
                onGenderChange={setGender}
                onIdNumberChange={setIdNumber}
                onAddressChange={setAddress}
              />
            )}

            {entityType === "Company" && (
              <CompanyFields
                name={name}
                registrationNumber={registrationNumber}
                jurisdiction={jurisdiction}
                taxId={taxId}
                address={address}
                onNameChange={setName}
                onRegistrationNumberChange={setRegistrationNumber}
                onJurisdictionChange={setJurisdiction}
                onTaxIdChange={setTaxId}
                onAddressChange={setAddress}
              />
            )}

            {entityType === "Organization" && (
              <OrganisationFields
                name={name}
                jurisdiction={jurisdiction}
                address={address}
                onNameChange={setName}
                onJurisdictionChange={setJurisdiction}
                onAddressChange={setAddress}
              />
            )}

            {entityType === "Vessel" && (
              <VesselFields
                name={name}
                imoNumber={imoNumber}
                mmsi={mmsi}
                flagState={flagState}
                onNameChange={setName}
                onImoNumberChange={setImoNumber}
                onMmsiChange={setMmsi}
                onFlagStateChange={setFlagState}
              />
            )}

            {/* ---- Advanced options ---- */}
            <div className="border border-border rounded">
              <button
                type="button"
                onClick={() => setShowAdvanced(!showAdvanced)}
                className="flex w-full items-center justify-between px-3 py-2 text-xs font-medium text-text-muted hover:text-text transition-colors"
              >
                Advanced options
                {showAdvanced ? (
                  <ChevronUp className="h-3.5 w-3.5" />
                ) : (
                  <ChevronDown className="h-3.5 w-3.5" />
                )}
              </button>

              {showAdvanced && (
                <div className="border-t border-border px-3 py-3 space-y-4">
                  <ServiceField label="Dataset">
                    <input
                      id="dataset"
                      name="dataset"
                      value={dataset}
                      onChange={(e) => setDataset(e.target.value)}
                      className="aws-input w-full"
                      placeholder="sanctions"
                    />
                  </ServiceField>

                  <ServiceField label="Algorithm">
                    <select
                      id="algorithm"
                      name="algorithm"
                      value={algorithm}
                      onChange={(e) => setAlgorithm(e.target.value)}
                      className="aws-input w-full"
                    >
                      {ALGORITHMS.map((a) => (
                        <option key={a.value} value={a.value}>
                          {a.label}
                        </option>
                      ))}
                    </select>
                  </ServiceField>

                  <ServiceField
                    label={`Threshold: ${threshold.toFixed(2)}`}
                    description="Minimum score (0 to 1) to include a match."
                  >
                    <input
                      type="range"
                      id="threshold"
                      name="threshold"
                      min={0}
                      max={1}
                      step={0.01}
                      value={threshold}
                      onChange={(e) => setThreshold(Number(e.target.value))}
                      className="w-full accent-cta"
                    />
                    <div className="flex justify-between text-xs text-text-muted mt-1">
                      <span>0.00</span>
                      <span>1.00</span>
                    </div>
                  </ServiceField>

                  <fieldset>
                    <legend className="text-sm font-medium text-text mb-2">Topics</legend>
                    <div className="flex flex-wrap gap-3">
                      {TOPIC_OPTIONS.map((opt) => (
                        <label
                          key={opt.value}
                          className="flex items-center gap-2 text-sm text-text cursor-pointer"
                        >
                          <input
                            type="checkbox"
                            checked={topics.includes(opt.value)}
                            onChange={() => toggleTopic(opt.value)}
                            className="rounded border-border"
                          />
                          {opt.label}
                        </label>
                      ))}
                    </div>
                  </fieldset>
                </div>
              )}
            </div>

            {/* ---- Submit ---- */}
            <div className="flex items-center justify-between gap-3 pt-2">
              <p className="text-xs text-text-muted">
                Screened against OpenSanctions datasets.
              </p>
              <Button
                type="submit"
                variant="cta"
                size="md"
                disabled={submitDisabled}
                loading={loading}
              >
                Screen
              </Button>
            </div>

            {error && (
              <div
                role="alert"
                className="rounded border border-danger/40 bg-danger/5 px-3 py-2 text-xs text-danger"
              >
                {error}
              </div>
            )}
          </form>
        </div>

        {/* -------- Result panel -------- */}
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
            ) : result ? (
              <div ref={resultCardRef} className="space-y-4">
                <VerificationResultCard
                  title="Screening summary"
                  reference={result.correlationId}
                  status={isClean ? "verified" : "not_verified"}
                  onExport={handleExport}
                  fields={[
                    { label: "Provider", value: result.provider },
                    { label: "Total matches", value: String(result.totalMatches) },
                    { label: "Outcome", value: result.outcome },
                    { label: "Dataset", value: result.dataset },
                  ]}
                  matchFields={[
                    {
                      label:
                        result.totalMatches === 0
                          ? "No sanctions matches"
                          : `${result.totalMatches} match(es) found`,
                      matched: result.totalMatches === 0,
                    },
                    {
                      label:
                        result.outcome === "SUCCEEDED" ? "Screening succeeded" : "Screening issue",
                      matched: result.outcome === "SUCCEEDED",
                    },
                  ]}
                />

                <SanctionsResultPanel
                  response={result}
                  onViewEntity={(entityId) => {
                    // Entity detail view can be implemented as a follow-up
                    console.log("View entity:", entityId);
                  }}
                  onExportPdf={handleExport}
                />
              </div>
            ) : (
              <VerificationEmptyState
                icon={ShieldAlert}
                heading="No results yet"
                description="Select an entity type, enter details, and click Screen to check for sanctions and PEP matches."
              />
            )}
          </AnimatedResult>
        </div>
      </div>

      <ProcessingDialog
        open={loading}
        title="Screening subject"
        message="Screening against OpenSanctions..."
      />
    </div>
  );
}

/* ------------------------------------------------------------------ */
/*  Entity-specific field groups                                       */
/* ------------------------------------------------------------------ */

interface PersonFieldsProps {
  firstName: string;
  lastName: string;
  dateOfBirth: string;
  nationality: string;
  gender: string;
  idNumber: string;
  address: string;
  onFirstNameChange: (v: string) => void;
  onLastNameChange: (v: string) => void;
  onDateOfBirthChange: (v: string) => void;
  onNationalityChange: (v: string) => void;
  onGenderChange: (v: string) => void;
  onIdNumberChange: (v: string) => void;
  onAddressChange: (v: string) => void;
}

function PersonFields({
  firstName,
  lastName,
  dateOfBirth,
  nationality,
  gender,
  idNumber,
  address,
  onFirstNameChange,
  onLastNameChange,
  onDateOfBirthChange,
  onNationalityChange,
  onGenderChange,
  onIdNumberChange,
  onAddressChange,
}: PersonFieldsProps) {
  return (
    <>
      <ServiceField label="First name *">
        <input
          required
          id="firstName"
          name="firstName"
          value={firstName}
          onChange={(e) => onFirstNameChange(e.target.value)}
          className="aws-input w-full"
          autoComplete="given-name"
        />
      </ServiceField>

      <ServiceField label="Last name *">
        <input
          required
          id="lastName"
          name="lastName"
          value={lastName}
          onChange={(e) => onLastNameChange(e.target.value)}
          className="aws-input w-full"
          autoComplete="family-name"
        />
      </ServiceField>

      <ServiceField label="Date of birth" description="YYYY-MM-DD format.">
        <input
          id="dateOfBirth"
          name="dateOfBirth"
          type="date"
          value={dateOfBirth}
          onChange={(e) => onDateOfBirthChange(e.target.value)}
          className="aws-input w-full"
        />
      </ServiceField>

      <ServiceField label="Nationality">
        <input
          id="nationality"
          name="nationality"
          value={nationality}
          onChange={(e) => onNationalityChange(e.target.value)}
          className="aws-input w-full"
          placeholder="e.g. South African"
        />
      </ServiceField>

      <ServiceField label="Gender">
        <select
          id="gender"
          name="gender"
          value={gender}
          onChange={(e) => onGenderChange(e.target.value)}
          className="aws-input w-full"
        >
          <option value="">-- Select --</option>
          <option value="male">Male</option>
          <option value="female">Female</option>
          <option value="other">Other</option>
        </select>
      </ServiceField>

      <ServiceField label="ID number">
        <input
          id="idNumber"
          name="idNumber"
          value={idNumber}
          onChange={(e) => onIdNumberChange(e.target.value)}
          className="aws-input w-full"
        />
      </ServiceField>

      <ServiceField label="Address">
        <input
          id="address"
          name="address"
          value={address}
          onChange={(e) => onAddressChange(e.target.value)}
          className="aws-input w-full"
          placeholder="Street, city, country"
        />
      </ServiceField>
    </>
  );
}

interface CompanyFieldsProps {
  name: string;
  registrationNumber: string;
  jurisdiction: string;
  taxId: string;
  address: string;
  onNameChange: (v: string) => void;
  onRegistrationNumberChange: (v: string) => void;
  onJurisdictionChange: (v: string) => void;
  onTaxIdChange: (v: string) => void;
  onAddressChange: (v: string) => void;
}

function CompanyFields({
  name,
  registrationNumber,
  jurisdiction,
  taxId,
  address,
  onNameChange,
  onRegistrationNumberChange,
  onJurisdictionChange,
  onTaxIdChange,
  onAddressChange,
}: CompanyFieldsProps) {
  return (
    <>
      <ServiceField label="Company name *">
        <input
          required
          id="companyName"
          name="companyName"
          value={name}
          onChange={(e) => onNameChange(e.target.value)}
          className="aws-input w-full"
        />
      </ServiceField>

      <ServiceField label="Registration number">
        <input
          id="registrationNumber"
          name="registrationNumber"
          value={registrationNumber}
          onChange={(e) => onRegistrationNumberChange(e.target.value)}
          className="aws-input w-full"
        />
      </ServiceField>

      <ServiceField label="Jurisdiction">
        <input
          id="jurisdiction"
          name="jurisdiction"
          value={jurisdiction}
          onChange={(e) => onJurisdictionChange(e.target.value)}
          className="aws-input w-full"
          placeholder="e.g. South Africa"
        />
      </ServiceField>

      <ServiceField label="Tax ID">
        <input
          id="taxId"
          name="taxId"
          value={taxId}
          onChange={(e) => onTaxIdChange(e.target.value)}
          className="aws-input w-full"
        />
      </ServiceField>

      <ServiceField label="Address">
        <input
          id="address"
          name="address"
          value={address}
          onChange={(e) => onAddressChange(e.target.value)}
          className="aws-input w-full"
          placeholder="Street, city, country"
        />
      </ServiceField>
    </>
  );
}

interface OrganisationFieldsProps {
  name: string;
  jurisdiction: string;
  address: string;
  onNameChange: (v: string) => void;
  onJurisdictionChange: (v: string) => void;
  onAddressChange: (v: string) => void;
}

function OrganisationFields({
  name,
  jurisdiction,
  address,
  onNameChange,
  onJurisdictionChange,
  onAddressChange,
}: OrganisationFieldsProps) {
  return (
    <>
      <ServiceField label="Organisation name *">
        <input
          required
          id="orgName"
          name="orgName"
          value={name}
          onChange={(e) => onNameChange(e.target.value)}
          className="aws-input w-full"
        />
      </ServiceField>

      <ServiceField label="Jurisdiction">
        <input
          id="jurisdiction"
          name="jurisdiction"
          value={jurisdiction}
          onChange={(e) => onJurisdictionChange(e.target.value)}
          className="aws-input w-full"
          placeholder="e.g. United Nations"
        />
      </ServiceField>

      <ServiceField label="Address">
        <input
          id="address"
          name="address"
          value={address}
          onChange={(e) => onAddressChange(e.target.value)}
          className="aws-input w-full"
          placeholder="Street, city, country"
        />
      </ServiceField>
    </>
  );
}

interface VesselFieldsProps {
  name: string;
  imoNumber: string;
  mmsi: string;
  flagState: string;
  onNameChange: (v: string) => void;
  onImoNumberChange: (v: string) => void;
  onMmsiChange: (v: string) => void;
  onFlagStateChange: (v: string) => void;
}

function VesselFields({
  name,
  imoNumber,
  mmsi,
  flagState,
  onNameChange,
  onImoNumberChange,
  onMmsiChange,
  onFlagStateChange,
}: VesselFieldsProps) {
  return (
    <>
      <ServiceField label="Vessel name *">
        <input
          required
          id="vesselName"
          name="vesselName"
          value={name}
          onChange={(e) => onNameChange(e.target.value)}
          className="aws-input w-full"
        />
      </ServiceField>

      <ServiceField label="IMO number" description="International Maritime Organization number.">
        <input
          id="imoNumber"
          name="imoNumber"
          value={imoNumber}
          onChange={(e) => onImoNumberChange(e.target.value)}
          className="aws-input w-full"
          placeholder="e.g. 9074729"
        />
      </ServiceField>

      <ServiceField label="MMSI" description="Maritime Mobile Service Identity.">
        <input
          id="mmsi"
          name="mmsi"
          value={mmsi}
          onChange={(e) => onMmsiChange(e.target.value)}
          className="aws-input w-full"
          placeholder="e.g. 636092624"
        />
      </ServiceField>

      <ServiceField label="Flag state">
        <input
          id="flagState"
          name="flagState"
          value={flagState}
          onChange={(e) => onFlagStateChange(e.target.value)}
          className="aws-input w-full"
          placeholder="e.g. Panama"
        />
      </ServiceField>
    </>
  );
}

/* ------------------------------------------------------------------ */
/*  Loading skeleton                                                   */
/* ------------------------------------------------------------------ */

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
        <div className="grid grid-cols-2 gap-4 sm:grid-cols-4">
          {[...Array(4)].map((_, i) => (
            <div key={i} className="border border-border rounded p-4">
              <Skeleton className="h-3 w-20 mb-2" />
              <Skeleton className="h-6 w-16" />
            </div>
          ))}
        </div>
        {[...Array(3)].map((_, i) => (
          <div key={i} className="border border-border rounded overflow-hidden">
            <div className="flex items-center justify-between px-4 py-3">
              <Skeleton className="h-4 w-48" />
              <Skeleton className="h-4 w-12" />
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
