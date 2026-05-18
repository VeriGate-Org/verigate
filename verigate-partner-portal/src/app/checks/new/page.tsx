"use client";

import { useCallback, useReducer, useState } from "react";
import { ClipboardCheck } from "lucide-react";
import { ServiceField } from "@/components/services/shared/ServiceField";
import { CheckSelector } from "@/components/services/checks/CheckSelector";
import { CheckResultCard, type CheckState } from "@/components/services/checks/CheckResultCard";
import { CHECK_DEFINITIONS } from "@/components/services/checks/checkFieldRegistry";
import { validateSaId } from "@/lib/utils/sa-id-validation";
import { executeVerification } from "@/lib/services/verification-service";

// ── Reducer for check execution state ────────────────────────────────

type State = {
  results: Record<string, CheckState>;
  isSubmitting: boolean;
};

type Action =
  | { type: "START_ALL"; checks: string[] }
  | { type: "CHECK_SUCCESS"; check: string; data: unknown }
  | { type: "CHECK_ERROR"; check: string; error: string }
  | { type: "ALL_DONE" }
  | { type: "RESET" };

function reducer(state: State, action: Action): State {
  switch (action.type) {
    case "START_ALL": {
      const results: Record<string, CheckState> = {};
      for (const c of action.checks) {
        results[c] = { status: "running" };
      }
      return { results, isSubmitting: true };
    }
    case "CHECK_SUCCESS":
      return {
        ...state,
        results: {
          ...state.results,
          [action.check]: { status: "success", data: action.data },
        },
      };
    case "CHECK_ERROR":
      return {
        ...state,
        results: {
          ...state.results,
          [action.check]: { status: "error", error: action.error },
        },
      };
    case "ALL_DONE":
      return { ...state, isSubmitting: false };
    case "RESET":
      return { results: {}, isSubmitting: false };
  }
}

// ── Page component ───────────────────────────────────────────────────

export default function NewCheckPage() {
  // Common fields
  const [idNumber, setIdNumber] = useState("");
  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");

  // Additional fields stored as a flat map keyed by `${checkType}__${fieldName}`
  const [additionalFields, setAdditionalFields] = useState<Record<string, string>>({});

  // Selected checks
  const [selectedChecks, setSelectedChecks] = useState<Set<string>>(new Set());

  // Validation errors
  const [errors, setErrors] = useState<Record<string, string>>({});

  // Reducer for results
  const [state, dispatch] = useReducer(reducer, { results: {}, isSubmitting: false });

  // ── Helpers ──────────────────────────────────────────────────────────

  const handleToggle = useCallback((checkType: string) => {
    setSelectedChecks((prev) => {
      const next = new Set(prev);
      if (next.has(checkType)) next.delete(checkType);
      else next.add(checkType);
      return next;
    });
  }, []);

  const setAdditionalField = useCallback((checkType: string, fieldName: string, value: string) => {
    setAdditionalFields((prev) => ({ ...prev, [`${checkType}__${fieldName}`]: value }));
  }, []);

  const getAdditionalField = useCallback(
    (checkType: string, fieldName: string) => additionalFields[`${checkType}__${fieldName}`] ?? "",
    [additionalFields],
  );

  // ── Validation ───────────────────────────────────────────────────────

  const validate = useCallback((): boolean => {
    const next: Record<string, string> = {};

    // Determine if any selected check needs an ID number
    const needsId = Array.from(selectedChecks).some((c) =>
      CHECK_DEFINITIONS[c]?.usesCommonFields.includes("idNumber"),
    );
    const needsFirstName = Array.from(selectedChecks).some((c) =>
      CHECK_DEFINITIONS[c]?.usesCommonFields.includes("firstName"),
    );
    const needsLastName = Array.from(selectedChecks).some((c) =>
      CHECK_DEFINITIONS[c]?.usesCommonFields.includes("lastName"),
    );

    if (needsId) {
      if (!idNumber.trim()) {
        next.idNumber = "ID number is required for the selected checks.";
      } else {
        const idResult = validateSaId(idNumber);
        if (!idResult.valid) {
          next.idNumber = idResult.errors[0] ?? "Invalid SA ID number.";
        }
      }
    }

    if (needsFirstName && !firstName.trim()) {
      next.firstName = "First name is required for the selected checks.";
    }
    if (needsLastName && !lastName.trim()) {
      next.lastName = "Last name is required for the selected checks.";
    }

    // Validate additional required fields per check
    for (const checkType of selectedChecks) {
      const def = CHECK_DEFINITIONS[checkType];
      if (!def) continue;
      for (const field of def.additionalFields) {
        if (field.required) {
          const val = getAdditionalField(checkType, field.name);
          if (!val.trim()) {
            next[`${checkType}__${field.name}`] = `${field.label} is required.`;
          } else if (field.name === "taxReferenceNumber" && val.length < 10) {
            next[`${checkType}__${field.name}`] = "Tax reference must be at least 10 digits.";
          } else if (field.name === "consent" && val !== "true") {
            next[`${checkType}__${field.name}`] = "Credit consent is required.";
          }
        }
      }
    }

    setErrors(next);
    return Object.keys(next).length === 0;
  }, [selectedChecks, idNumber, firstName, lastName, getAdditionalField]);

  // ── Submit ───────────────────────────────────────────────────────────

  const handleSubmit = useCallback(async () => {
    if (selectedChecks.size === 0) return;
    if (!validate()) return;

    const checksArray = Array.from(selectedChecks);
    dispatch({ type: "START_ALL", checks: checksArray });

    const common = { idNumber, firstName, lastName };

    const promises = checksArray.map(async (checkType) => {
      const def = CHECK_DEFINITIONS[checkType];
      if (!def) return;

      // Gather additional fields for this check
      const additional: Record<string, string> = {};
      for (const field of def.additionalFields) {
        additional[field.name] = getAdditionalField(checkType, field.name);
      }

      try {
        const payload = def.buildPayload(common, additional);
        const data = await executeVerification(def.type, payload);
        dispatch({ type: "CHECK_SUCCESS", check: checkType, data });
      } catch (err) {
        const message = err instanceof Error ? err.message : "Verification failed";
        dispatch({ type: "CHECK_ERROR", check: checkType, error: message });
      }
    });

    await Promise.allSettled(promises);
    dispatch({ type: "ALL_DONE" });
  }, [selectedChecks, validate, idNumber, firstName, lastName, getAdditionalField]);

  // ── Retry a single check ─────────────────────────────────────────────

  const handleRetry = useCallback(
    async (checkType: string) => {
      const def = CHECK_DEFINITIONS[checkType];
      if (!def) return;

      dispatch({ type: "CHECK_SUCCESS", check: checkType, data: undefined });
      // Re-set to running
      dispatch({
        type: "START_ALL",
        checks: [
          ...Object.keys(state.results).filter((k) => k !== checkType),
          checkType,
        ],
      });
      // Preserve existing results
      for (const [k, v] of Object.entries(state.results)) {
        if (k !== checkType && v.status === "success") {
          dispatch({ type: "CHECK_SUCCESS", check: k, data: v.data });
        } else if (k !== checkType && v.status === "error") {
          dispatch({ type: "CHECK_ERROR", check: k, error: v.error ?? "" });
        }
      }

      const common = { idNumber, firstName, lastName };
      const additional: Record<string, string> = {};
      for (const field of def.additionalFields) {
        additional[field.name] = getAdditionalField(checkType, field.name);
      }

      try {
        const payload = def.buildPayload(common, additional);
        const data = await executeVerification(def.type, payload);
        dispatch({ type: "CHECK_SUCCESS", check: checkType, data });
      } catch (err) {
        const message = err instanceof Error ? err.message : "Verification failed";
        dispatch({ type: "CHECK_ERROR", check: checkType, error: message });
      }
    },
    [state.results, idNumber, firstName, lastName, getAdditionalField],
  );

  // ── Derived state ────────────────────────────────────────────────────

  const selectedChecksArray = Array.from(selectedChecks);
  const hasResults = Object.keys(state.results).length > 0;

  // Checks with additional fields that are currently selected
  const checksWithAdditionalFields = selectedChecksArray.filter(
    (c) => (CHECK_DEFINITIONS[c]?.additionalFields.length ?? 0) > 0,
  );

  const canSubmit = selectedChecks.size > 0 && !state.isSubmitting;

  // ── Render ───────────────────────────────────────────────────────────

  return (
    <div className="space-y-6">
      {/* Header */}
      <header>
        <div className="flex items-center gap-3 mb-1">
          <ClipboardCheck className="h-6 w-6 text-[color:var(--color-accent-strong)]" />
          <h1 className="text-xl font-bold text-text">New check</h1>
        </div>
        <p className="text-sm text-text-muted">
          Enter subject details and select the checks to perform.
        </p>
      </header>

      {/* Two-column form */}
      <div className="grid grid-cols-1 lg:grid-cols-[1fr_320px] gap-6">
        {/* Left column: subject info + additional fields */}
        <div className="space-y-6">
          {/* Common fields card */}
          <div className="console-card">
            <div className="console-card-header">
              <div className="text-sm font-semibold text-text">Subject Information</div>
            </div>
            <div className="console-card-body space-y-4">
              <ServiceField
                label="ID number"
                description="13-digit South African ID number."
                error={errors.idNumber}
              >
                <input
                  value={idNumber}
                  onChange={(e) => {
                    const digits = e.target.value.replace(/\D/g, "").slice(0, 13);
                    setIdNumber(digits);
                  }}
                  className="aws-input w-full"
                  placeholder="e.g. 9001015009087"
                  inputMode="numeric"
                  maxLength={13}
                />
              </ServiceField>

              <ServiceField
                label="First name(s)"
                description="As it appears on their ID document."
                error={errors.firstName}
              >
                <input
                  value={firstName}
                  onChange={(e) => setFirstName(e.target.value)}
                  className="aws-input w-full"
                  placeholder="e.g. Thabo James"
                />
              </ServiceField>

              <ServiceField
                label="Last name"
                description="Surname as it appears on their ID document."
                error={errors.lastName}
              >
                <input
                  value={lastName}
                  onChange={(e) => setLastName(e.target.value)}
                  className="aws-input w-full"
                  placeholder="e.g. Mokoena"
                />
              </ServiceField>
            </div>
          </div>

          {/* Additional fields card */}
          {checksWithAdditionalFields.length > 0 && (
            <div className="console-card">
              <div className="console-card-header">
                <div className="text-sm font-semibold text-text">Additional Fields</div>
              </div>
              <div className="console-card-body space-y-5">
                {checksWithAdditionalFields.map((checkType) => {
                  const def = CHECK_DEFINITIONS[checkType];
                  if (!def) return null;
                  return (
                    <div key={checkType}>
                      <div className="text-xs font-semibold text-text-muted uppercase tracking-wide mb-3">
                        {def.label}
                      </div>
                      <div className="space-y-3">
                        {def.additionalFields.map((field) => {
                          const fieldKey = `${checkType}__${field.name}`;
                          const value = getAdditionalField(checkType, field.name);
                          return (
                            <ServiceField
                              key={fieldKey}
                              label={field.label}
                              description={field.description}
                              error={errors[fieldKey]}
                            >
                              {field.type === "select" && field.options ? (
                                <select
                                  className="aws-select w-full select-input"
                                  value={value}
                                  onChange={(e) =>
                                    setAdditionalField(checkType, field.name, e.target.value)
                                  }
                                >
                                  <option value="">Select...</option>
                                  {field.options.map((opt) => (
                                    <option key={opt.value} value={opt.value}>
                                      {opt.label}
                                    </option>
                                  ))}
                                </select>
                              ) : (
                                <input
                                  className="aws-input w-full"
                                  value={value}
                                  onChange={(e) => {
                                    let val = e.target.value;
                                    if (field.inputMode === "numeric") {
                                      val = val.replace(/\D/g, "");
                                    }
                                    if (field.maxLength) {
                                      val = val.slice(0, field.maxLength);
                                    }
                                    setAdditionalField(checkType, field.name, val);
                                  }}
                                  placeholder={field.placeholder}
                                  inputMode={field.inputMode === "numeric" ? "numeric" : "text"}
                                  maxLength={field.maxLength}
                                />
                              )}
                            </ServiceField>
                          );
                        })}
                      </div>
                    </div>
                  );
                })}
              </div>
            </div>
          )}

          {/* Submit button */}
          <button
            type="button"
            disabled={!canSubmit}
            onClick={handleSubmit}
            className="w-full sm:w-auto px-6 py-2.5 rounded text-sm font-semibold text-white bg-[color:var(--color-cta)] hover:opacity-90 disabled:opacity-50 disabled:cursor-not-allowed transition-opacity"
          >
            {state.isSubmitting
              ? "Running..."
              : `Run ${selectedChecks.size} check${selectedChecks.size !== 1 ? "s" : ""}`}
          </button>
        </div>

        {/* Right column: check selector */}
        <CheckSelector selectedChecks={selectedChecks} onToggle={handleToggle} />
      </div>

      {/* Results section */}
      {hasResults && (
        <div className="space-y-4">
          <h2 className="text-lg font-bold text-text">Results</h2>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
            {Object.keys(state.results).map((checkType) => {
              const def = CHECK_DEFINITIONS[checkType];
              if (!def) return null;
              return (
                <CheckResultCard
                  key={checkType}
                  checkType={checkType}
                  state={state.results[checkType]}
                  definition={def}
                  onRetry={() => handleRetry(checkType)}
                />
              );
            })}
          </div>
        </div>
      )}
    </div>
  );
}
