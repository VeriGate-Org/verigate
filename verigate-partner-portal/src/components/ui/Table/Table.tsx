import * as React from "react";
import { cn } from "@/lib/cn";
import { ChevronUp, ChevronDown, ChevronsUpDown } from "lucide-react";

// Table Context for managing state
interface TableContextValue {
  density?: "compact" | "comfortable";
  sortColumn?: string;
  sortDirection?: "asc" | "desc";
  onSort?: (column: string) => void;
}

const TableContext = React.createContext<TableContextValue>({});

// Main Table Component
interface TableProps extends React.HTMLAttributes<HTMLTableElement> {
  density?: "compact" | "comfortable";
  sortColumn?: string;
  sortDirection?: "asc" | "desc";
  onSort?: (column: string) => void;
}

export const Table = React.forwardRef<HTMLTableElement, TableProps>(
  ({ className, density = "comfortable", sortColumn, sortDirection, onSort, children, ...props }, ref) => {
    const contextValue = React.useMemo(
      () => ({ density, sortColumn, sortDirection, onSort }),
      [density, sortColumn, sortDirection, onSort]
    );

    return (
      <TableContext.Provider value={contextValue}>
        <div className="overflow-hidden rounded-aws-container border border-border-control bg-surface">
          <table
            ref={ref}
            className={cn("aws-table", className)}
            {...props}
          >
            {children}
          </table>
        </div>
      </TableContext.Provider>
    );
  }
);
Table.displayName = "Table";

// Table Header
interface TableHeaderProps extends React.HTMLAttributes<HTMLTableSectionElement> {
  children: React.ReactNode;
}

export const TableHeader = React.forwardRef<HTMLTableSectionElement, TableHeaderProps>(
  ({ className, ...props }, ref) => (
    <thead ref={ref} className={cn("bg-background", className)} {...props} />
  )
);
TableHeader.displayName = "TableHeader";

// Table Body
interface TableBodyProps extends React.HTMLAttributes<HTMLTableSectionElement> {
  children: React.ReactNode;
}

export const TableBody = React.forwardRef<HTMLTableSectionElement, TableBodyProps>(
  ({ className, ...props }, ref) => (
    <tbody ref={ref} className={cn(className)} {...props} />
  )
);
TableBody.displayName = "TableBody";

// Table Row
interface TableRowProps extends React.HTMLAttributes<HTMLTableRowElement> {
  selected?: boolean;
  clickable?: boolean;
}

export const TableRow = React.forwardRef<HTMLTableRowElement, TableRowProps>(
  ({ className, selected, clickable, ...props }, ref) => (
    <tr
      ref={ref}
      className={cn(
        "transition-colors duration-aws-quick",
        {
          "hover:bg-hover": clickable,
          "bg-accent-soft border-accent": selected,
          "cursor-pointer": clickable,
        },
        className
      )}
      {...props}
    />
  )
);
TableRow.displayName = "TableRow";

// Table Head Cell (sortable)
interface TableHeadProps extends React.HTMLAttributes<HTMLTableCellElement> {
  sortKey?: string;
  sortable?: boolean;
  width?: string;
}

export const TableHead = React.forwardRef<HTMLTableCellElement, TableHeadProps>(
  ({ className, children, sortKey, sortable, width, onClick, ...props }, ref) => {
    const { sortColumn, sortDirection, onSort } = React.useContext(TableContext);
    
    const handleClick = React.useCallback(() => {
      if (sortable && sortKey && onSort) {
        onSort(sortKey);
      }
      onClick?.({} as React.MouseEvent<HTMLTableCellElement>);
    }, [sortable, sortKey, onSort, onClick]);

    const getSortIcon = () => {
      if (!sortable) return null;
      
      if (sortColumn !== sortKey) {
        return <ChevronsUpDown className="ml-1 h-3 w-3 text-text-muted" />;
      }
      
      return sortDirection === "asc" ? (
        <ChevronUp className="ml-1 h-3 w-3 text-accent" />
      ) : (
        <ChevronDown className="ml-1 h-3 w-3 text-accent" />
      );
    };

    return (
      <th
        ref={ref}
        className={cn(
          "aws-table-header",
          {
            "cursor-pointer select-none hover:bg-hover/50": sortable,
          },
          className
        )}
        style={{ width }}
        onClick={sortable ? handleClick : onClick}
        {...props}
      >
        <div className="flex items-center">
          <span>{children}</span>
          {getSortIcon()}
        </div>
      </th>
    );
  }
);
TableHead.displayName = "TableHead";

// Table Cell
interface TableCellProps extends React.TdHTMLAttributes<HTMLTableCellElement> {
  width?: string;
}

export const TableCell = React.forwardRef<HTMLTableCellElement, TableCellProps>(
  ({ className, width, ...props }, ref) => {
    const { density } = React.useContext(TableContext);
    
    return (
      <td
        ref={ref}
        className={cn(
          "border-b border-border text-sm",
          {
            "px-aws-s py-aws-xs": density === "compact",
            "px-aws-m py-aws-s": density === "comfortable",
          },
          className
        )}
        style={{ width }}
        {...props}
      />
    );
  }
);
TableCell.displayName = "TableCell";

// Table Footer
interface TableFooterProps extends React.HTMLAttributes<HTMLTableSectionElement> {
  children: React.ReactNode;
}

export const TableFooter = React.forwardRef<HTMLTableSectionElement, TableFooterProps>(
  ({ className, ...props }, ref) => (
    <tfoot ref={ref} className={cn("bg-background font-medium", className)} {...props} />
  )
);
TableFooter.displayName = "TableFooter";

// Table Empty State
interface TableEmptyProps {
  children: React.ReactNode;
  colSpan?: number;
}

export const TableEmpty: React.FC<TableEmptyProps> = ({ children, colSpan }) => (
  <TableRow>
    <TableCell colSpan={colSpan} className="text-center py-aws-xxl text-text-muted">
      {children}
    </TableCell>
  </TableRow>
);

// Table Loading State
interface TableLoadingProps {
  colSpan?: number;
  rows?: number;
}

export const TableLoading: React.FC<TableLoadingProps> = ({ colSpan, rows = 5 }) => (
  <>
    {Array.from({ length: rows }).map((_, index) => (
      <TableRow key={index}>
        <TableCell colSpan={colSpan}>
          <div className="animate-pulse bg-background h-4 rounded"></div>
        </TableCell>
      </TableRow>
    ))}
  </>
);