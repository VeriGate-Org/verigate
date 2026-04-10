interface Props {
  className?: string;
  animate?: boolean;
}

export const IndustryNetwork = ({ className = "", animate = false }: Props) => (
  <svg viewBox="0 0 400 300" fill="none" xmlns="http://www.w3.org/2000/svg" className={className}>
    <defs>
      <linearGradient id="industryGradMain" x1="0%" y1="0%" x2="100%" y2="100%">
        <stop offset="0%" stopColor="#0A2540" />
        <stop offset="100%" stopColor="#00B4D8" />
      </linearGradient>
      <linearGradient id="industryGradBuilding" x1="0%" y1="0%" x2="0%" y2="100%">
        <stop offset="0%" stopColor="#0A2540" stopOpacity="0.7" />
        <stop offset="100%" stopColor="#0A2540" stopOpacity="0.4" />
      </linearGradient>
      <radialGradient id="industryGradGlow" cx="50%" cy="50%" r="50%">
        <stop offset="0%" stopColor="#00B4D8" stopOpacity="0.15" />
        <stop offset="100%" stopColor="#00B4D8" stopOpacity="0" />
      </radialGradient>
      <filter id="industryGlow">
        <feGaussianBlur stdDeviation="2" result="blur" />
        <feMerge>
          <feMergeNode in="blur" />
          <feMergeNode in="SourceGraphic" />
        </feMerge>
      </filter>
    </defs>

    {/* Background glow */}
    <circle cx="200" cy="150" r="130" fill="url(#industryGradGlow)" />

    {/* Network connection lines */}
    <line x1="80" y1="90" x2="200" y2="75" stroke="#00B4D8" strokeWidth="1" strokeOpacity="0.25" strokeDasharray={animate ? "6 4" : "none"}>
      {animate && <animate attributeName="stroke-dashoffset" from="10" to="0" dur="2s" repeatCount="indefinite" />}
    </line>
    <line x1="320" y1="80" x2="200" y2="75" stroke="#00B4D8" strokeWidth="1" strokeOpacity="0.25" strokeDasharray={animate ? "6 4" : "none"}>
      {animate && <animate attributeName="stroke-dashoffset" from="10" to="0" dur="2.2s" repeatCount="indefinite" />}
    </line>
    <line x1="80" y1="90" x2="60" y2="200" stroke="#00B4D8" strokeWidth="1" strokeOpacity="0.2" strokeDasharray={animate ? "6 4" : "none"}>
      {animate && <animate attributeName="stroke-dashoffset" from="10" to="0" dur="2.5s" repeatCount="indefinite" />}
    </line>
    <line x1="320" y1="80" x2="340" y2="190" stroke="#00B4D8" strokeWidth="1" strokeOpacity="0.2" strokeDasharray={animate ? "6 4" : "none"}>
      {animate && <animate attributeName="stroke-dashoffset" from="10" to="0" dur="2.3s" repeatCount="indefinite" />}
    </line>
    <line x1="60" y1="200" x2="200" y2="230" stroke="#00B4D8" strokeWidth="1" strokeOpacity="0.2" strokeDasharray={animate ? "6 4" : "none"}>
      {animate && <animate attributeName="stroke-dashoffset" from="10" to="0" dur="2.7s" repeatCount="indefinite" />}
    </line>
    <line x1="340" y1="190" x2="200" y2="230" stroke="#00B4D8" strokeWidth="1" strokeOpacity="0.2" strokeDasharray={animate ? "6 4" : "none"}>
      {animate && <animate attributeName="stroke-dashoffset" from="10" to="0" dur="2.6s" repeatCount="indefinite" />}
    </line>
    <line x1="200" y1="75" x2="200" y2="230" stroke="#00B4D8" strokeWidth="1" strokeOpacity="0.15" />
    <line x1="80" y1="90" x2="340" y2="190" stroke="#00B4D8" strokeWidth="0.8" strokeOpacity="0.1" />
    <line x1="320" y1="80" x2="60" y2="200" stroke="#00B4D8" strokeWidth="0.8" strokeOpacity="0.1" />

    {/* Building 1 - Center top (tall office) */}
    <g>
      <rect x="185" y="42" width="30" height="50" rx="2" fill="url(#industryGradBuilding)" />
      <rect x="189" y="47" width="5" height="5" rx="0.5" fill="#00B4D8" fillOpacity="0.4" />
      <rect x="197" y="47" width="5" height="5" rx="0.5" fill="#00B4D8" fillOpacity="0.3" />
      <rect x="206" y="47" width="5" height="5" rx="0.5" fill="#00B4D8" fillOpacity="0.4" />
      <rect x="189" y="55" width="5" height="5" rx="0.5" fill="#00B4D8" fillOpacity="0.3" />
      <rect x="197" y="55" width="5" height="5" rx="0.5" fill="#00B4D8" fillOpacity="0.5" />
      <rect x="206" y="55" width="5" height="5" rx="0.5" fill="#00B4D8" fillOpacity="0.3" />
      <rect x="189" y="63" width="5" height="5" rx="0.5" fill="#00B4D8" fillOpacity="0.4" />
      <rect x="197" y="63" width="5" height="5" rx="0.5" fill="#00B4D8" fillOpacity="0.3" />
      <rect x="206" y="63" width="5" height="5" rx="0.5" fill="#00B4D8" fillOpacity="0.4" />
      <rect x="189" y="71" width="5" height="5" rx="0.5" fill="#00B4D8" fillOpacity="0.2" />
      <rect x="197" y="71" width="5" height="5" rx="0.5" fill="#00B4D8" fillOpacity="0.4" />
      <rect x="206" y="71" width="5" height="5" rx="0.5" fill="#00B4D8" fillOpacity="0.2" />
    </g>

    {/* Building 2 - Top left (bank) */}
    <g>
      <rect x="62" y="65" width="36" height="38" rx="2" fill="url(#industryGradBuilding)" />
      <path d="M62 65 L80 52 L98 65" fill="#0A2540" fillOpacity="0.6" />
      <line x1="72" y1="65" x2="72" y2="103" stroke="#00B4D8" strokeWidth="2" strokeOpacity="0.3" />
      <line x1="80" y1="65" x2="80" y2="103" stroke="#00B4D8" strokeWidth="2" strokeOpacity="0.3" />
      <line x1="88" y1="65" x2="88" y2="103" stroke="#00B4D8" strokeWidth="2" strokeOpacity="0.3" />
    </g>

    {/* Building 3 - Top right (modern) */}
    <g>
      <rect x="305" y="55" width="30" height="42" rx="3" fill="url(#industryGradBuilding)" />
      <rect x="308" y="60" width="24" height="8" rx="1" fill="#00B4D8" fillOpacity="0.2" />
      <rect x="308" y="72" width="24" height="8" rx="1" fill="#00B4D8" fillOpacity="0.15" />
      <rect x="308" y="84" width="24" height="8" rx="1" fill="#00B4D8" fillOpacity="0.2" />
    </g>

    {/* Building 4 - Bottom left (hospital/facility) */}
    <g>
      <rect x="42" y="178" width="36" height="35" rx="2" fill="url(#industryGradBuilding)" />
      <rect x="56" y="173" width="8" height="40" rx="1" fill="#0A2540" fillOpacity="0.5" />
      <line x1="56" y1="190" x2="64" y2="190" stroke="#00B4D8" strokeWidth="1.5" strokeOpacity="0.5" />
      <line x1="60" y1="186" x2="60" y2="194" stroke="#00B4D8" strokeWidth="1.5" strokeOpacity="0.5" />
      <rect x="46" y="186" width="6" height="8" rx="0.5" fill="#00B4D8" fillOpacity="0.2" />
      <rect x="68" y="186" width="6" height="8" rx="0.5" fill="#00B4D8" fillOpacity="0.2" />
    </g>

    {/* Building 5 - Bottom right (warehouse) */}
    <g>
      <rect x="322" y="172" width="38" height="30" rx="2" fill="url(#industryGradBuilding)" />
      <path d="M322 172 L341 160 L360 172" fill="#0A2540" fillOpacity="0.5" />
      <rect x="336" y="186" width="10" height="16" rx="1" fill="#00B4D8" fillOpacity="0.2" />
      <rect x="326" y="178" width="7" height="7" rx="0.5" fill="#00B4D8" fillOpacity="0.15" />
      <rect x="350" y="178" width="7" height="7" rx="0.5" fill="#00B4D8" fillOpacity="0.15" />
    </g>

    {/* Building 6 - Bottom center */}
    <g>
      <rect x="183" y="210" width="34" height="36" rx="2" fill="url(#industryGradBuilding)" />
      <rect x="187" y="215" width="6" height="6" rx="0.5" fill="#00B4D8" fillOpacity="0.3" />
      <rect x="197" y="215" width="6" height="6" rx="0.5" fill="#00B4D8" fillOpacity="0.3" />
      <rect x="207" y="215" width="6" height="6" rx="0.5" fill="#00B4D8" fillOpacity="0.3" />
      <rect x="187" y="225" width="6" height="6" rx="0.5" fill="#00B4D8" fillOpacity="0.2" />
      <rect x="197" y="225" width="6" height="6" rx="0.5" fill="#00B4D8" fillOpacity="0.2" />
      <rect x="207" y="225" width="6" height="6" rx="0.5" fill="#00B4D8" fillOpacity="0.2" />
      <rect x="194" y="234" width="12" height="12" rx="1" fill="#00B4D8" fillOpacity="0.15" />
    </g>

    {/* Data intersection points */}
    <circle cx="140" cy="82" r="4" fill="#00B4D8" fillOpacity="0.5" filter="url(#industryGlow)">
      {animate && <animate attributeName="r" values="3;5;3" dur="2s" repeatCount="indefinite" />}
    </circle>
    <circle cx="260" cy="78" r="4" fill="#00B4D8" fillOpacity="0.5" filter="url(#industryGlow)">
      {animate && <animate attributeName="r" values="3;5;3" dur="2.3s" repeatCount="indefinite" />}
    </circle>
    <circle cx="130" cy="145" r="3" fill="#00B4D8" fillOpacity="0.4">
      {animate && <animate attributeName="r" values="2;4;2" dur="2.5s" repeatCount="indefinite" />}
    </circle>
    <circle cx="270" cy="140" r="3" fill="#00B4D8" fillOpacity="0.4">
      {animate && <animate attributeName="r" values="2;4;2" dur="2.7s" repeatCount="indefinite" />}
    </circle>
    <circle cx="130" cy="215" r="3.5" fill="#00B4D8" fillOpacity="0.4">
      {animate && <animate attributeName="r" values="2.5;4.5;2.5" dur="2.2s" repeatCount="indefinite" />}
    </circle>
    <circle cx="270" cy="210" r="3.5" fill="#00B4D8" fillOpacity="0.4">
      {animate && <animate attributeName="r" values="2.5;4.5;2.5" dur="2.4s" repeatCount="indefinite" />}
    </circle>
    <circle cx="200" cy="152" r="5" fill="#00B4D8" fillOpacity="0.6" filter="url(#industryGlow)">
      {animate && <animate attributeName="r" values="4;6;4" dur="1.8s" repeatCount="indefinite" />}
    </circle>

    {/* Decorative floating particles */}
    <circle cx="160" cy="45" r="1.5" fill="#0A2540" fillOpacity="0.2" />
    <circle cx="240" cy="260" r="1.5" fill="#0A2540" fillOpacity="0.2" />
    <circle cx="30" cy="140" r="2" fill="#00B4D8" fillOpacity="0.15" />
    <circle cx="370" cy="135" r="2" fill="#00B4D8" fillOpacity="0.15" />
    <circle cx="110" cy="260" r="1.5" fill="#00B4D8" fillOpacity="0.1" />
    <circle cx="300" cy="255" r="1.5" fill="#00B4D8" fillOpacity="0.1" />
  </svg>
);
