interface Props {
  className?: string;
  animate?: boolean;
}

export const ComplianceFlow = ({ className = "", animate = false }: Props) => (
  <svg viewBox="0 0 400 300" fill="none" xmlns="http://www.w3.org/2000/svg" className={className}>
    <defs>
      <linearGradient id="complianceGradMain" x1="0%" y1="0%" x2="100%" y2="100%">
        <stop offset="0%" stopColor="#0A2540" />
        <stop offset="100%" stopColor="#00B4D8" />
      </linearGradient>
      <linearGradient id="complianceGradBeam" x1="0%" y1="0%" x2="100%" y2="0%">
        <stop offset="0%" stopColor="#0A2540" />
        <stop offset="50%" stopColor="#00B4D8" />
        <stop offset="100%" stopColor="#0A2540" />
      </linearGradient>
      <linearGradient id="complianceGradDoc" x1="0%" y1="0%" x2="0%" y2="100%">
        <stop offset="0%" stopColor="#00B4D8" stopOpacity="0.3" />
        <stop offset="100%" stopColor="#0A2540" stopOpacity="0.1" />
      </linearGradient>
      <radialGradient id="complianceGradGlow" cx="50%" cy="40%" r="40%">
        <stop offset="0%" stopColor="#00B4D8" stopOpacity="0.15" />
        <stop offset="100%" stopColor="#00B4D8" stopOpacity="0" />
      </radialGradient>
      <filter id="complianceGlow">
        <feGaussianBlur stdDeviation="2" result="blur" />
        <feMerge>
          <feMergeNode in="blur" />
          <feMergeNode in="SourceGraphic" />
        </feMerge>
      </filter>
    </defs>

    {/* Background glow */}
    <ellipse cx="200" cy="130" rx="150" ry="100" fill="url(#complianceGradGlow)" />

    {/* Balance pillar */}
    <rect x="196" y="130" width="8" height="120" rx="2" fill="#0A2540" fillOpacity="0.6" />

    {/* Pillar base */}
    <ellipse cx="200" cy="255" rx="40" ry="8" fill="#0A2540" fillOpacity="0.15" />
    <rect x="170" y="245" width="60" height="12" rx="3" fill="#0A2540" fillOpacity="0.3" />

    {/* Balance beam */}
    <line x1="70" y1="125" x2="330" y2="125" stroke="url(#complianceGradBeam)" strokeWidth="3" strokeLinecap="round" />

    {/* Central pivot circle */}
    <circle cx="200" cy="125" r="10" fill="#0A2540" stroke="#00B4D8" strokeWidth="2" />
    <circle cx="200" cy="125" r="4" fill="#00B4D8" filter="url(#complianceGlow)">
      {animate && <animate attributeName="r" values="3;5;3" dur="2s" repeatCount="indefinite" />}
    </circle>

    {/* Left chain */}
    <line x1="90" y1="125" x2="90" y2="155" stroke="#0A2540" strokeWidth="1.5" strokeOpacity="0.5" />

    {/* Left scale pan */}
    <path d="M55 155 Q55 165 90 168 Q125 165 125 155" stroke="#0A2540" strokeWidth="2" fill="url(#complianceGradDoc)" />

    {/* Left document stack */}
    <rect x="68" y="138" width="28" height="35" rx="2" fill="#0A2540" fillOpacity="0.08" stroke="#0A2540" strokeWidth="1" strokeOpacity="0.3" />
    <rect x="72" y="134" width="28" height="35" rx="2" fill="#0A2540" fillOpacity="0.1" stroke="#0A2540" strokeWidth="1" strokeOpacity="0.4" />
    <rect x="76" y="130" width="28" height="35" rx="2" fill="white" stroke="#0A2540" strokeWidth="1" strokeOpacity="0.5" />
    {/* Doc lines */}
    <line x1="81" y1="139" x2="99" y2="139" stroke="#0A2540" strokeWidth="1" strokeOpacity="0.3" />
    <line x1="81" y1="144" x2="96" y2="144" stroke="#0A2540" strokeWidth="1" strokeOpacity="0.2" />
    <line x1="81" y1="149" x2="99" y2="149" stroke="#0A2540" strokeWidth="1" strokeOpacity="0.3" />
    <line x1="81" y1="154" x2="93" y2="154" stroke="#0A2540" strokeWidth="1" strokeOpacity="0.2" />

    {/* Right chain */}
    <line x1="310" y1="125" x2="310" y2="155" stroke="#0A2540" strokeWidth="1.5" strokeOpacity="0.5" />

    {/* Right scale pan */}
    <path d="M275 155 Q275 165 310 168 Q345 165 345 155" stroke="#0A2540" strokeWidth="2" fill="url(#complianceGradDoc)" />

    {/* Right document stack */}
    <rect x="288" y="138" width="28" height="35" rx="2" fill="#0A2540" fillOpacity="0.08" stroke="#0A2540" strokeWidth="1" strokeOpacity="0.3" />
    <rect x="292" y="134" width="28" height="35" rx="2" fill="#0A2540" fillOpacity="0.1" stroke="#0A2540" strokeWidth="1" strokeOpacity="0.4" />
    <rect x="296" y="130" width="28" height="35" rx="2" fill="white" stroke="#00B4D8" strokeWidth="1" strokeOpacity="0.5" />
    {/* Doc checkmark */}
    <path d="M303 145 L310 153 L320 139" stroke="#00B4D8" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" />

    {/* Flowing curved lines - left side */}
    <path d="M30 60 Q60 80 90 100 Q100 110 90 125" stroke="#00B4D8" strokeWidth="1" strokeOpacity="0.4" fill="none" strokeDasharray={animate ? "6 4" : "none"}>
      {animate && <animate attributeName="stroke-dashoffset" from="10" to="0" dur="2s" repeatCount="indefinite" />}
    </path>
    <path d="M20 100 Q50 95 70 110 Q80 118 80 125" stroke="#00B4D8" strokeWidth="1" strokeOpacity="0.3" fill="none" strokeDasharray={animate ? "4 4" : "none"}>
      {animate && <animate attributeName="stroke-dashoffset" from="8" to="0" dur="2.5s" repeatCount="indefinite" />}
    </path>

    {/* Flowing curved lines - right side */}
    <path d="M370 55 Q340 75 310 100 Q300 110 310 125" stroke="#00B4D8" strokeWidth="1" strokeOpacity="0.4" fill="none" strokeDasharray={animate ? "6 4" : "none"}>
      {animate && <animate attributeName="stroke-dashoffset" from="10" to="0" dur="2.2s" repeatCount="indefinite" />}
    </path>
    <path d="M380 95 Q350 90 330 108 Q318 118 320 125" stroke="#00B4D8" strokeWidth="1" strokeOpacity="0.3" fill="none" strokeDasharray={animate ? "4 4" : "none"}>
      {animate && <animate attributeName="stroke-dashoffset" from="8" to="0" dur="2.7s" repeatCount="indefinite" />}
    </path>

    {/* Flow source nodes */}
    <circle cx="30" cy="60" r="4" fill="#00B4D8" fillOpacity="0.5">
      {animate && <animate attributeName="fillOpacity" values="0.3;0.7;0.3" dur="2s" repeatCount="indefinite" />}
    </circle>
    <circle cx="20" cy="100" r="3" fill="#00B4D8" fillOpacity="0.4" />
    <circle cx="370" cy="55" r="4" fill="#00B4D8" fillOpacity="0.5">
      {animate && <animate attributeName="fillOpacity" values="0.3;0.7;0.3" dur="2.3s" repeatCount="indefinite" />}
    </circle>
    <circle cx="380" cy="95" r="3" fill="#00B4D8" fillOpacity="0.4" />

    {/* Top decorative elements - paragraph symbol */}
    <text x="195" y="105" fontSize="18" fill="#0A2540" fillOpacity="0.2" fontFamily="serif">&sect;</text>

    {/* Small regulatory icons - left */}
    <rect x="35" y="185" width="18" height="22" rx="2" fill="none" stroke="#0A2540" strokeWidth="1" strokeOpacity="0.2" />
    <line x1="39" y1="192" x2="49" y2="192" stroke="#0A2540" strokeWidth="0.8" strokeOpacity="0.2" />
    <line x1="39" y1="196" x2="47" y2="196" stroke="#0A2540" strokeWidth="0.8" strokeOpacity="0.2" />
    <line x1="39" y1="200" x2="49" y2="200" stroke="#0A2540" strokeWidth="0.8" strokeOpacity="0.2" />

    {/* Small regulatory icons - right */}
    <rect x="347" y="185" width="18" height="22" rx="2" fill="none" stroke="#0A2540" strokeWidth="1" strokeOpacity="0.2" />
    <path d="M352 193 L356 197 L362 190" stroke="#00B4D8" strokeWidth="1" strokeOpacity="0.3" strokeLinecap="round" />

    {/* Decorative dots */}
    <circle cx="150" cy="90" r="2" fill="#0A2540" fillOpacity="0.15" />
    <circle cx="250" cy="85" r="2" fill="#0A2540" fillOpacity="0.15" />
    <circle cx="160" cy="210" r="1.5" fill="#00B4D8" fillOpacity="0.2" />
    <circle cx="240" cy="215" r="1.5" fill="#00B4D8" fillOpacity="0.2" />
    <circle cx="135" cy="270" r="2" fill="#0A2540" fillOpacity="0.1" />
    <circle cx="265" cy="268" r="2" fill="#0A2540" fillOpacity="0.1" />

    {/* Bottom connecting arc */}
    <path d="M90 200 Q200 240 310 200" stroke="#0A2540" strokeWidth="1" strokeOpacity="0.1" fill="none" strokeDasharray="4 4" />
  </svg>
);
