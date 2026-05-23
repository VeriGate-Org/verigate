/* global React, Button, Icon, Logo */
/* ──────────────────────────────────────────────────────────
   Tier 6 — Marketing long-tail
   ROI Calculator · Careers · Events · Partner Program ·
   Blog index + post · Resources · Legal (Privacy/Terms/Cookies) · 404
   ──────────────────────────────────────────────────────── */
const { useState: useStateL } = React;

function MSectionHeader({ eyebrow, title, subtitle, center = true, dark }) {
  return (
    <div style={{ textAlign: center ? 'center' : 'left', maxWidth: 720, margin: center ? '0 auto 40px' : '0 0 28px' }}>
      <span style={{ fontSize: 12, fontWeight: 600, color: '#00B3D9', textTransform: 'uppercase', letterSpacing: '0.1em' }}>{eyebrow}</span>
      <h2 style={{ fontSize: 36, fontWeight: 700, color: dark ? '#fff' : '#1A2E4B', margin: '8px 0 12px', letterSpacing: '-0.01em', lineHeight: 1.2 }}>{title}</h2>
      {subtitle && <p style={{ fontSize: 15, color: dark ? 'rgba(255,255,255,0.7)' : '#64748B', lineHeight: 1.6 }}>{subtitle}</p>}
    </div>
  );
}

/* ──────────────────────────────────────────────────────────
   ROI CALCULATOR — interactive
   ──────────────────────────────────────────────────────── */
function ROICalculator() {
  const [vol, setVol] = useStateL(500);
  const [pricePer, setPricePer] = useStateL(45);
  const [manualMins, setManualMins] = useStateL(28);
  const [rate, setRate] = useStateL(280);
  const monthly = vol * pricePer;
  const manualHours = (vol * manualMins) / 60;
  const labourCost = manualHours * rate;
  const savings = labourCost - monthly;
  const fmt = (n) => 'R ' + Math.round(n).toLocaleString('en-ZA');
  return (
    <main style={{ paddingTop: 60 }} data-screen-label="ROI Calculator">
      <section style={{ background: 'linear-gradient(160deg,#0F1A2E,#1A2E4B 60%,#1a3a5c)', padding: '64px 32px', color: '#fff', position: 'relative', overflow: 'hidden' }}>
        <div style={{ position: 'absolute', inset: 0, backgroundImage: 'radial-gradient(circle, rgba(255,255,255,0.06) 1px, transparent 1px)', backgroundSize: '24px 24px' }} />
        <div style={{ position: 'relative', maxWidth: 920, margin: '0 auto', textAlign: 'center' }}>
          <span style={{ fontSize: 12, fontWeight: 600, color: '#00B3D9', textTransform: 'uppercase', letterSpacing: '0.1em' }}>ROI calculator</span>
          <h1 style={{ fontSize: 42, fontWeight: 700, lineHeight: 1.15, letterSpacing: '-0.02em', margin: '10px 0 14px' }}>See what automated verification saves you.</h1>
          <p style={{ fontSize: 15, color: 'rgba(255,255,255,0.7)' }}>Move the sliders. The numbers update in real time.</p>
        </div>
      </section>
      <section style={{ padding: '48px 32px 96px', background: '#F8FAFC' }}>
        <div style={{ maxWidth: 1000, margin: '0 auto', display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 24 }}>
          <div style={{ background: '#fff', border: '1px solid #E2E8F0', borderRadius: 8, padding: 28 }}>
            <h3 style={{ fontSize: 18, fontWeight: 600, color: '#1A2E4B', marginBottom: 18 }}>Your numbers</h3>
            {[
              { l: 'Monthly verifications', v: vol,        s: setVol,        min: 50,   max: 5000, step: 50,   fmt: (n) => n.toLocaleString('en-ZA') },
              { l: 'Cost per verification', v: pricePer,   s: setPricePer,   min: 10,   max: 200,  step: 5,    fmt: (n) => 'R ' + n },
              { l: 'Manual time per check (min)', v: manualMins, s: setManualMins, min: 5, max: 90, step: 1, fmt: (n) => n + ' min' },
              { l: 'Hourly cost of operator',     v: rate,       s: setRate,       min: 150, max: 600, step: 10, fmt: (n) => 'R ' + n + '/hr' },
            ].map(f => (
              <div key={f.l} style={{ marginBottom: 16 }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: 13, fontWeight: 500, color: '#1A2E4B', marginBottom: 6 }}>
                  <span>{f.l}</span>
                  <span style={{ fontFamily: "'JetBrains Mono', monospace", color: '#00B3D9', fontWeight: 600 }}>{f.fmt(f.v)}</span>
                </div>
                <input type="range" min={f.min} max={f.max} step={f.step} value={f.v} onChange={e => f.s(Number(e.target.value))} style={{ width: '100%', accentColor: '#00B3D9' }} />
              </div>
            ))}
          </div>
          <div style={{ background: 'linear-gradient(160deg,#0F1A2E,#1A2E4B 60%,#1a3a5c)', borderRadius: 8, padding: 32, color: '#fff', position: 'relative', overflow: 'hidden' }}>
            <div style={{ position: 'absolute', top: 0, left: 0, right: 0, height: 3, background: 'linear-gradient(90deg,#D63031 25%,#1A2E4B 25% 75%,#00B3D9 75%)' }} />
            <span style={{ fontSize: 11, color: '#00B3D9', textTransform: 'uppercase', letterSpacing: '0.1em', fontWeight: 600 }}>Estimated annual savings</span>
            <div style={{ fontSize: 56, fontWeight: 700, marginTop: 6, lineHeight: 1, letterSpacing: '-0.02em', background: 'linear-gradient(90deg,#22d3ee,#60a5fa,#06b6d4)', WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent', backgroundClip: 'text' }}>{fmt(savings * 12)}</div>
            <div style={{ marginTop: 24, paddingTop: 18, borderTop: '1px solid rgba(255,255,255,0.10)', display: 'flex', flexDirection: 'column', gap: 12 }}>
              {[
                ['Monthly VeriGate cost', fmt(monthly)],
                ['Manual hours saved / mo', Math.round(manualHours) + ' hr'],
                ['Manual labour cost replaced', fmt(labourCost)],
                ['Monthly savings', fmt(savings)],
              ].map(([k, v]) => (
                <div key={k} style={{ display: 'flex', justifyContent: 'space-between', fontSize: 13 }}>
                  <span style={{ color: 'rgba(255,255,255,0.7)' }}>{k}</span>
                  <span style={{ fontFamily: "'JetBrains Mono', monospace", fontWeight: 600 }}>{v}</span>
                </div>
              ))}
            </div>
            <div style={{ marginTop: 22 }}>
              <Button variant="hero" style={{ width: '100%' }}>Get a tailored quote →</Button>
            </div>
          </div>
        </div>
      </section>
    </main>
  );
}

/* ──────────────────────────────────────────────────────────
   CAREERS
   ──────────────────────────────────────────────────────── */
function Careers() {
  const roles = [
    { team: 'Engineering',  title: 'Senior Backend Engineer · Java',         loc: 'Cape Town / Remote ZA', type: 'Full-time' },
    { team: 'Engineering',  title: 'Frontend Engineer · React / Next.js',     loc: 'Cape Town / Remote ZA', type: 'Full-time' },
    { team: 'Engineering',  title: 'Risk & Decisioning Engineer',              loc: 'Cape Town',              type: 'Full-time' },
    { team: 'Compliance',   title: 'POPIA & Compliance Lead',                  loc: 'Cape Town',              type: 'Full-time' },
    { team: 'Design',       title: 'Senior Product Designer',                  loc: 'Remote ZA',              type: 'Full-time' },
    { team: 'Sales',        title: 'Enterprise Account Executive',             loc: 'Johannesburg',           type: 'Full-time' },
    { team: 'Success',      title: 'Customer Success Manager',                  loc: 'Cape Town',              type: 'Full-time' },
  ];
  return (
    <main style={{ paddingTop: 60 }} data-screen-label="Careers">
      <section style={{ background: 'linear-gradient(160deg,#0F1A2E,#1A2E4B 60%,#1a3a5c)', padding: '80px 32px', color: '#fff', position: 'relative', overflow: 'hidden' }}>
        <div style={{ position: 'absolute', inset: 0, backgroundImage: 'radial-gradient(circle, rgba(255,255,255,0.06) 1px, transparent 1px)', backgroundSize: '24px 24px' }} />
        <div style={{ position: 'relative', maxWidth: 760, margin: '0 auto', textAlign: 'center' }}>
          <span style={{ fontSize: 12, fontWeight: 600, color: '#00B3D9', textTransform: 'uppercase', letterSpacing: '0.1em' }}>Careers</span>
          <h1 style={{ fontSize: 48, fontWeight: 700, lineHeight: 1.1, letterSpacing: '-0.02em', margin: '10px 0 14px' }}>Help us make compliance the easy thing.</h1>
          <p style={{ fontSize: 17, color: 'rgba(255,255,255,0.75)' }}>We're a 22-person team building the platform we wished existed.</p>
        </div>
      </section>
      <section style={{ padding: '64px 32px', background: '#fff' }}>
        <div style={{ maxWidth: 1000, margin: '0 auto' }}>
          <MSectionHeader eyebrow="Why VeriGate" title="What it's actually like to work here." center={false} />
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: 16 }}>
            {[
              { icon: 'Shield',   t: 'Real impact',    d: 'Every line of code touches a real onboarding, today, somewhere in South Africa.' },
              { icon: 'UserCheck',t: 'Mission first',  d: '100% Black-owned, Level 1 B-BBEE. Hiring with the country we want to live in.' },
              { icon: 'Zap',      t: 'Move fast',      d: "Small team, no committees. Ship daily. We don't tolerate bullshit processes." },
              { icon: 'Globe',    t: 'Remote-friendly', d: 'HQ in Cape Town, but most roles are remote-first within SA timezones.' },
              { icon: 'CheckCircle',t: 'Generous PTO', d: '25 days PTO, plus public holidays. Sabbatical after 4 years.' },
              { icon: 'BarChart', t: 'Equity for everyone', d: 'Every employee gets meaningful equity. We win or lose together.' },
            ].map(c => (
              <div key={c.t} style={{ border: '1px solid #E2E8F0', borderRadius: 8, padding: 22 }}>
                <div style={{ width: 38, height: 38, borderRadius: 8, background: 'rgba(0,179,217,0.10)', display: 'inline-flex', alignItems: 'center', justifyContent: 'center', marginBottom: 12 }}>
                  <Icon name={c.icon} size={18} color="#00B3D9" />
                </div>
                <h4 style={{ fontSize: 16, fontWeight: 600, color: '#1A2E4B', marginBottom: 6 }}>{c.t}</h4>
                <p style={{ fontSize: 13, color: '#64748B', lineHeight: 1.6 }}>{c.d}</p>
              </div>
            ))}
          </div>
        </div>
      </section>
      <section style={{ padding: '64px 32px 96px', background: '#F8FAFC' }}>
        <div style={{ maxWidth: 1000, margin: '0 auto' }}>
          <MSectionHeader eyebrow="Open roles" title="7 positions open" center={false} />
          <div style={{ background: '#fff', border: '1px solid #E2E8F0', borderRadius: 8, overflow: 'hidden' }}>
            {roles.map((r, i) => (
              <a key={r.title} href="#" style={{ display: 'flex', alignItems: 'center', gap: 16, padding: '18px 22px', borderTop: i > 0 ? '1px solid #F1F5F9' : 'none', transition: 'background 100ms cubic-bezier(0.4,0,0.2,1)' }}
                onMouseEnter={e => e.currentTarget.style.background = '#F8FAFC'}
                onMouseLeave={e => e.currentTarget.style.background = 'transparent'}>
                <span style={{ fontSize: 11, fontWeight: 600, color: '#00B3D9', textTransform: 'uppercase', letterSpacing: '0.08em', width: 100, flexShrink: 0 }}>{r.team}</span>
                <div style={{ flex: 1 }}>
                  <div style={{ fontSize: 15, fontWeight: 600, color: '#1A2E4B' }}>{r.title}</div>
                  <div style={{ fontSize: 12, color: '#64748B', marginTop: 2 }}>{r.loc} · {r.type}</div>
                </div>
                <span style={{ color: '#00B3D9', fontSize: 18 }}>→</span>
              </a>
            ))}
          </div>
        </div>
      </section>
    </main>
  );
}

/* ──────────────────────────────────────────────────────────
   EVENTS
   ──────────────────────────────────────────────────────── */
function Events() {
  const events = [
    { d: '04 Jun 2026', t: 'Webinar', title: 'POPIA in practice: 12 months in', city: 'Online · 11:00 SAST', tone: '#00B3D9', upcoming: true },
    { d: '18 Jun 2026', t: 'Conference', title: 'Africa Fintech Summit', city: 'Cape Town · CTICC', tone: '#E23D36', upcoming: true },
    { d: '02 Jul 2026', t: 'Workshop', title: 'Policy Builder masterclass', city: 'JHB · The Capital Empire', tone: '#1A2E4B', upcoming: true },
    { d: '14 May 2026', t: 'Webinar', title: 'Sanctions screening — what changed', city: 'Online', tone: '#4F5B67', upcoming: false },
    { d: '28 Apr 2026', t: 'Conference', title: 'RegTech Africa', city: 'Cape Town', tone: '#4F5B67', upcoming: false },
  ];
  return (
    <main style={{ paddingTop: 60 }} data-screen-label="Events">
      <section style={{ padding: '64px 32px', background: '#F8FAFC' }}>
        <div style={{ maxWidth: 920, margin: '0 auto', textAlign: 'center' }}>
          <span style={{ fontSize: 12, fontWeight: 600, color: '#00B3D9', textTransform: 'uppercase', letterSpacing: '0.1em' }}>Events</span>
          <h1 style={{ fontSize: 42, fontWeight: 700, color: '#1A2E4B', margin: '8px 0 12px', letterSpacing: '-0.01em' }}>Where to find us.</h1>
          <p style={{ fontSize: 15, color: '#64748B' }}>Webinars, workshops, and the conferences we sponsor.</p>
        </div>
      </section>
      <section style={{ padding: '32px 32px 96px', background: '#F8FAFC' }}>
        <div style={{ maxWidth: 880, margin: '0 auto' }}>
          {['Upcoming', 'Past'].map(group => (
            <div key={group} style={{ marginBottom: 32 }}>
              <h3 style={{ fontSize: 14, fontWeight: 600, color: '#1A2E4B', textTransform: 'uppercase', letterSpacing: '0.1em', marginBottom: 14 }}>{group}</h3>
              {events.filter(e => (group === 'Upcoming') === e.upcoming).map(e => (
                <div key={e.title} style={{ background: '#fff', border: '1px solid #E2E8F0', borderRadius: 8, padding: '18px 22px', marginBottom: 10, display: 'flex', alignItems: 'center', gap: 18, opacity: e.upcoming ? 1 : 0.7 }}>
                  <div style={{ textAlign: 'center', flexShrink: 0, padding: '6px 12px', borderRight: '1px solid #E2E8F0' }}>
                    <div style={{ fontSize: 11, fontWeight: 600, color: e.tone, textTransform: 'uppercase', letterSpacing: '0.06em' }}>{e.d.slice(3, 6)}</div>
                    <div style={{ fontSize: 24, fontWeight: 700, color: '#1A2E4B', lineHeight: 1 }}>{e.d.slice(0, 2)}</div>
                    <div style={{ fontSize: 10, color: '#64748B' }}>{e.d.slice(7)}</div>
                  </div>
                  <div style={{ flex: 1 }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 4 }}>
                      <span style={{ fontSize: 10, padding: '2px 8px', background: '#F2F3F3', borderRadius: 3, color: e.tone, fontWeight: 600, textTransform: 'uppercase', letterSpacing: '0.06em' }}>{e.t}</span>
                    </div>
                    <div style={{ fontSize: 16, fontWeight: 600, color: '#1A2E4B' }}>{e.title}</div>
                    <div style={{ fontSize: 12, color: '#64748B', marginTop: 2 }}>{e.city}</div>
                  </div>
                  {e.upcoming && <Button variant="outline">Register →</Button>}
                </div>
              ))}
            </div>
          ))}
        </div>
      </section>
    </main>
  );
}

/* ──────────────────────────────────────────────────────────
   PARTNER PROGRAM
   ──────────────────────────────────────────────────────── */
function PartnerProgram() {
  return (
    <main style={{ paddingTop: 60 }} data-screen-label="Partner Program">
      <section style={{ background: 'linear-gradient(160deg,#0F1A2E,#1A2E4B 60%,#1a3a5c)', padding: '80px 32px', color: '#fff', position: 'relative', overflow: 'hidden' }}>
        <div style={{ position: 'absolute', inset: 0, backgroundImage: 'radial-gradient(circle, rgba(255,255,255,0.06) 1px, transparent 1px)', backgroundSize: '24px 24px' }} />
        <div style={{ position: 'relative', maxWidth: 800, margin: '0 auto', textAlign: 'center' }}>
          <span style={{ fontSize: 12, fontWeight: 600, color: '#00B3D9', textTransform: 'uppercase', letterSpacing: '0.1em' }}>Partner program</span>
          <h1 style={{ fontSize: 46, fontWeight: 700, lineHeight: 1.1, letterSpacing: '-0.02em', margin: '10px 0 12px' }}>Build with us. Earn with us.</h1>
          <p style={{ fontSize: 16, color: 'rgba(255,255,255,0.75)' }}>Resellers, system integrators, and consulting firms — three tiers, generous revenue share.</p>
        </div>
      </section>
      <section style={{ padding: '64px 32px', background: '#fff' }}>
        <div style={{ maxWidth: 1100, margin: '0 auto', display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: 20 }}>
          {[
            { tier: 'Registered',  share: '15%',  req: 'Open to anyone. Refer customers, get paid.',                    perks: ['15% revenue share · year 1','Marketing assets','Self-serve onboarding'] },
            { tier: 'Certified',   share: '25%',  req: '3+ certified consultants. Live customer references.',           perks: ['25% revenue share · year 1','Dedicated partner manager','Co-marketing budget','Sandbox tenants'], featured: true },
            { tier: 'Strategic',   share: '40%',  req: 'Joint go-to-market. Quarterly business reviews.',               perks: ['40% revenue share · year 1','Executive sponsorship','Custom integrations','White-label option'] },
          ].map(p => (
            <div key={p.tier} style={{ background: p.featured ? '#FFFBFB' : '#fff', border: `1px solid ${p.featured ? '#D63031' : '#E2E8F0'}`, borderTop: `3px solid ${p.featured ? '#D63031' : '#1A2E4B'}`, borderRadius: 8, padding: 28, position: 'relative' }}>
              {p.featured && <div style={{ position: 'absolute', top: -12, left: '50%', transform: 'translateX(-50%)', background: '#D63031', color: '#fff', fontSize: 10, fontWeight: 700, letterSpacing: '0.12em', padding: '4px 14px', borderRadius: 4 }}>MOST POPULAR</div>}
              <div style={{ fontSize: 14, fontWeight: 700, color: '#1A2E4B', textTransform: 'uppercase', letterSpacing: '0.06em' }}>{p.tier}</div>
              <div style={{ fontSize: 48, fontWeight: 700, color: '#D63031', marginTop: 10, lineHeight: 1 }}>{p.share}</div>
              <div style={{ fontSize: 11, color: '#94A3B8', marginTop: 4, textTransform: 'uppercase', letterSpacing: '0.08em', fontWeight: 600 }}>Revenue share</div>
              <div style={{ fontSize: 13, color: '#64748B', marginTop: 14, lineHeight: 1.55, minHeight: 50 }}>{p.req}</div>
              <ul style={{ listStyle: 'none', padding: 0, marginTop: 18, display: 'flex', flexDirection: 'column', gap: 8 }}>
                {p.perks.map(x => <li key={x} style={{ display: 'flex', gap: 8, fontSize: 13 }}><span style={{ color: '#2C974B', fontWeight: 700 }}>✓</span>{x}</li>)}
              </ul>
              <Button variant={p.featured ? 'hero' : 'outline'} style={{ width: '100%', marginTop: 22 }}>Apply</Button>
            </div>
          ))}
        </div>
      </section>
    </main>
  );
}

/* ──────────────────────────────────────────────────────────
   BLOG INDEX
   ──────────────────────────────────────────────────────── */
const POSTS = [
  { slug: 'state-of-verification-2026', title: 'The State of Verification 2026', excerpt: 'Our annual report on identity, fraud, and compliance trends across South Africa.', tag: 'Report', date: '14 May 2026', read: '12 min', featured: true },
  { slug: 'fica-amendments-2026', title: 'FICA amendments 2026: what changed and what to do',  excerpt: 'A practical guide to the latest FICA amendments and how to update your KYB processes.', tag: 'Compliance', date: '06 May 2026', read: '8 min' },
  { slug: 'popia-background-screening', title: 'POPIA-compliant background screening, end to end', excerpt: 'How to capture consent, retain data, and respond to subject access requests without missing a step.', tag: 'POPIA', date: '28 Apr 2026', read: '10 min' },
  { slug: 'criminal-checks-hiring', title: 'Criminal record checks in hiring: legal, fast, fair', excerpt: 'When to run them, what they show, and how to use the results without falling foul of the Employment Equity Act.', tag: 'Hiring', date: '14 Apr 2026', read: '7 min' },
  { slug: 'facial-recognition-onboarding', title: 'Facial recognition for onboarding — done right', excerpt: 'Liveness detection, selfie match, and the POPIA implications of biometric capture.', tag: 'Biometric', date: '02 Apr 2026', read: '9 min' },
  { slug: 'bulk-verification-upload', title: 'Verifying 5,000 customers at once', excerpt: 'A walkthrough of bulk upload, error handling, and how to set realistic SLAs.', tag: 'How-to', date: '20 Mar 2026', read: '6 min' },
];

function Blog({ onOpenPost }) {
  const featured = POSTS.find(p => p.featured);
  return (
    <main style={{ paddingTop: 60 }} data-screen-label="Blog">
      <section style={{ padding: '64px 32px', background: '#F8FAFC' }}>
        <div style={{ maxWidth: 1100, margin: '0 auto' }}>
          <MSectionHeader eyebrow="The blog" title="Writing about verification, compliance, and the SA market." center={false} />
          {featured && (
            <button onClick={() => onOpenPost(featured.slug)} style={{ width: '100%', textAlign: 'left', background: '#1A2E4B', borderRadius: 12, padding: '40px 36px', color: '#fff', position: 'relative', overflow: 'hidden', cursor: 'pointer', border: 'none', marginBottom: 32 }}>
              <div style={{ position: 'absolute', top: 0, left: 0, right: 0, height: 3, background: 'linear-gradient(90deg,#D63031 25%,#1A2E4B 25% 75%,#00B3D9 75%)' }} />
              <span style={{ fontSize: 11, color: '#00B3D9', textTransform: 'uppercase', letterSpacing: '0.1em', fontWeight: 600 }}>{featured.tag} · Featured</span>
              <h3 style={{ fontSize: 32, fontWeight: 700, lineHeight: 1.2, marginTop: 10, letterSpacing: '-0.01em' }}>{featured.title}</h3>
              <p style={{ fontSize: 16, color: 'rgba(255,255,255,0.75)', marginTop: 12, lineHeight: 1.6, maxWidth: 720 }}>{featured.excerpt}</p>
              <div style={{ marginTop: 16, fontSize: 12, color: 'rgba(255,255,255,0.6)' }}>{featured.date} · {featured.read} read · Read more →</div>
            </button>
          )}
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: 18 }}>
            {POSTS.filter(p => !p.featured).map(p => (
              <button key={p.slug} onClick={() => onOpenPost(p.slug)} style={{ textAlign: 'left', background: '#fff', border: '1px solid #E2E8F0', borderRadius: 8, padding: 22, cursor: 'pointer', transition: 'all 200ms cubic-bezier(0.4,0,0.2,1)' }}
                onMouseEnter={e => { e.currentTarget.style.transform = 'translateY(-3px)'; e.currentTarget.style.boxShadow = '0 16px 24px -8px rgba(0,28,36,0.10)'; e.currentTarget.style.borderColor = 'rgba(0,179,217,0.4)'; }}
                onMouseLeave={e => { e.currentTarget.style.transform = 'translateY(0)'; e.currentTarget.style.boxShadow = 'none'; e.currentTarget.style.borderColor = '#E2E8F0'; }}>
                <span style={{ fontSize: 10, color: '#00B3D9', textTransform: 'uppercase', letterSpacing: '0.1em', fontWeight: 600 }}>{p.tag}</span>
                <h4 style={{ fontSize: 17, fontWeight: 600, color: '#1A2E4B', marginTop: 8, lineHeight: 1.3 }}>{p.title}</h4>
                <p style={{ fontSize: 13, color: '#64748B', marginTop: 8, lineHeight: 1.55 }}>{p.excerpt}</p>
                <div style={{ marginTop: 14, fontSize: 11, color: '#94A3B8' }}>{p.date} · {p.read} read</div>
              </button>
            ))}
          </div>
        </div>
      </section>
    </main>
  );
}

function BlogPost({ slug, onBack }) {
  const p = POSTS.find(x => x.slug === slug) || POSTS[0];
  return (
    <main style={{ paddingTop: 60 }} data-screen-label="Blog post">
      <article style={{ maxWidth: 720, margin: '0 auto', padding: '64px 32px' }}>
        <button onClick={onBack} style={{ background: 'transparent', border: 'none', color: '#00B3D9', fontSize: 13, fontWeight: 500, cursor: 'pointer', padding: 0, marginBottom: 24, fontFamily: "'Inter', sans-serif" }}>← All posts</button>
        <span style={{ fontSize: 12, fontWeight: 600, color: '#00B3D9', textTransform: 'uppercase', letterSpacing: '0.1em' }}>{p.tag}</span>
        <h1 style={{ fontSize: 44, fontWeight: 700, color: '#1A2E4B', lineHeight: 1.15, margin: '10px 0 14px', letterSpacing: '-0.02em' }}>{p.title}</h1>
        <div style={{ display: 'flex', gap: 14, fontSize: 12, color: '#64748B', marginBottom: 32, paddingBottom: 24, borderBottom: '1px solid #E2E8F0' }}>
          <span>By <b style={{ color: '#1A2E4B' }}>Arthur Manena</b></span>
          <span>{p.date}</span>
          <span>{p.read} read</span>
        </div>
        <div style={{ fontSize: 17, color: '#334155', lineHeight: 1.75, marginBottom: 28 }}>{p.excerpt}</div>
        <h2 style={{ fontSize: 24, fontWeight: 700, color: '#1A2E4B', marginTop: 32, marginBottom: 12 }}>Why this matters in 2026</h2>
        <p style={{ fontSize: 15, color: '#334155', lineHeight: 1.75, marginBottom: 14 }}>The compliance landscape in South Africa shifts faster than the regulations themselves. Information Officers now bear personal liability for POPIA breaches, FICA reviews have tightened CDD thresholds, and the sanctions universe expanded by 12% year-over-year.</p>
        <p style={{ fontSize: 15, color: '#334155', lineHeight: 1.75, marginBottom: 14 }}>What's changed isn't just the regulations — it's the expectation. Customers expect onboarding in minutes. Regulators expect audit trails. Boards expect zero surprises.</p>
        <h2 style={{ fontSize: 24, fontWeight: 700, color: '#1A2E4B', marginTop: 32, marginBottom: 12 }}>Three things to do this quarter</h2>
        <ol style={{ paddingLeft: 22, fontSize: 15, color: '#334155', lineHeight: 1.75 }}>
          <li style={{ marginBottom: 10 }}><b style={{ color: '#1A2E4B' }}>Refresh consent capture.</b> Every existing customer record needs POPIA-compliant consent on file. Re-consent at next interaction.</li>
          <li style={{ marginBottom: 10 }}><b style={{ color: '#1A2E4B' }}>Audit your provider tree.</b> Every third-party vendor handling personal data needs an operator agreement. Map it; close gaps.</li>
          <li style={{ marginBottom: 10 }}><b style={{ color: '#1A2E4B' }}>Run a tabletop exercise.</b> Simulate a subject access request, end-to-end, within your stated SLA. Find what breaks.</li>
        </ol>
        <div style={{ marginTop: 40, padding: 24, background: '#F8FAFC', borderLeft: '3px solid #00B3D9', borderRadius: 4, fontSize: 14, color: '#334155', fontStyle: 'italic' }}>
          This is a sample of the long-form content VeriGate publishes. Real posts live at verigate.co.za/blog.
        </div>
      </article>
    </main>
  );
}

/* ──────────────────────────────────────────────────────────
   RESOURCES
   ──────────────────────────────────────────────────────── */
function Resources() {
  const res = [
    { type: 'Whitepaper', icon: 'FileSearch', title: "POPIA compliance — operator's playbook", size: '34 pages · PDF' },
    { type: 'Case study', icon: 'BarChart',   title: 'Tier-1 bank cuts onboarding from 5 days to 11 min',  size: '8 pages · PDF' },
    { type: 'Whitepaper', icon: 'FileSearch', title: 'The economics of automated verification',             size: '22 pages · PDF' },
    { type: 'Case study', icon: 'BarChart',   title: 'How a fintech eliminated 94% of manual KYC review',   size: '6 pages · PDF' },
    { type: 'Template',   icon: 'Zap',        title: 'POPIA consent capture — copy & paste templates',      size: '4 templates · DOCX' },
    { type: 'Template',   icon: 'Zap',        title: 'Risk policy starter pack for fintech',                 size: '3 policies · JSON' },
  ];
  return (
    <main style={{ paddingTop: 60 }} data-screen-label="Resources">
      <section style={{ padding: '64px 32px', background: '#F8FAFC' }}>
        <div style={{ maxWidth: 880, margin: '0 auto', textAlign: 'center' }}>
          <span style={{ fontSize: 12, fontWeight: 600, color: '#00B3D9', textTransform: 'uppercase', letterSpacing: '0.1em' }}>Resources</span>
          <h1 style={{ fontSize: 42, fontWeight: 700, color: '#1A2E4B', margin: '8px 0 12px', letterSpacing: '-0.01em' }}>Whitepapers, case studies & templates.</h1>
        </div>
      </section>
      <section style={{ padding: '32px 32px 96px', background: '#F8FAFC' }}>
        <div style={{ maxWidth: 1000, margin: '0 auto', display: 'grid', gridTemplateColumns: 'repeat(2, 1fr)', gap: 16 }}>
          {res.map(r => (
            <div key={r.title} style={{ background: '#fff', border: '1px solid #E2E8F0', borderRadius: 8, padding: 22, display: 'flex', gap: 16, alignItems: 'flex-start' }}>
              <div style={{ width: 42, height: 42, borderRadius: 8, background: 'rgba(0,179,217,0.10)', display: 'inline-flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0 }}>
                <Icon name={r.icon} size={20} color="#00B3D9" />
              </div>
              <div style={{ flex: 1 }}>
                <span style={{ fontSize: 10, fontWeight: 600, color: '#00B3D9', textTransform: 'uppercase', letterSpacing: '0.08em' }}>{r.type}</span>
                <h4 style={{ fontSize: 15, fontWeight: 600, color: '#1A2E4B', marginTop: 4, lineHeight: 1.3 }}>{r.title}</h4>
                <div style={{ fontSize: 11, color: '#94A3B8', marginTop: 6 }}>{r.size}</div>
              </div>
              <Button variant="outline" style={{ padding: '6px 12px', fontSize: 12 }}>Download</Button>
            </div>
          ))}
        </div>
      </section>
    </main>
  );
}

/* ──────────────────────────────────────────────────────────
   LEGAL pages (Privacy / Terms / Cookies)
   ──────────────────────────────────────────────────────── */

const LEGAL = {
  privacy: {
    title: 'Privacy Policy',
    updated: 'Updated 14 April 2026',
    sections: [
      { h: 'About this policy', p: "This Privacy Policy describes how VeriGate (Pty) Ltd ('we', 'us', 'our') collects, uses, and protects the personal information of natural persons who interact with our website, partner portal, and verification services." },
      { h: 'Information we collect', p: "We collect: account details (name, work email, organisation); verification subject data passed to us by our partners under POPIA-compliant consent (ID numbers, names, biometric data, document images, addresses); technical data (IP addresses, device identifiers, session timings); and billing details where applicable." },
      { h: 'Lawful basis under POPIA', p: 'We process personal information under the lawful bases of consent (s.11(1)(a)), contract performance (s.11(1)(b)), legal obligation (s.11(1)(c)), and legitimate interest (s.11(1)(f)). The applicable basis is documented per data category.' },
      { h: 'Operator agreements', p: 'When you submit subject data to VeriGate for verification, we act as an Operator under POPIA. Our standard Operator Agreement governs this relationship and is available on request.' },
      { h: 'Retention', p: 'Verification records are retained for 7 years per FICA s.42, or longer where the partner requires it. Consent records are retained for the same period to evidence POPIA compliance.' },
      { h: 'Your rights', p: 'You have the right to access, correct, and (where applicable) delete personal information we hold. To exercise these rights, contact our Information Officer at io@verigate.co.za. We respond within 30 days.' },
      { h: 'Contact', p: 'Information Officer · Arthur Manena · io@verigate.co.za · +27 82 211 8921 · 1 Cinnebar Street, Table View, 7441, South Africa.' },
    ],
  },
  terms: {
    title: 'Terms of Service',
    updated: 'Updated 14 April 2026',
    sections: [
      { h: 'Acceptance', p: "By accessing or using VeriGate's website, partner portal, or APIs (the 'Services'), you agree to these Terms of Service. If you don't agree, don't use the Services." },
      { h: 'Eligibility', p: 'You must be at least 18 years old, have authority to bind your organisation, and operate within South Africa (or another jurisdiction agreed in writing with VeriGate).' },
      { h: 'Account responsibilities', p: 'You are responsible for safeguarding credentials, keeping account information accurate, and ensuring authorised use only. You must enable MFA for all team members with admin access.' },
      { h: 'Acceptable use', p: 'You may not use VeriGate to (a) verify subjects without their POPIA-compliant consent, (b) circumvent rate limits or security controls, (c) resell or sublicense the Services without our written consent, or (d) attempt to extract or scrape data from the platform.' },
      { h: 'Service availability', p: 'We target 99.5% uptime for Starter/Business plans and 99.95% for Enterprise. Maintenance windows are notified at least 7 days in advance, except for emergency security patches.' },
      { h: 'Fees and billing', p: 'Fees are listed at verigate.co.za/pricing. Annual plans are non-refundable. Pay-as-you-go is billed monthly in arrears. Unpaid invoices over 30 days result in service suspension.' },
      { h: 'Liability', p: 'To the maximum extent permitted by law, our total liability is limited to the fees paid by you in the 12 months preceding the claim.' },
      { h: 'Governing law', p: 'These Terms are governed by the laws of South Africa. Disputes are resolved in the Western Cape Division of the High Court.' },
    ],
  },
  cookies: {
    title: 'Cookie Policy',
    updated: 'Updated 14 April 2026',
    sections: [
      { h: 'What cookies are', p: 'Cookies are small text files stored on your device when you visit a website. They help us remember you, understand how you use VeriGate, and improve our services.' },
      { h: 'Cookies we use', p: 'Strictly necessary (authentication, CSRF protection); functional (language, preferences); analytics (anonymised page-view aggregation via Plausible — no third-party trackers); none for advertising.' },
      { h: 'Your choices', p: 'You can disable cookies in your browser settings, but strictly necessary cookies are required for the partner portal to function. We never set advertising cookies.' },
      { h: 'No third-party tracking', p: "We don't use Google Analytics, Facebook Pixel, or any cross-site tracker. Our analytics is self-hosted Plausible, aggregating page views only — no individual user profiles." },
    ],
  },
};

function Legal({ kind }) {
  const d = LEGAL[kind] || LEGAL.privacy;
  return (
    <main style={{ paddingTop: 60 }} data-screen-label={`Legal · ${d.title}`}>
      <article style={{ maxWidth: 760, margin: '0 auto', padding: '64px 32px 96px' }}>
        <span style={{ fontSize: 12, fontWeight: 600, color: '#00B3D9', textTransform: 'uppercase', letterSpacing: '0.1em' }}>Legal</span>
        <h1 style={{ fontSize: 38, fontWeight: 700, color: '#1A2E4B', margin: '8px 0 6px', letterSpacing: '-0.01em' }}>{d.title}</h1>
        <p style={{ fontSize: 12, color: '#94A3B8', marginBottom: 32 }}>{d.updated} · VeriGate (Pty) Ltd · Reg. 2025/525145/07</p>
        {d.sections.map(s => (
          <section key={s.h} style={{ marginBottom: 28 }}>
            <h2 style={{ fontSize: 19, fontWeight: 700, color: '#1A2E4B', marginBottom: 8 }}>{s.h}</h2>
            <p style={{ fontSize: 14, color: '#334155', lineHeight: 1.75 }}>{s.p}</p>
          </section>
        ))}
      </article>
    </main>
  );
}

/* ──────────────────────────────────────────────────────────
   404
   ──────────────────────────────────────────────────────── */
function Site404({ onGoHome }) {
  return (
    <main style={{ paddingTop: 60, background: 'linear-gradient(160deg,#0F1A2E,#1A2E4B 60%,#1a3a5c)', minHeight: '90vh', display: 'flex', alignItems: 'center', justifyContent: 'center', position: 'relative', overflow: 'hidden' }} data-screen-label="404">
      <div style={{ position: 'absolute', inset: 0, backgroundImage: 'radial-gradient(circle, rgba(255,255,255,0.06) 1px, transparent 1px)', backgroundSize: '24px 24px' }} />
      <div style={{ position: 'absolute', inset: 0, background: 'radial-gradient(ellipse 60% 50% at 50% 50%, rgba(0,179,217,0.12) 0%, transparent 70%)' }} />
      <div style={{ position: 'relative', textAlign: 'center', maxWidth: 520, padding: 32, color: '#fff' }}>
        <svg width="110" height="120" viewBox="20 25 112 122" style={{ filter: 'drop-shadow(0 8px 24px rgba(226,61,54,0.4))', marginBottom: 18 }}>
          <path fill="#E23D36" d="M76 30 C56 30 26 39 26 42 L26 74 C26 106 50 132 76 142 C102 132 126 106 126 74 L126 42 C126 39 96 30 76 30 Z"/>
          <line x1="46" y1="58" x2="106" y2="102" stroke="#fff" strokeWidth="13" strokeLinecap="round" />
          <line x1="106" y1="58" x2="46" y2="102" stroke="#fff" strokeWidth="13" strokeLinecap="round" />
        </svg>
        <div style={{ fontSize: 110, fontWeight: 700, lineHeight: 1, letterSpacing: '-0.04em', background: 'linear-gradient(90deg,#22d3ee,#60a5fa,#06b6d4)', WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent', backgroundClip: 'text', marginBottom: 10 }}>404</div>
        <h1 style={{ fontSize: 26, fontWeight: 600, marginBottom: 12 }}>This page doesn't exist.</h1>
        <p style={{ fontSize: 15, color: 'rgba(255,255,255,0.7)', lineHeight: 1.6, marginBottom: 28 }}>The link you followed may be broken, or the page may have been moved. Try the homepage or get in touch if you got here from a VeriGate link.</p>
        <div style={{ display: 'flex', gap: 10, justifyContent: 'center', flexWrap: 'wrap' }}>
          <Button variant="hero" onClick={onGoHome}>← Back to homepage</Button>
          <Button variant="ghost" style={{ color: '#fff' }}>Contact support</Button>
        </div>
      </div>
    </main>
  );
}

window.ROICalculator = ROICalculator;
window.Careers = Careers;
window.Events = Events;
window.PartnerProgram = PartnerProgram;
window.Blog = Blog;
window.BlogPost = BlogPost;
window.Resources = Resources;
window.Legal = Legal;
window.Site404 = Site404;
