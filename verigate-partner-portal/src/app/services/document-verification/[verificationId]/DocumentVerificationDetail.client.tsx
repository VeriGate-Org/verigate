"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { useParams } from "next/navigation";
import {
  ArrowLeft,
  Download,
  FileText,
  Loader2,
  AlertCircle,
  CheckCircle2,
  XCircle,
  AlertTriangle,
} from "lucide-react";
import { Badge } from "@/components/ui/Badge";
import { DOCUMENT_TYPE_LABELS } from "@/components/services/document-verification/documentFieldConfigs";
import {
  getVerificationStatus,
  getVerificationDocuments,
  type BffVerificationStatusResponse,
  type DocumentLink,
} from "@/lib/bff-client";

export default function DocumentVerificationDetail() {
  const { verificationId } = useParams<{ verificationId: string }>();

  const [verification, setVerification] = useState<BffVerificationStatusResponse | null>(null);
  const [documents, setDocuments] = useState<DocumentLink[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchData = () => {
    setIsLoading(true);
    setError(null);

    Promise.all([
      getVerificationStatus(verificationId),
      getVerificationDocuments(verificationId).catch(() => [] as DocumentLink[]),
    ])
      .then(([status, docs]) => {
        setVerification(status);
        setDocuments(docs);
      })
      .catch((err) => {
        setError(err.message ?? "Failed to load verification details");
      })
      .finally(() => setIsLoading(false));
  };

  useEffect(() => {
    fetchData();
  }, [verificationId]);

  if (isLoading) {
    return (
      <div className="space-y-6">
        <BackLink />
        <div className="flex items-center justify-center py-16">
          <Loader2 className="w-6 h-6 animate-spin text-blue-600" />
          <span className="ml-2 text-sm text-gray-600">Loading verification details...</span>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="space-y-6">
        <BackLink />
        <div className="rounded-lg border border-red-200 bg-red-50 p-4 flex items-start gap-3">
          <AlertCircle className="w-5 h-5 text-red-600 mt-0.5 flex-shrink-0" />
          <div className="flex-1">
            <p className="text-sm font-medium text-red-800">
              Failed to load verification details
            </p>
            <p className="text-sm text-red-700 mt-1">{error}</p>
            <button
              onClick={fetchData}
              className="mt-3 px-3 py-1.5 text-sm bg-red-100 hover:bg-red-200 text-red-800 rounded-md border border-red-300"
            >
              Retry
            </button>
          </div>
        </div>
      </div>
    );
  }

  if (!verification) {
    return (
      <div className="space-y-6">
        <BackLink />
        <div className="rounded-lg border border-gray-200 bg-white p-8 text-center">
          <p className="text-sm text-gray-500">Verification not found</p>
          <p className="text-xs text-gray-400 mt-1">
            No verification with ID{" "}
            <span className="font-mono font-medium text-gray-600">{verificationId}</span> was
            found.
          </p>
        </div>
      </div>
    );
  }

  const aux = verification.auxiliaryData ?? {};
  const documentType = aux.documentType ?? "unknown";
  const documentNumber = aux.documentNumber ?? "";
  const outcome = aux.outcome ?? verification.status;
  const confidenceScore = aux.confidenceScore;
  const matchDetails = aux.matchDetails;
  const extractedFields = aux.extractedFields;
  const documentTypeLabel = DOCUMENT_TYPE_LABELS[documentType] ?? documentType;

  return (
    <div className="space-y-6">
      <BackLink />

      {/* Header */}
      <div className="flex flex-col gap-1">
        <div className="flex items-center gap-3">
          <h1 className="text-xl font-semibold text-gray-900">Document Verification</h1>
          <OutcomeBadge outcome={outcome} />
        </div>
        <p className="text-sm text-gray-500 font-mono">{verificationId}</p>
      </div>

      {/* Details grid */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-4">
        {/* Left: Core details */}
        <div className="lg:col-span-2 rounded-lg border border-gray-200 bg-white">
          <div className="px-4 py-3 border-b border-gray-200">
            <h2 className="text-sm font-semibold text-gray-900">Details</h2>
          </div>
          <div className="px-4 py-4">
            <dl className="grid grid-cols-1 sm:grid-cols-2 gap-x-6 gap-y-4 text-sm">
              <DetailRow label="Verification ID" value={verificationId} mono />
              <DetailRow label="Status" value={verification.status} />
              <DetailRow label="Document Type" value={documentTypeLabel} />
              <DetailRow label="Document Number" value={documentNumber || "\u2014"} mono />
              <DetailRow label="Outcome" value={<OutcomeBadge outcome={outcome} />} />
              {confidenceScore && (
                <DetailRow
                  label="Confidence Score"
                  value={`${Math.round(parseFloat(confidenceScore) * 100)}%`}
                />
              )}
              {matchDetails && <DetailRow label="Match Details" value={matchDetails} />}
              {extractedFields && (
                <DetailRow label="Extracted Fields" value={extractedFields} />
              )}
            </dl>
          </div>
        </div>

        {/* Right: Status card */}
        <div className="rounded-lg border border-gray-200 bg-white">
          <div className="px-4 py-3 border-b border-gray-200">
            <h2 className="text-sm font-semibold text-gray-900">Verification Status</h2>
          </div>
          <div className="px-4 py-4 space-y-4">
            <div className="text-center">
              <OutcomeIcon outcome={outcome} />
              <div className="text-sm font-medium text-gray-900 mt-2">
                {outcomeLabel(outcome)}
              </div>
            </div>
            {confidenceScore && (
              <div className="flex items-center justify-between text-sm">
                <span className="text-gray-500">Confidence</span>
                <span className="font-medium text-gray-900">
                  {Math.round(parseFloat(confidenceScore) * 100)}%
                </span>
              </div>
            )}
            {verification.errorDetails && verification.errorDetails.length > 0 && (
              <div className="space-y-1">
                <span className="text-xs text-gray-500">Errors</span>
                {verification.errorDetails.map((err, i) => (
                  <p key={i} className="text-xs text-red-700 bg-red-50 rounded px-2 py-1">
                    {err}
                  </p>
                ))}
              </div>
            )}
          </div>
        </div>
      </div>

      {/* Documents / Downloads */}
      <div className="rounded-lg border border-gray-200 bg-white">
        <div className="px-4 py-3 border-b border-gray-200 flex items-center justify-between">
          <h2 className="text-sm font-semibold text-gray-900">Documents</h2>
          <span className="text-xs text-gray-500">{documents.length} file(s)</span>
        </div>
        <div className="px-4 py-4">
          {documents.length === 0 ? (
            <p className="text-sm text-gray-500 text-center py-4">
              No downloadable documents available for this verification.
            </p>
          ) : (
            <ul className="divide-y divide-gray-100">
              {documents.map((doc, i) => (
                <li key={i} className="flex items-center justify-between py-3">
                  <div className="flex items-center gap-3 min-w-0">
                    <FileText className="w-5 h-5 text-gray-400 flex-shrink-0" />
                    <span className="text-sm text-gray-700 truncate font-mono">
                      {doc.s3Key.split("/").pop() ?? doc.s3Key}
                    </span>
                  </div>
                  <a
                    href={doc.downloadUrl}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="inline-flex items-center gap-1.5 px-3 py-1.5 text-sm bg-blue-50 hover:bg-blue-100 text-blue-700 rounded-md border border-blue-200"
                  >
                    <Download className="w-4 h-4" />
                    Download
                  </a>
                </li>
              ))}
            </ul>
          )}
        </div>
      </div>
    </div>
  );
}

// -- Sub-components & helpers --

function BackLink() {
  return (
    <Link
      href="/services/document-verification"
      className="inline-flex items-center gap-1.5 text-sm text-gray-500 hover:text-gray-900 transition-colors"
    >
      <ArrowLeft className="h-4 w-4" />
      Back to document verification
    </Link>
  );
}

function DetailRow({
  label,
  value,
  mono,
}: {
  label: string;
  value: React.ReactNode;
  mono?: boolean;
}) {
  return (
    <div>
      <dt className="text-xs text-gray-500">{label}</dt>
      <dd className={`mt-0.5 text-gray-900 ${mono ? "font-mono" : ""}`}>{value}</dd>
    </div>
  );
}

function OutcomeBadge({ outcome }: { outcome: string }) {
  switch (outcome) {
    case "SUCCEEDED":
    case "VERIFIED":
      return (
        <Badge variant="success" size="sm">
          Verified
        </Badge>
      );
    case "SOFT_FAIL":
    case "NOT_VERIFIED":
      return (
        <Badge variant="warning" size="sm">
          Not Verified
        </Badge>
      );
    case "HARD_FAIL":
    case "FAILED":
      return (
        <Badge variant="danger" size="sm">
          Failed
        </Badge>
      );
    case "SYSTEM_OUTAGE":
      return (
        <Badge variant="danger" size="sm">
          System Outage
        </Badge>
      );
    default:
      return (
        <Badge variant="neutral" size="sm">
          {outcome}
        </Badge>
      );
  }
}

function OutcomeIcon({ outcome }: { outcome: string }) {
  const cls = "w-10 h-10 mx-auto";
  switch (outcome) {
    case "SUCCEEDED":
    case "VERIFIED":
      return <CheckCircle2 className={`${cls} text-green-500`} />;
    case "SOFT_FAIL":
    case "NOT_VERIFIED":
      return <AlertTriangle className={`${cls} text-amber-500`} />;
    case "HARD_FAIL":
    case "FAILED":
    case "SYSTEM_OUTAGE":
      return <XCircle className={`${cls} text-red-500`} />;
    default:
      return <FileText className={`${cls} text-gray-400`} />;
  }
}

function outcomeLabel(outcome: string): string {
  switch (outcome) {
    case "SUCCEEDED":
    case "VERIFIED":
      return "Document Verified";
    case "SOFT_FAIL":
    case "NOT_VERIFIED":
      return "Not Verified";
    case "HARD_FAIL":
    case "FAILED":
      return "Verification Failed";
    case "SYSTEM_OUTAGE":
      return "System Outage";
    default:
      return outcome;
  }
}
