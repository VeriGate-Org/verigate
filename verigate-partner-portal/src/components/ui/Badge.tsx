import { cn } from "@/lib/cn";
import * as React from "react";

export interface BadgeProps extends React.HTMLAttributes<HTMLSpanElement> {
  variant?: "neutral" | "success" | "warning" | "danger" | "info" | "pending";
  size?: "sm" | "md" | "lg";
  icon?: React.ReactNode;
}

export function Badge({ 
  className, 
  variant = "neutral", 
  size = "md", 
  icon,
  children,
  ...props 
}: BadgeProps) {
  const base = "inline-flex items-center rounded-aws-token font-medium gap-aws-xs";
  
  const sizeStyles = {
    sm: "px-aws-xs py-aws-2xs text-xs",
    md: "px-aws-s py-aws-xs text-aws-body",
    lg: "px-aws-m py-aws-s text-aws-body",
  };
  
  const variantStyles = {
    neutral: "bg-background text-text border border-border",
    success: "bg-success/10 text-success border border-success/20",
    warning: "bg-warning/10 text-warning border border-warning/20",
    danger: "bg-danger/10 text-danger border border-danger/20",
    info: "bg-info/10 text-info border border-info/20",
    pending: "bg-text-muted/10 text-text-muted border border-text-muted/20",
  };
  
  return (
    <span 
      className={cn(
        base, 
        sizeStyles[size], 
        variantStyles[variant], 
        className
      )} 
      {...props} 
    >
      {icon && <span className="flex-shrink-0">{icon}</span>}
      {children}
    </span>
  );
}
