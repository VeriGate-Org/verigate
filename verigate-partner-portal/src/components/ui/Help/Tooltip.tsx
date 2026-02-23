import * as React from "react";
import * as TooltipPrimitive from "@radix-ui/react-tooltip";
import { cn } from "@/lib/cn";
import { HelpCircle, Info } from "lucide-react";

// Enhanced Tooltip with rich content support
export interface TooltipProps {
  children: React.ReactNode;
  content: React.ReactNode;
  side?: "top" | "right" | "bottom" | "left";
  align?: "start" | "center" | "end";
  delayDuration?: number;
  maxWidth?: string;
  variant?: "default" | "info" | "help";
}

export const Tooltip: React.FC<TooltipProps> = ({
  children,
  content,
  side = "top",
  align = "center",
  delayDuration = 200,
  maxWidth = "320px",
  variant = "default",
}) => {
  return (
    <TooltipPrimitive.Provider delayDuration={delayDuration}>
      <TooltipPrimitive.Root>
        <TooltipPrimitive.Trigger asChild>
          {children}
        </TooltipPrimitive.Trigger>
        <TooltipPrimitive.Portal>
          <TooltipPrimitive.Content
            side={side}
            align={align}
            sideOffset={8}
            className={cn(
              "z-50 rounded-aws-control border border-border bg-base-100 px-aws-m py-aws-s text-aws-body shadow-aws-raised",
              "animate-in fade-in-0 zoom-in-95 data-[state=closed]:animate-out data-[state=closed]:fade-out-0 data-[state=closed]:zoom-out-95",
              "data-[side=bottom]:slide-in-from-top-2 data-[side=left]:slide-in-from-right-2",
              "data-[side=right]:slide-in-from-left-2 data-[side=top]:slide-in-from-bottom-2",
              {
                "bg-info-soft border-info text-info-strong": variant === "info",
                "bg-accent-soft border-accent text-accent-strong": variant === "help",
              }
            )}
            style={{ maxWidth }}
          >
            {content}
            <TooltipPrimitive.Arrow className="fill-base-100" />
          </TooltipPrimitive.Content>
        </TooltipPrimitive.Portal>
      </TooltipPrimitive.Root>
    </TooltipPrimitive.Provider>
  );
};

// Info Icon with Tooltip
export interface InfoTooltipProps {
  content: React.ReactNode;
  className?: string;
  iconClassName?: string;
  side?: "top" | "right" | "bottom" | "left";
}

export const InfoTooltip: React.FC<InfoTooltipProps> = ({
  content,
  className,
  iconClassName,
  side = "top",
}) => {
  return (
    <Tooltip content={content} side={side} variant="info">
      <button
        type="button"
        className={cn(
          "inline-flex items-center justify-center rounded-full p-0.5 text-text-muted hover:text-accent transition-colors",
          className
        )}
        aria-label="More information"
      >
        <Info className={cn("h-4 w-4", iconClassName)} />
      </button>
    </Tooltip>
  );
};

// Help Icon with Tooltip
export interface HelpTooltipProps {
  content: React.ReactNode;
  className?: string;
  iconClassName?: string;
  side?: "top" | "right" | "bottom" | "left";
}

export const HelpTooltip: React.FC<HelpTooltipProps> = ({
  content,
  className,
  iconClassName,
  side = "top",
}) => {
  return (
    <Tooltip content={content} side={side} variant="help">
      <button
        type="button"
        className={cn(
          "inline-flex items-center justify-center rounded-full p-0.5 text-text-muted hover:text-accent transition-colors",
          className
        )}
        aria-label="Help"
      >
        <HelpCircle className={cn("h-4 w-4", iconClassName)} />
      </button>
    </Tooltip>
  );
};

// Inline Help Text
export interface InlineHelpProps {
  children: React.ReactNode;
  className?: string;
}

export const InlineHelp: React.FC<InlineHelpProps> = ({ children, className }) => {
  return (
    <div className={cn("text-xs text-text-muted mt-1", className)}>
      {children}
    </div>
  );
};
