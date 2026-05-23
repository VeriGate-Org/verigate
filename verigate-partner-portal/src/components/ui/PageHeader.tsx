import { cn } from "@/lib/cn";

export interface PageHeaderProps {
  category?: string;
  title: string;
  description?: string;
  actions?: React.ReactNode;
  className?: string;
}

export function PageHeader({
  category,
  title,
  description,
  actions,
  className,
}: PageHeaderProps) {
  return (
    <div className={cn("flex items-start justify-between gap-4", className)}>
      <div>
        {category && (
          <span className="text-[10px] font-semibold uppercase tracking-[0.12em] text-text-muted">
            {category}
          </span>
        )}
        <h1 className="text-aws-heading-l font-semibold text-text">{title}</h1>
        {description && (
          <p className="text-[13px] text-text-muted mt-1 max-w-xl leading-relaxed">
            {description}
          </p>
        )}
      </div>
      {actions && <div className="flex items-center gap-2 shrink-0">{actions}</div>}
    </div>
  );
}
