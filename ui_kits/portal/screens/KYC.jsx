/* global React, PortalIcon, PortalButton, PortalBadge */
const { useState: useStateK } = React;

const FUNNEL = [
  { id: 'init',     label: 'Initiated',         count: 412, color: '#1A2E4B', icon: 'Plus' },
  { id: 'id',       label: 'ID document',       count: 388, color: '#1A2E4B', icon: 'FileSearch' },
  { id: 'live',     label: 'Liveness & selfie', count: 361, color: '#00B3D9', icon: 'UserCheck' },
  { id: 'dha',      label: 'DHA confirmed',     count: 348, color: '#00B3D9', icon: 'Shield' },
  { id: 'verified', label: 'Verified',          count: 332, color: '#2C974B', icon: 'CheckCircle' },
];

const PHONE_STEPS = [
  { n: 1, title: 'Verify your identity',     sub: 'We need to confirm who you are. This takes about 2 minutes.', cta: 'Get started', dotIdx: 0 },
  { n: 2, title: 'Capture your ID',           sub: 'Hold your green ID book or smart ID card flat in good light.', cta: 'Capture',     dotIdx: 1 },
  { n: 3, title: 'Take a quick selfie',       sub: 'Look at the camera. We use this to confirm the ID is yours.',    cta: 'Take selfie', dotIdx: 2 },
  { n: 4, title: 'Verified.',                 sub: 'Thanks — your identity has been verified with the Department of Home Affairs.', cta: 'Done', dotIdx: 3, done: true },
];

function PhoneFrame({ children, step, onPrev, onNext }) {
  return (
    <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 10 }}>
      {/* iPhone-ish bezel */}
      <div style={{ width: 260, height: 510, background: '#1A1A2E', borderRadius: 36, padding: 10, boxShadow: '0 20px 40px -10px rgba(0,28,36,0.4), inset 0 0 0 2px rgba(255,255,255,0.06)' }}>
        <div style={{ width: '100%', height: '100%', borderRadius: 28, background: '#fff', overflow: 'hidden', position: 'relative', display: 'flex', flexDirection: 'column' }}>
          {/* notch */}
          <div style={{ position: 'absolute', top: 0, left: '50%', transform: 'translateX(-50%)', width: 90, height: 22, background: '#1A1A2E', borderRadius: '0 0 14px 14px', zIndex: 2 }} />
          {/* status bar */}
          <div style={{ height: 32, display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '6px 22px 0', fontSize: 10, fontWeight: 600, color: '#1A2024' }}>
            <span>9:41</span>
            <span></span>
          </div>
          {children}
        </div>
      </div>
      <div style={{ display: 'flex', gap: 8, alignItems: 'center' }}>
        <button onClick={onPrev} disabled={step === 0} style={{ width: 28, height: 28, borderRadius: 999, border: '1px solid #D5DBDB', background: '#fff', color: '#1A2E4B', cursor: step === 0 ? 'not-allowed' : 'pointer', opacity: step === 0 ? 0.4 : 1 }}>‹</button>
        <span style={{ fontSize: 11, color: '#4F5B67', minWidth: 70, textAlign: 'center' }}>Step {step + 1} of {PHONE_STEPS.length}</span>
        <button onClick={onNext} disabled={step === PHONE_STEPS.length - 1} style={{ width: 28, height: 28, borderRadius: 999, border: '1px solid #D5DBDB', background: '#fff', color: '#1A2E4B', cursor: step === PHONE_STEPS.length - 1 ? 'not-allowed' : 'pointer', opacity: step === PHONE_STEPS.length - 1 ? 0.4 : 1 }}>›</button>
      </div>
    </div>
  );
}

function PhoneScreen({ step }) {
  const s = PHONE_STEPS[step];
  return (
    <div style={{ flex: 1, display: 'flex', flexDirection: 'column', padding: '24px 18px 18px' }}>
      {/* Brand row */}
      <div style={{ display: 'flex', alignItems: 'center', gap: 6, marginBottom: 16 }}>
        <svg width="16" height="18" viewBox="20 25 112 122"><path fill="#E23D36" d="M76 30 C56 30 26 39 26 42 L26 74 C26 106 50 132 76 142 C102 132 126 106 126 74 L126 42 C126 39 96 30 76 30 Z"/><path d="M46 84 L63 102 L106 58" fill="none" stroke="#FFFFFF" strokeWidth="13" strokeLinecap="round" strokeLinejoin="round"/></svg>
        <span style={{ fontFamily: "'Manrope', sans-serif", fontWeight: 500, fontSize: 12, color: '#1A2E4B', letterSpacing: '-0.4px' }}>VeriGate</span>
      </div>

      {/* Step content */}
      <div style={{ flex: 1, display: 'flex', flexDirection: 'column' }}>
        {/* Illustration area */}
        <div style={{ height: 150, background: s.done ? 'linear-gradient(135deg, rgba(44,151,75,0.10), rgba(44,151,75,0.02))' : 'rgba(0,179,217,0.06)', borderRadius: 12, marginBottom: 16, display: 'flex', alignItems: 'center', justifyContent: 'center', position: 'relative', overflow: 'hidden' }}>
          {step === 0 && <PortalIcon name="Shield" size={56} color="#00B3D9" stroke={1.5} />}
          {step === 1 && (
            <div style={{ width: 150, height: 92, background: '#1A2E4B', borderRadius: 8, position: 'relative', overflow: 'hidden', display: 'flex', flexDirection: 'column', padding: 8 }}>
              <div style={{ display: 'flex', gap: 6, alignItems: 'center', marginBottom: 4 }}>
                <span style={{ fontSize: 6, color: '#fff', fontWeight: 700, letterSpacing: '0.04em' }}>REPUBLIC OF SOUTH AFRICA</span>
              </div>
              <div style={{ display: 'flex', gap: 6, flex: 1 }}>
                <div style={{ width: 36, height: 44, background: 'rgba(255,255,255,0.12)', borderRadius: 3 }} />
                <div style={{ flex: 1, display: 'flex', flexDirection: 'column', gap: 3 }}>
                  <div style={{ height: 4, width: '70%', background: 'rgba(255,255,255,0.5)', borderRadius: 2 }} />
                  <div style={{ height: 4, width: '50%', background: 'rgba(255,255,255,0.3)', borderRadius: 2 }} />
                  <div style={{ height: 4, width: '60%', background: 'rgba(255,255,255,0.3)', borderRadius: 2 }} />
                  <div style={{ height: 4, width: '80%', background: 'rgba(255,255,255,0.3)', borderRadius: 2 }} />
                </div>
              </div>
              <span style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: 7, color: '#00B3D9', marginTop: 4 }}>9001015800087</span>
              {/* scanner sweep */}
              <div style={{ position: 'absolute', left: 0, right: 0, top: 40, height: 2, background: 'linear-gradient(90deg, transparent, #00B3D9, transparent)', boxShadow: '0 0 8px #00B3D9' }} />
            </div>
          )}
          {step === 2 && (
            <div style={{ position: 'relative', width: 100, height: 100, borderRadius: 999, background: '#F2F3F3', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
              <PortalIcon name="User" size={48} color="#4F5B67" stroke={1.5} />
              {/* face guide ring */}
              <div style={{ position: 'absolute', inset: -6, borderRadius: 999, border: '2px dashed #00B3D9', animation: 'spin 8s linear infinite' }} />
            </div>
          )}
          {step === 3 && (
            <div style={{ width: 72, height: 72, borderRadius: 999, background: '#2C974B', display: 'flex', alignItems: 'center', justifyContent: 'center', boxShadow: '0 0 30px rgba(44,151,75,0.5)' }}>
              <PortalIcon name="CheckCircle" size={40} color="#fff" stroke={2.5} />
            </div>
          )}
        </div>

        <div style={{ fontSize: 16, fontWeight: 600, color: '#1A2024', marginBottom: 6, letterSpacing: '-0.01em' }}>{s.title}</div>
        <div style={{ fontSize: 11, color: '#4F5B67', lineHeight: 1.55, marginBottom: 12 }}>{s.sub}</div>

        {step === 3 && (
          <div style={{ background: 'rgba(44,151,75,0.08)', border: '1px solid rgba(44,151,75,0.2)', borderRadius: 6, padding: 8, marginBottom: 12 }}>
            <div style={{ fontSize: 9, color: '#4F5B67', textTransform: 'uppercase', letterSpacing: '0.06em', fontWeight: 600 }}>Reference</div>
            <div style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: 10, color: '#1A2024', marginTop: 2 }}>VG-2026-0143</div>
          </div>
        )}

        {/* progress dots */}
        <div style={{ display: 'flex', gap: 4, justifyContent: 'center', marginBottom: 14 }}>
          {PHONE_STEPS.map((_, i) => (
            <span key={i} style={{ width: i === s.dotIdx ? 16 : 6, height: 6, borderRadius: 3, background: i <= s.dotIdx ? '#00B3D9' : '#E2E8F0', transition: 'all 200ms cubic-bezier(0.4,0,0.2,1)' }} />
          ))}
        </div>

        <button style={{ width: '100%', padding: '11px 0', background: s.done ? '#2C974B' : '#1A2E4B', color: '#fff', border: 'none', borderRadius: 8, fontSize: 13, fontWeight: 600, fontFamily: "'Inter', sans-serif", cursor: 'pointer' }}>{s.cta}</button>
      </div>
    </div>
  );
}

function KYC({ rows, onOpenDetail, onStartNew }) {
  const [phoneStep, setPhoneStep] = useStateK(1);
  const [biometricStrict, setBiometricStrict] = useStateK(true);
  const [retryEnabled, setRetryEnabled] = useStateK(true);

  const kycRows = (rows || []).filter(r => r.type === 'KYC').slice(0, 5);

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }} data-screen-label="KYC">
      <style>{`@keyframes spin{to{transform:rotate(360deg)}}`}</style>

      {/* Header */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-end' }}>
        <div>
          <div style={{ fontSize: 11, color: '#4F5B67', textTransform: 'uppercase', letterSpacing: '0.08em', fontWeight: 600 }}>Identity & Personal</div>
          <h1 style={{ fontSize: 26, fontWeight: 600, color: '#1A2024', marginTop: 4, letterSpacing: '-0.01em' }}>KYC Verification</h1>
          <div style={{ fontSize: 13, color: '#4F5B67', marginTop: 4 }}>Automated identity verification with document scanning, liveness detection, and biometric matching against DHA records.</div>
        </div>
        <div style={{ display: 'flex', gap: 8 }}>
          <PortalButton variant="secondary" icon={<PortalIcon name="Download" size={13} color="#1A2E4B" />}>KYC report</PortalButton>
          <PortalButton variant="cta" icon={<PortalIcon name="Plus" size={13} color="#fff" />} onClick={onStartNew}>Start KYC →</PortalButton>
        </div>
      </div>

      {/* KPIs */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: 14 }}>
        {[
          { label: 'KYC checks this week', value: '412',     sub: '+18% WoW',   tone: '#2C974B' },
          { label: 'Pass rate',            value: '94.2%',   sub: 'Industry avg 91%', tone: '#2C974B' },
          { label: 'Avg. time to verify',  value: '2.4m',    sub: 'P95 under 9m', tone: '#00B3D9' },
          { label: 'Drop-off in funnel',   value: '19.4%',   sub: '−2.1pt vs last week', tone: '#2C974B' },
        ].map(s => (
          <div key={s.label} style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, padding: '14px 16px' }}>
            <div style={{ fontSize: 10, color: '#4F5B67', textTransform: 'uppercase', letterSpacing: '0.06em', fontWeight: 600 }}>{s.label}</div>
            <div style={{ fontSize: 26, fontWeight: 700, color: '#1A2024', marginTop: 4, lineHeight: 1.1 }}>{s.value}</div>
            <div style={{ fontSize: 11, color: s.tone, fontWeight: 600, marginTop: 4 }}>{s.sub}</div>
          </div>
        ))}
      </div>

      {/* Funnel + Phone preview */}
      <div style={{ display: 'grid', gridTemplateColumns: '1.6fr 1fr', gap: 16, alignItems: 'stretch' }}>
        {/* FUNNEL */}
        <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, overflow: 'hidden', display: 'flex', flexDirection: 'column' }}>
          <div style={{ padding: '14px 18px', borderBottom: '1px solid #e9ebed', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <div>
              <div style={{ fontSize: 14, fontWeight: 600 }}>KYC funnel</div>
              <div style={{ fontSize: 11, color: '#4F5B67', marginTop: 2 }}>This week · 412 initiated → 332 verified · 80.6% completion</div>
            </div>
            <PortalButton variant="link">View raw events →</PortalButton>
          </div>
          <div style={{ padding: '22px 18px', flex: 1, display: 'flex', flexDirection: 'column', gap: 10 }}>
            {FUNNEL.map((stage, i) => {
              const pct = (stage.count / FUNNEL[0].count) * 100;
              const drop = i > 0 ? (((FUNNEL[i-1].count - stage.count) / FUNNEL[i-1].count) * 100).toFixed(1) : null;
              return (
                <div key={stage.id}>
                  {drop && parseFloat(drop) > 0 && (
                    <div style={{ display: 'flex', alignItems: 'center', gap: 6, fontSize: 10, color: '#E23D36', fontWeight: 600, marginLeft: 30, marginBottom: 4 }}>
                      <span style={{ width: 1, height: 8, background: '#E2E8F0' }} />
                      <span>↓ {drop}% drop-off</span>
                    </div>
                  )}
                  <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
                    <div style={{ width: 28, height: 28, borderRadius: 6, background: `${stage.color}15`, display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0 }}>
                      <PortalIcon name={stage.icon} size={14} color={stage.color} />
                    </div>
                    <div style={{ flex: 1, position: 'relative', height: 30 }}>
                      <div style={{ position: 'absolute', inset: 0, background: '#F8FAFC', borderRadius: 4 }} />
                      <div style={{ position: 'absolute', top: 0, bottom: 0, left: 0, width: `${pct}%`, background: stage.color, borderRadius: 4, opacity: 0.92, transition: 'width 400ms cubic-bezier(0.4,0,0.2,1)' }} />
                      <div style={{ position: 'relative', height: '100%', display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '0 12px', color: pct > 35 ? '#fff' : '#1A2024', fontSize: 12, fontWeight: 600 }}>
                        <span>{stage.label}</span>
                        <span style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: 11, fontWeight: 700, color: '#1A2E4B' }}>{stage.count}</span>
                      </div>
                    </div>
                    <span style={{ width: 48, textAlign: 'right', fontSize: 11, color: '#4F5B67', fontFamily: "'JetBrains Mono', monospace" }}>{pct.toFixed(1)}%</span>
                  </div>
                </div>
              );
            })}
          </div>
        </div>

        {/* PHONE PREVIEW */}
        <div style={{ background: 'linear-gradient(160deg,#0F1A2E 0%,#1A2E4B 60%,#1a3a5c 100%)', borderRadius: 8, padding: '20px 16px', position: 'relative', overflow: 'hidden', display: 'flex', flexDirection: 'column' }}>
          <div style={{ position: 'absolute', inset: 0, backgroundImage: 'radial-gradient(circle, rgba(255,255,255,0.06) 1px, transparent 1px)', backgroundSize: '20px 20px' }} />
          <div style={{ position: 'relative', marginBottom: 14, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <div>
              <div style={{ fontSize: 10, color: '#00B3D9', textTransform: 'uppercase', letterSpacing: '0.12em', fontWeight: 600 }}>Subject preview</div>
              <div style={{ fontSize: 13, color: '#fff', fontWeight: 600, marginTop: 3 }}>What your customer sees</div>
            </div>
            <span style={{ fontSize: 10, color: 'rgba(255,255,255,0.6)', fontFamily: "'JetBrains Mono', monospace" }}>iOS Safari</span>
          </div>
          <div style={{ position: 'relative', flex: 1, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
            <PhoneFrame step={phoneStep} onPrev={() => setPhoneStep(Math.max(0, phoneStep - 1))} onNext={() => setPhoneStep(Math.min(PHONE_STEPS.length - 1, phoneStep + 1))}>
              <PhoneScreen step={phoneStep} />
            </PhoneFrame>
          </div>
        </div>
      </div>

      {/* Workflow config + recent KYC */}
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1.6fr', gap: 16 }}>
        {/* Config */}
        <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, overflow: 'hidden' }}>
          <div style={{ padding: '14px 18px', borderBottom: '1px solid #e9ebed' }}>
            <div style={{ fontSize: 14, fontWeight: 600 }}>Workflow configuration</div>
            <div style={{ fontSize: 11, color: '#4F5B67', marginTop: 2 }}>Default policy for new KYC checks</div>
          </div>
          <div style={{ padding: '6px 0' }}>
            {[
              { k: 'Verification sources', v: 'DHA · SAPS · TransUnion · CIPC' },
              { k: 'Required documents',   v: 'SA ID book or smart ID card' },
              { k: 'Biometric threshold',  v: biometricStrict ? '0.90 (strict)' : '0.78 (lenient)' },
              { k: 'Liveness model',       v: 'Active prompt-based (eye blink + head turn)' },
              { k: 'Retry policy',         v: retryEnabled ? '3 attempts · 5 min lockout' : 'Disabled — single attempt' },
              { k: 'Consent retention',    v: '7 years (POPIA-recommended)' },
            ].map(r => (
              <div key={r.k} style={{ padding: '8px 18px', borderBottom: '1px solid #f1f5f9', display: 'flex', justifyContent: 'space-between', gap: 12, fontSize: 12 }}>
                <span style={{ color: '#4F5B67' }}>{r.k}</span>
                <span style={{ color: '#1A2024', fontWeight: 500, textAlign: 'right' }}>{r.v}</span>
              </div>
            ))}
          </div>
          <div style={{ padding: '10px 18px', display: 'flex', gap: 6, flexWrap: 'wrap', background: '#F8FAFC' }}>
            <button onClick={() => setBiometricStrict(!biometricStrict)} style={{ padding: '4px 10px', fontSize: 11, borderRadius: 16, border: '1px solid #D5DBDB', background: '#fff', cursor: 'pointer', color: '#1A2024' }}>Toggle biometric strictness</button>
            <button onClick={() => setRetryEnabled(!retryEnabled)} style={{ padding: '4px 10px', fontSize: 11, borderRadius: 16, border: '1px solid #D5DBDB', background: '#fff', cursor: 'pointer', color: '#1A2024' }}>Toggle retries</button>
            <PortalButton variant="link" style={{ marginLeft: 'auto', fontSize: 11 }}>Edit workflow →</PortalButton>
          </div>
        </div>

        {/* Recent KYC */}
        <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, overflow: 'hidden' }}>
          <div style={{ padding: '14px 18px', borderBottom: '1px solid #e9ebed', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <div>
              <div style={{ fontSize: 14, fontWeight: 600 }}>Recent KYC verifications</div>
              <div style={{ fontSize: 11, color: '#4F5B67', marginTop: 2 }}>{kycRows.length} most recent</div>
            </div>
            <PortalButton variant="link">View all →</PortalButton>
          </div>
          <table style={{ width: '100%', borderCollapse: 'separate', borderSpacing: 0, fontSize: 12 }}>
            <thead style={{ background: '#F2F3F3' }}>
              <tr>{['ID', 'Subject', 'Status', 'Updated', ''].map(h => <th key={h} style={{ padding: '8px 14px', textAlign: 'left', fontSize: 10, color: '#4F5B67', textTransform: 'uppercase', letterSpacing: '0.05em', fontWeight: 600, borderBottom: '1px solid #e9ebed' }}>{h}</th>)}</tr>
            </thead>
            <tbody>
              {kycRows.map(r => (
                <tr key={r.id} onClick={() => onOpenDetail(r)} style={{ cursor: 'pointer', transition: 'background 100ms cubic-bezier(0.4,0,0.2,1)' }}
                    onMouseEnter={e => e.currentTarget.style.background = '#F8FAFC'}
                    onMouseLeave={e => e.currentTarget.style.background = 'transparent'}>
                  <td style={{ padding: '10px 14px', borderBottom: '1px solid #e9ebed', fontFamily: "'JetBrains Mono', monospace", fontSize: 11, color: '#00B3D9' }}>{r.id}</td>
                  <td style={{ padding: '10px 14px', borderBottom: '1px solid #e9ebed' }}>{r.subject}</td>
                  <td style={{ padding: '10px 14px', borderBottom: '1px solid #e9ebed' }}><PortalBadge variant={r.status}>{r.statusLabel}</PortalBadge></td>
                  <td style={{ padding: '10px 14px', borderBottom: '1px solid #e9ebed', color: '#4F5B67' }}>{r.updated}</td>
                  <td style={{ padding: '10px 14px', borderBottom: '1px solid #e9ebed', textAlign: 'right' }}>
                    <PortalIcon name="ChevronRight" size={12} color="#4F5B67" />
                  </td>
                </tr>
              ))}
              {!kycRows.length && (
                <tr><td colSpan={5} style={{ padding: 24, textAlign: 'center', color: '#4F5B67', fontSize: 12 }}>No KYC verifications yet — start your first.</td></tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}

window.KYC = KYC;
