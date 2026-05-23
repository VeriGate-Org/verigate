/* global React, PortalIcon, PortalButton, PortalBadge */

function VerificationDetail({ row, onBack, onDelete }) {
  if (!row) return null;
  const r = row;

  // Simulate sub-check timeline per verification
  const checks = [
    { id: 'ID',       label: 'Department of Home Affairs',   provider: 'DHA',       at: '14:32:14', status: 'success', detail: 'ID 9001015800087 matched — South African citizen, status active.' },
    { id: 'SAPS',     label: 'SAPS criminal record',         provider: 'SAPS-AFIS', at: '14:32:48', status: r.status === 'danger' ? 'danger' : 'success', detail: r.status === 'danger' ? 'Conviction record returned — 2018-04 (Fraud, Section 3 PRECCA).' : 'No criminal record on file.' },
    { id: 'CREDIT',   label: 'Credit bureau check',          provider: 'TransUnion',at: '14:33:02', status: r.status === 'warning' ? 'warning' : 'success', detail: r.status === 'warning' ? 'Adverse listing within last 12 months — manual review required.' : 'Credit score 712 · No adverse listings.' },
    { id: 'SANCTIONS',label: 'Sanctions & PEP screening',    provider: 'OFAC + EU', at: '14:33:15', status: 'success', detail: 'No matches against UN, OFAC, EU, UK sanctions lists.' },
    { id: 'BIOMETRIC',label: 'Biometric liveness',           provider: 'Internal',  at: '14:33:42', status: 'success', detail: 'Liveness score 0.97 · Document-to-selfie match 0.94.' },
  ];

  const tone = { success: '#2C974B', warning: '#C28B0B', danger: '#E23D36', info: '#00B3D9', pending: '#4F5B67' };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
      <button onClick={onBack} style={{ alignSelf: 'flex-start', background: 'transparent', border: 'none', color: '#00B3D9', fontSize: 12, fontWeight: 500, cursor: 'pointer', display: 'inline-flex', alignItems: 'center', gap: 4, padding: 0, fontFamily: "'Inter', sans-serif" }}>
        ← Back to verifications
      </button>

      {/* Header */}
      <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, overflow: 'hidden' }}>
        <div style={{ display: 'flex', height: 3 }}>
          <div style={{ flex: 3, background: '#E23D36' }} />
          <div style={{ flex: 5, background: '#1A2E4B' }} />
          <div style={{ flex: 2, background: '#00B3D9' }} />
        </div>
        <div style={{ padding: '20px 24px', display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
          <div style={{ display: 'flex', gap: 16, alignItems: 'center' }}>
            <div style={{ width: 56, height: 56, borderRadius: 999, background: '#1A2E4B', color: '#fff', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: 18, fontWeight: 600, flexShrink: 0 }}>
              {r.subject.split(' ').map(p => p[0]).slice(0,2).join('')}
            </div>
            <div>
              <div style={{ display: 'flex', alignItems: 'center', gap: 10, marginBottom: 4 }}>
                <span style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: 12, color: '#00B3D9' }}>{r.id}</span>
                <span style={{ fontSize: 11, padding: '2px 8px', background: '#F2F3F3', borderRadius: 3, fontWeight: 600, letterSpacing: '0.04em' }}>{r.type}</span>
                <PortalBadge variant={r.status}>{r.statusLabel}</PortalBadge>
              </div>
              <div style={{ fontSize: 20, fontWeight: 600, color: '#1A2024' }}>{r.subject}</div>
              <div style={{ fontSize: 12, color: '#4F5B67', marginTop: 2 }}>
                ID 9001015800087 · {r.country === 'ZA' ? 'South Africa' : r.country} · Started {r.updated} · Cost <b style={{ color: '#1A2024', fontFamily: "'JetBrains Mono', monospace" }}>R {r.cost.toFixed(2)}</b>
              </div>
            </div>
          </div>
          <div style={{ display: 'flex', gap: 8 }}>
            <PortalButton variant="secondary" icon={<PortalIcon name="Download" size={13} color="#1A2E4B" />}>Download PDF</PortalButton>
            <PortalButton variant="secondary">Re-run</PortalButton>
            <PortalButton variant="destructive" onClick={() => onDelete(r)}>Delete</PortalButton>
          </div>
        </div>
      </div>

      {/* Two-column: timeline + sidebar */}
      <div style={{ display: 'grid', gridTemplateColumns: '1.6fr 1fr', gap: 16 }}>
        <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, overflow: 'hidden' }}>
          <div style={{ padding: '14px 18px', borderBottom: '1px solid #e9ebed', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <div>
              <div style={{ fontSize: 14, fontWeight: 600 }}>Verification timeline</div>
              <div style={{ fontSize: 11, color: '#4F5B67', marginTop: 2 }}>5 sub-checks · all timestamps SAST</div>
            </div>
            <span style={{ fontSize: 11, color: '#4F5B67', fontFamily: "'JetBrains Mono', monospace" }}>Today, 14:32 → 14:33</span>
          </div>
          <div style={{ padding: '8px 0' }}>
            {checks.map((c, i) => (
              <div key={c.id} style={{ display: 'flex', gap: 14, padding: '12px 22px', position: 'relative' }}>
                {/* connector line */}
                {i < checks.length - 1 && <div style={{ position: 'absolute', left: 30, top: 30, bottom: -2, width: 1, background: '#e9ebed' }} />}
                <div style={{ width: 14, height: 14, borderRadius: 999, background: '#fff', border: `3px solid ${tone[c.status]}`, marginTop: 4, flexShrink: 0, zIndex: 1 }} />
                <div style={{ flex: 1 }}>
                  <div style={{ display: 'flex', alignItems: 'baseline', justifyContent: 'space-between', gap: 12 }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
                      <span style={{ fontSize: 13, fontWeight: 600, color: '#1A2024' }}>{c.label}</span>
                      <PortalBadge variant={c.status} size="sm">{c.status === 'success' ? 'Pass' : c.status === 'warning' ? 'Review' : c.status === 'danger' ? 'Fail' : 'Pending'}</PortalBadge>
                    </div>
                    <span style={{ fontSize: 11, color: '#4F5B67', fontFamily: "'JetBrains Mono', monospace" }}>{c.at} · {c.provider}</span>
                  </div>
                  <div style={{ fontSize: 12, color: '#4F5B67', marginTop: 4, lineHeight: 1.55 }}>{c.detail}</div>
                </div>
              </div>
            ))}
          </div>
          {/* Final result */}
          <div style={{ padding: '14px 22px', background: r.status === 'danger' ? 'rgba(226,61,54,0.05)' : r.status === 'warning' ? 'rgba(194,139,11,0.05)' : 'rgba(44,151,75,0.05)', borderTop: `2px solid ${tone[r.status]}` }}>
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', gap: 12 }}>
              <div>
                <div style={{ fontSize: 11, color: '#4F5B67', textTransform: 'uppercase', letterSpacing: '0.06em', fontWeight: 600 }}>Final result</div>
                <div style={{ fontSize: 16, fontWeight: 600, color: tone[r.status], marginTop: 2 }}>
                  {r.status === 'success' ? '✓ Verified — proceed with onboarding' : r.status === 'warning' ? '⚠ Manual review required' : r.status === 'danger' ? '✗ Failed — escalate to compliance' : 'In progress'}
                </div>
              </div>
              <PortalButton variant={r.status === 'success' ? 'primary' : 'cta'}>{r.status === 'danger' ? 'Escalate →' : r.status === 'warning' ? 'Open review →' : 'Approve onboarding →'}</PortalButton>
            </div>
          </div>
        </div>

        <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
          {/* Subject */}
          <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, padding: '14px 18px' }}>
            <div style={{ fontSize: 11, color: '#4F5B67', textTransform: 'uppercase', letterSpacing: '0.06em', fontWeight: 600, marginBottom: 10 }}>Subject details</div>
            {[
              ['Full name', r.subject],
              ['ID number', '9001015800087'],
              ['Date of birth', '1990-01-01'],
              ['Citizenship', 'South African'],
              ['Address on file', '1 Cinnebar St, Table View'],
            ].map(([k, v]) => (
              <div key={k} style={{ display: 'flex', justifyContent: 'space-between', padding: '6px 0', borderBottom: '1px solid #f1f5f9', fontSize: 12 }}>
                <span style={{ color: '#4F5B67' }}>{k}</span>
                <span style={{ color: '#1A2024', fontWeight: 500, textAlign: 'right' }}>{v}</span>
              </div>
            ))}
          </div>
          {/* Notes */}
          <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, padding: '14px 18px' }}>
            <div style={{ fontSize: 11, color: '#4F5B67', textTransform: 'uppercase', letterSpacing: '0.06em', fontWeight: 600, marginBottom: 8 }}>Internal notes</div>
            <textarea placeholder="Leave a note for your compliance team…" style={{ width: '100%', minHeight: 70, padding: 10, fontSize: 12, fontFamily: "'Inter', sans-serif", border: '1px solid #D5DBDB', borderRadius: 4, resize: 'vertical', outline: 'none' }} />
            <div style={{ marginTop: 8, display: 'flex', justifyContent: 'flex-end' }}>
              <PortalButton variant="ghost" size="sm">Add note</PortalButton>
            </div>
          </div>
          {/* Audit */}
          <div style={{ background: '#F8FAFC', border: '1px dashed #CBD5E1', borderRadius: 8, padding: '12px 16px', fontSize: 11, color: '#4F5B67', lineHeight: 1.6 }}>
            <b style={{ color: '#1A2E4B' }}>POPIA audit log:</b> All record access is logged. Subject was notified per POPIA s.18 on consent capture (2026-04-07).
          </div>
        </div>
      </div>
    </div>
  );
}

window.VerificationDetail = VerificationDetail;
