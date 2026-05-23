/* global React, PortalIcon, PortalButton, PortalBadge */
const { useState: useStateCD } = React;

const STATUS_LABELS = { OPEN: 'Open', IN_REVIEW: 'In Review', ESCALATED: 'Escalated', RESOLVED: 'Resolved' };
const STATUS_TONES  = { OPEN: 'info', IN_REVIEW: 'warning', ESCALATED: 'danger', RESOLVED: 'success' };

function CDAvatar({ name, size = 28 }) {
  const initials = (name || '?').split(' ').map(p => p[0]).slice(0,2).join('').toUpperCase();
  return <div style={{ width: size, height: size, borderRadius: 999, background: '#1A2E4B', color: '#fff', display: 'inline-flex', alignItems: 'center', justifyContent: 'center', fontSize: size * 0.36, fontWeight: 600, flexShrink: 0 }}>{initials}</div>;
}

function CDRiskGauge({ score }) {
  const color = score >= 80 ? '#E23D36' : score >= 60 ? '#C28B0B' : score >= 30 ? '#00B3D9' : '#2C974B';
  const r = 50;
  const c = Math.PI * r;
  return (
    <div style={{ position: 'relative', width: 130, height: 70 }}>
      <svg width={130} height={130} style={{ transform: 'translateY(0)' }}>
        <path d={`M 15,65 A ${r} ${r} 0 0 1 115,65`} fill="none" stroke="#F2F3F3" strokeWidth="9" strokeLinecap="round" />
        <path d={`M 15,65 A ${r} ${r} 0 0 1 115,65`} fill="none" stroke={color} strokeWidth="9" strokeLinecap="round" strokeDasharray={`${(score/100) * c} ${c}`} />
      </svg>
      <div style={{ position: 'absolute', top: 22, left: 0, right: 0, textAlign: 'center' }}>
        <div style={{ fontSize: 32, fontWeight: 700, color: '#1A2024', lineHeight: 1 }}>{score}</div>
        <div style={{ fontSize: 9, color: '#4F5B67', textTransform: 'uppercase', letterSpacing: '0.08em', marginTop: 2 }}>Composite</div>
      </div>
    </div>
  );
}

function CaseDetail({ caseObj, onBack }) {
  const [status, setStatus] = useStateCD(caseObj?.status || 'OPEN');
  const [commentText, setCommentText] = useStateCD('');
  const [comments, setComments] = useStateCD([
    { author: 'System',          text: `Case auto-created from verification ${caseObj?.verificationId}.`, ts: caseObj?.createdAt, system: true },
    { author: 'Naledi Nkosi',    text: 'Reviewing the credit listing; will request supporting docs from subject.', ts: '2026-05-18T15:11:00Z' },
    { author: 'Arthur Manena',   text: 'Escalating to compliance — confirm with TransUnion before resolving.',     ts: '2026-05-18T16:42:00Z' },
  ]);

  if (!caseObj) return null;

  const timeline = [
    { event: 'Case opened',                          actor: 'System',          ts: caseObj.createdAt, tone: 'info' },
    { event: 'Assigned to Naledi Nkosi',             actor: 'Arthur Manena',   ts: '2026-05-18T14:42:00Z', tone: 'info' },
    { event: 'Comment added',                        actor: 'Naledi Nkosi',    ts: '2026-05-18T15:11:00Z', tone: 'pending' },
    { event: 'Escalated to compliance',              actor: 'Arthur Manena',   ts: '2026-05-18T16:42:00Z', tone: 'danger' },
    { event: 'TransUnion confirmation requested',    actor: 'System',          ts: '2026-05-18T16:43:12Z', tone: 'info' },
  ];

  const factors = [
    { name: 'ID verification (DHA)',     value: '✓ Pass',      weight: 5,  contrib: -5 },
    { name: 'Sanctions / PEP',           value: '✓ Clear',     weight: 10, contrib: -10 },
    { name: 'Biometric match',           value: '✓ 0.94',       weight: 8,  contrib: -8 },
    { name: 'Credit bureau',             value: '✗ Hard fail', weight: 35, contrib: +35 },
    { name: 'Adverse listings (12mo)',   value: '⚠ 2 found',   weight: 25, contrib: +25 },
    { name: 'Address consistency',       value: '⚠ Mismatch',  weight: 15, contrib: +15 },
    { name: 'Velocity flags',            value: '✓ Normal',    weight: 8,  contrib: -8 },
  ];

  const tone = { info: '#00B3D9', danger: '#E23D36', warning: '#C28B0B', success: '#2C974B', pending: '#4F5B67' };

  const handleAddComment = () => {
    if (!commentText.trim()) return;
    setComments([...comments, { author: 'Arthur Manena (you)', text: commentText, ts: new Date().toISOString() }]);
    setCommentText('');
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }} data-screen-label="Case Detail">
      <button onClick={onBack} style={{ alignSelf: 'flex-start', background: 'transparent', border: 'none', color: '#00B3D9', fontSize: 12, fontWeight: 500, cursor: 'pointer', display: 'inline-flex', alignItems: 'center', gap: 4, padding: 0, fontFamily: "'Inter', sans-serif" }}>
        ← Back to cases
      </button>

      {/* Header card */}
      <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, overflow: 'hidden' }}>
        <div style={{ display: 'flex', height: 3 }}>
          <div style={{ flex: 3, background: '#E23D36' }} />
          <div style={{ flex: 5, background: '#1A2E4B' }} />
          <div style={{ flex: 2, background: '#00B3D9' }} />
        </div>
        <div style={{ padding: '20px 24px', display: 'grid', gridTemplateColumns: '1fr auto auto', gap: 28, alignItems: 'center' }}>
          <div style={{ display: 'flex', gap: 16, alignItems: 'center' }}>
            <CDAvatar name={caseObj.subjectName} size={56} />
            <div>
              <div style={{ display: 'flex', alignItems: 'center', gap: 10, marginBottom: 4 }}>
                <span style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: 12, color: '#00B3D9' }}>{caseObj.short}</span>
                <PortalBadge variant={STATUS_TONES[status]}>{STATUS_LABELS[status]}</PortalBadge>
                <span style={{ fontSize: 11, color: '#4F5B67', display: 'inline-flex', alignItems: 'center', gap: 4 }}>
                  <span style={{ width: 6, height: 6, borderRadius: 999, background: caseObj.priority === 'CRITICAL' ? '#E23D36' : caseObj.priority === 'HIGH' ? '#C28B0B' : '#00B3D9' }} />
                  {caseObj.priority} priority
                </span>
              </div>
              <div style={{ fontSize: 20, fontWeight: 600, color: '#1A2024' }}>{caseObj.subjectName}</div>
              <div style={{ fontSize: 12, color: '#4F5B67', marginTop: 4, fontFamily: "'JetBrains Mono', monospace" }}>{caseObj.subjectId}</div>
            </div>
          </div>
          <CDRiskGauge score={caseObj.riskScore} />
          <div style={{ display: 'flex', gap: 8 }}>
            <PortalButton variant="secondary">Reassign</PortalButton>
            <select value={status} onChange={e => setStatus(e.target.value)} style={{ padding: '7px 10px', borderRadius: 2, border: '1px solid #CBD5E1', background: '#fff', fontSize: 13, fontFamily: "'Inter', sans-serif" }}>
              {Object.entries(STATUS_LABELS).map(([k, v]) => <option key={k} value={k}>{v}</option>)}
            </select>
            {status !== 'RESOLVED' ? (
              <PortalButton variant="cta">Resolve →</PortalButton>
            ) : (
              <PortalButton variant="primary">Reopen</PortalButton>
            )}
          </div>
        </div>
      </div>

      {/* Two-column body */}
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 340px', gap: 16 }}>
        {/* MAIN */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: 14 }}>
          {/* AI Summary */}
          <div style={{ background: 'linear-gradient(135deg, rgba(0,179,217,0.06), rgba(0,179,217,0.02))', border: '1px solid rgba(0,179,217,0.25)', borderRadius: 8, padding: '14px 18px' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 8 }}>
              <span style={{ width: 22, height: 22, borderRadius: 6, background: 'rgba(0,179,217,0.15)', display: 'inline-flex', alignItems: 'center', justifyContent: 'center' }}>
                <PortalIcon name="Zap" size={12} color="#00B3D9" />
              </span>
              <span style={{ fontSize: 12, fontWeight: 600, color: '#0099bb', textTransform: 'uppercase', letterSpacing: '0.08em' }}>AI summary</span>
              <span style={{ marginLeft: 'auto', fontSize: 10, color: '#4F5B67', fontFamily: "'JetBrains Mono', monospace" }}>Generated 2m ago</span>
            </div>
            <div style={{ fontSize: 13, color: '#1A2024', lineHeight: 1.6 }}>
              This case was opened because the credit bureau returned a <b>hard fail</b> with two adverse listings within the last 12 months. Identity (DHA), sanctions, and biometrics all passed cleanly. The composite risk score is <b style={{ color: '#E23D36' }}>{caseObj.riskScore}</b> — driven primarily by credit findings (+60) and address inconsistency (+15). <b>Recommended action:</b> request supporting documents from subject and verify with TransUnion before approving onboard.
            </div>
          </div>

          {/* Risk breakdown */}
          <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, overflow: 'hidden' }}>
            <div style={{ padding: '14px 18px', borderBottom: '1px solid #e9ebed' }}>
              <div style={{ fontSize: 14, fontWeight: 600 }}>Risk factors</div>
              <div style={{ fontSize: 11, color: '#4F5B67', marginTop: 2 }}>7 signals contribute to the composite score</div>
            </div>
            <div style={{ padding: '6px 0' }}>
              {factors.map(f => (
                <div key={f.name} style={{ display: 'flex', alignItems: 'center', gap: 14, padding: '8px 18px', borderBottom: '1px solid #f1f5f9', fontSize: 12 }}>
                  <div style={{ flex: 1, color: '#1A2024', fontWeight: 500 }}>{f.name}</div>
                  <div style={{ width: 110, color: f.contrib > 0 ? '#E23D36' : '#4F5B67', fontFamily: "'JetBrains Mono', monospace", fontSize: 11 }}>{f.value}</div>
                  <div style={{ width: 80, height: 4, background: '#F2F3F3', borderRadius: 2, position: 'relative', overflow: 'hidden' }}>
                    <div style={{ position: 'absolute', left: '50%', top: 0, bottom: 0, width: `${Math.abs(f.contrib) * 1.4}%`, background: f.contrib > 0 ? '#E23D36' : '#2C974B', transform: f.contrib > 0 ? 'translateX(0)' : 'translateX(-100%)' }} />
                  </div>
                  <div style={{ width: 50, textAlign: 'right', fontFamily: "'JetBrains Mono', monospace", fontWeight: 600, color: f.contrib > 0 ? '#E23D36' : '#2C974B' }}>{f.contrib > 0 ? '+' : ''}{f.contrib}</div>
                </div>
              ))}
            </div>
          </div>

          {/* Comments */}
          <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, overflow: 'hidden' }}>
            <div style={{ padding: '14px 18px', borderBottom: '1px solid #e9ebed', display: 'flex', alignItems: 'center', gap: 6 }}>
              <PortalIcon name="ChevronRight" size={14} color="#1A2E4B" stroke={2.5} style={{ transform: 'rotate(45deg)' }} />
              <span style={{ fontSize: 14, fontWeight: 600 }}>Comments ({comments.filter(c => !c.system).length})</span>
            </div>
            <div style={{ padding: '12px 18px', display: 'flex', flexDirection: 'column', gap: 10 }}>
              {comments.map((cm, i) => (
                <div key={i} style={{ display: 'flex', gap: 10 }}>
                  <CDAvatar name={cm.system ? 'Verigate' : cm.author} size={28} />
                  <div style={{ flex: 1, background: cm.system ? '#F8FAFC' : '#fff', border: `1px solid ${cm.system ? '#e9ebed' : '#D5DBDB'}`, borderRadius: 6, padding: '8px 12px' }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 4 }}>
                      <span style={{ fontSize: 11, fontWeight: 600, color: cm.system ? '#4F5B67' : '#1A2024' }}>{cm.author}</span>
                      <span style={{ fontSize: 10, color: '#4F5B67' }}>{new Date(cm.ts).toLocaleString('en-ZA', { dateStyle: 'medium', timeStyle: 'short' })}</span>
                    </div>
                    <div style={{ fontSize: 12, color: '#1A2024', lineHeight: 1.5 }}>{cm.text}</div>
                  </div>
                </div>
              ))}
              <div style={{ display: 'flex', gap: 8, paddingTop: 8, borderTop: '1px solid #f1f5f9' }}>
                <CDAvatar name="Arthur Manena" size={28} />
                <input value={commentText} onChange={e => setCommentText(e.target.value)} onKeyDown={e => e.key === 'Enter' && handleAddComment()} placeholder="Add a comment, mention @teammates or attach a document…"
                  style={{ flex: 1, padding: '7px 11px', borderRadius: 4, border: '1px solid #D5DBDB', fontSize: 12, fontFamily: "'Inter', sans-serif", outline: 'none' }}
                  onFocus={e => { e.currentTarget.style.borderColor = '#00B3D9'; e.currentTarget.style.boxShadow = '0 0 0 3px rgba(0,179,217,0.15)'; }}
                  onBlur={e => { e.currentTarget.style.borderColor = '#D5DBDB'; e.currentTarget.style.boxShadow = 'none'; }} />
                <PortalButton variant="primary" size="sm" onClick={handleAddComment} disabled={!commentText.trim()}>Comment</PortalButton>
              </div>
            </div>
          </div>
        </div>

        {/* SIDEBAR */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
          {/* Linked verification */}
          <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, padding: '14px 16px' }}>
            <div style={{ fontSize: 11, color: '#4F5B67', textTransform: 'uppercase', letterSpacing: '0.06em', fontWeight: 600, marginBottom: 10 }}>Linked records</div>
            <div style={{ display: 'flex', flexDirection: 'column', gap: 6 }}>
              {[
                { label: 'Verification', id: caseObj.verificationId,    icon: 'FileSearch' },
                { label: 'Workflow',     id: 'WF-onboard-standard-v3',  icon: 'Layers' },
                { label: 'Subject ID',   id: caseObj.subjectId,         icon: 'User', mono: true },
              ].map(r => (
                <div key={r.label} style={{ display: 'flex', alignItems: 'center', gap: 8, padding: '6px 8px', borderRadius: 4, fontSize: 11, color: '#1A2024', background: '#F8FAFC' }}>
                  <PortalIcon name={r.icon} size={12} color="#4F5B67" />
                  <span style={{ color: '#4F5B67', minWidth: 70 }}>{r.label}</span>
                  <span style={{ flex: 1, fontFamily: "'JetBrains Mono', monospace", color: '#00B3D9' }}>{r.id}</span>
                </div>
              ))}
            </div>
          </div>

          {/* Timeline */}
          <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, padding: '14px 16px' }}>
            <div style={{ fontSize: 11, color: '#4F5B67', textTransform: 'uppercase', letterSpacing: '0.06em', fontWeight: 600, marginBottom: 12 }}>Activity</div>
            <div style={{ display: 'flex', flexDirection: 'column', gap: 10, position: 'relative' }}>
              <div style={{ position: 'absolute', left: 5, top: 6, bottom: 6, width: 1, background: '#e9ebed' }} />
              {timeline.map((t, i) => (
                <div key={i} style={{ display: 'flex', gap: 10, position: 'relative' }}>
                  <span style={{ width: 11, height: 11, borderRadius: 999, background: '#fff', border: `2px solid ${tone[t.tone]}`, flexShrink: 0, zIndex: 1, marginTop: 3 }} />
                  <div style={{ flex: 1 }}>
                    <div style={{ fontSize: 12, color: '#1A2024' }}>{t.event}</div>
                    <div style={{ fontSize: 10, color: '#4F5B67', marginTop: 2 }}>{t.actor} · {new Date(t.ts).toLocaleString('en-ZA', { dateStyle: 'short', timeStyle: 'short' })}</div>
                  </div>
                </div>
              ))}
            </div>
          </div>

          {/* SLA */}
          <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, padding: '14px 16px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 10 }}>
              <span style={{ fontSize: 11, color: '#4F5B67', textTransform: 'uppercase', letterSpacing: '0.06em', fontWeight: 600 }}>Resolution SLA</span>
              <PortalBadge variant="warning" size="sm">22h left</PortalBadge>
            </div>
            <div style={{ height: 6, background: '#F2F3F3', borderRadius: 3, overflow: 'hidden', marginBottom: 8 }}>
              <div style={{ width: '54%', height: '100%', background: '#C28B0B' }} />
            </div>
            <div style={{ fontSize: 11, color: '#4F5B67', lineHeight: 1.5 }}>Target: 48h · Elapsed: 26h · 6h to escalation tier 2.</div>
          </div>

          {/* POPIA */}
          <div style={{ background: '#F8FAFC', border: '1px dashed #CBD5E1', borderRadius: 8, padding: '12px 14px', fontSize: 11, color: '#4F5B67', lineHeight: 1.55 }}>
            <b style={{ color: '#1A2E4B' }}>POPIA s.18:</b> Subject was notified of this verification at consent capture. All comments and access events are logged.
          </div>
        </div>
      </div>
    </div>
  );
}

window.CaseDetail = CaseDetail;
