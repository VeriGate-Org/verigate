/* global React, PortalIcon, PortalButton, PortalBadge */
const { useState: useStateCoI, useMemo: useMemoCoI } = React;

/* ──────────────────────────────────────────────────────────
   Conflict of Interest — Detect & Manage
   A new compliance feature that cross-references subjects across:
     · employees, vendors, suppliers, customers, board members
     · related-party transactions
     · external directorships, side businesses
     · PEP relatives & associates
   ──────────────────────────────────────────────────────── */

const CONFLICTS = [
  { id: 'COI-2026-0042', subject: 'Naledi Nkosi',         role: 'Admin · VeriGate',
    counterparty: 'Stellar Vendor (Pty) Ltd',             cpRole: 'Procurement vendor',
    relation: 'Director (undisclosed)',                    discovered: '2026-05-18T14:33Z',
    severity: 'critical', risk: 92, status: 'open',       statusLabel: 'Open',     priority: 'CRITICAL',
    signals: ['CIPC director listing', 'Bank account name match', 'POPIA disclosure absent'],
    txn: 'R 248,500 over 6 months' },
  { id: 'COI-2026-0041', subject: 'Sipho Dlamini',        role: 'Operator · VeriGate',
    counterparty: 'TransUnion ZA',                         cpRole: 'Data provider',
    relation: 'Former employee (within 12 months)',        discovered: '2026-05-17T11:08Z',
    severity: 'high',     risk: 74, status: 'in_review',  statusLabel: 'In Review', priority: 'HIGH',
    signals: ['LinkedIn employment history', 'Self-disclosed 2026-04'],
    txn: '—' },
  { id: 'COI-2026-0040', subject: 'Mandla Tshabalala',    role: 'Subject · onboarding',
    counterparty: 'Acme Operations (Pty) Ltd',             cpRole: 'Existing customer',
    relation: 'Same residential address as 2nd-degree relative who is a PEP',
    discovered: '2026-05-16T16:42Z',
    severity: 'medium',   risk: 56, status: 'in_review',  statusLabel: 'In Review', priority: 'MEDIUM',
    signals: ['Address match', 'PEP register hit (cousin)'],
    txn: 'R 1,420 onboarding fees' },
  { id: 'COI-2026-0039', subject: 'Arthur Manena',        role: 'Director · VeriGate',
    counterparty: 'Cinnebar Holdings',                     cpRole: 'Office landlord',
    relation: 'Director · disclosed',                      discovered: '2026-05-15T09:11Z',
    severity: 'low',      risk: 18, status: 'resolved',   statusLabel: 'Disclosed', priority: 'LOW',
    signals: ['Board minutes 2025-09', 'POPIA disclosure on file'],
    txn: 'R 86,400 monthly rent' },
  { id: 'COI-2026-0038', subject: 'Pyongyang Trading',     role: 'Subject · onboarding',
    counterparty: 'OFAC SDN list',                          cpRole: 'Sanctions match',
    relation: 'Beneficial owner overlap with sanctioned entity',
    discovered: '2026-05-14T13:24Z',
    severity: 'critical', risk: 98, status: 'escalated',  statusLabel: 'Escalated', priority: 'CRITICAL',
    signals: ['UBO match score 0.94', 'OFAC SDN list', 'Address in restricted country'],
    txn: 'Onboarding rejected before processing' },
  { id: 'COI-2026-0037', subject: 'Thandiwe Khumalo',     role: 'Operator · VeriGate',
    counterparty: 'CipherCorp Consulting',                 cpRole: 'Marketing vendor',
    relation: 'Spouse is director',                        discovered: '2026-05-12T08:51Z',
    severity: 'medium',   risk: 48, status: 'resolved',   statusLabel: 'Disclosed', priority: 'MEDIUM',
    signals: ['Marriage register match', 'Self-disclosed 2025-12'],
    txn: 'R 14,500 monthly retainer' },
];

const SEVERITY_TONES = { critical: 'danger', high: 'warning', medium: 'info', low: 'pending' };
const SEVERITY_COLORS = { critical: '#E23D36', high: '#C28B0B', medium: '#00B3D9', low: '#4F5B67' };
const STATUS_TONES = { open: 'danger', in_review: 'warning', resolved: 'success', escalated: 'danger' };

function CoIAvatar({ name, size = 32 }) {
  const initials = (name || '?').split(' ').map(p => p[0]).slice(0,2).join('').toUpperCase();
  return <div style={{ width: size, height: size, borderRadius: 999, background: '#1A2E4B', color: '#fff', display: 'inline-flex', alignItems: 'center', justifyContent: 'center', fontSize: size * 0.36, fontWeight: 600, flexShrink: 0 }}>{initials}</div>;
}

function RiskDot({ score, size = 36 }) {
  const color = score >= 80 ? '#E23D36' : score >= 60 ? '#C28B0B' : score >= 30 ? '#00B3D9' : '#2C974B';
  const r = size / 2 - 3;
  return (
    <div style={{ position: 'relative', width: size, height: size, flexShrink: 0 }}>
      <svg width={size} height={size} style={{ transform: 'rotate(-90deg)' }}>
        <circle cx={size/2} cy={size/2} r={r} fill="none" stroke="#F2F3F3" strokeWidth="3" />
        <circle cx={size/2} cy={size/2} r={r} fill="none" stroke={color} strokeWidth="3" strokeLinecap="round" strokeDasharray={`${(2*Math.PI*r) * (score/100)} ${2*Math.PI*r}`} />
      </svg>
      <div style={{ position: 'absolute', inset: 0, display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: size * 0.32, fontWeight: 700, color: '#1A2024' }}>{score}</div>
    </div>
  );
}

/* ── Relationship graph (compact SVG) ──────────────────── */

function RelationGraph({ conflict }) {
  return (
    <svg viewBox="0 0 380 200" width="100%" height="200" style={{ background: '#F8FAFC', borderRadius: 6 }}>
      {/* connector */}
      <line x1="80" y1="100" x2="300" y2="100" stroke="#E23D36" strokeWidth="2" strokeDasharray="6 4" />
      <text x="190" y="84" fontSize="10" fill="#E23D36" textAnchor="middle" fontWeight="600">{conflict.relation.slice(0, 40)}{conflict.relation.length > 40 ? '…' : ''}</text>
      {/* signals badges along the line */}
      {conflict.signals.slice(0, 2).map((s, i) => (
        <g key={i} transform={`translate(${130 + i * 80}, 110)`}>
          <rect x="-50" y="0" width="100" height="22" rx="11" fill="#fff" stroke="#E23D36" strokeWidth="1" />
          <text x="0" y="14" fontSize="9" fill="#1A2024" textAnchor="middle">{s.length > 18 ? s.slice(0, 17) + '…' : s}</text>
        </g>
      ))}
      {/* subject node */}
      <g>
        <circle cx="80" cy="100" r="34" fill="#1A2E4B" />
        <text x="80" y="105" fontSize="14" fontWeight="700" fill="#fff" textAnchor="middle">{(conflict.subject || '').split(' ').map(p => p[0]).slice(0,2).join('').toUpperCase()}</text>
        <text x="80" y="156" fontSize="11" fontWeight="600" fill="#1A2024" textAnchor="middle">{conflict.subject}</text>
        <text x="80" y="170" fontSize="9" fill="#4F5B67" textAnchor="middle">{conflict.role}</text>
      </g>
      {/* counterparty node */}
      <g>
        <circle cx="300" cy="100" r="34" fill="#E23D36" />
        <text x="300" y="105" fontSize="14" fontWeight="700" fill="#fff" textAnchor="middle">{(conflict.counterparty || '').split(' ').map(p => p[0]).slice(0,2).join('').toUpperCase()}</text>
        <text x="300" y="156" fontSize="11" fontWeight="600" fill="#1A2024" textAnchor="middle">{conflict.counterparty.length > 28 ? conflict.counterparty.slice(0, 27) + '…' : conflict.counterparty}</text>
        <text x="300" y="170" fontSize="9" fill="#4F5B67" textAnchor="middle">{conflict.cpRole}</text>
      </g>
    </svg>
  );
}

/* ── List ──────────────────────────────────────────────── */

function CoIListView({ conflicts, onOpen }) {
  return (
    <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, overflow: 'hidden', boxShadow: '0 1px 1px 0 rgba(0,28,36,0.30)' }}>
      <table style={{ width: '100%', borderCollapse: 'separate', borderSpacing: 0, fontSize: 12 }}>
        <thead style={{ background: '#F2F3F3' }}>
          <tr>{['ID', 'Subject', '↔', 'Counterparty', 'Relation', 'Severity', 'Risk', 'Status', 'Discovered', ''].map(h => (
            <th key={h} style={{ padding: '10px 12px', textAlign: 'left', fontSize: 10, color: '#4F5B67', textTransform: 'uppercase', letterSpacing: '0.05em', fontWeight: 600, borderBottom: '1px solid #e9ebed' }}>{h}</th>
          ))}</tr>
        </thead>
        <tbody>
          {conflicts.map(c => (
            <tr key={c.id} onClick={() => onOpen(c)} style={{ cursor: 'pointer', transition: 'background 100ms cubic-bezier(0.4,0,0.2,1)' }}
                onMouseEnter={e => e.currentTarget.style.background = '#F8FAFC'}
                onMouseLeave={e => e.currentTarget.style.background = 'transparent'}>
              <td style={{ padding: '10px 12px', borderBottom: '1px solid #e9ebed', fontFamily: "'JetBrains Mono', monospace", fontSize: 11, color: '#00B3D9' }}>{c.id}</td>
              <td style={{ padding: '10px 12px', borderBottom: '1px solid #e9ebed' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                  <CoIAvatar name={c.subject} size={26} />
                  <div>
                    <div style={{ fontWeight: 500, fontSize: 12 }}>{c.subject}</div>
                    <div style={{ fontSize: 10, color: '#4F5B67' }}>{c.role}</div>
                  </div>
                </div>
              </td>
              <td style={{ padding: '10px 12px', borderBottom: '1px solid #e9ebed', color: SEVERITY_COLORS[c.severity], fontWeight: 700, fontSize: 14 }}>↔</td>
              <td style={{ padding: '10px 12px', borderBottom: '1px solid #e9ebed' }}>
                <div style={{ fontWeight: 500, fontSize: 12 }}>{c.counterparty}</div>
                <div style={{ fontSize: 10, color: '#4F5B67' }}>{c.cpRole}</div>
              </td>
              <td style={{ padding: '10px 12px', borderBottom: '1px solid #e9ebed', maxWidth: 240, fontSize: 11, color: '#1A2024' }}>{c.relation.length > 50 ? c.relation.slice(0, 49) + '…' : c.relation}</td>
              <td style={{ padding: '10px 12px', borderBottom: '1px solid #e9ebed' }}>
                <span style={{ display: 'inline-flex', alignItems: 'center', gap: 4, fontSize: 11, color: SEVERITY_COLORS[c.severity], fontWeight: 600 }}>
                  <span style={{ width: 7, height: 7, borderRadius: 999, background: SEVERITY_COLORS[c.severity] }} />
                  {c.priority}
                </span>
              </td>
              <td style={{ padding: '10px 12px', borderBottom: '1px solid #e9ebed' }}>
                <RiskDot score={c.risk} size={32} />
              </td>
              <td style={{ padding: '10px 12px', borderBottom: '1px solid #e9ebed' }}><PortalBadge variant={STATUS_TONES[c.status]} size="sm">{c.statusLabel}</PortalBadge></td>
              <td style={{ padding: '10px 12px', borderBottom: '1px solid #e9ebed', color: '#4F5B67', fontSize: 11 }}>{new Date(c.discovered).toLocaleDateString('en-ZA', { day: '2-digit', month: 'short' })}</td>
              <td style={{ padding: '10px 12px', borderBottom: '1px solid #e9ebed', textAlign: 'right' }}><PortalIcon name="ChevronRight" size={12} color="#4F5B67" /></td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

/* ── Detail ────────────────────────────────────────────── */

function CoIDetail({ conflict, onBack, onResolve }) {
  const [decision, setDecision] = useStateCoI('');
  if (!conflict) return null;

  const txns = [
    { date: '2026-04-12', desc: 'Invoice INV-2042 paid', amount: 'R 42,300' },
    { date: '2026-03-08', desc: 'Invoice INV-1988 paid', amount: 'R 38,600' },
    { date: '2026-02-14', desc: 'Invoice INV-1944 paid', amount: 'R 47,100' },
    { date: '2026-01-22', desc: 'Invoice INV-1880 paid', amount: 'R 41,200' },
  ];

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 14 }} data-screen-label="Conflict Detail">
      <button onClick={onBack} style={{ alignSelf: 'flex-start', background: 'transparent', border: 'none', color: '#00B3D9', fontSize: 12, fontWeight: 500, cursor: 'pointer', padding: 0, fontFamily: "'Inter', sans-serif" }}>← Back to conflicts</button>

      {/* Header */}
      <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, overflow: 'hidden' }}>
        <div style={{ display: 'flex', height: 3 }}>
          <div style={{ flex: 3, background: '#E23D36' }} />
          <div style={{ flex: 5, background: '#1A2E4B' }} />
          <div style={{ flex: 2, background: '#00B3D9' }} />
        </div>
        <div style={{ padding: '18px 22px', display: 'grid', gridTemplateColumns: '1fr auto auto', gap: 24, alignItems: 'center' }}>
          <div>
            <div style={{ display: 'flex', alignItems: 'center', gap: 10, marginBottom: 6 }}>
              <span style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: 11, color: '#00B3D9' }}>{conflict.id}</span>
              <PortalBadge variant={STATUS_TONES[conflict.status]}>{conflict.statusLabel}</PortalBadge>
              <span style={{ fontSize: 11, color: SEVERITY_COLORS[conflict.severity], fontWeight: 600, display: 'inline-flex', alignItems: 'center', gap: 4 }}>
                <span style={{ width: 7, height: 7, borderRadius: 999, background: SEVERITY_COLORS[conflict.severity] }} />
                {conflict.priority} severity
              </span>
            </div>
            <div style={{ fontSize: 19, fontWeight: 600, color: '#1A2024', lineHeight: 1.3 }}>{conflict.subject} <span style={{ color: '#4F5B67', fontWeight: 400 }}>↔</span> {conflict.counterparty}</div>
            <div style={{ fontSize: 13, color: '#4F5B67', marginTop: 4 }}>{conflict.relation}</div>
          </div>
          <RiskDot score={conflict.risk} size={56} />
          <div style={{ display: 'flex', gap: 8 }}>
            <PortalButton variant="secondary">Reassign</PortalButton>
            <PortalButton variant="destructive">Escalate</PortalButton>
            <PortalButton variant="cta" onClick={() => onResolve(conflict)}>Resolve →</PortalButton>
          </div>
        </div>
      </div>

      {/* Two-column body */}
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 340px', gap: 14 }}>
        <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
          {/* AI explanation */}
          <div style={{ background: 'linear-gradient(135deg, rgba(0,179,217,0.06), rgba(0,179,217,0.02))', border: '1px solid rgba(0,179,217,0.25)', borderRadius: 8, padding: '14px 18px' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 8 }}>
              <span style={{ width: 22, height: 22, borderRadius: 6, background: 'rgba(0,179,217,0.15)', display: 'inline-flex', alignItems: 'center', justifyContent: 'center' }}>
                <PortalIcon name="Zap" size={12} color="#00B3D9" />
              </span>
              <span style={{ fontSize: 11, fontWeight: 600, color: '#0099bb', textTransform: 'uppercase', letterSpacing: '0.08em' }}>Why this was flagged</span>
            </div>
            <div style={{ fontSize: 13, color: '#1A2024', lineHeight: 1.6 }}>
              <b>{conflict.subject}</b> has an undisclosed financial relationship with <b>{conflict.counterparty}</b>. The detector cross-referenced CIPC director listings, bank-account holder names, and POPIA disclosure records. Composite risk score is <b style={{ color: SEVERITY_COLORS[conflict.severity] }}>{conflict.risk}</b>. <b>Recommended action:</b> require disclosure within 5 business days or recuse the subject from related procurement decisions.
            </div>
          </div>

          {/* Relationship graph */}
          <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, overflow: 'hidden' }}>
            <div style={{ padding: '12px 18px', borderBottom: '1px solid #e9ebed' }}>
              <div style={{ fontSize: 14, fontWeight: 600 }}>Relationship graph</div>
              <div style={{ fontSize: 11, color: '#4F5B67', marginTop: 2 }}>{conflict.signals.length} signal(s) detected</div>
            </div>
            <div style={{ padding: 16 }}>
              <RelationGraph conflict={conflict} />
            </div>
          </div>

          {/* Signals breakdown */}
          <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, overflow: 'hidden' }}>
            <div style={{ padding: '12px 18px', borderBottom: '1px solid #e9ebed' }}>
              <div style={{ fontSize: 14, fontWeight: 600 }}>Detection signals</div>
            </div>
            {conflict.signals.map((s, i) => (
              <div key={i} style={{ display: 'flex', alignItems: 'center', gap: 12, padding: '10px 18px', borderTop: i > 0 ? '1px solid #f1f5f9' : 'none' }}>
                <span style={{ width: 8, height: 8, borderRadius: 999, background: SEVERITY_COLORS[conflict.severity] }} />
                <div style={{ flex: 1, fontSize: 12, color: '#1A2024' }}>{s}</div>
                <PortalBadge variant={SEVERITY_TONES[conflict.severity]} size="sm">Match</PortalBadge>
              </div>
            ))}
          </div>

          {/* Related transactions */}
          {conflict.txn !== '—' && (
            <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, overflow: 'hidden' }}>
              <div style={{ padding: '12px 18px', borderBottom: '1px solid #e9ebed', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <div>
                  <div style={{ fontSize: 14, fontWeight: 600 }}>Related-party transactions</div>
                  <div style={{ fontSize: 11, color: '#4F5B67', marginTop: 2 }}>{conflict.txn}</div>
                </div>
                <PortalButton variant="link">Export ledger →</PortalButton>
              </div>
              {txns.map((t, i) => (
                <div key={i} style={{ display: 'flex', justifyContent: 'space-between', padding: '8px 18px', borderTop: i > 0 ? '1px solid #f1f5f9' : 'none', fontSize: 12 }}>
                  <span style={{ color: '#4F5B67', fontFamily: "'JetBrains Mono', monospace" }}>{t.date}</span>
                  <span style={{ flex: 1, marginLeft: 14 }}>{t.desc}</span>
                  <span style={{ fontFamily: "'JetBrains Mono', monospace", fontWeight: 600, color: '#1A2024' }}>{t.amount}</span>
                </div>
              ))}
            </div>
          )}
        </div>

        {/* Sidebar */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
          <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, padding: '14px 16px' }}>
            <div style={{ fontSize: 11, color: '#4F5B67', textTransform: 'uppercase', letterSpacing: '0.06em', fontWeight: 600, marginBottom: 10 }}>Resolution</div>
            <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
              {[
                { id: 'disclose', label: 'Mark as disclosed', tone: '#2C974B', desc: 'Subject filed POPIA-compliant disclosure.' },
                { id: 'recuse',   label: 'Require recusal',   tone: '#C28B0B', desc: 'Subject must step out of related decisions.' },
                { id: 'terminate',label: 'Terminate relationship', tone: '#E23D36', desc: 'End the engagement or onboarding.' },
                { id: 'noaction', label: 'No further action', tone: '#4F5B67', desc: 'False positive or already mitigated.' },
              ].map(d => (
                <label key={d.id} style={{ display: 'flex', gap: 8, padding: '10px 12px', border: `1px solid ${decision === d.id ? d.tone : '#e9ebed'}`, borderRadius: 6, cursor: 'pointer', background: decision === d.id ? `${d.tone}0a` : '#fff' }}>
                  <input type="radio" name="resolution" checked={decision === d.id} onChange={() => setDecision(d.id)} style={{ accentColor: d.tone, marginTop: 2 }} />
                  <div>
                    <div style={{ fontSize: 12, fontWeight: 600, color: d.tone }}>{d.label}</div>
                    <div style={{ fontSize: 10, color: '#4F5B67', marginTop: 2, lineHeight: 1.5 }}>{d.desc}</div>
                  </div>
                </label>
              ))}
            </div>
            <textarea placeholder="Resolution note (logged for audit)" style={{ width: '100%', marginTop: 10, padding: '8px 10px', border: '1px solid #D5DBDB', borderRadius: 4, fontSize: 12, fontFamily: "'Inter', sans-serif", resize: 'vertical', minHeight: 60, outline: 'none' }} />
            <PortalButton variant="primary" disabled={!decision} style={{ width: '100%', marginTop: 8 }}>Confirm resolution</PortalButton>
          </div>

          <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, padding: '14px 16px' }}>
            <div style={{ fontSize: 11, color: '#4F5B67', textTransform: 'uppercase', letterSpacing: '0.06em', fontWeight: 600, marginBottom: 10 }}>Disclosure status</div>
            <div style={{ display: 'flex', flexDirection: 'column', gap: 6, fontSize: 12 }}>
              <div style={{ display: 'flex', justifyContent: 'space-between' }}><span style={{ color: '#4F5B67' }}>Required by</span><span style={{ fontWeight: 500, fontFamily: "'JetBrains Mono', monospace" }}>2026-05-25</span></div>
              <div style={{ display: 'flex', justifyContent: 'space-between' }}><span style={{ color: '#4F5B67' }}>Days remaining</span><span style={{ fontWeight: 600, color: '#C28B0B' }}>5 days</span></div>
              <div style={{ display: 'flex', justifyContent: 'space-between' }}><span style={{ color: '#4F5B67' }}>Form filed</span><PortalBadge variant="warning" size="sm">Not yet</PortalBadge></div>
              <div style={{ display: 'flex', justifyContent: 'space-between' }}><span style={{ color: '#4F5B67' }}>Reminder sent</span><span style={{ fontWeight: 500 }}>2 days ago</span></div>
            </div>
            <PortalButton variant="secondary" size="sm" style={{ width: '100%', marginTop: 10 }}>Send disclosure form</PortalButton>
          </div>

          <div style={{ background: '#F8FAFC', border: '1px dashed #CBD5E1', borderRadius: 8, padding: '12px 14px', fontSize: 11, color: '#4F5B67', lineHeight: 1.55 }}>
            <b style={{ color: '#1A2E4B' }}>Audit log:</b> Every action on this conflict is logged with actor, timestamp, and supporting documents. View full audit at the bottom of the page.
          </div>
        </div>
      </div>
    </div>
  );
}

/* ── Main page ─────────────────────────────────────────── */

function ConflictOfInterest({ initialView = 'list' }) {
  const [view, setView] = useStateCoI(initialView);
  const [openConflict, setOpenConflict] = useStateCoI(null);
  const [filter, setFilter] = useStateCoI('all');
  const [query, setQuery] = useStateCoI('');

  const filtered = useMemoCoI(() => {
    let r = CONFLICTS;
    if (filter === 'open')      r = r.filter(c => c.status === 'open' || c.status === 'in_review' || c.status === 'escalated');
    else if (filter === 'resolved') r = r.filter(c => c.status === 'resolved');
    else if (filter !== 'all')  r = r.filter(c => c.severity === filter);
    if (query) r = r.filter(c => (c.subject + c.counterparty + c.id).toLowerCase().includes(query.toLowerCase()));
    return r;
  }, [filter, query]);

  if (view === 'detail' && openConflict) {
    return <CoIDetail conflict={openConflict} onBack={() => { setView('list'); setOpenConflict(null); }} onResolve={() => { setView('list'); setOpenConflict(null); }} />;
  }

  const open = CONFLICTS.filter(c => c.status === 'open' || c.status === 'in_review' || c.status === 'escalated').length;
  const critical = CONFLICTS.filter(c => c.severity === 'critical' && c.status !== 'resolved').length;
  const undisclosed = CONFLICTS.filter(c => c.signals.some(s => s.toLowerCase().includes('absent')) && c.status !== 'resolved').length;
  const resolved = CONFLICTS.filter(c => c.status === 'resolved').length;

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 14 }} data-screen-label="Conflict of Interest">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-end' }}>
        <div>
          <div style={{ fontSize: 11, color: '#4F5B67', textTransform: 'uppercase', letterSpacing: '0.08em', fontWeight: 600, display: 'inline-flex', alignItems: 'center', gap: 8 }}>
            Enterprise · Compliance
            <span style={{ fontSize: 9, padding: '2px 6px', background: '#00B3D9', color: '#fff', borderRadius: 3, fontWeight: 700, letterSpacing: '0.06em' }}>NEW</span>
          </div>
          <h1 style={{ fontSize: 26, fontWeight: 600, color: '#1A2024', marginTop: 4, letterSpacing: '-0.01em' }}>Conflict of Interest</h1>
          <div style={{ fontSize: 13, color: '#4F5B67', marginTop: 4, maxWidth: 720, lineHeight: 1.55 }}>
            Detect undisclosed relationships between your employees, vendors, customers, and board. Cross-references CIPC, marriage registers, PEP lists, bank-account holders, and POPIA disclosures.
          </div>
        </div>
        <div style={{ display: 'flex', gap: 8 }}>
          <PortalButton variant="secondary" icon={<PortalIcon name="Download" size={13} color="#1A2E4B" />}>Export ledger</PortalButton>
          <PortalButton variant="primary">Run detection scan</PortalButton>
          <PortalButton variant="cta" icon={<PortalIcon name="Plus" size={13} color="#fff" />}>+ Disclose conflict</PortalButton>
        </div>
      </div>

      {/* KPI strip */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(5, 1fr)', gap: 14 }}>
        {[
          { label: 'Active conflicts',  value: open,         tone: '#1A2024' },
          { label: 'Critical · unresolved', value: critical, tone: '#E23D36' },
          { label: 'Undisclosed',       value: undisclosed,  tone: '#C28B0B' },
          { label: 'Resolved (30d)',    value: resolved,     tone: '#2C974B' },
          { label: 'Last scan',         value: '6h ago',     tone: '#00B3D9', mono: true },
        ].map(s => (
          <div key={s.label} style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, padding: '12px 16px' }}>
            <div style={{ fontSize: 10, color: '#4F5B67', textTransform: 'uppercase', letterSpacing: '0.06em', fontWeight: 600 }}>{s.label}</div>
            <div style={{ fontSize: 24, fontWeight: 700, color: s.tone, marginTop: 4, fontFamily: s.mono ? "'JetBrains Mono', monospace" : "'Inter', sans-serif" }}>{s.value}</div>
          </div>
        ))}
      </div>

      {/* Filter strip */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', gap: 16 }}>
        <div style={{ display: 'flex', gap: 6, flexWrap: 'wrap' }}>
          {[
            { id: 'all',      label: 'All',         count: CONFLICTS.length },
            { id: 'open',     label: 'Open',        count: open },
            { id: 'critical', label: 'Critical',    count: CONFLICTS.filter(c => c.severity === 'critical').length },
            { id: 'high',     label: 'High',         count: CONFLICTS.filter(c => c.severity === 'high').length },
            { id: 'medium',   label: 'Medium',       count: CONFLICTS.filter(c => c.severity === 'medium').length },
            { id: 'resolved', label: 'Resolved',     count: resolved },
          ].map(c => (
            <button key={c.id} onClick={() => setFilter(c.id)} style={{
              padding: '6px 12px', borderRadius: 16, border: `1px solid ${filter === c.id ? '#1A2E4B' : '#D5DBDB'}`,
              background: filter === c.id ? '#1A2E4B' : '#fff', color: filter === c.id ? '#fff' : '#1A2024',
              fontSize: 12, fontWeight: 500, cursor: 'pointer', display: 'inline-flex', alignItems: 'center', gap: 6, fontFamily: "'Inter', sans-serif",
            }}>{c.label} <span style={{ fontSize: 10, opacity: 0.7, fontFamily: "'JetBrains Mono', monospace" }}>{c.count}</span></button>
          ))}
        </div>
        <div style={{ position: 'relative', width: 280 }}>
          <span style={{ position: 'absolute', left: 10, top: '50%', transform: 'translateY(-50%)' }}><PortalIcon name="Search" size={13} color="#4F5B67" /></span>
          <input value={query} onChange={e => setQuery(e.target.value)} placeholder="Search subject, counterparty, or ID…"
            style={{ width: '100%', padding: '6px 12px 6px 32px', borderRadius: 4, border: '1px solid #D5DBDB', fontSize: 12, fontFamily: "'Inter', sans-serif", outline: 'none' }}
            onFocus={e => { e.currentTarget.style.borderColor = '#00B3D9'; e.currentTarget.style.boxShadow = '0 0 0 3px rgba(0,179,217,0.15)'; }}
            onBlur={e => { e.currentTarget.style.borderColor = '#D5DBDB'; e.currentTarget.style.boxShadow = 'none'; }} />
        </div>
      </div>

      <CoIListView conflicts={filtered} onOpen={(c) => { setOpenConflict(c); setView('detail'); }} />

      {/* Detection config card */}
      <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, padding: '14px 18px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <div style={{ fontSize: 13, fontWeight: 600 }}>Detection sources</div>
          <div style={{ display: 'flex', gap: 8, marginTop: 8, flexWrap: 'wrap' }}>
            {['CIPC directors', 'PEP register', 'Marriage register', 'Bank account holders', 'Address overlap', 'POPIA disclosures', 'LinkedIn employment', 'OFAC SDN'].map(s => (
              <PortalBadge key={s} variant="info" size="sm">{s}</PortalBadge>
            ))}
          </div>
        </div>
        <PortalButton variant="link">Configure detection →</PortalButton>
      </div>
    </div>
  );
}

window.ConflictOfInterest = ConflictOfInterest;
