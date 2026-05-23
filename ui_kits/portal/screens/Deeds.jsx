/* global React, PortalIcon, PortalButton, PortalBadge, ServicePageHeader, ServicePageTabs */
const { useState: useStateDe, useMemo: useMemoDe } = React;

/* ──────────────────────────────────────────────────────────
   Tier 3 — Deeds Registry family (4 screens)
     · property-ownership   /services/property-ownership
     · property-conversion  /services/property-conversion
     · property-valuation   /services/property-valuation
     · deeds-map            /services/deeds-map
   All in /Business & Compliance/ sidebar group.
   ──────────────────────────────────────────────────────── */

function DField({ label, hint, required, children }) {
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

function DInput(props) {
  return (
    <input {...props}
      style={{ width: '100%', padding: '8px 11px', border: '1px solid #D5DBDB', borderRadius: 4, fontSize: 13, fontFamily: props.mono ? "'JetBrains Mono', monospace" : "'Inter', sans-serif", outline: 'none', ...(props.style || {}) }}
      onFocus={e => { e.currentTarget.style.borderColor = '#00B3D9'; e.currentTarget.style.boxShadow = '0 0 0 3px rgba(0,179,217,0.15)'; }}
      onBlur={e => { e.currentTarget.style.borderColor = '#D5DBDB'; e.currentTarget.style.boxShadow = 'none'; }}
    />
  );
}

function DSelect({ options, ...rest }) {
  return (
    <select {...rest} style={{ width: '100%', padding: '8px 11px', border: '1px solid #D5DBDB', borderRadius: 4, fontSize: 13, fontFamily: "'Inter', sans-serif", background: '#fff', outline: 'none' }}>
      {options.map(o => <option key={o.value} value={o.value}>{o.label}</option>)}
    </select>
  );
}

/* Sample property data */
const SAMPLE_PROPERTIES = [
  { erf: 'ERF 12453', address: '14 Cinnebar Street, Table View, 7441',  size: '612 m²',  type: 'Residential',  bond: 'R 1,240,000', registered: '2018-04-12', municipal: 'R 2,400,000' },
  { erf: 'ERF 8821',  address: '88 Beach Road, Sea Point, 8005',         size: '420 m²',  type: 'Sectional title', bond: 'Bond settled', registered: '2014-11-04', municipal: 'R 3,800,000' },
  { erf: 'ERF 24017', address: '3 Vineyard Avenue, Constantia, 7806',    size: '4,210 m²', type: 'Residential', bond: 'R 4,200,000', registered: '2022-09-30', municipal: 'R 8,500,000' },
];

const SA_PROVINCES = [
  { value: 'WC', label: 'Western Cape' },
  { value: 'GP', label: 'Gauteng' },
  { value: 'KZN', label: 'KwaZulu-Natal' },
  { value: 'EC', label: 'Eastern Cape' },
  { value: 'FS', label: 'Free State' },
  { value: 'LP', label: 'Limpopo' },
  { value: 'MP', label: 'Mpumalanga' },
  { value: 'NC', label: 'Northern Cape' },
  { value: 'NW', label: 'North West' },
];

/* ──────────────────────────────────────────────────────────
   Property Card — shared component
   ──────────────────────────────────────────────────────── */

function PropertyCard({ p }) {
  return (
    <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, padding: '14px 18px', display: 'flex', gap: 14, alignItems: 'flex-start' }}>
      <div style={{ width: 56, height: 56, borderRadius: 6, background: 'linear-gradient(160deg, #1A2E4B, #1a3a5c)', display: 'inline-flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0 }}>
        <PortalIcon name="Home" size={24} color="#00B3D9" />
      </div>
      <div style={{ flex: 1 }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 4 }}>
          <span style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: 11, color: '#00B3D9' }}>{p.erf}</span>
          <PortalBadge variant="info" size="sm">{p.type}</PortalBadge>
        </div>
        <div style={{ fontSize: 13, fontWeight: 600, color: '#1A2024' }}>{p.address}</div>
        <div style={{ display: 'flex', gap: 14, marginTop: 8, fontSize: 11, color: '#4F5B67', flexWrap: 'wrap' }}>
          <span><b style={{ color: '#1A2024' }}>Size:</b> {p.size}</span>
          <span><b style={{ color: '#1A2024' }}>Municipal value:</b> {p.municipal}</span>
          <span><b style={{ color: '#1A2024' }}>Bond:</b> {p.bond}</span>
          <span><b style={{ color: '#1A2024' }}>Registered:</b> {p.registered}</span>
        </div>
      </div>
      <PortalButton variant="ghost" size="sm">Details →</PortalButton>
    </div>
  );
}

/* ──────────────────────────────────────────────────────────
   1) Deeds Registry — search by ID or owner name
   ──────────────────────────────────────────────────────── */

function PropertyOwnership() {
  const [tab, setTab] = useStateDe('new');
  const [id, setId] = useStateDe('');
  const [status, setStatus] = useStateDe('idle');
  const submit = (e) => {
    e.preventDefault();
    setStatus('loading');
    setTimeout(() => setStatus('result'), 1100);
  };
  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 14 }} data-screen-label="Deeds Registry">
      <ServicePageHeader title="Deeds Registry" subtitle="South African deeds registry lookup. Find all properties owned by a person or entity." />
      <ServicePageTabs tabs={[{ id: 'new', label: 'Property search' }, { id: 'history', label: 'History', count: 5 }, { id: 'bulk', label: 'Bulk lookup', disabled: true }]} active={tab} onChange={setTab} />

      {tab === 'new' && (
        <div style={{ display: 'grid', gridTemplateColumns: 'minmax(0, 440px) minmax(0, 1fr)', gap: 14, alignItems: 'flex-start' }}>
          <form onSubmit={submit} style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, overflow: 'hidden' }}>
            <div style={{ padding: '14px 18px', borderBottom: '1px solid #e9ebed' }}>
              <div style={{ fontSize: 14, fontWeight: 600 }}>Search by owner</div>
              <div style={{ fontSize: 11, color: '#4F5B67', marginTop: 2 }}>Returns all properties registered to this ID or registration number.</div>
            </div>
            <div style={{ padding: '16px 18px', display: 'flex', flexDirection: 'column', gap: 12 }}>
              <DField label="ID or registration number" required hint="13-digit SA ID for individuals, CIPC reg. for companies."><DInput mono value={id} onChange={e => setId(e.target.value)} /></DField>
              <DField label="Search scope">
                <DSelect value="all" onChange={() => {}} options={[{value:'all',label:'All provinces'},...SA_PROVINCES]} />
              </DField>
              <DField label="Include historical transfers">
                <DSelect value="yes" onChange={() => {}} options={[{value:'yes',label:'Yes — show 10 years history'},{value:'no',label:'No — current ownership only'}]} />
              </DField>
            </div>
            <div style={{ padding: '12px 18px', borderTop: '1px solid #e9ebed', background: '#F8FAFC', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <span style={{ fontSize: 11, color: '#4F5B67' }}>R 95.00 · real-time from DRDLR</span>
              <PortalButton variant="cta" disabled={!id || status === 'loading'}>{status === 'loading' ? 'Searching…' : 'Search deeds →'}</PortalButton>
            </div>
          </form>

          {status === 'idle' && (
            <div style={{ background: '#fff', border: '1px dashed #CBD5E1', borderRadius: 8, padding: '40px 24px', textAlign: 'center' }}>
              <div style={{ width: 48, height: 48, borderRadius: 12, background: 'rgba(0,179,217,0.08)', display: 'inline-flex', alignItems: 'center', justifyContent: 'center', marginBottom: 10 }}>
                <PortalIcon name="Home" size={22} color="#00B3D9" />
              </div>
              <div style={{ fontSize: 14, fontWeight: 600 }}>Search for properties</div>
              <div style={{ fontSize: 12, color: '#4F5B67', marginTop: 4 }}>Enter an ID or CIPC registration number to find all registered properties.</div>
            </div>
          )}
          {status === 'loading' && (
            <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, padding: '20px 22px' }}>
              <div style={{ fontSize: 12, color: '#0099bb', fontWeight: 600, marginBottom: 10 }}>Searching deeds registry…</div>
              {[80, 60, 70].map((w, i) => <div key={i} style={{ height: 12, width: w + '%', background: '#F2F3F3', borderRadius: 3, marginBottom: 8 }} />)}
            </div>
          )}
          {status === 'result' && (
            <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
              <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, padding: '14px 18px' }}>
                <div style={{ fontSize: 11, color: '#4F5B67', textTransform: 'uppercase', letterSpacing: '0.06em', fontWeight: 600 }}>Summary</div>
                <div style={{ display: 'flex', gap: 24, marginTop: 8 }}>
                  <div><div style={{ fontSize: 20, fontWeight: 700 }}>3</div><div style={{ fontSize: 10, color: '#4F5B67' }}>PROPERTIES</div></div>
                  <div><div style={{ fontSize: 20, fontWeight: 700, color: '#00B3D9' }}>R 14.7M</div><div style={{ fontSize: 10, color: '#4F5B67' }}>TOTAL MUNICIPAL VALUE</div></div>
                  <div><div style={{ fontSize: 20, fontWeight: 700 }}>2</div><div style={{ fontSize: 10, color: '#4F5B67' }}>ACTIVE BONDS</div></div>
                </div>
              </div>
              {SAMPLE_PROPERTIES.map(p => <PropertyCard key={p.erf} p={p} />)}
            </div>
          )}
        </div>
      )}

      {tab === 'history' && (
        <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, padding: '14px 18px', fontSize: 12, color: '#4F5B67' }}>5 recent deed searches · click to re-run.</div>
      )}
    </div>
  );
}

/* ──────────────────────────────────────────────────────────
   2) Street / ERF Conversion
   ──────────────────────────────────────────────────────── */

function PropertyConversion() {
  const [direction, setDirection] = useStateDe('addr_to_erf');
  const [value, setValue] = useStateDe('');
  const [status, setStatus] = useStateDe('idle');
  const submit = (e) => { e.preventDefault(); setStatus('loading'); setTimeout(() => setStatus('result'), 900); };
  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 14 }} data-screen-label="Street/ERF Conversion">
      <ServicePageHeader title="Street ↔ ERF Conversion" subtitle="Translate physical street addresses into ERF numbers and back. Useful for cross-referencing municipal records." />
      <div style={{ display: 'grid', gridTemplateColumns: 'minmax(0, 440px) minmax(0, 1fr)', gap: 14, alignItems: 'flex-start' }}>
        <form onSubmit={submit} style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, overflow: 'hidden' }}>
          <div style={{ padding: '14px 18px', borderBottom: '1px solid #e9ebed' }}>
            <div style={{ fontSize: 14, fontWeight: 600 }}>Convert</div>
          </div>
          <div style={{ padding: '16px 18px', display: 'flex', flexDirection: 'column', gap: 12 }}>
            <div style={{ display: 'flex', gap: 0, border: '1px solid #D5DBDB', borderRadius: 4, overflow: 'hidden' }}>
              {[{ id: 'addr_to_erf', label: 'Address → ERF' }, { id: 'erf_to_addr', label: 'ERF → Address' }].map((o, i) => (
                <button key={o.id} type="button" onClick={() => setDirection(o.id)} style={{ flex: 1, padding: '7px 0', fontSize: 12, fontWeight: 500, background: direction === o.id ? '#1A2E4B' : '#fff', color: direction === o.id ? '#fff' : '#1A2024', border: 'none', borderLeft: i > 0 ? '1px solid #D5DBDB' : 'none', cursor: 'pointer', fontFamily: "'Inter', sans-serif" }}>{o.label}</button>
              ))}
            </div>
            <DField label={direction === 'addr_to_erf' ? 'Street address' : 'ERF number'} required>
              <DInput value={value} onChange={e => setValue(e.target.value)} mono={direction === 'erf_to_addr'} placeholder={direction === 'addr_to_erf' ? '14 Cinnebar Street, Table View' : 'ERF 12453'} />
            </DField>
            <DField label="Province / municipality">
              <DSelect value="WC" onChange={() => {}} options={SA_PROVINCES} />
            </DField>
          </div>
          <div style={{ padding: '12px 18px', borderTop: '1px solid #e9ebed', background: '#F8FAFC', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <span style={{ fontSize: 11, color: '#4F5B67' }}>R 12.00 · municipal records</span>
            <PortalButton variant="cta" disabled={!value || status === 'loading'}>{status === 'loading' ? 'Looking up…' : 'Convert →'}</PortalButton>
          </div>
        </form>

        <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
          {status === 'result' ? (
            <>
              <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, padding: '20px 24px' }}>
                <div style={{ fontSize: 11, color: '#4F5B67', textTransform: 'uppercase', letterSpacing: '0.06em', fontWeight: 600, marginBottom: 8 }}>{direction === 'addr_to_erf' ? 'Resolved ERF' : 'Resolved address'}</div>
                {direction === 'addr_to_erf' ? (
                  <div style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: 24, fontWeight: 700, color: '#00B3D9' }}>ERF 12453</div>
                ) : (
                  <div style={{ fontSize: 17, fontWeight: 600 }}>14 Cinnebar Street, Table View, 7441</div>
                )}
                <div style={{ display: 'grid', gridTemplateColumns: 'repeat(2, 1fr)', gap: 8, marginTop: 14, paddingTop: 14, borderTop: '1px solid #f1f5f9' }}>
                  {[['Municipality', 'City of Cape Town'], ['Suburb', 'Table View'], ['Postal code', '7441'], ['Zoning', 'Residential']].map(([k, v]) => (
                    <div key={k} style={{ fontSize: 12 }}><span style={{ color: '#4F5B67' }}>{k}: </span><b>{v}</b></div>
                  ))}
                </div>
              </div>
              <PropertyCard p={SAMPLE_PROPERTIES[0]} />
            </>
          ) : (
            <div style={{ background: '#fff', border: '1px dashed #CBD5E1', borderRadius: 8, padding: '40px 24px', textAlign: 'center' }}>
              <PortalIcon name="Search" size={24} color="#00B3D9" />
              <div style={{ fontSize: 14, fontWeight: 600, marginTop: 8 }}>Enter an address or ERF</div>
              <div style={{ fontSize: 12, color: '#4F5B67', marginTop: 4 }}>We'll cross-reference against municipal records.</div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

/* ──────────────────────────────────────────────────────────
   3) Property Valuation
   ──────────────────────────────────────────────────────── */

function PropertyValuation() {
  const [v, setV] = useStateDe({});
  const [status, setStatus] = useStateDe('idle');
  const set = (p) => setV(x => ({ ...x, ...p }));
  const submit = (e) => { e.preventDefault(); setStatus('loading'); setTimeout(() => setStatus('result'), 1200); };

  const result = {
    estimate: 'R 2,850,000',
    low: 'R 2,650,000',
    high: 'R 3,050,000',
    confidence: 92,
    comparables: [
      { addr: '18 Cinnebar Street',   size: '590 m²',  sold: 'R 2,750,000', when: '2025-11' },
      { addr: '7 Coastal Lane',        size: '640 m²',  sold: 'R 3,100,000', when: '2025-09' },
      { addr: '22 Stellenbosch Way',   size: '580 m²',  sold: 'R 2,680,000', when: '2026-01' },
    ],
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 14 }} data-screen-label="Property Valuation">
      <ServicePageHeader title="Property Valuation" subtitle="Automated valuation model — combines deeds data, recent comparables, and area trends to estimate market value." />
      <div style={{ display: 'grid', gridTemplateColumns: 'minmax(0, 440px) minmax(0, 1fr)', gap: 14, alignItems: 'flex-start' }}>
        <form onSubmit={submit} style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, overflow: 'hidden' }}>
          <div style={{ padding: '14px 18px', borderBottom: '1px solid #e9ebed' }}>
            <div style={{ fontSize: 14, fontWeight: 600 }}>Property details</div>
          </div>
          <div style={{ padding: '16px 18px', display: 'flex', flexDirection: 'column', gap: 12 }}>
            <DField label="ERF number or address" required><DInput mono value={v.id || ''} onChange={e => set({ id: e.target.value })} placeholder="ERF 12453" /></DField>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10 }}>
              <DField label="Erf size (m²)"><DInput mono value={v.size || ''} onChange={e => set({ size: e.target.value })} placeholder="612" /></DField>
              <DField label="Bedrooms"><DInput mono value={v.bed || ''} onChange={e => set({ bed: e.target.value })} placeholder="3" /></DField>
            </div>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10 }}>
              <DField label="Bathrooms"><DInput mono value={v.bath || ''} onChange={e => set({ bath: e.target.value })} placeholder="2" /></DField>
              <DField label="Year built"><DInput mono value={v.year || ''} onChange={e => set({ year: e.target.value })} placeholder="2008" /></DField>
            </div>
            <DField label="Condition"><DSelect value="good" onChange={() => {}} options={[{value:'excellent',label:'Excellent'},{value:'good',label:'Good'},{value:'fair',label:'Fair'},{value:'poor',label:'Needs work'}]} /></DField>
          </div>
          <div style={{ padding: '12px 18px', borderTop: '1px solid #e9ebed', background: '#F8FAFC', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <span style={{ fontSize: 11, color: '#4F5B67' }}>R 350.00 · AVM result in 15 seconds</span>
            <PortalButton variant="cta" disabled={!v.id || status === 'loading'}>{status === 'loading' ? 'Valuing…' : 'Value property →'}</PortalButton>
          </div>
        </form>

        <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
          {status === 'idle' && (
            <div style={{ background: '#fff', border: '1px dashed #CBD5E1', borderRadius: 8, padding: '40px 24px', textAlign: 'center' }}>
              <PortalIcon name="BarChart" size={24} color="#00B3D9" />
              <div style={{ fontSize: 14, fontWeight: 600, marginTop: 8 }}>Awaiting property details</div>
            </div>
          )}
          {status === 'result' && (
            <>
              <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, overflow: 'hidden' }}>
                <div style={{ display: 'flex', height: 3 }}>
                  <div style={{ flex: 3, background: '#E23D36' }} />
                  <div style={{ flex: 5, background: '#1A2E4B' }} />
                  <div style={{ flex: 2, background: '#00B3D9' }} />
                </div>
                <div style={{ padding: '22px 24px', textAlign: 'center' }}>
                  <div style={{ fontSize: 11, color: '#4F5B67', textTransform: 'uppercase', letterSpacing: '0.08em', fontWeight: 600 }}>Estimated market value</div>
                  <div style={{ fontSize: 42, fontWeight: 700, color: '#1A2E4B', marginTop: 6, fontFamily: "'Inter', sans-serif", letterSpacing: '-0.02em' }}>{result.estimate}</div>
                  <div style={{ display: 'flex', justifyContent: 'center', gap: 10, marginTop: 8, fontSize: 12, color: '#4F5B67' }}>
                    <span>{result.low}</span>
                    <span style={{ position: 'relative', width: 160, height: 6, background: '#F2F3F3', borderRadius: 3, overflow: 'hidden', alignSelf: 'center' }}>
                      <span style={{ position: 'absolute', left: '20%', right: '20%', top: 0, bottom: 0, background: 'linear-gradient(90deg, #2C974B, #00B3D9, #2C974B)' }} />
                      <span style={{ position: 'absolute', left: '50%', top: -2, bottom: -2, width: 2, background: '#1A2E4B' }} />
                    </span>
                    <span>{result.high}</span>
                  </div>
                  <div style={{ marginTop: 14, display: 'inline-flex', alignItems: 'center', gap: 6, padding: '4px 10px', borderRadius: 16, background: 'rgba(44,151,75,0.10)', color: '#2C974B', fontSize: 11, fontWeight: 600 }}>
                    <PortalIcon name="Shield" size={11} color="#2C974B" />
                    {result.confidence}% confidence · 3 comparables within 500m
                  </div>
                </div>
              </div>
              <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, overflow: 'hidden' }}>
                <div style={{ padding: '12px 18px', borderBottom: '1px solid #e9ebed', fontSize: 13, fontWeight: 600 }}>Recent comparables</div>
                {result.comparables.map((c, i) => (
                  <div key={i} style={{ display: 'flex', justifyContent: 'space-between', padding: '10px 18px', borderBottom: i < result.comparables.length - 1 ? '1px solid #f1f5f9' : 'none', fontSize: 12 }}>
                    <div>
                      <div style={{ fontWeight: 500 }}>{c.addr}</div>
                      <div style={{ fontSize: 11, color: '#4F5B67' }}>{c.size} · sold {c.when}</div>
                    </div>
                    <div style={{ fontFamily: "'JetBrains Mono', monospace", fontWeight: 600, color: '#00B3D9' }}>{c.sold}</div>
                  </div>
                ))}
              </div>
            </>
          )}
        </div>
      </div>
    </div>
  );
}

/* ──────────────────────────────────────────────────────────
   4) Deeds Map — visual property search
   ──────────────────────────────────────────────────────── */

function DeedsMap() {
  const [selected, setSelected] = useStateDe(SAMPLE_PROPERTIES[0]);
  // pretend coords mapped onto fake map area
  const pins = [
    { p: SAMPLE_PROPERTIES[0], x: 38, y: 42, hot: false },
    { p: SAMPLE_PROPERTIES[1], x: 62, y: 28, hot: true },
    { p: SAMPLE_PROPERTIES[2], x: 50, y: 68, hot: false },
    { p: { erf: 'ERF 99021', address: '12 Lighthouse Cres.', size: '380 m²', type: 'Residential', municipal: 'R 1,200,000', registered: '2020-02-18', bond: 'R 900,000' }, x: 24, y: 60, hot: false },
    { p: { erf: 'ERF 5512',  address: '5 Marine Drive',      size: '720 m²', type: 'Residential', municipal: 'R 5,800,000', registered: '2012-07-04', bond: 'Bond settled' }, x: 72, y: 50, hot: false },
  ];
  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 14 }} data-screen-label="Deeds Map">
      <ServicePageHeader title="Deeds Map" subtitle="Visual property search. Click any pin to view ERF and ownership details." />
      <div style={{ display: 'grid', gridTemplateColumns: 'minmax(0, 1fr) 340px', gap: 14 }}>
        {/* Map panel */}
        <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, overflow: 'hidden', position: 'relative' }}>
          <div style={{ padding: '12px 18px', borderBottom: '1px solid #e9ebed', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <div>
              <div style={{ fontSize: 13, fontWeight: 600 }}>Cape Town · Atlantic Seaboard</div>
              <div style={{ fontSize: 11, color: '#4F5B67' }}>{pins.length} properties shown · 1 flagged</div>
            </div>
            <div style={{ display: 'flex', gap: 6 }}>
              <PortalButton variant="secondary" size="sm">Filters</PortalButton>
              <PortalButton variant="secondary" size="sm">Layers</PortalButton>
            </div>
          </div>
          <div style={{ position: 'relative', height: 460, background: 'linear-gradient(135deg, #d4e4ec 0%, #b8d0dc 30%, #e8d8c0 30%, #d4c3a8 60%, #c5b594 100%)', overflow: 'hidden' }}>
            {/* fake grid */}
            <svg width="100%" height="100%" style={{ position: 'absolute', inset: 0, opacity: 0.15 }}>
              {Array.from({ length: 14 }).map((_, i) => <line key={'h' + i} x1="0" y1={i * 40} x2="100%" y2={i * 40} stroke="#1A2E4B" strokeWidth="1" />)}
              {Array.from({ length: 24 }).map((_, i) => <line key={'v' + i} x1={i * 40} y1="0" x2={i * 40} y2="100%" stroke="#1A2E4B" strokeWidth="1" />)}
            </svg>
            {/* fake coastline */}
            <svg width="100%" height="100%" style={{ position: 'absolute', inset: 0 }} viewBox="0 0 100 100" preserveAspectRatio="none">
              <path d="M 0 30 Q 20 35 30 40 T 50 38 T 70 35 T 100 30 L 100 0 L 0 0 Z" fill="rgba(0,179,217,0.15)" />
              <path d="M 0 30 Q 20 35 30 40 T 50 38 T 70 35 T 100 30" stroke="#00B3D9" strokeWidth="0.4" fill="none" />
            </svg>
            <div style={{ position: 'absolute', top: 12, left: 14, padding: '4px 10px', background: 'rgba(26,46,75,0.85)', color: '#fff', fontSize: 10, borderRadius: 4, fontFamily: "'JetBrains Mono', monospace" }}>−33.8° S · 18.5° E · zoom 14</div>
            {/* Pins */}
            {pins.map((pin, i) => (
              <button key={i} onClick={() => setSelected(pin.p)} style={{
                position: 'absolute', left: pin.x + '%', top: pin.y + '%', transform: 'translate(-50%, -100%)',
                background: 'transparent', border: 'none', cursor: 'pointer', padding: 0,
              }}>
                <div style={{ width: 28, height: 36, position: 'relative' }}>
                  <svg viewBox="0 0 28 36" width="28" height="36">
                    <path d={`M 14 0 C 6 0 0 6 0 14 C 0 24 14 36 14 36 C 14 36 28 24 28 14 C 28 6 22 0 14 0 Z`} fill={pin.hot ? '#E23D36' : (selected?.erf === pin.p.erf ? '#00B3D9' : '#1A2E4B')} />
                    <circle cx="14" cy="14" r="5" fill="#fff" />
                  </svg>
                </div>
              </button>
            ))}
          </div>
          <div style={{ padding: '10px 14px', display: 'flex', gap: 14, fontSize: 11, color: '#4F5B67', alignItems: 'center', borderTop: '1px solid #e9ebed' }}>
            <span style={{ display: 'inline-flex', alignItems: 'center', gap: 5 }}><span style={{ width: 10, height: 10, borderRadius: 999, background: '#1A2E4B' }} />Owned</span>
            <span style={{ display: 'inline-flex', alignItems: 'center', gap: 5 }}><span style={{ width: 10, height: 10, borderRadius: 999, background: '#00B3D9' }} />Selected</span>
            <span style={{ display: 'inline-flex', alignItems: 'center', gap: 5 }}><span style={{ width: 10, height: 10, borderRadius: 999, background: '#E23D36' }} />Flagged · sanctions match</span>
          </div>
        </div>

        {/* Side panel */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
          <div style={{ background: '#fff', border: '1px solid #D5DBDB', borderRadius: 8, overflow: 'hidden' }}>
            <div style={{ display: 'flex', height: 3 }}>
              <div style={{ flex: 3, background: '#E23D36' }} />
              <div style={{ flex: 5, background: '#1A2E4B' }} />
              <div style={{ flex: 2, background: '#00B3D9' }} />
            </div>
            <div style={{ padding: '14px 16px' }}>
              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                <span style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: 12, color: '#00B3D9' }}>{selected.erf}</span>
                <PortalBadge variant="info" size="sm">{selected.type}</PortalBadge>
              </div>
              <div style={{ fontSize: 14, fontWeight: 600, marginTop: 6 }}>{selected.address}</div>
              <div style={{ display: 'flex', flexDirection: 'column', gap: 6, marginTop: 12, paddingTop: 12, borderTop: '1px solid #f1f5f9' }}>
                {[['Erf size', selected.size], ['Municipal value', selected.municipal], ['Bond status', selected.bond], ['Registered', selected.registered]].map(([k, v]) => (
                  <div key={k} style={{ display: 'flex', justifyContent: 'space-between', fontSize: 12 }}><span style={{ color: '#4F5B67' }}>{k}</span><b>{v}</b></div>
                ))}
              </div>
              <div style={{ marginTop: 14, display: 'flex', gap: 6 }}>
                <PortalButton variant="primary" size="sm" style={{ flex: 1 }}>Full deeds report</PortalButton>
                <PortalButton variant="secondary" size="sm">Value it</PortalButton>
              </div>
            </div>
          </div>
          <div style={{ background: '#F8FAFC', border: '1px dashed #CBD5E1', borderRadius: 8, padding: '10px 14px', fontSize: 11, color: '#4F5B67', lineHeight: 1.55 }}>
            <b style={{ color: '#1A2E4B' }}>Map data:</b> properties shown are illustrative. Live map integrates with Surveyor-General + Municipal GIS feeds.
          </div>
        </div>
      </div>
    </div>
  );
}

window.PropertyOwnership = PropertyOwnership;
window.PropertyConversion = PropertyConversion;
window.PropertyValuation = PropertyValuation;
window.DeedsMap = DeedsMap;
