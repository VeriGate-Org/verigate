import * as React from "react";
import { cn } from "@/lib/cn";
import { ChevronDown, Check } from "lucide-react";

// Select Component
export interface SelectOption {
  value: string;
  label: string;
  disabled?: boolean;
}

export interface SelectProps extends Omit<React.SelectHTMLAttributes<HTMLSelectElement>, "children"> {
  options: SelectOption[];
  placeholder?: string;
  error?: string;
  helperText?: string;
}

export const Select = React.forwardRef<HTMLSelectElement, SelectProps>(
  ({ className, options, placeholder, error, helperText, ...props }, ref) => {
    return (
      <div className="space-y-aws-2xs">
        <div className="relative">
          <select
            className={cn(
              "aws-select w-full appearance-none pr-10",
              {
                "border-danger focus:border-danger focus:ring-danger": error,
              },
              className
            )}
            ref={ref}
            {...props}
          >
            {placeholder && (
              <option value="" disabled>
                {placeholder}
              </option>
            )}
            {options.map((option) => (
              <option key={option.value} value={option.value} disabled={option.disabled}>
                {option.label}
              </option>
            ))}
          </select>
          <ChevronDown className="absolute right-aws-m top-1/2 h-4 w-4 -translate-y-1/2 text-text-muted pointer-events-none" />
        </div>
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
Select.displayName = "Select";

// Multiselect Component (for future enhancement)
export interface MultiselectProps {
  options: SelectOption[];
  value: string[];
  onChange: (value: string[]) => void;
  placeholder?: string;
  error?: string;
  helperText?: string;
  className?: string;
}

export const Multiselect: React.FC<MultiselectProps> = ({
  options,
  value,
  onChange,
  placeholder = "Select options...",
  error,
  helperText,
  className,
}) => {
  const [isOpen, setIsOpen] = React.useState(false);
  const [searchTerm, setSearchTerm] = React.useState("");

  const filteredOptions = options.filter((option) =>
    option.label.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const toggleOption = (optionValue: string) => {
    if (value.includes(optionValue)) {
      onChange(value.filter((v) => v !== optionValue));
    } else {
      onChange([...value, optionValue]);
    }
  };

  const selectedLabels = options
    .filter((option) => value.includes(option.value))
    .map((option) => option.label);

  return (
    <div className="space-y-aws-2xs">
      <div className={cn("relative", className)}>
        <button
          type="button"
          onClick={() => setIsOpen(!isOpen)}
          className={cn(
            "aws-select w-full text-left flex items-center justify-between",
            {
              "border-danger focus:border-danger": error,
            }
          )}
        >
          <span className={selectedLabels.length === 0 ? "text-text-muted" : ""}>
            {selectedLabels.length === 0
              ? placeholder
              : selectedLabels.length === 1
              ? selectedLabels[0]
              : `${selectedLabels.length} selected`}
          </span>
          <ChevronDown className="h-4 w-4 text-text-muted" />
        </button>

        {isOpen && (
          <div className="absolute z-50 mt-1 w-full bg-surface border border-border rounded-aws-control shadow-aws-raised">
            <div className="p-aws-s border-b border-border">
              <input
                type="text"
                placeholder="Search..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="aws-input w-full"
              />
            </div>
            <div className="max-h-60 overflow-auto">
              {filteredOptions.length === 0 ? (
                <div className="p-aws-m text-center text-text-muted">No options found</div>
              ) : (
                filteredOptions.map((option) => (
                  <button
                    key={option.value}
                    type="button"
                    onClick={() => toggleOption(option.value)}
                    disabled={option.disabled}
                    className={cn(
                      "w-full text-left px-aws-m py-aws-s hover:bg-hover flex items-center gap-aws-s",
                      {
                        "opacity-50 cursor-not-allowed": option.disabled,
                      }
                    )}
                  >
                    <div className="flex-shrink-0 w-4 h-4 border border-border rounded-aws-control flex items-center justify-center">
                      {value.includes(option.value) && (
                        <Check className="h-3 w-3 text-accent" />
                      )}
                    </div>
                    <span>{option.label}</span>
                  </button>
                ))
              )}
            </div>
          </div>
        )}
      </div>
      {error && (
        <p className="text-aws-body text-danger">{error}</p>
      )}
      {helperText && !error && (
        <p className="text-aws-body text-text-muted">{helperText}</p>
      )}
    </div>
  );
};