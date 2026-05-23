/* global React, PortalIcon, PortalButton, PortalBadge */
const { useState: useStateW } = React;

const PRODUCTS = [
  { id: 'kyc',       label: 'KYC — Full Verification',  cost: 45,  desc: 'ID + DHA + sanctions + basic credit. Standard onboarding.' },
  { id: 'criminal',  label: 'Criminal Record (1hr)',    cost: 250, desc: 'SAPS-AFIS search with fingerprint capture. 1-hour SLA.' },
  { id: 'credit',    label: 'Credit Bureau',            cost: 85,  desc: 'TransUnion + Experian credit profile and adverse listing scan.' },
  { id: 'sanctions', label: 'Sanctions & PEP',          cost: 120, desc: 'OFAC, UN, EU, UK sanctions + politically exposed person register.' },
];

function WizardStep({ n, label, current }) {
  const done = current > n;
  const active = current === n;
  return (
    <div style={{ display: 'flex', alignItems: 'center', gap: 8, flex: 1 }}>
      <div style={{ width: 24, height: 24, borderRadius: 999, background: done ? '#2C974B' : active ? '#1A2E4B' : '#fff', border: `1px solid ${done ? '#2C974B' : active ? '#1A2E4B' : '#D5DBDB'}`, color: done || active ? '#fff' : '#4F5B67', fontSize: 11, fontWeight: 600, display: 'inline-flex', alignItems: 'center', justifyContent: 'center', fontFamily: "'Inter', sans-serif" }}>
        {done ? '✓' : n}
      </div>
      <span style={{ fontSize: 12, color: active ? '#1A2024' : '#4F5B67', fontWeight: active ? 600 : 400 }}>{label}</span>
    </div>
  );
}

function NewVerificationFlow({ open, onClose, onSubmit }) {
  const [step, setStep] = useStateW(1);
  const [product, setProduct] = useStateW('kyc');
  const [subject, setSubject] = useStateW({ first: '', last: '', idNum: '', dob: '' });
  const [biometric, setBiometric] = useStateW(false);
  const [consent, setConsent] = useStateW(false);

  if (!open) return null;

  const prod = PRODUCTS.find(p => p.id === product);
  const totalCost = prod.cost + (biometric ? 60 : 0);
  const canNext1 = !!product;
  const canNext2 = subject.first && subject.last && subject.idNum.length === 13;
  const canSubmit = consent;

  return (
    <div onClick={onClose} style={{ position: 'fixed', inset: 0, background: 'rgba(15,26,46,0.45)', display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 100, animation: 'fadeIn 200ms cubic-bezier(0.4,0,0.2,1)' }}>
      <div onClick={e => e.stopPropagation()} style={{ background: '#fff', borderRadius: 8, width: 600, maxWidth: 'calc(100vw - 32px)', boxShadow: '0 8px 16px 4px rgba(0,28,36,0.18)', border: '1px solid #D5DBDB', overflow: 'hidden' }}>
        {/* Tri-bar */}
        <div style={{ display: 'flex', height: 3 }}>
          <div style={{ flex: 3, background: '#E23D36' }} />
          <div style={{ flex: 5, background: '#1A2E4B' }} />
          <div style={{ flex: 2, background: '#00B3D9' }} />
        </div>

        {/* Header */}
        <div style={{ padding: '18px 24px 12px', display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
          <div>
            <div style={{ fontSize: 16, fontWeight: 600, color: '#1A2024' }}>Start a new verification</div>
            <div style={{ fontSize: 12, color: '#4F5B67', marginTop: 2 }}>Costs are billed to your current month invoice.</div>
          </div>
          <button onClick={onClose} style={{ background: 'transparent', border: 'none', cursor: 'pointer', fontSize: 20, color: '#4F5B67', lineHeight: 1 }}>×</button>
        </div>

        {/* Stepper */}
        <div style={{ padding: '8px 24px 14px', display: 'flex', gap: 12, alignItems: 'center', borderBottom: '1px solid #e9ebed' }}>
          <WizardStep n={1} label="Product" current={step} />
          <span style={{ width: 24, height: 1, background: '#D5DBDB' }} />
          <WizardStep n={2} label="Subject" current={step} />
          <span style={{ width: 24, height: 1, background: '#D5DBDB' }} />
          <WizardStep n={3} label="Consent & Review" current={step} />
        </div>

        {/* Body */}
        <div style={{ padding: '20px 24px', minHeight: 280 }}>
          {step === 1 && (
            <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
              <div style={{ fontSize: 13, fontWeight: 600, marginBottom: 4 }}>Choose a product</div>
              {PRODUCTS.map(p => (
                <button key={p.id} onClick={() => setProduct(p.id)} style={{
                  display: 'flex', alignItems: 'flex-start', gap: 12, padding: '12px 14px',
                  border: `1px solid ${product === p.id ? '#00B3D9' : '#D5DBDB'}`,
                  background: product === p.id ? 'rgba(0,179,217,0.05)' : '#fff',
                  borderRadius: 6, cursor: 'pointer', textAlign: 'left', fontFamily: "'Inter', sans-serif",
                  transition: 'all 100ms cubic-bezier(0.4,0,0.2,1)',
                }}>
                  <div style={{ width: 14, height: 14, borderRadius: 999, border: `2px solid ${product === p.id ? '#00B3D9' : '#CBD5E1'}`, background: product === p.id ? '#00B3D9' : 'transparent', marginTop: 3, flexShrink: 0, position: 'relative' }}>
                    {product === p.id && <span style={{ position: 'absolute', inset: 2, borderRadius: 999, background: '#fff' }} />}
                  </div>
                  <div style={{ flex: 1 }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'baseline' }}>
                      <span style={{ fontSize: 13, fontWeight: 600, color: '#1A2024' }}>{p.label}</span>
                      <span style={{ fontSize: 13, fontWeight: 600, color: '#1A2E4B', fontFamily: "'JetBrains Mono', monospace" }}>R {p.cost.toFixed(2)}</span>
                    </div>
                    <div style={{ fontSize: 11, color: '#4F5B67', marginTop: 3, lineHeight: 1.5 }}>{p.desc}</div>
                  </div>
                </button>
              ))}
              <label style={{ display: 'flex', alignItems: 'center', gap: 8, padding: '10px 14px', marginTop: 6, background: '#F8FAFC', borderRadius: 6, fontSize: 12, cursor: 'pointer' }}>
                <input type="checkbox" checked={biometric} onChange={e => setBiometric(e.target.checked)} style={{ accentColor: '#00B3D9' }} />
                <span style={{ flex: 1 }}>Include biometric capture (face liveness + selfie match)</span>
                <span style={{ fontFamily: "'JetBrains Mono', monospace", color: '#4F5B67' }}>+R 60.00</span>
              </label>
            </div>
          )}

          {step === 2 && (
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 14 }}>
              {[
                { k: 'first', label: 'First name *',     ph: 'John', },
                { k: 'last',  label: 'Last name *',      ph: 'Doe',  },
                { k: 'idNum', label: 'ID number *',      ph: '13-digit SA ID', mono: true, hint: 'Used to confirm against DHA records.' },
                { k: 'dob',   label: 'Date of birth',    ph: 'YYYY-MM-DD', },
              ].map(f => (
                <div key={f.k}>
                  <label style={{ display: 'block', fontSize: 11, fontWeight: 500, color: '#1A2024', marginBottom: 4 }}>{f.label}</label>
                  <input value={subject[f.k]} onChange={e => setSubject({ ...subject, [f.k]: e.target.value })} placeholder={f.ph}
                    style={{ width: '100%', padding: '8px 10px', border: '1px solid #D5DBDB', borderRadius: 4, fontSize: 13, fontFamily: f.mono ? "'JetBrains Mono', monospace" : "'Inter', sans-serif", outline: 'none' }}
                    onFocus={e => { e.currentTarget.style.borderColor = '#00B3D9'; e.currentTarget.style.boxShadow = '0 0 0 3px rgba(0,179,217,0.15)'; }}
                    onBlur={e => { e.currentTarget.style.borderColor = '#D5DBDB'; e.currentTarget.style.boxShadow = 'none'; }}
                  />
                  {f.hint && <div style={{ fontSize: 10, color: '#4F5B67', marginTop: 3 }}>{f.hint}</div>}
                </div>
              ))}
              <div style={{ gridColumn: '1 / -1', display: 'flex', gap: 10, padding: '12px 14px', background: 'rgba(0,179,217,0.05)', border: '1px solid rgba(0,179,217,0.2)', borderRadius: 6, fontSize: 12, color: '#1A2E4B', lineHeight: 1.5 }}>
                <PortalIcon name="Shield" size={16} color="#00B3D9" />
                <span><b>POPIA notice:</b> the subject must be informed and have given written or digital consent before submitting. You'll capture that in the next step.</span>
              </div>
            </div>
          )}

          {step === 3 && (
            <div style={{ display: 'flex', flexDirection: 'column', gap: 14 }}>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 14 }}>
                <div style={{ background: '#F8FAFC', border: '1px solid #e9ebed', borderRadius: 6, padding: '14px 16px' }}>
                  <div style={{ fontSize: 10, color: '#4F5B67', textTransform: 'uppercase', letterSpacing: '0.06em', fontWeight: 600, marginBottom: 8 }}>Subject</div>
                  <div style={{ fontSize: 14, fontWeight: 600 }}>{subject.first} {subject.last}</div>
                  <div style={{ fontSize: 12, color: '#4F5B67', fontFamily: "'JetBrains Mono', monospace", marginTop: 2 }}>{subject.idNum || '—'}</div>
                  <div style={{ fontSize: 11, color: '#4F5B67', marginTop: 2 }}>{subject.dob || 'No DOB'}</div>
                </div>
                <div style={{ background: '#F8FAFC', border: '1px solid #e9ebed', borderRadius: 6, padding: '14px 16px' }}>
                  <div style={{ fontSize: 10, color: '#4F5B67', textTransform: 'uppercase', letterSpacing: '0.06em', fontWeight: 600, marginBottom: 8 }}>Product</div>
                  <div style={{ fontSize: 14, fontWeight: 600 }}>{prod.label}</div>
                  {biometric && <div style={{ fontSize: 11, color: '#4F5B67', marginTop: 2 }}>+ Biometric capture</div>}
                  <div style={{ marginTop: 6, fontSize: 16, fontWeight: 700, color: '#E23D36', fontFamily: "'Inter', sans-serif" }}>R {totalCost.toFixed(2)}</div>
                </div>
              </div>
              <label style={{ display: 'flex', alignItems: 'flex-start', gap: 10, padding: '12px 14px', border: `1px solid ${consent ? '#00B3D9' : '#D5DBDB'}`, borderRadius: 6, cursor: 'pointer', fontSize: 12, lineHeight: 1.55, background: consent ? 'rgba(0,179,217,0.05)' : '#fff' }}>
                <input type="checkbox" checked={consent} onChange={e => setConsent(e.target.checked)} style={{ accentColor: '#00B3D9', marginTop: 2 }} />
                <span>I confirm the subject has provided POPIA-compliant consent for this verification, and that data will be processed per VeriGate's POPIA notice.</span>
              </label>
            </div>
          )}
        </div>

        {/* Footer */}
        <div style={{ padding: '14px 24px', borderTop: '1px solid #e9ebed', background: '#F8FAFC', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <div style={{ fontSize: 11, color: '#4F5B67' }}>Estimated cost · <b style={{ color: '#1A2024', fontFamily: "'JetBrains Mono', monospace" }}>R {totalCost.toFixed(2)}</b></div>
          <div style={{ display: 'flex', gap: 8 }}>
            <PortalButton variant="secondary" onClick={onClose}>Cancel</PortalButton>
            {step > 1 && <PortalButton variant="ghost" onClick={() => setStep(step - 1)}>← Back</PortalButton>}
            {step < 3 && <PortalButton variant="primary" onClick={() => setStep(step + 1)} disabled={(step === 1 && !canNext1) || (step === 2 && !canNext2)}>Next →</PortalButton>}
            {step === 3 && <PortalButton variant="cta" onClick={() => onSubmit({ product, subject, biometric, cost: totalCost })} disabled={!canSubmit}>Start verification →</PortalButton>}
          </div>
        </div>
      </div>
    </div>
  );
}

window.NewVerificationFlow = NewVerificationFlow;
