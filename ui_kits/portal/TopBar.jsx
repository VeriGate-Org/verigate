/* global React, PortalIcon */

function TopBar({ breadcrumbs = [], user = { name: 'Arthur Manena', initial: 'A' }, theme = 'light', onToggleTheme }) {
  const isDark = theme === 'dark';
  const t = isDark ? {
    bg: '#0f1f36', border: 'rgba(255,255,255,0.08)', text: '#fff', textMuted: 'rgba(255,255,255,0.6)',
    sep: 'rgba(255,255,255,0.10)', inputBg: 'rgba(0,0,0,0.25)', inputBorder: 'rgba(255,255,255,0.10)',
    iconBg: 'rgba(255,255,255,0.06)', iconBorder: 'rgba(255,255,255,0.10)',
  } : {
    bg: '#fff', border: '#E2E8F0', text: '#1A2024', textMuted: '#4F5B67',
    sep: '#E2E8F0', inputBg: '#fff', inputBorder: '#D5DBDB',
    iconBg: '#fff', iconBorder: '#D5DBDB',
  };
  return (
    <header style={{ height: 60, background: t.bg, borderBottom: `1px solid ${t.border}`, display: 'flex', alignItems: 'center', padding: '0 24px', gap: 24, flexShrink: 0, transition: 'background 200ms cubic-bezier(0.4,0,0.2,1)' }}>
      <nav style={{ flex: 1, display: 'flex', alignItems: 'center', gap: 6, fontSize: 13 }}>
        {breadcrumbs.map((b, i) => (
          <React.Fragment key={i}>
            {i > 0 && <PortalIcon name="ChevronRight" size={12} color={t.textMuted} />}
            <span style={{ color: i === breadcrumbs.length - 1 ? t.text : t.textMuted, fontWeight: i === breadcrumbs.length - 1 ? 600 : 400 }}>{b}</span>
          </React.Fragment>
        ))}
      </nav>
      <div style={{ position: 'relative', width: 320 }}>
        <span style={{ position: 'absolute', left: 10, top: '50%', transform: 'translateY(-50%)', display: 'inline-flex' }}>
          <PortalIcon name="Search" size={14} color={t.textMuted} />
        </span>
        <input
          placeholder="Search verifications, IDs, or correlation numbers…"
          style={{ width: '100%', padding: '7px 12px 7px 32px', borderRadius: 4, border: `1px solid ${t.inputBorder}`, background: t.inputBg, fontSize: 13, fontFamily: "'Inter', sans-serif", color: t.text, outline: 'none' }}
          onFocus={e => { e.currentTarget.style.borderColor = '#00B3D9'; e.currentTarget.style.boxShadow = '0 0 0 3px rgba(0,179,217,0.15)'; }}
          onBlur={e => { e.currentTarget.style.borderColor = t.inputBorder; e.currentTarget.style.boxShadow = 'none'; }}
        />
      </div>
      {/* Theme toggle */}
      <button onClick={onToggleTheme} title={isDark ? 'Switch to light mode' : 'Switch to dark mode'} style={{ width: 32, height: 32, border: `1px solid ${t.iconBorder}`, borderRadius: 4, background: t.iconBg, cursor: 'pointer', display: 'inline-flex', alignItems: 'center', justifyContent: 'center', transition: 'all 100ms cubic-bezier(0.4,0,0.2,1)' }}
        onMouseEnter={e => { e.currentTarget.style.borderColor = '#00B3D9'; }}
        onMouseLeave={e => { e.currentTarget.style.borderColor = t.iconBorder; }}>
        {isDark ? (
          /* Sun icon — switch to light */
          <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="#FBBF24" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
            <circle cx="12" cy="12" r="4" />
            <path d="M12 2v2M12 20v2M4.93 4.93l1.41 1.41M17.66 17.66l1.41 1.41M2 12h2M20 12h2M4.93 19.07l1.41-1.41M17.66 6.34l1.41-1.41" />
          </svg>
        ) : (
          /* Moon icon — switch to dark */
          <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="#4F5B67" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
            <path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z" />
          </svg>
        )}
      </button>
      <button title="Notifications" style={{ width: 32, height: 32, border: `1px solid ${t.iconBorder}`, borderRadius: 4, background: t.iconBg, cursor: 'pointer', display: 'inline-flex', alignItems: 'center', justifyContent: 'center', position: 'relative' }}>
        <PortalIcon name="Bell" size={15} color={t.textMuted} />
        <span style={{ position: 'absolute', top: 6, right: 6, width: 6, height: 6, borderRadius: 999, background: '#E23D36' }} />
      </button>
      <div style={{ display: 'flex', alignItems: 'center', gap: 8, paddingLeft: 16, borderLeft: `1px solid ${t.sep}`, cursor: 'pointer' }}>
        <div style={{ width: 30, height: 30, borderRadius: 999, background: '#1A2E4B', color: '#fff', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: 12, fontWeight: 600 }}>{user.initial}</div>
        <div style={{ display: 'flex', flexDirection: 'column' }}>
          <span style={{ fontSize: 12, fontWeight: 600, color: t.text }}>{user.name}</span>
          <span style={{ fontSize: 10, color: t.textMuted }}>VeriGate Admin</span>
        </div>
        <PortalIcon name="ChevronDown" size={12} color={t.textMuted} />
      </div>
    </header>
  );
}

window.PortalTopBar = TopBar;
