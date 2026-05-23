"use client";

import Link from "next/link";
import { cn } from "@/lib/cn";
import { Badge } from "@/components/ui/Badge";
import { ChevronRight } from "lucide-react";
import type { ComponentType } from "react";

export interface ServiceCardProps {
  name: string;
  shortLabel: string;
  description: string;
  category: string;
  icon: ComponentType<{ className?: string; size?: number }>;
  route: string;
  provider: string;
  disabled?: boolean;
  className?: string;
}

export function ServiceCard(props: ServiceCardProps) {
  const { shortLabel, description, icon: Icon, route, provider, disabled, className } = props;

  const content = (
    <div
      className={cn(
        "console-card group relative flex flex-col gap-3 p-4 transition-all",
        !disabled && "hover:border-accent hover:shadow-sm cursor-pointer",
        disabled && "opacity-50 cursor-not-allowed",
        className,
      )}
    >
      <div className="flex items-start justify-between gap-3">
        <div
          className={cn(
            "w-9 h-9 rounded-lg flex items-center justify-center shrink-0",
            "bg-accent-soft",
          )}
        >
          <Icon size={18} className="text-accent" />
        </div>
        <ChevronRight
          size={14}
          className={cn(
            "text-text-muted mt-0.5 transition-transform",
            !disabled && "group-hover:translate-x-0.5 group-hover:text-accent",
          )}
        />
      </div>

      <div className="flex-1 min-w-0">
        <h3 className="text-sm font-semibold text-text leading-snug truncate">
          {shortLabel}
        </h3>
        <p className="text-[11px] text-text-muted mt-0.5 leading-relaxed line-clamp-2">
          {description}
        </p>
      </div>

      <div className="flex items-center gap-1.5 flex-wrap">
        <Badge variant="neutral" size="sm" noGlyph>
          {provider}
        </Badge>
        {disabled && (
          <Badge variant="pending" size="sm" noGlyph>
            Coming soon
          </Badge>
        )}
      </div>
    </div>
  );

  if (disabled) {
    return content;
  }

  return (
    <Link href={route} className="no-underline">
      {content}
    </Link>
  );
}
