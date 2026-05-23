"use client";

import { cn } from "@/lib/cn";

export interface FilterChip {
  label: string;
  value: string;
  count?: number;
}

export interface FilterChipsProps {
  chips: FilterChip[];
  value: string;
  onChange: (value: string) => void;
  className?: string;
}

export function FilterChips({
  chips,
  value,
  onChange,
  className,
}: FilterChipsProps) {
  return (
    <div className={cn("flex items-center gap-1 flex-wrap", className)}>
      {chips.map((chip) => {
        const isActive = chip.value === value;
        return (
          <button
            key={chip.value}
            onClick={() => onChange(chip.value)}
            className={cn(
              "inline-flex items-center gap-1.5 px-3 py-1.5 rounded-aws-token text-xs font-medium transition-all",
              "border",
              isActive
                ? "bg-accent-soft border-accent text-accent font-semibold"
                : "bg-surface border-border text-text-muted hover:border-accent hover:text-accent",
            )}
          >
            {chip.label}
            {chip.count !== undefined && (
              <span
                className={cn(
                  "text-[10px] px-1.5 py-0.5 rounded-full font-semibold",
                  isActive
                    ? "bg-accent text-white"
                    : "bg-[#F2F3F3] text-text-muted",
                )}
              >
                {chip.count}
              </span>
            )}
          </button>
        );
      })}
    </div>
  );
}
