import * as React from "react";
import { cn } from "@/lib/cn";

interface SkeletonProps extends React.HTMLAttributes<HTMLDivElement> {
  className?: string;
}

export const Skeleton = React.forwardRef<HTMLDivElement, SkeletonProps>(
  ({ className, ...props }, ref) => {
    return (
      <div
        ref={ref}
        className={cn(
          "animate-pulse rounded-md bg-text-muted/10 dark:bg-text-muted/20",
          className
        )}
        {...props}
      />
    );
  }
);
Skeleton.displayName = "Skeleton";

// Specific skeleton components for common patterns
export const SkeletonText: React.FC<{ lines?: number; className?: string }> = ({ 
  lines = 1, 
  className 
}) => (
  <div className={cn("space-y-2", className)}>
    {Array.from({ length: lines }).map((_, i) => (
      <Skeleton 
        key={i} 
        className={cn(
          "h-4",
          i === lines - 1 ? "w-3/4" : "w-full"
        )} 
      />
    ))}
  </div>
);

export const SkeletonCard: React.FC<{ className?: string }> = ({ className }) => (
  <div className={cn("console-card", className)}>
    <div className="console-card-header">
      <div className="space-y-2">
        <Skeleton className="h-5 w-1/3" />
        <Skeleton className="h-3 w-1/2" />
      </div>
      <Skeleton className="h-8 w-20" />
    </div>
    <div className="console-card-body space-y-4">
      <SkeletonText lines={3} />
      <div className="grid grid-cols-3 gap-4">
        <Skeleton className="h-20" />
        <Skeleton className="h-20" />
        <Skeleton className="h-20" />
      </div>
    </div>
  </div>
);

export const SkeletonTable: React.FC<{ 
  rows?: number; 
  columns?: number; 
  className?: string 
}> = ({ 
  rows = 5, 
  columns = 4, 
  className 
}) => (
  <div className={cn("console-card", className)}>
    <div className="console-card-header">
      <Skeleton className="h-5 w-1/4" />
      <Skeleton className="h-8 w-24" />
    </div>
    <div className="console-card-body p-0">
      <div className="overflow-hidden">
        {/* Table header */}
        <div className="bg-background px-4 py-3 border-b border-border">
          <div className="grid gap-4" style={{ gridTemplateColumns: `repeat(${columns}, 1fr)` }}>
            {Array.from({ length: columns }).map((_, i) => (
              <Skeleton key={i} className="h-4 w-20" />
            ))}
          </div>
        </div>
        
        {/* Table rows */}
        {Array.from({ length: rows }).map((_, rowIndex) => (
          <div key={rowIndex} className="px-4 py-3 border-b border-border last:border-b-0">
            <div className="grid gap-4" style={{ gridTemplateColumns: `repeat(${columns}, 1fr)` }}>
              {Array.from({ length: columns }).map((_, colIndex) => (
                <Skeleton key={colIndex} className="h-4" />
              ))}
            </div>
          </div>
        ))}
      </div>
    </div>
  </div>
);

export const SkeletonMetricCard: React.FC<{ className?: string }> = ({ className }) => (
  <div className={cn("console-card", className)}>
    <div className="console-card-body">
      <div className="flex items-center justify-between mb-2">
        <Skeleton className="h-4 w-24" />
        <Skeleton className="h-4 w-4 rounded-full" />
      </div>
      <Skeleton className="h-8 w-16 mb-1" />
      <Skeleton className="h-3 w-20" />
    </div>
  </div>
);

export const SkeletonChart: React.FC<{ 
  height?: number; 
  className?: string 
}> = ({ 
  height = 200, 
  className 
}) => (
  <div className={cn("console-card", className)}>
    <div className="console-card-header">
      <Skeleton className="h-5 w-32" />
      <Skeleton className="h-3 w-16" />
    </div>
    <div className="console-card-body">
      <Skeleton className="w-full" style={{ height: `${height}px` }} />
    </div>
  </div>
);