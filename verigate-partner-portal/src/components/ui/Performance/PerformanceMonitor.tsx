"use client";

import * as React from "react";
import { cn } from "@/lib/cn";
import { AlertTriangle, CheckCircle, Clock, TrendingDown, TrendingUp, Activity } from "lucide-react";

// Performance metrics tracking
export interface PerformanceMetrics {
  avgResponseTime: number;
  slowestEndpoint: string;
  slowestTime: number;
  errorRate: number;
  totalRequests: number;
  renderTime: number;
  memoryUsage: number;
  timestamp: Date;
}

// Performance monitoring hook
export function usePerformanceMonitoring() {
  const [metrics, setMetrics] = React.useState<PerformanceMetrics>({
    avgResponseTime: 0,
    slowestEndpoint: "",
    slowestTime: 0,
    errorRate: 0,
    totalRequests: 0,
    renderTime: 0,
    memoryUsage: 0,
    timestamp: new Date(),
  });

  const [apiTimes, setApiTimes] = React.useState<Map<string, number[]>>(new Map());

  // Track API call
  const trackApiCall = React.useCallback((endpoint: string, duration: number, success: boolean) => {
    setApiTimes(prev => {
      const times = prev.get(endpoint) || [];
      times.push(duration);
      // Keep only last 100 calls per endpoint
      if (times.length > 100) times.shift();
      const newMap = new Map(prev);
      newMap.set(endpoint, times);
      return newMap;
    });

    // Update metrics
    setMetrics(prev => {
      const allTimes: number[] = [];
      let slowestEndpoint = prev.slowestEndpoint;
      let slowestTime = prev.slowestTime;

      apiTimes.forEach((times, endpoint) => {
        allTimes.push(...times);
        const maxTime = Math.max(...times);
        if (maxTime > slowestTime) {
          slowestEndpoint = endpoint;
          slowestTime = maxTime;
        }
      });

      const avgResponseTime = allTimes.length > 0
        ? allTimes.reduce((a, b) => a + b, 0) / allTimes.length
        : 0;

      return {
        ...prev,
        avgResponseTime,
        slowestEndpoint,
        slowestTime,
        totalRequests: prev.totalRequests + 1,
        errorRate: success ? prev.errorRate : prev.errorRate + 1,
        timestamp: new Date(),
      };
    });
  }, [apiTimes]);

  // Track component render
  const trackRender = React.useCallback((duration: number) => {
    setMetrics(prev => ({
      ...prev,
      renderTime: duration,
      timestamp: new Date(),
    }));
  }, []);

  // Get memory usage if available
  React.useEffect(() => {
    if (typeof window !== "undefined" && "memory" in performance) {
      const memory = (performance as unknown as { memory?: { usedJSHeapSize: number } }).memory;
      if (memory) {
        setMetrics(prev => ({
          ...prev,
          memoryUsage: memory.usedJSHeapSize / 1048576, // Convert to MB
        }));
      }
    }
  }, []);

  return {
    metrics,
    trackApiCall,
    trackRender,
  };
}

// Performance Monitor Component
export interface PerformanceMonitorProps {
  className?: string;
  refreshInterval?: number;
}

export const PerformanceMonitor: React.FC<PerformanceMonitorProps> = ({
  className,
}) => {
  const { metrics } = usePerformanceMonitoring();
  const [isMinimized, setIsMinimized] = React.useState(false);

  const getStatusColor = (value: number, thresholds: { good: number; warning: number }) => {
    if (value <= thresholds.good) return "text-success";
    if (value <= thresholds.warning) return "text-warning";
    return "text-danger";
  };

  if (isMinimized) {
    return (
      <button
        onClick={() => setIsMinimized(false)}
        className="fixed bottom-4 right-4 bg-base-100 border border-border rounded-full p-3 shadow-aws-raised hover:shadow-aws-sticky transition-shadow z-50"
        aria-label="Show performance monitor"
      >
        <Activity className="h-5 w-5 text-accent" />
      </button>
    );
  }

  return (
    <div className={cn("fixed bottom-4 right-4 bg-base-100 border border-border rounded-aws-container shadow-aws-modal z-50 w-80", className)}>
      <div className="flex items-center justify-between px-4 py-3 border-b border-border">
        <div className="flex items-center gap-2">
          <Activity className="h-4 w-4 text-accent" />
          <h3 className="text-sm font-semibold text-text">Performance Monitor</h3>
        </div>
        <button
          onClick={() => setIsMinimized(true)}
          className="text-text-muted hover:text-text text-xs"
          aria-label="Minimize"
        >
          Minimize
        </button>
      </div>

      <div className="p-4 space-y-3">
        {/* Average Response Time */}
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <Clock className="h-4 w-4 text-text-muted" />
            <span className="text-xs text-text-muted">Avg Response</span>
          </div>
          <span className={cn("text-sm font-semibold", getStatusColor(metrics.avgResponseTime, { good: 200, warning: 500 }))}>
            {metrics.avgResponseTime.toFixed(0)}ms
          </span>
        </div>

        {/* Slowest Endpoint */}
        {metrics.slowestEndpoint && (
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <TrendingDown className="h-4 w-4 text-text-muted" />
              <span className="text-xs text-text-muted">Slowest</span>
            </div>
            <div className="text-right">
              <div className={cn("text-sm font-semibold", getStatusColor(metrics.slowestTime, { good: 300, warning: 1000 }))}>
                {metrics.slowestTime.toFixed(0)}ms
              </div>
              <div className="text-xs text-text-muted truncate max-w-[150px]">
                {metrics.slowestEndpoint}
              </div>
            </div>
          </div>
        )}

        {/* Error Rate */}
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <AlertTriangle className="h-4 w-4 text-text-muted" />
            <span className="text-xs text-text-muted">Errors</span>
          </div>
          <span className={cn("text-sm font-semibold", metrics.errorRate > 0 ? "text-danger" : "text-success")}>
            {metrics.errorRate}
          </span>
        </div>

        {/* Total Requests */}
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <TrendingUp className="h-4 w-4 text-text-muted" />
            <span className="text-xs text-text-muted">Requests</span>
          </div>
          <span className="text-sm font-semibold text-text">
            {metrics.totalRequests}
          </span>
        </div>

        {/* Memory Usage */}
        {metrics.memoryUsage > 0 && (
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <Activity className="h-4 w-4 text-text-muted" />
              <span className="text-xs text-text-muted">Memory</span>
            </div>
            <span className="text-sm font-semibold text-text">
              {metrics.memoryUsage.toFixed(1)} MB
            </span>
          </div>
        )}

        {/* Status Indicator */}
        <div className="pt-2 border-t border-border">
          <div className="flex items-center gap-2">
            {metrics.avgResponseTime <= 200 && metrics.errorRate === 0 ? (
              <>
                <CheckCircle className="h-4 w-4 text-success" />
                <span className="text-xs text-success font-medium">Excellent Performance</span>
              </>
            ) : metrics.avgResponseTime <= 500 && metrics.errorRate < 5 ? (
              <>
                <Clock className="h-4 w-4 text-warning" />
                <span className="text-xs text-warning font-medium">Good Performance</span>
              </>
            ) : (
              <>
                <AlertTriangle className="h-4 w-4 text-danger" />
                <span className="text-xs text-danger font-medium">Performance Issues</span>
              </>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

// Performance Context
interface PerformanceContextValue {
  trackApiCall: (endpoint: string, duration: number, success: boolean) => void;
  trackRender: (duration: number) => void;
  metrics: PerformanceMetrics;
}

const PerformanceContext = React.createContext<PerformanceContextValue | undefined>(undefined);

export const usePerformance = () => {
  const context = React.useContext(PerformanceContext);
  if (!context) {
    throw new Error("usePerformance must be used within PerformanceProvider");
  }
  return context;
};

export interface PerformanceProviderProps {
  children: React.ReactNode;
  enableMonitor?: boolean;
}

export const PerformanceProvider: React.FC<PerformanceProviderProps> = ({ 
  children, 
  enableMonitor = false 
}) => {
  const { metrics, trackApiCall, trackRender } = usePerformanceMonitoring();

  const value = React.useMemo(
    () => ({ metrics, trackApiCall, trackRender }),
    [metrics, trackApiCall, trackRender]
  );

  return (
    <PerformanceContext.Provider value={value}>
      {children}
      {enableMonitor && <PerformanceMonitor />}
    </PerformanceContext.Provider>
  );
};
