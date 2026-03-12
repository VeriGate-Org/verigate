import type { ReactNode } from "react";

export interface ServiceFieldProps {
  label: string;
  description?: string;
  error?: string | null;
  children: ReactNode;
}

export function ServiceField({ label, description, error, children }: ServiceFieldProps) {
  return (
    <label className="block space-y-1 text-sm">
      <span className="font-medium text-text">{label}</span>
      {description && <span className="block text-xs text-text-muted">{description}</span>}
      {children}
      {error && (
        <span className="block text-xs text-danger mt-1" role="alert">{error}</span>
      )}
    </label>
  );
}
