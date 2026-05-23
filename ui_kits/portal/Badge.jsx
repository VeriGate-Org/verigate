/* global React */
/* Brand-aligned status badges. Brand red for danger so the shield + portal share a hue. */

const badgeStyles = {
  neutral: { bg: '#F2F3F3', color: '#1A2024', border: '#D5DBDB',                                     glyph: null },
  success: { bg: 'rgba(44,151,75,0.10)', color: '#2C974B', border: 'rgba(44,151,75,0.25)',           glyph: '✓' },
  warning: { bg: 'rgba(194,139,11,0.10)', color: '#C28B0B', border: 'rgba(194,139,11,0.25)',         glyph: '⚠' },
  danger:  { bg: 'rgba(226,61,54,0.10)',  color: '#E23D36', border: 'rgba(226,61,54,0.25)',          glyph: '✗' },
  info:    { bg: 'rgba(0,179,217,0.10)',  color: '#0099bb', border: 'rgba(0,179,217,0.25)',          glyph: 'ⓘ' },
  pending: { bg: 'rgba(79,91,103,0.10)',  color: '#4F5B67', border: 'rgba(79,91,103,0.25)',          glyph: '⊙' },
};

function Badge({ variant = 'neutral', children, size = 'sm', noGlyph, style }) {
  const s = badgeStyles[variant];
  const pad = size === 'lg' ? '5px 12px' : '3px 9px';
  const fs  = size === 'lg' ? 13 : 12;
  return (
    <span style={{ display: 'inline-flex', alignItems: 'center', gap: 4, padding: pad, borderRadius: 16, fontSize: fs, fontWeight: 500, background: s.bg, color: s.color, border: `1px solid ${s.border}`, fontFamily: "'Inter', sans-serif", lineHeight: 1.3, whiteSpace: 'nowrap', ...style }}>
      {s.glyph && !noGlyph && <span aria-hidden="true">{s.glyph}</span>}
      {children}
    </span>
  );
}

window.PortalBadge = Badge;
