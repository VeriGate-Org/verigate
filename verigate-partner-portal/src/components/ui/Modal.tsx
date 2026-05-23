"use client";

import * as Dialog from "@radix-ui/react-dialog";
import { X } from "lucide-react";
import { cn } from "@/lib/cn";

export interface ModalProps {
  open: boolean;
  onClose: () => void;
  title: string;
  children: React.ReactNode;
  className?: string;
  wide?: boolean;
}

export function Modal({
  open,
  onClose,
  title,
  children,
  className,
  wide,
}: ModalProps) {
  return (
    <Dialog.Root open={open} onOpenChange={(v) => !v && onClose()}>
      <Dialog.Portal>
        <Dialog.Overlay className="fixed inset-0 z-50 bg-[rgba(15,26,46,0.45)] backdrop-blur-[2px] data-[state=open]:animate-in data-[state=open]:fade-in-0" />
        <Dialog.Content
          className={cn(
            "fixed left-1/2 top-1/2 z-50 -translate-x-1/2 -translate-y-1/2",
            "bg-surface border border-border rounded-aws-container shadow-[var(--aws-elevation-modal)]",
            "max-h-[85vh] overflow-y-auto",
            wide ? "w-[680px]" : "w-[460px]",
            "max-w-[calc(100vw-32px)]",
            "data-[state=open]:animate-in data-[state=open]:fade-in-0 data-[state=open]:zoom-in-95",
            className,
          )}
        >
          {/* Tri-bar accent */}
          <div className="flex gap-0.5 px-6 pt-4">
            <span className="h-[3px] w-5 rounded-full bg-[#E23D36]" />
            <span className="h-[3px] w-5 rounded-full bg-accent" />
            <span className="h-[3px] w-5 rounded-full bg-primary" />
          </div>

          <div className="flex items-start justify-between px-6 pt-3 pb-1">
            <Dialog.Title className="text-base font-semibold text-text">
              {title}
            </Dialog.Title>
            <Dialog.Close className="rounded-sm p-1 text-text-muted hover:text-text transition-colors">
              <X size={16} />
            </Dialog.Close>
          </div>

          <div className="px-6 pb-5">{children}</div>
        </Dialog.Content>
      </Dialog.Portal>
    </Dialog.Root>
  );
}
