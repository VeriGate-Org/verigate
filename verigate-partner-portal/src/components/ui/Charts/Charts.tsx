import * as React from "react";
import { cn } from "@/lib/cn";

// Simple chart components using CSS and SVG

interface MetricCardProps {
  title: string;
  value: string | number;
  change?: {
    value: number;
    period: string;
    type: "increase" | "decrease" | "neutral";
  };
  icon?: React.ComponentType<{ className?: string }>;
  className?: string;
}

export const MetricCard: React.FC<MetricCardProps> = ({
  title,
  value,
  change,
  icon: Icon,
  className,
}) => {
  const [isClient, setIsClient] = React.useState(false);

  // Detect client-side rendering
  React.useEffect(() => {
    setIsClient(true);
  }, []);

  const getChangeColor = (type: string) => {
    switch (type) {
      case "increase":
        return "text-success";
      case "decrease":
        return "text-danger";
      default:
        return "text-text-muted";
    }
  };

  // Format value consistently
  const displayValue = React.useMemo(() => {
    if (!isClient) {
      // Server-side: render raw value
      return typeof value === "number" ? value : value;
    }
    // Client-side: format numbers with locale
    return typeof value === "number" ? value.toLocaleString() : value;
  }, [value, isClient]);

  return (
    <div className={cn("console-card", className)}>
      <div className="console-card-body">
        <div className="flex items-center justify-between mb-2">
          <h3 className="text-xs font-medium text-text-muted uppercase tracking-wide">
            {title}
          </h3>
          {Icon && <Icon className="h-4 w-4 text-text-muted" />}
        </div>
        
        <div className="text-2xl font-semibold text-text mb-1">
          {displayValue}
        </div>
        
        {change && (
          <div className={cn("text-xs", getChangeColor(change.type))}>
            {change.type === "increase" && "+"}
            {change.value}% {change.period}
          </div>
        )}
      </div>
    </div>
  );
};

interface TrendChartProps {
  data: Array<{ label: string; value: number }>;
  className?: string;
  height?: number;
}

export const TrendChart: React.FC<TrendChartProps> = ({
  data,
  className,
  height = 200,
}) => {
  const maxValue = Math.max(...data.map(d => d.value));
  const minValue = Math.min(...data.map(d => d.value));
  const range = maxValue - minValue || 1;

  const points = data
    .map((d, i) => {
      const x = (i / (data.length - 1)) * 100;
      const y = 100 - ((d.value - minValue) / range) * 100;
      return `${x},${y}`;
    })
    .join(" ");

  return (
    <div className={cn("console-card", className)}>
      <div className="console-card-body">
        <svg 
          width="100%" 
          height={height} 
          viewBox="0 0 100 100" 
          preserveAspectRatio="none"
          className="overflow-visible"
        >
          {/* Grid lines */}
          <defs>
            <pattern id="grid" width="10" height="10" patternUnits="userSpaceOnUse">
              <path
                d="M 10 0 L 0 0 0 10"
                fill="none"
                stroke="var(--color-border)"
                strokeWidth="0.5"
              />
            </pattern>
          </defs>
          <rect width="100" height="100" fill="url(#grid)" />
          
          {/* Trend line */}
          <polyline
            fill="none"
            stroke="var(--color-accent)"
            strokeWidth="2"
            points={points}
            vectorEffect="non-scaling-stroke"
          />
          
          {/* Data points */}
          {data.map((d, i) => {
            const x = (i / (data.length - 1)) * 100;
            const y = 100 - ((d.value - minValue) / range) * 100;
            return (
              <circle
                key={i}
                cx={x}
                cy={y}
                r="2"
                fill="var(--color-accent)"
                className="hover:r-3 transition-all"
              />
            );
          })}
        </svg>
        
        {/* Labels */}
        <div className="flex justify-between mt-2 text-xs text-text-muted">
          {data.map((d, i) => (
            <span key={i} className={i % 2 === 0 ? "" : "hidden sm:inline"}>
              {d.label}
            </span>
          ))}
        </div>
      </div>
    </div>
  );
};

interface DonutChartProps {
  data: Array<{
    label: string;
    value: number;
    color: string;
  }>;
  className?: string;
  size?: number;
}

export const DonutChart: React.FC<DonutChartProps> = ({
  data,
  className,
  size = 200,
}) => {
  const total = data.reduce((sum, d) => sum + d.value, 0);
  const radius = 40;
  const strokeWidth = 8;
  const innerRadius = radius - strokeWidth;
  const circumference = 2 * Math.PI * innerRadius;

  const [isClient, setIsClient] = React.useState(false);

  // Detect client-side rendering
  React.useEffect(() => {
    setIsClient(true);
  }, []);

  // Format values consistently
  const formattedTotal = React.useMemo(() => {
    return isClient ? total.toLocaleString() : total;
  }, [total, isClient]);

  const formattedValues = React.useMemo(() => {
    return data.map(d => isClient ? d.value.toLocaleString() : d.value);
  }, [data, isClient]);

  let currentAngle = 0;

  return (
    <div className={cn("console-card", className)}>
      <div className="console-card-body flex items-center gap-4">
        <div className="relative">
          <svg width={size} height={size} className="transform -rotate-90">
            <circle
              cx={size / 2}
              cy={size / 2}
              r={innerRadius}
              fill="none"
              stroke="var(--color-border)"
              strokeWidth={strokeWidth}
            />
            
            {data.map((segment, index) => {
              const percentage = segment.value / total;
              const strokeDasharray = `${percentage * circumference} ${circumference}`;
              const strokeDashoffset = -currentAngle * circumference;
              
              currentAngle += percentage;
              
              return (
                <circle
                  key={index}
                  cx={size / 2}
                  cy={size / 2}
                  r={innerRadius}
                  fill="none"
                  stroke={segment.color}
                  strokeWidth={strokeWidth}
                  strokeDasharray={strokeDasharray}
                  strokeDashoffset={strokeDashoffset}
                  className="transition-all duration-300"
                />
              );
            })}
          </svg>
          
          {/* Center text */}
          <div className="absolute inset-0 flex items-center justify-center">
            <div className="text-center">
              <div className="text-lg font-semibold">{formattedTotal}</div>
              <div className="text-xs text-text-muted">Total</div>
            </div>
          </div>
        </div>
        
        {/* Legend */}
        <div className="space-y-2">
          {data.map((segment, index) => {
            const percentage = ((segment.value / total) * 100).toFixed(1);
            return (
              <div key={index} className="flex items-center gap-2">
                <div 
                  className="w-3 h-3 rounded-full"
                  style={{ backgroundColor: segment.color }}
                />
                <span className="text-aws-body text-text">{segment.label}</span>
                <span className="text-aws-body text-text-muted">
                  {formattedValues[index]} ({percentage}%)
                </span>
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
};

interface ProgressBarProps {
  value: number;
  max: number;
  label?: string;
  color?: string;
  className?: string;
}

export const ProgressBar: React.FC<ProgressBarProps> = ({
  value,
  max,
  label,
  color = "var(--color-accent)",
  className,
}) => {
  const percentage = Math.min((value / max) * 100, 100);
  const [isClient, setIsClient] = React.useState(false);

  // Detect client-side rendering
  React.useEffect(() => {
    setIsClient(true);
  }, []);

  // Format values consistently
  const formattedValue = React.useMemo(() => {
    return isClient ? value.toLocaleString() : value;
  }, [value, isClient]);

  const formattedMax = React.useMemo(() => {
    return isClient ? max.toLocaleString() : max;
  }, [max, isClient]);

  return (
    <div className={cn("space-y-1", className)}>
      {label && (
        <div className="flex justify-between text-aws-body">
          <span className="text-text">{label}</span>
          <span className="text-text-muted">
            {formattedValue} / {formattedMax}
          </span>
        </div>
      )}
      <div className="w-full bg-border rounded-full h-2">
        <div
          className="h-2 rounded-full transition-all duration-300"
          style={{
            width: `${percentage}%`,
            backgroundColor: color,
          }}
        />
      </div>
    </div>
  );
};

// Real-time data hook
export function useRealTimeData<T>(
  initialData: T,
  fetchData: () => Promise<T>,
  interval: number = 30000 // 30 seconds
) {
  const [data, setData] = React.useState<T>(initialData);
  const [loading, setLoading] = React.useState(false);
  const [error, setError] = React.useState<string | null>(null);
  const [lastUpdated, setLastUpdated] = React.useState<Date | null>(null);

  React.useEffect(() => {
    const updateData = async () => {
      try {
        setLoading(true);
        setError(null);
        const newData = await fetchData();
        setData(newData);
        setLastUpdated(new Date());
      } catch (err) {
        setError(err instanceof Error ? err.message : "Failed to update data");
      } finally {
        setLoading(false);
      }
    };

    const intervalId = setInterval(updateData, interval);
    updateData(); // Initial fetch

    return () => clearInterval(intervalId);
  }, [fetchData, interval]);

  return { data, loading, error, lastUpdated: lastUpdated || new Date(0) };
}