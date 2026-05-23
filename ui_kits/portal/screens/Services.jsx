/* global React, PortalIcon, PortalButton, PortalBadge, ServicePageHeader, ServicePageTabs */
const { useState: useStateSv, useMemo: useMemoSv } = React;

/* ──────────────────────────────────────────────────────────
   Tier 2 — Service pages (config-driven from the Sanctions/Doc template)
   Each entry below renders as a full service page:
     - header + tabs + form (left) + result (right) + history
   ──────────────────────────────────────────────────────── */

function FInput(props) {
  return (
    <input {...props}
      style={{ width: '100%', padding: '8px 11px', border: '1px solid #D5DBDB', borderRadius: 4, fontSize: 13, fontFamily: props.mono ? "'JetBrains Mono', monospace" : "'Inter', sans-serif", outline: 'none', ...(props.style || {}) }}
      onFocus={e => { e.currentTarget.style.borderColor = '#00B3D9'; e.currentTarget.style.boxShadow = '0 0 0 3px rgba(0,179,217,0.15)'; }}
      onBlur={e => { e.currentTarget.style.borderColor = '#D5DBDB'; e.currentTarget.style.boxShadow = 'none'; }}
    />
  );
}

function FSelect({ options, ...rest }) {
  return (
    <select {...rest} style={{ width: '100%', padding: '8px 11px', border: '1px solid #D5DBDB', borderRadius: 4, fontSize: 13, fontFamily: "'Inter', sans-serif", background: '#fff', outline: 'none', ...(rest.style || {}) }}>
      {options.map(o => <option key={o.value} value={o.value}>{o.label}</option>)}
    </select>
  );
}

function FField({ label, hint, required, children, span }) {
  return (
    <div style={{ gridColumn: span ? `span ${span}` : 'auto' }}>
      <label style={{ display: 'block', fontSize: 11, fontWeight: 500, color: '#1A2024', marginBottom: 4 }}>
        {label}{required && <span style={{ color: '#E23D36', marginLeft: 2 }}>*</span>}
      </label>
      {children}
      {hint && <div style={{ fontSize: 10, color: '#4F5B67', marginTop: 4, lineHeight: 1.5 }}>{hint}</div>}
    </div>
  );
}

/* ──────────────────────────────────────────────────────────
   SERVICE CONFIGS
   ──────────────────────────────────────────────────────── */

const ZA_BANKS = [
  { value: 'absa', label: 'Absa Bank' },
  { value: 'fnb',  label: 'FNB' },
  { value: 'std',  label: 'Standard Bank' },
  { value: 'nedb', label: 'Nedbank' },
  { value: 'capitec', label: 'Capitec' },
  { value: 'discovery', label: 'Discovery Bank' },
  { value: 'investec', label: 'Investec' },
  { value: 'tymebank', label: 'TymeBank' },
];

const SERVICES = {
  /* ── BANK ACCOUNT VALIDATION ──────────────────────────── */
  bank: {
    title: 'Bank Account Validation',
    subtitle: 'AVS — confirms the account holder, bank, branch, and that the account is open and able to accept debits/credits.',
    category: ['Financial', 'Bank Account Validation'],
    formFields: ({ d, set }) => (
      <>
        <FField label="Account holder name" required><FInput value={d.holder || ''} onChange={e => set({ holder: e.target.value })} placeholder="Full name as on the account" /></FField>
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10 }}>
          <FField label="ID number" required><FInput mono value={d.id || ''} onChange={e => set({ id: e.target.value.replace(/\D/g, '').slice(0, 13) })} /></FField>
          <FField label="Bank" required><FSelect value={d.bank || 'absa'} onChange={e => set({ bank: e.target.value })} options={[{ value: '', label: '— Select —' }, ...ZA_BANKS]} /></FField>
        </div>
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10 }}>
          <FField label="Account number" required><FInput mono value={d.acct || ''} onChange={e => set({ acct: e.target.value.replace(/\D/g, '').slice(0, 11) })} /></FField>
          <FField label="Branch code"><FInput mono value={d.branch || ''} onChange={e => set({ branch: e.target.value.replace(/\D/g, '').slice(0, 6) })} placeholder="universal" /></FField>
        </div>
        <FField label="Account type">
          <FSelect value={d.type || 'cheque'} onChange={e => set({ type: e.target.value })} options={[{value:'cheque',label:'Cheque'},{value:'savings',label:'Savings'},{value:'credit',label:'Credit'},{value:'transmission',label:'Transmission'}]} />
        </FField>
      </>
    ),
    canSubmit: (d) => d.holder && d.id?.length === 13 && d.bank && d.acct?.length >= 9,
    runLabel: 'Verify account →',
    summary: (d) => `R 12.50 · ${d.bank?.toUpperCase()} · result in 4 seconds`,
    result: () => ({
      checks: [
        { label: 'Account exists',                  pass: true },
        { label: 'Account holder name matches ID',  pass: true },
        { label: 'Account accepts debits',          pass: true },
        { label: 'Account accepts credits',         pass: true },
        { label: 'Account open more than 3 months', pass: true },
      ],
      meta: [['Bank confirmed', 'Standard Bank'], ['Branch code', '051001'], ['Account type', 'Cheque'], ['Status', 'OPEN']],
    }),
    history: [
      { id: 'BAV-2026-0411', subject: 'Jane Smith',    bank: 'STD',     status: 'success', label: 'Verified', at: '2026-05-18T14:21Z' },
      { id: 'BAV-2026-0410', subject: 'Acme Corp',     bank: 'NEDB',    status: 'warning', label: 'Name partial', at: '2026-05-18T13:08Z' },
      { id: 'BAV-2026-0409', subject: 'Pieter v.d. M.',bank: 'CAPITEC', status: 'success', label: 'Verified', at: '2026-05-18T11:42Z' },
      { id: 'BAV-2026-0408', subject: 'Lerato Mokoena',bank: 'FNB',     status: 'danger',  label: 'No match', at: '2026-05-17T16:55Z' },
    ],
  },

  /* ── CREDIT CHECK ──────────────────────────────────────── */
  credit: {
    title: 'Credit Check',
    subtitle: 'TransUnion + Experian credit profile, score, and adverse listings — 12 month rolling window.',
    category: ['Financial', 'Credit Check'],
    formFields: ({ d, set }) => (
      <>
        <FField label="ID number" required><FInput mono value={d.id || ''} onChange={e => set({ id: e.target.value.replace(/\D/g, '').slice(0,13) })} /></FField>
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10 }}>
          <FField label="First name"><FInput value={d.first || ''} onChange={e => set({ first: e.target.value })} /></FField>
          <FField label="Last name"><FInput value={d.last || ''} onChange={e => set({ last: e.target.value })} /></FField>
        </div>
        <FField label="Purpose of enquiry" hint="Required by Section 70(2) of the National Credit Act.">
          <FSelect value={d.purpose || 'onboarding'} onChange={e => set({ purpose: e.target.value })} options={[{value:'onboarding',label:'Customer onboarding'},{value:'employment',label:'Employment screening'},{value:'tenant',label:'Tenant screening'},{value:'review',label:'Account review'}]} />
        </FField>
        <FField label="Bureau">
          <FSelect value={d.bureau || 'transunion'} onChange={e => set({ bureau: e.target.value })} options={[{value:'transunion',label:'TransUnion'},{value:'experian',label:'Experian'},{value:'both',label:'Both (composite)'}]} />
        </FField>
      </>
    ),
    canSubmit: (d) => d.id?.length === 13,
    runLabel: 'Run credit check →',
    summary: (d) => `R 85.00 · ${d.bureau || 'transunion'} · result in 8 seconds`,
    result: () => ({
      score: { value: 712, max: 850, tier: 'Good', tone: 'success' },
      meta: [['Last enquiry', '14 days ago'], ['Active accounts', '4'], ['Closed accounts', '12'], ['Total monthly obligation', 'R 8,420']],
      checks: [
        { label: 'No active adverse listings', pass: true },
        { label: 'No judgements last 5 years',  pass: true },
        { label: 'No defaults last 24 months',  pass: true },
        { label: 'Debt-to-income ratio < 0.50', pass: true,  detail: '0.38' },
      ],
    }),
    history: [
      { id: 'CR-2026-0832', subject: 'Mandla T.',     score: 580, tier: 'Below avg', status: 'warning', label: 'Review', at: '2026-05-18T14:33Z' },
      { id: 'CR-2026-0831', subject: 'Jane Smith',    score: 489, tier: 'Poor',     status: 'danger',  label: 'Decline', at: '2026-05-18T13:11Z' },
      { id: 'CR-2026-0830', subject: 'Bob Williams',  score: 712, tier: 'Good',     status: 'success', label: 'Approve', at: '2026-05-18T10:42Z' },
      { id: 'CR-2026-0829', subject: 'Naledi Nkosi',  score: 798, tier: 'Excellent',status: 'success', label: 'Approve', at: '2026-05-17T16:14Z' },
    ],
  },

  /* ── INCOME VERIFICATION ──────────────────────────────── */
  income: {
    title: 'Income Verification',
    subtitle: 'Confirms gross + net income via payslip OCR, employer verification, or 3-month bank-statement parsing.',
    category: ['Financial', 'Income Verification'],
    formFields: ({ d, set }) => (
      <>
        <FField label="ID number" required><FInput mono value={d.id || ''} onChange={e => set({ id: e.target.value.replace(/\D/g, '').slice(0,13) })} /></FField>
        <FField label="Method">
          <FSelect value={d.method || 'payslip'} onChange={e => set({ method: e.target.value })} options={[{value:'payslip',label:'Payslip OCR'},{value:'employer',label:'Employer verification'},{value:'bank',label:'Bank statement analysis'}]} />
        </FField>
        <FField label="Stated monthly gross"><FInput mono value={d.gross || ''} onChange={e => set({ gross: e.target.value })} placeholder="R 25,000" /></FField>
        <FField label="Employer name (optional)"><FInput value={d.employer || ''} onChange={e => set({ employer: e.target.value })} /></FField>
      </>
    ),
    canSubmit: (d) => d.id?.length === 13,
    runLabel: 'Verify income →',
    summary: () => 'R 65.00 · result in 1–3 minutes',
    result: () => ({
      meta: [['Confirmed gross', 'R 24,800'], ['Confirmed net', 'R 18,930'], ['Employer', 'Acme (Pty) Ltd'], ['Stability', '14 months']],
      checks: [
        { label: 'Stated income within ±5% of confirmed', pass: true },
        { label: 'Employer registered with CIPC',         pass: true },
        { label: 'Stable income > 12 months',             pass: true },
        { label: 'No income gaps in last 6 months',       pass: true },
      ],
    }),
    history: [
      { id: 'IV-2026-0203', subject: 'Sipho D.',      method: 'Payslip',  status: 'success', label: 'Confirmed', at: '2026-05-18T11:08Z' },
      { id: 'IV-2026-0202', subject: 'Thandiwe K.',   method: 'Bank',     status: 'warning', label: 'Reduced',   at: '2026-05-17T14:35Z' },
    ],
  },

  /* ── TAX COMPLIANCE ──────────────────────────────────── */
  tax: {
    title: 'Tax Compliance',
    subtitle: 'SARS tax clearance status verification via the TCS PIN process. Valid for 12 months from issue.',
    category: ['Financial', 'Tax Compliance'],
    formFields: ({ d, set }) => (
      <>
        <FField label="Tax reference number" required><FInput mono value={d.tax || ''} onChange={e => set({ tax: e.target.value.replace(/\D/g, '').slice(0,10) })} placeholder="10-digit SARS number" /></FField>
        <FField label="TCS PIN" required hint="Issued by the taxpayer via SARS eFiling. Required for third-party verification."><FInput mono value={d.pin || ''} onChange={e => set({ pin: e.target.value })} /></FField>
        <FField label="Reason">
          <FSelect value={d.reason || 'good_standing'} onChange={e => set({ reason: e.target.value })} options={[{value:'good_standing',label:'Good standing'},{value:'tender',label:'Tender'},{value:'fia',label:'Foreign investment allowance'},{value:'emigration',label:'Emigration'}]} />
        </FField>
      </>
    ),
    canSubmit: (d) => d.tax?.length === 10 && d.pin,
    runLabel: 'Check status →',
    summary: () => 'R 35.00 · real-time SARS API',
    result: () => ({
      meta: [['Tax number', '0123456789'], ['Status', 'COMPLIANT'], ['Issued', '2025-12-04'], ['Expires', '2026-12-04']],
      checks: [
        { label: 'No outstanding returns',     pass: true },
        { label: 'No outstanding debt',        pass: true },
        { label: 'Tax type registrations OK',  pass: true },
      ],
    }),
    history: [
      { id: 'TC-2026-0091', subject: 'Acme Corp',     status: 'success', label: 'Compliant',     at: '2026-05-18T09:12Z' },
      { id: 'TC-2026-0090', subject: 'Pyongyang T.',  status: 'danger',  label: 'Non-compliant', at: '2026-05-17T14:42Z' },
    ],
  },

  /* ── COMPANY & DIRECTORS ──────────────────────────────── */
  company: {
    title: 'Company & Directors',
    subtitle: 'CIPC business registry lookup — company status, registered address, directors, and ultimate beneficial owners.',
    category: ['Business & Compliance', 'Corporate'],
    formFields: ({ d, set }) => (
      <>
        <FField label="Registration number" required hint="CIPC company registration (e.g. 2018/451288/07)"><FInput mono value={d.reg || ''} onChange={e => set({ reg: e.target.value })} /></FField>
        <FField label="Company name (optional)"><FInput value={d.name || ''} onChange={e => set({ name: e.target.value })} /></FField>
        <FField label="Include">
          <div style={{ display: 'flex', gap: 14, flexWrap: 'wrap', fontSize: 12 }}>
            {['Directors', 'UBOs', 'Annual returns', 'Audit history'].map(opt => (
              <label key={opt} style={{ display: 'inline-flex', alignItems: 'center', gap: 6, cursor: 'pointer' }}>
                <input type="checkbox" defaultChecked style={{ accentColor: '#00B3D9' }} />{opt}
              </label>
            ))}
          </div>
        </FField>
      </>
    ),
    canSubmit: (d) => !!d.reg,
    runLabel: 'Look up company →',
    summary: () => 'R 95.00 · CIPC real-time',
    result: () => ({
      meta: [['Company', 'Acme Operations (Pty) Ltd'], ['Reg.', '2018/451288/07'], ['Status', 'ACTIVE'], ['Type', 'Private Company']],
      checks: [
        { label: 'Company in good standing', pass: true },
        { label: 'Annual returns up to date', pass: true },
        { label: 'No business rescue / liquidation', pass: true },
      ],
      list: { title: 'Directors (4)', items: [
        { name: 'Arthur Manena',   role: 'Director', appointed: '2018-09-12', id: '8211056500088' },
        { name: 'Naledi Nkosi',    role: 'Director', appointed: '2020-03-04', id: '9304117500084' },
        { name: 'Sipho Dlamini',   role: 'Director', appointed: '2022-11-30', id: '8211056500088' },
        { name: 'Lerato Mokoena',  role: 'Director', appointed: '2024-06-14', id: '9006120800086' },
      ]},
    }),
    history: [
      { id: 'CO-2026-0124', subject: 'Acme Operations',  status: 'success', label: 'Active',     at: '2026-05-18T13:42Z' },
      { id: 'CO-2026-0123', subject: 'Pyongyang Trading',status: 'warning', label: 'Deregistered', at: '2026-05-17T11:20Z' },
    ],
  },

  /* ── EMPLOYMENT ──────────────────────────────────────── */
  employment: {
    title: 'Employment Verification',
    subtitle: 'Confirms employment history, dates, and roles via direct employer outreach + payroll cross-reference.',
    category: ['Business & Compliance', 'Employment'],
    formFields: ({ d, set }) => (
      <>
        <FField label="ID number" required><FInput mono value={d.id || ''} onChange={e => set({ id: e.target.value.replace(/\D/g, '').slice(0,13) })} /></FField>
        <FField label="Years to verify"><FSelect value={d.years || '5'} onChange={e => set({ years: e.target.value })} options={[{value:'3',label:'Last 3 years'},{value:'5',label:'Last 5 years'},{value:'10',label:'Last 10 years'}]} /></FField>
        <FField label="Include reference checks?">
          <FSelect value={d.refs || 'yes'} onChange={e => set({ refs: e.target.value })} options={[{value:'yes',label:'Yes — supervisor & HR'},{value:'no',label:'No — dates only'}]} />
        </FField>
      </>
    ),
    canSubmit: (d) => d.id?.length === 13,
    runLabel: 'Verify employment →',
    summary: () => 'R 180.00 · 1–3 business days',
    result: () => ({
      checks: [
        { label: 'All employers confirmed',           pass: true },
        { label: 'Dates match claimed history',       pass: true },
        { label: 'No undisclosed gaps > 90 days',     pass: true },
        { label: 'Supervisor references positive',    pass: true },
      ],
      list: { title: 'Employment history (3)', items: [
        { name: 'Acme Operations (Pty) Ltd', role: 'Senior Compliance Officer', appointed: '2022-04 → present' },
        { name: 'Standard Bank',              role: 'Compliance Analyst',         appointed: '2019-06 → 2022-03' },
        { name: 'Deloitte ZA',                role: 'Audit Associate',            appointed: '2016-01 → 2019-05' },
      ]},
    }),
    history: [
      { id: 'EM-2026-0067', subject: 'Naledi Nkosi', status: 'success', label: 'Confirmed', at: '2026-05-18T10:22Z' },
    ],
  },

  /* ── QUALIFICATION ────────────────────────────────────── */
  qualification: {
    title: 'Qualification Verification',
    subtitle: 'SAQA + Umalusi + institution-direct verification of degrees, diplomas, matric certificates, and professional bodies.',
    category: ['Business & Compliance', 'Qualification'],
    formFields: ({ d, set }) => (
      <>
        <FField label="ID number" required><FInput mono value={d.id || ''} onChange={e => set({ id: e.target.value.replace(/\D/g, '').slice(0,13) })} /></FField>
        <FField label="Qualification level">
          <FSelect value={d.level || 'degree'} onChange={e => set({ level: e.target.value })} options={[{value:'matric',label:'Matric (NSC)'},{value:'higher_cert',label:'Higher certificate'},{value:'diploma',label:'Diploma'},{value:'degree',label:'Degree'},{value:'postgrad',label:'Post-graduate'}]} />
        </FField>
        <FField label="Institution"><FInput value={d.institution || ''} onChange={e => set({ institution: e.target.value })} placeholder="e.g. University of Cape Town" /></FField>
        <FField label="Year completed"><FInput mono value={d.year || ''} onChange={e => set({ year: e.target.value.replace(/\D/g, '').slice(0, 4) })} placeholder="2018" /></FField>
      </>
    ),
    canSubmit: (d) => d.id?.length === 13,
    runLabel: 'Verify qualification →',
    summary: () => 'R 120.00 · 1–5 business days',
    result: () => ({
      meta: [['Qualification', 'BCom (Hons) Accounting'], ['Institution', 'University of Cape Town'], ['Year', '2018'], ['SAQA ID', '15523']],
      checks: [
        { label: 'Found on national learners records database', pass: true },
        { label: 'Qualification accredited',                    pass: true },
        { label: 'Subjects + credits match record',             pass: true },
      ],
    }),
    history: [
      { id: 'QV-2026-0188', subject: 'Arthur M.', status: 'success', label: 'Verified', at: '2026-05-18T09:50Z' },
      { id: 'QV-2026-0187', subject: 'Pieter v.d. M.', status: 'danger', label: 'Not found', at: '2026-05-17T11:08Z' },
    ],
  },

  /* ── NEGATIVE NEWS ────────────────────────────────────── */
  negnews: {
    title: 'Negative News Screening',
    subtitle: 'Adverse-media search across 16,000+ South African and international sources. Filtered by topic and recency.',
    category: ['Screening', 'Negative News'],
    formFields: ({ d, set }) => (
      <>
        <FField label="Subject name" required><FInput value={d.name || ''} onChange={e => set({ name: e.target.value })} /></FField>
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10 }}>
          <FField label="Date range"><FSelect value={d.range || '12m'} onChange={e => set({ range: e.target.value })} options={[{value:'6m',label:'Last 6 months'},{value:'12m',label:'Last 12 months'},{value:'24m',label:'Last 24 months'},{value:'5y',label:'Last 5 years'}]} /></FField>
          <FField label="Language"><FSelect value={d.lang || 'all'} onChange={e => set({ lang: e.target.value })} options={[{value:'en',label:'English'},{value:'af',label:'Afrikaans'},{value:'all',label:'All'}]} /></FField>
        </div>
        <FField label="Topics">
          <div style={{ display: 'flex', gap: 12, flexWrap: 'wrap', fontSize: 12 }}>
            {['Fraud', 'Bribery', 'Money laundering', 'Tax evasion', 'Litigation', 'Politics'].map(t => (
              <label key={t} style={{ display: 'inline-flex', gap: 5, alignItems: 'center', cursor: 'pointer' }}>
                <input type="checkbox" defaultChecked style={{ accentColor: '#00B3D9' }} />{t}
              </label>
            ))}
          </div>
        </FField>
      </>
    ),
    canSubmit: (d) => !!d.name,
    runLabel: 'Search media →',
    summary: () => 'R 75.00 · result in 10 seconds',
    result: () => ({
      meta: [['Articles scanned', '2,418'], ['Matches found', '3'], ['Highest severity', 'Medium'], ['Sources covered', '16,420']],
      list: { title: 'Articles (3)', items: [
        { name: 'Daily Maverick', role: 'Director quoted in tax probe inquiry', appointed: '2025-08-14' },
        { name: 'BusinessTech',   role: 'Mentioned in unrelated litigation',     appointed: '2024-11-22' },
        { name: 'News24',         role: 'Industry award recognition',            appointed: '2024-02-09' },
      ]},
    }),
    history: [
      { id: 'NN-2026-0312', subject: 'Mandla T.', status: 'warning', label: '3 hits', at: '2026-05-18T15:42Z' },
    ],
  },

  /* ── FRAUD WATCHLIST ──────────────────────────────────── */
  fraud: {
    title: 'Fraud Watchlist',
    subtitle: 'Internal blacklist of confirmed fraudsters shared across VeriGate partners. Opt-in; contributions earn credits.',
    category: ['Screening', 'Fraud Watchlist'],
    formFields: ({ d, set }) => (
      <>
        <FField label="ID number"><FInput mono value={d.id || ''} onChange={e => set({ id: e.target.value.replace(/\D/g, '').slice(0,13) })} /></FField>
        <FField label="Email address"><FInput value={d.email || ''} onChange={e => set({ email: e.target.value })} /></FField>
        <FField label="Phone number"><FInput mono value={d.phone || ''} onChange={e => set({ phone: e.target.value })} /></FField>
        <FField label="Device fingerprint (optional)" hint="If captured during onboarding"><FInput mono value={d.device || ''} onChange={e => set({ device: e.target.value })} /></FField>
      </>
    ),
    canSubmit: (d) => d.id || d.email || d.phone,
    runLabel: 'Check watchlist →',
    summary: () => 'R 18.00 · real-time',
    result: () => ({
      checks: [
        { label: 'ID number not on watchlist',     pass: true },
        { label: 'Email not on watchlist',         pass: true },
        { label: 'Phone not on watchlist',         pass: true },
        { label: 'Device fingerprint not flagged', pass: true },
      ],
      meta: [['Last DB refresh', '2 hours ago'], ['Partner contributions', '4,820'], ['Match score', '0/100']],
    }),
    history: [
      { id: 'FW-2026-1241', subject: 'b...@yahoo.com', status: 'danger', label: 'Match',    at: '2026-05-18T14:08Z' },
      { id: 'FW-2026-1240', subject: '+27 82 xxx',     status: 'success',label: 'Clear',    at: '2026-05-18T13:42Z' },
    ],
  },

  /* ── VAT VENDOR SEARCH ────────────────────────────────── */
  vat: {
    title: 'VAT Vendor Search',
    subtitle: 'SARS VAT vendor validation — confirm a counterparty is a registered VAT vendor before reclaiming input VAT.',
    category: ['Business & Compliance', 'VAT Vendor Search'],
    formFields: ({ d, set }) => (
      <>
        <FField label="VAT number" required><FInput mono value={d.vat || ''} onChange={e => set({ vat: e.target.value.replace(/\D/g, '').slice(0,10) })} placeholder="10-digit VAT number" /></FField>
        <FField label="Trading name (optional)"><FInput value={d.name || ''} onChange={e => set({ name: e.target.value })} /></FField>
      </>
    ),
    canSubmit: (d) => d.vat?.length === 10,
    runLabel: 'Look up VAT vendor →',
    summary: () => 'R 8.00 · real-time SARS',
    result: () => ({
      meta: [['VAT number', '4012345678'], ['Trading name', 'Acme Operations (Pty) Ltd'], ['Status', 'ACTIVE'], ['Registered', '2018-09-12']],
      checks: [
        { label: 'VAT number valid',          pass: true },
        { label: 'Vendor in good standing',   pass: true },
        { label: 'Currently registered',      pass: true },
      ],
    }),
    history: [
      { id: 'VT-2026-0512', subject: '4012345678', status: 'success', label: 'Active',   at: '2026-05-18T11:28Z' },
      { id: 'VT-2026-0511', subject: '4099887766', status: 'danger',  label: 'Cancelled', at: '2026-05-17T15:10Z' },
    ],
  },
};

window.SERVICE_CONFIGS = SERVICES;

/* ──────────────────────────────────────────────────────────
   Reusable result panel
   ──────────────────────────────────────────────────────── */

function CheckRow({ check }) {
  return (
    <div style={{ display: 'flex', alignItems: 'center', gap: 10, padding: '8px 14px', borderBottom: '1px solid #f1f5f9', fontSize: 12 }}>
      <span style={{ color: check.pass ? '#2C974B' : '#E23D36', fontSize: 14, fontWeight: 700 }}>{check.pass ? '✓' : '✗'}</span>
      <span style={{ flex: 1, color: '#1A2024' }}>{check.label}</span>
      {check.detail && <span style={{ fontFamily: "'JetBrains Mono', monospace", color: '#4F5B67', fontSize: 11 }}>{check.detail}</span>}
    </div>
  );
}

function ScoreGauge({ value, max, tier, tone }) {
  const pct = (value / max) * 100;
  const color = { success: '#2C974B', warning: '#C28B0B', danger: '#E23D36' }[tone] || '#1A2E4B';
  return (
    <div style={{ display: 'flex', alignItems: 'center', gap: 14 }}>
      <div style={{ width: 70, height: 70, position: 'relative' }}>
        <svg width={70} height={70} style={{ transform: 'rotate(-90deg)' }}>
          <circle cx="35" cy="35" r="30" fill="none" stroke="#F2F3F3" strokeWidth="6" />
          <circle cx="35" cy="35" r="30" fill="none" stroke={color} strokeWidth="6" strokeLinecap="round" strokeDasharray={`${(pct/100) * (2*Math.PI*30)} ${2*Math.PI*30}`} />
        </svg>
        <div style={{ position: 'absolute', inset: 0, display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: 18, fontWeight: 700, color: '#1A2024' }}>{value}</div>
      </div>
      <div>
        <div style={{ fontSize: 10, color: '#4F5B67', textTransform: 'uppercase', letterSpacing: '0.06em', fontWeight: 600 }}>Credit score</div>
        <div style={{ fontSize: 16, fontWeight: 600, color, marginTop: 2 }}>{tier}</div>
        <div style={{ fontSize: 11, color: '#4F5B67' }}>{value} / {max}</div>
      </div>
    </div>
  );
}

function ResultPanel({ status, result, runId }) {
  if (status === 'idle') {
    return (
      <div style={{ background: '#fff', border: '1px dashed #CBD5E1', borderRadius: 8, padding: '40px 24px', textAlign: 'center' }}>
        <div style={{ width: 48, height: 48, borderRadius: 12, background: 'rgba(0,179,217,0.08)', display: 'inline-flex', alignItems: 'center', justifyContent: 'center', marginBottom: 10 }}>
          <PortalIcon name="FileSearch" size={22} color="#00B3D9" />
        </div>
        <div style={{ fontSize: 14, fontWeight: 600, color: '#1A2024' }}>No results yet</div>
        <div style={{ fontSize: 12, color: '#4F5B67', marginTop: 4, maxWidth: 320, marginLeft: 'auto', marginRight: 'auto' }}>Enter the subject details on the left and submit to run this check.</div>
      </div>
    );
  }
  if (status === 'loading') {
    return (
      <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, padding: '20px 22px', display: 'flex', flexDirection: 'column', gap: 12 }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 8, color: '#0099bb', fontSize: 12, fontWeight: 600 }}>
          <span style={{ width: 8, height: 8, borderRadius: 999, background: '#00B3D9', boxShadow: '0 0 8px #00B3D9', animation: 'svcPulse 1.2s ease-in-out infinite' }} />
          Running verification…
        </div>
        <style>{`@keyframes svcPulse{0%,100%{opacity:1}50%{opacity:0.35}} @keyframes svcShimmer{0%{background-position:200% 0}100%{background-position:-200% 0}}`}</style>
        {[80, 60, 70, 50].map((w, i) => (
          <div key={i} style={{ height: 10, width: w + '%', background: 'linear-gradient(90deg, #F2F3F3, #E4E7E7, #F2F3F3)', backgroundSize: '200% 100%', borderRadius: 3, animation: `svcShimmer 1.5s linear infinite`, animationDelay: i * 0.12 + 's' }} />
        ))}
      </div>
    );
  }
  if (!result) return null;
  const allPass = !result.checks || result.checks.every(c => c.pass);
  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
      <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, overflow: 'hidden' }}>
        <div style={{ display: 'flex', height: 3 }}>
          <div style={{ flex: 3, background: '#E23D36' }} />
          <div style={{ flex: 5, background: '#1A2E4B' }} />
          <div style={{ flex: 2, background: '#00B3D9' }} />
        </div>
        <div style={{ padding: '14px 18px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <div>
            <div style={{ display: 'flex', alignItems: 'center', gap: 10, marginBottom: 4 }}>
              <span style={{ fontSize: 14, fontWeight: 600 }}>Verification result</span>
              <PortalBadge variant={allPass ? 'success' : 'warning'}>{allPass ? 'Pass' : 'Review'}</PortalBadge>
            </div>
            <div style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: 11, color: '#00B3D9' }}>{runId}</div>
          </div>
          <PortalButton variant="secondary" size="sm" icon={<PortalIcon name="Download" size={12} color="#1A2E4B" />}>Export PDF</PortalButton>
        </div>
        {result.score && (
          <div style={{ padding: '14px 18px', borderTop: '1px solid #e9ebed' }}>
            <ScoreGauge {...result.score} />
          </div>
        )}
        {result.meta && (
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(2, 1fr)', borderTop: '1px solid #e9ebed' }}>
            {result.meta.map(([k, v], i) => (
              <div key={k} style={{ padding: '10px 16px', borderTop: i >= 2 ? '1px solid #f1f5f9' : 'none', borderLeft: i % 2 === 1 ? '1px solid #e9ebed' : 'none' }}>
                <div style={{ fontSize: 10, color: '#4F5B67', textTransform: 'uppercase', letterSpacing: '0.06em', fontWeight: 600 }}>{k}</div>
                <div style={{ fontSize: 13, color: '#1A2024', marginTop: 2, fontWeight: 500 }}>{v}</div>
              </div>
            ))}
          </div>
        )}
      </div>
      {result.checks && (
        <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, overflow: 'hidden' }}>
          <div style={{ padding: '10px 18px', borderBottom: '1px solid #e9ebed', fontSize: 13, fontWeight: 600 }}>Checks performed</div>
          {result.checks.map((c, i) => <CheckRow key={i} check={c} />)}
        </div>
      )}
      {result.list && (
        <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, overflow: 'hidden' }}>
          <div style={{ padding: '10px 18px', borderBottom: '1px solid #e9ebed', fontSize: 13, fontWeight: 600 }}>{result.list.title}</div>
          {result.list.items.map((it, i) => (
            <div key={i} style={{ display: 'flex', alignItems: 'center', gap: 12, padding: '10px 18px', borderBottom: i < result.list.items.length - 1 ? '1px solid #f1f5f9' : 'none' }}>
              <div style={{ width: 28, height: 28, borderRadius: 999, background: '#1A2E4B', color: '#fff', display: 'inline-flex', alignItems: 'center', justifyContent: 'center', fontSize: 10, fontWeight: 600 }}>{(it.name || '?').split(' ').map(s => s[0]).slice(0,2).join('')}</div>
              <div style={{ flex: 1 }}>
                <div style={{ fontSize: 12, fontWeight: 600 }}>{it.name}</div>
                <div style={{ fontSize: 11, color: '#4F5B67' }}>{it.role}</div>
              </div>
              <span style={{ fontSize: 11, color: '#4F5B67', fontFamily: "'JetBrains Mono', monospace" }}>{it.appointed || it.id}</span>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

/* ──────────────────────────────────────────────────────────
   Generic service page renderer
   ──────────────────────────────────────────────────────── */

function ServicePage({ configKey }) {
  const cfg = SERVICES[configKey];
  if (!cfg) return null;
  const [tab, setTab] = useStateSv('new');
  const [data, setData] = useStateSv({});
  const [status, setStatus] = useStateSv('idle');
  const [result, setResult] = useStateSv(null);
  const set = (partial) => setData(d => ({ ...d, ...partial }));

  const runId = useMemoSv(() => cfg.title.split(' ')[0].toUpperCase().slice(0, 3) + '-2026-' + String(Math.floor(Math.random() * 8000) + 1000), [cfg]);

  const tabs = [
    { id: 'new',     label: cfg.runLabel.replace(' →', '').replace('Verify ', 'New ').replace('Run ', 'New ').replace('Check ', 'New ').replace('Look up ', 'New ').replace('Search ', 'New ') },
    { id: 'history', label: 'History', count: cfg.history?.length },
    { id: 'bulk',    label: 'Bulk upload', disabled: true },
  ];

  const submit = (e) => {
    e.preventDefault();
    setStatus('loading');
    setTimeout(() => { setResult(cfg.result(data)); setStatus('result'); }, 1100);
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 14 }} data-screen-label={cfg.title}>
      <ServicePageHeader title={cfg.title} subtitle={cfg.subtitle} />
      <ServicePageTabs tabs={tabs} active={tab} onChange={setTab} />

      {tab === 'new' && (
        <div style={{ display: 'grid', gridTemplateColumns: 'minmax(0, 440px) minmax(0, 1fr)', gap: 14, alignItems: 'flex-start' }}>
          <form onSubmit={submit} style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, overflow: 'hidden' }}>
            <div style={{ padding: '14px 18px', borderBottom: '1px solid #e9ebed' }}>
              <div style={{ fontSize: 14, fontWeight: 600 }}>Subject details</div>
              <div style={{ fontSize: 11, color: '#4F5B67', marginTop: 2 }}>Required fields are marked with <span style={{ color: '#E23D36' }}>*</span>.</div>
            </div>
            <div style={{ padding: '16px 18px', display: 'flex', flexDirection: 'column', gap: 12 }}>
              {cfg.formFields({ d: data, set })}
            </div>
            <div style={{ padding: '12px 18px', borderTop: '1px solid #e9ebed', background: '#F8FAFC', display: 'flex', alignItems: 'center', justifyContent: 'space-between', gap: 8 }}>
              <span style={{ fontSize: 11, color: '#4F5B67' }}>{cfg.summary(data)}</span>
              <PortalButton variant="cta" disabled={!cfg.canSubmit(data) || status === 'loading'}>{status === 'loading' ? 'Running…' : cfg.runLabel}</PortalButton>
            </div>
          </form>
          <ResultPanel status={status} result={result} runId={runId} />
        </div>
      )}

      {tab === 'history' && (
        <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, overflow: 'hidden' }}>
          <div style={{ padding: '12px 18px', borderBottom: '1px solid #e9ebed' }}>
            <div style={{ fontSize: 14, fontWeight: 600 }}>History</div>
            <div style={{ fontSize: 11, color: '#4F5B67', marginTop: 2 }}>{cfg.history.length} past checks</div>
          </div>
          <table style={{ width: '100%', borderCollapse: 'separate', borderSpacing: 0, fontSize: 12 }}>
            <thead style={{ background: '#F2F3F3' }}>
              <tr>{['ID', 'Subject', 'Status', 'Result', 'When'].map(h => (
                <th key={h} style={{ padding: '8px 14px', textAlign: 'left', fontSize: 10, color: '#4F5B67', textTransform: 'uppercase', letterSpacing: '0.05em', fontWeight: 600, borderBottom: '1px solid #e9ebed' }}>{h}</th>
              ))}</tr>
            </thead>
            <tbody>
              {cfg.history.map(r => (
                <tr key={r.id} style={{ cursor: 'pointer' }} onMouseEnter={e => e.currentTarget.style.background = '#F8FAFC'} onMouseLeave={e => e.currentTarget.style.background = 'transparent'}>
                  <td style={{ padding: '10px 14px', borderBottom: '1px solid #e9ebed', fontFamily: "'JetBrains Mono', monospace", fontSize: 11, color: '#00B3D9' }}>{r.id}</td>
                  <td style={{ padding: '10px 14px', borderBottom: '1px solid #e9ebed' }}>{r.subject}</td>
                  <td style={{ padding: '10px 14px', borderBottom: '1px solid #e9ebed' }}><PortalBadge variant={r.status}>{r.label}</PortalBadge></td>
                  <td style={{ padding: '10px 14px', borderBottom: '1px solid #e9ebed', color: '#4F5B67', fontFamily: "'JetBrains Mono', monospace" }}>{r.score ? r.score + ' · ' + r.tier : r.bank || r.method || '—'}</td>
                  <td style={{ padding: '10px 14px', borderBottom: '1px solid #e9ebed', color: '#4F5B67', fontSize: 11 }}>{new Date(r.at).toLocaleString('en-ZA', { dateStyle: 'short', timeStyle: 'short' })}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {tab === 'bulk' && (
        <div style={{ background: '#fff', border: '1px dashed #CBD5E1', borderRadius: 8, padding: '48px 24px', textAlign: 'center' }}>
          <div style={{ fontSize: 14, fontWeight: 600 }}>Bulk {cfg.title.toLowerCase()}</div>
          <div style={{ fontSize: 12, color: '#4F5B67', marginTop: 6 }}>CSV upload pattern — same as Bulk Identity. Per-service bulk lands in Tier 3.</div>
        </div>
      )}
    </div>
  );
}

window.ServicePage = ServicePage;
