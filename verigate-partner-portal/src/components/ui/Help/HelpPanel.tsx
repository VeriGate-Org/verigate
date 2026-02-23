"use client";

import * as React from "react";
import { cn } from "@/lib/cn";
import { X, HelpCircle, ExternalLink } from "lucide-react";
import { Button } from "@/components/ui/Button";

// Help Panel that slides in from the right
export interface HelpPanelProps {
  isOpen: boolean;
  onClose: () => void;
  title?: string;
  children: React.ReactNode;
  width?: string;
}

export const HelpPanel: React.FC<HelpPanelProps> = ({
  isOpen,
  onClose,
  title = "Help",
  children,
  width = "400px",
}) => {
  // Close on escape key
  React.useEffect(() => {
    const handleEscape = (e: KeyboardEvent) => {
      if (e.key === "Escape" && isOpen) {
        onClose();
      }
    };

    document.addEventListener("keydown", handleEscape);
    return () => document.removeEventListener("keydown", handleEscape);
  }, [isOpen, onClose]);

  // Prevent body scroll when open
  React.useEffect(() => {
    if (isOpen) {
      document.body.style.overflow = "hidden";
    } else {
      document.body.style.overflow = "";
    }

    return () => {
      document.body.style.overflow = "";
    };
  }, [isOpen]);

  if (!isOpen) return null;

  return (
    <>
      {/* Overlay */}
      <div
        className="fixed inset-0 bg-black/30 z-40 transition-opacity duration-aws-moderate"
        onClick={onClose}
        aria-hidden="true"
      />

      {/* Panel */}
      <div
        className={cn(
          "fixed top-0 right-0 bottom-0 z-50 bg-base-100 border-l border-border shadow-aws-modal",
          "transform transition-transform duration-aws-moderate ease-aws-emphasized",
          isOpen ? "translate-x-0" : "translate-x-full"
        )}
        style={{ width }}
        role="dialog"
        aria-modal="true"
        aria-labelledby="help-panel-title"
      >
        {/* Header */}
        <div className="flex items-center justify-between px-aws-l py-aws-m border-b border-border">
          <div className="flex items-center gap-aws-s">
            <HelpCircle className="h-5 w-5 text-accent" />
            <h2 id="help-panel-title" className="text-lg font-semibold text-text">
              {title}
            </h2>
          </div>
          <Button
            variant="ghost"
            size="sm"
            onClick={onClose}
            aria-label="Close help panel"
          >
            <X className="h-4 w-4" />
          </Button>
        </div>

        {/* Content */}
        <div className="overflow-y-auto h-[calc(100vh-4rem)] px-aws-l py-aws-m">
          {children}
        </div>
      </div>
    </>
  );
};

// Help Section Component
export interface HelpSectionProps {
  title: string;
  icon?: React.ComponentType<{ className?: string }>;
  children: React.ReactNode;
  className?: string;
}

export const HelpSection: React.FC<HelpSectionProps> = ({
  title,
  icon: Icon,
  children,
  className,
}) => {
  return (
    <div className={cn("mb-aws-l", className)}>
      <div className="flex items-center gap-aws-xs mb-aws-s">
        {Icon && <Icon className="h-4 w-4 text-accent" />}
        <h3 className="text-sm font-semibold text-text">{title}</h3>
      </div>
      <div className="text-aws-body text-text-muted space-y-aws-s">
        {children}
      </div>
    </div>
  );
};

// Help Link Component
export interface HelpLinkProps {
  href: string;
  children: React.ReactNode;
  external?: boolean;
  className?: string;
}

export const HelpLink: React.FC<HelpLinkProps> = ({
  href,
  children,
  external = false,
  className,
}) => {
  return (
    <a
      href={href}
      className={cn(
        "inline-flex items-center gap-1 text-accent hover:text-accent-strong transition-colors text-aws-body",
        className
      )}
      target={external ? "_blank" : undefined}
      rel={external ? "noopener noreferrer" : undefined}
    >
      {children}
      {external && <ExternalLink className="h-3 w-3" />}
    </a>
  );
};

// Code Example Component
export interface CodeExampleProps {
  code: string;
  language?: string;
  className?: string;
}

export const CodeExample: React.FC<CodeExampleProps> = ({
  code,
  className,
}) => {
  const [copied, setCopied] = React.useState(false);

  const handleCopy = async () => {
    await navigator.clipboard.writeText(code);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  return (
    <div className={cn("relative group", className)}>
      <pre className="bg-base-200 border border-border rounded-aws-control p-aws-s overflow-x-auto text-xs font-mono">
        <code className="text-text">{code}</code>
      </pre>
      <button
        onClick={handleCopy}
        className="absolute top-2 right-2 px-2 py-1 text-xs bg-base-100 border border-border rounded opacity-0 group-hover:opacity-100 transition-opacity hover:bg-hover"
      >
        {copied ? "Copied!" : "Copy"}
      </button>
    </div>
  );
};

// Help Context Hook
interface HelpContextValue {
  openHelp: (content?: React.ReactNode) => void;
  closeHelp: () => void;
  isHelpOpen: boolean;
}

const HelpContext = React.createContext<HelpContextValue | undefined>(undefined);

export const useHelp = () => {
  const context = React.useContext(HelpContext);
  if (!context) {
    throw new Error("useHelp must be used within HelpProvider");
  }
  return context;
};

// Help Provider Component
export interface HelpProviderProps {
  children: React.ReactNode;
}

export const HelpProvider: React.FC<HelpProviderProps> = ({ children }) => {
  const [isOpen, setIsOpen] = React.useState(false);
  const [content, setContent] = React.useState<React.ReactNode>(null);

  const openHelp = React.useCallback((helpContent?: React.ReactNode) => {
    if (helpContent) {
      setContent(helpContent);
    }
    setIsOpen(true);
  }, []);

  const closeHelp = React.useCallback(() => {
    setIsOpen(false);
  }, []);

  const value = React.useMemo(
    () => ({ openHelp, closeHelp, isHelpOpen: isOpen }),
    [openHelp, closeHelp, isOpen]
  );

  return (
    <HelpContext.Provider value={value}>
      {children}
      <HelpPanel isOpen={isOpen} onClose={closeHelp}>
        {content}
      </HelpPanel>
    </HelpContext.Provider>
  );
};
