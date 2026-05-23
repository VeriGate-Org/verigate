import { cva, type VariantProps } from "class-variance-authority";
import { cn } from "@/lib/cn";

const GLYPHS: Record<string, string | null> = {
  success: "\u2713",
  warning: "\u26A0",
  danger: "\u2717",
  info: "\u24D8",
  pending: "\u2299",
  neutral: null,
};

const badgeVariants = cva(
  "inline-flex items-center gap-1 rounded-aws-token font-medium whitespace-nowrap leading-snug",
  {
    variants: {
      variant: {
        neutral: "bg-[#F2F3F3] text-[#1A2024] border border-[#D5DBDB]",
        success:
          "bg-[rgba(44,151,75,0.10)] text-[#2C974B] border border-[rgba(44,151,75,0.25)]",
        warning:
          "bg-[rgba(194,139,11,0.10)] text-[#C28B0B] border border-[rgba(194,139,11,0.25)]",
        danger:
          "bg-[rgba(226,61,54,0.10)] text-[#E23D36] border border-[rgba(226,61,54,0.25)]",
        info: "bg-[rgba(0,179,217,0.10)] text-[#0099bb] border border-[rgba(0,179,217,0.25)]",
        pending:
          "bg-[rgba(79,91,103,0.10)] text-[#4F5B67] border border-[rgba(79,91,103,0.25)]",
      },
      size: {
        sm: "px-2.5 py-[3px] text-xs",
        lg: "px-3 py-[5px] text-[13px]",
      },
    },
    defaultVariants: {
      variant: "neutral",
      size: "sm",
    },
  },
);

export interface BadgeProps
  extends React.HTMLAttributes<HTMLSpanElement>,
    VariantProps<typeof badgeVariants> {
  noGlyph?: boolean;
}

export function Badge({
  className,
  variant = "neutral",
  size,
  noGlyph,
  children,
  ...props
}: BadgeProps) {
  const glyph = variant ? GLYPHS[variant] : null;
  return (
    <span className={cn(badgeVariants({ variant, size }), className)} {...props}>
      {glyph && !noGlyph && <span aria-hidden="true">{glyph}</span>}
      {children}
    </span>
  );
}
