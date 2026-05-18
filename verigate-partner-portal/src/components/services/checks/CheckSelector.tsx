"use client";

import { CHECK_CATEGORIES, CHECK_DEFINITIONS } from "./checkFieldRegistry";
import { SlaIndicator } from "@/components/services/shared/SlaIndicator";

interface CheckSelectorProps {
  selectedChecks: Set<string>;
  onToggle: (checkType: string) => void;
}

export function CheckSelector({ selectedChecks, onToggle }: CheckSelectorProps) {
  const allCheckTypes = CHECK_CATEGORIES.flatMap((cat) => cat.checks);
  const allSelected = allCheckTypes.every((c) => selectedChecks.has(c));

  const handleSelectAll = () => {
    for (const type of allCheckTypes) {
      if (!selectedChecks.has(type)) onToggle(type);
    }
  };

  const handleClearAll = () => {
    for (const type of allCheckTypes) {
      if (selectedChecks.has(type)) onToggle(type);
    }
  };

  return (
    <div className="console-card lg:sticky lg:top-20 self-start">
      <div className="console-card-header">
        <div className="text-sm font-semibold text-text">Checks Required</div>
        <div className="flex items-center gap-2 text-xs">
          <button
            type="button"
            onClick={allSelected ? handleClearAll : handleSelectAll}
            className="text-[color:var(--color-accent-strong)] hover:underline font-medium"
          >
            {allSelected ? "Clear all" : "Select all"}
          </button>
        </div>
      </div>

      <div className="console-card-body space-y-5">
        {CHECK_CATEGORIES.map((category) => (
          <div key={category.label}>
            <div className="text-[11px] text-text-muted uppercase tracking-[0.12em] font-semibold mb-2">
              {category.label}
            </div>
            <div className="space-y-1">
              {category.checks.map((checkType) => {
                const def = CHECK_DEFINITIONS[checkType];
                if (!def) return null;
                const checked = selectedChecks.has(checkType);
                return (
                  <label
                    key={checkType}
                    className="flex items-center gap-2.5 px-2 py-1.5 rounded cursor-pointer hover:bg-hover transition-colors"
                  >
                    <input
                      type="checkbox"
                      checked={checked}
                      onChange={() => onToggle(checkType)}
                      className="h-3.5 w-3.5 rounded border-border text-accent accent-[color:var(--color-accent)] focus:ring-accent"
                    />
                    <span className="flex-1 text-[13px] text-text">{def.label}</span>
                    <SlaIndicator
                      sla={def.realTime ? "Real-time" : "Async"}
                      realTime={def.realTime}
                    />
                  </label>
                );
              })}
            </div>
          </div>
        ))}

        <div className="pt-3 border-t border-border text-xs text-text-muted text-center">
          {selectedChecks.size} check{selectedChecks.size !== 1 ? "s" : ""} selected
        </div>
      </div>
    </div>
  );
}
