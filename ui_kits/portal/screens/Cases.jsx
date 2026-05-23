/* global React, PortalIcon, PortalButton, PortalBadge */
const { useState: useStateC, useMemo: useMemoC } = React;

/* ──────────────────────────────────────────────────────────
   Sample data (mirrors verigate-partner-portal Case type)
   ──────────────────────────────────────────────────────── */
const CASES = [
  { caseId: 'CS-7b3a91e2-…', short: 'CS-7b3a91e2', subjectName: 'Jane Smith',            subjectId: '8503125800087', status: 'ESCALATED', priority: 'HIGH',    riskScore: 87, riskTier: 'HIGH',     assignee: 'Naledi Nkosi',  createdAt: '2026-05-18T14:33:02Z', verificationId: 'VG-2026-0140', reason: 'Credit bureau returned hard fail; adverse listings within 12 months.' },
  { caseId: 'CS-1d4b8f12-…', short: 'CS-1d4b8f12', subjectName: 'Acme Corp Ltd',         subjectId: 'CIPC 2018/451288/07', status: 'IN_REVIEW', priority: 'HIGH', riskScore: 78, riskTier: 'HIGH',     assignee: 'Sipho Dlamini', createdAt: '2026-05-18T13:18:14Z', verificationId: 'VG-2026-0141', reason: 'Partial sanctions list match — name score 0.78.' },
  { caseId: 'CS-9e2c5a45-…', short: 'CS-9e2c5a45', subjectName: 'Bob Williams',          subjectId: '7806174500083', status: 'IN_REVIEW', priority: 'MEDIUM', riskScore: 64, riskTier: 'MEDIUM', assignee: 'Arthur Manena',  createdAt: '2026-05-18T13:46:21Z', verificationId: 'VG-2026-0139', reason: 'DHA response delayed past 30-minute SLA.' },
  { caseId: 'CS-3a8e2f9c-…', short: 'CS-3a8e2f9c', subjectName: 'Mandla Tshabalala',     subjectId: '8801135500084', status: 'OPEN',     priority: 'MEDIUM', riskScore: 58, riskTier: 'MEDIUM', assignee: null,             createdAt: '2026-05-17T10:24:08Z', verificationId: 'VG-2026-0134', reason: 'Credit profile mismatch — score below threshold (-12 vs policy).' },
  { caseId: 'CS-6c1f4d80-…', short: 'CS-6c1f4d80', subjectName: 'Lerato Mokoena',        subjectId: '9006120800086', status: 'RESOLVED',  priority: 'LOW',    riskScore: 22, riskTier: 'LOW',    assignee: 'Arthur Manena',  createdAt: '2026-05-16T08:55:33Z', verificationId: 'VG-2026-0138', reason: 'Auto-resolved — passed manual review.' },
  { caseId: 'CS-2f7d8a31-…', short: 'CS-2f7d8a31', subjectName: 'Pieter van der Merwe',  subjectId: '7503060800087', status: 'OPEN',     priority: 'LOW',    riskScore: 41, riskTier: 'MEDIUM', assignee: null,             createdAt: '2026-05-17T16:08:12Z', verificationId: 'VG-2026-0137', reason: 'Inconsistent address on file.' },
  { caseId: 'CS-8b4e1f02-…', short: 'CS-8b4e1f02', subjectName: 'Naledi Nkosi',          subjectId: '9304117500084', status: 'RESOLVED',  priority: 'LOW',    riskScore: 18, riskTier: 'LOW',    assignee: 'Naledi Nkosi',  createdAt: '2026-05-15T11:42:00Z', verificationId: 'VG-2026-0136', reason: 'Biometric mismatch retried, resolved on second attempt.' },
  { caseId: 'CS-5e9d3c47-…', short: 'CS-5e9d3c47', subjectName: 'Thandiwe Khumalo',      subjectId: '9509223200086', status: 'ESCALATED', priority: 'CRITICAL', riskScore: 94, riskTier: 'CRITICAL', assignee: 'Sipho Dlamini', createdAt: '2026-05-18T09:14:53Z', verificationId: 'VG-2026-0133', reason: 'Suspected synthetic identity — ID issued same week as application.' },
  { caseId: 'CS-1a2b3c4d-…', short: 'CS-1a2b3c4d', subjectName: 'Sipho Dlamini',         subjectId: '8211056500088', status: 'IN_REVIEW', priority: 'MEDIUM', riskScore: 55, riskTier: 'MEDIUM', assignee: 'Arthur Manena',  createdAt: '2026-05-17T12:05:42Z', verificationId: 'VG-2026-0135', reason: 'PEP screening — extended family member listed as PEP.' },
];

window.CASES_DATA = CASES;

const STATUSES = [
  { id: 'OPEN',      label: 'Open',       tone: 'info' },
  { id: 'IN_REVIEW', label: 'In Review',  tone: 'warning' },
  { id: 'ESCALATED', label: 'Escalated',  tone: 'danger' },
  { id: 'RESOLVED',  label: 'Resolved',   tone: 'success' },
];

const PRIORITIES = [
  { id: 'CRITICAL', label: 'Critical', color: '#E23D36' },
  { id: 'HIGH',     label: 'High',     color: '#C28B0B' },
  { id: 'MEDIUM',   label: 'Medium',   color: '#00B3D9' },
  { id: 'LOW',      label: 'Low',      color: '#4F5B67' },
];

function fmtAgo(iso) {
  const ago = Date.now() - new Date(iso).getTime();
  const h = Math.floor(ago / 3600000);
  if (h < 1)   return Math.floor(ago / 60000) + 'm ago';
  if (h < 24)  return h + 'h ago';
  return Math.floor(h / 24) + 'd ago';
}

function statusTone(status) { return STATUSES.find(s => s.id === status)?.tone || 'neutral'; }
function statusLabel(status) { return STATUSES.find(s => s.id === status)?.label || status; }
function priorityColor(p) { return PRIORITIES.find(x => x.id === p)?.color || '#4F5B67'; }
function priorityLabel(p) { return PRIORITIES.find(x => x.id === p)?.label || p; }

function RiskScore({ score, size = 'md' }) {
  const color = score >= 80 ? '#E23D36' : score >= 60 ? '#C28B0B' : score >= 30 ? '#00B3D9' : '#2C974B';
  const dim = size === 'lg' ? { w: 56, h: 56, fs: 22 } : size === 'sm' ? { w: 36, h: 36, fs: 14 } : { w: 44, h: 44, fs: 16 };
  return (
    <div style={{ position: 'relative', width: dim.w, height: dim.h, flexShrink: 0 }}>
      <svg width={dim.w} height={dim.h} style={{ transform: 'rotate(-90deg)' }}>
        <circle cx={dim.w/2} cy={dim.h/2} r={dim.w/2 - 3} fill="none" stroke="#F2F3F3" strokeWidth="3" />
        <circle cx={dim.w/2} cy={dim.h/2} r={dim.w/2 - 3} fill="none" stroke={color} strokeWidth="3" strokeDasharray={`${2 * Math.PI * (dim.w/2 - 3) * (score/100)} ${2 * Math.PI * (dim.w/2 - 3)}`} strokeLinecap="round" />
      </svg>
      <div style={{ position: 'absolute', inset: 0, display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: dim.fs, fontWeight: 700, color: '#1A2024', fontFamily: "'Inter', sans-serif" }}>{score}</div>
    </div>
  );
}

function PriorityFlag({ priority }) {
  const p = PRIORITIES.find(x => x.id === priority);
  return (
    <span style={{ display: 'inline-flex', alignItems: 'center', gap: 4, fontSize: 11, color: p?.color, fontWeight: 600 }}>
      <span style={{ width: 8, height: 8, background: p?.color, borderRadius: 999 }} />
      {p?.label}
    </span>
  );
}

function Avatar({ name, size = 28 }) {
  const initials = (name || '?').split(' ').map(p => p[0]).slice(0,2).join('').toUpperCase();
  return <div style={{ width: size, height: size, borderRadius: 999, background: '#1A2E4B', color: '#fff', display: 'inline-flex', alignItems: 'center', justifyContent: 'center', fontSize: size * 0.36, fontWeight: 600, flexShrink: 0 }}>{initials}</div>;
}

/* ──────────────────────────────────────────────────────────
   HEADER (shared across all variants)
   ──────────────────────────────────────────────────────── */
function CasesHeader({ count, onLayoutChange, layout }) {
  return (
    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-end' }}>
      <div>
        <div style={{ fontSize: 11, color: '#4F5B67', textTransform: 'uppercase', letterSpacing: '0.08em', fontWeight: 600 }}>Overview</div>
        <h1 style={{ fontSize: 26, fontWeight: 600, color: '#1A2024', marginTop: 4, letterSpacing: '-0.01em' }}>Cases</h1>
        <div style={{ fontSize: 13, color: '#4F5B67', marginTop: 4 }}>Cases are automatically created when a verification result requires manual review. <b>{count} active</b>.</div>
      </div>
      <div style={{ display: 'flex', gap: 8, alignItems: 'center' }}>
        {onLayoutChange && (
          <div style={{ display: 'inline-flex', border: '1px solid #D5DBDB', borderRadius: 4, overflow: 'hidden' }}>
            {[
              { id: 'table',  label: 'Table',  icon: 'BarChart' },
              { id: 'kanban', label: 'Kanban', icon: 'Layers' },
              { id: 'cards',  label: 'Cards',  icon: 'Briefcase' },
            ].map((l, i) => (
              <button key={l.id} onClick={() => onLayoutChange(l.id)} style={{
                padding: '6px 10px', background: layout === l.id ? '#1A2E4B' : '#fff', color: layout === l.id ? '#fff' : '#1A2024',
                border: 'none', borderLeft: i > 0 ? '1px solid #D5DBDB' : 'none', fontSize: 12, fontWeight: 500, fontFamily: "'Inter', sans-serif",
                cursor: 'pointer', display: 'inline-flex', alignItems: 'center', gap: 4, transition: 'all 100ms cubic-bezier(0.4,0,0.2,1)',
              }}>
                <PortalIcon name={l.icon} size={12} color="currentColor" />{l.label}
              </button>
            ))}
          </div>
        )}
        <PortalButton variant="secondary" icon={<PortalIcon name="Download" size={13} color="#1A2E4B" />}>Export</PortalButton>
        <PortalButton variant="cta" icon={<PortalIcon name="Plus" size={13} color="#fff" />}>New case</PortalButton>
      </div>
    </div>
  );
}

/* ──────────────────────────────────────────────────────────
   VARIANT A — Classic Data Table
   ──────────────────────────────────────────────────────── */
function CasesTableView({ cases, onOpenDetail }) {
  const [filter, setFilter] = useStateC('all');
  const [query, setQuery] = useStateC('');
  const filtered = useMemoC(() => {
    let r = cases;
    if (filter !== 'all') r = r.filter(c => c.status === filter);
    if (query) r = r.filter(c => (c.subjectName + c.caseId + (c.assignee || '')).toLowerCase().includes(query.toLowerCase()));
    return r;
  }, [cases, filter, query]);

  const chips = [
    { id: 'all',       label: 'All',       count: cases.length },
    { id: 'OPEN',      label: 'Open',      count: cases.filter(c => c.status === 'OPEN').length },
    { id: 'IN_REVIEW', label: 'In Review', count: cases.filter(c => c.status === 'IN_REVIEW').length },
    { id: 'ESCALATED', label: 'Escalated', count: cases.filter(c => c.status === 'ESCALATED').length },
    { id: 'RESOLVED',  label: 'Resolved',  count: cases.filter(c => c.status === 'RESOLVED').length },
  ];

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 14 }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', gap: 16 }}>
        <div style={{ display: 'flex', gap: 6 }}>
          {chips.map(c => (
            <button key={c.id} onClick={() => setFilter(c.id)} style={{
              padding: '6px 12px', borderRadius: 16, border: `1px solid ${filter === c.id ? '#1A2E4B' : '#D5DBDB'}`,
              background: filter === c.id ? '#1A2E4B' : '#fff', color: filter === c.id ? '#fff' : '#1A2024',
              fontSize: 12, fontWeight: 500, cursor: 'pointer', display: 'inline-flex', alignItems: 'center', gap: 6, fontFamily: "'Inter', sans-serif",
            }}>{c.label} <span style={{ fontSize: 10, opacity: 0.7, fontFamily: "'JetBrains Mono', monospace" }}>{c.count}</span></button>
          ))}
        </div>
        <div style={{ position: 'relative', width: 280 }}>
          <span style={{ position: 'absolute', left: 10, top: '50%', transform: 'translateY(-50%)' }}><PortalIcon name="Search" size={13} color="#4F5B67" /></span>
          <input value={query} onChange={e => setQuery(e.target.value)} placeholder="Search subject, case ID, assignee…"
            style={{ width: '100%', padding: '6px 12px 6px 32px', borderRadius: 4, border: '1px solid #D5DBDB', fontSize: 12, fontFamily: "'Inter', sans-serif", outline: 'none' }}
            onFocus={e => { e.currentTarget.style.borderColor = '#00B3D9'; e.currentTarget.style.boxShadow = '0 0 0 3px rgba(0,179,217,0.15)'; }}
            onBlur={e => { e.currentTarget.style.borderColor = '#D5DBDB'; e.currentTarget.style.boxShadow = 'none'; }} />
        </div>
      </div>

      <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, overflow: 'hidden', boxShadow: '0 1px 1px 0 rgba(0,28,36,0.30)' }}>
        <table style={{ width: '100%', borderCollapse: 'separate', borderSpacing: 0, fontSize: 13 }}>
          <thead style={{ background: '#F2F3F3' }}>
            <tr>{['Case ID', 'Subject', 'Status', 'Priority', 'Risk', 'Assignee', 'Created', ''].map(h => (
              <th key={h} style={{ padding: '10px 14px', textAlign: 'left', fontWeight: 600, fontSize: 11, color: '#4F5B67', textTransform: 'uppercase', letterSpacing: '0.05em', borderBottom: '1px solid #e9ebed' }}>{h}</th>
            ))}</tr>
          </thead>
          <tbody>
            {filtered.map(c => (
              <tr key={c.caseId} onClick={() => onOpenDetail(c)}
                  style={{ cursor: 'pointer', transition: 'background 100ms cubic-bezier(0.4,0,0.2,1)' }}
                  onMouseEnter={e => e.currentTarget.style.background = '#F8FAFC'}
                  onMouseLeave={e => e.currentTarget.style.background = 'transparent'}>
                <td style={{ padding: '12px 14px', borderBottom: '1px solid #e9ebed', fontFamily: "'JetBrains Mono', monospace", fontSize: 11, color: '#00B3D9' }}>{c.short}</td>
                <td style={{ padding: '12px 14px', borderBottom: '1px solid #e9ebed' }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
                    <Avatar name={c.subjectName} size={28} />
                    <div>
                      <div style={{ fontWeight: 500 }}>{c.subjectName}</div>
                      <div style={{ fontSize: 10, color: '#4F5B67', fontFamily: "'JetBrains Mono', monospace" }}>{c.subjectId}</div>
                    </div>
                  </div>
                </td>
                <td style={{ padding: '12px 14px', borderBottom: '1px solid #e9ebed' }}><PortalBadge variant={statusTone(c.status)}>{statusLabel(c.status)}</PortalBadge></td>
                <td style={{ padding: '12px 14px', borderBottom: '1px solid #e9ebed' }}><PriorityFlag priority={c.priority} /></td>
                <td style={{ padding: '12px 14px', borderBottom: '1px solid #e9ebed' }}><RiskScore score={c.riskScore} size="sm" /></td>
                <td style={{ padding: '12px 14px', borderBottom: '1px solid #e9ebed', color: c.assignee ? '#1A2024' : '#4F5B67', fontStyle: c.assignee ? 'normal' : 'italic' }}>{c.assignee || 'Unassigned'}</td>
                <td style={{ padding: '12px 14px', borderBottom: '1px solid #e9ebed', color: '#4F5B67', fontSize: 12 }}>{fmtAgo(c.createdAt)}</td>
                <td style={{ padding: '12px 14px', borderBottom: '1px solid #e9ebed', textAlign: 'right' }}>
                  <PortalIcon name="ChevronRight" size={13} color="#4F5B67" />
                </td>
              </tr>
            ))}
            {!filtered.length && <tr><td colSpan={8} style={{ padding: 36, textAlign: 'center', color: '#4F5B67' }}>No cases match your filters.</td></tr>}
          </tbody>
        </table>
      </div>
    </div>
  );
}

/* ──────────────────────────────────────────────────────────
   VARIANT B — Kanban
   ──────────────────────────────────────────────────────── */
function CasesKanbanView({ cases, onOpenDetail }) {
  const columns = STATUSES.map(s => ({
    ...s,
    cases: cases.filter(c => c.status === s.id),
  }));
  const toneColor = { info: '#00B3D9', warning: '#C28B0B', danger: '#E23D36', success: '#2C974B' };

  return (
    <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: 14, alignItems: 'flex-start' }}>
      {columns.map(col => (
        <div key={col.id} style={{ background: '#F8FAFC', border: '1px solid #e9ebed', borderRadius: 8, minHeight: 200, display: 'flex', flexDirection: 'column' }}>
          <div style={{ padding: '12px 14px', borderBottom: '1px solid #e9ebed', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
              <span style={{ width: 8, height: 8, borderRadius: 999, background: toneColor[col.tone] }} />
              <span style={{ fontSize: 12, fontWeight: 600, color: '#1A2024' }}>{col.label}</span>
              <span style={{ fontSize: 10, color: '#4F5B67', fontFamily: "'JetBrains Mono', monospace", background: '#fff', padding: '1px 6px', borderRadius: 999, border: '1px solid #e9ebed' }}>{col.cases.length}</span>
            </div>
            <button style={{ background: 'transparent', border: 'none', color: '#4F5B67', cursor: 'pointer', fontSize: 16 }}>+</button>
          </div>
          <div style={{ padding: 8, display: 'flex', flexDirection: 'column', gap: 8, flex: 1 }}>
            {col.cases.map(c => (
              <div key={c.caseId} onClick={() => onOpenDetail(c)}
                onMouseEnter={e => e.currentTarget.style.boxShadow = '0 6px 12px 4px rgba(0,28,36,0.15)'}
                onMouseLeave={e => e.currentTarget.style.boxShadow = '0 1px 1px 0 rgba(0,28,36,0.30)'}
                style={{ background: '#fff', borderRadius: 6, border: '1px solid #e9ebed', borderLeft: `3px solid ${priorityColor(c.priority)}`, padding: '10px 12px', cursor: 'grab', boxShadow: '0 1px 1px 0 rgba(0,28,36,0.30)', transition: 'box-shadow 100ms cubic-bezier(0.4,0,0.2,1)' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 8 }}>
                  <span style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: 10, color: '#00B3D9' }}>{c.short}</span>
                  <PriorityFlag priority={c.priority} />
                </div>
                <div style={{ fontSize: 13, fontWeight: 600, color: '#1A2024', lineHeight: 1.3, marginBottom: 4 }}>{c.subjectName}</div>
                <div style={{ fontSize: 11, color: '#4F5B67', lineHeight: 1.5, marginBottom: 10, display: '-webkit-box', WebkitLineClamp: 2, WebkitBoxOrient: 'vertical', overflow: 'hidden' }}>{c.reason}</div>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', paddingTop: 8, borderTop: '1px solid #f1f5f9' }}>
                  {c.assignee ? <Avatar name={c.assignee} size={22} /> : <span style={{ fontSize: 10, color: '#4F5B67', fontStyle: 'italic' }}>Unassigned</span>}
                  <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                    <span style={{ fontSize: 10, color: '#4F5B67' }}>{fmtAgo(c.createdAt)}</span>
                    <RiskScore score={c.riskScore} size="sm" />
                  </div>
                </div>
              </div>
            ))}
            {!col.cases.length && <div style={{ padding: '32px 12px', textAlign: 'center', fontSize: 11, color: '#4F5B67', fontStyle: 'italic', background: '#fff', border: '1px dashed #CBD5E1', borderRadius: 6 }}>No cases</div>}
          </div>
        </div>
      ))}
    </div>
  );
}

/* ──────────────────────────────────────────────────────────
   VARIANT C — Visual Card Grid (risk-first)
   ──────────────────────────────────────────────────────── */
function CasesCardsView({ cases, onOpenDetail }) {
  const sorted = [...cases].sort((a, b) => b.riskScore - a.riskScore);
  return (
    <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: 14 }}>
      {sorted.map(c => (
        <div key={c.caseId} onClick={() => onOpenDetail(c)}
          onMouseEnter={e => { e.currentTarget.style.transform = 'translateY(-3px)'; e.currentTarget.style.boxShadow = '0 20px 25px -5px rgba(0,28,36,0.10)'; }}
          onMouseLeave={e => { e.currentTarget.style.transform = 'translateY(0)'; e.currentTarget.style.boxShadow = '0 1px 1px 0 rgba(0,28,36,0.30)'; }}
          style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, overflow: 'hidden', cursor: 'pointer', boxShadow: '0 1px 1px 0 rgba(0,28,36,0.30)', transition: 'all 200ms cubic-bezier(0.4,0,0.2,1)' }}>
          {/* Tri-bar accent on Critical/High */}
          {(c.priority === 'CRITICAL' || c.priority === 'HIGH') && (
            <div style={{ height: 2, display: 'flex' }}>
              <div style={{ flex: 3, background: '#E23D36' }} />
              <div style={{ flex: 5, background: '#1A2E4B' }} />
              <div style={{ flex: 2, background: '#00B3D9' }} />
            </div>
          )}
          <div style={{ padding: '16px 18px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 12 }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
                <Avatar name={c.subjectName} size={36} />
                <div>
                  <div style={{ fontSize: 13, fontWeight: 600, color: '#1A2024' }}>{c.subjectName}</div>
                  <div style={{ fontSize: 10, color: '#4F5B67', fontFamily: "'JetBrains Mono', monospace" }}>{c.subjectId}</div>
                </div>
              </div>
              <RiskScore score={c.riskScore} size="lg" />
            </div>
            <div style={{ fontSize: 12, color: '#4F5B67', lineHeight: 1.55, marginBottom: 12, display: '-webkit-box', WebkitLineClamp: 2, WebkitBoxOrient: 'vertical', overflow: 'hidden', minHeight: 36 }}>{c.reason}</div>
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', paddingTop: 10, borderTop: '1px solid #f1f5f9' }}>
              <div style={{ display: 'flex', gap: 6, alignItems: 'center' }}>
                <PortalBadge variant={statusTone(c.status)} size="sm">{statusLabel(c.status)}</PortalBadge>
                <PriorityFlag priority={c.priority} />
              </div>
              <span style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: 10, color: '#00B3D9' }}>{c.short}</span>
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: 10 }}>
              <span style={{ fontSize: 10, color: '#4F5B67' }}>{c.assignee || <em>Unassigned</em>} · {fmtAgo(c.createdAt)}</span>
              <span style={{ fontSize: 11, color: '#00B3D9', fontWeight: 500 }}>Open →</span>
            </div>
          </div>
        </div>
      ))}
    </div>
  );
}

/* ──────────────────────────────────────────────────────────
   Default Cases page — Table layout, with switcher
   ──────────────────────────────────────────────────────── */
function Cases({ onOpenDetail }) {
  const [layout, setLayout] = useStateC('table');
  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }} data-screen-label="Cases">
      <CasesHeader count={CASES.length} layout={layout} onLayoutChange={setLayout} />
      {layout === 'table'  && <CasesTableView  cases={CASES} onOpenDetail={onOpenDetail} />}
      {layout === 'kanban' && <CasesKanbanView cases={CASES} onOpenDetail={onOpenDetail} />}
      {layout === 'cards'  && <CasesCardsView  cases={CASES} onOpenDetail={onOpenDetail} />}
    </div>
  );
}

window.Cases = Cases;
window.CasesTableView = CasesTableView;
window.CasesKanbanView = CasesKanbanView;
window.CasesCardsView = CasesCardsView;
window.CasesHeader = CasesHeader;
