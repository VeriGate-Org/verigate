import { forwardRef, type InputHTMLAttributes } from "react";
import { cn } from "@/lib/cn";

export interface InputProps extends InputHTMLAttributes<HTMLInputElement> {
  error?: boolean;
  label?: string;
  hint?: string;
  errorMessage?: string;
}

const Input = forwardRef<HTMLInputElement, InputProps>(
  ({ className, error, label, hint, errorMessage, id, ...props }, ref) => {
    const inputId = id || label?.toLowerCase().replace(/\s+/g, "-");
    return (
      <div className="flex flex-col gap-1">
        {label && (
          <label
            htmlFor={inputId}
            className="text-xs font-semibold text-text-muted uppercase tracking-wide"
          >
            {label}
          </label>
        )}
        <input
          ref={ref}
          id={inputId}
          className={cn(
            "aws-input w-full",
            error && "!border-[var(--color-alert)] !shadow-none",
            className,
          )}
          aria-invalid={error || undefined}
          {...props}
        />
        {hint && !error && (
          <span className="text-[11px] text-text-muted">{hint}</span>
        )}
        {error && errorMessage && (
          <span className="text-[11px] text-[var(--color-alert)]">
            {errorMessage}
          </span>
        )}
      </div>
    );
  },
);
Input.displayName = "Input";

export { Input };
