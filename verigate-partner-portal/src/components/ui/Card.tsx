import { cn } from "@/lib/cn";

export function Card({
  className,
  children,
  ...props
}: React.HTMLAttributes<HTMLDivElement>) {
  return (
    <div className={cn("console-card", className)} {...props}>
      {children}
    </div>
  );
}

export function CardHeader({
  className,
  children,
  ...props
}: React.HTMLAttributes<HTMLDivElement>) {
  return (
    <div className={cn("console-card-header", className)} {...props}>
      {children}
    </div>
  );
}

export function CardBody({
  className,
  compact,
  children,
  ...props
}: React.HTMLAttributes<HTMLDivElement> & { compact?: boolean }) {
  return (
    <div
      className={cn(
        compact ? "console-card-body--compact" : "console-card-body",
        className,
      )}
      {...props}
    >
      {children}
    </div>
  );
}
