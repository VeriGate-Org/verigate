/* global React, PortalIcon, PortalButton, PortalBadge, ServicePageHeader, ServicePageTabs */
const { useState: useStateD, useMemo: useMemoD } = React;

/* ──────────────────────────────────────────────────────────
   Document Verification — clones the Service Page template
   ──────────────────────────────────────────────────────── */

const DOC_GROUPS = [
  { label: 'Identity', types: [
    { value: 'id_card',        label: 'SA ID Card',         icon: 'UserCheck',  hint: 'Smart card or green book' },
    { value: 'passport',       label: 'Passport',           icon: 'Globe',      hint: 'Photo page' },
    { value: 'drivers_license',label: "Driver's License",   icon: 'CreditCard', hint: 'Front + back' },
  ]},
  { label: 'Business', types: [
    { value: 'b_bbee',         label: 'B-BBEE Certificate', icon: 'Briefcase' },
    { value: 'cipc',           label: 'CIPC Registration',  icon: 'Briefcase' },
  ]},
  { label: 'Financial', types: [
    { value: 'tax_certificate',label: 'Tax Clearance',      icon: 'Receipt' },
    { value: 'financial_stmt', label: 'Financial Stmt.',    icon: 'BarChart' },
  ]},
  { label: 'Proof of Address', types: [
    { value: 'utility_bill',   label: 'Utility Bill',       icon: 'FileSearch' },
  ]},
];

const DOC_HISTORY = [
  { id: 'DV-2026-1284', file: 'sa_id_jane_smith.jpg',      type: 'id_card',       typeLabel: 'SA ID Card',     tampering: 96, fieldsCount: 8, status: 'success', statusLabel: 'Authentic',   at: '2026-05-18T14:33Z' },
  { id: 'DV-2026-1283', file: 'passport_mandela_t.pdf',    type: 'passport',      typeLabel: 'Passport',       tampering: 91, fieldsCount: 9, status: 'success', statusLabel: 'Authentic',   at: '2026-05-18T13:11Z' },
  { id: 'DV-2026-1282', file: 'utility_bill_naledi.pdf',   type: 'utility_bill',  typeLabel: 'Utility Bill',   tampering: 72, fieldsCount: 5, status: 'warning', statusLabel: 'Review',      at: '2026-05-18T11:50Z' },
  { id: 'DV-2026-1281', file: 'cipc_acme_corp.pdf',        type: 'cipc',          typeLabel: 'CIPC Reg.',      tampering: 88, fieldsCount: 7, status: 'success', statusLabel: 'Authentic',   at: '2026-05-18T09:24Z' },
  { id: 'DV-2026-1280', file: 'drivers_pieter.jpg',        type: 'drivers_license', typeLabel: "Driver's License", tampering: 54, fieldsCount: 7, status: 'danger',  statusLabel: 'Tampered', at: '2026-05-17T16:42Z' },
  { id: 'DV-2026-1279', file: 'tax_cert_sipho.pdf',        type: 'tax_certificate', typeLabel: 'Tax Clearance', tampering: 94, fieldsCount: 6, status: 'success', statusLabel: 'Authentic',   at: '2026-05-17T14:08Z' },
];

/* Sample verification result */
const DEMO_RESULT = {
  id: 'DV-2026-1285',
  fileName: 'sa_id_demo.jpg',
  documentType: 'id_card',
  documentTypeLabel: 'SA ID Card',
  fields: [
    { key: 'idNumber',     label: 'ID number',     value: '9001015800087', confidence: 0.98, mono: true },
    { key: 'surname',      label: 'Surname',       value: 'Mokoena',       confidence: 0.96 },
    { key: 'forenames',    label: 'Forenames',     value: 'Lerato',        confidence: 0.94 },
    { key: 'gender',       label: 'Gender',        value: 'Female',        confidence: 0.99 },
    { key: 'dateOfBirth',  label: 'Date of birth', value: '1990-01-01',    confidence: 0.97, mono: true },
    { key: 'countryOfBirth',label:'Country of birth',value:'South Africa', confidence: 0.95 },
    { key: 'citizenship',  label: 'Citizenship',   value: 'South African', confidence: 0.93 },
    { key: 'issueDate',    label: 'Date of issue', value: '2010-04-12',    confidence: 0.89, mono: true },
  ],
  enteredIdNumber: '9001015800087',
  tampering: {
    overall: 94,
    metrics: [
      { key: 'fontConsistency',     label: 'Font consistency',    value: 97 },
      { key: 'layoutAlignment',     label: 'Layout alignment',    value: 96 },
      { key: 'imageQuality',        label: 'Image quality',        value: 88 },
      { key: 'securityFeatures',    label: 'Security features',    value: 95 },
      { key: 'metadataConsistency', label: 'Metadata consistency', value: 94 },
    ],
    flags: [],
  },
};

const TAMPERED_RESULT = {
  ...DEMO_RESULT,
  tampering: {
    overall: 54,
    metrics: [
      { key: 'fontConsistency',     label: 'Font consistency',     value: 41 },
      { key: 'layoutAlignment',     label: 'Layout alignment',     value: 78 },
      { key: 'imageQuality',        label: 'Image quality',        value: 62 },
      { key: 'securityFeatures',    label: 'Security features',    value: 39 },
      { key: 'metadataConsistency', label: 'Metadata consistency', value: 51 },
    ],
    flags: [
      'Font on ID number does not match issuer template (Calibri vs Helvetica Bold).',
      'EXIF metadata was overwritten — original capture device cannot be confirmed.',
      'Edge pixels around the photo show resampling artifacts consistent with image splicing.',
    ],
  },
};

/* ──────────────────────────────────────────────────────────
   Form / upload panel
   ──────────────────────────────────────────────────────── */

function DocTypeSelector({ value, onChange }) {
  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
      {DOC_GROUPS.map(g => (
        <div key={g.label}>
          <div style={{ fontSize: 10, color: '#4F5B67', textTransform: 'uppercase', letterSpacing: '0.06em', fontWeight: 600, marginBottom: 6 }}>{g.label}</div>
          <div style={{ display: 'grid', gridTemplateColumns: g.types.length === 1 ? '1fr' : 'repeat(3, 1fr)', gap: 6 }}>
            {g.types.map(t => (
              <button key={t.value} type="button" onClick={() => onChange(t.value)} style={{
                display: 'flex', alignItems: 'flex-start', gap: 8, padding: '10px 12px',
                background: value === t.value ? 'rgba(0,179,217,0.06)' : '#fff',
                border: `1px solid ${value === t.value ? '#00B3D9' : '#D5DBDB'}`,
                borderRadius: 6, textAlign: 'left', cursor: 'pointer', fontFamily: "'Inter', sans-serif",
                transition: 'all 100ms cubic-bezier(0.4,0,0.2,1)',
              }}>
                <div style={{ width: 24, height: 24, borderRadius: 4, background: value === t.value ? '#00B3D9' : 'rgba(0,179,217,0.10)', display: 'inline-flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0 }}>
                  <PortalIcon name={t.icon} size={12} color={value === t.value ? '#fff' : '#00B3D9'} />
                </div>
                <div style={{ flex: 1, minWidth: 0 }}>
                  <div style={{ fontSize: 12, fontWeight: 600, color: '#1A2024', lineHeight: 1.2 }}>{t.label}</div>
                  {t.hint && <div style={{ fontSize: 10, color: '#4F5B67', marginTop: 2 }}>{t.hint}</div>}
                </div>
              </button>
            ))}
          </div>
        </div>
      ))}
    </div>
  );
}

function DropZone({ file, onFile }) {
  const [drag, setDrag] = useStateD(false);
  return (
    <label
      onDragOver={e => { e.preventDefault(); setDrag(true); }}
      onDragLeave={() => setDrag(false)}
      onDrop={e => { e.preventDefault(); setDrag(false); if (e.dataTransfer.files[0]) onFile(e.dataTransfer.files[0]); }}
      style={{ display: 'block', padding: '24px 18px', border: `2px dashed ${drag ? '#00B3D9' : file ? '#2C974B' : '#CBD5E1'}`, background: drag ? 'rgba(0,179,217,0.05)' : file ? 'rgba(44,151,75,0.04)' : '#F8FAFC', borderRadius: 6, textAlign: 'center', cursor: 'pointer', transition: 'all 100ms cubic-bezier(0.4,0,0.2,1)' }}>
      <input type="file" accept="image/*,application/pdf" style={{ display: 'none' }} onChange={e => e.target.files[0] && onFile(e.target.files[0])} />
      {file ? (
        <>
          <div style={{ width: 36, height: 36, borderRadius: 999, background: '#2C974B', display: 'inline-flex', alignItems: 'center', justifyContent: 'center', marginBottom: 8 }}>
            <PortalIcon name="CheckCircle" size={18} color="#fff" stroke={2.5} />
          </div>
          <div style={{ fontSize: 12, fontWeight: 600, color: '#1A2024' }}>{file.name}</div>
          <div style={{ fontSize: 10, color: '#4F5B67', marginTop: 2 }}>{(file.size / 1024).toFixed(0)} KB · Ready to verify</div>
        </>
      ) : (
        <>
          <div style={{ width: 36, height: 36, borderRadius: 999, background: 'rgba(0,179,217,0.10)', display: 'inline-flex', alignItems: 'center', justifyContent: 'center', marginBottom: 8 }}>
            <PortalIcon name="FileSearch" size={18} color="#00B3D9" />
          </div>
          <div style={{ fontSize: 12, fontWeight: 600, color: '#1A2024' }}>Drop document or click to upload</div>
          <div style={{ fontSize: 10, color: '#4F5B67', marginTop: 4 }}>JPEG, PNG, or PDF · Max 10 MB · Both sides if applicable</div>
        </>
      )}
    </label>
  );
}

function DocForm({ onSubmit, loading }) {
  const [docType, setDocType] = useStateD('id_card');
  const [file, setFile] = useStateD(null);
  const [idNumber, setIdNumber] = useStateD('');
  const handle = (e) => { e.preventDefault(); if (!file || loading) return; onSubmit({ docType, file, idNumber }); };
  return (
    <form onSubmit={handle} style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, overflow: 'hidden', boxShadow: '0 1px 1px 0 rgba(0,28,36,0.30)' }}>
      <div style={{ padding: '14px 18px', borderBottom: '1px solid #e9ebed' }}>
        <div style={{ fontSize: 14, fontWeight: 600 }}>Upload &amp; verify</div>
        <div style={{ fontSize: 11, color: '#4F5B67', marginTop: 2 }}>Choose document type, attach the file, and we'll extract fields + scan for tampering.</div>
      </div>
      <div style={{ padding: '16px 18px', display: 'flex', flexDirection: 'column', gap: 14 }}>
        <DocTypeSelector value={docType} onChange={setDocType} />
        <DropZone file={file} onFile={setFile} />
        {docType === 'id_card' && (
          <div>
            <label style={{ display: 'block', fontSize: 11, fontWeight: 500, color: '#1A2024', marginBottom: 4 }}>ID number to cross-check <span style={{ color: '#4F5B67', fontWeight: 400 }}>(optional)</span></label>
            <input value={idNumber} onChange={e => setIdNumber(e.target.value.replace(/\D/g, '').slice(0,13))} placeholder="13-digit SA ID"
              style={{ width: '100%', padding: '8px 11px', border: '1px solid #D5DBDB', borderRadius: 4, fontSize: 13, fontFamily: "'JetBrains Mono', monospace", outline: 'none' }}
              onFocus={e => { e.currentTarget.style.borderColor = '#00B3D9'; e.currentTarget.style.boxShadow = '0 0 0 3px rgba(0,179,217,0.15)'; }}
              onBlur={e => { e.currentTarget.style.borderColor = '#D5DBDB'; e.currentTarget.style.boxShadow = 'none'; }} />
            <div style={{ fontSize: 10, color: '#4F5B67', marginTop: 4 }}>We'll confirm the extracted ID number matches.</div>
          </div>
        )}
      </div>
      <div style={{ padding: '12px 18px', borderTop: '1px solid #e9ebed', background: '#F8FAFC', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        <span style={{ fontSize: 11, color: '#4F5B67' }}>OCR + tampering analysis · ~3 seconds</span>
        <PortalButton variant="cta" disabled={!file || loading}>{loading ? 'Analysing…' : 'Verify document →'}</PortalButton>
      </div>
    </form>
  );
}

/* ──────────────────────────────────────────────────────────
   Result components
   ──────────────────────────────────────────────────────── */

function DocumentPreview({ docType, fileName }) {
  return (
    <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, overflow: 'hidden' }}>
      <div style={{ padding: '14px 18px', borderBottom: '1px solid #e9ebed' }}>
        <div style={{ fontSize: 13, fontWeight: 600 }}>Document preview</div>
        <div style={{ fontSize: 11, color: '#4F5B67', marginTop: 2, fontFamily: "'JetBrains Mono', monospace" }}>{fileName}</div>
      </div>
      <div style={{ padding: 18, background: '#F2F3F3' }}>
        {/* Stylised SA ID card */}
        <div style={{ background: 'linear-gradient(160deg, #1A2E4B, #1a3a5c)', borderRadius: 6, padding: '12px 14px', color: '#fff', position: 'relative', overflow: 'hidden', minHeight: 140 }}>
          <div style={{ position: 'absolute', top: 8, right: 8, opacity: 0.08 }}>
            <svg width="60" height="68" viewBox="20 25 112 122"><path fill="#fff" d="M76 30 C56 30 26 39 26 42 L26 74 C26 106 50 132 76 142 C102 132 126 106 126 74 L126 42 C126 39 96 30 76 30 Z"/></svg>
          </div>
          <div style={{ fontSize: 7, fontWeight: 700, letterSpacing: '0.06em', marginBottom: 8 }}>REPUBLIC OF SOUTH AFRICA</div>
          <div style={{ display: 'flex', gap: 10 }}>
            <div style={{ width: 50, height: 60, background: 'rgba(255,255,255,0.10)', borderRadius: 3, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
              <PortalIcon name="User" size={28} color="rgba(255,255,255,0.4)" stroke={1.5} />
            </div>
            <div style={{ flex: 1, display: 'flex', flexDirection: 'column', gap: 5 }}>
              <div><div style={{ fontSize: 6, color: 'rgba(255,255,255,0.5)' }}>SURNAME</div><div style={{ fontSize: 9, fontWeight: 600 }}>MOKOENA</div></div>
              <div><div style={{ fontSize: 6, color: 'rgba(255,255,255,0.5)' }}>FORENAMES</div><div style={{ fontSize: 9, fontWeight: 600 }}>LERATO</div></div>
              <div style={{ display: 'flex', gap: 14 }}>
                <div><div style={{ fontSize: 6, color: 'rgba(255,255,255,0.5)' }}>DATE OF BIRTH</div><div style={{ fontSize: 8, fontFamily: "'JetBrains Mono', monospace" }}>01 JAN 1990</div></div>
                <div><div style={{ fontSize: 6, color: 'rgba(255,255,255,0.5)' }}>GENDER</div><div style={{ fontSize: 8 }}>F</div></div>
              </div>
              <div><div style={{ fontSize: 6, color: 'rgba(255,255,255,0.5)' }}>ID NO.</div><div style={{ fontSize: 10, fontFamily: "'JetBrains Mono', monospace", color: '#00B3D9' }}>9001015800087</div></div>
            </div>
          </div>
        </div>
        <div style={{ marginTop: 10, display: 'flex', gap: 6, flexWrap: 'wrap' }}>
          <PortalBadge variant="info" size="sm">{docType === 'id_card' ? 'SA ID Card' : docType}</PortalBadge>
          <PortalBadge variant="neutral" size="sm">Image · 2480 × 1560</PortalBadge>
          <PortalBadge variant="neutral" size="sm">412 KB</PortalBadge>
        </div>
      </div>
    </div>
  );
}

function ExtractedFields({ fields, enteredIdNumber, docType }) {
  const extractedId = fields.find(f => f.key === 'idNumber')?.value;
  const showIdMatch = docType === 'id_card' && enteredIdNumber && extractedId;
  const idMatches = extractedId === enteredIdNumber;
  const confColor = (c) => c >= 0.90 ? '#2C974B' : c >= 0.75 ? '#C28B0B' : '#E23D36';
  return (
    <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, overflow: 'hidden' }}>
      <div style={{ padding: '14px 18px', borderBottom: '1px solid #e9ebed', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <span style={{ fontSize: 14, fontWeight: 600 }}>Extracted fields</span>
        <span style={{ fontSize: 11, color: '#4F5B67' }}>{fields.length} detected</span>
      </div>
      <div style={{ padding: '12px 18px', display: 'flex', flexDirection: 'column', gap: 10 }}>
        {showIdMatch && (
          <div style={{ display: 'flex', alignItems: 'center', gap: 8, padding: '8px 12px', borderRadius: 4, border: `1px solid ${idMatches ? 'rgba(44,151,75,0.3)' : 'rgba(226,61,54,0.3)'}`, background: idMatches ? 'rgba(44,151,75,0.05)' : 'rgba(226,61,54,0.05)', color: idMatches ? '#2C974B' : '#E23D36', fontSize: 12, fontWeight: 500 }}>
            <PortalIcon name={idMatches ? 'CheckCircle' : 'X'} size={14} color="currentColor" />
            {idMatches ? 'Extracted ID number matches the entered ID' : `Extracted ID (${extractedId}) does not match entered ID (${enteredIdNumber})`}
          </div>
        )}
        <div style={{ border: '1px solid #e9ebed', borderRadius: 4 }}>
          {fields.map((f, i) => (
            <div key={f.key} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '8px 14px', borderTop: i > 0 ? '1px solid #f1f5f9' : 'none', fontSize: 12 }}>
              <span style={{ color: '#4F5B67', fontWeight: 500 }}>{f.label}</span>
              <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
                <span style={{ color: '#1A2024', fontFamily: f.mono ? "'JetBrains Mono', monospace" : "'Inter', sans-serif" }}>{f.value}</span>
                <span style={{ fontSize: 10, padding: '1px 7px', borderRadius: 999, background: `${confColor(f.confidence)}1a`, color: confColor(f.confidence), fontWeight: 700, fontFamily: "'JetBrains Mono', monospace" }}>{Math.round(f.confidence * 100)}%</span>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}

function TamperingAnalysis({ data }) {
  const passed = data.overall >= 80;
  const barColor = (v) => v >= 90 ? '#2C974B' : v >= 75 ? '#C28B0B' : '#E23D36';
  return (
    <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, overflow: 'hidden' }}>
      <div style={{ padding: '14px 18px', borderBottom: '1px solid #e9ebed', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
          <PortalIcon name={passed ? 'Shield' : 'X'} size={15} color={passed ? '#2C974B' : '#E23D36'} stroke={2.2} />
          <span style={{ fontSize: 14, fontWeight: 600 }}>Tampering analysis</span>
        </div>
        <PortalBadge variant={passed ? 'success' : 'danger'}>{data.overall}% — {passed ? 'Pass' : 'Fail'}</PortalBadge>
      </div>
      <div style={{ padding: '14px 18px', display: 'flex', flexDirection: 'column', gap: 10 }}>
        {data.metrics.map(m => (
          <div key={m.key}>
            <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: 11, marginBottom: 4 }}>
              <span style={{ color: '#4F5B67', fontWeight: 500 }}>{m.label}</span>
              <span style={{ color: barColor(m.value), fontFamily: "'JetBrains Mono', monospace", fontWeight: 600 }}>{m.value}%</span>
            </div>
            <div style={{ height: 5, background: '#F2F3F3', borderRadius: 3, overflow: 'hidden' }}>
              <div style={{ width: m.value + '%', height: '100%', background: barColor(m.value), transition: 'width 400ms cubic-bezier(0.4,0,0.2,1)' }} />
            </div>
          </div>
        ))}
        {data.flags?.length > 0 && (
          <div style={{ marginTop: 6, display: 'flex', flexDirection: 'column', gap: 6 }}>
            <div style={{ fontSize: 11, fontWeight: 600, color: '#C28B0B', textTransform: 'uppercase', letterSpacing: '0.06em' }}>Flags ({data.flags.length})</div>
            {data.flags.map((f, i) => (
              <div key={i} style={{ display: 'flex', gap: 8, padding: '8px 12px', borderRadius: 4, border: '1px solid rgba(194,139,11,0.3)', background: 'rgba(194,139,11,0.05)', color: '#1A2024', fontSize: 11, lineHeight: 1.5 }}>
                <span style={{ color: '#C28B0B', flexShrink: 0 }}>⚠</span>
                {f}
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}

function DocResult({ status, result }) {
  if (status === 'idle') {
    return (
      <div style={{ background: '#fff', border: '1px dashed #CBD5E1', borderRadius: 8, padding: '40px 24px', textAlign: 'center' }}>
        <div style={{ width: 48, height: 48, borderRadius: 12, background: 'rgba(0,179,217,0.08)', display: 'inline-flex', alignItems: 'center', justifyContent: 'center', marginBottom: 10 }}>
          <PortalIcon name="FileSearch" size={22} color="#00B3D9" />
        </div>
        <div style={{ fontSize: 14, fontWeight: 600 }}>Upload a document to begin</div>
        <div style={{ fontSize: 12, color: '#4F5B67', marginTop: 4, maxWidth: 320, marginLeft: 'auto', marginRight: 'auto' }}>We'll extract every field with confidence scoring and analyse the document for tampering signals.</div>
      </div>
    );
  }
  if (status === 'loading') {
    return (
      <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, padding: '20px 22px', display: 'flex', flexDirection: 'column', gap: 12 }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 8, color: '#0099bb', fontSize: 12, fontWeight: 600 }}>
          <span style={{ width: 8, height: 8, borderRadius: 999, background: '#00B3D9', boxShadow: '0 0 8px #00B3D9', animation: 'docPulse 1.2s ease-in-out infinite' }} />
          Extracting fields & analysing for tampering…
        </div>
        <style>{`@keyframes docPulse{0%,100%{opacity:1}50%{opacity:0.35}}`}</style>
        {[80, 65, 75, 55, 60].map((w, i) => (
          <div key={i} style={{ height: 10, width: w + '%', background: 'linear-gradient(90deg, #F2F3F3, #E4E7E7, #F2F3F3)', backgroundSize: '200% 100%', borderRadius: 3, animation: 'docShimmer 1.5s linear infinite', animationDelay: i * 0.12 + 's' }} />
        ))}
        <style>{`@keyframes docShimmer{0%{background-position:200% 0}100%{background-position:-200% 0}}`}</style>
      </div>
    );
  }
  if (!result) return null;
  const passed = result.tampering.overall >= 80;
  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
      {/* Top summary */}
      <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, overflow: 'hidden' }}>
        <div style={{ display: 'flex', height: 3 }}>
          <div style={{ flex: 3, background: '#E23D36' }} />
          <div style={{ flex: 5, background: '#1A2E4B' }} />
          <div style={{ flex: 2, background: '#00B3D9' }} />
        </div>
        <div style={{ padding: '16px 18px', display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
          <div>
            <div style={{ display: 'flex', alignItems: 'center', gap: 10, marginBottom: 6 }}>
              <span style={{ fontSize: 14, fontWeight: 600 }}>Verification result</span>
              <PortalBadge variant={passed ? 'success' : 'danger'}>{passed ? 'Authentic' : 'Tampered'}</PortalBadge>
            </div>
            <div style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: 11, color: '#00B3D9' }}>{result.id}</div>
          </div>
          <PortalButton variant="secondary" size="sm" icon={<PortalIcon name="Download" size={12} color="#1A2E4B" />}>Export PDF</PortalButton>
        </div>
      </div>
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12, alignItems: 'flex-start' }}>
        <DocumentPreview docType={result.documentType} fileName={result.fileName} />
        <TamperingAnalysis data={result.tampering} />
      </div>
      <ExtractedFields fields={result.fields} enteredIdNumber={result.enteredIdNumber} docType={result.documentType} />
    </div>
  );
}

/* History tab */
function DocHistoryTab() {
  return (
    <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, overflow: 'hidden' }}>
      <div style={{ padding: '12px 18px', borderBottom: '1px solid #e9ebed' }}>
        <div style={{ fontSize: 14, fontWeight: 600 }}>Verification history</div>
        <div style={{ fontSize: 11, color: '#4F5B67', marginTop: 2 }}>{DOC_HISTORY.length} documents · last 7 days</div>
      </div>
      <table style={{ width: '100%', borderCollapse: 'separate', borderSpacing: 0, fontSize: 12 }}>
        <thead style={{ background: '#F2F3F3' }}>
          <tr>{['ID', 'File', 'Type', 'Fields', 'Tampering', 'Status', 'Verified'].map(h => (
            <th key={h} style={{ padding: '8px 14px', textAlign: 'left', fontSize: 10, color: '#4F5B67', textTransform: 'uppercase', letterSpacing: '0.05em', fontWeight: 600, borderBottom: '1px solid #e9ebed' }}>{h}</th>
          ))}</tr>
        </thead>
        <tbody>
          {DOC_HISTORY.map(r => (
            <tr key={r.id} style={{ cursor: 'pointer' }}
                onMouseEnter={e => e.currentTarget.style.background = '#F8FAFC'}
                onMouseLeave={e => e.currentTarget.style.background = 'transparent'}>
              <td style={{ padding: '10px 14px', borderBottom: '1px solid #e9ebed', fontFamily: "'JetBrains Mono', monospace", fontSize: 11, color: '#00B3D9' }}>{r.id}</td>
              <td style={{ padding: '10px 14px', borderBottom: '1px solid #e9ebed' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                  <PortalIcon name="FileSearch" size={14} color="#4F5B67" />
                  <span style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: 11 }}>{r.file}</span>
                </div>
              </td>
              <td style={{ padding: '10px 14px', borderBottom: '1px solid #e9ebed', color: '#4F5B67' }}>{r.typeLabel}</td>
              <td style={{ padding: '10px 14px', borderBottom: '1px solid #e9ebed', fontFamily: "'JetBrains Mono', monospace", color: '#4F5B67' }}>{r.fieldsCount}</td>
              <td style={{ padding: '10px 14px', borderBottom: '1px solid #e9ebed' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                  <div style={{ width: 50, height: 4, background: '#F2F3F3', borderRadius: 2, overflow: 'hidden' }}>
                    <div style={{ width: r.tampering + '%', height: '100%', background: r.tampering >= 90 ? '#2C974B' : r.tampering >= 75 ? '#C28B0B' : '#E23D36' }} />
                  </div>
                  <span style={{ fontFamily: "'JetBrains Mono', monospace", color: r.tampering >= 90 ? '#2C974B' : r.tampering >= 75 ? '#C28B0B' : '#E23D36', fontWeight: 600 }}>{r.tampering}%</span>
                </div>
              </td>
              <td style={{ padding: '10px 14px', borderBottom: '1px solid #e9ebed' }}><PortalBadge variant={r.status}>{r.statusLabel}</PortalBadge></td>
              <td style={{ padding: '10px 14px', borderBottom: '1px solid #e9ebed', color: '#4F5B67', fontSize: 11 }}>{new Date(r.at).toLocaleString('en-ZA', { dateStyle: 'short', timeStyle: 'short' })}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

/* ──────────────────────────────────────────────────────────
   Document Verification page
   ──────────────────────────────────────────────────────── */

function DocumentVerification() {
  const [tab, setTab] = useStateD('new');
  const [status, setStatus] = useStateD('idle');
  const [result, setResult] = useStateD(null);

  const handle = ({ docType, file, idNumber }) => {
    setStatus('loading');
    setTimeout(() => {
      // demo: tampered if filename contains "tamp" or "fake"; clean otherwise
      const tampered = /tamp|fake|fraud/i.test(file.name);
      setResult({ ...(tampered ? TAMPERED_RESULT : DEMO_RESULT), fileName: file.name, enteredIdNumber: idNumber || DEMO_RESULT.enteredIdNumber });
      setStatus('result');
    }, 1500);
  };

  const tabs = [
    { id: 'new',      label: 'Verify document' },
    { id: 'history',  label: 'History', count: DOC_HISTORY.length },
    { id: 'bulk',     label: 'Bulk upload', disabled: true },
    { id: 'autofill', label: 'Auto-fill form', disabled: true },
  ];

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 14 }} data-screen-label="Document Verification">
      <ServicePageHeader
        title="Document Verification"
        subtitle="OCR + tampering analysis for South African identity, business, financial, and proof-of-address documents."
      />
      <ServicePageTabs tabs={tabs} active={tab} onChange={setTab} />

      {tab === 'new' && (
        <div style={{ display: 'grid', gridTemplateColumns: 'minmax(0, 440px) minmax(0, 1fr)', gap: 14, alignItems: 'flex-start' }}>
          <DocForm onSubmit={handle} loading={status === 'loading'} />
          <DocResult status={status} result={result} />
        </div>
      )}
      {tab === 'history' && <DocHistoryTab />}
      {(tab === 'bulk' || tab === 'autofill') && (
        <div style={{ background: '#fff', border: '1px dashed #CBD5E1', borderRadius: 8, padding: '48px 24px', textAlign: 'center' }}>
          <div style={{ fontSize: 14, fontWeight: 600 }}>{tab === 'bulk' ? 'Bulk document verification' : 'Document auto-fill'}</div>
          <div style={{ fontSize: 12, color: '#4F5B67', marginTop: 6 }}>Coming in the next tier of build-out.</div>
        </div>
      )}
    </div>
  );
}

window.DocumentVerification = DocumentVerification;
