"use client";

import { useCallback, useMemo, useState } from "react";
import type { FormEvent, ReactNode } from "react";
import JsonViewer from "@/components/code/JsonViewer";
import { ProcessingDialog } from "@/components/ui/ProcessingDialog";
import { Button } from "@/components/ui/Button";
import {
  type PropertyOwnershipResponse as PropertyResponse,
} from "@/lib/mock-services";
import { executeVerification } from "@/lib/services/verification-service";
import { FileDown } from "lucide-react";

const PROVINCES = [
  "Gauteng",
  "Western Cape",
  "KwaZulu-Natal",
  "Eastern Cape",
  "Mpumalanga",
  "Limpopo",
  "Free State",
  "North West",
  "Northern Cape",
] as const;

export default function PropertyOwnership() {
  const [searchType, setSearchType] = useState<string>("ownerName");
  const [query, setQuery] = useState<string>("");
  const [province, setProvince] = useState<string>(PROVINCES[0]);
  const [result, setResult] = useState<PropertyResponse | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const handleExport = useCallback(() => {
    if (typeof window !== "undefined") {
      window.print();
    }
  }, []);

  const canSubmit = useMemo(() => query.trim().length > 2, [query]);

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!canSubmit) return;
    setLoading(true);
    setError(null);

    try {
      const data = await executeVerification("PROPERTY_OWNERSHIP_VERIFICATION", { searchType, query, province }) as PropertyResponse;
      setResult(data);
    } catch (err) {
      const message = err instanceof Error ? err.message : "Search failed";
      setError(message);
      setResult(null);
    } finally {
      setLoading(false);
    }
  };

  const submitDisabled = loading || !canSubmit;

  return (
    <div className="space-y-6">
      <header className="space-y-1">
        <h1 className="text-xl font-semibold text-text">Deeds registry search</h1>
        <p className="text-sm text-text-muted">Search ownership, bonds, and municipal status from the national deeds registry.</p>
      </header>

      <div className="grid gap-4 lg:grid-cols-[minmax(0,480px)_minmax(0,1fr)]">
        <div className="console-card">
          <div className="console-card-header">
            <div>
              <div className="text-sm font-semibold text-text">Search criteria</div>
              <div className="text-xs text-text-muted">Owner, ID number, or erf/title information.</div>
            </div>
          </div>

          <form className="console-card-body space-y-4" onSubmit={handleSubmit}>
            <Field label="Search type">
              <select
                className="aws-select w-full select-input"
                value={searchType}
                onChange={(event) => setSearchType(event.target.value)}
              >
                <option value="ownerName">Owner name</option>
                <option value="ownerId">Owner ID number</option>
                <option value="erf">ERF / title details</option>
              </select>
            </Field>

            <Field label="Query" description="Minimum three characters.">
              <input
                value={query}
                onChange={(event) => setQuery(event.target.value)}
                className="aws-input w-full"
                placeholder={searchType === "ownerId" ? "ID number" : "e.g. John Doe"}
              />
            </Field>

            <Field label="Province">
              <select
                className="aws-select w-full select-input"
                value={province}
                onChange={(event) => setProvince(event.target.value)}
              >
                {PROVINCES.map((item) => (
                  <option key={item} value={item}>
                    {item}
                  </option>
                ))}
              </select>
            </Field>

            <div className="flex items-center justify-between gap-3 pt-2">
              <p className="text-xs text-text-muted">Results show active bonds and municipal arrears flags.</p>
              <Button 
                type="submit" 
                variant="cta" 
                size="md"
                disabled={submitDisabled}
                loading={loading}
              >
                Search
              </Button>
            </div>

            {error && <div className="rounded border border-danger/40 bg-danger/5 px-3 py-2 text-xs text-danger">{error}</div>}
          </form>
        </div>

        <div className="console-card">
          <div className="console-card-header">
            <div className="text-sm font-semibold text-text">Response fields</div>
          </div>
          <div className="console-card-body space-y-2 text-xs text-text-muted">
            <SchemaRow name="summary.totalProperties" type="Integer" />
            <SchemaRow name="items[].currentBonds" type="Array<Bond>" />
            <SchemaRow name="items[].municipal" type="Object" />
            <SchemaRow name="items[].registrationDate" type="ISO date" />
          </div>
        </div>
      </div>

      {result && (
        <div className="space-y-4">
          <div className="console-card">
            <div className="console-card-header">
              <div>
                <div className="text-sm font-semibold text-text">Summary</div>
                <div className="text-xs text-text-muted">Criteria {result.criteria.query} • {result.criteria.province}</div>
              </div>
              <Button 
                variant="secondary" 
                size="sm" 
                onClick={handleExport} 
                icon={<FileDown className="h-4 w-4" />}
              >
                Export PDF
              </Button>
            </div>
            <div className="console-card-body grid gap-4 sm:grid-cols-3">
              <SummaryCard label="Properties" value={result.summary.totalProperties} />
              <SummaryCard label="Active bonds" value={result.summary.totalActiveBonds} />
              <SummaryCard label="Municipal flags" value={result.summary.totalMunicipalFlags} />
            </div>
          </div>

          <div className="console-card">
            <div className="console-card-header">
              <div className="text-sm font-semibold text-text">Properties</div>
            </div>
            <div className="console-card-body p-0">
              <div className="overflow-x-auto">
                <table className="min-w-full text-left text-sm">
                  <thead className="bg-background text-xs uppercase tracking-wide text-text-muted">
                    <tr>
                      <th className="px-3 py-2">Property</th>
                      <th className="px-3 py-2">Owner</th>
                      <th className="px-3 py-2">Title deed</th>
                      <th className="px-3 py-2">Registration date</th>
                      <th className="px-3 py-2">Bonds</th>
                      <th className="px-3 py-2">Municipal</th>
                    </tr>
                  </thead>
                  <tbody>
                    {result.items.map((item) => (
                      <tr key={item.propertyId} className="border-b border-border last:border-0">
                        <td className="px-3 py-2">
                          <div className="font-medium text-text">ERF {item.erfNumber}/{item.portion}</div>
                          <div className="text-xs text-text-muted">{item.township}, {item.province}</div>
                        </td>
                        <td className="px-3 py-2">
                          <div className="text-text">{item.ownerName}</div>
                          <div className="text-xs text-text-muted">{item.ownerIdNumber}</div>
                        </td>
                        <td className="px-3 py-2 text-text">{item.titleDeed}</td>
                        <td className="px-3 py-2 text-text-muted">{formatDate(item.registrationDate)}</td>
                        <td className="px-3 py-2">
                          {item.currentBonds.length ? (
                            <ul className="space-y-1 text-xs text-text">
                              {item.currentBonds.map((bond, index) => (
                                <li key={`${item.propertyId}-bond-${index}`}>
                                  {bond.bondholder} • ZAR {bond.amount.toLocaleString()}
                                </li>
                              ))}
                            </ul>
                          ) : (
                            <span className="text-xs text-text-muted">None</span>
                          )}
                        </td>
                        <td className="px-3 py-2 text-xs text-text">
                          <div>Acct: {item.municipal.accountNumber}</div>
                          <div>
                            Arrears: {item.municipal.arrears ? `ZAR ${item.municipal.arrears.toLocaleString()}` : "None"}
                          </div>
                          {item.municipal.ratesFlag && <div className="text-danger">Rates flag</div>}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          </div>

          <div className="console-card">
            <div className="console-card-header">
              <div className="text-sm font-semibold text-text">Raw response</div>
            </div>
            <div className="console-card-body bg-background">
              <JsonViewer data={result} />
            </div>
          </div>
        </div>
      )}
      <ProcessingDialog open={loading} title="Searching deeds" message="Generating mock property ownership records..." />
    </div>
  );
}

function formatDate(iso: string) {
  if (!iso) return "—";
  const date = new Date(iso);
  return Number.isNaN(date.getTime()) ? "—" : date.toLocaleDateString();
}

function SummaryCard({ label, value }: { label: string; value: number }) {
  return (
    <div className="space-y-1 rounded border border-border bg-background p-3">
      <div className="text-xs uppercase tracking-wide text-text-muted">{label}</div>
      <div className="text-sm font-medium text-text">{value.toLocaleString()}</div>
    </div>
  );
}

interface SchemaRowProps {
  name: string;
  type: string;
}

function SchemaRow({ name, type }: SchemaRowProps) {
  return (
    <div className="flex items-center justify-between rounded border border-transparent px-2 py-1 hover:border-border">
      <span className="font-medium text-text">{name}</span>
      <span>{type}</span>
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
      {description && <span className="block text-xs text-text-muted">{description}</span>}
      {children}
    </label>
  );
}
