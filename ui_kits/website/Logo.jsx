/* global React */

function Logo({ variant = 'light', size = 28, withWord = true, ...rest }) {
  const wordFill = variant === 'dark' ? '#FFFFFF' : '#1A1A2E';
  const w = withWord ? size * 5.6 : size * 0.83;
  const h = size * 1.2;
  if (!withWord) {
    return (
      <svg width={size * 0.92} height={size} viewBox="20 25 112 122" {...rest}>
        <path fill="#E23D36" d="M76 30 C56 30 26 39 26 42 L26 74 C26 106 50 132 76 142 C102 132 126 106 126 74 L126 42 C126 39 96 30 76 30 Z"/>
        <path d="M46 84 L63 102 L106 58" fill="none" stroke="#FFFFFF" strokeWidth="13" strokeLinecap="round" strokeLinejoin="round"/>
      </svg>
    );
  }
  return (
    <svg width={w} height={h} viewBox="45 58 245 60" {...rest}>
      <g transform="translate(76, 87) scale(0.42) translate(-76, -87)">
        <path fill="#E23D36" d="M76 30 C56 30 26 39 26 42 L26 74 C26 106 50 132 76 142 C102 132 126 106 126 74 L126 42 C126 39 96 30 76 30 Z"/>
        <path d="M46 84 L63 102 L106 58" fill="none" stroke="#FFFFFF" strokeWidth="13" strokeLinecap="round" strokeLinejoin="round"/>
      </g>
      <text x="108" y="101" fontFamily="Manrope, sans-serif" fontWeight="500" fontSize="38" fill={wordFill} letterSpacing="-2">VeriGate</text>
    </svg>
  );
}

window.Logo = Logo;
