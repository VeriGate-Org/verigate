interface Props {
  className?: string;
  animate?: boolean;
}

export const DataSources = ({ className = "", animate = false }: Props) => (
  <svg viewBox="0 0 400 300" fill="none" xmlns="http://www.w3.org/2000/svg" className={className}>
    <defs>
      <linearGradient id="dataSourcesGradMain" x1="0%" y1="0%" x2="100%" y2="100%">
        <stop offset="0%" stopColor="#0A2540" />
        <stop offset="100%" stopColor="#00B4D8" />
      </linearGradient>
      <linearGradient id="dataSourcesGradHub" x1="0%" y1="0%" x2="100%" y2="100%">
        <stop offset="0%" stopColor="#0A2540" stopOpacity="0.8" />
        <stop offset="100%" stopColor="#00B4D8" stopOpacity="0.6" />
      </linearGradient>
      <radialGradient id="dataSourcesGradGlow" cx="50%" cy="50%" r="50%">
        <stop offset="0%" stopColor="#00B4D8" stopOpacity="0.2" />
        <stop offset="100%" stopColor="#00B4D8" stopOpacity="0" />
      </radialGradient>
      <radialGradient id="dataSourcesGradInner" cx="50%" cy="50%" r="50%">
        <stop offset="0%" stopColor="#00B4D8" stopOpacity="0.15" />
        <stop offset="70%" stopColor="#0A2540" stopOpacity="0.05" />
        <stop offset="100%" stopColor="#0A2540" stopOpacity="0" />
      </radialGradient>
      <filter id="dataSourcesGlow">
        <feGaussianBlur stdDeviation="3" result="blur" />
        <feMerge>
          <feMergeNode in="blur" />
          <feMergeNode in="SourceGraphic" />
        </feMerge>
      </filter>
    </defs>

    {/* Background radial glow */}
    <circle cx="200" cy="150" r="140" fill="url(#dataSourcesGradGlow)" />
    <circle cx="200" cy="150" r="90" fill="url(#dataSourcesGradInner)" />

    {/* Curved flow lines from sources to hub */}
    {/* From top-left (Government) */}
    <path d="M75 55 Q120 80 160 115 Q180 130 190 145" stroke="#00B4D8" strokeWidth="1.5" strokeOpacity="0.3" fill="none" strokeDasharray={animate ? "6 4" : "none"}>
      {animate && <animate attributeName="stroke-dashoffset" from="10" to="0" dur="2s" repeatCount="indefinite" />}
    </path>
    {/* From top-right (Bureau) */}
    <path d="M325 55 Q280 80 240 115 Q220 130 210 145" stroke="#00B4D8" strokeWidth="1.5" strokeOpacity="0.3" fill="none" strokeDasharray={animate ? "6 4" : "none"}>
      {animate && <animate attributeName="stroke-dashoffset" from="10" to="0" dur="2.2s" repeatCount="indefinite" />}
    </path>
    {/* From left (Database) */}
    <path d="M45 155 Q90 150 140 148 Q170 147 188 148" stroke="#00B4D8" strokeWidth="1.5" strokeOpacity="0.3" fill="none" strokeDasharray={animate ? "6 4" : "none"}>
      {animate && <animate attributeName="stroke-dashoffset" from="10" to="0" dur="1.8s" repeatCount="indefinite" />}
    </path>
    {/* From right (API) */}
    <path d="M355 150 Q310 148 260 148 Q230 148 212 148" stroke="#00B4D8" strokeWidth="1.5" strokeOpacity="0.3" fill="none" strokeDasharray={animate ? "6 4" : "none"}>
      {animate && <animate attributeName="stroke-dashoffset" from="10" to="0" dur="2.1s" repeatCount="indefinite" />}
    </path>
    {/* From bottom-left (Documents) */}
    <path d="M80 248 Q120 220 160 185 Q180 168 192 155" stroke="#00B4D8" strokeWidth="1.5" strokeOpacity="0.3" fill="none" strokeDasharray={animate ? "6 4" : "none"}>
      {animate && <animate attributeName="stroke-dashoffset" from="10" to="0" dur="2.4s" repeatCount="indefinite" />}
    </path>
    {/* From bottom-right (Watchlists) */}
    <path d="M320 245 Q280 218 240 185 Q220 168 208 155" stroke="#00B4D8" strokeWidth="1.5" strokeOpacity="0.3" fill="none" strokeDasharray={animate ? "6 4" : "none"}>
      {animate && <animate attributeName="stroke-dashoffset" from="10" to="0" dur="2.3s" repeatCount="indefinite" />}
    </path>

    {/* Central hub - outer ring */}
    <circle cx="200" cy="150" r="32" fill="none" stroke="url(#dataSourcesGradMain)" strokeWidth="2.5" />
    {/* Central hub - middle ring */}
    <circle cx="200" cy="150" r="24" fill="url(#dataSourcesGradHub)" fillOpacity="0.15" stroke="#00B4D8" strokeWidth="1" strokeOpacity="0.4" />
    {/* Central hub - inner circle */}
    <circle cx="200" cy="150" r="14" fill="url(#dataSourcesGradHub)" filter="url(#dataSourcesGlow)">
      {animate && <animate attributeName="r" values="13;15;13" dur="2s" repeatCount="indefinite" />}
    </circle>
    {/* Hub center dot */}
    <circle cx="200" cy="150" r="5" fill="#00B4D8" fillOpacity="0.8" />

    {/* Orbital dots on hub ring */}
    <circle cx="200" cy="118" r="3" fill="#00B4D8" fillOpacity="0.6">
      {animate && (
        <animateTransform attributeName="transform" type="rotate" from="0 200 150" to="360 200 150" dur="8s" repeatCount="indefinite" />
      )}
    </circle>
    <circle cx="200" cy="182" r="2.5" fill="#00B4D8" fillOpacity="0.4">
      {animate && (
        <animateTransform attributeName="transform" type="rotate" from="180 200 150" to="540 200 150" dur="8s" repeatCount="indefinite" />
      )}
    </circle>

    {/* Source Node 1 - Government (top-left) */}
    <g>
      <circle cx="65" cy="48" r="22" fill="white" stroke="#0A2540" strokeWidth="1.5" strokeOpacity="0.3" />
      <circle cx="65" cy="48" r="18" fill="#0A2540" fillOpacity="0.04" />
      {/* Government building icon */}
      <rect x="55" y="46" width="20" height="12" rx="1" fill="none" stroke="#0A2540" strokeWidth="1.2" strokeOpacity="0.5" />
      <path d="M53 46 L65 38 L77 46" fill="none" stroke="#0A2540" strokeWidth="1.2" strokeOpacity="0.5" />
      <line x1="59" y1="46" x2="59" y2="58" stroke="#0A2540" strokeWidth="1" strokeOpacity="0.3" />
      <line x1="65" y1="46" x2="65" y2="58" stroke="#0A2540" strokeWidth="1" strokeOpacity="0.3" />
      <line x1="71" y1="46" x2="71" y2="58" stroke="#0A2540" strokeWidth="1" strokeOpacity="0.3" />
    </g>

    {/* Source Node 2 - Bureau (top-right) */}
    <g>
      <circle cx="335" cy="48" r="22" fill="white" stroke="#0A2540" strokeWidth="1.5" strokeOpacity="0.3" />
      <circle cx="335" cy="48" r="18" fill="#0A2540" fillOpacity="0.04" />
      {/* Bureau/report icon */}
      <rect x="325" y="38" width="20" height="22" rx="2" fill="none" stroke="#0A2540" strokeWidth="1.2" strokeOpacity="0.5" />
      <line x1="329" y1="44" x2="341" y2="44" stroke="#0A2540" strokeWidth="1" strokeOpacity="0.3" />
      <line x1="329" y1="48" x2="339" y2="48" stroke="#0A2540" strokeWidth="1" strokeOpacity="0.3" />
      <line x1="329" y1="52" x2="341" y2="52" stroke="#0A2540" strokeWidth="1" strokeOpacity="0.3" />
      <line x1="329" y1="56" x2="336" y2="56" stroke="#0A2540" strokeWidth="1" strokeOpacity="0.3" />
    </g>

    {/* Source Node 3 - Database (left) */}
    <g>
      <circle cx="38" cy="155" r="22" fill="white" stroke="#0A2540" strokeWidth="1.5" strokeOpacity="0.3" />
      <circle cx="38" cy="155" r="18" fill="#0A2540" fillOpacity="0.04" />
      {/* Database icon */}
      <ellipse cx="38" cy="146" rx="10" ry="4" fill="none" stroke="#0A2540" strokeWidth="1.2" strokeOpacity="0.5" />
      <path d="M28 146 L28 162 Q28 166 38 166 Q48 166 48 162 L48 146" fill="none" stroke="#0A2540" strokeWidth="1.2" strokeOpacity="0.5" />
      <path d="M28 152 Q28 156 38 156 Q48 156 48 152" fill="none" stroke="#0A2540" strokeWidth="1" strokeOpacity="0.3" />
      <path d="M28 158 Q28 162 38 162 Q48 162 48 158" fill="none" stroke="#0A2540" strokeWidth="1" strokeOpacity="0.3" />
    </g>

    {/* Source Node 4 - API (right) */}
    <g>
      <circle cx="362" cy="150" r="22" fill="white" stroke="#0A2540" strokeWidth="1.5" strokeOpacity="0.3" />
      <circle cx="362" cy="150" r="18" fill="#0A2540" fillOpacity="0.04" />
      {/* API bracket icon */}
      <text x="362" y="155" textAnchor="middle" fontSize="13" fontWeight="bold" fill="#0A2540" fillOpacity="0.5" fontFamily="monospace">{`{ }`}</text>
    </g>

    {/* Source Node 5 - Documents (bottom-left) */}
    <g>
      <circle cx="72" cy="252" r="22" fill="white" stroke="#0A2540" strokeWidth="1.5" strokeOpacity="0.3" />
      <circle cx="72" cy="252" r="18" fill="#0A2540" fillOpacity="0.04" />
      {/* ID card icon */}
      <rect x="60" y="242" width="24" height="18" rx="2" fill="none" stroke="#0A2540" strokeWidth="1.2" strokeOpacity="0.5" />
      <circle cx="68" cy="249" r="3" fill="none" stroke="#0A2540" strokeWidth="1" strokeOpacity="0.4" />
      <line x1="74" y1="247" x2="80" y2="247" stroke="#0A2540" strokeWidth="1" strokeOpacity="0.3" />
      <line x1="74" y1="251" x2="79" y2="251" stroke="#0A2540" strokeWidth="1" strokeOpacity="0.3" />
      <line x1="63" y1="256" x2="81" y2="256" stroke="#0A2540" strokeWidth="1" strokeOpacity="0.2" />
    </g>

    {/* Source Node 6 - Watchlists (bottom-right) */}
    <g>
      <circle cx="328" cy="248" r="22" fill="white" stroke="#0A2540" strokeWidth="1.5" strokeOpacity="0.3" />
      <circle cx="328" cy="248" r="18" fill="#0A2540" fillOpacity="0.04" />
      {/* Shield/watchlist icon */}
      <path d="M328 238 L340 243 L340 252 Q340 258 328 262 Q316 258 316 252 L316 243 Z" fill="none" stroke="#0A2540" strokeWidth="1.2" strokeOpacity="0.5" />
      <path d="M323 250 L327 254 L334 246" stroke="#00B4D8" strokeWidth="1.2" strokeOpacity="0.6" strokeLinecap="round" strokeLinejoin="round" />
    </g>

    {/* Data flow particles */}
    {animate && (
      <>
        <circle r="2" fill="#00B4D8" fillOpacity="0.6">
          <animateMotion dur="2s" repeatCount="indefinite" path="M75 55 Q120 80 160 115 Q180 130 190 145" />
        </circle>
        <circle r="2" fill="#00B4D8" fillOpacity="0.6">
          <animateMotion dur="2.2s" repeatCount="indefinite" path="M325 55 Q280 80 240 115 Q220 130 210 145" />
        </circle>
        <circle r="2" fill="#00B4D8" fillOpacity="0.5">
          <animateMotion dur="1.8s" repeatCount="indefinite" path="M45 155 Q90 150 140 148 Q170 147 188 148" />
        </circle>
        <circle r="2" fill="#00B4D8" fillOpacity="0.5">
          <animateMotion dur="2.1s" repeatCount="indefinite" path="M355 150 Q310 148 260 148 Q230 148 212 148" />
        </circle>
        <circle r="2" fill="#00B4D8" fillOpacity="0.6">
          <animateMotion dur="2.4s" repeatCount="indefinite" path="M80 248 Q120 220 160 185 Q180 168 192 155" />
        </circle>
        <circle r="2" fill="#00B4D8" fillOpacity="0.6">
          <animateMotion dur="2.3s" repeatCount="indefinite" path="M320 245 Q280 218 240 185 Q220 168 208 155" />
        </circle>
      </>
    )}

    {/* Decorative dots */}
    <circle cx="150" cy="60" r="1.5" fill="#00B4D8" fillOpacity="0.2" />
    <circle cx="250" cy="65" r="1.5" fill="#00B4D8" fillOpacity="0.2" />
    <circle cx="150" cy="240" r="1.5" fill="#0A2540" fillOpacity="0.15" />
    <circle cx="250" cy="235" r="1.5" fill="#0A2540" fillOpacity="0.15" />
  </svg>
);
