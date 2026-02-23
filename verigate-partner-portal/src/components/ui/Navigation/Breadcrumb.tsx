import * as React from "react";
import Link from "next/link";
import { cn } from "@/lib/cn";
import { ChevronRight, Home } from "lucide-react";

export interface BreadcrumbItem {
  label: string;
  href?: string;
  current?: boolean;
}

export interface BreadcrumbProps {
  items: BreadcrumbItem[];
  className?: string;
  maxItems?: number;
  showHome?: boolean;
}

export const Breadcrumb: React.FC<BreadcrumbProps> = ({
  items,
  className,
  maxItems = 5,
  showHome = true,
}) => {
  // Handle overflow with ellipsis
  const displayItems = React.useMemo(() => {
    if (items.length <= maxItems) return items;
    
    const firstItem = items[0];
    const lastItems = items.slice(-(maxItems - 2));
    
    return [
      firstItem,
      { label: "...", href: undefined },
      ...lastItems,
    ];
  }, [items, maxItems]);

  return (
    <nav aria-label="Breadcrumb" className={cn("flex items-center space-x-1", className)}>
      {showHome && (
        <>
          <Link
            href="/dashboard"
            className="text-text-muted hover:text-accent transition-colors"
          >
            <Home className="h-4 w-4" />
            <span className="sr-only">Home</span>
          </Link>
          {displayItems.length > 0 && (
            <ChevronRight className="h-3 w-3 text-text-muted" />
          )}
        </>
      )}
      
      <ol className="flex items-center space-x-1">
        {displayItems.map((item, index) => (
          <li key={index} className="flex items-center">
            {index > 0 && (
              <ChevronRight className="h-3 w-3 text-text-muted mx-1" />
            )}
            
            {item.label === "..." ? (
              <span className="text-text-muted px-1">...</span>
            ) : item.current || !item.href ? (
              <span className="text-text font-medium text-aws-body">
                {item.label}
              </span>
            ) : (
              <Link
                href={item.href}
                className="text-accent hover:text-accent-strong transition-colors text-aws-body"
              >
                {item.label}
              </Link>
            )}
          </li>
        ))}
      </ol>
    </nav>
  );
};