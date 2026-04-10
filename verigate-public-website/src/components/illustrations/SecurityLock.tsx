interface SecurityLockProps {
  className?: string;
  animate?: boolean;
}

export const SecurityLock = ({ className = "", animate = true }: SecurityLockProps) => (
  <svg
    viewBox="0 0 400 400"
    fill="none"
    xmlns="http://www.w3.org/2000/svg"
    className={className}
    aria-hidden="true"
  >
    <defs>
      <linearGradient id="seclock-grad1" x1="0%" y1="0%" x2="100%" y2="100%">
        <stop offset="0%" stopColor="#0A2540" stopOpacity="0.12" />
        <stop offset="100%" stopColor="#00B4D8" stopOpacity="0.08" />
      </linearGradient>
      <linearGradient id="seclock-grad2" x1="50%" y1="0%" x2="50%" y2="100%">
        <stop offset="0%" stopColor="#0A2540" />
        <stop offset="100%" stopColor="#0E3A5C" />
      </linearGradient>
      <linearGradient id="seclock-grad3" x1="0%" y1="0%" x2="100%" y2="0%">
        <stop offset="0%" stopColor="#00B4D8" stopOpacity="0.6" />
        <stop offset="100%" stopColor="#00B4D8" stopOpacity="0.1" />
      </linearGradient>
    </defs>

    {/* Background circle */}
    <circle cx="200" cy="200" r="180" fill="url(#seclock-grad1)" />

    {/* Outer circuit ring */}
    <circle cx="200" cy="200" r="150" stroke="#0A2540" strokeOpacity="0.08" strokeWidth="1" fill="none" />
    <circle cx="200" cy="200" r="120" stroke="#00B4D8" strokeOpacity="0.12" strokeWidth="1" fill="none" strokeDasharray="8 6" />

    {/* Circuit paths */}
    {/* Top */}
    <line x1="200" y1="50" x2="200" y2="100" stroke="#00B4D8" strokeOpacity="0.3" strokeWidth="1.5" />
    <circle cx="200" cy="50" r="3" fill="#00B4D8" fillOpacity="0.5" />
    {/* Bottom */}
    <line x1="200" y1="300" x2="200" y2="350" stroke="#00B4D8" strokeOpacity="0.3" strokeWidth="1.5" />
    <circle cx="200" cy="350" r="3" fill="#00B4D8" fillOpacity="0.5" />
    {/* Left */}
    <line x1="50" y1="200" x2="110" y2="200" stroke="#00B4D8" strokeOpacity="0.3" strokeWidth="1.5" />
    <circle cx="50" cy="200" r="3" fill="#00B4D8" fillOpacity="0.5" />
    {/* Right */}
    <line x1="290" y1="200" x2="350" y2="200" stroke="#00B4D8" strokeOpacity="0.3" strokeWidth="1.5" />
    <circle cx="350" cy="200" r="3" fill="#00B4D8" fillOpacity="0.5" />

    {/* Diagonal circuit paths */}
    <line x1="85" y1="85" x2="135" y2="135" stroke="#0A2540" strokeOpacity="0.15" strokeWidth="1" />
    <circle cx="85" cy="85" r="2.5" fill="#0A2540" fillOpacity="0.3" />
    <line x1="315" y1="85" x2="265" y2="135" stroke="#0A2540" strokeOpacity="0.15" strokeWidth="1" />
    <circle cx="315" cy="85" r="2.5" fill="#0A2540" fillOpacity="0.3" />
    <line x1="85" y1="315" x2="135" y2="265" stroke="#0A2540" strokeOpacity="0.15" strokeWidth="1" />
    <circle cx="85" cy="315" r="2.5" fill="#0A2540" fillOpacity="0.3" />
    <line x1="315" y1="315" x2="265" y2="265" stroke="#0A2540" strokeOpacity="0.15" strokeWidth="1" />
    <circle cx="315" cy="315" r="2.5" fill="#0A2540" fillOpacity="0.3" />

    {/* Lock body */}
    <rect x="150" y="195" width="100" height="80" rx="10" fill="url(#seclock-grad2)" />
    <rect x="150" y="195" width="100" height="80" rx="10" stroke="#00B4D8" strokeOpacity="0.3" strokeWidth="1" fill="none" />

    {/* Lock shackle */}
    <path
      d="M170 195 V170 C170 145 185 130 200 130 C215 130 230 145 230 170 V195"
      stroke="#0A2540"
      strokeWidth="10"
      strokeLinecap="round"
      fill="none"
    />
    <path
      d="M170 195 V170 C170 145 185 130 200 130 C215 130 230 145 230 170 V195"
      stroke="#00B4D8"
      strokeWidth="1.5"
      strokeLinecap="round"
      fill="none"
      strokeOpacity="0.4"
    />

    {/* Keyhole */}
    <circle cx="200" cy="228" r="10" fill="#00B4D8" fillOpacity="0.7" />
    <rect x="197" y="232" width="6" height="16" rx="3" fill="#00B4D8" fillOpacity="0.5" />

    {/* Glow effect around keyhole */}
    <circle cx="200" cy="228" r="16" fill="none" stroke="#00B4D8" strokeOpacity="0.2" strokeWidth="1">
      {animate && (
        <animate attributeName="r" values="16;20;16" dur="3s" repeatCount="indefinite" />
      )}
    </circle>
    <circle cx="200" cy="228" r="22" fill="none" stroke="#00B4D8" strokeOpacity="0.1" strokeWidth="1">
      {animate && (
        <animate attributeName="r" values="22;28;22" dur="3s" repeatCount="indefinite" />
      )}
    </circle>

    {/* Binary/data dots scattered around */}
    <circle cx="120" cy="160" r="2" fill="#00B4D8" fillOpacity="0.4" />
    <circle cx="130" cy="170" r="1.5" fill="#0A2540" fillOpacity="0.3" />
    <circle cx="280" cy="160" r="2" fill="#00B4D8" fillOpacity="0.4" />
    <circle cx="270" cy="170" r="1.5" fill="#0A2540" fillOpacity="0.3" />
    <circle cx="120" cy="260" r="2" fill="#00B4D8" fillOpacity="0.3" />
    <circle cx="280" cy="260" r="2" fill="#00B4D8" fillOpacity="0.3" />
    <circle cx="160" cy="310" r="1.5" fill="#0A2540" fillOpacity="0.2" />
    <circle cx="240" cy="310" r="1.5" fill="#0A2540" fillOpacity="0.2" />

    {/* Small shield badges */}
    <g transform="translate(100, 110) scale(0.35)">
      <path d="M20 4 L36 12 V24 C36 36 28 44 20 48 C12 44 4 36 4 24 V12 Z" fill="#00B4D8" fillOpacity="0.15" stroke="#00B4D8" strokeOpacity="0.3" strokeWidth="1.5" />
    </g>
    <g transform="translate(275, 110) scale(0.35)">
      <path d="M20 4 L36 12 V24 C36 36 28 44 20 48 C12 44 4 36 4 24 V12 Z" fill="#00B4D8" fillOpacity="0.15" stroke="#00B4D8" strokeOpacity="0.3" strokeWidth="1.5" />
    </g>
  </svg>
);
