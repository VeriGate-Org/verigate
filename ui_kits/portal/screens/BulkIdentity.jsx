/* global React, PortalIcon, PortalButton, PortalBadge, ServicePageHeader, ServicePageTabs */
const { useState: useStateB, useMemo: useMemoB, useEffect: useEffectB } = React;

/* ──────────────────────────────────────────────────────────
   Bulk Identity Verification — 3 phases: upload → processing → results
   Mirrors verigate-partner-portal/components/services/BulkIdentityVerification.client.tsx
   ──────────────────────────────────────────────────────── */

const SAMPLE_CSV = `id_number,billing_group
9001015800087,Onboarding-Q2
8503125800087,Onboarding-Q2
7806174500083,Vendor-2026
9304117500084,Onboarding-Q2
8801135500084,
9509223200086,Onboarding-Q2
7503060800087,Vendor-2026
8211056500088,
9006120800086,Onboarding-Q2
9405127700089,Onboarding-Q2`;

const PAST_JOBS = [
  { id: 'BJV-2026-019', file: 'q2_onboarding.csv',  count: 412, passed: 388, failed: 14, review: 10, status: 'success', statusLabel: 'Completed', at: '2026-05-17T09:11Z' },
  { id: 'BJV-2026-018', file: 'vendor_screen.csv',   count: 78,  passed: 72,  failed: 4,  review: 2,  status: 'success', statusLabel: 'Completed', at: '2026-05-15T14:42Z' },
  { id: 'BJV-2026-017', file: 'q1_audit.csv',       count: 1240, passed: 1188, failed: 31, review: 21, status: 'success', statusLabel: 'Completed', at: '2026-05-12T11:22Z' },
  { id: 'BJV-2026-016', file: 'staff_refresh.csv',   count: 24,  passed: 0, failed: 0, review: 0, status: 'danger', statusLabel: 'Failed', at: '2026-05-10T08:11Z' },
];

function parseCsv(text) {
  const lines = text.split(/\r?\n/).map(l => l.trim()).filter(Boolean);
  // skip header if present
  const data = (lines[0] || '').toLowerCase().includes('id_number') ? lines.slice(1) : lines;
  return data.map((line, i) => {
    const [id, group] = line.split(',').map(s => (s || '').trim());
    const valid = /^\d{13}$/.test(id);
    return { row: i + 1, idNumber: id, billingGroup: group || '', valid };
  });
}

function BulkUploadPhase({ csvText, setCsvText, onSubmit }) {
  const items = useMemoB(() => parseCsv(csvText), [csvText]);
  const errors = items.filter(x => !x.valid && x.idNumber);
  const validCount = items.filter(x => x.valid).length;
  const groups = useMemoB(() => Array.from(new Set(items.filter(x => x.billingGroup).map(x => x.billingGroup))), [items]);

  const handleFile = (file) => {
    const reader = new FileReader();
    reader.onload = e => setCsvText(e.target.result);
    reader.readAsText(file);
  };

  const downloadTemplate = () => {
    const csv = 'id_number,billing_group\n9001015800087,Onboarding-Q2\n8503125800087,Onboarding-Q2\n';
    const blob = new Blob([csv], { type: 'text/csv' });
    const a = document.createElement('a');
    a.href = URL.createObjectURL(blob);
    a.download = 'bulk-identity-template.csv';
    a.click();
  };

  return (
    <div style={{ display: 'grid', gridTemplateColumns: 'minmax(0, 440px) minmax(0, 1fr)', gap: 14, alignItems: 'flex-start' }}>
      <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, overflow: 'hidden' }}>
        <div style={{ padding: '14px 18px', borderBottom: '1px solid #e9ebed' }}>
          <div style={{ fontSize: 14, fontWeight: 600 }}>Input</div>
          <div style={{ fontSize: 11, color: '#4F5B67', marginTop: 2 }}>CSV file or paste ID numbers (one per line).</div>
        </div>
        <div style={{ padding: '16px 18px', display: 'flex', flexDirection: 'column', gap: 12 }}>
          <label style={{ display: 'block', padding: '20px 14px', border: '2px dashed #CBD5E1', background: '#F8FAFC', borderRadius: 6, textAlign: 'center', cursor: 'pointer' }}
            onDragOver={e => e.preventDefault()}
            onDrop={e => { e.preventDefault(); if (e.dataTransfer.files[0]) handleFile(e.dataTransfer.files[0]); }}>
            <input type="file" accept=".csv,.txt" style={{ display: 'none' }} onChange={e => e.target.files[0] && handleFile(e.target.files[0])} />
            <PortalIcon name="Download" size={20} color="#00B3D9" style={{ transform: 'rotate(180deg)' }} />
            <div style={{ fontSize: 12, fontWeight: 600, color: '#1A2024', marginTop: 6 }}>Drop CSV or click to browse</div>
            <div style={{ fontSize: 10, color: '#4F5B67', marginTop: 2 }}>Max 5,000 rows per job</div>
          </label>
          <div style={{ position: 'relative' }}>
            <textarea value={csvText} onChange={e => setCsvText(e.target.value)} rows={10} placeholder={"id_number,billing_group\n9001015800087,Onboarding-Q2"}
              style={{ width: '100%', padding: '10px 12px', border: '1px solid #D5DBDB', borderRadius: 4, fontSize: 12, fontFamily: "'JetBrains Mono', monospace", lineHeight: 1.6, resize: 'vertical', outline: 'none' }}
              onFocus={e => { e.currentTarget.style.borderColor = '#00B3D9'; e.currentTarget.style.boxShadow = '0 0 0 3px rgba(0,179,217,0.15)'; }}
              onBlur={e => { e.currentTarget.style.borderColor = '#D5DBDB'; e.currentTarget.style.boxShadow = 'none'; }} />
          </div>
          <div style={{ display: 'flex', gap: 8, alignItems: 'center', justifyContent: 'space-between' }}>
            <button onClick={() => setCsvText(SAMPLE_CSV)} style={{ background: 'transparent', border: 'none', color: '#00B3D9', fontSize: 11, fontWeight: 500, cursor: 'pointer', fontFamily: "'Inter', sans-serif" }}>Load sample data</button>
            <PortalButton variant="ghost" size="sm" onClick={downloadTemplate} icon={<PortalIcon name="Download" size={11} color="#1A2E4B" />}>Download template</PortalButton>
          </div>
        </div>
        <div style={{ padding: '12px 18px', borderTop: '1px solid #e9ebed', background: '#F8FAFC', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <span style={{ fontSize: 11, color: '#4F5B67' }}>{validCount} valid · {errors.length} errors</span>
          <PortalButton variant="cta" disabled={validCount === 0} onClick={() => onSubmit(items.filter(x => x.valid))}>Submit bulk job →</PortalButton>
        </div>
      </div>

      <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
        <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, padding: '14px 18px', display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: 16 }}>
          <div><div style={{ fontSize: 10, color: '#4F5B67', textTransform: 'uppercase', letterSpacing: '0.06em', fontWeight: 600 }}>Total rows</div><div style={{ fontSize: 24, fontWeight: 700, color: '#1A2024' }}>{items.length}</div></div>
          <div><div style={{ fontSize: 10, color: '#2C974B', textTransform: 'uppercase', letterSpacing: '0.06em', fontWeight: 600 }}>Valid</div><div style={{ fontSize: 24, fontWeight: 700, color: '#2C974B' }}>{validCount}</div></div>
          <div><div style={{ fontSize: 10, color: '#E23D36', textTransform: 'uppercase', letterSpacing: '0.06em', fontWeight: 600 }}>Errors</div><div style={{ fontSize: 24, fontWeight: 700, color: '#E23D36' }}>{errors.length}</div></div>
        </div>
        {groups.length > 0 && (
          <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, padding: '12px 16px' }}>
            <div style={{ fontSize: 11, color: '#4F5B67', textTransform: 'uppercase', letterSpacing: '0.06em', fontWeight: 600, marginBottom: 8 }}>Billing groups detected</div>
            <div style={{ display: 'flex', gap: 6, flexWrap: 'wrap' }}>
              {groups.map(g => <PortalBadge key={g} variant="info" size="sm">{g}</PortalBadge>)}
              {items.some(x => !x.billingGroup) && <PortalBadge variant="neutral" size="sm">{items.filter(x => !x.billingGroup).length} unassigned</PortalBadge>}
            </div>
          </div>
        )}
        <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, overflow: 'hidden' }}>
          <div style={{ padding: '12px 18px', borderBottom: '1px solid #e9ebed' }}>
            <div style={{ fontSize: 13, fontWeight: 600 }}>Preview ({items.length} rows)</div>
          </div>
          <div style={{ maxHeight: 320, overflowY: 'auto' }}>
            <table style={{ width: '100%', borderCollapse: 'separate', borderSpacing: 0, fontSize: 12 }}>
              <thead style={{ background: '#F2F3F3', position: 'sticky', top: 0 }}>
                <tr>{['#', 'ID number', 'Billing group', 'Status'].map(h => (
                  <th key={h} style={{ padding: '6px 14px', textAlign: 'left', fontSize: 10, color: '#4F5B67', textTransform: 'uppercase', letterSpacing: '0.05em', fontWeight: 600, borderBottom: '1px solid #e9ebed' }}>{h}</th>
                ))}</tr>
              </thead>
              <tbody>
                {items.length === 0 && <tr><td colSpan={4} style={{ padding: 28, textAlign: 'center', color: '#4F5B67', fontSize: 12 }}>Paste or upload to preview.</td></tr>}
                {items.map(r => (
                  <tr key={r.row}>
                    <td style={{ padding: '6px 14px', borderBottom: '1px solid #f1f5f9', color: '#4F5B67', fontFamily: "'JetBrains Mono', monospace" }}>{r.row}</td>
                    <td style={{ padding: '6px 14px', borderBottom: '1px solid #f1f5f9', fontFamily: "'JetBrains Mono', monospace", color: r.valid ? '#1A2024' : '#E23D36' }}>{r.idNumber || '—'}</td>
                    <td style={{ padding: '6px 14px', borderBottom: '1px solid #f1f5f9', color: '#4F5B67' }}>{r.billingGroup || <em style={{ color: '#94A3B8' }}>none</em>}</td>
                    <td style={{ padding: '6px 14px', borderBottom: '1px solid #f1f5f9' }}>{r.valid ? <PortalBadge variant="success" size="sm">Valid</PortalBadge> : <PortalBadge variant="danger" size="sm">Invalid</PortalBadge>}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
}

function BulkProcessingPhase({ total, progress, onCancel }) {
  return (
    <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, padding: '32px 28px', textAlign: 'center' }}>
      <div style={{ width: 56, height: 56, borderRadius: 999, background: 'rgba(0,179,217,0.10)', display: 'inline-flex', alignItems: 'center', justifyContent: 'center', marginBottom: 14 }}>
        <span style={{ width: 12, height: 12, borderRadius: 999, background: '#00B3D9', boxShadow: '0 0 12px #00B3D9', animation: 'bulkPulse 1.2s ease-in-out infinite' }} />
        <style>{`@keyframes bulkPulse{0%,100%{opacity:1;transform:scale(1)}50%{opacity:0.4;transform:scale(1.4)}}`}</style>
      </div>
      <div style={{ fontSize: 16, fontWeight: 600, color: '#1A2024' }}>Processing bulk verification</div>
      <div style={{ fontSize: 12, color: '#4F5B67', marginTop: 6 }}>Verifying <b>{total}</b> identities against DHA. This typically takes 1–2 minutes.</div>
      <div style={{ maxWidth: 420, margin: '24px auto 12px', height: 8, background: '#F2F3F3', borderRadius: 4, overflow: 'hidden' }}>
        <div style={{ width: progress + '%', height: '100%', background: 'linear-gradient(90deg, #00B3D9, #0099bb)', transition: 'width 200ms cubic-bezier(0.4,0,0.2,1)' }} />
      </div>
      <div style={{ fontSize: 11, color: '#4F5B67', fontFamily: "'JetBrains Mono', monospace" }}>{Math.round((progress / 100) * total)} / {total} · {progress}%</div>
      <div style={{ marginTop: 18 }}>
        <PortalButton variant="ghost" onClick={onCancel}>Cancel job</PortalButton>
      </div>
    </div>
  );
}

function BulkResultsPhase({ items, onReset }) {
  // Build a summary
  const fakeOutcomes = items.map((r, i) => {
    const seed = i + r.idNumber.charCodeAt(0);
    const outcome = seed % 9 === 0 ? 'failed' : seed % 7 === 0 ? 'review' : 'passed';
    return { ...r, outcome, score: outcome === 'passed' ? 90 + (seed % 10) : outcome === 'review' ? 55 + (seed % 20) : 20 + (seed % 30) };
  });
  const passed = fakeOutcomes.filter(x => x.outcome === 'passed').length;
  const failed = fakeOutcomes.filter(x => x.outcome === 'failed').length;
  const review = fakeOutcomes.filter(x => x.outcome === 'review').length;
  const passRate = ((passed / fakeOutcomes.length) * 100).toFixed(1);

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 14 }}>
      <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, overflow: 'hidden' }}>
        <div style={{ display: 'flex', height: 3 }}>
          <div style={{ flex: 3, background: '#E23D36' }} />
          <div style={{ flex: 5, background: '#1A2E4B' }} />
          <div style={{ flex: 2, background: '#00B3D9' }} />
        </div>
        <div style={{ padding: '16px 20px', display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', borderBottom: '1px solid #e9ebed' }}>
          <div>
            <div style={{ fontSize: 11, color: '#4F5B67', textTransform: 'uppercase', letterSpacing: '0.06em', fontWeight: 600 }}>Job complete</div>
            <div style={{ fontSize: 18, fontWeight: 600, marginTop: 4 }}>Bulk verification finished</div>
            <div style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: 11, color: '#00B3D9', marginTop: 2 }}>BJV-2026-{String(20 + Math.floor(Math.random() * 10)).padStart(3, '0')}</div>
          </div>
          <div style={{ display: 'flex', gap: 8 }}>
            <PortalButton variant="secondary" icon={<PortalIcon name="Download" size={12} color="#1A2E4B" />}>Export CSV</PortalButton>
            <PortalButton variant="primary" onClick={onReset}>New bulk job</PortalButton>
          </div>
        </div>
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)' }}>
          {[
            { label: 'Total submitted', value: fakeOutcomes.length, tone: '#1A2024' },
            { label: 'Passed',          value: passed,             tone: '#2C974B' },
            { label: 'Manual review',   value: review,             tone: '#C28B0B' },
            { label: 'Failed',          value: failed,             tone: '#E23D36' },
          ].map((c, i) => (
            <div key={c.label} style={{ padding: '14px 20px', borderLeft: i > 0 ? '1px solid #e9ebed' : 'none' }}>
              <div style={{ fontSize: 10, color: '#4F5B67', textTransform: 'uppercase', letterSpacing: '0.06em', fontWeight: 600 }}>{c.label}</div>
              <div style={{ fontSize: 28, fontWeight: 700, color: c.tone, marginTop: 4 }}>{c.value}</div>
            </div>
          ))}
        </div>
        <div style={{ padding: '12px 20px', background: '#F8FAFC', borderTop: '1px solid #e9ebed', fontSize: 12, color: '#4F5B67' }}>
          Pass rate <b style={{ color: '#1A2024', fontFamily: "'JetBrains Mono', monospace" }}>{passRate}%</b> · Industry average 91.0% · Total billed <b style={{ color: '#1A2024', fontFamily: "'JetBrains Mono', monospace" }}>R {(fakeOutcomes.length * 25).toLocaleString('en-ZA', { minimumFractionDigits: 2 })}</b>
        </div>
      </div>

      <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, overflow: 'hidden' }}>
        <div style={{ padding: '12px 18px', borderBottom: '1px solid #e9ebed', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <div style={{ fontSize: 13, fontWeight: 600 }}>Per-subject results</div>
          <div style={{ display: 'flex', gap: 6 }}>
            {['All', 'Passed', 'Review', 'Failed'].map(f => (
              <button key={f} style={{ padding: '4px 10px', fontSize: 11, borderRadius: 16, border: '1px solid #D5DBDB', background: f === 'All' ? '#1A2E4B' : '#fff', color: f === 'All' ? '#fff' : '#1A2024', cursor: 'pointer', fontFamily: "'Inter', sans-serif", fontWeight: 500 }}>{f}</button>
            ))}
          </div>
        </div>
        <table style={{ width: '100%', borderCollapse: 'separate', borderSpacing: 0, fontSize: 12 }}>
          <thead style={{ background: '#F2F3F3' }}>
            <tr>{['#', 'ID number', 'Billing group', 'Outcome', 'Score', 'Cost'].map(h => (
              <th key={h} style={{ padding: '8px 14px', textAlign: 'left', fontSize: 10, color: '#4F5B67', textTransform: 'uppercase', letterSpacing: '0.05em', fontWeight: 600, borderBottom: '1px solid #e9ebed' }}>{h}</th>
            ))}</tr>
          </thead>
          <tbody>
            {fakeOutcomes.map(r => {
              const tone = { passed: 'success', review: 'warning', failed: 'danger' }[r.outcome];
              const label = { passed: 'Verified', review: 'Review', failed: 'Failed' }[r.outcome];
              return (
                <tr key={r.row}>
                  <td style={{ padding: '8px 14px', borderBottom: '1px solid #f1f5f9', color: '#4F5B67', fontFamily: "'JetBrains Mono', monospace" }}>{r.row}</td>
                  <td style={{ padding: '8px 14px', borderBottom: '1px solid #f1f5f9', fontFamily: "'JetBrains Mono', monospace" }}>{r.idNumber}</td>
                  <td style={{ padding: '8px 14px', borderBottom: '1px solid #f1f5f9', color: '#4F5B67' }}>{r.billingGroup || <em>—</em>}</td>
                  <td style={{ padding: '8px 14px', borderBottom: '1px solid #f1f5f9' }}><PortalBadge variant={tone} size="sm">{label}</PortalBadge></td>
                  <td style={{ padding: '8px 14px', borderBottom: '1px solid #f1f5f9', fontFamily: "'JetBrains Mono', monospace", fontWeight: 600, color: r.score >= 80 ? '#2C974B' : r.score >= 50 ? '#C28B0B' : '#E23D36' }}>{r.score}</td>
                  <td style={{ padding: '8px 14px', borderBottom: '1px solid #f1f5f9', fontFamily: "'JetBrains Mono', monospace" }}>R 25.00</td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>
    </div>
  );
}

function BulkPastJobs() {
  return (
    <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, overflow: 'hidden' }}>
      <div style={{ padding: '12px 18px', borderBottom: '1px solid #e9ebed' }}>
        <div style={{ fontSize: 14, fontWeight: 600 }}>Past bulk jobs</div>
        <div style={{ fontSize: 11, color: '#4F5B67', marginTop: 2 }}>{PAST_JOBS.length} jobs · last 30 days</div>
      </div>
      <table style={{ width: '100%', borderCollapse: 'separate', borderSpacing: 0, fontSize: 12 }}>
        <thead style={{ background: '#F2F3F3' }}>
          <tr>{['Job ID', 'File', 'Submitted', 'Passed', 'Review', 'Failed', 'Status', 'Created'].map(h => (
            <th key={h} style={{ padding: '8px 14px', textAlign: 'left', fontSize: 10, color: '#4F5B67', textTransform: 'uppercase', letterSpacing: '0.05em', fontWeight: 600, borderBottom: '1px solid #e9ebed' }}>{h}</th>
          ))}</tr>
        </thead>
        <tbody>
          {PAST_JOBS.map(j => (
            <tr key={j.id} style={{ cursor: 'pointer' }} onMouseEnter={e => e.currentTarget.style.background = '#F8FAFC'} onMouseLeave={e => e.currentTarget.style.background = 'transparent'}>
              <td style={{ padding: '10px 14px', borderBottom: '1px solid #e9ebed', fontFamily: "'JetBrains Mono', monospace", fontSize: 11, color: '#00B3D9' }}>{j.id}</td>
              <td style={{ padding: '10px 14px', borderBottom: '1px solid #e9ebed', fontFamily: "'JetBrains Mono', monospace", fontSize: 11 }}>{j.file}</td>
              <td style={{ padding: '10px 14px', borderBottom: '1px solid #e9ebed', fontFamily: "'JetBrains Mono', monospace", fontWeight: 600 }}>{j.count}</td>
              <td style={{ padding: '10px 14px', borderBottom: '1px solid #e9ebed', color: '#2C974B', fontWeight: 600 }}>{j.passed}</td>
              <td style={{ padding: '10px 14px', borderBottom: '1px solid #e9ebed', color: '#C28B0B', fontWeight: 600 }}>{j.review}</td>
              <td style={{ padding: '10px 14px', borderBottom: '1px solid #e9ebed', color: '#E23D36', fontWeight: 600 }}>{j.failed}</td>
              <td style={{ padding: '10px 14px', borderBottom: '1px solid #e9ebed' }}><PortalBadge variant={j.status}>{j.statusLabel}</PortalBadge></td>
              <td style={{ padding: '10px 14px', borderBottom: '1px solid #e9ebed', color: '#4F5B67', fontSize: 11 }}>{new Date(j.at).toLocaleString('en-ZA', { dateStyle: 'short', timeStyle: 'short' })}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

function BulkIdentity() {
  const [tab, setTab] = useStateB('new');
  const [csvText, setCsvText] = useStateB('');
  const [phase, setPhase] = useStateB('upload'); // upload | processing | results
  const [items, setItems] = useStateB([]);
  const [progress, setProgress] = useStateB(0);

  useEffectB(() => {
    if (phase !== 'processing') return;
    setProgress(0);
    const start = Date.now();
    const duration = 3500;
    const tick = setInterval(() => {
      const pct = Math.min(100, ((Date.now() - start) / duration) * 100);
      setProgress(Math.round(pct));
      if (pct >= 100) { clearInterval(tick); setPhase('results'); }
    }, 80);
    return () => clearInterval(tick);
  }, [phase]);

  const reset = () => { setPhase('upload'); setCsvText(''); setItems([]); };

  const tabs = [
    { id: 'new',     label: 'New bulk job' },
    { id: 'history', label: 'Past jobs', count: PAST_JOBS.length },
  ];

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 14 }} data-screen-label="Bulk Identity Verification">
      <ServicePageHeader
        title="Bulk Identity Verification"
        subtitle="Verify hundreds or thousands of subjects against DHA records in a single job. CSV upload, paste, or API."
      />
      <ServicePageTabs tabs={tabs} active={tab} onChange={setTab} />

      {tab === 'new' && phase === 'upload'    && <BulkUploadPhase csvText={csvText} setCsvText={setCsvText} onSubmit={(its) => { setItems(its); setPhase('processing'); }} />}
      {tab === 'new' && phase === 'processing'&& <BulkProcessingPhase total={items.length} progress={progress} onCancel={reset} />}
      {tab === 'new' && phase === 'results'   && <BulkResultsPhase items={items} onReset={reset} />}
      {tab === 'history' && <BulkPastJobs />}
    </div>
  );
}

window.BulkIdentity = BulkIdentity;
