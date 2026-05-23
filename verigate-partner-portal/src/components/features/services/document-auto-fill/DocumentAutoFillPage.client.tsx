"use client";

import { useState, useCallback } from "react";
import { PageHeader } from "@/components/ui/PageHeader";
import { Card, CardHeader, CardBody } from "@/components/ui/Card";
import { Button } from "@/components/ui/Button";
import { cn } from "@/lib/cn";
import { FileSearch, CheckCircle } from "lucide-react";

/* ------------------------------------------------------------------ */
/*  Types & data                                                       */
/* ------------------------------------------------------------------ */

interface ExtractedField {
  key: string;
  label: string;
  value: string;
  mono?: boolean;
}

const EXTRACTED_FIELDS: ExtractedField[] = [
  { key: "firstName", label: "First name", value: "Lerato" },
  { key: "lastName", label: "Last name", value: "Mokoena" },
  { key: "idNumber", label: "ID number", value: "9001015800087", mono: true },
  { key: "dateOfBirth", label: "Date of birth", value: "1990-01-01", mono: true },
  { key: "gender", label: "Gender", value: "Female" },
  { key: "nationality", label: "Nationality", value: "South African" },
  { key: "address", label: "Address", value: "14 Cinnebar Street, Table View" },
  { key: "postalCode", label: "Postal code", value: "7441", mono: true },
];

/* ------------------------------------------------------------------ */
/*  Main component                                                     */
/* ------------------------------------------------------------------ */

export function DocumentAutoFillPage() {
  const [dropped, setDropped] = useState(false);
  const [fields, setFields] = useState<ExtractedField[]>([]);

  const handleDrop = useCallback((e: React.DragEvent) => {
    e.preventDefault();
    setDropped(true);
    setFields(EXTRACTED_FIELDS);
  }, []);

  const handleFileChange = useCallback(() => {
    setDropped(true);
    setFields(EXTRACTED_FIELDS);
  }, []);

  const handleFieldChange = useCallback((key: string, value: string) => {
    setFields((prev) =>
      prev.map((f) => (f.key === key ? { ...f, value } : f)),
    );
  }, []);

  return (
    <div className="space-y-3.5">
      <PageHeader
        category="Identity \u00B7 Auto-fill"
        title="Document Auto-Fill"
        description="Drop a document, get the form populated. Saves operators ~3 minutes per onboarding."
      />

      <div className="grid grid-cols-2 gap-3.5 max-lg:grid-cols-1">
        {/* Drop zone */}
        <label
          onDragOver={(e) => e.preventDefault()}
          onDrop={handleDrop}
          className={cn(
            "console-card border-2 border-dashed rounded-aws-container py-[60px] px-6 text-center cursor-pointer transition-all duration-200",
            dropped
              ? "bg-[rgba(44,151,75,0.04)] border-[#2C974B]"
              : "bg-surface border-[#CBD5E1]",
          )}
        >
          <input
            type="file"
            accept="image/*,application/pdf"
            className="hidden"
            onChange={handleFileChange}
          />
          {dropped ? (
            <>
              <div className="w-14 h-14 rounded-full bg-[#2C974B] inline-flex items-center justify-center mb-3">
                <CheckCircle size={28} className="text-white" strokeWidth={2.5} />
              </div>
              <div className="text-sm font-semibold">sa_id_lerato.jpg uploaded</div>
              <div className="text-xs text-text-muted mt-1">
                8 fields extracted. Edit on the right, then push to any service form.
              </div>
            </>
          ) : (
            <>
              <div className="w-14 h-14 rounded-xl bg-accent-soft inline-flex items-center justify-center mb-3">
                <FileSearch size={26} className="text-accent" />
              </div>
              <div className="text-sm font-semibold">Drop any SA identity document</div>
              <div className="text-xs text-text-muted mt-1.5 max-w-[360px] mx-auto">
                SA ID, passport, driver&apos;s license &mdash; fields are extracted with OCR and ready to push into any onboarding form.
              </div>
            </>
          )}
        </label>

        {/* Extracted fields panel */}
        <Card>
          <CardHeader>
            <div className="flex items-center justify-between w-full">
              <div>
                <div className="text-sm font-semibold">Extracted fields</div>
                <div className="text-[11px] text-text-muted mt-0.5">
                  {dropped ? `${fields.length} fields \u00B7 ready to push` : "Drop a document to begin"}
                </div>
              </div>
              {dropped && (
                <Button variant="cta" size="sm">
                  Push to form &rarr;
                </Button>
              )}
            </div>
          </CardHeader>
          {dropped ? (
            <div className="py-2.5">
              {fields.map((f, i) => (
                <div
                  key={f.key}
                  className={cn(
                    "px-[18px] py-2",
                    i > 0 && "border-t border-[#f1f5f9]",
                  )}
                >
                  <label className="text-[10px] text-text-muted uppercase tracking-wide font-semibold">
                    {f.label}
                  </label>
                  <input
                    value={f.value}
                    onChange={(e) => handleFieldChange(f.key, e.target.value)}
                    className={cn(
                      "w-full py-1 border-none border-b border-b-transparent text-[13px] text-text outline-none mt-0.5 bg-transparent focus:border-b-accent",
                      f.mono && "font-mono",
                    )}
                  />
                </div>
              ))}
            </div>
          ) : (
            <CardBody>
              <div className="py-12 text-center text-xs text-text-muted">
                Fields appear here once you upload.
              </div>
            </CardBody>
          )}
        </Card>
      </div>
    </div>
  );
}
