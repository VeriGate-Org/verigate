// Performance tracking utilities
export interface PerformanceEntry {
  name: string;
  duration: number;
  timestamp: number;
  success: boolean;
  metadata?: Record<string, unknown>;
}

class PerformanceTracker {
  private entries: PerformanceEntry[] = [];
  private maxEntries = 1000;

  track(entry: PerformanceEntry) {
    this.entries.push(entry);
    
    // Keep only recent entries
    if (this.entries.length > this.maxEntries) {
      this.entries = this.entries.slice(-this.maxEntries);
    }
  }

  getMetrics() {
    const durations = this.entries.map(e => e.duration);
    const successCount = this.entries.filter(e => e.success).length;
    
    return {
      count: this.entries.length,
      avgDuration: durations.length > 0 
        ? durations.reduce((a, b) => a + b, 0) / durations.length 
        : 0,
      minDuration: durations.length > 0 ? Math.min(...durations) : 0,
      maxDuration: durations.length > 0 ? Math.max(...durations) : 0,
      successRate: this.entries.length > 0 
        ? (successCount / this.entries.length) * 100 
        : 100,
    };
  }

  getEntriesByName(name: string) {
    return this.entries.filter(e => e.name === name);
  }

  clear() {
    this.entries = [];
  }
}

export const performanceTracker = new PerformanceTracker();

// Measure function execution time
export function measurePerformance<T>(
  name: string,
  fn: () => T,
  onComplete?: (entry: PerformanceEntry) => void
): T {
  const start = performance.now();
  let success = true;
  let result: T;

  try {
    result = fn();
  } catch (error) {
    success = false;
    throw error;
  } finally {
    const duration = performance.now() - start;
    const entry: PerformanceEntry = {
      name,
      duration,
      timestamp: Date.now(),
      success,
    };
    
    performanceTracker.track(entry);
    onComplete?.(entry);
  }

  return result!;
}

// Measure async function execution time
export async function measurePerformanceAsync<T>(
  name: string,
  fn: () => Promise<T>,
  onComplete?: (entry: PerformanceEntry) => void
): Promise<T> {
  const start = performance.now();
  let success = true;
  let result: T;

  try {
    result = await fn();
  } catch (error) {
    success = false;
    throw error;
  } finally {
    const duration = performance.now() - start;
    const entry: PerformanceEntry = {
      name,
      duration,
      timestamp: Date.now(),
      success,
    };
    
    performanceTracker.track(entry);
    onComplete?.(entry);
  }

  return result!;
}

// Get Web Vitals
export function getWebVitals() {
  if (typeof window === "undefined") return null;

  const navigation = performance.getEntriesByType("navigation")[0] as PerformanceNavigationTiming;
  const paint = performance.getEntriesByType("paint");

  return {
    // Time to First Byte
    ttfb: navigation ? navigation.responseStart - navigation.requestStart : 0,
    // First Contentful Paint
    fcp: paint.find(p => p.name === "first-contentful-paint")?.startTime || 0,
    // DOM Content Loaded
    dcl: navigation ? navigation.domContentLoadedEventEnd - navigation.domContentLoadedEventStart : 0,
    // Load Complete
    load: navigation ? navigation.loadEventEnd - navigation.loadEventStart : 0,
  };
}

// Report to analytics (placeholder - integrate with your analytics service)
export function reportPerformanceMetrics(metrics: Record<string, number>) {
  if (process.env.NODE_ENV === "development") {
    console.log("[Performance Metrics]", metrics);
  }
  
  // TODO: Send to your analytics service
  // Example: analytics.track('performance', metrics);
}
