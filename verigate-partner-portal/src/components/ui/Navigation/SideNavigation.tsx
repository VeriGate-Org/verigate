import * as React from "react";
import Link from "next/link";
import { usePathname } from "next/navigation";
import { cn } from "@/lib/cn";
import { ChevronDown, ChevronRight } from "lucide-react";

export interface NavigationItem {
  id: string;
  label: string;
  href?: string;
  icon?: React.ComponentType<{ className?: string }>;
  badge?: string | number;
  children?: NavigationItem[];
  disabled?: boolean;
}

interface SideNavigationProps {
  items: NavigationItem[];
  className?: string;
  width?: string;
  collapsible?: boolean;
  defaultCollapsed?: boolean;
}

export const SideNavigation: React.FC<SideNavigationProps> = ({
  items,
  className,
  width = "240px",
  collapsible = false,
  defaultCollapsed = false,
}) => {
  const pathname = usePathname();
  const [collapsed, setCollapsed] = React.useState(defaultCollapsed);
  const [expandedItems, setExpandedItems] = React.useState<Set<string>>(new Set());

  const toggleExpanded = React.useCallback((itemId: string) => {
    setExpandedItems(prev => {
      const newExpanded = new Set(prev);
      if (newExpanded.has(itemId)) {
        newExpanded.delete(itemId);
      } else {
        newExpanded.add(itemId);
      }
      return newExpanded;
    });
  }, []);

  const isActive = React.useCallback((item: NavigationItem): boolean => {
    if (item.href && pathname === item.href) return true;
    if (item.href && pathname.startsWith(`${item.href}/`)) return true;
    return false;
  }, [pathname]);

  const hasActiveChild = React.useCallback((item: NavigationItem): boolean => {
    if (!item.children) return false;
    return item.children.some(child => isActive(child) || hasActiveChild(child));
  }, [isActive]);

  const renderItem = React.useCallback((item: NavigationItem, level = 0) => {
    const active = isActive(item);
    const hasChildren = item.children && item.children.length > 0;
    const expanded = expandedItems.has(item.id);

    const content = (
      <div
        className={cn(
          "flex items-center w-full px-aws-m py-aws-s text-aws-body font-medium rounded-aws-control transition-all duration-aws-quick",
          {
            "bg-accent-soft text-accent border border-accent/20": active,
            "text-text hover:bg-hover hover:text-accent": !active && !item.disabled,
            "text-text-muted cursor-not-allowed": item.disabled,
            "pl-aws-xl": level > 0,
          }
        )}
      >
        {item.icon && (
          <item.icon className={cn("h-4 w-4 mr-aws-s flex-shrink-0", {
            "text-accent": active,
            "text-text-muted": !active,
          })} />
        )}
        
        {!collapsed && (
          <>
            <span className="flex-1 truncate">{item.label}</span>
            
            {item.badge && (
              <span className="ml-aws-s px-aws-xs py-aws-2xs bg-accent text-white text-xs rounded-full">
                {item.badge}
              </span>
            )}
            
            {hasChildren && (
              <button
                onClick={(e) => {
                  e.preventDefault();
                  e.stopPropagation();
                  toggleExpanded(item.id);
                }}
                className="ml-aws-s p-1 hover:bg-hover rounded"
              >
                {expanded ? (
                  <ChevronDown className="h-3 w-3" />
                ) : (
                  <ChevronRight className="h-3 w-3" />
                )}
              </button>
            )}
          </>
        )}
      </div>
    );

    return (
      <div key={item.id}>
        {item.href && !item.disabled ? (
          <Link href={item.href} className="block">
            {content}
          </Link>
        ) : (
          <div className={hasChildren ? "cursor-pointer" : "cursor-default"} onClick={() => hasChildren && toggleExpanded(item.id)}>
            {content}
          </div>
        )}
        
        {hasChildren && (expanded || collapsed) && (
          <div className={cn("space-y-1", {
            "ml-aws-l": !collapsed,
            "mt-aws-s": true,
          })}>
            {item.children!.map(child => renderItem(child, level + 1))}
          </div>
        )}
      </div>
    );
  }, [collapsed, expandedItems, toggleExpanded, isActive]);

  // Auto-expand effect
  React.useEffect(() => {
    const activeItems = new Set<string>();
    
    const findActiveParents = (items: NavigationItem[], parentId?: string) => {
      items.forEach(item => {
        if (hasActiveChild(item) && parentId) {
          activeItems.add(parentId);
        }
        if (item.children) {
          findActiveParents(item.children, item.id);
        }
      });
    };
    
    findActiveParents(items);
    setExpandedItems(prev => new Set([...prev, ...activeItems]));
  }, [items, hasActiveChild]);

  return (
    <nav
      className={cn(
        "bg-surface border-r border-border h-full overflow-y-auto transition-all duration-aws-show",
        className
      )}
      style={{ width: collapsed ? "64px" : width }}
    >
      <div className="p-aws-s space-y-1">
        {items.map(item => renderItem(item))}
      </div>
      
      {collapsible && (
        <div className="absolute bottom-0 left-0 right-0 p-aws-s border-t border-border bg-surface">
          <button
            onClick={() => setCollapsed(!collapsed)}
            className="w-full p-aws-s text-text-muted hover:text-accent hover:bg-hover rounded-aws-control transition-colors"
          >
            {collapsed ? (
              <ChevronRight className="h-4 w-4 mx-auto" />
            ) : (
              <span className="text-aws-body">Collapse</span>
            )}
          </button>
        </div>
      )}
    </nav>
  );
};