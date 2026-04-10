interface Props {
  className?: string;
  animate?: boolean;
}

export const SouthAfricaMap = ({ className = "", animate = false }: Props) => (
  <svg viewBox="0 0 400 300" fill="none" xmlns="http://www.w3.org/2000/svg" className={className}>
    <defs>
      <linearGradient id="saMapGradMain" x1="0%" y1="0%" x2="100%" y2="100%">
        <stop offset="0%" stopColor="#0A2540" />
        <stop offset="100%" stopColor="#00B4D8" />
      </linearGradient>
      <linearGradient id="saMapGradAccent" x1="0%" y1="0%" x2="0%" y2="100%">
        <stop offset="0%" stopColor="#00B4D8" />
        <stop offset="100%" stopColor="#0A2540" />
      </linearGradient>
      <radialGradient id="saMapGlow" cx="50%" cy="50%" r="50%">
        <stop offset="0%" stopColor="#00B4D8" stopOpacity="0.2" />
        <stop offset="100%" stopColor="#00B4D8" stopOpacity="0" />
      </radialGradient>
      <filter id="saMapShadow">
        <feDropShadow dx="0" dy="2" stdDeviation="4" floodColor="#0A2540" floodOpacity="0.1" />
      </filter>
    </defs>

    {/* Background glow */}
    <circle cx="200" cy="150" r="130" fill="url(#saMapGlow)" />

    {/* South Africa outline - simplified shape */}
    <path
      d="M140 70 L180 55 L230 55 L270 65 L300 85 L320 110 L330 140 L325 170 L310 195 L285 215 L260 230 L240 240 L220 245 L200 243 L180 238 L160 228 L145 215 L130 195 L118 175 L112 155 L110 135 L115 110 L125 90 Z"
      fill="url(#saMapGradMain)"
      fillOpacity="0.12"
      stroke="url(#saMapGradMain)"
      strokeWidth="2"
      filter="url(#saMapShadow)"
    />

    {/* Inner border */}
    <path
      d="M140 70 L180 55 L230 55 L270 65 L300 85 L320 110 L330 140 L325 170 L310 195 L285 215 L260 230 L240 240 L220 245 L200 243 L180 238 L160 228 L145 215 L130 195 L118 175 L112 155 L110 135 L115 110 L125 90 Z"
      fill="none"
      stroke="#00B4D8"
      strokeWidth="1"
      strokeOpacity="0.3"
    />

    {/* Provincial border lines */}
    <path d="M180 80 L190 140 L175 190" stroke="#0A2540" strokeWidth="0.8" strokeOpacity="0.15" />
    <path d="M230 75 L225 130 L240 180" stroke="#0A2540" strokeWidth="0.8" strokeOpacity="0.15" />
    <path d="M150 130 L220 135 L290 120" stroke="#0A2540" strokeWidth="0.8" strokeOpacity="0.15" />
    <path d="M140 180 L210 175 L280 190" stroke="#0A2540" strokeWidth="0.8" strokeOpacity="0.15" />

    {/* City markers - Johannesburg/Pretoria (Gauteng) */}
    <circle cx="225" cy="105" r="6" fill="#00B4D8" fillOpacity="0.3" stroke="#00B4D8" strokeWidth="1.5">
      {animate && <animate attributeName="r" values="5;7;5" dur="2s" repeatCount="indefinite" />}
    </circle>
    <circle cx="225" cy="105" r="2.5" fill="#00B4D8" />
    <text x="237" y="108" fontSize="8" fill="#0A2540" fillOpacity="0.6" fontFamily="sans-serif" fontWeight="600">Gauteng</text>

    {/* Cape Town (Western Cape) */}
    <circle cx="160" cy="218" r="5" fill="#00B4D8" fillOpacity="0.3" stroke="#00B4D8" strokeWidth="1.5">
      {animate && <animate attributeName="r" values="4;6;4" dur="2.3s" repeatCount="indefinite" />}
    </circle>
    <circle cx="160" cy="218" r="2" fill="#00B4D8" />
    <text x="130" y="235" fontSize="7" fill="#0A2540" fillOpacity="0.5" fontFamily="sans-serif">Cape Town</text>

    {/* Durban (KZN) */}
    <circle cx="280" cy="175" r="4.5" fill="#00B4D8" fillOpacity="0.3" stroke="#00B4D8" strokeWidth="1.5">
      {animate && <animate attributeName="r" values="3.5;5.5;3.5" dur="2.5s" repeatCount="indefinite" />}
    </circle>
    <circle cx="280" cy="175" r="2" fill="#00B4D8" />
    <text x="290" y="178" fontSize="7" fill="#0A2540" fillOpacity="0.5" fontFamily="sans-serif">Durban</text>

    {/* Port Elizabeth (Eastern Cape) */}
    <circle cx="230" cy="215" r="3.5" fill="#00B4D8" fillOpacity="0.25" stroke="#00B4D8" strokeWidth="1">
      {animate && <animate attributeName="r" values="3;4.5;3" dur="2.8s" repeatCount="indefinite" />}
    </circle>
    <circle cx="230" cy="215" r="1.5" fill="#00B4D8" />

    {/* Bloemfontein (Free State) */}
    <circle cx="210" cy="155" r="3.5" fill="#00B4D8" fillOpacity="0.25" stroke="#00B4D8" strokeWidth="1" />
    <circle cx="210" cy="155" r="1.5" fill="#00B4D8" />

    {/* Connection lines between cities */}
    <line x1="225" y1="105" x2="280" y2="175" stroke="#00B4D8" strokeWidth="0.8" strokeOpacity="0.2" strokeDasharray={animate ? "4 3" : "none"}>
      {animate && <animate attributeName="stroke-dashoffset" from="7" to="0" dur="2s" repeatCount="indefinite" />}
    </line>
    <line x1="225" y1="105" x2="160" y2="218" stroke="#00B4D8" strokeWidth="0.8" strokeOpacity="0.2" strokeDasharray={animate ? "4 3" : "none"}>
      {animate && <animate attributeName="stroke-dashoffset" from="7" to="0" dur="2.5s" repeatCount="indefinite" />}
    </line>
    <line x1="225" y1="105" x2="210" y2="155" stroke="#00B4D8" strokeWidth="0.8" strokeOpacity="0.15" strokeDasharray={animate ? "4 3" : "none"}>
      {animate && <animate attributeName="stroke-dashoffset" from="7" to="0" dur="1.8s" repeatCount="indefinite" />}
    </line>
    <line x1="280" y1="175" x2="230" y2="215" stroke="#00B4D8" strokeWidth="0.8" strokeOpacity="0.15" strokeDasharray={animate ? "4 3" : "none"}>
      {animate && <animate attributeName="stroke-dashoffset" from="7" to="0" dur="2.2s" repeatCount="indefinite" />}
    </line>

    {/* Shield badge overlay - top right */}
    <path
      d="M330 30 L355 40 L355 62 Q355 78 330 88 Q305 78 305 62 L305 40 Z"
      fill="url(#saMapGradMain)"
      fillOpacity="0.15"
      stroke="url(#saMapGradMain)"
      strokeWidth="1.5"
    />
    <path d="M322 55 L328 62 L340 48" stroke="#00B4D8" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      {animate && <animate attributeName="stroke-opacity" values="0.6;1;0.6" dur="2s" repeatCount="indefinite" />}
    </path>

    {/* Decorative data labels */}
    <rect x="50" y="50" width="60" height="24" rx="4" fill="white" stroke="#0A2540" strokeWidth="0.8" strokeOpacity="0.1" filter="url(#saMapShadow)" />
    <rect x="56" y="56" width="20" height="3" rx="1" fill="#0A2540" fillOpacity="0.15" />
    <rect x="56" y="63" width="40" height="3" rx="1" fill="#00B4D8" fillOpacity="0.25" />

    <rect x="320" y="230" width="60" height="24" rx="4" fill="white" stroke="#0A2540" strokeWidth="0.8" strokeOpacity="0.1" filter="url(#saMapShadow)" />
    <rect x="326" y="236" width="25" height="3" rx="1" fill="#0A2540" fillOpacity="0.15" />
    <rect x="326" y="243" width="45" height="3" rx="1" fill="#00B4D8" fillOpacity="0.25" />

    {/* Floating particles */}
    <circle cx="80" cy="100" r="2" fill="#00B4D8" fillOpacity="0.3">
      {animate && <animate attributeName="cy" values="100;95;100" dur="3s" repeatCount="indefinite" />}
    </circle>
    <circle cx="350" cy="140" r="1.5" fill="#00B4D8" fillOpacity="0.25">
      {animate && <animate attributeName="cy" values="140;135;140" dur="2.5s" repeatCount="indefinite" />}
    </circle>
    <circle cx="90" cy="250" r="2" fill="#0A2540" fillOpacity="0.2">
      {animate && <animate attributeName="cy" values="250;245;250" dur="3.2s" repeatCount="indefinite" />}
    </circle>
  </svg>
);
