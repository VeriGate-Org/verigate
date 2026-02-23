import { cva, type VariantProps } from "class-variance-authority";
import { cn } from "@/lib/cn";
import * as React from "react";

const buttonStyles = cva(
  "aws-button inline-flex items-center justify-center font-medium transition-all focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-accent focus-visible:ring-offset-2 disabled:opacity-50 disabled:pointer-events-none",
  {
    variants: {
      variant: {
        primary: "aws-button--primary",
        secondary: "aws-button--secondary", 
        destructive: "aws-button--destructive",
        ghost: "aws-button--ghost",
        cta: "aws-button--cta",
        "cta-outline": "aws-button--cta-outline",
        "primary-outline": "aws-button--primary-outline",
        // Additional AWS Console variants
        link: "bg-transparent border-transparent text-accent hover:text-accent-strong hover:underline p-0 h-auto font-normal",
      },
      size: {
        sm: "h-8 px-aws-m text-aws-body gap-aws-xs",
        md: "h-9 px-aws-l text-aws-body gap-aws-s", 
        lg: "h-10 px-aws-xl text-aws-body gap-aws-s",
        xl: "h-12 px-aws-xxl text-aws-heading-xs gap-aws-m",
      },
    },
    defaultVariants: {
      variant: "secondary",
      size: "md",
    },
  }
);

export interface ButtonProps
  extends React.ButtonHTMLAttributes<HTMLButtonElement>,
    VariantProps<typeof buttonStyles> {
  loading?: boolean;
  icon?: React.ReactNode;
  iconPosition?: "left" | "right";
}

export const Button = React.forwardRef<HTMLButtonElement, ButtonProps>(
  ({ className, variant, size, loading, icon, iconPosition = "left", children, disabled, ...props }, ref) => {
    const hasIcon = icon && !loading;
    const hasChildren = React.Children.count(children) > 0;
    
    return (
      <button 
        ref={ref} 
        className={cn(buttonStyles({ variant, size }), className)} 
        disabled={disabled || loading}
        {...props}
      >
        {loading && (
          <svg
            className="animate-spin h-4 w-4"
            fill="none"
            viewBox="0 0 24 24"
            aria-hidden="true"
          >
            <circle
              className="opacity-25"
              cx="12"
              cy="12"
              r="10"
              stroke="currentColor"
              strokeWidth="4"
            />
            <path
              className="opacity-75"
              fill="currentColor"
              d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
            />
          </svg>
        )}
        
        {hasIcon && iconPosition === "left" && !loading && (
          <span className="flex items-center justify-center h-4 w-4" aria-hidden="true">
            {icon}
          </span>
        )}
        
        {hasChildren && (
          <span className={cn(
            loading && "opacity-0",
            !hasChildren && "sr-only"
          )}>
            {children}
          </span>
        )}
        
        {hasIcon && iconPosition === "right" && !loading && (
          <span className="flex items-center justify-center h-4 w-4" aria-hidden="true">
            {icon}
          </span>
        )}
      </button>
    );
  }
);
Button.displayName = "Button";
