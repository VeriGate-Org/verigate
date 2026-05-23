/* global React, Logo, Button */

function Nav({ active = 'home', onNavigate }) {
  const links = [
    { id: 'platform',   label: 'Platform' },
    { id: 'verification-types', label: 'Verifications' },
    { id: 'compliance', label: 'Compliance' },
    { id: 'pricing',    label: 'Pricing' },
    { id: 'about',      label: 'About' },
  ];
  return (
    <nav style={{ position: 'sticky', top: 0, zIndex: 50, background: 'rgba(255,255,255,0.92)', backdropFilter: 'blur(8px)', borderBottom: '1px solid #E2E8F0' }}>
      <div style={{ maxWidth: 1200, margin: '0 auto', display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '14px 32px' }}>
        <a href="#" onClick={(e) => { e.preventDefault(); onNavigate && onNavigate('home'); }} style={{ display: 'flex', alignItems: 'center' }}>
          <Logo size={26} />
        </a>
        <div style={{ display: 'flex', alignItems: 'center', gap: 28 }}>
          {links.map(l => (
            <a key={l.id} href={`#${l.id}`} onClick={(e) => { e.preventDefault(); onNavigate && onNavigate(l.id); }}
               style={{ fontSize: 13, fontWeight: active === l.id ? 600 : 500, color: active === l.id ? '#00B3D9' : '#334155', textDecoration: 'none', transition: 'color 200ms cubic-bezier(0.4,0,0.2,1)' }}>
              {l.label}
            </a>
          ))}
        </div>
        <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
          <Button variant="ghost" style={{ padding: '8px 14px', fontSize: 13 }}>Sign in</Button>
          <Button variant="hero" style={{ padding: '8px 16px', fontSize: 13 }} onClick={() => onNavigate && onNavigate('request-demo')}>Book a demo</Button>
        </div>
      </div>
    </nav>
  );
}

window.Nav = Nav;
