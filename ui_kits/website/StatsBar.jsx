/* global React */

function StatsBar({ items }) {
  return (
    <section style={{ background: '#1A2E4B', padding: '48px 32px', position: 'relative' }}>
      <div style={{ position: 'absolute', top: 0, left: 0, right: 0, height: 3, background: 'linear-gradient(90deg,#D63031 25%,#1A2E4B 25% 75%,#00B3D9 75%)' }} />
      <div style={{ maxWidth: 1100, margin: '0 auto', display: 'grid', gridTemplateColumns: `repeat(${items.length}, 1fr)`, gap: 24 }}>
        {items.map((it, i) => (
          <div key={i} style={{ textAlign: 'center' }}>
            <div style={{ fontSize: 44, fontWeight: 700, color: '#E23D36', lineHeight: 1, fontFamily: "'Inter', sans-serif" }}>{it.value}</div>
            <div style={{ fontSize: 11, color: 'rgba(255,255,255,0.7)', marginTop: 8, textTransform: 'uppercase', letterSpacing: '0.08em', fontWeight: 500 }}>{it.label}</div>
          </div>
        ))}
      </div>
    </section>
  );
}

window.StatsBar = StatsBar;
