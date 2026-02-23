import * as React from "react";
import { cn } from "@/lib/cn";

// Form Field wrapper component
export interface FormFieldProps {
  label?: string;
  required?: boolean;
  error?: string;
  helperText?: string;
  children: React.ReactNode;
  className?: string;
  id?: string;
}

export const FormField: React.FC<FormFieldProps> = ({
  label,
  required,
  error,
  helperText,
  children,
  className,
  id,
}) => {
  const fieldId = React.useId();
  const finalId = id || fieldId;

  return (
    <div className={cn("space-y-aws-xs", className)}>
      {label && (
        <label htmlFor={finalId} className="block text-aws-body font-medium text-text">
          {label}
          {required && <span className="text-danger ml-1">*</span>}
        </label>
      )}
      <div>
        {React.isValidElement(children)
          ? React.cloneElement(children as React.ReactElement<{
              id?: string;
              error?: string;
              helperText?: string;
            }>, {
              id: finalId,
              error,
              helperText: !error ? helperText : undefined,
            })
          : children}
      </div>
    </div>
  );
};

// Form Section for grouping related fields
export interface FormSectionProps {
  title?: string;
  description?: string;
  children: React.ReactNode;
  className?: string;
}

export const FormSection: React.FC<FormSectionProps> = ({
  title,
  description,
  children,
  className,
}) => {
  return (
    <div className={cn("space-y-aws-l", className)}>
      {(title || description) && (
        <div className="border-b border-border pb-aws-m">
          {title && (
            <h3 className="text-aws-heading-s font-medium text-text">{title}</h3>
          )}
          {description && (
            <p className="text-aws-body text-text-muted mt-aws-2xs">{description}</p>
          )}
        </div>
      )}
      <div className="space-y-aws-l">{children}</div>
    </div>
  );
};

// Form Actions wrapper
export interface FormActionsProps {
  children: React.ReactNode;
  align?: "left" | "right" | "center";
  className?: string;
}

export const FormActions: React.FC<FormActionsProps> = ({
  children,
  align = "right",
  className,
}) => {
  return (
    <div
      className={cn(
        "flex gap-aws-m pt-aws-l border-t border-border",
        {
          "justify-start": align === "left",
          "justify-center": align === "center",
          "justify-end": align === "right",
        },
        className
      )}
    >
      {children}
    </div>
  );
};