interface Props {
  className?: string;
  animate?: boolean;
}

export const FraudDetection = ({ className = "", animate = false }: Props) => (
  <svg viewBox="0 0 400 300" fill="none" xmlns="http://www.w3.org/2000/svg" className={className}>
    <defs>
      <linearGradient id="fraudGradMain" x1="0%" y1="0%" x2="100%" y2="100%">
        <stop offset="0%" stopColor="#0A2540" />
        <stop offset="100%" stopColor="#00B4D8" />
      </linearGradient>
      <linearGradient id="fraudGradLens" x1="0%" y1="0%" x2="100%" y2="100%">
        <stop offset="0%" stopColor="#00B4D8" stopOpacity="0.1" />
        <stop offset="100%" stopColor="#00B4D8" stopOpacity="0.05" />
      </linearGradient>
      <radialGradient id="fraudGradGlow" cx="50%" cy="50%" r="50%">
        <stop offset="0%" stopColor="#00B4D8" stopOpacity="0.2" />
        <stop offset="100%" stopColor="#00B4D8" stopOpacity="0" />
      </radialGradient>
      <radialGradient id="fraudGradAlert" cx="50%" cy="50%" r="50%">
        <stop offset="0%" stopColor="#FF6B6B" stopOpacity="0.8" />
        <stop offset="100%" stopColor="#FF6B6B" stopOpacity="0.3" />
      </radialGradient>
      <clipPath id="fraudLensClip">
        <circle cx="175" cy="150" r="72" />
      </clipPath>
      <filter id="fraudGlow">
        <feGaussianBlur stdDeviation="3" result="blur" />
        <feMerge>
          <feMergeNode in="blur" />
          <feMergeNode in="SourceGraphic" />
        </feMerge>
      </filter>
    </defs>

    {/* Background glow */}
    <circle cx="175" cy="150" r="110" fill="url(#fraudGradGlow)" />

    {/* Fingerprint pattern (behind magnifying glass) */}
    <g opacity="0.15">
      <path d="M175 85 Q175 85 175 85 C145 85 120 110 120 145 C120 175 140 200 170 210" stroke="#0A2540" strokeWidth="2" fill="none" />
      <path d="M175 95 C150 95 130 115 130 145 C130 170 145 192 170 200" stroke="#0A2540" strokeWidth="2" fill="none" />
      <path d="M175 105 C155 105 140 120 140 145 C140 165 150 182 168 190" stroke="#0A2540" strokeWidth="2" fill="none" />
      <path d="M175 115 C160 115 150 128 150 145 C150 160 156 172 166 180" stroke="#0A2540" strokeWidth="2" fill="none" />
      <path d="M175 125 C165 125 158 133 158 145 C158 155 162 163 168 170" stroke="#0A2540" strokeWidth="2" fill="none" />
      <path d="M175 135 C170 135 166 139 166 145 C166 150 168 155 172 158" stroke="#0A2540" strokeWidth="2" fill="none" />
      <path d="M175 85 C205 85 230 110 230 145 C230 175 210 200 180 210" stroke="#0A2540" strokeWidth="2" fill="none" />
      <path d="M175 95 C200 95 220 115 220 145 C220 170 205 192 180 200" stroke="#0A2540" strokeWidth="2" fill="none" />
      <path d="M175 105 C195 105 210 120 210 145 C210 165 200 182 182 190" stroke="#0A2540" strokeWidth="2" fill="none" />
      <path d="M175 115 C190 115 200 128 200 145 C200 160 194 172 184 180" stroke="#0A2540" strokeWidth="2" fill="none" />
    </g>

    {/* Magnifying glass lens - outer ring */}
    <circle cx="175" cy="150" r="75" stroke="url(#fraudGradMain)" strokeWidth="4" fill="none" />
    <circle cx="175" cy="150" r="72" fill="url(#fraudGradLens)" />

    {/* Enhanced fingerprint inside lens */}
    <g clipPath="url(#fraudLensClip)">
      <path d="M175 90 C140 90 115 118 115 150 C115 182 135 207 170 215" stroke="#0A2540" strokeWidth="2.5" fill="none" strokeOpacity="0.4" />
      <path d="M175 100 C148 100 125 123 125 150 C125 177 140 198 168 206" stroke="#0A2540" strokeWidth="2.5" fill="none" strokeOpacity="0.4" />
      <path d="M175 110 C153 110 135 128 135 150 C135 170 148 188 166 196" stroke="#00B4D8" strokeWidth="2" fill="none" strokeOpacity="0.6" />
      <path d="M175 120 C158 120 145 133 145 150 C145 165 153 178 165 185" stroke="#00B4D8" strokeWidth="2" fill="none" strokeOpacity="0.5" />
      <path d="M175 130 C163 130 155 138 155 150 C155 160 160 168 168 174" stroke="#00B4D8" strokeWidth="2" fill="none" strokeOpacity="0.4" />
      <path d="M175 90 C210 90 235 118 235 150 C235 182 215 207 180 215" stroke="#0A2540" strokeWidth="2.5" fill="none" strokeOpacity="0.4" />
      <path d="M175 100 C202 100 225 123 225 150 C225 177 210 198 182 206" stroke="#0A2540" strokeWidth="2.5" fill="none" strokeOpacity="0.4" />
      <path d="M175 110 C197 110 215 128 215 150 C215 170 202 188 184 196" stroke="#00B4D8" strokeWidth="2" fill="none" strokeOpacity="0.6" />
      <path d="M175 120 C192 120 205 133 205 150 C205 165 197 178 185 185" stroke="#00B4D8" strokeWidth="2" fill="none" strokeOpacity="0.5" />

      {/* Highlighted anomaly area */}
      <rect x="148" y="138" width="30" height="22" rx="3" fill="#FF6B6B" fillOpacity="0.15" stroke="#FF6B6B" strokeWidth="1" strokeOpacity="0.4" strokeDasharray="3 2">
        {animate && <animate attributeName="strokeOpacity" values="0.2;0.6;0.2" dur="1.5s" repeatCount="indefinite" />}
      </rect>
    </g>

    {/* Magnifying glass inner ring highlight */}
    <circle cx="175" cy="150" r="70" stroke="#00B4D8" strokeWidth="1" strokeOpacity="0.3" />

    {/* Lens reflection */}
    <path d="M135 115 Q145 105 160 100" stroke="white" strokeWidth="2" strokeOpacity="0.2" strokeLinecap="round" fill="none" />

    {/* Magnifying glass handle */}
    <line x1="230" y1="205" x2="290" y2="260" stroke="#0A2540" strokeWidth="8" strokeLinecap="round" />
    <line x1="230" y1="205" x2="290" y2="260" stroke="url(#fraudGradMain)" strokeWidth="5" strokeLinecap="round" />

    {/* Alert badge - top right */}
    <g>
      <circle cx="265" cy="80" r="18" fill="#FF6B6B" fillOpacity="0.15" stroke="#FF6B6B" strokeWidth="1.5" strokeOpacity="0.6">
        {animate && <animate attributeName="r" values="17;19;17" dur="1.5s" repeatCount="indefinite" />}
      </circle>
      <text x="265" y="86" textAnchor="middle" fontSize="18" fontWeight="bold" fill="#FF6B6B" fillOpacity="0.8">!</text>
    </g>

    {/* Alert badge - bottom left */}
    <g>
      <circle cx="70" cy="230" r="14" fill="#FF6B6B" fillOpacity="0.1" stroke="#FF6B6B" strokeWidth="1" strokeOpacity="0.4">
        {animate && <animate attributeName="strokeOpacity" values="0.2;0.5;0.2" dur="2s" repeatCount="indefinite" />}
      </circle>
      <text x="70" y="235" textAnchor="middle" fontSize="14" fontWeight="bold" fill="#FF6B6B" fillOpacity="0.6">!</text>
    </g>

    {/* Scan lines across lens */}
    {animate && (
      <line x1="103" y1="150" x2="247" y2="150" stroke="#00B4D8" strokeWidth="1" strokeOpacity="0.3">
        <animate attributeName="y1" values="100;200;100" dur="3s" repeatCount="indefinite" />
        <animate attributeName="y2" values="100;200;100" dur="3s" repeatCount="indefinite" />
      </line>
    )}

    {/* Data analysis nodes floating around */}
    <circle cx="320" cy="50" r="3" fill="#00B4D8" fillOpacity="0.4">
      {animate && <animate attributeName="fillOpacity" values="0.2;0.6;0.2" dur="2s" repeatCount="indefinite" />}
    </circle>
    <circle cx="340" cy="120" r="2.5" fill="#0A2540" fillOpacity="0.3" />
    <circle cx="50" cy="80" r="3" fill="#00B4D8" fillOpacity="0.3">
      {animate && <animate attributeName="fillOpacity" values="0.2;0.5;0.2" dur="2.5s" repeatCount="indefinite" />}
    </circle>
    <circle cx="35" cy="170" r="2" fill="#0A2540" fillOpacity="0.2" />

    {/* Connecting analysis lines */}
    <path d="M250 80 L265 80" stroke="#FF6B6B" strokeWidth="0.8" strokeOpacity="0.3" />
    <path d="M283 80 L320 50" stroke="#00B4D8" strokeWidth="0.8" strokeOpacity="0.2" />
    <path d="M320 50 L340 120" stroke="#00B4D8" strokeWidth="0.8" strokeOpacity="0.15" />

    {/* Small crosshair at anomaly */}
    <line x1="163" y1="143" x2="163" y2="155" stroke="#FF6B6B" strokeWidth="0.8" strokeOpacity="0.5" />
    <line x1="157" y1="149" x2="169" y2="149" stroke="#FF6B6B" strokeWidth="0.8" strokeOpacity="0.5" />

    {/* Corner decorative brackets */}
    <path d="M30 30 L30 50 M30 30 L50 30" stroke="#0A2540" strokeWidth="1.5" strokeOpacity="0.15" />
    <path d="M370 30 L370 50 M370 30 L350 30" stroke="#0A2540" strokeWidth="1.5" strokeOpacity="0.15" />
    <path d="M30 270 L30 250 M30 270 L50 270" stroke="#0A2540" strokeWidth="1.5" strokeOpacity="0.15" />
    <path d="M370 270 L370 250 M370 270 L350 270" stroke="#0A2540" strokeWidth="1.5" strokeOpacity="0.15" />
  </svg>
);
