/* global React, PortalIcon, PortalButton, PortalBadge */
const { useState: useStateAd, useMemo: useMemoAd } = React;

/* ──────────────────────────────────────────────────────────
   Tier 4 — Admin & System
     · User Management         /admin/users
     · System Health           /system-health
     · Help & Support          /help
     · Document Insights       /document-analytics
     · Document Auto-Fill      /document-auto-fill
   ──────────────────────────────────────────────────────── */

/* ── User Management ───────────────────────────────────── */

const USERS = [
  { name: 'Arthur Manena',   email: 'arthur@verigate.co.za',   role: 'Owner',     mfa: true,  last: '2 min ago',    seats: 'live',   status: 'success', label: 'Active' },
  { name: 'Naledi Nkosi',    email: 'naledi@verigate.co.za',   role: 'Admin',     mfa: true,  last: '14 min ago',   seats: 'live',   status: 'success', label: 'Active' },
  { name: 'Sipho Dlamini',   email: 'sipho@verigate.co.za',    role: 'Operator',  mfa: true,  last: '1 hr ago',     seats: 'live',   status: 'success', label: 'Active' },
  { name: 'Lerato Mokoena',  email: 'lerato@verigate.co.za',   role: 'Operator',  mfa: false, last: '—',            seats: 'invited',status: 'pending', label: 'Invited' },
  { name: 'Thandiwe Khumalo',email: 'thandi@verigate.co.za',   role: 'Read-only', mfa: true,  last: 'Yesterday',    seats: 'live',   status: 'success', label: 'Active' },
  { name: 'Pieter v.d. Merwe',email: 'pieter@verigate.co.za',   role: 'Operator',  mfa: false, last: '32 days ago',  seats: 'live',   status: 'warning', label: 'Stale' },
];

function UserManagement() {
  const [query, setQuery] = useStateAd('');
  const [filter, setFilter] = useStateAd('all');
  const filtered = useMemoAd(() => {
    let r = USERS;
    if (filter !== 'all') r = r.filter(u => u.status === filter || u.role.toLowerCase() === filter);
    if (query) r = r.filter(u => (u.name + u.email).toLowerCase().includes(query.toLowerCase()));
    return r;
  }, [query, filter]);

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 14 }} data-screen-label="User Management">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-end' }}>
        <div>
          <div style={{ fontSize: 11, color: '#4F5B67', textTransform: 'uppercase', letterSpacing: '0.08em', fontWeight: 600 }}>Admin · Users</div>
          <h1 style={{ fontSize: 26, fontWeight: 600, color: '#1A2024', marginTop: 4, letterSpacing: '-0.01em' }}>User Management</h1>
          <div style={{ fontSize: 13, color: '#4F5B67', marginTop: 4 }}>Manage members, roles, and access across your organisation.</div>
        </div>
        <div style={{ display: 'flex', gap: 8 }}>
          <PortalButton variant="secondary" icon={<PortalIcon name="Download" size={13} color="#1A2E4B" />}>Export</PortalButton>
          <PortalButton variant="cta" icon={<PortalIcon name="Plus" size={13} color="#fff" />}>Invite member</PortalButton>
        </div>
      </div>

      {/* KPIs */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: 14 }}>
        {[
          { label: 'Total members',   value: USERS.length },
          { label: 'Active',          value: USERS.filter(u => u.status === 'success').length, tone: '#2C974B' },
          { label: 'Invited',         value: USERS.filter(u => u.status === 'pending').length, tone: '#C28B0B' },
          { label: 'MFA adoption',    value: Math.round(USERS.filter(u => u.mfa).length / USERS.length * 100) + '%', tone: '#1A2024' },
        ].map(s => (
          <div key={s.label} style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, padding: '14px 18px' }}>
            <div style={{ fontSize: 10, color: '#4F5B67', textTransform: 'uppercase', letterSpacing: '0.06em', fontWeight: 600 }}>{s.label}</div>
            <div style={{ fontSize: 24, fontWeight: 700, color: s.tone || '#1A2024', marginTop: 4, fontFamily: "'Inter', sans-serif" }}>{s.value}</div>
          </div>
        ))}
      </div>

      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', gap: 12 }}>
        <div style={{ display: 'flex', gap: 6 }}>
          {[
            { id: 'all',      label: 'All' },
            { id: 'owner',    label: 'Owners' },
            { id: 'admin',    label: 'Admins' },
            { id: 'operator', label: 'Operators' },
            { id: 'pending',  label: 'Invited' },
          ].map(c => (
            <button key={c.id} onClick={() => setFilter(c.id)} style={{ padding: '6px 12px', borderRadius: 16, border: `1px solid ${filter === c.id ? '#1A2E4B' : '#D5DBDB'}`, background: filter === c.id ? '#1A2E4B' : '#fff', color: filter === c.id ? '#fff' : '#1A2024', fontSize: 12, fontWeight: 500, cursor: 'pointer', fontFamily: "'Inter', sans-serif" }}>{c.label}</button>
          ))}
        </div>
        <div style={{ position: 'relative', width: 240 }}>
          <span style={{ position: 'absolute', left: 10, top: '50%', transform: 'translateY(-50%)' }}><PortalIcon name="Search" size={13} color="#4F5B67" /></span>
          <input value={query} onChange={e => setQuery(e.target.value)} placeholder="Search by name or email…" style={{ width: '100%', padding: '6px 12px 6px 32px', borderRadius: 4, border: '1px solid #D5DBDB', fontSize: 12, fontFamily: "'Inter', sans-serif", outline: 'none' }} />
        </div>
      </div>

      <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, overflow: 'hidden' }}>
        <table style={{ width: '100%', borderCollapse: 'separate', borderSpacing: 0, fontSize: 12 }}>
          <thead style={{ background: '#F2F3F3' }}>
            <tr>{['User', 'Role', 'MFA', 'Last active', 'Status', ''].map(h => <th key={h} style={{ padding: '8px 14px', textAlign: 'left', fontSize: 10, color: '#4F5B67', textTransform: 'uppercase', letterSpacing: '0.05em', fontWeight: 600, borderBottom: '1px solid #e9ebed' }}>{h}</th>)}</tr>
          </thead>
          <tbody>
            {filtered.map(u => (
              <tr key={u.email} style={{ cursor: 'pointer' }} onMouseEnter={e => e.currentTarget.style.background = '#F8FAFC'} onMouseLeave={e => e.currentTarget.style.background = 'transparent'}>
                <td style={{ padding: '10px 14px', borderBottom: '1px solid #e9ebed' }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
                    <div style={{ width: 28, height: 28, borderRadius: 999, background: '#1A2E4B', color: '#fff', display: 'inline-flex', alignItems: 'center', justifyContent: 'center', fontSize: 10, fontWeight: 600 }}>{u.name.split(' ').map(p => p[0]).slice(0,2).join('')}</div>
                    <div><div style={{ fontWeight: 500 }}>{u.name}</div><div style={{ fontSize: 11, color: '#4F5B67' }}>{u.email}</div></div>
                  </div>
                </td>
                <td style={{ padding: '10px 14px', borderBottom: '1px solid #e9ebed' }}>{u.role}</td>
                <td style={{ padding: '10px 14px', borderBottom: '1px solid #e9ebed' }}>{u.mfa ? <PortalBadge variant="success" size="sm">On</PortalBadge> : <PortalBadge variant="warning" size="sm">Off</PortalBadge>}</td>
                <td style={{ padding: '10px 14px', borderBottom: '1px solid #e9ebed', color: '#4F5B67' }}>{u.last}</td>
                <td style={{ padding: '10px 14px', borderBottom: '1px solid #e9ebed' }}><PortalBadge variant={u.status}>{u.label}</PortalBadge></td>
                <td style={{ padding: '10px 14px', borderBottom: '1px solid #e9ebed', textAlign: 'right' }}><button style={{ background: 'transparent', border: 'none', color: '#4F5B67', fontSize: 14, cursor: 'pointer' }}>⋯</button></td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

/* ── System Health ─────────────────────────────────────── */

const PROVIDERS = [
  { name: 'Department of Home Affairs (DHA)', tone: 'warning', label: 'Watch',     uptime: '99.21%', avg: '42 min', sla: 92.1, last: '12 s ago' },
  { name: 'SAPS-AFIS',                         tone: 'success', label: 'Healthy',   uptime: '99.94%', avg: '1.2 hr', sla: 98.4, last: '4 s ago' },
  { name: 'TransUnion',                        tone: 'success', label: 'Healthy',   uptime: '99.99%', avg: '6.2 s',  sla: 100,  last: '2 s ago' },
  { name: 'OpenSanctions',                     tone: 'success', label: 'Healthy',   uptime: '99.98%', avg: '1.1 s',  sla: 100,  last: '1 s ago' },
  { name: 'CIPC',                              tone: 'success', label: 'Healthy',   uptime: '99.87%', avg: '3.4 s',  sla: 99.2, last: '8 s ago' },
  { name: 'Umalusi',                           tone: 'danger',  label: 'Degraded',  uptime: '94.20%', avg: '3.4 hr', sla: 71.2, last: '3 m ago' },
  { name: 'SARS',                              tone: 'success', label: 'Healthy',   uptime: '99.91%', avg: '8.2 s',  sla: 99.6, last: '6 s ago' },
];

const INCIDENTS = [
  { date: '2026-05-18 14:42', service: 'Umalusi',                     severity: 'sev2', tone: 'danger',  msg: 'Response times exceeding 3 hours. Investigating upstream.' },
  { date: '2026-05-18 09:11', service: 'DHA',                         severity: 'sev3', tone: 'warning', msg: 'Elevated response times (42m vs target 30m). Monitoring.' },
  { date: '2026-05-15 16:28', service: 'All',                         severity: 'info', tone: 'info',    msg: 'Scheduled maintenance window 22:00–23:00 SAST.' },
  { date: '2026-05-12 11:04', service: 'OpenSanctions',               severity: 'resolved', tone: 'success', msg: 'Intermittent timeouts resolved at 11:48 SAST.' },
];

function SystemHealth() {
  const tone = { success: '#2C974B', warning: '#C28B0B', danger: '#E23D36', info: '#00B3D9' };
  const healthy = PROVIDERS.filter(p => p.tone === 'success').length;
  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 14 }} data-screen-label="System Health">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-end' }}>
        <div>
          <div style={{ fontSize: 11, color: '#4F5B67', textTransform: 'uppercase', letterSpacing: '0.08em', fontWeight: 600 }}>Admin · System</div>
          <h1 style={{ fontSize: 26, fontWeight: 600, color: '#1A2024', marginTop: 4, letterSpacing: '-0.01em' }}>System Health</h1>
          <div style={{ fontSize: 13, color: '#4F5B67', marginTop: 4 }}>Real-time status of every verification provider VeriGate integrates with.</div>
        </div>
        <PortalButton variant="secondary">Subscribe to status updates</PortalButton>
      </div>

      <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, padding: '20px 24px', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 16 }}>
          <span style={{ width: 14, height: 14, borderRadius: 999, background: '#2C974B', boxShadow: '0 0 12px #2C974B' }} />
          <div>
            <div style={{ fontSize: 17, fontWeight: 600 }}>{healthy === PROVIDERS.length ? 'All systems operational' : `${PROVIDERS.length - healthy} service(s) degraded`}</div>
            <div style={{ fontSize: 12, color: '#4F5B67', marginTop: 2 }}>{healthy}/{PROVIDERS.length} providers healthy · Last check 12 s ago</div>
          </div>
        </div>
        <div style={{ display: 'flex', gap: 16, fontSize: 11, color: '#4F5B67' }}>
          <div><div style={{ fontSize: 18, fontWeight: 700, color: '#1A2024' }}>99.84%</div><div>30-day uptime</div></div>
          <div><div style={{ fontSize: 18, fontWeight: 700, color: '#00B3D9' }}>1.4 s</div><div>P50 response</div></div>
          <div><div style={{ fontSize: 18, fontWeight: 700, color: '#1A2024' }}>4</div><div>Incidents (7d)</div></div>
        </div>
      </div>

      <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, overflow: 'hidden' }}>
        <div style={{ padding: '12px 18px', borderBottom: '1px solid #e9ebed' }}>
          <div style={{ fontSize: 14, fontWeight: 600 }}>Provider status</div>
          <div style={{ fontSize: 11, color: '#4F5B67', marginTop: 2 }}>Last 30 days uptime · SLA target 99.5%</div>
        </div>
        {PROVIDERS.map((p, i) => (
          <div key={p.name} style={{ display: 'flex', alignItems: 'center', gap: 14, padding: '12px 18px', borderTop: i > 0 ? '1px solid #f1f5f9' : 'none' }}>
            <span style={{ width: 10, height: 10, borderRadius: 999, background: tone[p.tone], boxShadow: `0 0 8px ${tone[p.tone]}` }} />
            <div style={{ flex: 1 }}>
              <div style={{ fontSize: 13, fontWeight: 600 }}>{p.name}</div>
              <div style={{ fontSize: 11, color: '#4F5B67', marginTop: 2, display: 'flex', gap: 14 }}>
                <span>Avg <b style={{ color: '#1A2024', fontFamily: "'JetBrains Mono', monospace" }}>{p.avg}</b></span>
                <span>SLA <b style={{ color: '#1A2024', fontFamily: "'JetBrains Mono', monospace" }}>{p.sla}%</b></span>
                <span>Last check <b style={{ color: '#1A2024' }}>{p.last}</b></span>
              </div>
            </div>
            {/* 30-day uptime bars */}
            <div style={{ display: 'flex', gap: 1, alignItems: 'flex-end' }}>
              {Array.from({ length: 30 }).map((_, d) => {
                const seed = (p.name.charCodeAt(0) + d * 7) % 100;
                const dayTone = (d === 0 || d === 5) && p.tone !== 'success' ? p.tone : seed > 95 ? 'warning' : 'success';
                return <div key={d} style={{ width: 3, height: 18, background: tone[dayTone], opacity: 0.85, borderRadius: 1 }} />;
              })}
            </div>
            <div style={{ width: 80, textAlign: 'right' }}>
              <div style={{ fontSize: 14, fontWeight: 700, fontFamily: "'JetBrains Mono', monospace", color: tone[p.tone] }}>{p.uptime}</div>
              <div style={{ fontSize: 10, color: '#4F5B67' }}>30 days</div>
            </div>
            <PortalBadge variant={p.tone}>{p.label}</PortalBadge>
          </div>
        ))}
      </div>

      <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, overflow: 'hidden' }}>
        <div style={{ padding: '12px 18px', borderBottom: '1px solid #e9ebed' }}>
          <div style={{ fontSize: 14, fontWeight: 600 }}>Recent incidents</div>
        </div>
        {INCIDENTS.map((inc, i) => (
          <div key={i} style={{ display: 'flex', gap: 14, padding: '12px 18px', borderTop: i > 0 ? '1px solid #f1f5f9' : 'none' }}>
            <span style={{ width: 8, height: 8, borderRadius: 999, background: tone[inc.tone], marginTop: 6, flexShrink: 0 }} />
            <div style={{ flex: 1 }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 4 }}>
                <span style={{ fontSize: 12, fontWeight: 600 }}>{inc.service}</span>
                <span style={{ fontSize: 10, color: '#4F5B67', fontFamily: "'JetBrains Mono', monospace" }}>{inc.date} SAST</span>
              </div>
              <div style={{ fontSize: 11, color: '#4F5B67', display: 'flex', gap: 8, alignItems: 'center' }}>
                <span style={{ fontFamily: "'JetBrains Mono', monospace", padding: '1px 6px', background: '#F2F3F3', borderRadius: 3, color: tone[inc.tone], fontWeight: 600 }}>{inc.severity.toUpperCase()}</span>
                {inc.msg}
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

/* ── Help & Support ────────────────────────────────────── */

function Help() {
  const articles = [
    { title: 'Getting started with VeriGate',         body: 'A 5-minute walkthrough of your first verification.' },
    { title: 'Setting up API keys & webhooks',         body: 'Integrate VeriGate into your onboarding flow.' },
    { title: 'POPIA compliance checklist',             body: 'Everything you need to be a registered operator.' },
    { title: 'Policy Builder fundamentals',            body: 'Build a verification policy from scratch.' },
    { title: 'Reading a verification report',          body: 'How risk scores, signals, and decisions combine.' },
    { title: 'Troubleshooting failed verifications',   body: 'Most common failures and how to resolve them.' },
  ];
  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 14 }} data-screen-label="Help">
      <div>
        <div style={{ fontSize: 11, color: '#4F5B67', textTransform: 'uppercase', letterSpacing: '0.08em', fontWeight: 600 }}>Admin · Help</div>
        <h1 style={{ fontSize: 26, fontWeight: 600, color: '#1A2024', marginTop: 4, letterSpacing: '-0.01em' }}>Help &amp; Support</h1>
        <div style={{ fontSize: 13, color: '#4F5B67', marginTop: 4 }}>Docs, tutorials, and live support for your team.</div>
      </div>

      {/* Search */}
      <div style={{ background: 'linear-gradient(160deg, #0F1A2E, #1A2E4B 60%, #1a3a5c)', borderRadius: 8, padding: '32px 28px', textAlign: 'center', position: 'relative', overflow: 'hidden' }}>
        <div style={{ position: 'absolute', inset: 0, backgroundImage: 'radial-gradient(circle, rgba(255,255,255,0.06) 1px, transparent 1px)', backgroundSize: '20px 20px', opacity: 0.6 }} />
        <div style={{ position: 'relative' }}>
          <div style={{ fontSize: 20, fontWeight: 600, color: '#fff', marginBottom: 12 }}>How can we help?</div>
          <div style={{ position: 'relative', maxWidth: 540, margin: '0 auto' }}>
            <span style={{ position: 'absolute', left: 14, top: '50%', transform: 'translateY(-50%)' }}><PortalIcon name="Search" size={14} color="#4F5B67" /></span>
            <input placeholder="Search docs, runbooks, troubleshooting…" style={{ width: '100%', padding: '12px 16px 12px 38px', borderRadius: 6, border: 'none', fontSize: 14, fontFamily: "'Inter', sans-serif", outline: 'none' }} />
          </div>
        </div>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: 12 }}>
        {[
          { icon: 'FileSearch', label: 'Documentation',   sub: 'API reference + guides', tone: '#00B3D9' },
          { icon: 'Zap',        label: 'Quick tutorials',  sub: '5-min walkthroughs',     tone: '#2C974B' },
          { icon: 'User',       label: 'Talk to support',  sub: 'Mon–Fri · 08:00–18:00 SAST', tone: '#C28B0B' },
          { icon: 'Cog',        label: 'Status page',      sub: 'Live system health',     tone: '#1A2E4B' },
        ].map(c => (
          <div key={c.label} style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, padding: '16px 18px', cursor: 'pointer', transition: 'all 200ms cubic-bezier(0.4,0,0.2,1)' }}
            onMouseEnter={e => { e.currentTarget.style.borderColor = c.tone; e.currentTarget.style.transform = 'translateY(-2px)'; e.currentTarget.style.boxShadow = '0 8px 16px -4px rgba(0,28,36,0.10)'; }}
            onMouseLeave={e => { e.currentTarget.style.borderColor = '#D5DBDB'; e.currentTarget.style.transform = 'translateY(0)'; e.currentTarget.style.boxShadow = 'none'; }}>
            <div style={{ width: 36, height: 36, borderRadius: 8, background: `${c.tone}1a`, display: 'inline-flex', alignItems: 'center', justifyContent: 'center', marginBottom: 10 }}>
              <PortalIcon name={c.icon} size={18} color={c.tone} />
            </div>
            <div style={{ fontSize: 13, fontWeight: 600 }}>{c.label}</div>
            <div style={{ fontSize: 11, color: '#4F5B67', marginTop: 2 }}>{c.sub}</div>
          </div>
        ))}
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '1.6fr 1fr', gap: 14 }}>
        <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, overflow: 'hidden' }}>
          <div style={{ padding: '12px 18px', borderBottom: '1px solid #e9ebed' }}>
            <div style={{ fontSize: 14, fontWeight: 600 }}>Popular articles</div>
          </div>
          {articles.map((a, i) => (
            <div key={a.title} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '12px 18px', borderTop: i > 0 ? '1px solid #f1f5f9' : 'none', cursor: 'pointer' }}
              onMouseEnter={e => e.currentTarget.style.background = '#F8FAFC'}
              onMouseLeave={e => e.currentTarget.style.background = 'transparent'}>
              <div>
                <div style={{ fontSize: 13, fontWeight: 500 }}>{a.title}</div>
                <div style={{ fontSize: 11, color: '#4F5B67', marginTop: 2 }}>{a.body}</div>
              </div>
              <PortalIcon name="ChevronRight" size={13} color="#4F5B67" />
            </div>
          ))}
        </div>
        <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
          <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, padding: '16px 18px' }}>
            <div style={{ fontSize: 13, fontWeight: 600 }}>Open a ticket</div>
            <div style={{ fontSize: 11, color: '#4F5B67', marginTop: 2, marginBottom: 12 }}>Average first response: <b>34 min</b></div>
            <select style={{ width: '100%', padding: '7px 10px', border: '1px solid #D5DBDB', borderRadius: 4, fontSize: 12, marginBottom: 8, fontFamily: "'Inter', sans-serif" }}>
              <option>What can we help with?</option>
              <option>API / integration issue</option>
              <option>Verification failed unexpectedly</option>
              <option>Billing question</option>
              <option>Feature request</option>
              <option>Compliance / POPIA question</option>
            </select>
            <textarea placeholder="Describe the issue…" rows={4} style={{ width: '100%', padding: '8px 10px', border: '1px solid #D5DBDB', borderRadius: 4, fontSize: 12, fontFamily: "'Inter', sans-serif", resize: 'vertical' }} />
            <PortalButton variant="cta" style={{ width: '100%', marginTop: 8 }}>Open ticket →</PortalButton>
          </div>
          <div style={{ background: '#F8FAFC', border: '1px dashed #CBD5E1', borderRadius: 8, padding: '12px 14px', fontSize: 11, color: '#4F5B67', lineHeight: 1.55 }}>
            <b style={{ color: '#1A2E4B' }}>Phone:</b> +27 82 211 8921<br />
            <b style={{ color: '#1A2E4B' }}>Email:</b> support@verigate.co.za<br />
            <b style={{ color: '#1A2E4B' }}>Hours:</b> Mon–Fri 08:00–18:00 SAST
          </div>
        </div>
      </div>
    </div>
  );
}

/* ── Document Insights (analytics) ─────────────────────── */

function DocumentInsights() {
  const types = [
    { name: 'SA ID Card',        count: 4820, tampering: 96, pass: 98.4 },
    { name: 'Passport',           count: 1240, tampering: 91, pass: 96.8 },
    { name: "Driver's License",   count: 892,  tampering: 84, pass: 93.2 },
    { name: 'Utility Bill',       count: 638,  tampering: 72, pass: 88.4 },
    { name: 'CIPC Registration',  count: 312,  tampering: 89, pass: 95.1 },
    { name: 'Tax Clearance',      count: 184,  tampering: 94, pass: 97.6 },
  ];
  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 14 }} data-screen-label="Document Insights">
      <div>
        <div style={{ fontSize: 11, color: '#4F5B67', textTransform: 'uppercase', letterSpacing: '0.08em', fontWeight: 600 }}>Identity · Insights</div>
        <h1 style={{ fontSize: 26, fontWeight: 600, color: '#1A2024', marginTop: 4, letterSpacing: '-0.01em' }}>Document Insights</h1>
        <div style={{ fontSize: 13, color: '#4F5B67', marginTop: 4 }}>Cross-document analytics: tampering rates by type, fraud trends, and processing performance.</div>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: 14 }}>
        {[
          { label: 'Docs processed (30d)', value: '8,086',  tone: '#1A2024' },
          { label: 'Authentic rate',       value: '94.2%',  tone: '#2C974B' },
          { label: 'Tampering flagged',    value: '4.1%',   tone: '#C28B0B' },
          { label: 'Hard fraud caught',    value: '1.7%',   tone: '#E23D36' },
        ].map(s => (
          <div key={s.label} style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, padding: '14px 18px' }}>
            <div style={{ fontSize: 10, color: '#4F5B67', textTransform: 'uppercase', letterSpacing: '0.06em', fontWeight: 600 }}>{s.label}</div>
            <div style={{ fontSize: 24, fontWeight: 700, color: s.tone, marginTop: 4 }}>{s.value}</div>
          </div>
        ))}
      </div>

      <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, overflow: 'hidden' }}>
        <div style={{ padding: '12px 18px', borderBottom: '1px solid #e9ebed' }}>
          <div style={{ fontSize: 14, fontWeight: 600 }}>By document type</div>
          <div style={{ fontSize: 11, color: '#4F5B67', marginTop: 2 }}>Volume · tampering integrity · pass rate</div>
        </div>
        {types.map((t, i) => (
          <div key={t.name} style={{ display: 'flex', alignItems: 'center', gap: 14, padding: '12px 18px', borderTop: i > 0 ? '1px solid #f1f5f9' : 'none' }}>
            <div style={{ flex: 1 }}>
              <div style={{ fontSize: 13, fontWeight: 600 }}>{t.name}</div>
              <div style={{ fontSize: 11, color: '#4F5B67' }}>{t.count.toLocaleString('en-ZA')} processed</div>
            </div>
            <div style={{ display: 'flex', gap: 16, alignItems: 'center' }}>
              <div style={{ width: 110 }}>
                <div style={{ fontSize: 10, color: '#4F5B67', marginBottom: 3 }}>Integrity</div>
                <div style={{ height: 5, background: '#F2F3F3', borderRadius: 3, overflow: 'hidden' }}>
                  <div style={{ width: t.tampering + '%', height: '100%', background: t.tampering >= 90 ? '#2C974B' : t.tampering >= 75 ? '#C28B0B' : '#E23D36' }} />
                </div>
                <div style={{ fontSize: 10, color: '#4F5B67', marginTop: 2, fontFamily: "'JetBrains Mono', monospace" }}>{t.tampering}%</div>
              </div>
              <div style={{ width: 110 }}>
                <div style={{ fontSize: 10, color: '#4F5B67', marginBottom: 3 }}>Pass rate</div>
                <div style={{ height: 5, background: '#F2F3F3', borderRadius: 3, overflow: 'hidden' }}>
                  <div style={{ width: t.pass + '%', height: '100%', background: '#1A2E4B' }} />
                </div>
                <div style={{ fontSize: 10, color: '#4F5B67', marginTop: 2, fontFamily: "'JetBrains Mono', monospace" }}>{t.pass}%</div>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

/* ── Document Auto-Fill ────────────────────────────────── */

function DocumentAutoFill() {
  const [dropped, setDropped] = useStateAd(false);
  const fields = dropped ? [
    { key: 'firstName',     label: 'First name',          value: 'Lerato' },
    { key: 'lastName',      label: 'Last name',           value: 'Mokoena' },
    { key: 'idNumber',      label: 'ID number',           value: '9001015800087', mono: true },
    { key: 'dateOfBirth',   label: 'Date of birth',       value: '1990-01-01', mono: true },
    { key: 'gender',        label: 'Gender',              value: 'Female' },
    { key: 'nationality',   label: 'Nationality',         value: 'South African' },
    { key: 'address',       label: 'Address',             value: '14 Cinnebar Street, Table View' },
    { key: 'postalCode',    label: 'Postal code',         value: '7441', mono: true },
  ] : [];
  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 14 }} data-screen-label="Document Auto-Fill">
      <div>
        <div style={{ fontSize: 11, color: '#4F5B67', textTransform: 'uppercase', letterSpacing: '0.08em', fontWeight: 600 }}>Identity · Auto-fill</div>
        <h1 style={{ fontSize: 26, fontWeight: 600, color: '#1A2024', marginTop: 4, letterSpacing: '-0.01em' }}>Document Auto-Fill</h1>
        <div style={{ fontSize: 13, color: '#4F5B67', marginTop: 4 }}>Drop a document, get the form populated. Saves operators ~3 minutes per onboarding.</div>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 14 }}>
        <label
          onDragOver={e => e.preventDefault()}
          onDrop={e => { e.preventDefault(); setDropped(true); }}
          style={{ background: dropped ? 'rgba(44,151,75,0.04)' : '#fff', border: `2px dashed ${dropped ? '#2C974B' : '#CBD5E1'}`, borderRadius: 8, padding: '60px 24px', textAlign: 'center', cursor: 'pointer', transition: 'all 200ms cubic-bezier(0.4,0,0.2,1)' }}>
          <input type="file" accept="image/*,application/pdf" style={{ display: 'none' }} onChange={() => setDropped(true)} />
          {dropped ? (
            <>
              <div style={{ width: 56, height: 56, borderRadius: 999, background: '#2C974B', display: 'inline-flex', alignItems: 'center', justifyContent: 'center', marginBottom: 12 }}><PortalIcon name="CheckCircle" size={28} color="#fff" stroke={2.5} /></div>
              <div style={{ fontSize: 14, fontWeight: 600 }}>sa_id_lerato.jpg uploaded</div>
              <div style={{ fontSize: 12, color: '#4F5B67', marginTop: 4 }}>8 fields extracted. Edit on the right, then push to any service form.</div>
            </>
          ) : (
            <>
              <div style={{ width: 56, height: 56, borderRadius: 12, background: 'rgba(0,179,217,0.10)', display: 'inline-flex', alignItems: 'center', justifyContent: 'center', marginBottom: 12 }}><PortalIcon name="FileSearch" size={26} color="#00B3D9" /></div>
              <div style={{ fontSize: 14, fontWeight: 600 }}>Drop any SA identity document</div>
              <div style={{ fontSize: 12, color: '#4F5B67', marginTop: 6, maxWidth: 360, marginLeft: 'auto', marginRight: 'auto' }}>SA ID, passport, driver's license — fields are extracted with OCR and ready to push into any onboarding form.</div>
            </>
          )}
        </label>

        <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, overflow: 'hidden' }}>
          <div style={{ padding: '12px 18px', borderBottom: '1px solid #e9ebed', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <div>
              <div style={{ fontSize: 14, fontWeight: 600 }}>Extracted fields</div>
              <div style={{ fontSize: 11, color: '#4F5B67', marginTop: 2 }}>{dropped ? `${fields.length} fields · ready to push` : 'Drop a document to begin'}</div>
            </div>
            {dropped && <PortalButton variant="cta" size="sm">Push to form →</PortalButton>}
          </div>
          {dropped ? (
            <div style={{ padding: '10px 0' }}>
              {fields.map((f, i) => (
                <div key={f.key} style={{ padding: '8px 18px', borderTop: i > 0 ? '1px solid #f1f5f9' : 'none' }}>
                  <label style={{ fontSize: 10, color: '#4F5B67', textTransform: 'uppercase', letterSpacing: '0.06em', fontWeight: 600 }}>{f.label}</label>
                  <input defaultValue={f.value} style={{ width: '100%', padding: '4px 0', border: 'none', borderBottom: '1px solid transparent', fontSize: 13, fontFamily: f.mono ? "'JetBrains Mono', monospace" : "'Inter', sans-serif", color: '#1A2024', outline: 'none', marginTop: 2 }}
                    onFocus={e => e.currentTarget.style.borderBottomColor = '#00B3D9'}
                    onBlur={e => e.currentTarget.style.borderBottomColor = 'transparent'} />
                </div>
              ))}
            </div>
          ) : (
            <div style={{ padding: '48px 24px', textAlign: 'center', fontSize: 12, color: '#4F5B67' }}>Fields appear here once you upload.</div>
          )}
        </div>
      </div>
    </div>
  );
}

window.UserManagement = UserManagement;
window.SystemHealth = SystemHealth;
window.Help = Help;
window.DocumentInsights = DocumentInsights;
window.DocumentAutoFill = DocumentAutoFill;
