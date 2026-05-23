/* global React, Button, Icon */

function Hero({ onPrimary }) {
  return (
    <section style={{ background: 'linear-gradient(160deg,#0F1A2E 0%,#1A2E4B 40%,#1a3a5c 70%,#0d2440 100%)', position: 'relative', overflow: 'hidden', padding: '96px 32px 120px' }}>
      <div style={{ position: 'absolute', inset: 0, background: 'radial-gradient(ellipse 60% 50% at 50% 50%, rgba(0,179,217,0.15) 0%, transparent 70%)' }} />
      <div style={{ position: 'absolute', inset: 0, backgroundImage: 'radial-gradient(circle, rgba(255,255,255,0.08) 1px, transparent 1px)', backgroundSize: '24px 24px' }} />
      <div style={{ position: 'relative', maxWidth: 1100, margin: '0 auto', textAlign: 'center' }}>
        <div style={{ display: 'inline-flex', alignItems: 'center', gap: 8, padding: '6px 14px', borderRadius: 999, background: 'rgba(0,179,217,0.12)', border: '1px solid rgba(0,179,217,0.3)', marginBottom: 24 }}>
          <span style={{ width: 6, height: 6, borderRadius: 999, background: '#00B3D9', boxShadow: '0 0 8px #00B3D9' }} />
          <span style={{ fontSize: 12, color: '#a5f3fc', fontWeight: 500, letterSpacing: 0.04, textTransform: 'uppercase' }}>Realtime risk intelligence</span>
        </div>
        <h1 style={{ fontFamily: "'Inter', sans-serif", fontSize: 60, fontWeight: 700, color: '#fff', lineHeight: 1.05, letterSpacing: '-0.02em', maxWidth: 880, margin: '0 auto 18px' }}>
          Identity verification, <span style={{ background: 'linear-gradient(90deg,#22d3ee,#60a5fa,#06b6d4)', WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent', backgroundClip: 'text' }}>redefined</span>.
        </h1>
        <p style={{ fontSize: 18, color: 'rgba(255,255,255,0.72)', lineHeight: 1.6, maxWidth: 640, margin: '0 auto 32px' }}>
          Document scanning, biometric checks, and sanctions screening — fully POPIA-compliant, with verifications completed from one hour.
        </p>
        <div style={{ display: 'flex', gap: 12, justifyContent: 'center', flexWrap: 'wrap' }}>
          <Button variant="hero" onClick={onPrimary} icon={<Icon name="ArrowRight" size={16} />}>Start verifying</Button>
          <Button variant="ghost" style={{ color: '#fff' }}>Talk to sales</Button>
        </div>
        <div style={{ marginTop: 56, display: 'flex', gap: 28, justifyContent: 'center', alignItems: 'center', flexWrap: 'wrap', opacity: 0.65 }}>
          {['SAPS', 'DHA', 'SAQA', 'Umalusi', 'TransUnion', 'CIPC'].map(s => (
            <span key={s} style={{ fontSize: 12, color: 'rgba(255,255,255,0.6)', fontWeight: 600, letterSpacing: '0.08em' }}>{s}</span>
          ))}
        </div>
      </div>
    </section>
  );
}

window.Hero = Hero;
