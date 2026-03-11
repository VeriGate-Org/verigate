"use client";

import { Component, type ErrorInfo, type ReactNode } from "react";
import { AlertTriangle } from "lucide-react";

interface Props {
  children: ReactNode;
  fallback?: ReactNode;
}

interface State {
  hasError: boolean;
  error: Error | null;
}

export default class ErrorBoundary extends Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = { hasError: false, error: null };
  }

  static getDerivedStateFromError(error: Error): State {
    return { hasError: true, error };
  }

  componentDidCatch(error: Error, errorInfo: ErrorInfo) {
    console.error("ErrorBoundary caught:", error, errorInfo);
  }

  render() {
    if (this.state.hasError) {
      if (this.props.fallback) {
        return this.props.fallback;
      }

      return (
        <div className="flex flex-col items-center justify-center gap-4 rounded-lg border border-danger/40 bg-danger/5 p-8">
          <AlertTriangle className="h-10 w-10 text-danger" />
          <div className="text-center">
            <h2 className="text-lg font-semibold text-text">Something went wrong</h2>
            <p className="mt-1 text-sm text-text-muted">
              {this.state.error?.message || "An unexpected error occurred."}
            </p>
          </div>
          <button
            onClick={() => this.setState({ hasError: false, error: null })}
            className="rounded border border-border px-4 py-2 text-sm font-medium text-text hover:bg-surface"
          >
            Try again
          </button>
        </div>
      );
    }

    return this.props.children;
  }
}
