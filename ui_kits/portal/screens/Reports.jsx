/* global React, PortalIcon, PortalButton, PortalBadge */

function Bar({ value, max, color }) {
  return (
    <div style={{ flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 4 }}>
      <div style={{ height: 120, width: '100%', display: 'flex', alignItems: 'flex-end' }}>
        <div style={{ width: '100%', background: color, height: `${(value / max) * 100}%`, borderRadius: '4px 4px 0 0', minHeight: 2, transition: 'height 400ms cubic-bezier(0.4,0,0.2,1)' }} />
      </div>
    </div>
  );
}

function Reports() {
  const days = ['Mon','Tue','Wed','Thu','Fri','Sat','Sun'];
  const completed = [42, 51, 48, 63, 72, 28, 12];
  const failed    = [3, 4, 2, 5, 6, 2, 1];
  const review    = [5, 6, 4, 7, 9, 3, 2];
  const maxV = Math.max(...completed) * 1.1;

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-end' }}>
        <div>
          <div style={{ fontSize: 11, color: '#4F5B67', textTransform: 'uppercase', letterSpacing: '0.08em', fontWeight: 600 }}>Reports</div>
          <h1 style={{ fontSize: 26, fontWeight: 600, color: '#1A2024', marginTop: 4, letterSpacing: '-0.01em' }}>Weekly verification report</h1>
          <div style={{ fontSize: 13, color: '#4F5B67', marginTop: 4 }}>Volume, pass rate, and provider performance for the week of 12–18 May 2026.</div>
        </div>
        <div style={{ display: 'flex', gap: 8 }}>
          <select style={{ padding: '6px 10px', border: '1px solid #D5DBDB', borderRadius: 4, background: '#fff', fontSize: 12, fontFamily: "'Inter', sans-serif" }}>
            <option>This week</option><option>Last week</option><option>This month</option><option>Last quarter</option>
          </select>
          <PortalButton variant="secondary" icon={<PortalIcon name="Download" size={13} color="#1A2E4B" />}>Export PDF</PortalButton>
        </div>
      </div>

      {/* Headline KPIs */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: 14 }}>
        {[
          { label: 'Total this week', value: '316',     delta: '+18% WoW',  tone: '#2C974B' },
          { label: 'Pass rate',       value: '93.4%',   delta: '+0.6pt',     tone: '#2C974B' },
          { label: 'Manual reviews',  value: '36',      delta: '+11% WoW',  tone: '#E23D36' },
          { label: 'Avg. cost / check', value: 'R 78',  delta: '−R 4',      tone: '#2C974B' },
        ].map(s => (
          <div key={s.label} style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, padding: '14px 18px' }}>
            <div style={{ fontSize: 10, color: '#4F5B67', textTransform: 'uppercase', letterSpacing: '0.06em', fontWeight: 600 }}>{s.label}</div>
            <div style={{ fontSize: 28, fontWeight: 700, color: '#1A2024', lineHeight: 1.1, marginTop: 4 }}>{s.value}</div>
            <div style={{ fontSize: 11, fontWeight: 600, color: s.tone, marginTop: 4 }}>{s.delta}</div>
          </div>
        ))}
      </div>

      {/* Volume chart */}
      <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, padding: '18px 22px' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
          <div>
            <div style={{ fontSize: 14, fontWeight: 600 }}>Daily volume</div>
            <div style={{ fontSize: 11, color: '#4F5B67', marginTop: 2 }}>Completed · Manual review · Failed</div>
          </div>
          <div style={{ display: 'flex', gap: 14, fontSize: 11 }}>
            <span style={{ display: 'inline-flex', alignItems: 'center', gap: 6 }}><span style={{ width: 10, height: 10, background: '#1A2E4B', borderRadius: 2 }} /> Completed</span>
            <span style={{ display: 'inline-flex', alignItems: 'center', gap: 6 }}><span style={{ width: 10, height: 10, background: '#C28B0B', borderRadius: 2 }} /> Review</span>
            <span style={{ display: 'inline-flex', alignItems: 'center', gap: 6 }}><span style={{ width: 10, height: 10, background: '#E23D36', borderRadius: 2 }} /> Failed</span>
          </div>
        </div>
        <div style={{ display: 'flex', gap: 28, alignItems: 'flex-end', padding: '0 20px' }}>
          {days.map((d, i) => (
            <div key={d} style={{ flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 8 }}>
              <div style={{ display: 'flex', gap: 4, width: '100%', justifyContent: 'center', alignItems: 'flex-end', height: 140 }}>
                <Bar value={completed[i]} max={maxV} color="#1A2E4B" />
                <Bar value={review[i]}    max={maxV} color="#C28B0B" />
                <Bar value={failed[i]}    max={maxV} color="#E23D36" />
              </div>
              <span style={{ fontSize: 11, color: '#4F5B67', fontWeight: 500 }}>{d}</span>
              <span style={{ fontSize: 10, color: '#4F5B67', fontFamily: "'JetBrains Mono', monospace" }}>{completed[i]}/{review[i]}/{failed[i]}</span>
            </div>
          ))}
        </div>
      </div>

      {/* Two-up: provider performance + breakdown */}
      <div style={{ display: 'grid', gridTemplateColumns: '1.4fr 1fr', gap: 16 }}>
        <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, overflow: 'hidden' }}>
          <div style={{ padding: '14px 18px', borderBottom: '1px solid #e9ebed' }}>
            <div style={{ fontSize: 14, fontWeight: 600 }}>Provider performance</div>
            <div style={{ fontSize: 11, color: '#4F5B67', marginTop: 2 }}>SLA = 30 min, except SAPS (1 hr)</div>
          </div>
          <table style={{ width: '100%', borderCollapse: 'separate', borderSpacing: 0, fontSize: 12 }}>
            <thead style={{ background: '#F2F3F3' }}>
              <tr>{['Provider', 'Checks', 'Avg. response', 'Within SLA', ''].map(h => <th key={h} style={{ padding: '8px 14px', textAlign: 'left', fontSize: 10, color: '#4F5B67', textTransform: 'uppercase', letterSpacing: '0.05em', fontWeight: 600, borderBottom: '1px solid #e9ebed' }}>{h}</th>)}</tr>
            </thead>
            <tbody>
              {[
                { name: 'DHA',        checks: 142, avg: '34 s',   sla: 99.3, status: 'success' },
                { name: 'SAPS-AFIS',  checks: 88,  avg: '42 min', sla: 92.1, status: 'warning' },
                { name: 'TransUnion', checks: 71,  avg: '6.2 s',  sla: 100,  status: 'success' },
                { name: 'OFAC + EU',  checks: 65,  avg: '1.1 s',  sla: 100,  status: 'success' },
                { name: 'Umalusi',    checks: 22,  avg: '3.4 hr', sla: 71.2, status: 'danger' },
              ].map(p => (
                <tr key={p.name}>
                  <td style={{ padding: '10px 14px', borderBottom: '1px solid #e9ebed', fontWeight: 500 }}>{p.name}</td>
                  <td style={{ padding: '10px 14px', borderBottom: '1px solid #e9ebed', fontFamily: "'JetBrains Mono', monospace" }}>{p.checks}</td>
                  <td style={{ padding: '10px 14px', borderBottom: '1px solid #e9ebed', fontFamily: "'JetBrains Mono', monospace", color: '#4F5B67' }}>{p.avg}</td>
                  <td style={{ padding: '10px 14px', borderBottom: '1px solid #e9ebed' }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                      <div style={{ width: 60, height: 6, background: '#F2F3F3', borderRadius: 3, overflow: 'hidden' }}>
                        <div style={{ width: `${p.sla}%`, height: '100%', background: p.status === 'danger' ? '#E23D36' : p.status === 'warning' ? '#C28B0B' : '#2C974B' }} />
                      </div>
                      <span style={{ fontFamily: "'JetBrains Mono', monospace", color: '#4F5B67' }}>{p.sla}%</span>
                    </div>
                  </td>
                  <td style={{ padding: '10px 14px', borderBottom: '1px solid #e9ebed' }}>
                    <PortalBadge variant={p.status} size="sm">{p.status === 'success' ? 'Healthy' : p.status === 'warning' ? 'Watch' : 'Degraded'}</PortalBadge>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, padding: '14px 18px' }}>
          <div style={{ fontSize: 14, fontWeight: 600 }}>Product mix</div>
          <div style={{ fontSize: 11, color: '#4F5B67', marginTop: 2, marginBottom: 14 }}>Share of weekly volume</div>
          {[
            { name: 'KYC',       pct: 54, color: '#1A2E4B' },
            { name: 'Sanctions', pct: 18, color: '#00B3D9' },
            { name: 'Credit',    pct: 12, color: '#2C974B' },
            { name: 'Criminal',  pct: 10, color: '#C28B0B' },
            { name: 'Other',     pct: 6,  color: '#4F5B67' },
          ].map(p => (
            <div key={p.name} style={{ marginBottom: 10 }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: 12, marginBottom: 4 }}>
                <span>{p.name}</span>
                <span style={{ fontFamily: "'JetBrains Mono', monospace", color: '#4F5B67' }}>{p.pct}%</span>
              </div>
              <div style={{ height: 6, background: '#F2F3F3', borderRadius: 3, overflow: 'hidden' }}>
                <div style={{ width: `${p.pct}%`, height: '100%', background: p.color }} />
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}

window.Reports = Reports;
