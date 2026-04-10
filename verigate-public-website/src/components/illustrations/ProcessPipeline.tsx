interface Props {
  className?: string;
  animate?: boolean;
}

export const ProcessPipeline = ({ className = "", animate = false }: Props) => (
  <svg viewBox="0 0 400 300" fill="none" xmlns="http://www.w3.org/2000/svg" className={className}>
    <defs>
      <linearGradient id="pipelineGradMain" x1="0%" y1="0%" x2="100%" y2="0%">
        <stop offset="0%" stopColor="#0A2540" />
        <stop offset="100%" stopColor="#00B4D8" />
      </linearGradient>
      <linearGradient id="pipelineGradStage1" x1="0%" y1="0%" x2="100%" y2="100%">
        <stop offset="0%" stopColor="#0A2540" stopOpacity="0.8" />
        <stop offset="100%" stopColor="#0A2540" stopOpacity="0.5" />
      </linearGradient>
      <linearGradient id="pipelineGradStage2" x1="0%" y1="0%" x2="100%" y2="100%">
        <stop offset="0%" stopColor="#0A2540" stopOpacity="0.6" />
        <stop offset="100%" stopColor="#00B4D8" stopOpacity="0.4" />
      </linearGradient>
      <linearGradient id="pipelineGradStage3" x1="0%" y1="0%" x2="100%" y2="100%">
        <stop offset="0%" stopColor="#00B4D8" stopOpacity="0.5" />
        <stop offset="100%" stopColor="#00B4D8" stopOpacity="0.3" />
      </linearGradient>
      <linearGradient id="pipelineGradStage4" x1="0%" y1="0%" x2="100%" y2="100%">
        <stop offset="0%" stopColor="#00B4D8" stopOpacity="0.7" />
        <stop offset="100%" stopColor="#00B4D8" stopOpacity="0.5" />
      </linearGradient>
      <radialGradient id="pipelineGradGlow" cx="50%" cy="50%" r="50%">
        <stop offset="0%" stopColor="#00B4D8" stopOpacity="0.12" />
        <stop offset="100%" stopColor="#00B4D8" stopOpacity="0" />
      </radialGradient>
      <filter id="pipelineGlow">
        <feGaussianBlur stdDeviation="2" result="blur" />
        <feMerge>
          <feMergeNode in="blur" />
          <feMergeNode in="SourceGraphic" />
        </feMerge>
      </filter>
    </defs>

    {/* Background glow */}
    <ellipse cx="200" cy="150" rx="180" ry="80" fill="url(#pipelineGradGlow)" />

    {/* Main pipeline track */}
    <line x1="50" y1="150" x2="350" y2="150" stroke="#0A2540" strokeWidth="2" strokeOpacity="0.1" />

    {/* Connection arrows between stages */}
    {/* Arrow 1 -> 2 */}
    <path d="M108 150 L142 150" stroke="url(#pipelineGradMain)" strokeWidth="2" strokeOpacity="0.4" markerEnd="none">
      {animate && (
        <>
          <animate attributeName="strokeOpacity" values="0.2;0.6;0.2" dur="2s" repeatCount="indefinite" />
        </>
      )}
    </path>
    <polygon points="140,145 150,150 140,155" fill="#0A2540" fillOpacity="0.4">
      {animate && <animate attributeName="fillOpacity" values="0.2;0.5;0.2" dur="2s" repeatCount="indefinite" />}
    </polygon>

    {/* Arrow 2 -> 3 */}
    <path d="M208 150 L242 150" stroke="url(#pipelineGradMain)" strokeWidth="2" strokeOpacity="0.4">
      {animate && <animate attributeName="strokeOpacity" values="0.2;0.6;0.2" dur="2s" begin="0.5s" repeatCount="indefinite" />}
    </path>
    <polygon points="240,145 250,150 240,155" fill="#00B4D8" fillOpacity="0.4">
      {animate && <animate attributeName="fillOpacity" values="0.2;0.5;0.2" dur="2s" begin="0.5s" repeatCount="indefinite" />}
    </polygon>

    {/* Arrow 3 -> 4 */}
    <path d="M308 150 L342 150" stroke="url(#pipelineGradMain)" strokeWidth="2" strokeOpacity="0.4">
      {animate && <animate attributeName="strokeOpacity" values="0.2;0.6;0.2" dur="2s" begin="1s" repeatCount="indefinite" />}
    </path>
    <polygon points="340,145 350,150 340,155" fill="#00B4D8" fillOpacity="0.5">
      {animate && <animate attributeName="fillOpacity" values="0.3;0.6;0.3" dur="2s" begin="1s" repeatCount="indefinite" />}
    </polygon>

    {/* Stage 1 - Input/Collect */}
    <g>
      <circle cx="80" cy="150" r="28" fill="white" stroke="#0A2540" strokeWidth="2" strokeOpacity="0.5" />
      <circle cx="80" cy="150" r="24" fill="url(#pipelineGradStage1)" fillOpacity="0.1" />
      {/* Upload/input icon */}
      <rect x="68" y="144" width="24" height="16" rx="2" fill="none" stroke="#0A2540" strokeWidth="1.5" strokeOpacity="0.5" />
      <path d="M80 156 L80 146" stroke="#0A2540" strokeWidth="1.5" strokeOpacity="0.5" strokeLinecap="round" />
      <path d="M76 149 L80 144 L84 149" stroke="#0A2540" strokeWidth="1.5" strokeOpacity="0.5" strokeLinecap="round" strokeLinejoin="round" />
      {/* Stage label background */}
      <rect x="56" y="186" width="48" height="18" rx="4" fill="#0A2540" fillOpacity="0.06" />
      <rect x="62" y="192" width="36" height="5" rx="1.5" fill="#0A2540" fillOpacity="0.15" />
    </g>

    {/* Stage 2 - Process/Verify */}
    <g>
      <circle cx="180" cy="150" r="28" fill="white" stroke="#0A2540" strokeWidth="2" strokeOpacity="0.4" />
      <circle cx="180" cy="150" r="24" fill="url(#pipelineGradStage2)" fillOpacity="0.1" />
      {/* Gear/process icon */}
      <circle cx="180" cy="150" r="10" fill="none" stroke="#0A2540" strokeWidth="1.5" strokeOpacity="0.4" />
      <circle cx="180" cy="150" r="5" fill="none" stroke="#00B4D8" strokeWidth="1.2" strokeOpacity="0.5" />
      {/* Gear teeth */}
      <line x1="180" y1="138" x2="180" y2="142" stroke="#0A2540" strokeWidth="2" strokeOpacity="0.4" strokeLinecap="round" />
      <line x1="180" y1="158" x2="180" y2="162" stroke="#0A2540" strokeWidth="2" strokeOpacity="0.4" strokeLinecap="round" />
      <line x1="168" y1="150" x2="172" y2="150" stroke="#0A2540" strokeWidth="2" strokeOpacity="0.4" strokeLinecap="round" />
      <line x1="188" y1="150" x2="192" y2="150" stroke="#0A2540" strokeWidth="2" strokeOpacity="0.4" strokeLinecap="round" />
      <line x1="172" y1="142" x2="174.5" y2="144.5" stroke="#0A2540" strokeWidth="2" strokeOpacity="0.3" strokeLinecap="round" />
      <line x1="188" y1="158" x2="185.5" y2="155.5" stroke="#0A2540" strokeWidth="2" strokeOpacity="0.3" strokeLinecap="round" />
      <line x1="172" y1="158" x2="174.5" y2="155.5" stroke="#0A2540" strokeWidth="2" strokeOpacity="0.3" strokeLinecap="round" />
      <line x1="188" y1="142" x2="185.5" y2="144.5" stroke="#0A2540" strokeWidth="2" strokeOpacity="0.3" strokeLinecap="round" />
      {/* Stage label */}
      <rect x="156" y="186" width="48" height="18" rx="4" fill="#0A2540" fillOpacity="0.06" />
      <rect x="162" y="192" width="36" height="5" rx="1.5" fill="#0A2540" fillOpacity="0.15" />
    </g>

    {/* Stage 3 - Analyze/Score */}
    <g>
      <circle cx="280" cy="150" r="28" fill="white" stroke="#00B4D8" strokeWidth="2" strokeOpacity="0.5" />
      <circle cx="280" cy="150" r="24" fill="url(#pipelineGradStage3)" fillOpacity="0.1" />
      {/* Chart/analyze icon */}
      <line x1="268" y1="162" x2="292" y2="162" stroke="#0A2540" strokeWidth="1.2" strokeOpacity="0.4" />
      <line x1="268" y1="162" x2="268" y2="138" stroke="#0A2540" strokeWidth="1.2" strokeOpacity="0.4" />
      <polyline points="272,156 276,150 282,152 286,142 290,145" stroke="#00B4D8" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round" fill="none" />
      {/* Stage label */}
      <rect x="256" y="186" width="48" height="18" rx="4" fill="#00B4D8" fillOpacity="0.06" />
      <rect x="262" y="192" width="36" height="5" rx="1.5" fill="#00B4D8" fillOpacity="0.2" />
    </g>

    {/* Stage 4 - Output/Result */}
    <g>
      <circle cx="370" cy="150" r="24" fill="white" stroke="#00B4D8" strokeWidth="2" strokeOpacity="0.6" filter="url(#pipelineGlow)" />
      <circle cx="370" cy="150" r="20" fill="url(#pipelineGradStage4)" fillOpacity="0.15" />
      {/* Checkmark/complete icon */}
      <path d="M360 150 L367 158 L382 142" stroke="#00B4D8" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
        {animate && <animate attributeName="strokeOpacity" values="0.5;1;0.5" dur="2s" repeatCount="indefinite" />}
      </path>
      {/* Stage label */}
      <rect x="348" y="182" width="44" height="18" rx="4" fill="#00B4D8" fillOpacity="0.08" />
      <rect x="354" y="188" width="32" height="5" rx="1.5" fill="#00B4D8" fillOpacity="0.2" />
    </g>

    {/* Top decorative branch from Stage 1 */}
    <path d="M80 122 L80 95 L110 80" stroke="#0A2540" strokeWidth="1" strokeOpacity="0.15" fill="none" />
    <circle cx="110" cy="80" r="4" fill="#0A2540" fillOpacity="0.1" />
    <rect x="102" y="64" width="16" height="10" rx="2" fill="none" stroke="#0A2540" strokeWidth="0.8" strokeOpacity="0.15" />

    {/* Top branch from Stage 2 */}
    <path d="M180 122 L180 90 Q180 80 170 78 L155 78" stroke="#0A2540" strokeWidth="1" strokeOpacity="0.12" fill="none" />
    <path d="M180 90 Q180 80 190 78 L210 78" stroke="#0A2540" strokeWidth="1" strokeOpacity="0.12" fill="none" />
    <circle cx="155" cy="78" r="3" fill="#0A2540" fillOpacity="0.1" />
    <circle cx="210" cy="78" r="3" fill="#00B4D8" fillOpacity="0.15" />

    {/* Bottom branch from Stage 3 */}
    <path d="M280 178 L280 210 Q280 220 270 222 L255 222" stroke="#00B4D8" strokeWidth="1" strokeOpacity="0.15" fill="none" />
    <path d="M280 210 Q280 220 290 222 L305 222" stroke="#00B4D8" strokeWidth="1" strokeOpacity="0.15" fill="none" />
    <circle cx="255" cy="222" r="3" fill="#00B4D8" fillOpacity="0.12" />
    <circle cx="305" cy="222" r="3" fill="#00B4D8" fillOpacity="0.15" />

    {/* Progress dots on pipeline */}
    <circle cx="125" cy="150" r="2" fill="#0A2540" fillOpacity="0.2" />
    <circle cx="225" cy="150" r="2" fill="#00B4D8" fillOpacity="0.2" />
    <circle cx="325" cy="150" r="2" fill="#00B4D8" fillOpacity="0.3" />

    {/* Animated traveling dot */}
    {animate && (
      <circle r="3" fill="#00B4D8" fillOpacity="0.7" filter="url(#pipelineGlow)">
        <animateMotion dur="4s" repeatCount="indefinite" path="M50 150 L108 150 L142 150 L208 150 L242 150 L308 150 L350 150" />
      </circle>
    )}

    {/* Corner accents */}
    <path d="M20 40 L20 55 M20 40 L35 40" stroke="#0A2540" strokeWidth="1" strokeOpacity="0.1" />
    <path d="M380 40 L380 55 M380 40 L365 40" stroke="#0A2540" strokeWidth="1" strokeOpacity="0.1" />
    <path d="M20 260 L20 245 M20 260 L35 260" stroke="#0A2540" strokeWidth="1" strokeOpacity="0.1" />
    <path d="M380 260 L380 245 M380 260 L365 260" stroke="#0A2540" strokeWidth="1" strokeOpacity="0.1" />

    {/* Subtle grid lines */}
    <line x1="50" y1="100" x2="350" y2="100" stroke="#0A2540" strokeWidth="0.5" strokeOpacity="0.04" />
    <line x1="50" y1="200" x2="350" y2="200" stroke="#0A2540" strokeWidth="0.5" strokeOpacity="0.04" />
  </svg>
);
