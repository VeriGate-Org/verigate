/* global React, PortalIcon, PortalButton, PortalBadge */
const { useState: useStateV, useMemo: useMemoV } = React;

const SAMPLE_ROWS = [
  { id: 'VG-2026-0142', type: 'KYC',       subject: 'John M. Doe',            country: 'ZA', status: 'success', statusLabel: 'Verified',    updated: '2 min ago',  cost: 45.00 },
  { id: 'VG-2026-0141', type: 'SANCTIONS', subject: 'Acme Corp Ltd',          country: 'ZA', status: 'warning', statusLabel: 'Review',      updated: '15 min ago', cost: 120.00 },
  { id: 'VG-2026-0140', type: 'CREDIT',    subject: 'Jane Smith',             country: 'ZA', status: 'danger',  statusLabel: 'Failed',      updated: '1 hr ago',   cost: 85.00 },
  { id: 'VG-2026-0139', type: 'ID',        subject: 'Bob Williams',           country: 'ZA', status: 'info',    statusLabel: 'In Progress', updated: 'Just now',   cost: 25.00 },
  { id: 'VG-2026-0138', type: 'CRIMINAL',  subject: 'Lerato Mokoena',         country: 'ZA', status: 'success', statusLabel: 'Verified',    updated: '2 hr ago',   cost: 250.00 },
  { id: 'VG-2026-0137', type: 'KYC',       subject: 'Pieter van der Merwe',   country: 'ZA', status: 'pending', statusLabel: 'Not Started', updated: '4 hr ago',   cost: 45.00 },
  { id: 'VG-2026-0136', type: 'BIOMETRIC', subject: 'Naledi Nkosi',           country: 'ZA', status: 'success', statusLabel: 'Verified',    updated: 'Yesterday',  cost: 180.00 },
  { id: 'VG-2026-0135', type: 'PEP',       subject: 'Sipho Dlamini',          country: 'ZA', status: 'success', statusLabel: 'Verified',    updated: 'Yesterday',  cost: 95.00 },
  { id: 'VG-2026-0134', type: 'CREDIT',    subject: 'Mandla Tshabalala',      country: 'ZA', status: 'warning', statusLabel: 'Review',      updated: '2 days ago', cost: 85.00 },
  { id: 'VG-2026-0133', type: 'KYC',       subject: 'Thandiwe Khumalo',       country: 'ZA', status: 'success', statusLabel: 'Verified',    updated: '2 days ago', cost: 45.00 },
];

window.SAMPLE_ROWS = SAMPLE_ROWS;

const FILTERS = [
  { id: 'all',     label: 'All',         count: 10 },
  { id: 'open',    label: 'Open',        count: 4 },
  { id: 'success', label: 'Verified',    count: 5 },
  { id: 'warning', label: 'Needs review',count: 2 },
  { id: 'danger',  label: 'Failed',      count: 1 },
];

function Verifications({ rows, onOpenDetail, onStartNew, onDelete }) {
  const [filter, setFilter] = useStateV('all');
  const [query, setQuery] = useStateV('');
  const filtered = useMemoV(() => {
    let r = rows;
    if (filter === 'open') r = r.filter(x => x.status === 'info' || x.status === 'pending' || x.status === 'warning');
    else if (filter !== 'all') r = r.filter(x => x.status === filter);
    if (query) r = r.filter(x => (x.subject + x.id + x.type).toLowerCase().includes(query.toLowerCase()));
    return r;
  }, [rows, filter, query]);

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-end' }}>
        <div>
          <div style={{ fontSize: 11, color: '#4F5B67', textTransform: 'uppercase', letterSpacing: '0.08em', fontWeight: 600 }}>Verifications</div>
          <h1 style={{ fontSize: 26, fontWeight: 600, color: '#1A2024', marginTop: 4, letterSpacing: '-0.01em' }}>All verifications</h1>
          <div style={{ fontSize: 13, color: '#4F5B67', marginTop: 4 }}>Filter, search, and drill into any verification record.</div>
        </div>
        <div style={{ display: 'flex', gap: 8 }}>
          <PortalButton variant="secondary" icon={<PortalIcon name="Download" size={13} color="#1A2E4B" />}>Export CSV</PortalButton>
          <PortalButton variant="cta" icon={<PortalIcon name="Plus" size={13} color="#fff" />} onClick={onStartNew}>New verification</PortalButton>
        </div>
      </div>

      {/* Filter chips + search */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', gap: 16 }}>
        <div style={{ display: 'flex', gap: 6 }}>
          {FILTERS.map(f => (
            <button key={f.id} onClick={() => setFilter(f.id)} style={{
              padding: '6px 12px', borderRadius: 16, border: `1px solid ${filter === f.id ? '#1A2E4B' : '#D5DBDB'}`,
              background: filter === f.id ? '#1A2E4B' : '#fff', color: filter === f.id ? '#fff' : '#1A2024',
              fontSize: 12, fontWeight: 500, fontFamily: "'Inter', sans-serif", cursor: 'pointer',
              transition: 'all 100ms cubic-bezier(0.4,0,0.2,1)',
              display: 'inline-flex', alignItems: 'center', gap: 6,
            }}>
              {f.label}
              <span style={{ fontSize: 10, opacity: 0.7, fontFamily: "'JetBrains Mono', monospace" }}>{f.count}</span>
            </button>
          ))}
        </div>
        <div style={{ position: 'relative', width: 280 }}>
          <span style={{ position: 'absolute', left: 10, top: '50%', transform: 'translateY(-50%)', display: 'inline-flex' }}>
            <PortalIcon name="Search" size={13} color="#4F5B67" />
          </span>
          <input value={query} onChange={e => setQuery(e.target.value)} placeholder="Search subject or ID…"
            style={{ width: '100%', padding: '6px 12px 6px 32px', borderRadius: 4, border: '1px solid #D5DBDB', background: '#fff', fontSize: 12, fontFamily: "'Inter', sans-serif", outline: 'none' }}
            onFocus={e => { e.currentTarget.style.borderColor = '#00B3D9'; e.currentTarget.style.boxShadow = '0 0 0 3px rgba(0,179,217,0.15)'; }}
            onBlur={e => { e.currentTarget.style.borderColor = '#D5DBDB'; e.currentTarget.style.boxShadow = 'none'; }}
          />
        </div>
      </div>

      <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, overflow: 'hidden', boxShadow: '0 1px 1px 0 rgba(0,28,36,0.30)' }}>
        <table style={{ width: '100%', borderCollapse: 'separate', borderSpacing: 0, fontSize: 13 }}>
          <thead style={{ background: '#F2F3F3' }}>
            <tr>
              {['Correlation ID', 'Type', 'Subject', 'Status', 'Updated', 'Cost', ''].map(h => (
                <th key={h} style={{ padding: '10px 14px', textAlign: 'left', fontWeight: 600, fontSize: 11, color: '#4F5B67', textTransform: 'uppercase', letterSpacing: '0.05em', borderBottom: '1px solid #e9ebed' }}>{h}</th>
              ))}
            </tr>
          </thead>
          <tbody>
            {filtered.map(r => (
              <tr key={r.id}
                  onMouseEnter={e => e.currentTarget.style.background = '#F8FAFC'}
                  onMouseLeave={e => e.currentTarget.style.background = 'transparent'}
                  style={{ cursor: 'pointer', transition: 'background 100ms cubic-bezier(0.4,0,0.2,1)' }}>
                <td onClick={() => onOpenDetail(r)} style={{ padding: '11px 14px', borderBottom: '1px solid #e9ebed', fontFamily: "'JetBrains Mono', monospace", fontSize: 12, color: '#00B3D9' }}>{r.id}</td>
                <td onClick={() => onOpenDetail(r)} style={{ padding: '11px 14px', borderBottom: '1px solid #e9ebed', fontWeight: 500 }}>{r.type}</td>
                <td onClick={() => onOpenDetail(r)} style={{ padding: '11px 14px', borderBottom: '1px solid #e9ebed' }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                    <div style={{ width: 24, height: 24, borderRadius: 999, background: '#1A2E4B', color: '#fff', display: 'inline-flex', alignItems: 'center', justifyContent: 'center', fontSize: 10, fontWeight: 600 }}>{r.subject.split(' ').map(p => p[0]).slice(0,2).join('')}</div>
                    {r.subject}
                  </div>
                </td>
                <td onClick={() => onOpenDetail(r)} style={{ padding: '11px 14px', borderBottom: '1px solid #e9ebed' }}><PortalBadge variant={r.status}>{r.statusLabel}</PortalBadge></td>
                <td onClick={() => onOpenDetail(r)} style={{ padding: '11px 14px', borderBottom: '1px solid #e9ebed', color: '#4F5B67' }}>{r.updated}</td>
                <td onClick={() => onOpenDetail(r)} style={{ padding: '11px 14px', borderBottom: '1px solid #e9ebed', fontFamily: "'JetBrains Mono', monospace", fontSize: 12 }}>R {r.cost.toFixed(2)}</td>
                <td style={{ padding: '11px 14px', borderBottom: '1px solid #e9ebed', textAlign: 'right' }}>
                  <button onClick={e => { e.stopPropagation(); onOpenDetail(r); }} style={{ background: 'transparent', border: 'none', color: '#00B3D9', fontSize: 12, fontWeight: 500, cursor: 'pointer', padding: '0 6px' }}>Open</button>
                  <button onClick={e => { e.stopPropagation(); onDelete(r); }} style={{ background: 'transparent', border: 'none', color: '#E23D36', fontSize: 12, fontWeight: 500, cursor: 'pointer', padding: '0 6px' }}>Delete</button>
                </td>
              </tr>
            ))}
            {!filtered.length && (
              <tr><td colSpan={7} style={{ padding: '36px 14px', textAlign: 'center', color: '#4F5B67', fontSize: 13 }}>No verifications match these filters.</td></tr>
            )}
          </tbody>
        </table>
        <div style={{ padding: '10px 14px', borderTop: '1px solid #e9ebed', display: 'flex', justifyContent: 'space-between', alignItems: 'center', fontSize: 11, color: '#4F5B67', background: '#F8FAFC' }}>
          <span>Showing <b style={{ color: '#1A2024' }}>{filtered.length}</b> of {rows.length} records</span>
          <div style={{ display: 'flex', gap: 4 }}>
            <button style={{ padding: '3px 9px', border: '1px solid #D5DBDB', background: '#fff', borderRadius: 2, fontSize: 11, cursor: 'pointer' }}>← Prev</button>
            <button style={{ padding: '3px 9px', border: '1px solid #1A2E4B', background: '#1A2E4B', color: '#fff', borderRadius: 2, fontSize: 11, cursor: 'pointer' }}>1</button>
            <button style={{ padding: '3px 9px', border: '1px solid #D5DBDB', background: '#fff', borderRadius: 2, fontSize: 11, cursor: 'pointer' }}>2</button>
            <button style={{ padding: '3px 9px', border: '1px solid #D5DBDB', background: '#fff', borderRadius: 2, fontSize: 11, cursor: 'pointer' }}>Next →</button>
          </div>
        </div>
      </div>
    </div>
  );
}

window.Verifications = Verifications;
