/**
 * Circular confidence score gauge with color gradient.
 * Red (0-40), Amber (40-70), Green (70-100).
 */

interface ConfidenceScoreProps {
  score: number;
  size?: number;
  label?: string;
}

export function ConfidenceScore({ score, size = 64, label }: ConfidenceScoreProps) {
  const clamped = Math.max(0, Math.min(100, Math.round(score)));
  const radius = (size - 8) / 2;
  const circumference = 2 * Math.PI * radius;
  const offset = circumference - (clamped / 100) * circumference;
  const center = size / 2;

  const color =
    clamped >= 70
      ? "var(--color-success)"
      : clamped >= 40
        ? "var(--color-warning)"
        : "var(--color-danger)";

  return (
    <div className="inline-flex flex-col items-center gap-1">
      <svg
        width={size}
        height={size}
        viewBox={`0 0 ${size} ${size}`}
        role="img"
        aria-label={`Confidence score: ${clamped}%`}
      >
        <circle
          cx={center}
          cy={center}
          r={radius}
          fill="none"
          stroke="var(--color-border)"
          strokeWidth={4}
        />
        <circle
          cx={center}
          cy={center}
          r={radius}
          fill="none"
          stroke={color}
          strokeWidth={4}
          strokeDasharray={circumference}
          strokeDashoffset={offset}
          strokeLinecap="round"
          transform={`rotate(-90 ${center} ${center})`}
          style={{ transition: "stroke-dashoffset 0.4s ease" }}
        />
        <text
          x={center}
          y={center}
          textAnchor="middle"
          dominantBaseline="central"
          fill="currentColor"
          fontSize={size * 0.25}
          fontWeight={600}
        >
          {clamped}%
        </text>
      </svg>
      {label && <span className="text-xs text-text-muted">{label}</span>}
    </div>
  );
}
