interface Props {
  className?: string;
  animate?: boolean;
}

export const TeamCollaboration = ({ className = "", animate = false }: Props) => (
  <svg viewBox="0 0 400 300" fill="none" xmlns="http://www.w3.org/2000/svg" className={className}>
    <defs>
      <linearGradient id="teamGradMain" x1="0%" y1="0%" x2="100%" y2="100%">
        <stop offset="0%" stopColor="#0A2540" />
        <stop offset="100%" stopColor="#00B4D8" />
      </linearGradient>
      <linearGradient id="teamGradAccent" x1="0%" y1="0%" x2="0%" y2="100%">
        <stop offset="0%" stopColor="#00B4D8" />
        <stop offset="100%" stopColor="#0A2540" />
      </linearGradient>
      <radialGradient id="teamGlow" cx="50%" cy="50%" r="50%">
        <stop offset="0%" stopColor="#00B4D8" stopOpacity="0.2" />
        <stop offset="100%" stopColor="#00B4D8" stopOpacity="0" />
      </radialGradient>
      <filter id="teamShadow">
        <feDropShadow dx="0" dy="2" stdDeviation="3" floodColor="#0A2540" floodOpacity="0.1" />
      </filter>
    </defs>

    {/* Background glow */}
    <circle cx="200" cy="150" r="120" fill="url(#teamGlow)" />

    {/* Central hub circle */}
    <circle cx="200" cy="150" r="35" fill="url(#teamGradMain)" fillOpacity="0.1" stroke="url(#teamGradMain)" strokeWidth="2" />
    <circle cx="200" cy="150" r="25" fill="url(#teamGradMain)" fillOpacity="0.06" stroke="#00B4D8" strokeWidth="1" strokeOpacity="0.3" />

    {/* Central icon - people/team */}
    <circle cx="192" cy="142" r="5" fill="none" stroke="#00B4D8" strokeWidth="1.5" />
    <path d="M184 156 Q192 150 192 150 Q192 150 200 156" stroke="#00B4D8" strokeWidth="1.5" strokeLinecap="round" fill="none" />
    <circle cx="208" cy="142" r="5" fill="none" stroke="#0A2540" strokeWidth="1.5" strokeOpacity="0.5" />
    <path d="M200 156 Q208 150 208 150 Q208 150 216 156" stroke="#0A2540" strokeWidth="1.5" strokeLinecap="round" strokeOpacity="0.5" fill="none" />

    {/* Team member nodes - arranged in a circle */}
    {/* Top */}
    <circle cx="200" cy="50" r="18" fill="white" stroke="#0A2540" strokeWidth="1" strokeOpacity="0.12" filter="url(#teamShadow)" />
    <circle cx="200" cy="45" r="5" fill="#00B4D8" fillOpacity="0.3" />
    <rect x="191" y="54" width="18" height="3" rx="1" fill="#0A2540" fillOpacity="0.15" />
    <rect x="193" y="60" width="14" height="2" rx="1" fill="#00B4D8" fillOpacity="0.2" />

    {/* Top-right */}
    <circle cx="310" cy="90" r="18" fill="white" stroke="#0A2540" strokeWidth="1" strokeOpacity="0.12" filter="url(#teamShadow)" />
    <circle cx="310" cy="85" r="5" fill="#00B4D8" fillOpacity="0.3" />
    <rect x="301" y="94" width="18" height="3" rx="1" fill="#0A2540" fillOpacity="0.15" />
    <rect x="303" y="100" width="14" height="2" rx="1" fill="#00B4D8" fillOpacity="0.2" />

    {/* Right */}
    <circle cx="340" cy="180" r="18" fill="white" stroke="#0A2540" strokeWidth="1" strokeOpacity="0.12" filter="url(#teamShadow)" />
    <circle cx="340" cy="175" r="5" fill="#00B4D8" fillOpacity="0.3" />
    <rect x="331" y="184" width="18" height="3" rx="1" fill="#0A2540" fillOpacity="0.15" />
    <rect x="333" y="190" width="14" height="2" rx="1" fill="#00B4D8" fillOpacity="0.2" />

    {/* Bottom-right */}
    <circle cx="280" cy="255" r="18" fill="white" stroke="#0A2540" strokeWidth="1" strokeOpacity="0.12" filter="url(#teamShadow)" />
    <circle cx="280" cy="250" r="5" fill="#00B4D8" fillOpacity="0.3" />
    <rect x="271" y="259" width="18" height="3" rx="1" fill="#0A2540" fillOpacity="0.15" />
    <rect x="273" y="265" width="14" height="2" rx="1" fill="#00B4D8" fillOpacity="0.2" />

    {/* Bottom-left */}
    <circle cx="120" cy="255" r="18" fill="white" stroke="#0A2540" strokeWidth="1" strokeOpacity="0.12" filter="url(#teamShadow)" />
    <circle cx="120" cy="250" r="5" fill="#00B4D8" fillOpacity="0.3" />
    <rect x="111" y="259" width="18" height="3" rx="1" fill="#0A2540" fillOpacity="0.15" />
    <rect x="113" y="265" width="14" height="2" rx="1" fill="#00B4D8" fillOpacity="0.2" />

    {/* Left */}
    <circle cx="60" cy="180" r="18" fill="white" stroke="#0A2540" strokeWidth="1" strokeOpacity="0.12" filter="url(#teamShadow)" />
    <circle cx="60" cy="175" r="5" fill="#00B4D8" fillOpacity="0.3" />
    <rect x="51" y="184" width="18" height="3" rx="1" fill="#0A2540" fillOpacity="0.15" />
    <rect x="53" y="190" width="14" height="2" rx="1" fill="#00B4D8" fillOpacity="0.2" />

    {/* Top-left */}
    <circle cx="90" cy="90" r="18" fill="white" stroke="#0A2540" strokeWidth="1" strokeOpacity="0.12" filter="url(#teamShadow)" />
    <circle cx="90" cy="85" r="5" fill="#00B4D8" fillOpacity="0.3" />
    <rect x="81" y="94" width="18" height="3" rx="1" fill="#0A2540" fillOpacity="0.15" />
    <rect x="83" y="100" width="14" height="2" rx="1" fill="#00B4D8" fillOpacity="0.2" />

    {/* Connection lines from nodes to center */}
    <line x1="200" y1="68" x2="200" y2="115" stroke="#00B4D8" strokeWidth="1" strokeOpacity="0.25" strokeDasharray={animate ? "4 3" : "none"}>
      {animate && <animate attributeName="stroke-dashoffset" from="7" to="0" dur="1.5s" repeatCount="indefinite" />}
    </line>
    <line x1="295" y1="100" x2="230" y2="135" stroke="#00B4D8" strokeWidth="1" strokeOpacity="0.2" strokeDasharray={animate ? "4 3" : "none"}>
      {animate && <animate attributeName="stroke-dashoffset" from="7" to="0" dur="1.8s" repeatCount="indefinite" />}
    </line>
    <line x1="322" y1="175" x2="235" y2="155" stroke="#00B4D8" strokeWidth="1" strokeOpacity="0.2" strokeDasharray={animate ? "4 3" : "none"}>
      {animate && <animate attributeName="stroke-dashoffset" from="7" to="0" dur="2s" repeatCount="indefinite" />}
    </line>
    <line x1="265" y1="245" x2="220" y2="178" stroke="#00B4D8" strokeWidth="1" strokeOpacity="0.2" strokeDasharray={animate ? "4 3" : "none"}>
      {animate && <animate attributeName="stroke-dashoffset" from="7" to="0" dur="2.2s" repeatCount="indefinite" />}
    </line>
    <line x1="135" y1="245" x2="180" y2="178" stroke="#00B4D8" strokeWidth="1" strokeOpacity="0.2" strokeDasharray={animate ? "4 3" : "none"}>
      {animate && <animate attributeName="stroke-dashoffset" from="7" to="0" dur="1.7s" repeatCount="indefinite" />}
    </line>
    <line x1="78" y1="175" x2="165" y2="155" stroke="#00B4D8" strokeWidth="1" strokeOpacity="0.2" strokeDasharray={animate ? "4 3" : "none"}>
      {animate && <animate attributeName="stroke-dashoffset" from="7" to="0" dur="1.9s" repeatCount="indefinite" />}
    </line>
    <line x1="105" y1="100" x2="170" y2="135" stroke="#00B4D8" strokeWidth="1" strokeOpacity="0.2" strokeDasharray={animate ? "4 3" : "none"}>
      {animate && <animate attributeName="stroke-dashoffset" from="7" to="0" dur="2.1s" repeatCount="indefinite" />}
    </line>

    {/* Floating chat/message bubbles */}
    <rect x="240" cy="40" y="30" width="35" height="18" rx="4" fill="white" stroke="#00B4D8" strokeWidth="0.8" strokeOpacity="0.3" filter="url(#teamShadow)">
      {animate && <animate attributeName="y" values="30;26;30" dur="3s" repeatCount="indefinite" />}
    </rect>
    <rect x="246" y="36" width="20" height="2" rx="1" fill="#0A2540" fillOpacity="0.12" />
    <rect x="246" y="41" width="15" height="2" rx="1" fill="#00B4D8" fillOpacity="0.15" />

    <rect x="45" y="120" width="30" height="16" rx="4" fill="white" stroke="#00B4D8" strokeWidth="0.8" strokeOpacity="0.25" filter="url(#teamShadow)">
      {animate && <animate attributeName="y" values="120;116;120" dur="3.5s" repeatCount="indefinite" />}
    </rect>
    <rect x="50" y="125" width="18" height="2" rx="1" fill="#0A2540" fillOpacity="0.1" />
    <rect x="50" y="130" width="12" height="2" rx="1" fill="#00B4D8" fillOpacity="0.12" />

    {/* Decorative particles */}
    <circle cx="155" cy="35" r="2" fill="#00B4D8" fillOpacity="0.3">
      {animate && <animate attributeName="cy" values="35;30;35" dur="2.5s" repeatCount="indefinite" />}
    </circle>
    <circle cx="355" cy="130" r="1.5" fill="#00B4D8" fillOpacity="0.25">
      {animate && <animate attributeName="cy" values="130;125;130" dur="3s" repeatCount="indefinite" />}
    </circle>
    <circle cx="30" cy="240" r="2" fill="#0A2540" fillOpacity="0.2">
      {animate && <animate attributeName="cy" values="240;235;240" dur="2.8s" repeatCount="indefinite" />}
    </circle>
    <circle cx="370" cy="260" r="1.5" fill="#00B4D8" fillOpacity="0.2">
      {animate && <animate attributeName="cy" values="260;255;260" dur="3.2s" repeatCount="indefinite" />}
    </circle>
  </svg>
);
