/* global React, Logo */

function Footer() {
  const cols = [
    { title: 'Services', items: ['KYC Verification', 'Criminal Checks', 'Credit Bureau', 'Sanctions & PEP', 'Document Authentication'] },
    { title: 'Company',  items: ['About', 'Compliance', 'Careers', 'Contact', 'Press'] },
    { title: 'Legal',    items: ['Terms of Service', 'Privacy Policy', 'POPIA Notice', 'Cookie Policy'] },
  ];
  return (
    <footer style={{ background: '#0F1A2E', color: 'rgba(255,255,255,0.6)', paddingTop: 64, position: 'relative' }}>
      <div style={{ maxWidth: 1100, margin: '0 auto', padding: '0 32px', display: 'grid', gridTemplateColumns: '1.4fr 1fr 1fr 1fr', gap: 48 }}>
        <div>
          <Logo variant="dark" size={24} />
          <p style={{ marginTop: 16, fontSize: 13, lineHeight: 1.7, maxWidth: 280 }}>
            South African background screening and verification technology. POPIA-compliant. Level 1 B-BBEE.
          </p>
          <div style={{ marginTop: 20, fontSize: 12, lineHeight: 1.8 }}>
            <div>+27 82 211 8921</div>
            <div style={{ color: '#00B3D9' }}>info@verigate.co.za</div>
            <div>1 Cinnebar St, Table View, 7441</div>
          </div>
        </div>
        {cols.map(c => (
          <div key={c.title}>
            <div style={{ fontSize: 11, color: '#fff', fontWeight: 600, textTransform: 'uppercase', letterSpacing: '0.1em', marginBottom: 14 }}>{c.title}</div>
            <ul style={{ listStyle: 'none', padding: 0, margin: 0, fontSize: 13, lineHeight: 2 }}>
              {c.items.map(i => <li key={i}><a href="#" style={{ color: 'rgba(255,255,255,0.6)', textDecoration: 'none' }}>{i}</a></li>)}
            </ul>
          </div>
        ))}
      </div>
      <div style={{ borderTop: '1px solid rgba(255,255,255,0.08)', marginTop: 56, padding: '20px 32px', maxWidth: 1100, marginLeft: 'auto', marginRight: 'auto', display: 'flex', justifyContent: 'space-between', alignItems: 'center', fontSize: 11 }}>
        <span>© 2026 VeriGate (Pty) Ltd · Reg. 2025/525145/07</span>
        <span style={{ color: 'rgba(255,255,255,0.4)' }}>Realtime Risk Intelligence</span>
      </div>
      <div style={{ height: 3, display: 'flex' }}>
        <div style={{ flex: 3, background: '#D63031' }} />
        <div style={{ flex: 5, background: '#1A2E4B' }} />
        <div style={{ flex: 2, background: '#00B3D9' }} />
      </div>
    </footer>
  );
}

window.Footer = Footer;
