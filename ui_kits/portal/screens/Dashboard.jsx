/* global React, PortalIcon, PortalButton, PortalBadge */
const { useState } = React;

/* Tiny inline data — pretend this is from /api */
const ACTIVITY = [
  { id: 'VG-2026-0142', actor: 'You',           verb: 'started verification for',  target: 'John M. Doe',          tone: 'info',    when: '2 min ago' },
  { id: 'VG-2026-0141', actor: 'System',        verb: 'flagged for review',        target: 'Acme Corp Ltd',        tone: 'warning', when: '15 min ago' },
  { id: 'VG-2026-0140', actor: 'Bureau',        verb: 'returned a fail on',        target: 'Jane Smith',           tone: 'danger',  when: '1 hr ago' },
  { id: 'VG-2026-0138', actor: 'SAPS',          verb: 'returned clean record for', target: 'Lerato Mokoena',       tone: 'success', when: '2 hr ago' },
  { id: 'VG-2026-0136', actor: 'Naledi N.',     verb: 'completed biometric for',   target: 'her own onboard',      tone: 'success', when: 'Yesterday' },
];

const SPARK = [38, 42, 41, 47, 45, 52, 49, 58, 55, 61, 64, 72];

function MiniSpark({ data, color = '#00B3D9' }) {
  const max = Math.max(...data);
  const min = Math.min(...data);
  const w = 120, h = 36;
  const pts = data.map((v, i) => {
    const x = (i / (data.length - 1)) * w;
    const y = h - ((v - min) / (max - min || 1)) * (h - 4) - 2;
    return `${x},${y}`;
  }).join(' ');
  return (
    <svg width={w} height={h} viewBox={`0 0 ${w} ${h}`} style={{ display: 'block' }}>
      <polyline fill="none" stroke={color} strokeWidth="1.5" points={pts} />
      <polyline fill={`${color}22`} stroke="none" points={`0,${h} ${pts} ${w},${h}`} />
    </svg>
  );
}

function StatTile({ label, value, delta, sub, spark, sparkColor }) {
  return (
    <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, padding: '14px 16px 12px', display: 'flex', flexDirection: 'column', gap: 4, boxShadow: '0 1px 1px 0 rgba(0,28,36,0.30)' }}>
      <div style={{ fontSize: 10, color: '#4F5B67', textTransform: 'uppercase', letterSpacing: '0.06em', fontWeight: 600 }}>{label}</div>
      <div style={{ display: 'flex', alignItems: 'flex-end', justifyContent: 'space-between', gap: 8 }}>
        <div>
          <div style={{ fontSize: 26, fontWeight: 700, color: '#1A2024', lineHeight: 1.1, fontFamily: "'Inter', sans-serif" }}>{value}</div>
          {sub && <div style={{ fontSize: 11, color: '#4F5B67', marginTop: 2 }}>{sub}</div>}
        </div>
        {spark && <MiniSpark data={spark} color={sparkColor || '#00B3D9'} />}
      </div>
      {delta && (
        <div style={{ marginTop: 4, fontSize: 11, color: delta.startsWith('-') ? '#E23D36' : '#2C974B', fontWeight: 600, display: 'inline-flex', alignItems: 'center', gap: 4 }}>
          <PortalIcon name="ArrowUpRight" size={11} />{delta}
        </div>
      )}
    </div>
  );
}

function Dashboard({ onOpenVerification, onStartNew, rowsCount }) {
  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 20 }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-end' }}>
        <div>
          <div style={{ fontSize: 11, color: '#4F5B67', textTransform: 'uppercase', letterSpacing: '0.08em', fontWeight: 600 }}>Dashboard</div>
          <h1 style={{ fontSize: 26, fontWeight: 600, color: '#1A2024', marginTop: 4, letterSpacing: '-0.01em' }}>Welcome back, Arthur</h1>
          <div style={{ fontSize: 13, color: '#4F5B67', marginTop: 4 }}>Here's what's happened across your verifications today.</div>
        </div>
        <div style={{ display: 'flex', gap: 8 }}>
          <PortalButton variant="secondary" icon={<PortalIcon name="Download" size={13} color="#1A2E4B" />}>Export today</PortalButton>
          <PortalButton variant="cta" icon={<PortalIcon name="Plus" size={13} color="#fff" />} onClick={onStartNew}>New verification</PortalButton>
        </div>
      </div>

      {/* KPI tiles */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: 14 }}>
        <StatTile label="Verifications today" value="72" delta="+12%" sub="vs yesterday" spark={SPARK} />
        <StatTile label="Pass rate" value="94.2%" delta="+0.3pt" sub="Industry avg 91.0%" spark={[88, 90, 89, 91, 92, 93, 93, 94, 93, 94, 94.2, 94.2]} sparkColor="#2C974B" />
        <StatTile label="Avg. turnaround" value="2.4m" delta="-18s" sub="P95 under 9m" spark={[3.4, 3.2, 3.0, 3.1, 2.9, 2.8, 2.7, 2.6, 2.6, 2.5, 2.5, 2.4]} sparkColor="#00B3D9" />
        <StatTile label="Spend (mo.)" value="R 18,420" sub="Of R 25,000 budget" spark={[2,4,5,7,9,11,12,14,15,16,17.5,18.4]} sparkColor="#1A2E4B" />
      </div>

      {/* Two-column: alerts + activity */}
      <div style={{ display: 'grid', gridTemplateColumns: '1.4fr 1fr', gap: 16 }}>
        <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, padding: 0, overflow: 'hidden' }}>
          <div style={{ display: 'flex', height: 3 }}>
            <div style={{ flex: 3, background: '#E23D36' }} />
            <div style={{ flex: 5, background: '#1A2E4B' }} />
            <div style={{ flex: 2, background: '#00B3D9' }} />
          </div>
          <div style={{ padding: '14px 18px', display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderBottom: '1px solid #e9ebed' }}>
            <div>
              <div style={{ fontSize: 14, fontWeight: 600, color: '#1A2024' }}>Needs your attention</div>
              <div style={{ fontSize: 11, color: '#4F5B67', marginTop: 2 }}>{rowsCount} open · 3 escalated · 1 SLA at risk</div>
            </div>
            <PortalButton variant="link" onClick={onOpenVerification}>View all →</PortalButton>
          </div>
          <div style={{ display: 'flex', flexDirection: 'column' }}>
            {[
              { id: 'VG-2026-0140', who: 'Jane Smith',     why: 'Credit bureau returned a hard fail — manual review required.',  badge: 'danger',  badgeLabel: 'Failed',    age: '1 hr ago' },
              { id: 'VG-2026-0141', who: 'Acme Corp Ltd',  why: 'Sanctions list match — partial name, score 0.78.',              badge: 'warning', badgeLabel: 'Review',    age: '15 min ago' },
              { id: 'VG-2026-0139', who: 'Bob Williams',   why: 'Waiting on Department of Home Affairs (12m elapsed, SLA 30m).', badge: 'info',    badgeLabel: 'In Progress', age: 'Just now' },
            ].map(a => (
              <button key={a.id} onClick={() => onOpenVerification(a.id)} style={{ display: 'flex', alignItems: 'flex-start', gap: 12, padding: '12px 18px', borderTop: '1px solid #f1f5f9', background: 'transparent', border: 'none', borderBottom: 0, textAlign: 'left', cursor: 'pointer', fontFamily: "'Inter', sans-serif" }}
                onMouseEnter={e => e.currentTarget.style.background = '#F8FAFC'}
                onMouseLeave={e => e.currentTarget.style.background = 'transparent'}
              >
                <PortalBadge variant={a.badge} size="sm">{a.badgeLabel}</PortalBadge>
                <div style={{ flex: 1 }}>
                  <div style={{ display: 'flex', alignItems: 'baseline', gap: 8, marginBottom: 2 }}>
                    <span style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: 11, color: '#00B3D9' }}>{a.id}</span>
                    <span style={{ fontSize: 13, fontWeight: 600, color: '#1A2024' }}>{a.who}</span>
                  </div>
                  <div style={{ fontSize: 12, color: '#4F5B67', lineHeight: 1.5 }}>{a.why}</div>
                </div>
                <span style={{ fontSize: 11, color: '#4F5B67', whiteSpace: 'nowrap' }}>{a.age}</span>
              </button>
            ))}
          </div>
        </div>

        <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, overflow: 'hidden' }}>
          <div style={{ padding: '14px 18px', borderBottom: '1px solid #e9ebed' }}>
            <div style={{ fontSize: 14, fontWeight: 600 }}>Activity</div>
            <div style={{ fontSize: 11, color: '#4F5B67', marginTop: 2 }}>Last 24 hours</div>
          </div>
          <div style={{ display: 'flex', flexDirection: 'column', padding: '8px 0' }}>
            {ACTIVITY.map((a, i) => (
              <div key={a.id + i} style={{ display: 'flex', gap: 12, padding: '8px 18px', alignItems: 'flex-start' }}>
                <span style={{ width: 6, height: 6, borderRadius: 999, background: { info: '#00B3D9', success: '#2C974B', warning: '#C28B0B', danger: '#E23D36' }[a.tone], marginTop: 6, flexShrink: 0 }} />
                <div style={{ flex: 1 }}>
                  <div style={{ fontSize: 12, color: '#1A2024', lineHeight: 1.5 }}>
                    <b style={{ fontWeight: 600 }}>{a.actor}</b> {a.verb} <b style={{ fontWeight: 600 }}>{a.target}</b>
                  </div>
                  <div style={{ fontSize: 10, color: '#4F5B67', marginTop: 2, fontFamily: "'JetBrains Mono', monospace" }}>{a.id} · {a.when}</div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}

window.Dashboard = Dashboard;
