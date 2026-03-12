import { CheckCircle2, XCircle } from "lucide-react";

interface FieldMatchIndicatorProps {
  matched: boolean;
  label?: string;
}

export function FieldMatchIndicator({ matched, label }: FieldMatchIndicatorProps) {
  return (
    <span className="inline-flex items-center gap-1.5 text-sm">
      {matched ? (
        <>
          <CheckCircle2 className="h-4 w-4 text-success" aria-hidden="true" />
          <span className="text-success">{label ?? "Matched"}</span>
        </>
      ) : (
        <>
          <XCircle className="h-4 w-4 text-danger" aria-hidden="true" />
          <span className="text-danger">{label ?? "Not matched"}</span>
        </>
      )}
    </span>
  );
}
