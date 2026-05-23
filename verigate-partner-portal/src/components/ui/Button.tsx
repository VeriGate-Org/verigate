import { forwardRef, type ButtonHTMLAttributes } from "react";
import { cva, type VariantProps } from "class-variance-authority";
import { cn } from "@/lib/cn";

const buttonVariants = cva(
  "aws-button inline-flex items-center justify-center gap-1.5 whitespace-nowrap font-medium transition-all disabled:opacity-50 disabled:cursor-not-allowed",
  {
    variants: {
      variant: {
        primary: "aws-button--primary",
        secondary: "aws-button--secondary",
        cta: "aws-button--cta",
        "cta-outline": "aws-button--cta-outline",
        "primary-outline": "aws-button--primary-outline",
        destructive: "aws-button--destructive",
        ghost: "aws-button--ghost",
        link: "bg-transparent border-transparent text-accent p-0 font-medium hover:underline",
      },
      size: {
        sm: "py-1 px-2.5 text-xs",
        md: "py-[7px] px-[14px] text-[13px]",
        lg: "py-2.5 px-5 text-sm",
      },
    },
    defaultVariants: {
      variant: "primary",
      size: "md",
    },
  },
);

export interface ButtonProps
  extends ButtonHTMLAttributes<HTMLButtonElement>,
    VariantProps<typeof buttonVariants> {
  icon?: React.ReactNode;
  iconRight?: React.ReactNode;
}

const Button = forwardRef<HTMLButtonElement, ButtonProps>(
  ({ className, variant, size, icon, iconRight, children, ...props }, ref) => (
    <button
      ref={ref}
      className={cn(buttonVariants({ variant, size }), className)}
      {...props}
    >
      {icon && <span className="inline-flex shrink-0">{icon}</span>}
      {children}
      {iconRight && <span className="inline-flex shrink-0">{iconRight}</span>}
    </button>
  ),
);
Button.displayName = "Button";

export { Button, buttonVariants };
