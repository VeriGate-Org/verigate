/* global React, PortalIcon, PortalButton, PortalBadge */
const { useState: useStateP, useMemo: useMemoP } = React;

/* ──────────────────────────────────────────────────────────
   Policies — list + visual policy builder (edit)
   Data shape from policies/[policyId]/EditPolicy.client.tsx:
     { id, name, description, version, steps, scoringConfig{strategy,tiers,overrideRules}, status }
   ──────────────────────────────────────────────────────── */

const POLICIES = [
  { id: 'pol_onboard_v3',  name: 'Standard onboarding',   description: 'KYC + sanctions + credit. Used for all new individual customers.', version: 3, status: 'published', steps: 4, lastRun: '2 min ago',  runs: 1842, decision: 'APPROVE 91% · REVIEW 7% · REJECT 2%' },
  { id: 'pol_aml_enhanced',name: 'Enhanced AML screening',description: 'PEP + sanctions + adverse media. Applies to high-risk industries.',  version: 2, status: 'published', steps: 5, lastRun: '14 min ago', runs: 412,  decision: 'APPROVE 78% · REVIEW 18% · REJECT 4%' },
  { id: 'pol_corp_kyb',    name: 'Corporate KYB',         description: 'Company + directors + UBO + sanctions. For business onboarding.',     version: 5, status: 'published', steps: 6, lastRun: '1 hr ago',   runs: 318,  decision: 'APPROVE 84% · REVIEW 12% · REJECT 4%' },
  { id: 'pol_property',    name: 'Property transaction',  description: 'Deeds + valuation + KYC. For real-estate compliance partners.',       version: 1, status: 'draft',     steps: 4, lastRun: '—',          runs: 0,    decision: '—' },
  { id: 'pol_gaming',      name: 'Gaming registration',   description: 'Age + ID + AML lite. For licensed gaming operators.',                  version: 2, status: 'published', steps: 3, lastRun: '4 hr ago',   runs: 5024, decision: 'APPROVE 95% · REVIEW 3% · REJECT 2%' },
  { id: 'pol_archived',    name: 'Onboarding v2 (legacy)',description: 'Replaced by v3 in April 2026.',                                       version: 2, status: 'archived',  steps: 3, lastRun: '32 days ago',runs: 18204, decision: '—' },
];

const SAMPLE_POLICY = {
  id: 'pol_onboard_v3',
  name: 'Standard onboarding',
  description: 'KYC + sanctions + credit. Used for all new individual customers.',
  version: 3,
  status: 'published',
  steps: [
    { id: 'step_1', kind: 'verification', service: 'ID Verification (DHA)',    weight: 25, required: true,  threshold: 0.90 },
    { id: 'step_2', kind: 'verification', service: 'Sanctions & PEP',          weight: 30, required: true,  threshold: 0.85 },
    { id: 'step_3', kind: 'verification', service: 'Credit Bureau (TransUnion)', weight: 25, required: false, threshold: 0.70 },
    { id: 'step_4', kind: 'verification', service: 'Biometric liveness',         weight: 20, required: false, threshold: 0.80 },
  ],
  scoringConfig: {
    strategy: 'WEIGHTED_AVERAGE',
    tiers: [
      { name: 'LOW_RISK',    lowerBound: 80, upperBound: 100, decision: 'APPROVE',        color: '#2C974B' },
      { name: 'MEDIUM_RISK', lowerBound: 50, upperBound: 79,  decision: 'MANUAL_REVIEW',  color: '#C28B0B' },
      { name: 'HIGH_RISK',   lowerBound: 0,  upperBound: 49,  decision: 'REJECT',         color: '#E23D36' },
    ],
    overrideRules: [
      { id: 'or_1', when: 'sanctions.matches > 0',  decision: 'REJECT' },
      { id: 'or_2', when: 'subject.country IN OFAC', decision: 'REJECT' },
    ],
  },
};

const STATUS_TONES = { published: 'success', draft: 'warning', archived: 'pending' };

/* ──────────────────────────────────────────────────────────
   Policies list
   ──────────────────────────────────────────────────────── */

function PoliciesList({ onOpen }) {
  const [filter, setFilter] = useStateP('all');
  const filtered = useMemoP(() => filter === 'all' ? POLICIES : POLICIES.filter(p => p.status === filter), [filter]);

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 14 }} data-screen-label="Policies">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-end' }}>
        <div>
          <div style={{ fontSize: 11, color: '#4F5B67', textTransform: 'uppercase', letterSpacing: '0.08em', fontWeight: 600, display: 'inline-flex', alignItems: 'center', gap: 8 }}>
            Enterprise · Policies
            <span style={{ fontSize: 9, padding: '2px 6px', background: '#00B3D9', color: '#fff', borderRadius: 3, fontWeight: 700, letterSpacing: '0.06em' }}>NEW</span>
          </div>
          <h1 style={{ fontSize: 26, fontWeight: 600, color: '#1A2024', marginTop: 4, letterSpacing: '-0.01em' }}>Policy Builder</h1>
          <div style={{ fontSize: 13, color: '#4F5B67', marginTop: 4, maxWidth: 640, lineHeight: 1.55 }}>Compose verification workflows from service steps, scoring strategies, and override rules. Test before publishing; version every change.</div>
        </div>
        <div style={{ display: 'flex', gap: 8 }}>
          <PortalButton variant="secondary" icon={<PortalIcon name="Download" size={13} color="#1A2E4B" />}>Import policy</PortalButton>
          <PortalButton variant="cta" icon={<PortalIcon name="Plus" size={13} color="#fff" />} onClick={() => onOpen({ ...SAMPLE_POLICY, name: 'Untitled policy', id: 'pol_new', steps: [], status: 'draft' })}>New policy</PortalButton>
        </div>
      </div>

      <div style={{ display: 'flex', gap: 6 }}>
        {[
          { id: 'all',       label: 'All',       count: POLICIES.length },
          { id: 'published', label: 'Published', count: POLICIES.filter(p => p.status === 'published').length },
          { id: 'draft',     label: 'Drafts',    count: POLICIES.filter(p => p.status === 'draft').length },
          { id: 'archived',  label: 'Archived',  count: POLICIES.filter(p => p.status === 'archived').length },
        ].map(c => (
          <button key={c.id} onClick={() => setFilter(c.id)} style={{
            padding: '6px 12px', borderRadius: 16, border: `1px solid ${filter === c.id ? '#1A2E4B' : '#D5DBDB'}`,
            background: filter === c.id ? '#1A2E4B' : '#fff', color: filter === c.id ? '#fff' : '#1A2024',
            fontSize: 12, fontWeight: 500, cursor: 'pointer', fontFamily: "'Inter', sans-serif",
            display: 'inline-flex', alignItems: 'center', gap: 6,
          }}>{c.label} <span style={{ fontSize: 10, opacity: 0.7, fontFamily: "'JetBrains Mono', monospace" }}>{c.count}</span></button>
        ))}
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(2, 1fr)', gap: 14 }}>
        {filtered.map(p => (
          <div key={p.id} onClick={() => onOpen({ ...SAMPLE_POLICY, name: p.name, description: p.description, id: p.id, version: p.version, status: p.status })}
            style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, padding: '18px 20px', cursor: 'pointer', transition: 'all 200ms cubic-bezier(0.4,0,0.2,1)' }}
            onMouseEnter={e => { e.currentTarget.style.transform = 'translateY(-2px)'; e.currentTarget.style.boxShadow = '0 8px 16px -4px rgba(0,28,36,0.10)'; e.currentTarget.style.borderColor = 'rgba(0,179,217,0.5)'; }}
            onMouseLeave={e => { e.currentTarget.style.transform = 'translateY(0)'; e.currentTarget.style.boxShadow = 'none'; e.currentTarget.style.borderColor = '#D5DBDB'; }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 8 }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
                <div style={{ width: 36, height: 36, borderRadius: 6, background: 'rgba(0,179,217,0.10)', display: 'inline-flex', alignItems: 'center', justifyContent: 'center' }}>
                  <PortalIcon name="Layers" size={18} color="#00B3D9" />
                </div>
                <div>
                  <div style={{ fontSize: 14, fontWeight: 600, color: '#1A2024' }}>{p.name}</div>
                  <div style={{ fontSize: 11, color: '#4F5B67', fontFamily: "'JetBrains Mono', monospace", marginTop: 2 }}>{p.id} · v{p.version}</div>
                </div>
              </div>
              <PortalBadge variant={STATUS_TONES[p.status]}>{p.status === 'published' ? 'Published' : p.status === 'draft' ? 'Draft' : 'Archived'}</PortalBadge>
            </div>
            <div style={{ fontSize: 12, color: '#4F5B67', lineHeight: 1.55, marginBottom: 12, minHeight: 38 }}>{p.description}</div>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', paddingTop: 10, borderTop: '1px solid #f1f5f9', fontSize: 11, color: '#4F5B67' }}>
              <span>{p.steps} steps · last run {p.lastRun}</span>
              <span style={{ fontFamily: "'JetBrains Mono', monospace" }}>{p.runs.toLocaleString()} runs</span>
            </div>
            {p.decision !== '—' && (
              <div style={{ marginTop: 8, fontSize: 11, color: '#4F5B67' }}><b style={{ color: '#1A2024' }}>Decisions:</b> {p.decision}</div>
            )}
          </div>
        ))}
      </div>
    </div>
  );
}

/* ──────────────────────────────────────────────────────────
   Policy Builder — edit canvas
   Supports two layouts:
     layout="flow" → horizontal flow with cards (default canonical)
     layout="form" → form-style stacked editor (alternative)
   ──────────────────────────────────────────────────────── */

const AVAILABLE_SERVICES = [
  'ID Verification (DHA)', 'Sanctions & PEP', 'Credit Bureau (TransUnion)', 'Biometric liveness',
  'Document Verification', 'Criminal Record (SAPS)', 'Address Verification', 'Negative News',
];

function PolicyEditor({ policy, onBack, layout = 'flow' }) {
  const [p, setP] = useStateP(policy);
  const [selected, setSelected] = useStateP(null);
  const total = p.steps.reduce((sum, s) => sum + s.weight, 0);

  const updateStep = (id, partial) => setP({ ...p, steps: p.steps.map(s => s.id === id ? { ...s, ...partial } : s) });
  const removeStep = (id) => setP({ ...p, steps: p.steps.filter(s => s.id !== id) });
  const addStep = () => {
    const newStep = { id: `step_${Date.now()}`, kind: 'verification', service: AVAILABLE_SERVICES[0], weight: Math.max(0, 100 - total), required: false, threshold: 0.75 };
    setP({ ...p, steps: [...p.steps, newStep] });
    setSelected(newStep.id);
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 14, height: '100%' }} data-screen-label="Policy Editor">
      {/* Header */}
      <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, overflow: 'hidden' }}>
        <div style={{ display: 'flex', height: 3 }}>
          <div style={{ flex: 3, background: '#E23D36' }} />
          <div style={{ flex: 5, background: '#1A2E4B' }} />
          <div style={{ flex: 2, background: '#00B3D9' }} />
        </div>
        <div style={{ padding: '16px 22px', display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
          <div style={{ flex: 1 }}>
            <button onClick={onBack} style={{ background: 'transparent', border: 'none', color: '#00B3D9', fontSize: 11, fontWeight: 500, cursor: 'pointer', padding: 0, fontFamily: "'Inter', sans-serif", marginBottom: 6 }}>← All policies</button>
            <input value={p.name} onChange={e => setP({ ...p, name: e.target.value })}
              style={{ display: 'block', fontSize: 22, fontWeight: 600, color: '#1A2024', border: 'none', outline: 'none', background: 'transparent', width: '100%', padding: 0, fontFamily: "'Inter', sans-serif", letterSpacing: '-0.01em' }} />
            <input value={p.description} onChange={e => setP({ ...p, description: e.target.value })}
              style={{ display: 'block', fontSize: 12, color: '#4F5B67', border: 'none', outline: 'none', background: 'transparent', width: '100%', padding: 0, marginTop: 4, fontFamily: "'Inter', sans-serif" }} />
            <div style={{ display: 'flex', gap: 8, alignItems: 'center', marginTop: 8 }}>
              <span style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: 11, color: '#4F5B67' }}>{p.id} · v{p.version}</span>
              <PortalBadge variant={STATUS_TONES[p.status]}>{p.status === 'published' ? 'Published' : 'Draft'}</PortalBadge>
            </div>
          </div>
          <div style={{ display: 'flex', gap: 8 }}>
            <PortalButton variant="ghost">Test policy</PortalButton>
            <PortalButton variant="secondary">Save draft</PortalButton>
            <PortalButton variant="cta">Publish v{p.version + 1} →</PortalButton>
          </div>
        </div>
      </div>

      {/* Two-column editor */}
      <div style={{ display: 'grid', gridTemplateColumns: layout === 'flow' ? '1fr 340px' : '1fr 1fr', gap: 14, alignItems: 'flex-start' }}>
        {/* CANVAS */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
          <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, overflow: 'hidden' }}>
            <div style={{ padding: '12px 18px', borderBottom: '1px solid #e9ebed', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <div>
                <div style={{ fontSize: 13, fontWeight: 600 }}>Verification steps</div>
                <div style={{ fontSize: 11, color: '#4F5B67', marginTop: 2 }}>{p.steps.length} step(s) · total weight {total}/100</div>
              </div>
              <PortalButton variant="secondary" size="sm" icon={<PortalIcon name="Plus" size={11} color="#1A2E4B" />} onClick={addStep}>Add step</PortalButton>
            </div>

            {layout === 'flow' ? (
              /* Flow layout — horizontal cards with arrows */
              <div style={{ padding: 18, overflowX: 'auto' }}>
                <div style={{ display: 'flex', gap: 6, alignItems: 'center', minWidth: 'fit-content' }}>
                  <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 6 }}>
                    <div style={{ width: 36, height: 36, borderRadius: 999, background: '#1A2E4B', color: '#fff', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: 11, fontWeight: 600 }}>IN</div>
                    <span style={{ fontSize: 10, color: '#4F5B67' }}>Subject</span>
                  </div>
                  {p.steps.map((s, i) => (
                    <React.Fragment key={s.id}>
                      <PortalIcon name="ChevronRight" size={14} color="#CBD5E1" />
                      <button onClick={() => setSelected(s.id)} style={{
                        background: selected === s.id ? 'rgba(0,179,217,0.08)' : '#F8FAFC',
                        border: `1px solid ${selected === s.id ? '#00B3D9' : '#D5DBDB'}`,
                        borderRadius: 6, padding: '12px 14px', minWidth: 170, textAlign: 'left', cursor: 'pointer',
                        fontFamily: "'Inter', sans-serif",
                        transition: 'all 100ms cubic-bezier(0.4,0,0.2,1)',
                      }}>
                        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 6 }}>
                          <span style={{ fontSize: 9, fontWeight: 700, color: '#4F5B67', textTransform: 'uppercase', letterSpacing: '0.06em' }}>Step {i + 1}</span>
                          {s.required && <span style={{ fontSize: 9, color: '#E23D36', fontWeight: 600 }}>REQUIRED</span>}
                        </div>
                        <div style={{ fontSize: 12, fontWeight: 600, color: '#1A2024' }}>{s.service}</div>
                        <div style={{ marginTop: 6, display: 'flex', alignItems: 'center', gap: 6, fontSize: 10, color: '#4F5B67' }}>
                          <span style={{ fontFamily: "'JetBrains Mono', monospace" }}>w{s.weight}</span>
                          <span>·</span>
                          <span style={{ fontFamily: "'JetBrains Mono', monospace" }}>t{s.threshold.toFixed(2)}</span>
                        </div>
                      </button>
                    </React.Fragment>
                  ))}
                  <PortalIcon name="ChevronRight" size={14} color="#CBD5E1" />
                  <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 6 }}>
                    <div style={{ width: 36, height: 36, borderRadius: 999, background: '#2C974B', color: '#fff', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: 11, fontWeight: 600 }}>OUT</div>
                    <span style={{ fontSize: 10, color: '#4F5B67' }}>Decision</span>
                  </div>
                  <button onClick={addStep} style={{ marginLeft: 8, padding: '12px 14px', background: '#fff', border: '1px dashed #CBD5E1', borderRadius: 6, cursor: 'pointer', color: '#00B3D9', fontSize: 11, fontWeight: 500, fontFamily: "'Inter', sans-serif" }}>+ Step</button>
                </div>
              </div>
            ) : (
              /* Form layout — stacked rows */
              <div style={{ padding: 0 }}>
                {p.steps.map((s, i) => (
                  <div key={s.id} onClick={() => setSelected(s.id)}
                    style={{ display: 'flex', alignItems: 'center', gap: 10, padding: '12px 18px', borderTop: i > 0 ? '1px solid #f1f5f9' : 'none', background: selected === s.id ? 'rgba(0,179,217,0.04)' : 'transparent', cursor: 'pointer' }}>
                    <span style={{ width: 24, height: 24, borderRadius: 4, background: '#F2F3F3', color: '#4F5B67', fontSize: 11, fontWeight: 600, display: 'inline-flex', alignItems: 'center', justifyContent: 'center', fontFamily: "'JetBrains Mono', monospace" }}>{i + 1}</span>
                    <div style={{ flex: 1 }}>
                      <div style={{ fontSize: 13, fontWeight: 600 }}>{s.service}</div>
                      <div style={{ fontSize: 11, color: '#4F5B67', marginTop: 2, display: 'flex', gap: 12 }}>
                        <span>Weight <b style={{ color: '#1A2024', fontFamily: "'JetBrains Mono', monospace" }}>{s.weight}</b></span>
                        <span>Threshold <b style={{ color: '#1A2024', fontFamily: "'JetBrains Mono', monospace" }}>{s.threshold.toFixed(2)}</b></span>
                        {s.required && <span style={{ color: '#E23D36', fontWeight: 600 }}>Required</span>}
                      </div>
                    </div>
                    <button onClick={(e) => { e.stopPropagation(); removeStep(s.id); }} style={{ background: 'transparent', border: 'none', color: '#E23D36', cursor: 'pointer', fontSize: 16, padding: 4 }}>×</button>
                  </div>
                ))}
              </div>
            )}
          </div>

          {/* Scoring tiers */}
          <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, overflow: 'hidden' }}>
            <div style={{ padding: '12px 18px', borderBottom: '1px solid #e9ebed' }}>
              <div style={{ fontSize: 13, fontWeight: 600 }}>Scoring &amp; decisions</div>
              <div style={{ fontSize: 11, color: '#4F5B67', marginTop: 2 }}>Strategy: <b>{p.scoringConfig.strategy.replace('_', ' ').toLowerCase()}</b></div>
            </div>
            <div style={{ padding: '14px 18px', display: 'flex', flexDirection: 'column', gap: 10 }}>
              {/* Tier visualisation */}
              <div style={{ position: 'relative', height: 28, background: 'linear-gradient(to right, #E23D36 0% 49%, #C28B0B 49% 79%, #2C974B 79% 100%)', borderRadius: 4, overflow: 'hidden' }}>
                {[49, 79].map(b => <div key={b} style={{ position: 'absolute', left: b + '%', top: 0, bottom: 0, width: 2, background: '#fff', opacity: 0.6 }} />)}
                {p.scoringConfig.tiers.map(t => (
                  <div key={t.name} style={{ position: 'absolute', left: t.lowerBound + '%', top: 4, bottom: 4, width: (t.upperBound - t.lowerBound) + '%', display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#fff', fontSize: 10, fontWeight: 600, textShadow: '0 1px 2px rgba(0,0,0,0.2)' }}>
                    {t.decision}
                  </div>
                ))}
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: 10, color: '#4F5B67', fontFamily: "'JetBrains Mono', monospace" }}>
                <span>0</span><span>50</span><span>100</span>
              </div>
              {p.scoringConfig.tiers.map(t => (
                <div key={t.name} style={{ display: 'flex', alignItems: 'center', gap: 12, padding: '6px 0', borderTop: '1px solid #f1f5f9', fontSize: 12 }}>
                  <span style={{ width: 8, height: 8, borderRadius: 999, background: t.color }} />
                  <span style={{ flex: 1, fontWeight: 500 }}>{t.name.replace('_', ' ')}</span>
                  <span style={{ fontFamily: "'JetBrains Mono', monospace", color: '#4F5B67' }}>{t.lowerBound}–{t.upperBound}</span>
                  <PortalBadge variant={t.decision === 'APPROVE' ? 'success' : t.decision === 'REJECT' ? 'danger' : 'warning'} size="sm">{t.decision}</PortalBadge>
                </div>
              ))}
            </div>
          </div>

          {/* Override rules */}
          <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, overflow: 'hidden' }}>
            <div style={{ padding: '12px 18px', borderBottom: '1px solid #e9ebed', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <div style={{ fontSize: 13, fontWeight: 600 }}>Override rules</div>
              <PortalButton variant="link" size="sm">+ Add rule</PortalButton>
            </div>
            <div style={{ padding: '6px 0' }}>
              {p.scoringConfig.overrideRules.map(r => (
                <div key={r.id} style={{ display: 'flex', alignItems: 'center', gap: 12, padding: '8px 18px', borderTop: '1px solid #f1f5f9', fontSize: 12 }}>
                  <span style={{ fontSize: 10, padding: '2px 6px', background: '#F2F3F3', borderRadius: 3, fontFamily: "'JetBrains Mono', monospace", color: '#4F5B67' }}>WHEN</span>
                  <code style={{ flex: 1, fontFamily: "'JetBrains Mono', monospace", color: '#1A2024', background: 'transparent', padding: 0 }}>{r.when}</code>
                  <span style={{ fontSize: 10, padding: '2px 6px', background: '#F2F3F3', borderRadius: 3, fontFamily: "'JetBrains Mono', monospace", color: '#4F5B67' }}>THEN</span>
                  <PortalBadge variant="danger" size="sm">{r.decision}</PortalBadge>
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* INSPECTOR */}
        <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, padding: '14px 18px', position: 'sticky', top: 16, alignSelf: 'flex-start' }}>
          <div style={{ fontSize: 11, color: '#4F5B67', textTransform: 'uppercase', letterSpacing: '0.06em', fontWeight: 600, marginBottom: 10 }}>{selected ? 'Step inspector' : 'Quick stats'}</div>
          {selected ? (() => {
            const s = p.steps.find(x => x.id === selected);
            if (!s) return null;
            return (
              <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
                <div>
                  <label style={{ display: 'block', fontSize: 11, fontWeight: 500, color: '#1A2024', marginBottom: 4 }}>Service</label>
                  <select value={s.service} onChange={e => updateStep(s.id, { service: e.target.value })} style={{ width: '100%', padding: '7px 10px', border: '1px solid #D5DBDB', borderRadius: 4, fontSize: 12, fontFamily: "'Inter', sans-serif", background: '#fff' }}>
                    {AVAILABLE_SERVICES.map(x => <option key={x}>{x}</option>)}
                  </select>
                </div>
                <div>
                  <label style={{ display: 'flex', justifyContent: 'space-between', fontSize: 11, fontWeight: 500, color: '#1A2024', marginBottom: 4 }}>
                    Weight <span style={{ fontFamily: "'JetBrains Mono', monospace", color: '#4F5B67' }}>{s.weight}</span>
                  </label>
                  <input type="range" min={0} max={50} value={s.weight} onChange={e => updateStep(s.id, { weight: parseInt(e.target.value) })} style={{ width: '100%', accentColor: '#00B3D9' }} />
                </div>
                <div>
                  <label style={{ display: 'flex', justifyContent: 'space-between', fontSize: 11, fontWeight: 500, color: '#1A2024', marginBottom: 4 }}>
                    Threshold <span style={{ fontFamily: "'JetBrains Mono', monospace", color: '#4F5B67' }}>{s.threshold.toFixed(2)}</span>
                  </label>
                  <input type="range" min={0} max={1} step={0.01} value={s.threshold} onChange={e => updateStep(s.id, { threshold: parseFloat(e.target.value) })} style={{ width: '100%', accentColor: '#00B3D9' }} />
                </div>
                <label style={{ display: 'flex', alignItems: 'center', gap: 6, fontSize: 12, cursor: 'pointer' }}>
                  <input type="checkbox" checked={s.required} onChange={e => updateStep(s.id, { required: e.target.checked })} style={{ accentColor: '#00B3D9' }} />
                  Required step (a fail here rejects regardless of total score)
                </label>
                <PortalButton variant="ghost" size="sm" style={{ color: '#E23D36' }} onClick={() => { removeStep(s.id); setSelected(null); }}>Remove step</PortalButton>
              </div>
            );
          })() : (
            <div style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
              {[
                { label: 'Steps', value: p.steps.length },
                { label: 'Total weight', value: total + '/100', warn: total !== 100 },
                { label: 'Required steps', value: p.steps.filter(s => s.required).length },
                { label: 'Tiers', value: p.scoringConfig.tiers.length },
                { label: 'Override rules', value: p.scoringConfig.overrideRules.length },
              ].map(s => (
                <div key={s.label} style={{ display: 'flex', justifyContent: 'space-between', padding: '6px 0', borderBottom: '1px solid #f1f5f9', fontSize: 12 }}>
                  <span style={{ color: '#4F5B67' }}>{s.label}</span>
                  <span style={{ color: s.warn ? '#E23D36' : '#1A2024', fontWeight: 600, fontFamily: "'JetBrains Mono', monospace" }}>{s.value}</span>
                </div>
              ))}
              <div style={{ marginTop: 6, padding: '10px 12px', background: '#F8FAFC', borderRadius: 4, fontSize: 11, color: '#4F5B67', lineHeight: 1.5 }}>
                <b style={{ color: '#1A2E4B' }}>Tip:</b> click any step in the canvas to edit weights, thresholds, and required behaviour. Total weight should sum to 100 for weighted-average scoring.
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

window.PoliciesList = PoliciesList;
window.PolicyEditor = PolicyEditor;
window.SAMPLE_POLICY = SAMPLE_POLICY;
