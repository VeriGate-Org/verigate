/* global React */
/* Brand-aligned portal buttons. 2px radius · 100ms · 6 variants. */

const portalBtn = {
  base: { display: 'inline-flex', alignItems: 'center', justifyContent: 'center', gap: 6, padding: '7px 14px', borderRadius: 2, fontSize: 13, fontWeight: 500, fontFamily: "'Inter', sans-serif", cursor: 'pointer', border: '1px solid transparent', transition: 'all 100ms cubic-bezier(0.4,0,0.2,1)', whiteSpace: 'nowrap', lineHeight: 1.2 },
  primary:     { background: '#1A2E4B', borderColor: '#0f1f36', color: '#fff' },
  secondary:   { background: '#fff', borderColor: '#CBD5E1', color: '#1A2E4B' },
  cta:         { background: '#00B3D9', borderColor: '#00B3D9', color: '#fff', fontWeight: 600 },
  destructive: { background: '#E23D36', borderColor: '#E23D36', color: '#fff' },
  ghost:       { background: 'transparent', borderColor: 'transparent', color: '#1A2E4B' },
  link:        { background: 'transparent', borderColor: 'transparent', color: '#00B3D9', padding: 0, fontWeight: 500 },
};

const portalBtnHover = {
  primary:     { background: '#0f1f36' },
  secondary:   { background: '#F8FAFC', borderColor: '#1A2E4B', color: '#1A2E4B' },
  cta:         { background: '#0099bb', borderColor: '#0099bb', boxShadow: '0 4px 12px rgba(0,179,217,0.25)' },
  destructive: { background: '#c82f29', borderColor: '#c82f29' },
  ghost:       { background: 'rgba(0,179,217,0.08)', color: '#00B3D9' },
  link:        { textDecoration: 'underline' },
};

function PortalButton({ variant = 'primary', children, onClick, style, icon, iconRight, disabled, size = 'md', ...rest }) {
  const [hover, setHover] = React.useState(false);
  const sizeStyle = size === 'sm' ? { padding: '4px 10px', fontSize: 12 } : size === 'lg' ? { padding: '10px 20px', fontSize: 14 } : {};
  return (
    <button
      onClick={onClick}
      disabled={disabled}
      onMouseEnter={() => setHover(true)}
      onMouseLeave={() => setHover(false)}
      style={{ ...portalBtn.base, ...portalBtn[variant], ...sizeStyle, ...(hover && !disabled ? portalBtnHover[variant] : {}), opacity: disabled ? 0.5 : 1, cursor: disabled ? 'not-allowed' : 'pointer', ...style }}
      {...rest}
    >
      {icon && <span style={{ display: 'inline-flex' }}>{icon}</span>}
      {children}
      {iconRight && <span style={{ display: 'inline-flex' }}>{iconRight}</span>}
    </button>
  );
}

window.PortalButton = PortalButton;
