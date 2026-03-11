"use client";

import * as ToastPrimitive from "@radix-ui/react-toast";
import { X } from "lucide-react";
import {
  createContext,
  useCallback,
  useContext,
  useState,
  type ReactNode,
} from "react";

type ToastVariant = "default" | "success" | "error";

interface ToastItem {
  id: string;
  title: string;
  description?: string;
  variant: ToastVariant;
}

interface ToastContextValue {
  toast: (opts: { title: string; description?: string; variant?: ToastVariant }) => void;
}

const ToastContext = createContext<ToastContextValue>({
  toast: () => {},
});

export function useToast() {
  return useContext(ToastContext);
}

const variantStyles: Record<ToastVariant, string> = {
  default: "border-border bg-surface",
  success: "border-success/40 bg-success/5",
  error: "border-danger/40 bg-danger/5",
};

export function ToastProvider({ children }: { children: ReactNode }) {
  const [toasts, setToasts] = useState<ToastItem[]>([]);

  const toast = useCallback(
    (opts: { title: string; description?: string; variant?: ToastVariant }) => {
      const id = crypto.randomUUID();
      setToasts((prev) => [...prev, { id, variant: opts.variant ?? "default", ...opts }]);
    },
    [],
  );

  const removeToast = useCallback((id: string) => {
    setToasts((prev) => prev.filter((t) => t.id !== id));
  }, []);

  return (
    <ToastContext.Provider value={{ toast }}>
      <ToastPrimitive.Provider swipeDirection="right" duration={5000}>
        {children}
        {toasts.map((t) => (
          <ToastPrimitive.Root
            key={t.id}
            className={`rounded-lg border p-4 shadow-lg ${variantStyles[t.variant]}`}
            onOpenChange={(open) => {
              if (!open) removeToast(t.id);
            }}
          >
            <div className="flex items-start justify-between gap-2">
              <div>
                <ToastPrimitive.Title className="text-sm font-semibold text-text">
                  {t.title}
                </ToastPrimitive.Title>
                {t.description && (
                  <ToastPrimitive.Description className="mt-1 text-sm text-text-muted">
                    {t.description}
                  </ToastPrimitive.Description>
                )}
              </div>
              <ToastPrimitive.Close className="rounded p-1 text-text-muted hover:text-text">
                <X className="h-4 w-4" />
              </ToastPrimitive.Close>
            </div>
          </ToastPrimitive.Root>
        ))}
        <ToastPrimitive.Viewport className="fixed bottom-4 right-4 z-50 flex w-96 max-w-[calc(100vw-2rem)] flex-col gap-2" />
      </ToastPrimitive.Provider>
    </ToastContext.Provider>
  );
}
