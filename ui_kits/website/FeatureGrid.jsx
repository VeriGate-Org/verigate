/* global React, Icon */

function FeatureCard({ icon, title, body }) {
  const [hover, setHover] = React.useState(false);
  return (
    <div
      onMouseEnter={() => setHover(true)}
      onMouseLeave={() => setHover(false)}
      style={{
        background: '#fff',
        border: `1px solid ${hover ? 'rgba(0,179,217,0.5)' : '#E2E8F0'}`,
        borderRadius: 8,
        padding: 28,
        transition: 'all 300ms cubic-bezier(0.4,0,0.2,1)',
        transform: hover ? 'translateY(-4px)' : 'none',
        boxShadow: hover ? '0 20px 25px -5px rgba(0,179,217,0.10)' : 'none',
      }}
    >
      <div style={{ width: 44, height: 44, borderRadius: 8, background: 'rgba(0,179,217,0.10)', display: 'flex', alignItems: 'center', justifyContent: 'center', marginBottom: 16 }}>
        <Icon name={icon} size={22} color="#00B3D9" />
      </div>
      <h3 style={{ fontSize: 18, fontWeight: 600, color: '#1A2E4B', margin: '0 0 6px' }}>{title}</h3>
      <p style={{ fontSize: 14, color: '#64748B', lineHeight: 1.6, margin: 0 }}>{body}</p>
    </div>
  );
}

function FeatureGrid({ title, subtitle, items }) {
  return (
    <section style={{ padding: '96px 32px', background: '#fff' }}>
      <div style={{ maxWidth: 1100, margin: '0 auto' }}>
        <div style={{ textAlign: 'center', marginBottom: 56 }}>
          <span style={{ fontSize: 12, fontWeight: 600, color: '#00B3D9', textTransform: 'uppercase', letterSpacing: '0.1em' }}>What we do</span>
          <h2 style={{ fontSize: 38, fontWeight: 700, color: '#1A2E4B', margin: '8px 0 12px', letterSpacing: '-0.01em' }}>{title}</h2>
          <p style={{ fontSize: 16, color: '#64748B', maxWidth: 580, margin: '0 auto', lineHeight: 1.6 }}>{subtitle}</p>
        </div>
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: 24 }}>
          {items.map((it, i) => <FeatureCard key={i} {...it} />)}
        </div>
      </div>
    </section>
  );
}

window.FeatureGrid = FeatureGrid;
window.FeatureCard = FeatureCard;
