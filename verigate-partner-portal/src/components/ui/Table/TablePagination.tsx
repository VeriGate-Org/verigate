import * as React from "react";
import { cn } from "@/lib/cn";
import { Button } from "../Button";
import { ChevronLeft, ChevronRight, ChevronsLeft, ChevronsRight } from "lucide-react";

interface TablePaginationProps {
  currentPage: number;
  totalPages: number;
  totalItems: number;
  pageSize: number;
  onPageChange: (page: number) => void;
  onPageSizeChange?: (pageSize: number) => void;
  pageSizeOptions?: number[];
  className?: string;
}

export const TablePagination: React.FC<TablePaginationProps> = ({
  currentPage,
  totalPages,
  totalItems,
  pageSize,
  onPageChange,
  onPageSizeChange,
  pageSizeOptions = [10, 20, 50, 100],
  className,
}) => {
  const startItem = (currentPage - 1) * pageSize + 1;
  const endItem = Math.min(currentPage * pageSize, totalItems);

  const getVisiblePages = () => {
    const delta = 2;
    const range = [];
    const rangeWithDots = [];

    for (
      let i = Math.max(2, currentPage - delta);
      i <= Math.min(totalPages - 1, currentPage + delta);
      i++
    ) {
      range.push(i);
    }

    if (currentPage - delta > 2) {
      rangeWithDots.push(1, "...");
    } else {
      rangeWithDots.push(1);
    }

    rangeWithDots.push(...range);

    if (currentPage + delta < totalPages - 1) {
      rangeWithDots.push("...", totalPages);
    } else {
      if (totalPages > 1) {
        rangeWithDots.push(totalPages);
      }
    }

    return rangeWithDots;
  };

  const visiblePages = getVisiblePages();

  return (
    <div className={cn("flex items-center justify-between px-aws-l py-aws-m border-t border-border", className)}>
      <div className="flex items-center gap-aws-l">
        {onPageSizeChange && (
          <div className="flex items-center gap-aws-s">
            <span className="text-aws-body text-text-muted">Rows per page:</span>
            <select
              value={pageSize}
              onChange={(e) => onPageSizeChange(Number(e.target.value))}
              className="aws-select text-aws-body"
            >
              {pageSizeOptions.map((size) => (
                <option key={size} value={size}>
                  {size}
                </option>
              ))}
            </select>
          </div>
        )}
        
        <div className="text-aws-body text-text-muted">
          {totalItems > 0 ? (
            <>
              Showing {startItem.toLocaleString()}-{endItem.toLocaleString()} of{" "}
              {totalItems.toLocaleString()} items
            </>
          ) : (
            "No items"
          )}
        </div>
      </div>

      {totalPages > 1 && (
        <div className="flex items-center gap-aws-xs">
          <Button
            variant="secondary"
            size="sm"
            onClick={() => onPageChange(1)}
            disabled={currentPage === 1}
            className="px-aws-s"
          >
            <ChevronsLeft className="h-4 w-4" />
            <span className="sr-only">First page</span>
          </Button>
          
          <Button
            variant="secondary"
            size="sm"
            onClick={() => onPageChange(currentPage - 1)}
            disabled={currentPage === 1}
            className="px-aws-s"
          >
            <ChevronLeft className="h-4 w-4" />
            <span className="sr-only">Previous page</span>
          </Button>

          <div className="flex items-center gap-aws-2xs">
            {visiblePages.map((page, index) => (
              <React.Fragment key={index}>
                {page === "..." ? (
                  <span className="px-aws-s py-aws-xs text-text-muted">...</span>
                ) : (
                  <Button
                    variant={currentPage === page ? "primary" : "ghost"}
                    size="sm"
                    onClick={() => onPageChange(page as number)}
                    className="min-w-[32px] px-aws-s"
                  >
                    {page}
                  </Button>
                )}
              </React.Fragment>
            ))}
          </div>

          <Button
            variant="secondary"
            size="sm"
            onClick={() => onPageChange(currentPage + 1)}
            disabled={currentPage === totalPages}
            className="px-aws-s"
          >
            <ChevronRight className="h-4 w-4" />
            <span className="sr-only">Next page</span>
          </Button>
          
          <Button
            variant="secondary"
            size="sm"
            onClick={() => onPageChange(totalPages)}
            disabled={currentPage === totalPages}
            className="px-aws-s"
          >
            <ChevronsRight className="h-4 w-4" />
            <span className="sr-only">Last page</span>
          </Button>
        </div>
      )}
    </div>
  );
};