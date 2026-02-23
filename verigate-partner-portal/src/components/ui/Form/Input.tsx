import * as React from "react";
import { cn } from "@/lib/cn";

// Input Component
export interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  error?: string;
  helperText?: string;
}

export const Input = React.forwardRef<HTMLInputElement, InputProps>(
  ({ className, type, error, helperText, ...props }, ref) => {
    return (
      <div className="space-y-aws-2xs">
        <input
          type={type}
          className={cn(
            "aws-input w-full",
            {
              "border-danger focus:border-danger focus:ring-danger": error,
            },
            className
          )}
          ref={ref}
          {...props}
        />
        {error && (
          <p className="text-aws-body text-danger">{error}</p>
        )}
        {helperText && !error && (
          <p className="text-aws-body text-text-muted">{helperText}</p>
        )}
      </div>
    );
  }
);
Input.displayName = "Input";

// Textarea Component
export interface TextareaProps extends React.TextareaHTMLAttributes<HTMLTextAreaElement> {
  error?: string;
  helperText?: string;
}

export const Textarea = React.forwardRef<HTMLTextAreaElement, TextareaProps>(
  ({ className, error, helperText, ...props }, ref) => {
    return (
      <div className="space-y-aws-2xs">
        <textarea
          className={cn(
            "aws-textarea w-full min-h-[80px] resize-vertical",
            {
              "border-danger focus:border-danger focus:ring-danger": error,
            },
            className
          )}
          ref={ref}
          {...props}
        />
        {error && (
          <p className="text-aws-body text-danger">{error}</p>
        )}
        {helperText && !error && (
          <p className="text-aws-body text-text-muted">{helperText}</p>
        )}
      </div>
    );
  }
);
Textarea.displayName = "Textarea";