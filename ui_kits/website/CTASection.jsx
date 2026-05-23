/* global React, Button, Icon */

function CTASection() {
  return (
    <section style={{ padding: '96px 32px', background: '#F1F5F9' }}>
      <div style={{ maxWidth: 920, margin: '0 auto', background: 'linear-gradient(160deg,#0F1A2E 0%,#1A2E4B 40%,#1a3a5c 70%,#0d2440 100%)', borderRadius: 16, padding: '64px 56px', position: 'relative', overflow: 'hidden', textAlign: 'center' }}>
        <div style={{ position: 'absolute', inset: 0, backgroundImage: 'radial-gradient(circle, rgba(255,255,255,0.06) 1px, transparent 1px)', backgroundSize: '24px 24px' }} />
        <div style={{ position: 'relative' }}>
          <h2 style={{ fontSize: 36, fontWeight: 700, color: '#fff', lineHeight: 1.2, letterSpacing: '-0.01em', margin: '0 0 12px' }}>Ready to verify smarter?</h2>
          <p style={{ fontSize: 16, color: 'rgba(255,255,255,0.7)', lineHeight: 1.6, maxWidth: 540, margin: '0 auto 28px' }}>Talk to our team about volume pricing and API integration — or start with a single check today.</p>
          <div style={{ display: 'flex', gap: 12, justifyContent: 'center', flexWrap: 'wrap' }}>
            <Button variant="hero" icon={<Icon name="ArrowRight" size={16} />}>Get started</Button>
            <Button variant="ghost" style={{ color: '#fff' }}>Book a demo</Button>
          </div>
        </div>
      </div>
    </section>
  );
}

window.CTASection = CTASection;
