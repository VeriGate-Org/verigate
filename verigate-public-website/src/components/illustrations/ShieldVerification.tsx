interface Props {
  className?: string;
  animate?: boolean;
}

export const ShieldVerification = ({ className = "", animate = false }: Props) => (
  <svg viewBox="0 0 400 300" fill="none" xmlns="http://www.w3.org/2000/svg" className={className}>
    <defs>
      <linearGradient id="shieldGradMain" x1="0%" y1="0%" x2="100%" y2="100%">
        <stop offset="0%" stopColor="#0A2540" />
        <stop offset="100%" stopColor="#00B4D8" />
      </linearGradient>
      <linearGradient id="shieldGradAccent" x1="0%" y1="0%" x2="0%" y2="100%">
        <stop offset="0%" stopColor="#00B4D8" />
        <stop offset="100%" stopColor="#0A2540" />
      </linearGradient>
      <radialGradient id="shieldGradGlow" cx="50%" cy="50%" r="50%">
        <stop offset="0%" stopColor="#00B4D8" stopOpacity="0.3" />
        <stop offset="100%" stopColor="#00B4D8" stopOpacity="0" />
      </radialGradient>
      <filter id="shieldGlow">
        <feGaussianBlur stdDeviation="3" result="blur" />
        <feMerge>
          <feMergeNode in="blur" />
          <feMergeNode in="SourceGraphic" />
        </feMerge>
      </filter>
    </defs>

    {/* Background glow */}
    <circle cx="200" cy="150" r="120" fill="url(#shieldGradGlow)" />

    {/* Circuit paths - left side */}
    <path d="M40 80 L80 80 L80 120 L110 120" stroke="#0A2540" strokeWidth="1.5" strokeOpacity="0.3" strokeDasharray={animate ? "4 4" : "none"}>
      {animate && <animate attributeName="stroke-dashoffset" from="8" to="0" dur="1.5s" repeatCount="indefinite" />}
    </path>
    <path d="M30 150 L70 150 L70 170 L110 170" stroke="#0A2540" strokeWidth="1.5" strokeOpacity="0.3" strokeDasharray={animate ? "4 4" : "none"}>
      {animate && <animate attributeName="stroke-dashoffset" from="8" to="0" dur="1.8s" repeatCount="indefinite" />}
    </path>
    <path d="M50 220 L90 220 L90 200 L120 200" stroke="#0A2540" strokeWidth="1.5" strokeOpacity="0.3" strokeDasharray={animate ? "4 4" : "none"}>
      {animate && <animate attributeName="stroke-dashoffset" from="8" to="0" dur="2s" repeatCount="indefinite" />}
    </path>

    {/* Circuit paths - right side */}
    <path d="M290 100 L320 100 L320 80 L360 80" stroke="#0A2540" strokeWidth="1.5" strokeOpacity="0.3" strokeDasharray={animate ? "4 4" : "none"}>
      {animate && <animate attributeName="stroke-dashoffset" from="8" to="0" dur="1.6s" repeatCount="indefinite" />}
    </path>
    <path d="M290 160 L330 160 L330 150 L370 150" stroke="#0A2540" strokeWidth="1.5" strokeOpacity="0.3" strokeDasharray={animate ? "4 4" : "none"}>
      {animate && <animate attributeName="stroke-dashoffset" from="8" to="0" dur="1.9s" repeatCount="indefinite" />}
    </path>
    <path d="M280 210 L310 210 L310 230 L350 230" stroke="#0A2540" strokeWidth="1.5" strokeOpacity="0.3" strokeDasharray={animate ? "4 4" : "none"}>
      {animate && <animate attributeName="stroke-dashoffset" from="8" to="0" dur="2.1s" repeatCount="indefinite" />}
    </path>

    {/* Data nodes - left */}
    <circle cx="40" cy="80" r="5" fill="#00B4D8" fillOpacity="0.6">
      {animate && <animate attributeName="r" values="4;6;4" dur="2s" repeatCount="indefinite" />}
    </circle>
    <circle cx="30" cy="150" r="4" fill="#00B4D8" fillOpacity="0.5">
      {animate && <animate attributeName="r" values="3;5;3" dur="2.3s" repeatCount="indefinite" />}
    </circle>
    <circle cx="50" cy="220" r="5" fill="#00B4D8" fillOpacity="0.6">
      {animate && <animate attributeName="r" values="4;6;4" dur="1.8s" repeatCount="indefinite" />}
    </circle>

    {/* Data nodes - right */}
    <circle cx="360" cy="80" r="5" fill="#00B4D8" fillOpacity="0.6">
      {animate && <animate attributeName="r" values="4;6;4" dur="2.2s" repeatCount="indefinite" />}
    </circle>
    <circle cx="370" cy="150" r="4" fill="#00B4D8" fillOpacity="0.5">
      {animate && <animate attributeName="r" values="3;5;3" dur="1.7s" repeatCount="indefinite" />}
    </circle>
    <circle cx="350" cy="230" r="5" fill="#00B4D8" fillOpacity="0.6">
      {animate && <animate attributeName="r" values="4;6;4" dur="2.5s" repeatCount="indefinite" />}
    </circle>

    {/* Shield outer shape */}
    <path
      d="M200 40 L260 65 L260 155 Q260 210 200 250 Q140 210 140 155 L140 65 Z"
      fill="url(#shieldGradMain)"
      fillOpacity="0.15"
      stroke="url(#shieldGradMain)"
      strokeWidth="2.5"
    />

    {/* Shield inner shape */}
    <path
      d="M200 60 L248 80 L248 152 Q248 198 200 232 Q152 198 152 152 L152 80 Z"
      fill="url(#shieldGradMain)"
      fillOpacity="0.08"
      stroke="#00B4D8"
      strokeWidth="1"
      strokeOpacity="0.4"
    />

    {/* Shield top horizontal accent line */}
    <line x1="170" y1="90" x2="230" y2="90" stroke="#00B4D8" strokeWidth="1" strokeOpacity="0.3" />

    {/* Checkmark */}
    <path
      d="M175 148 L192 168 L228 125"
      stroke="#00B4D8"
      strokeWidth="4"
      strokeLinecap="round"
      strokeLinejoin="round"
      filter="url(#shieldGlow)"
    >
      {animate && (
        <animate attributeName="stroke-opacity" values="0.7;1;0.7" dur="2s" repeatCount="indefinite" />
      )}
    </path>

    {/* Small circuit dots on shield border */}
    <circle cx="200" cy="40" r="3" fill="#00B4D8" />
    <circle cx="260" cy="65" r="3" fill="#00B4D8" fillOpacity="0.7" />
    <circle cx="140" cy="65" r="3" fill="#00B4D8" fillOpacity="0.7" />
    <circle cx="200" cy="250" r="3" fill="#00B4D8" fillOpacity="0.5" />

    {/* Floating data particles */}
    <circle cx="100" cy="60" r="2" fill="#00B4D8" fillOpacity="0.4">
      {animate && <animate attributeName="cy" values="60;55;60" dur="3s" repeatCount="indefinite" />}
    </circle>
    <circle cx="310" cy="50" r="2.5" fill="#00B4D8" fillOpacity="0.3">
      {animate && <animate attributeName="cy" values="50;45;50" dur="2.5s" repeatCount="indefinite" />}
    </circle>
    <circle cx="85" cy="260" r="2" fill="#0A2540" fillOpacity="0.3">
      {animate && <animate attributeName="cy" values="260;255;260" dur="3.2s" repeatCount="indefinite" />}
    </circle>
    <circle cx="330" cy="270" r="1.5" fill="#00B4D8" fillOpacity="0.3">
      {animate && <animate attributeName="cy" values="270;265;270" dur="2.8s" repeatCount="indefinite" />}
    </circle>

    {/* Small decorative hexagons */}
    <polygon points="75,120 82,116 89,120 89,128 82,132 75,128" fill="none" stroke="#0A2540" strokeWidth="1" strokeOpacity="0.2" />
    <polygon points="315,190 322,186 329,190 329,198 322,202 315,198" fill="none" stroke="#0A2540" strokeWidth="1" strokeOpacity="0.2" />

    {/* Bottom connection lines */}
    <path d="M160 260 L200 275 L240 260" stroke="#0A2540" strokeWidth="1" strokeOpacity="0.15" />
    <circle cx="160" cy="260" r="2.5" fill="#0A2540" fillOpacity="0.2" />
    <circle cx="200" cy="275" r="2.5" fill="#00B4D8" fillOpacity="0.3" />
    <circle cx="240" cy="260" r="2.5" fill="#0A2540" fillOpacity="0.2" />
  </svg>
);
