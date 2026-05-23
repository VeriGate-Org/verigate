"use client";

import { PageHeader } from "@/components/ui/PageHeader";
import { FilterChips, type FilterChip } from "@/components/ui/FilterChips";
import { cn } from "@/lib/cn";

export interface ServicePageLayoutProps {
  category: string;
  title: string;
  description: string;
  actions?: React.ReactNode;
  tabs: FilterChip[];
  activeTab: string;
  onTabChange: (value: string) => void;
  children: React.ReactNode;
  className?: string;
}

export function ServicePageLayout({
  category,
  title,
  description,
  actions,
  tabs,
  activeTab,
  onTabChange,
  children,
  className,
}: ServicePageLayoutProps) {
  return (
    <div className={cn("space-y-aws-l", className)}>
      <PageHeader
        category={category}
        title={title}
        description={description}
        actions={actions}
      />

      <div className="border-b border-border">
        <FilterChips
          chips={tabs}
          value={activeTab}
          onChange={onTabChange}
          className="pb-px -mb-px"
        />
      </div>

      <div>{children}</div>
    </div>
  );
}
