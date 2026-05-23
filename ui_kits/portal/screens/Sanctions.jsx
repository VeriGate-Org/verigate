/* global React, PortalIcon, PortalButton, PortalBadge */
const { useState: useStateS, useMemo: useMemoS } = React;

/* ──────────────────────────────────────────────────────────
   Sanctions & PEP — Service Page template via Sanctions
   This file becomes the pattern other /services/* pages clone.
   Exports both the canonical page and a Variants registry.
   ──────────────────────────────────────────────────────── */

const ENTITY_TYPES = ['Person', 'Company', 'Organization', 'Vessel'];

const ALGORITHMS = [
  { value: 'logic-v1', label: 'Logic v1' },
  { value: 'logic-v2', label: 'Logic v2 (recommended)' },
];

const TOPIC_OPTIONS = [
  { value: 'sanction',  label: 'Sanctions' },
  { value: 'role.pep',  label: 'Politically Exposed Persons' },
  { value: 'crime',     label: 'Adverse media / crime' },
];

/* Sample history of past screenings */
const SANCTIONS_HISTORY = [
  { id: 'SC-2026-0421', subject: 'Acme Corp Ltd',       entityType: 'Company', dataset: 'sanctions', matches: 2, status: 'warning', statusLabel: 'Review', screenedAt: '2026-05-18T13:18Z', actor: 'Sipho Dlamini' },
  { id: 'SC-2026-0420', subject: 'Jane Smith',          entityType: 'Person',  dataset: 'sanctions+pep', matches: 0, status: 'success', statusLabel: 'Clear', screenedAt: '2026-05-18T11:42Z', actor: 'Naledi Nkosi' },
  { id: 'SC-2026-0419', subject: 'Mandla Tshabalala',   entityType: 'Person',  dataset: 'sanctions+pep', matches: 1, status: 'warning', statusLabel: 'PEP found', screenedAt: '2026-05-18T10:08Z', actor: 'Arthur Manena' },
  { id: 'SC-2026-0418', subject: 'MV Stellenbosch',     entityType: 'Vessel',  dataset: 'sanctions',     matches: 0, status: 'success', statusLabel: 'Clear', screenedAt: '2026-05-17T16:22Z', actor: 'System' },
  { id: 'SC-2026-0417', subject: 'Naledi Nkosi',        entityType: 'Person',  dataset: 'sanctions+pep', matches: 0, status: 'success', statusLabel: 'Clear', screenedAt: '2026-05-17T14:05Z', actor: 'Naledi Nkosi' },
  { id: 'SC-2026-0416', subject: 'Pyongyang Trading',   entityType: 'Company', dataset: 'sanctions',     matches: 3, status: 'danger',  statusLabel: 'Match',  screenedAt: '2026-05-17T09:30Z', actor: 'Sipho Dlamini' },
  { id: 'SC-2026-0415', subject: 'Thandiwe Khumalo',    entityType: 'Person',  dataset: 'sanctions+pep', matches: 0, status: 'success', statusLabel: 'Clear', screenedAt: '2026-05-16T18:11Z', actor: 'Arthur Manena' },
];

/* Sample match result for demo */
const DEMO_MATCH = {
  correlationId: 'SC-2026-0422',
  provider: 'OpenSanctions',
  dataset: 'sanctions+pep',
  totalMatches: 2,
  outcome: 'SUCCEEDED',
  matches: [
    {
      id: 'NK-1234567',
      score: 0.92,
      caption: 'Mandla Sipho Tshabalala',
      schema: 'Person',
      datasets: ['SA PEP Register', 'EU Consolidated'],
      topics: ['role.pep'],
      properties: {
        birthDate:    '1973-04-18',
        nationality:  'South African',
        position:     'Former Deputy Minister of Finance (2014–2017)',
        sources:      'SA Government Gazette · EU Pol Pers DB',
      },
    },
    {
      id: 'OFAC-456-A',
      score: 0.71,
      caption: 'M. Tshabalala',
      schema: 'Person',
      datasets: ['OFAC SDN'],
      topics: ['sanction'],
      properties: {
        birthDate:   '~1973',
        nationality: 'Unknown',
        listing:     'SDN List · Added 2022-03-14',
        sources:     'US Treasury OFAC',
      },
    },
  ],
};

/* ──────────────────────────────────────────────────────────
   Reusable form bits
   ──────────────────────────────────────────────────────── */

function SFField({ label, hint, required, children }) {
  return (
    <div>
      <label style={{ display: 'block', fontSize: 11, fontWeight: 500, color: '#1A2024', marginBottom: 4 }}>
        {label}{required && <span style={{ color: '#E23D36', marginLeft: 2 }}>*</span>}
      </label>
      {children}
      {hint && <div style={{ fontSize: 10, color: '#4F5B67', marginTop: 4, lineHeight: 1.5 }}>{hint}</div>}
    </div>
  );
}

function SFInput(props) {
  return (
    <input {...props}
      style={{ width: '100%', padding: '8px 11px', border: '1px solid #D5DBDB', borderRadius: 4, fontSize: 13, fontFamily: props.mono ? "'JetBrains Mono', monospace" : "'Inter', sans-serif", outline: 'none', transition: 'all 100ms cubic-bezier(0.4,0,0.2,1)', ...(props.style || {}) }}
      onFocus={e => { e.currentTarget.style.borderColor = '#00B3D9'; e.currentTarget.style.boxShadow = '0 0 0 3px rgba(0,179,217,0.15)'; }}
      onBlur={e => { e.currentTarget.style.borderColor = '#D5DBDB'; e.currentTarget.style.boxShadow = 'none'; }}
    />
  );
}

function SFSelect({ children, ...rest }) {
  return (
    <select {...rest}
      style={{ width: '100%', padding: '8px 11px', border: '1px solid #D5DBDB', borderRadius: 4, fontSize: 13, fontFamily: "'Inter', sans-serif", outline: 'none', background: '#fff', ...(rest.style || {}) }}>
      {children}
    </select>
  );
}

/* Entity-type tab bar */
function EntityTabs({ value, onChange }) {
  return (
    <div style={{ display: 'flex', borderBottom: '1px solid #e9ebed' }}>
      {ENTITY_TYPES.map(t => (
        <button key={t} type="button" onClick={() => onChange(t)} style={{
          flex: 1, padding: '10px 8px', fontSize: 12, fontWeight: value === t ? 600 : 500,
          color: value === t ? '#00B3D9' : '#4F5B67',
          background: 'transparent',
          border: 'none', borderBottom: `2px solid ${value === t ? '#00B3D9' : 'transparent'}`,
          cursor: 'pointer', fontFamily: "'Inter', sans-serif",
          transition: 'all 100ms cubic-bezier(0.4,0,0.2,1)',
        }}>{t}</button>
      ))}
    </div>
  );
}

/* Entity-specific field groups */
function PersonFields({ data, onChange }) {
  return (
    <>
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10 }}>
        <SFField label="First name" required><SFInput value={data.firstName || ''} onChange={e => onChange({ firstName: e.target.value })} autoComplete="given-name" /></SFField>
        <SFField label="Last name"  required><SFInput value={data.lastName  || ''} onChange={e => onChange({ lastName:  e.target.value })} autoComplete="family-name" /></SFField>
      </div>
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10 }}>
        <SFField label="Date of birth" hint="YYYY-MM-DD"><SFInput type="date" value={data.dob || ''} onChange={e => onChange({ dob: e.target.value })} /></SFField>
        <SFField label="Gender">
          <SFSelect value={data.gender || ''} onChange={e => onChange({ gender: e.target.value })}>
            <option value="">— Select —</option>
            <option value="male">Male</option>
            <option value="female">Female</option>
            <option value="other">Other</option>
          </SFSelect>
        </SFField>
      </div>
      <SFField label="Nationality"><SFInput value={data.nationality || ''} onChange={e => onChange({ nationality: e.target.value })} placeholder="e.g. South African" /></SFField>
      <SFField label="ID number"><SFInput value={data.idNumber || ''} onChange={e => onChange({ idNumber: e.target.value })} mono /></SFField>
      <SFField label="Address"><SFInput value={data.address || ''} onChange={e => onChange({ address: e.target.value })} placeholder="Street, city, country" /></SFField>
    </>
  );
}

function CompanyFields({ data, onChange }) {
  return (
    <>
      <SFField label="Company name" required><SFInput value={data.name || ''} onChange={e => onChange({ name: e.target.value })} /></SFField>
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10 }}>
        <SFField label="Registration #" hint="CIPC or local registry"><SFInput value={data.registrationNumber || ''} onChange={e => onChange({ registrationNumber: e.target.value })} mono /></SFField>
        <SFField label="Tax ID"><SFInput value={data.taxId || ''} onChange={e => onChange({ taxId: e.target.value })} mono /></SFField>
      </div>
      <SFField label="Jurisdiction"><SFInput value={data.jurisdiction || ''} onChange={e => onChange({ jurisdiction: e.target.value })} placeholder="e.g. South Africa" /></SFField>
      <SFField label="Address"><SFInput value={data.address || ''} onChange={e => onChange({ address: e.target.value })} /></SFField>
    </>
  );
}

function OrgFields({ data, onChange }) {
  return (
    <>
      <SFField label="Organisation name" required><SFInput value={data.name || ''} onChange={e => onChange({ name: e.target.value })} /></SFField>
      <SFField label="Jurisdiction"><SFInput value={data.jurisdiction || ''} onChange={e => onChange({ jurisdiction: e.target.value })} placeholder="e.g. United Nations" /></SFField>
      <SFField label="Address"><SFInput value={data.address || ''} onChange={e => onChange({ address: e.target.value })} /></SFField>
    </>
  );
}

function VesselFields({ data, onChange }) {
  return (
    <>
      <SFField label="Vessel name" required><SFInput value={data.name || ''} onChange={e => onChange({ name: e.target.value })} /></SFField>
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10 }}>
        <SFField label="IMO number" hint="International Maritime Org."><SFInput value={data.imoNumber || ''} onChange={e => onChange({ imoNumber: e.target.value })} mono placeholder="e.g. 9074729" /></SFField>
        <SFField label="MMSI" hint="Maritime Mobile Service Identity"><SFInput value={data.mmsi || ''} onChange={e => onChange({ mmsi: e.target.value })} mono /></SFField>
      </div>
      <SFField label="Flag state"><SFInput value={data.flagState || ''} onChange={e => onChange({ flagState: e.target.value })} placeholder="e.g. Panama" /></SFField>
    </>
  );
}

function AdvancedOptions({ open, onToggle, dataset, onDataset, algorithm, onAlgorithm, threshold, onThreshold, topics, onToggleTopic }) {
  return (
    <div style={{ border: '1px solid #e9ebed', borderRadius: 4, overflow: 'hidden' }}>
      <button type="button" onClick={onToggle} style={{ width: '100%', padding: '9px 12px', background: '#F8FAFC', border: 'none', display: 'flex', justifyContent: 'space-between', alignItems: 'center', fontSize: 12, fontWeight: 500, color: '#1A2024', cursor: 'pointer', fontFamily: "'Inter', sans-serif" }}>
        <span style={{ display: 'inline-flex', alignItems: 'center', gap: 6 }}><PortalIcon name="Cog" size={12} color="#4F5B67" /> Advanced options</span>
        <PortalIcon name="ChevronDown" size={12} color="#4F5B67" style={{ transform: open ? 'rotate(180deg)' : 'rotate(0deg)', transition: 'transform 200ms cubic-bezier(0.4,0,0.2,1)' }} />
      </button>
      {open && (
        <div style={{ padding: '14px', display: 'flex', flexDirection: 'column', gap: 12, borderTop: '1px solid #e9ebed' }}>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10 }}>
            <SFField label="Dataset"><SFInput value={dataset} onChange={e => onDataset(e.target.value)} mono /></SFField>
            <SFField label="Algorithm">
              <SFSelect value={algorithm} onChange={e => onAlgorithm(e.target.value)}>
                {ALGORITHMS.map(a => <option key={a.value} value={a.value}>{a.label}</option>)}
              </SFSelect>
            </SFField>
          </div>
          <SFField label={`Match threshold · ${threshold.toFixed(2)}`} hint="0.00 (loose) → 1.00 (exact)">
            <input type="range" min={0} max={1} step={0.01} value={threshold} onChange={e => onThreshold(parseFloat(e.target.value))} style={{ width: '100%', accentColor: '#00B3D9' }} />
            <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: 9, color: '#4F5B67', marginTop: 2, fontFamily: "'JetBrains Mono', monospace" }}><span>0.00</span><span>0.50</span><span>1.00</span></div>
          </SFField>
          <SFField label="Topics">
            <div style={{ display: 'flex', flexWrap: 'wrap', gap: 8 }}>
              {TOPIC_OPTIONS.map(t => (
                <label key={t.value} style={{ display: 'inline-flex', alignItems: 'center', gap: 5, fontSize: 12, color: '#1A2024', cursor: 'pointer' }}>
                  <input type="checkbox" checked={topics.includes(t.value)} onChange={() => onToggleTopic(t.value)} style={{ accentColor: '#00B3D9' }} />
                  {t.label}
                </label>
              ))}
            </div>
          </SFField>
        </div>
      )}
    </div>
  );
}

/* The form panel */
function SanctionsForm({ inline, onSubmit, loading }) {
  const [entity, setEntity] = useStateS('Person');
  const [data, setData] = useStateS({});
  const [advOpen, setAdvOpen] = useStateS(false);
  const [dataset, setDataset] = useStateS('sanctions');
  const [algorithm, setAlgorithm] = useStateS('logic-v2');
  const [threshold, setThreshold] = useStateS(0.7);
  const [topics, setTopics] = useStateS(['sanction', 'role.pep']);

  const merge = (partial) => setData(d => ({ ...d, ...partial }));
  const toggleTopic = (t) => setTopics(tp => tp.includes(t) ? tp.filter(x => x !== t) : [...tp, t]);

  const canSubmit = entity === 'Person' ? (data.firstName && data.lastName) : !!data.name;

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!canSubmit || loading) return;
    onSubmit({ entity, data, dataset, algorithm, threshold, topics });
  };

  return (
    <form onSubmit={handleSubmit} style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, overflow: 'hidden', boxShadow: '0 1px 1px 0 rgba(0,28,36,0.30)' }}>
      <div style={{ padding: '14px 18px', borderBottom: '1px solid #e9ebed' }}>
        <div style={{ fontSize: 14, fontWeight: 600 }}>Subject details</div>
        <div style={{ fontSize: 11, color: '#4F5B67', marginTop: 2 }}>Select an entity type and enter screening information.</div>
      </div>
      <EntityTabs value={entity} onChange={(t) => { setEntity(t); setData({}); }} />
      <div style={{ padding: '16px 18px', display: 'flex', flexDirection: 'column', gap: 12 }}>
        {entity === 'Person'       && <PersonFields  data={data} onChange={merge} />}
        {entity === 'Company'      && <CompanyFields data={data} onChange={merge} />}
        {entity === 'Organization' && <OrgFields     data={data} onChange={merge} />}
        {entity === 'Vessel'       && <VesselFields  data={data} onChange={merge} />}
        <AdvancedOptions open={advOpen} onToggle={() => setAdvOpen(!advOpen)} dataset={dataset} onDataset={setDataset} algorithm={algorithm} onAlgorithm={setAlgorithm} threshold={threshold} onThreshold={setThreshold} topics={topics} onToggleTopic={toggleTopic} />
      </div>
      <div style={{ padding: '12px 18px', borderTop: '1px solid #e9ebed', background: '#F8FAFC', display: 'flex', alignItems: 'center', justifyContent: 'space-between', gap: 8 }}>
        <span style={{ fontSize: 11, color: '#4F5B67' }}>Screened against <b style={{ color: '#1A2E4B' }}>OpenSanctions</b> · {topics.length} topic(s)</span>
        <PortalButton variant="cta" disabled={!canSubmit || loading}>{loading ? 'Screening…' : 'Screen subject →'}</PortalButton>
      </div>
    </form>
  );
}

/* Empty / loading / result */
function ResultPanel({ status, result, onExport, compact }) {
  if (status === 'idle') {
    return (
      <div style={{ background: '#fff', border: '1px dashed #CBD5E1', borderRadius: 8, padding: '40px 24px', textAlign: 'center' }}>
        <div style={{ width: 48, height: 48, borderRadius: 12, background: 'rgba(0,179,217,0.08)', display: 'inline-flex', alignItems: 'center', justifyContent: 'center', marginBottom: 10 }}>
          <PortalIcon name="Shield" size={22} color="#00B3D9" />
        </div>
        <div style={{ fontSize: 14, fontWeight: 600, color: '#1A2024' }}>No results yet</div>
        <div style={{ fontSize: 12, color: '#4F5B67', marginTop: 4, maxWidth: 320, marginLeft: 'auto', marginRight: 'auto' }}>Enter subject details on the left and click <b>Screen</b> to check OpenSanctions for matches.</div>
      </div>
    );
  }

  if (status === 'loading') {
    return (
      <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, padding: '20px 22px', display: 'flex', flexDirection: 'column', gap: 12 }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 8, color: '#0099bb', fontSize: 12, fontWeight: 600 }}>
          <span style={{ width: 8, height: 8, borderRadius: 999, background: '#00B3D9', boxShadow: '0 0 8px #00B3D9', animation: 'pulse 1.2s ease-in-out infinite' }} />
          Screening against OpenSanctions…
        </div>
        <style>{`@keyframes pulse{0%,100%{opacity:1}50%{opacity:0.35}}`}</style>
        {[80, 60, 70, 50].map((w, i) => (
          <div key={i} style={{ height: 10, width: w + '%', background: 'linear-gradient(90deg, #F2F3F3, #E4E7E7, #F2F3F3)', backgroundSize: '200% 100%', borderRadius: 3, animation: `shimmer 1.5s linear infinite`, animationDelay: i * 0.15 + 's' }} />
        ))}
        <style>{`@keyframes shimmer{0%{background-position:200% 0}100%{background-position:-200% 0}}`}</style>
      </div>
    );
  }

  if (!result) return null;

  const clean = result.totalMatches === 0;
  const tone = clean ? 'success' : result.totalMatches >= 3 ? 'danger' : 'warning';
  const toneColor = { success: '#2C974B', warning: '#C28B0B', danger: '#E23D36' }[tone];

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
      {/* Summary card */}
      <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, overflow: 'hidden' }}>
        <div style={{ display: 'flex', height: 3 }}>
          <div style={{ flex: 3, background: '#E23D36' }} />
          <div style={{ flex: 5, background: '#1A2E4B' }} />
          <div style={{ flex: 2, background: '#00B3D9' }} />
        </div>
        <div style={{ padding: '16px 18px', display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
          <div>
            <div style={{ display: 'flex', alignItems: 'center', gap: 10, marginBottom: 6 }}>
              <span style={{ fontSize: 14, fontWeight: 600 }}>Screening summary</span>
              <PortalBadge variant={tone}>{clean ? 'Clear' : `${result.totalMatches} match${result.totalMatches > 1 ? 'es' : ''}`}</PortalBadge>
            </div>
            <div style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: 11, color: '#00B3D9' }}>{result.correlationId}</div>
          </div>
          <PortalButton variant="secondary" size="sm" onClick={onExport} icon={<PortalIcon name="Download" size={12} color="#1A2E4B" />}>Export PDF</PortalButton>
        </div>
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', borderTop: '1px solid #e9ebed' }}>
          {[
            { label: 'Provider',   value: result.provider },
            { label: 'Dataset',    value: result.dataset, mono: true },
            { label: 'Matches',    value: result.totalMatches, tone: clean ? '#2C974B' : '#E23D36' },
            { label: 'Outcome',    value: result.outcome, mono: true },
          ].map((c, i) => (
            <div key={c.label} style={{ padding: '12px 16px', borderLeft: i > 0 ? '1px solid #e9ebed' : 'none' }}>
              <div style={{ fontSize: 10, color: '#4F5B67', textTransform: 'uppercase', letterSpacing: '0.06em', fontWeight: 600 }}>{c.label}</div>
              <div style={{ fontSize: 14, fontWeight: 600, color: c.tone || '#1A2024', marginTop: 4, fontFamily: c.mono ? "'JetBrains Mono', monospace" : "'Inter', sans-serif" }}>{c.value}</div>
            </div>
          ))}
        </div>
      </div>

      {clean ? (
        <div style={{ background: 'rgba(44,151,75,0.04)', border: '1px solid rgba(44,151,75,0.2)', borderRadius: 8, padding: '20px 22px', display: 'flex', gap: 14, alignItems: 'center' }}>
          <div style={{ width: 48, height: 48, borderRadius: 999, background: '#2C974B', display: 'inline-flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0, boxShadow: '0 0 16px rgba(44,151,75,0.4)' }}>
            <PortalIcon name="CheckCircle" size={26} color="#fff" stroke={2.5} />
          </div>
          <div>
            <div style={{ fontSize: 14, fontWeight: 600, color: '#2C974B' }}>No matches found</div>
            <div style={{ fontSize: 12, color: '#4F5B67', marginTop: 4 }}>This subject is clear against OpenSanctions and PEP datasets. You may proceed with onboarding.</div>
          </div>
        </div>
      ) : (
        <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, overflow: 'hidden' }}>
          <div style={{ padding: '12px 18px', borderBottom: '1px solid #e9ebed', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
            <span style={{ fontSize: 13, fontWeight: 600 }}>Match entities ({result.matches.length})</span>
            <span style={{ fontSize: 11, color: '#4F5B67' }}>Sorted by score, descending</span>
          </div>
          {result.matches.map((m, i) => (
            <div key={m.id} style={{ padding: '14px 18px', borderTop: i > 0 ? '1px solid #f1f5f9' : 'none' }}>
              <div style={{ display: 'flex', alignItems: 'flex-start', justifyContent: 'space-between', gap: 14, marginBottom: 10 }}>
                <div>
                  <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 4 }}>
                    <span style={{ fontSize: 14, fontWeight: 600, color: '#1A2024' }}>{m.caption}</span>
                    <span style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: 10, color: '#4F5B67' }}>{m.id}</span>
                  </div>
                  <div style={{ display: 'flex', gap: 5, flexWrap: 'wrap', marginBottom: 4 }}>
                    {m.topics.map(t => <PortalBadge key={t} variant={t === 'sanction' ? 'danger' : 'warning'} size="sm">{t === 'sanction' ? '⚠ Sanction' : '⚠ PEP'}</PortalBadge>)}
                    {m.datasets.map(d => <span key={d} style={{ fontSize: 10, padding: '2px 7px', background: '#F2F3F3', borderRadius: 3, color: '#4F5B67', fontFamily: "'JetBrains Mono', monospace" }}>{d}</span>)}
                  </div>
                </div>
                <div style={{ textAlign: 'right' }}>
                  <div style={{ fontSize: 22, fontWeight: 700, color: m.score >= 0.85 ? '#E23D36' : '#C28B0B', lineHeight: 1, fontFamily: "'Inter', sans-serif" }}>{(m.score * 100).toFixed(0)}<span style={{ fontSize: 12, color: '#4F5B67', fontWeight: 400 }}>/100</span></div>
                  <div style={{ fontSize: 10, color: '#4F5B67', marginTop: 2, textTransform: 'uppercase', letterSpacing: '0.06em', fontWeight: 600 }}>Match score</div>
                </div>
              </div>
              <div style={{ background: '#F8FAFC', borderRadius: 4, padding: '8px 12px', display: 'grid', gridTemplateColumns: compact ? '1fr' : '1fr 1fr', gap: '4px 16px' }}>
                {Object.entries(m.properties).map(([k, v]) => (
                  <div key={k} style={{ fontSize: 11, display: 'flex', gap: 6 }}>
                    <span style={{ color: '#4F5B67', textTransform: 'capitalize' }}>{k}:</span>
                    <span style={{ color: '#1A2024', fontWeight: 500 }}>{v}</span>
                  </div>
                ))}
              </div>
              <div style={{ marginTop: 10, display: 'flex', justifyContent: 'flex-end', gap: 6 }}>
                <PortalButton variant="ghost" size="sm">View entity →</PortalButton>
                <PortalButton variant="secondary" size="sm">Dismiss as false positive</PortalButton>
                <PortalButton variant="destructive" size="sm">Block onboarding</PortalButton>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

/* History tab */
function HistoryTab() {
  const [query, setQuery] = useStateS('');
  const filtered = useMemoS(() => {
    if (!query) return SANCTIONS_HISTORY;
    return SANCTIONS_HISTORY.filter(r => (r.subject + r.id).toLowerCase().includes(query.toLowerCase()));
  }, [query]);
  return (
    <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, overflow: 'hidden' }}>
      <div style={{ padding: '12px 18px', borderBottom: '1px solid #e9ebed', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        <div>
          <div style={{ fontSize: 14, fontWeight: 600 }}>Screening history</div>
          <div style={{ fontSize: 11, color: '#4F5B67', marginTop: 2 }}>{filtered.length} screenings · last 7 days</div>
        </div>
        <div style={{ position: 'relative', width: 240 }}>
          <span style={{ position: 'absolute', left: 10, top: '50%', transform: 'translateY(-50%)' }}><PortalIcon name="Search" size={13} color="#4F5B67" /></span>
          <SFInput value={query} onChange={e => setQuery(e.target.value)} placeholder="Search subject or ID…" style={{ padding: '6px 12px 6px 32px', fontSize: 12 }} />
        </div>
      </div>
      <table style={{ width: '100%', borderCollapse: 'separate', borderSpacing: 0, fontSize: 12 }}>
        <thead style={{ background: '#F2F3F3' }}>
          <tr>{['ID', 'Subject', 'Type', 'Dataset', 'Matches', 'Status', 'Screened', 'Actor'].map(h => (
            <th key={h} style={{ padding: '8px 14px', textAlign: 'left', fontSize: 10, color: '#4F5B67', textTransform: 'uppercase', letterSpacing: '0.05em', fontWeight: 600, borderBottom: '1px solid #e9ebed' }}>{h}</th>
          ))}</tr>
        </thead>
        <tbody>
          {filtered.map(r => (
            <tr key={r.id} style={{ cursor: 'pointer', transition: 'background 100ms cubic-bezier(0.4,0,0.2,1)' }}
                onMouseEnter={e => e.currentTarget.style.background = '#F8FAFC'}
                onMouseLeave={e => e.currentTarget.style.background = 'transparent'}>
              <td style={{ padding: '10px 14px', borderBottom: '1px solid #e9ebed', fontFamily: "'JetBrains Mono', monospace", fontSize: 11, color: '#00B3D9' }}>{r.id}</td>
              <td style={{ padding: '10px 14px', borderBottom: '1px solid #e9ebed', fontWeight: 500 }}>{r.subject}</td>
              <td style={{ padding: '10px 14px', borderBottom: '1px solid #e9ebed', color: '#4F5B67' }}>{r.entityType}</td>
              <td style={{ padding: '10px 14px', borderBottom: '1px solid #e9ebed', fontFamily: "'JetBrains Mono', monospace", fontSize: 11, color: '#4F5B67' }}>{r.dataset}</td>
              <td style={{ padding: '10px 14px', borderBottom: '1px solid #e9ebed', fontFamily: "'JetBrains Mono', monospace", fontWeight: 600, color: r.matches === 0 ? '#2C974B' : r.matches >= 3 ? '#E23D36' : '#C28B0B' }}>{r.matches}</td>
              <td style={{ padding: '10px 14px', borderBottom: '1px solid #e9ebed' }}><PortalBadge variant={r.status}>{r.statusLabel}</PortalBadge></td>
              <td style={{ padding: '10px 14px', borderBottom: '1px solid #e9ebed', color: '#4F5B67', fontSize: 11 }}>{new Date(r.screenedAt).toLocaleString('en-ZA', { dateStyle: 'short', timeStyle: 'short' })}</td>
              <td style={{ padding: '10px 14px', borderBottom: '1px solid #e9ebed', color: '#4F5B67' }}>{r.actor}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

/* ──────────────────────────────────────────────────────────
   SERVICE PAGE SHELL  — template other services will clone
   ──────────────────────────────────────────────────────── */

function ServicePageHeader({ title, subtitle, badge, onPrimary, primaryLabel }) {
  return (
    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-end' }}>
      <div>
        <div style={{ fontSize: 11, color: '#4F5B67', textTransform: 'uppercase', letterSpacing: '0.08em', fontWeight: 600, display: 'inline-flex', alignItems: 'center', gap: 8 }}>
          Screening
          {badge && <span style={{ fontSize: 9, padding: '2px 6px', background: '#00B3D9', color: '#fff', borderRadius: 3, fontWeight: 700, letterSpacing: '0.08em' }}>{badge}</span>}
        </div>
        <h1 style={{ fontSize: 26, fontWeight: 600, color: '#1A2024', marginTop: 4, letterSpacing: '-0.01em' }}>{title}</h1>
        <div style={{ fontSize: 13, color: '#4F5B67', marginTop: 4, maxWidth: 640, lineHeight: 1.55 }}>{subtitle}</div>
      </div>
      {onPrimary && <PortalButton variant="cta" onClick={onPrimary}>{primaryLabel}</PortalButton>}
    </div>
  );
}

function ServicePageTabs({ tabs, active, onChange }) {
  return (
    <div style={{ display: 'flex', gap: 0, borderBottom: '1px solid #D5DBDB' }}>
      {tabs.map(t => (
        <button key={t.id} onClick={() => onChange(t.id)} style={{
          padding: '10px 18px', background: 'transparent', border: 'none',
          borderBottom: `2px solid ${active === t.id ? '#00B3D9' : 'transparent'}`,
          fontSize: 13, fontWeight: active === t.id ? 600 : 500,
          color: active === t.id ? '#1A2024' : '#4F5B67',
          cursor: t.disabled ? 'not-allowed' : 'pointer',
          fontFamily: "'Inter', sans-serif", opacity: t.disabled ? 0.5 : 1,
          display: 'inline-flex', alignItems: 'center', gap: 6,
          transition: 'all 100ms cubic-bezier(0.4,0,0.2,1)',
        }} disabled={t.disabled}>
          {t.label}
          {t.count != null && <span style={{ fontSize: 10, padding: '1px 6px', background: active === t.id ? '#00B3D9' : '#F2F3F3', color: active === t.id ? '#fff' : '#4F5B67', borderRadius: 999, fontFamily: "'JetBrains Mono', monospace", fontWeight: 600 }}>{t.count}</span>}
          {t.disabled && <span style={{ fontSize: 9, color: '#4F5B67', textTransform: 'uppercase', letterSpacing: '0.06em' }}>soon</span>}
        </button>
      ))}
    </div>
  );
}

window.ServicePageHeader = ServicePageHeader;
window.ServicePageTabs = ServicePageTabs;

/* ──────────────────────────────────────────────────────────
   THE SANCTIONS PAGE  — supports 3 layout variants:
   variant = 'split' (form-left / result-right) — default canonical
   variant = 'stacked' (form on top, result below)
   variant = 'drawer' (history is primary, "+ New screening" opens slide-out form)
   ──────────────────────────────────────────────────────── */

function Sanctions({ variant = 'split' }) {
  const [tab, setTab] = useStateS('new');
  const [status, setStatus] = useStateS('idle'); // idle | loading | result
  const [result, setResult] = useStateS(null);
  const [drawerOpen, setDrawerOpen] = useStateS(false);

  const runScreen = (req) => {
    setStatus('loading');
    setTimeout(() => {
      // Demo: if subject name contains "tshabalala" or "pyongyang" → matches; else clear
      const has = JSON.stringify(req.data).toLowerCase();
      const hit = has.includes('tshabalala') || has.includes('pyongyang') || has.includes('m');
      setResult(hit ? DEMO_MATCH : { ...DEMO_MATCH, totalMatches: 0, matches: [] });
      setStatus('result');
      if (drawerOpen) setDrawerOpen(false);
    }, 1200);
  };

  const tabs = [
    { id: 'new',        label: 'New screening' },
    { id: 'history',    label: 'History', count: SANCTIONS_HISTORY.length },
    { id: 'monitoring', label: 'Monitoring', disabled: true },
    { id: 'batch',      label: 'Batch upload', disabled: true },
  ];

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 14 }} data-screen-label="Sanctions & PEP">
      <ServicePageHeader
        title="Sanctions & PEP screening"
        subtitle="Screen entities against OpenSanctions and politically exposed person registers. Persons, companies, organisations, vessels."
        onPrimary={variant === 'drawer' && tab === 'history' ? (() => setDrawerOpen(true)) : null}
        primaryLabel="+ New screening"
      />
      <ServicePageTabs tabs={tabs} active={tab} onChange={setTab} />

      {tab === 'history' && (
        <>
          <HistoryTab />
          {drawerOpen && (
            <div onClick={() => setDrawerOpen(false)} style={{ position: 'fixed', inset: 0, background: 'rgba(15,26,46,0.45)', display: 'flex', justifyContent: 'flex-end', zIndex: 90 }}>
              <div onClick={e => e.stopPropagation()} style={{ width: 480, height: '100%', background: '#F2F3F3', boxShadow: '-8px 0 24px rgba(0,28,36,0.2)', overflowY: 'auto', padding: 20, animation: 'slideIn 200ms cubic-bezier(0.4,0,0.2,1)' }}>
                <style>{`@keyframes slideIn{from{transform:translateX(100%)}to{transform:translateX(0)}}`}</style>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 14 }}>
                  <div style={{ fontSize: 15, fontWeight: 600 }}>New screening</div>
                  <button onClick={() => setDrawerOpen(false)} style={{ background: 'transparent', border: 'none', fontSize: 20, color: '#4F5B67', cursor: 'pointer' }}>×</button>
                </div>
                <SanctionsForm onSubmit={runScreen} loading={status === 'loading'} />
                {status !== 'idle' && <div style={{ marginTop: 14 }}><ResultPanel status={status} result={result} compact /></div>}
              </div>
            </div>
          )}
        </>
      )}

      {tab === 'new' && (
        <>
          {variant === 'split' && (
            <div style={{ display: 'grid', gridTemplateColumns: 'minmax(0, 440px) minmax(0, 1fr)', gap: 14, alignItems: 'flex-start' }}>
              <SanctionsForm onSubmit={runScreen} loading={status === 'loading'} />
              <ResultPanel status={status} result={result} />
            </div>
          )}
          {variant === 'stacked' && (
            <div style={{ display: 'flex', flexDirection: 'column', gap: 14 }}>
              <SanctionsForm onSubmit={runScreen} loading={status === 'loading'} />
              {status !== 'idle' && <ResultPanel status={status} result={result} />}
            </div>
          )}
          {variant === 'drawer' && (
            <ResultPanel status="idle" />
          )}
        </>
      )}

      {(tab === 'monitoring' || tab === 'batch') && (
        <div style={{ background: '#fff', border: '1px dashed #CBD5E1', borderRadius: 8, padding: '48px 24px', textAlign: 'center' }}>
          <div style={{ fontSize: 14, fontWeight: 600 }}>{tab === 'monitoring' ? 'Continuous monitoring' : 'Batch screening via CSV'}</div>
          <div style={{ fontSize: 12, color: '#4F5B67', marginTop: 6 }}>This tab is part of the next tier. See the design system for the upcoming pattern.</div>
        </div>
      )}
    </div>
  );
}

window.Sanctions = Sanctions;
window.SanctionsForm = SanctionsForm;
window.SanctionsResultPanel = ResultPanel;
window.SanctionsHistoryTab = HistoryTab;
window.DEMO_SANCTIONS_HISTORY = SANCTIONS_HISTORY;
window.DEMO_SANCTIONS_RESULT = DEMO_MATCH;
