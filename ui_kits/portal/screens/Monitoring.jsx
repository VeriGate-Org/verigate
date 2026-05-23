/* global React, PortalIcon, PortalButton, PortalBadge */
const { useState: useStateM } = React;

/* ──────────────────────────────────────────────────────────
   Monitoring — list of subjects under continuous screening + detail
   ──────────────────────────────────────────────────────── */

const MONITORED = [
  { id: 'M-2026-0042', subject: 'Jane Smith',            type: 'Person',  policy: 'pol_aml_enhanced',  freq: 'Daily',   nextRun: '6h',  alerts7d: 1, status: 'success', statusLabel: 'Clear',     onboard: '2026-04-12', country: 'ZA' },
  { id: 'M-2026-0041', subject: 'Acme Corp Ltd',         type: 'Company', policy: 'pol_corp_kyb',      freq: 'Weekly',  nextRun: '2d',  alerts7d: 3, status: 'warning', statusLabel: 'Match',    onboard: '2026-03-08', country: 'ZA' },
  { id: 'M-2026-0040', subject: 'Mandla Tshabalala',     type: 'Person',  policy: 'pol_aml_enhanced',  freq: 'Daily',   nextRun: '14h', alerts7d: 8, status: 'danger',  statusLabel: 'Escalated', onboard: '2025-11-22', country: 'ZA' },
  { id: 'M-2026-0039', subject: 'Naledi Nkosi',          type: 'Person',  policy: 'pol_onboard_v3',    freq: 'Monthly', nextRun: '12d', alerts7d: 0, status: 'success', statusLabel: 'Clear',     onboard: '2025-08-04', country: 'ZA' },
  { id: 'M-2026-0038', subject: 'Pyongyang Trading',     type: 'Company', policy: 'pol_aml_enhanced',  freq: 'Daily',   nextRun: '3h',  alerts7d: 12, status: 'danger', statusLabel: 'Escalated', onboard: '2026-01-15', country: 'ZA' },
  { id: 'M-2026-0037', subject: 'Sipho Dlamini',         type: 'Person',  policy: 'pol_onboard_v3',    freq: 'Weekly',  nextRun: '4d',  alerts7d: 0, status: 'success', statusLabel: 'Clear',     onboard: '2025-09-19', country: 'ZA' },
];

const ALERT_HISTORY = [
  { date: '2026-05-18', kind: 'Sanctions list update',  detail: 'Subject added to EU consolidated list — partial match score 0.78.', tone: 'warning' },
  { date: '2026-05-16', kind: 'PEP designation',        detail: 'Cousin (2nd degree) confirmed as municipal council member.',         tone: 'warning' },
  { date: '2026-05-12', kind: 'Adverse media',          detail: 'News article mentioning subject in fraud investigation.',            tone: 'danger' },
  { date: '2026-05-05', kind: 'Routine refresh',        detail: 'All checks clear. No changes since last refresh.',                  tone: 'success' },
  { date: '2026-04-28', kind: 'Routine refresh',        detail: 'All checks clear. No changes since last refresh.',                  tone: 'success' },
];

function Monitoring({ onOpenSubject }) {
  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 14 }} data-screen-label="Monitoring">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-end' }}>
        <div>
          <div style={{ fontSize: 11, color: '#4F5B67', textTransform: 'uppercase', letterSpacing: '0.08em', fontWeight: 600, display: 'inline-flex', alignItems: 'center', gap: 8 }}>
            Enterprise · Monitoring
            <span style={{ fontSize: 9, padding: '2px 6px', background: '#00B3D9', color: '#fff', borderRadius: 3, fontWeight: 700, letterSpacing: '0.06em' }}>NEW</span>
          </div>
          <h1 style={{ fontSize: 26, fontWeight: 600, color: '#1A2024', marginTop: 4, letterSpacing: '-0.01em' }}>Ongoing monitoring</h1>
          <div style={{ fontSize: 13, color: '#4F5B67', marginTop: 4, maxWidth: 640 }}>Subjects under continuous re-screening. Alerts fire when sanctions lists, PEP registers, or adverse media change.</div>
        </div>
        <PortalButton variant="cta" icon={<PortalIcon name="Plus" size={13} color="#fff" />}>+ Monitor subject</PortalButton>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: 14 }}>
        {[
          { label: 'Subjects monitored', value: MONITORED.length, tone: '#1A2024' },
          { label: 'Alerts (7 days)',    value: MONITORED.reduce((s, m) => s + m.alerts7d, 0), tone: '#C28B0B' },
          { label: 'Escalated',          value: MONITORED.filter(m => m.status === 'danger').length, tone: '#E23D36' },
          { label: 'Last refresh',       value: '14m ago', tone: '#1A2024', mono: true },
        ].map(s => (
          <div key={s.label} style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, padding: '14px 18px' }}>
            <div style={{ fontSize: 10, color: '#4F5B67', textTransform: 'uppercase', letterSpacing: '0.06em', fontWeight: 600 }}>{s.label}</div>
            <div style={{ fontSize: 24, fontWeight: 700, color: s.tone, marginTop: 4, fontFamily: s.mono ? "'JetBrains Mono', monospace" : "'Inter', sans-serif" }}>{s.value}</div>
          </div>
        ))}
      </div>

      <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, overflow: 'hidden' }}>
        <div style={{ padding: '12px 18px', borderBottom: '1px solid #e9ebed' }}>
          <div style={{ fontSize: 14, fontWeight: 600 }}>Monitored subjects</div>
          <div style={{ fontSize: 11, color: '#4F5B67', marginTop: 2 }}>Click any row to drill into the subject's alert history.</div>
        </div>
        <table style={{ width: '100%', borderCollapse: 'separate', borderSpacing: 0, fontSize: 12 }}>
          <thead style={{ background: '#F2F3F3' }}>
            <tr>{['Subject', 'Type', 'Policy', 'Frequency', 'Next', 'Alerts (7d)', 'Status', ''].map(h => (
              <th key={h} style={{ padding: '8px 14px', textAlign: 'left', fontSize: 10, color: '#4F5B67', textTransform: 'uppercase', letterSpacing: '0.05em', fontWeight: 600, borderBottom: '1px solid #e9ebed' }}>{h}</th>
            ))}</tr>
          </thead>
          <tbody>
            {MONITORED.map(m => (
              <tr key={m.id} onClick={() => onOpenSubject(m)} style={{ cursor: 'pointer' }}
                  onMouseEnter={e => e.currentTarget.style.background = '#F8FAFC'}
                  onMouseLeave={e => e.currentTarget.style.background = 'transparent'}>
                <td style={{ padding: '10px 14px', borderBottom: '1px solid #e9ebed' }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
                    <div style={{ width: 26, height: 26, borderRadius: 999, background: '#1A2E4B', color: '#fff', display: 'inline-flex', alignItems: 'center', justifyContent: 'center', fontSize: 10, fontWeight: 600 }}>{m.subject.split(' ').map(p => p[0]).slice(0,2).join('')}</div>
                    <div>
                      <div style={{ fontWeight: 500 }}>{m.subject}</div>
                      <div style={{ fontSize: 10, color: '#4F5B67', fontFamily: "'JetBrains Mono', monospace" }}>{m.id} · onboard {m.onboard}</div>
                    </div>
                  </div>
                </td>
                <td style={{ padding: '10px 14px', borderBottom: '1px solid #e9ebed', color: '#4F5B67' }}>{m.type}</td>
                <td style={{ padding: '10px 14px', borderBottom: '1px solid #e9ebed', fontFamily: "'JetBrains Mono', monospace", fontSize: 11, color: '#00B3D9' }}>{m.policy}</td>
                <td style={{ padding: '10px 14px', borderBottom: '1px solid #e9ebed' }}>{m.freq}</td>
                <td style={{ padding: '10px 14px', borderBottom: '1px solid #e9ebed', color: '#4F5B67', fontFamily: "'JetBrains Mono', monospace" }}>{m.nextRun}</td>
                <td style={{ padding: '10px 14px', borderBottom: '1px solid #e9ebed', fontFamily: "'JetBrains Mono', monospace", fontWeight: 600, color: m.alerts7d === 0 ? '#2C974B' : m.alerts7d > 5 ? '#E23D36' : '#C28B0B' }}>{m.alerts7d}</td>
                <td style={{ padding: '10px 14px', borderBottom: '1px solid #e9ebed' }}><PortalBadge variant={m.status}>{m.statusLabel}</PortalBadge></td>
                <td style={{ padding: '10px 14px', borderBottom: '1px solid #e9ebed', textAlign: 'right' }}><PortalIcon name="ChevronRight" size={12} color="#4F5B67" /></td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

function MonitoringDetail({ subject, onBack }) {
  if (!subject) return null;
  const tone = { success: '#2C974B', warning: '#C28B0B', danger: '#E23D36' };
  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 14 }} data-screen-label="Monitoring Subject">
      <button onClick={onBack} style={{ alignSelf: 'flex-start', background: 'transparent', border: 'none', color: '#00B3D9', fontSize: 12, fontWeight: 500, cursor: 'pointer', padding: 0, fontFamily: "'Inter', sans-serif" }}>← Back to monitoring</button>

      <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, overflow: 'hidden' }}>
        <div style={{ display: 'flex', height: 3 }}>
          <div style={{ flex: 3, background: '#E23D36' }} />
          <div style={{ flex: 5, background: '#1A2E4B' }} />
          <div style={{ flex: 2, background: '#00B3D9' }} />
        </div>
        <div style={{ padding: '20px 24px', display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
          <div style={{ display: 'flex', gap: 14, alignItems: 'center' }}>
            <div style={{ width: 50, height: 50, borderRadius: 999, background: '#1A2E4B', color: '#fff', display: 'inline-flex', alignItems: 'center', justifyContent: 'center', fontSize: 16, fontWeight: 600 }}>{subject.subject.split(' ').map(p => p[0]).slice(0,2).join('')}</div>
            <div>
              <div style={{ display: 'flex', alignItems: 'center', gap: 10, marginBottom: 4 }}>
                <span style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: 12, color: '#00B3D9' }}>{subject.id}</span>
                <PortalBadge variant={subject.status}>{subject.statusLabel}</PortalBadge>
              </div>
              <div style={{ fontSize: 18, fontWeight: 600 }}>{subject.subject}</div>
              <div style={{ fontSize: 12, color: '#4F5B67', marginTop: 4 }}>Under {subject.freq.toLowerCase()} monitoring · policy <b style={{ fontFamily: "'JetBrains Mono', monospace", color: '#00B3D9' }}>{subject.policy}</b> · onboarded {subject.onboard}</div>
            </div>
          </div>
          <div style={{ display: 'flex', gap: 8 }}>
            <PortalButton variant="secondary">Re-run now</PortalButton>
            <PortalButton variant="ghost" style={{ color: '#E23D36' }}>Stop monitoring</PortalButton>
          </div>
        </div>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '1.6fr 1fr', gap: 14 }}>
        <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, overflow: 'hidden' }}>
          <div style={{ padding: '14px 18px', borderBottom: '1px solid #e9ebed' }}>
            <div style={{ fontSize: 14, fontWeight: 600 }}>Alert history</div>
            <div style={{ fontSize: 11, color: '#4F5B67', marginTop: 2 }}>Last 30 days</div>
          </div>
          <div style={{ padding: '6px 0' }}>
            {ALERT_HISTORY.map((a, i) => (
              <div key={i} style={{ display: 'flex', gap: 14, padding: '12px 18px', borderTop: i > 0 ? '1px solid #f1f5f9' : 'none' }}>
                <div style={{ width: 12, height: 12, borderRadius: 999, background: '#fff', border: `2px solid ${tone[a.tone]}`, marginTop: 4, flexShrink: 0 }} />
                <div style={{ flex: 1 }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 2 }}>
                    <span style={{ fontSize: 13, fontWeight: 600 }}>{a.kind}</span>
                    <span style={{ fontSize: 11, color: '#4F5B67', fontFamily: "'JetBrains Mono', monospace" }}>{a.date}</span>
                  </div>
                  <div style={{ fontSize: 12, color: '#4F5B67', lineHeight: 1.55 }}>{a.detail}</div>
                </div>
              </div>
            ))}
          </div>
        </div>
        <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, padding: '14px 18px' }}>
          <div style={{ fontSize: 11, color: '#4F5B67', textTransform: 'uppercase', letterSpacing: '0.06em', fontWeight: 600, marginBottom: 10 }}>Monitoring settings</div>
          {[
            ['Frequency', subject.freq],
            ['Next refresh', subject.nextRun + ' from now'],
            ['Datasets', 'Sanctions · PEP · Adverse media'],
            ['Notify', 'arthur@verigate.co.za · #compliance'],
            ['Auto-create case', 'On any non-routine alert'],
          ].map(([k, v]) => (
            <div key={k} style={{ display: 'flex', justifyContent: 'space-between', padding: '6px 0', borderBottom: '1px solid #f1f5f9', fontSize: 12 }}>
              <span style={{ color: '#4F5B67' }}>{k}</span>
              <span style={{ color: '#1A2024', fontWeight: 500, textAlign: 'right', maxWidth: 200 }}>{v}</span>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}

window.Monitoring = Monitoring;
window.MonitoringDetail = MonitoringDetail;
