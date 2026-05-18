"use client";

import { useCallback, useState } from "react";
import { useTheme } from "@/components/theme/ThemeProvider";

type ThemeOption = "light" | "dark" | "system";

export default function AppearanceTab() {
  const { theme, setTheme } = useTheme();
  const [selected, setSelected] = useState<ThemeOption>(() => {
    if (typeof window !== "undefined") {
      const stored = window.localStorage.getItem("verigate-theme");
      if (stored === "light" || stored === "dark") return stored;
    }
    return "system";
  });

  const handleChange = useCallback(
    (option: ThemeOption) => {
      setSelected(option);
      if (option === "system") {
        const prefersDark = window.matchMedia("(prefers-color-scheme: dark)").matches;
        setTheme(prefersDark ? "dark" : "light");
        window.localStorage.removeItem("verigate-theme");
      } else {
        setTheme(option);
      }
    },
    [setTheme],
  );

  const THEME_OPTIONS: { value: ThemeOption; label: string; description: string }[] = [
    { value: "light", label: "Light", description: "Classic light interface with white backgrounds." },
    { value: "dark", label: "Dark", description: "Reduced-brightness interface for low-light environments." },
    { value: "system", label: "System", description: "Automatically match your operating system preference." },
  ];

  return (
    <div className="console-card">
      <div className="console-card-header">
        <div>
          <div className="text-sm font-semibold text-text">Theme</div>
          <div className="text-xs text-text-muted">Select a colour scheme for the partner portal.</div>
        </div>
      </div>
      <div className="console-card-body">
        <div className="grid gap-3 sm:grid-cols-3">
          {THEME_OPTIONS.map((opt) => {
            const isActive = selected === opt.value;
            return (
              <button
                key={opt.value}
                onClick={() => handleChange(opt.value)}
                className={`flex flex-col items-start gap-2 rounded-lg border p-4 text-left transition-all ${
                  isActive
                    ? "border-accent bg-accent-soft shadow-sm"
                    : "border-border bg-[color:var(--color-base-100)] hover:border-accent/50"
                }`}
              >
                <div
                  className={`h-10 w-full rounded border ${
                    opt.value === "light"
                      ? "border-gray-300 bg-[color:var(--color-base-100)]"
                      : opt.value === "dark"
                        ? "border-gray-600 bg-gray-800"
                        : "bg-gradient-to-r from-white to-gray-800 border-gray-400"
                  }`}
                />
                <div>
                  <div className="text-sm font-medium text-text">{opt.label}</div>
                  <div className="text-xs text-text-muted">{opt.description}</div>
                </div>
                {isActive && (
                  <span className="inline-flex items-center rounded-full bg-accent px-2 py-0.5 text-[10px] font-medium text-white">
                    Active
                  </span>
                )}
              </button>
            );
          })}
        </div>

        <div className="mt-4 text-xs text-text-muted">
          Current resolved theme: <span className="font-medium text-text">{theme}</span>
        </div>
      </div>
    </div>
  );
}
