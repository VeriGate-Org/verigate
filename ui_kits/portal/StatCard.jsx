/* global React, PortalIcon */

function StatCard({ label, value, delta, deltaTone = 'success', helper }) {
  const tone = { success: '#2C974B', warning: '#C28B0B', danger: '#D13212', muted: '#4F5B67' }[deltaTone];
  return (
    <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, padding: '16px 18px', boxShadow: '0 1px 1px 0 rgba(0,28,36,0.30)' }}>
      <div style={{ fontSize: 11, color: '#4F5B67', textTransform: 'uppercase', letterSpacing: '0.06em', fontWeight: 600 }}>{label}</div>
      <div style={{ display: 'flex', alignItems: 'baseline', gap: 8, marginTop: 6 }}>
        <span style={{ fontSize: 28, fontWeight: 700, color: '#1A2024', lineHeight: 1.1, fontFamily: "'Inter', sans-serif" }}>{value}</span>
        {delta && <span style={{ fontSize: 12, fontWeight: 600, color: tone, display: 'inline-flex', alignItems: 'center', gap: 2 }}>
          <PortalIcon name="ArrowUpRight" size={12} />{delta}
        </span>}
      </div>
      {helper && <div style={{ fontSize: 11, color: '#4F5B67', marginTop: 6 }}>{helper}</div>}
    </div>
  );
}

window.PortalStatCard = StatCard;
