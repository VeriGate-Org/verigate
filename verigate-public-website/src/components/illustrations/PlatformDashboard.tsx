interface Props {
  className?: string;
  animate?: boolean;
}

export const PlatformDashboard = ({ className = "", animate = false }: Props) => (
  <svg viewBox="0 0 400 300" fill="none" xmlns="http://www.w3.org/2000/svg" className={className}>
    <defs>
      <linearGradient id="dashboardGradMain" x1="0%" y1="0%" x2="100%" y2="100%">
        <stop offset="0%" stopColor="#0A2540" />
        <stop offset="100%" stopColor="#00B4D8" />
      </linearGradient>
      <linearGradient id="dashboardGradBar" x1="0%" y1="100%" x2="0%" y2="0%">
        <stop offset="0%" stopColor="#0A2540" stopOpacity="0.6" />
        <stop offset="100%" stopColor="#00B4D8" stopOpacity="0.8" />
      </linearGradient>
      <linearGradient id="dashboardGradLine" x1="0%" y1="0%" x2="100%" y2="0%">
        <stop offset="0%" stopColor="#00B4D8" />
        <stop offset="100%" stopColor="#0A2540" />
      </linearGradient>
      <linearGradient id="dashboardGradPanel" x1="0%" y1="0%" x2="0%" y2="100%">
        <stop offset="0%" stopColor="#0A2540" stopOpacity="0.05" />
        <stop offset="100%" stopColor="#0A2540" stopOpacity="0.02" />
      </linearGradient>
      <filter id="dashboardShadow">
        <feDropShadow dx="0" dy="2" stdDeviation="4" floodColor="#0A2540" floodOpacity="0.1" />
      </filter>
    </defs>

    {/* Main dashboard frame */}
    <rect x="30" y="20" width="340" height="260" rx="8" fill="white" stroke="#0A2540" strokeWidth="1.5" strokeOpacity="0.15" filter="url(#dashboardShadow)" />

    {/* Top bar */}
    <rect x="30" y="20" width="340" height="32" rx="8" fill="#0A2540" fillOpacity="0.04" />
    <rect x="30" y="44" width="340" height="1" fill="#0A2540" fillOpacity="0.08" />

    {/* Window dots */}
    <circle cx="50" cy="36" r="4" fill="#0A2540" fillOpacity="0.15" />
    <circle cx="63" cy="36" r="4" fill="#0A2540" fillOpacity="0.1" />
    <circle cx="76" cy="36" r="4" fill="#0A2540" fillOpacity="0.08" />

    {/* Top bar nav items */}
    <rect x="260" y="30" width="30" height="10" rx="2" fill="#0A2540" fillOpacity="0.06" />
    <rect x="296" y="30" width="30" height="10" rx="2" fill="#0A2540" fillOpacity="0.06" />
    <rect x="332" y="30" width="24" height="10" rx="2" fill="#00B4D8" fillOpacity="0.15" />

    {/* Sidebar */}
    <rect x="30" y="44" width="60" height="236" fill="#0A2540" fillOpacity="0.03" />
    <line x1="90" y1="44" x2="90" y2="280" stroke="#0A2540" strokeWidth="1" strokeOpacity="0.06" />

    {/* Sidebar items */}
    <rect x="40" y="58" width="40" height="6" rx="2" fill="#00B4D8" fillOpacity="0.3" />
    <rect x="40" y="74" width="36" height="6" rx="2" fill="#0A2540" fillOpacity="0.1" />
    <rect x="40" y="90" width="38" height="6" rx="2" fill="#0A2540" fillOpacity="0.08" />
    <rect x="40" y="106" width="32" height="6" rx="2" fill="#0A2540" fillOpacity="0.08" />
    <rect x="40" y="122" width="40" height="6" rx="2" fill="#0A2540" fillOpacity="0.06" />

    {/* KPI Card 1 */}
    <rect x="100" y="56" width="78" height="50" rx="5" fill="url(#dashboardGradPanel)" stroke="#0A2540" strokeWidth="0.8" strokeOpacity="0.08" />
    <rect x="108" y="64" width="30" height="4" rx="1" fill="#0A2540" fillOpacity="0.15" />
    <text x="108" y="88" fontSize="16" fontWeight="bold" fill="#0A2540" fillOpacity="0.6" fontFamily="sans-serif">2,847</text>

    {/* KPI Card 2 */}
    <rect x="186" y="56" width="78" height="50" rx="5" fill="url(#dashboardGradPanel)" stroke="#0A2540" strokeWidth="0.8" strokeOpacity="0.08" />
    <rect x="194" y="64" width="35" height="4" rx="1" fill="#0A2540" fillOpacity="0.15" />
    <text x="194" y="88" fontSize="16" fontWeight="bold" fill="#00B4D8" fillOpacity="0.7" fontFamily="sans-serif">98.2%</text>

    {/* KPI Card 3 */}
    <rect x="272" y="56" width="88" height="50" rx="5" fill="url(#dashboardGradPanel)" stroke="#0A2540" strokeWidth="0.8" strokeOpacity="0.08" />
    <rect x="280" y="64" width="28" height="4" rx="1" fill="#0A2540" fillOpacity="0.15" />
    <text x="280" y="88" fontSize="16" fontWeight="bold" fill="#0A2540" fillOpacity="0.6" fontFamily="sans-serif">1.2s</text>
    {/* Mini sparkline in KPI 3 */}
    <polyline points="330,82 335,78 340,80 345,74 350,76" stroke="#00B4D8" strokeWidth="1.2" fill="none" strokeOpacity="0.5" />

    {/* Bar chart panel */}
    <rect x="100" y="116" width="130" height="100" rx="5" fill="url(#dashboardGradPanel)" stroke="#0A2540" strokeWidth="0.8" strokeOpacity="0.08" />
    <rect x="108" y="122" width="45" height="4" rx="1" fill="#0A2540" fillOpacity="0.12" />

    {/* Bar chart bars */}
    <rect x="112" y="170" width="10" height="36" rx="2" fill="url(#dashboardGradBar)">
      {animate && <animate attributeName="height" values="0;36;36" dur="1s" fill="freeze" />}
      {animate && <animate attributeName="y" values="206;170;170" dur="1s" fill="freeze" />}
    </rect>
    <rect x="126" y="155" width="10" height="51" rx="2" fill="url(#dashboardGradBar)" fillOpacity="0.8">
      {animate && <animate attributeName="height" values="0;51;51" dur="1s" begin="0.15s" fill="freeze" />}
      {animate && <animate attributeName="y" values="206;155;155" dur="1s" begin="0.15s" fill="freeze" />}
    </rect>
    <rect x="140" y="162" width="10" height="44" rx="2" fill="url(#dashboardGradBar)" fillOpacity="0.7">
      {animate && <animate attributeName="height" values="0;44;44" dur="1s" begin="0.3s" fill="freeze" />}
      {animate && <animate attributeName="y" values="206;162;162" dur="1s" begin="0.3s" fill="freeze" />}
    </rect>
    <rect x="154" y="145" width="10" height="61" rx="2" fill="url(#dashboardGradBar)">
      {animate && <animate attributeName="height" values="0;61;61" dur="1s" begin="0.45s" fill="freeze" />}
      {animate && <animate attributeName="y" values="206;145;145" dur="1s" begin="0.45s" fill="freeze" />}
    </rect>
    <rect x="168" y="138" width="10" height="68" rx="2" fill="url(#dashboardGradBar)" fillOpacity="0.9">
      {animate && <animate attributeName="height" values="0;68;68" dur="1s" begin="0.6s" fill="freeze" />}
      {animate && <animate attributeName="y" values="206;138;138" dur="1s" begin="0.6s" fill="freeze" />}
    </rect>
    <rect x="182" y="150" width="10" height="56" rx="2" fill="url(#dashboardGradBar)" fillOpacity="0.75">
      {animate && <animate attributeName="height" values="0;56;56" dur="1s" begin="0.75s" fill="freeze" />}
      {animate && <animate attributeName="y" values="206;150;150" dur="1s" begin="0.75s" fill="freeze" />}
    </rect>
    <rect x="196" y="142" width="10" height="64" rx="2" fill="url(#dashboardGradBar)" fillOpacity="0.85">
      {animate && <animate attributeName="height" values="0;64;64" dur="1s" begin="0.9s" fill="freeze" />}
      {animate && <animate attributeName="y" values="206;142;142" dur="1s" begin="0.9s" fill="freeze" />}
    </rect>

    {/* Axis line */}
    <line x1="108" y1="206" x2="218" y2="206" stroke="#0A2540" strokeWidth="0.8" strokeOpacity="0.15" />

    {/* Line chart panel */}
    <rect x="238" y="116" width="122" height="100" rx="5" fill="url(#dashboardGradPanel)" stroke="#0A2540" strokeWidth="0.8" strokeOpacity="0.08" />
    <rect x="246" y="122" width="50" height="4" rx="1" fill="#0A2540" fillOpacity="0.12" />

    {/* Line chart area fill */}
    <path d="M250 195 L265 180 L280 185 L295 165 L310 170 L325 148 L340 155 L340 206 L250 206 Z" fill="#00B4D8" fillOpacity="0.08" />

    {/* Line chart line */}
    <polyline points="250,195 265,180 280,185 295,165 310,170 325,148 340,155" stroke="#00B4D8" strokeWidth="2" fill="none" strokeLinecap="round" strokeLinejoin="round" />

    {/* Line chart dots */}
    <circle cx="250" cy="195" r="2.5" fill="#00B4D8" fillOpacity="0.7" />
    <circle cx="265" cy="180" r="2.5" fill="#00B4D8" fillOpacity="0.7" />
    <circle cx="280" cy="185" r="2.5" fill="#00B4D8" fillOpacity="0.7" />
    <circle cx="295" cy="165" r="2.5" fill="#00B4D8" fillOpacity="0.7" />
    <circle cx="310" cy="170" r="2.5" fill="#00B4D8" fillOpacity="0.7" />
    <circle cx="325" cy="148" r="3" fill="#00B4D8" stroke="white" strokeWidth="1.5">
      {animate && <animate attributeName="r" values="2.5;4;2.5" dur="2s" repeatCount="indefinite" />}
    </circle>
    <circle cx="340" cy="155" r="2.5" fill="#00B4D8" fillOpacity="0.7" />

    {/* Axis */}
    <line x1="248" y1="206" x2="350" y2="206" stroke="#0A2540" strokeWidth="0.8" strokeOpacity="0.15" />

    {/* Bottom table panel */}
    <rect x="100" y="226" width="260" height="48" rx="5" fill="url(#dashboardGradPanel)" stroke="#0A2540" strokeWidth="0.8" strokeOpacity="0.08" />
    <rect x="108" y="232" width="55" height="4" rx="1" fill="#0A2540" fillOpacity="0.12" />

    {/* Table header */}
    <line x1="108" y1="242" x2="350" y2="242" stroke="#0A2540" strokeWidth="0.5" strokeOpacity="0.08" />

    {/* Table rows */}
    <rect x="108" y="246" width="40" height="3" rx="1" fill="#0A2540" fillOpacity="0.1" />
    <rect x="160" y="246" width="50" height="3" rx="1" fill="#0A2540" fillOpacity="0.08" />
    <rect x="225" y="246" width="30" height="3" rx="1" fill="#00B4D8" fillOpacity="0.2" />
    <rect x="268" y="244" width="22" height="7" rx="3" fill="#00B4D8" fillOpacity="0.15" />
    <circle cx="310" cy="247" r="4" fill="#0A2540" fillOpacity="0.06" />

    <rect x="108" y="256" width="35" height="3" rx="1" fill="#0A2540" fillOpacity="0.08" />
    <rect x="160" y="256" width="45" height="3" rx="1" fill="#0A2540" fillOpacity="0.06" />
    <rect x="225" y="256" width="25" height="3" rx="1" fill="#00B4D8" fillOpacity="0.15" />
    <rect x="268" y="254" width="22" height="7" rx="3" fill="#0A2540" fillOpacity="0.06" />
    <circle cx="310" cy="257" r="4" fill="#0A2540" fillOpacity="0.06" />

    <rect x="108" y="266" width="42" height="3" rx="1" fill="#0A2540" fillOpacity="0.08" />
    <rect x="160" y="266" width="55" height="3" rx="1" fill="#0A2540" fillOpacity="0.06" />
    <rect x="225" y="266" width="35" height="3" rx="1" fill="#00B4D8" fillOpacity="0.15" />
    <rect x="268" y="264" width="22" height="7" rx="3" fill="#00B4D8" fillOpacity="0.1" />
    <circle cx="310" cy="267" r="4" fill="#0A2540" fillOpacity="0.06" />
  </svg>
);
