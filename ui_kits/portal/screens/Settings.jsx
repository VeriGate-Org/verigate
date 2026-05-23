/* global React, PortalIcon, PortalButton, PortalBadge */
const { useState: useStateS } = React;

function Settings() {
  const [section, setSection] = useStateS('account');
  const [emailNotif, setEmailNotif] = useStateS(true);
  const [smsNotif, setSmsNotif] = useStateS(false);
  const [webhook, setWebhook] = useStateS(true);

  const Toggle = ({ on, onToggle }) => (
    <button onClick={onToggle} style={{ width: 36, height: 20, borderRadius: 999, border: 'none', background: on ? '#00B3D9' : '#CBD5E1', position: 'relative', cursor: 'pointer', transition: 'background 200ms cubic-bezier(0.4,0,0.2,1)' }}>
      <span style={{ position: 'absolute', top: 2, left: on ? 18 : 2, width: 16, height: 16, borderRadius: 999, background: '#fff', boxShadow: '0 1px 3px rgba(0,0,0,0.2)', transition: 'left 200ms cubic-bezier(0.4,0,0.2,1)' }} />
    </button>
  );

  const Row = ({ title, desc, control }) => (
    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '14px 18px', borderBottom: '1px solid #f1f5f9' }}>
      <div style={{ flex: 1 }}>
        <div style={{ fontSize: 13, fontWeight: 500, color: '#1A2024' }}>{title}</div>
        <div style={{ fontSize: 11, color: '#4F5B67', marginTop: 2, maxWidth: 460 }}>{desc}</div>
      </div>
      <div>{control}</div>
    </div>
  );

  const navItems = [
    { id: 'account',  label: 'Account' },
    { id: 'team',     label: 'Team & roles' },
    { id: 'branding', label: 'Branding' },
    { id: 'billing',  label: 'Billing' },
    { id: 'plan',     label: 'Plan & features' },
    { id: 'api',      label: 'API & webhooks' },
    { id: 'notify',   label: 'Notifications' },
    { id: 'deeds',    label: 'Deeds operations' },
    { id: 'compliance', label: 'POPIA & compliance' },
  ];

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
      <div>
        <div style={{ fontSize: 11, color: '#4F5B67', textTransform: 'uppercase', letterSpacing: '0.08em', fontWeight: 600 }}>Settings</div>
        <h1 style={{ fontSize: 26, fontWeight: 600, color: '#1A2024', marginTop: 4, letterSpacing: '-0.01em' }}>Configuration</h1>
        <div style={{ fontSize: 13, color: '#4F5B67', marginTop: 4 }}>Manage your organisation, team, billing, and integrations.</div>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '200px 1fr', gap: 16 }}>
        {/* Sub-nav */}
        <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, padding: '8px 0', height: 'fit-content' }}>
          {navItems.map(i => (
            <button key={i.id} onClick={() => setSection(i.id)} style={{
              width: '100%', textAlign: 'left',
              padding: '8px 14px',
              background: section === i.id ? 'rgba(0,179,217,0.10)' : 'transparent',
              border: 'none', borderLeft: `3px solid ${section === i.id ? '#00B3D9' : 'transparent'}`,
              fontSize: 13, fontWeight: section === i.id ? 600 : 400,
              color: section === i.id ? '#00B3D9' : '#1A2024',
              cursor: 'pointer', fontFamily: "'Inter', sans-serif",
            }}>{i.label}</button>
          ))}
        </div>

        {/* Pane */}
        <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, overflow: 'hidden' }}>
          {section === 'account' && (
            <>
              <div style={{ padding: '14px 18px', borderBottom: '1px solid #e9ebed' }}>
                <div style={{ fontSize: 14, fontWeight: 600 }}>Organisation</div>
                <div style={{ fontSize: 11, color: '#4F5B67', marginTop: 2 }}>How your account appears across the portal and on reports.</div>
              </div>
              <div style={{ padding: '18px', display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 14 }}>
                {[
                  { label: 'Organisation name', value: 'VeriGate (Pty) Ltd' },
                  { label: 'Registration number', value: '2025/525145/07', mono: true },
                  { label: 'Primary contact', value: 'Arthur Manena' },
                  { label: 'Contact email', value: 'arthur@verigate.co.za' },
                ].map(f => (
                  <div key={f.label}>
                    <label style={{ display: 'block', fontSize: 11, fontWeight: 500, color: '#4F5B67', marginBottom: 4 }}>{f.label}</label>
                    <input defaultValue={f.value} style={{ width: '100%', padding: '8px 10px', border: '1px solid #D5DBDB', borderRadius: 4, fontSize: 13, fontFamily: f.mono ? "'JetBrains Mono', monospace" : "'Inter', sans-serif", outline: 'none' }} />
                  </div>
                ))}
              </div>
              <div style={{ padding: '12px 18px', borderTop: '1px solid #e9ebed', display: 'flex', justifyContent: 'flex-end', gap: 8, background: '#F8FAFC' }}>
                <PortalButton variant="ghost">Discard</PortalButton>
                <PortalButton variant="primary">Save changes</PortalButton>
              </div>
            </>
          )}

          {section === 'team' && (
            <>
              <div style={{ padding: '14px 18px', borderBottom: '1px solid #e9ebed', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <div>
                  <div style={{ fontSize: 14, fontWeight: 600 }}>Team members</div>
                  <div style={{ fontSize: 11, color: '#4F5B67', marginTop: 2 }}>3 active seats · 2 invited</div>
                </div>
                <PortalButton variant="cta" icon={<PortalIcon name="Plus" size={13} color="#fff" />}>Invite member</PortalButton>
              </div>
              {[
                { name: 'Arthur Manena',    email: 'arthur@verigate.co.za',   role: 'Owner',     status: 'success', statusLabel: 'Active' },
                { name: 'Naledi Nkosi',     email: 'naledi@verigate.co.za',   role: 'Admin',     status: 'success', statusLabel: 'Active' },
                { name: 'Sipho Dlamini',    email: 'sipho@verigate.co.za',    role: 'Operator',  status: 'success', statusLabel: 'Active' },
                { name: 'Lerato Mokoena',   email: 'lerato@verigate.co.za',   role: 'Operator',  status: 'pending', statusLabel: 'Invited' },
              ].map(m => (
                <div key={m.email} style={{ display: 'flex', alignItems: 'center', gap: 14, padding: '12px 18px', borderBottom: '1px solid #f1f5f9' }}>
                  <div style={{ width: 32, height: 32, borderRadius: 999, background: '#1A2E4B', color: '#fff', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: 11, fontWeight: 600 }}>{m.name.split(' ').map(p => p[0]).slice(0,2).join('')}</div>
                  <div style={{ flex: 1 }}>
                    <div style={{ fontSize: 13, fontWeight: 600 }}>{m.name}</div>
                    <div style={{ fontSize: 11, color: '#4F5B67' }}>{m.email}</div>
                  </div>
                  <span style={{ fontSize: 12, color: '#4F5B67' }}>{m.role}</span>
                  <PortalBadge variant={m.status} size="sm">{m.statusLabel}</PortalBadge>
                  <button style={{ background: 'transparent', border: 'none', color: '#4F5B67', cursor: 'pointer', fontSize: 16, padding: '0 8px' }}>⋯</button>
                </div>
              ))}
            </>
          )}

          {section === 'notify' && (
            <>
              <div style={{ padding: '14px 18px', borderBottom: '1px solid #e9ebed' }}>
                <div style={{ fontSize: 14, fontWeight: 600 }}>Notifications</div>
                <div style={{ fontSize: 11, color: '#4F5B67', marginTop: 2 }}>How you hear about completed checks, escalations, and SLAs.</div>
              </div>
              <Row title="Email summaries" desc="Daily digest of all completed verifications, sent at 18:00 SAST." control={<Toggle on={emailNotif} onToggle={() => setEmailNotif(!emailNotif)} />} />
              <Row title="SMS alerts" desc="Urgent failures and escalations only. Standard SMS rates apply." control={<Toggle on={smsNotif} onToggle={() => setSmsNotif(!smsNotif)} />} />
              <Row title="Webhook callbacks" desc={<>Live POST to <span style={{ fontFamily: "'JetBrains Mono', monospace", color: '#00B3D9' }}>https://api.verigate.co.za/hooks/in</span> on every verification state change.</>} control={<Toggle on={webhook} onToggle={() => setWebhook(!webhook)} />} />
            </>
          )}

          {section === 'api' && (
            <>
              <div style={{ padding: '14px 18px', borderBottom: '1px solid #e9ebed' }}>
                <div style={{ fontSize: 14, fontWeight: 600 }}>API keys</div>
                <div style={{ fontSize: 11, color: '#4F5B67', marginTop: 2 }}>Keep your secret keys secret. We hash and store only the first 6 characters.</div>
              </div>
              {[
                { name: 'Production',   prefix: 'vg_live_a7f9...', last: '2 days ago', envColor: '#2C974B', envLabel: 'live' },
                { name: 'Sandbox',      prefix: 'vg_test_b3c1...', last: 'Yesterday', envColor: '#00B3D9', envLabel: 'test' },
              ].map(k => (
                <div key={k.name} style={{ padding: '14px 18px', borderBottom: '1px solid #f1f5f9', display: 'flex', alignItems: 'center', gap: 14 }}>
                  <div style={{ flex: 1 }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                      <span style={{ fontSize: 13, fontWeight: 600 }}>{k.name}</span>
                      <span style={{ fontSize: 10, padding: '2px 6px', borderRadius: 3, background: k.envColor, color: '#fff', fontWeight: 600, textTransform: 'uppercase', letterSpacing: '0.06em' }}>{k.envLabel}</span>
                    </div>
                    <div style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: 12, color: '#1A2024', marginTop: 4 }}>{k.prefix}<span style={{ color: '#4F5B67' }}>****************</span></div>
                    <div style={{ fontSize: 11, color: '#4F5B67', marginTop: 2 }}>Last used {k.last}</div>
                  </div>
                  <PortalButton variant="secondary" size="sm">Reveal</PortalButton>
                  <PortalButton variant="ghost" size="sm" style={{ color: '#E23D36' }}>Rotate</PortalButton>
                </div>
              ))}
            </>
          )}

          {section === 'billing' && (
            <>
              <div style={{ padding: '14px 18px', borderBottom: '1px solid #e9ebed' }}>
                <div style={{ fontSize: 14, fontWeight: 600 }}>Current month invoice</div>
                <div style={{ fontSize: 11, color: '#4F5B67', marginTop: 2 }}>Billing period: 1–18 May 2026</div>
              </div>
              <div style={{ padding: '20px 18px', textAlign: 'center', borderBottom: '1px solid #e9ebed' }}>
                <div style={{ fontSize: 11, color: '#4F5B67', textTransform: 'uppercase', letterSpacing: '0.08em', fontWeight: 600 }}>Spent so far</div>
                <div style={{ fontSize: 36, fontWeight: 700, color: '#1A2E4B', marginTop: 4, fontFamily: "'Inter', sans-serif" }}>R 18,420<span style={{ fontSize: 14, color: '#4F5B67' }}> / R 25,000</span></div>
                <div style={{ height: 6, background: '#F2F3F3', borderRadius: 3, margin: '12px auto 0', maxWidth: 360, overflow: 'hidden' }}>
                  <div style={{ width: '73%', height: '100%', background: '#00B3D9' }} />
                </div>
                <div style={{ fontSize: 11, color: '#4F5B67', marginTop: 8 }}>73% of monthly budget · 12 days remaining</div>
              </div>
              <Row title="Auto-recharge" desc="Top up R 5,000 when balance falls below R 1,000." control={<Toggle on={true} onToggle={() => {}} />} />
              <Row title="Payment method" desc="Visa •••• 4242 · Expires 09/27" control={<PortalButton variant="link">Change →</PortalButton>} />
            </>
          )}

          {section === 'compliance' && (
            <>
              <div style={{ padding: '14px 18px', borderBottom: '1px solid #e9ebed' }}>
                <div style={{ fontSize: 14, fontWeight: 600 }}>POPIA & compliance</div>
                <div style={{ fontSize: 11, color: '#4F5B67', marginTop: 2 }}>VeriGate is a Registered Operator under the Protection of Personal Information Act.</div>
              </div>
              <Row title="Consent retention" desc="How long subject consent records are retained after a verification completes." control={<select style={{ padding: '6px 10px', border: '1px solid #D5DBDB', borderRadius: 4, fontSize: 12, fontFamily: "'Inter', sans-serif" }}><option>7 years (recommended)</option><option>5 years</option></select>} />
              <Row title="Subject access requests" desc="Automate POPIA s.23 data-access requests via the partner portal." control={<Toggle on={true} onToggle={() => {}} />} />
              <Row title="Information Officer" desc="Arthur Manena · io@verigate.co.za" control={<PortalButton variant="link">Update →</PortalButton>} />
            </>
          )}

          {section === 'branding' && (
            <>
              <div style={{ padding: '14px 18px', borderBottom: '1px solid #e9ebed' }}>
                <div style={{ fontSize: 14, fontWeight: 600 }}>Branding &amp; white-label</div>
                <div style={{ fontSize: 11, color: '#4F5B67', marginTop: 2 }}>How your tenant appears in the portal, on reports, and in subject-facing flows.</div>
              </div>
              <div style={{ padding: '18px', display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 16 }}>
                <div>
                  <label style={{ display: 'block', fontSize: 11, fontWeight: 500, color: '#4F5B67', marginBottom: 6 }}>Logo</label>
                  <div style={{ display: 'flex', gap: 12, alignItems: 'center' }}>
                    <div style={{ width: 64, height: 64, borderRadius: 8, background: '#1A2E4B', display: 'inline-flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0 }}>
                      <svg width="30" height="34" viewBox="20 25 112 122"><path fill="#E23D36" d="M76 30 C56 30 26 39 26 42 L26 74 C26 106 50 132 76 142 C102 132 126 106 126 74 L126 42 C126 39 96 30 76 30 Z"/><path d="M46 84 L63 102 L106 58" fill="none" stroke="#FFFFFF" strokeWidth="13" strokeLinecap="round" strokeLinejoin="round"/></svg>
                    </div>
                    <div>
                      <PortalButton variant="secondary" size="sm">Upload new</PortalButton>
                      <div style={{ fontSize: 10, color: '#4F5B67', marginTop: 4 }}>PNG / SVG · Max 400 KB</div>
                    </div>
                  </div>
                </div>
                <div>
                  <label style={{ display: 'block', fontSize: 11, fontWeight: 500, color: '#4F5B67', marginBottom: 6 }}>Primary brand colour</label>
                  <div style={{ display: 'flex', gap: 8, alignItems: 'center' }}>
                    <div style={{ width: 36, height: 36, borderRadius: 6, background: '#E23D36', border: '1px solid #D5DBDB' }} />
                    <input defaultValue="#E23D36" style={{ flex: 1, padding: '8px 10px', border: '1px solid #D5DBDB', borderRadius: 4, fontSize: 13, fontFamily: "'JetBrains Mono', monospace" }} />
                  </div>
                </div>
                <div>
                  <label style={{ display: 'block', fontSize: 11, fontWeight: 500, color: '#4F5B67', marginBottom: 6 }}>Tenant display name</label>
                  <input defaultValue="VeriGate Partner Portal" style={{ width: '100%', padding: '8px 10px', border: '1px solid #D5DBDB', borderRadius: 4, fontSize: 13, fontFamily: "'Inter', sans-serif" }} />
                </div>
                <div>
                  <label style={{ display: 'block', fontSize: 11, fontWeight: 500, color: '#4F5B67', marginBottom: 6 }}>Tagline (optional)</label>
                  <input placeholder="Realtime Risk Intelligence" style={{ width: '100%', padding: '8px 10px', border: '1px solid #D5DBDB', borderRadius: 4, fontSize: 13, fontFamily: "'Inter', sans-serif" }} />
                </div>
              </div>
              <Row title="White-label mode" desc={<>Hide the VeriGate logo across the portal and use your branding only. Requires <b>Enterprise</b> plan.</>} control={<Toggle on={false} onToggle={() => {}} />} />
              <Row title="Subject-facing brand" desc="Apply this branding to email notifications and the subject capture flow." control={<Toggle on={true} onToggle={() => {}} />} />
              <div style={{ padding: '12px 18px', borderTop: '1px solid #e9ebed', background: '#F8FAFC', display: 'flex', justifyContent: 'flex-end', gap: 8 }}>
                <PortalButton variant="ghost">Discard</PortalButton>
                <PortalButton variant="primary">Save branding</PortalButton>
              </div>
            </>
          )}

          {section === 'plan' && (
            <>
              <div style={{ padding: '14px 18px', borderBottom: '1px solid #e9ebed' }}>
                <div style={{ fontSize: 14, fontWeight: 600 }}>Plan &amp; features</div>
                <div style={{ fontSize: 11, color: '#4F5B67', marginTop: 2 }}>Your current plan and what's included.</div>
              </div>
              <div style={{ padding: '20px', background: 'linear-gradient(135deg, rgba(0,179,217,0.05), rgba(0,179,217,0.01))', borderBottom: '1px solid #e9ebed', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <div>
                  <div style={{ fontSize: 10, color: '#00B3D9', textTransform: 'uppercase', letterSpacing: '0.08em', fontWeight: 700 }}>Current plan</div>
                  <div style={{ fontSize: 24, fontWeight: 700, color: '#1A2E4B', marginTop: 4 }}>Business</div>
                  <div style={{ fontSize: 12, color: '#4F5B67' }}>Renews 1 June 2026 · R 1,499 / month</div>
                </div>
                <div style={{ display: 'flex', gap: 8 }}>
                  <PortalButton variant="secondary">View invoices</PortalButton>
                  <PortalButton variant="cta">Upgrade to Enterprise →</PortalButton>
                </div>
              </div>
              <div style={{ padding: '18px', display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: 14 }}>
                {[
                  { label: 'KYC checks', used: 38, included: 50, unit: 'this month' },
                  { label: 'Team seats',  used: 4,  included: 5,  unit: 'used' },
                  { label: 'API calls',   used: 1842, included: 10000, unit: 'this month' },
                ].map(s => {
                  const pct = (s.used / s.included) * 100;
                  return (
                    <div key={s.label} style={{ border: '1px solid #e9ebed', borderRadius: 6, padding: '12px 14px' }}>
                      <div style={{ fontSize: 10, color: '#4F5B67', textTransform: 'uppercase', letterSpacing: '0.06em', fontWeight: 600 }}>{s.label}</div>
                      <div style={{ fontSize: 18, fontWeight: 700, color: '#1A2024', marginTop: 4, fontFamily: "'Inter', sans-serif" }}>{s.used.toLocaleString()}<span style={{ fontSize: 12, color: '#4F5B67', fontWeight: 400 }}> / {s.included.toLocaleString()}</span></div>
                      <div style={{ height: 4, background: '#F2F3F3', borderRadius: 2, marginTop: 8, overflow: 'hidden' }}>
                        <div style={{ width: pct + '%', height: '100%', background: pct > 90 ? '#E23D36' : pct > 70 ? '#C28B0B' : '#00B3D9' }} />
                      </div>
                      <div style={{ fontSize: 10, color: '#4F5B67', marginTop: 4 }}>{s.unit}</div>
                    </div>
                  );
                })}
              </div>
              <div style={{ padding: '14px 18px', borderTop: '1px solid #e9ebed' }}>
                <div style={{ fontSize: 12, fontWeight: 600, color: '#1A2024', marginBottom: 10 }}>Features included</div>
                {[
                  { feat: 'Partner Portal',          on: true },
                  { feat: 'Policy Builder',          on: true },
                  { feat: 'Bulk uploads (5k/job)',    on: true },
                  { feat: 'API + webhook access',     on: true },
                  { feat: 'Priority support · 4h',    on: true },
                  { feat: 'Continuous monitoring',    on: false, gated: 'Enterprise' },
                  { feat: 'Composite Reports',        on: false, gated: 'Enterprise' },
                  { feat: 'AI Assistant',             on: false, gated: 'Enterprise' },
                  { feat: 'Custom SLAs · 99.95%',     on: false, gated: 'Enterprise' },
                ].map(f => (
                  <div key={f.feat} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '7px 0', borderBottom: '1px solid #f1f5f9', fontSize: 12 }}>
                    <span style={{ color: f.on ? '#1A2024' : '#94A3B8', display: 'inline-flex', alignItems: 'center', gap: 8 }}>
                      <span style={{ color: f.on ? '#2C974B' : '#CBD5E1', fontWeight: 700 }}>{f.on ? '✓' : '✕'}</span>
                      {f.feat}
                    </span>
                    {f.gated && <PortalBadge variant="info" size="sm">Requires {f.gated}</PortalBadge>}
                  </div>
                ))}
              </div>
            </>
          )}

          {section === 'deeds' && (
            <>
              <div style={{ padding: '14px 18px', borderBottom: '1px solid #e9ebed' }}>
                <div style={{ fontSize: 14, fontWeight: 600 }}>Deeds operations</div>
                <div style={{ fontSize: 11, color: '#4F5B67', marginTop: 2 }}>Provider-independent deeds operations. Configure connectors, cache rules, and rate limits.</div>
              </div>
              <div style={{ padding: '18px', display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: 14 }}>
                {[
                  { label: 'Lookups today', value: '1,284', tone: '#1A2024' },
                  { label: 'Cache hit rate', value: '78%', tone: '#2C974B' },
                  { label: 'Avg. latency', value: '2.1 s', tone: '#00B3D9' },
                ].map(s => (
                  <div key={s.label} style={{ border: '1px solid #e9ebed', borderRadius: 6, padding: '12px 14px' }}>
                    <div style={{ fontSize: 10, color: '#4F5B67', textTransform: 'uppercase', letterSpacing: '0.06em', fontWeight: 600 }}>{s.label}</div>
                    <div style={{ fontSize: 20, fontWeight: 700, color: s.tone, marginTop: 4, fontFamily: "'Inter', sans-serif" }}>{s.value}</div>
                  </div>
                ))}
              </div>
              <div style={{ padding: '14px 18px', borderTop: '1px solid #e9ebed' }}>
                <div style={{ fontSize: 12, fontWeight: 600, color: '#1A2024', marginBottom: 10 }}>Provider connectors</div>
                {[
                  { name: 'DRDLR Deeds Office',          region: 'National',    status: 'success', label: 'Connected',         endpoint: 'deeds.drdlr.gov.za' },
                  { name: 'Surveyor-General GIS',         region: 'WC, GP, KZN', status: 'success', label: 'Connected',         endpoint: 'sg.westerncape.gov.za' },
                  { name: 'City of Cape Town Municipal',  region: 'WC',          status: 'success', label: 'Connected',         endpoint: 'gis.capetown.gov.za' },
                  { name: 'City of Johannesburg',         region: 'GP',          status: 'warning', label: 'Degraded',          endpoint: 'gis.joburg.org.za' },
                  { name: 'eThekwini Municipality',       region: 'KZN',         status: 'pending', label: 'Not configured',    endpoint: '—' },
                ].map(p => (
                  <div key={p.name} style={{ display: 'flex', alignItems: 'center', gap: 12, padding: '10px 0', borderBottom: '1px solid #f1f5f9' }}>
                    <span style={{ width: 8, height: 8, borderRadius: 999, background: p.status === 'success' ? '#2C974B' : p.status === 'warning' ? '#C28B0B' : '#CBD5E1', boxShadow: p.status === 'success' ? '0 0 6px #2C974B' : 'none', flexShrink: 0 }} />
                    <div style={{ flex: 1 }}>
                      <div style={{ fontSize: 13, fontWeight: 500 }}>{p.name}</div>
                      <div style={{ fontSize: 11, color: '#4F5B67', fontFamily: "'JetBrains Mono', monospace" }}>{p.endpoint} · {p.region}</div>
                    </div>
                    <PortalBadge variant={p.status}>{p.label}</PortalBadge>
                    <PortalButton variant="ghost" size="sm">Configure</PortalButton>
                  </div>
                ))}
              </div>
              <Row title="Cache TTL" desc="How long deeds responses are cached before re-querying upstream." control={<select style={{ padding: '6px 10px', border: '1px solid #D5DBDB', borderRadius: 4, fontSize: 12, fontFamily: "'Inter', sans-serif" }}><option>24 hours (recommended)</option><option>12 hours</option><option>1 hour</option><option>No cache</option></select>} />
              <Row title="Provider fallback" desc="On primary failure, retry against secondary providers automatically." control={<Toggle on={true} onToggle={() => {}} />} />
              <Row title="Rate limit per minute" desc="Maximum upstream calls per provider. Excess queued." control={<input defaultValue="60" style={{ width: 80, padding: '4px 8px', border: '1px solid #D5DBDB', borderRadius: 4, fontSize: 12, fontFamily: "'JetBrains Mono', monospace", textAlign: 'right' }} />} />
            </>
          )}
        </div>
      </div>
    </div>
  );
}

window.Settings = Settings;
