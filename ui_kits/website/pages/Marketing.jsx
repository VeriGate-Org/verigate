/* global React, Button, Icon, Logo */
/* ──────────────────────────────────────────────────────────
   Tier 5 — Marketing essentials
   Pricing · Platform · Compare · Contact · Request Demo · Technical Support
   FAQ · About · South Africa · Integrations · Supported Docs · Hub template
   ──────────────────────────────────────────────────────── */

const { useState: useStateM2 } = React;

function SectionHeader({ eyebrow, title, subtitle, dark, center = true }) {
  return (
    <div style={{ textAlign: center ? 'center' : 'left', maxWidth: 720, margin: center ? '0 auto 48px' : '0 0 32px' }}>
      <span style={{ fontSize: 12, fontWeight: 600, color: '#00B3D9', textTransform: 'uppercase', letterSpacing: '0.1em' }}>{eyebrow}</span>
      <h2 style={{ fontSize: 38, fontWeight: 700, color: dark ? '#fff' : '#1A2E4B', margin: '8px 0 12px', letterSpacing: '-0.01em', lineHeight: 1.2 }}>{title}</h2>
      {subtitle && <p style={{ fontSize: 16, color: dark ? 'rgba(255,255,255,0.7)' : '#64748B', lineHeight: 1.6 }}>{subtitle}</p>}
    </div>
  );
}

/* ──────────────────────────────────────────────────────────
   PRICING
   ──────────────────────────────────────────────────────── */

const PLANS = [
  { name: 'Starter',    price: 'R 0',     unit: '/ month', subtitle: 'Pay only for what you verify',  popular: false,
    items: [
      'Pay-as-you-go pricing',
      'All 28+ verification types',
      'POPIA-compliant by default',
      'Standard turnaround (1–5 business days)',
      'Email support',
      '1 team seat',
    ] },
  { name: 'Business',   price: 'R 1,499', unit: '/ month', subtitle: 'For growing teams with regular volume', popular: true,
    items: [
      'Everything in Starter',
      '50 KYC checks included',
      'Partner Portal + Policy Builder',
      'Bulk uploads (up to 5,000/job)',
      'Priority support · 4-hour response',
      '5 team seats · role-based access',
      'API + webhook access',
    ] },
  { name: 'Enterprise', price: 'Custom',  unit: '',         subtitle: 'Volume pricing for compliance-critical orgs', popular: false,
    items: [
      'Everything in Business',
      'Unlimited verifications',
      'Continuous monitoring + alerts',
      'Composite Reports & AI Assistant',
      'Dedicated Customer Success Manager',
      'Unlimited team seats',
      'Custom SLAs · 99.95% uptime',
      'Single-tenant deployment available',
    ] },
];

function Pricing() {
  return (
    <main style={{ paddingTop: 60, background: '#fff' }} data-screen-label="Pricing">
      <section style={{ padding: '64px 32px 32px', background: 'linear-gradient(180deg, #F8FAFC, #fff)' }}>
        <div style={{ maxWidth: 1100, margin: '0 auto' }}>
          <SectionHeader eyebrow="Pricing" title="Pricing that scales with you." subtitle="Start free, scale to enterprise. Every plan is POPIA-compliant out of the box." />
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: 20 }}>
            {PLANS.map(p => (
              <div key={p.name} style={{ background: p.popular ? '#FFFBFB' : '#fff', border: `1px solid ${p.popular ? '#D63031' : '#E2E8F0'}`, borderTop: `3px solid ${p.popular ? '#D63031' : '#1A2E4B'}`, borderRadius: 8, padding: 32, position: 'relative' }}>
                {p.popular && <div style={{ position: 'absolute', top: -12, left: '50%', transform: 'translateX(-50%)', background: '#D63031', color: '#fff', fontSize: 10, fontWeight: 700, letterSpacing: '0.12em', padding: '4px 14px', borderRadius: 4 }}>MOST POPULAR</div>}
                <div style={{ fontSize: 14, fontWeight: 700, color: '#1A2E4B', textTransform: 'uppercase', letterSpacing: '0.06em' }}>{p.name}</div>
                <div style={{ fontSize: 12, color: '#64748B', marginTop: 4, minHeight: 32 }}>{p.subtitle}</div>
                <div style={{ fontSize: 44, fontWeight: 700, color: '#1A2E4B', lineHeight: 1, marginTop: 16 }}>{p.price}<span style={{ fontSize: 14, fontWeight: 400, color: '#94A3B8', marginLeft: 6 }}>{p.unit}</span></div>
                <Button variant={p.popular ? 'hero' : 'outline'} style={{ width: '100%', marginTop: 20 }}>{p.name === 'Enterprise' ? 'Talk to sales' : 'Start with ' + p.name}</Button>
                <ul style={{ listStyle: 'none', padding: 0, marginTop: 24, fontSize: 14, color: '#334155', display: 'flex', flexDirection: 'column', gap: 10 }}>
                  {p.items.map(i => (
                    <li key={i} style={{ display: 'flex', gap: 10, lineHeight: 1.5 }}>
                      <span style={{ color: '#2C974B', fontWeight: 700, flexShrink: 0 }}>✓</span>
                      {i}
                    </li>
                  ))}
                </ul>
              </div>
            ))}
          </div>
          <div style={{ marginTop: 56, textAlign: 'center', fontSize: 14, color: '#64748B' }}>
            All plans include POPIA-compliant data handling, 99.5% SLA, and access to <b style={{ color: '#1A2E4B' }}>14+ official sources</b> (SAPS, DHA, SAQA, Umalusi, TransUnion, CIPC).
          </div>
        </div>
      </section>

      {/* Comparison table */}
      <section style={{ padding: '64px 32px', background: '#fff' }}>
        <div style={{ maxWidth: 1100, margin: '0 auto' }}>
          <SectionHeader eyebrow="Compare" title="Compare plans feature-by-feature" />
          <div style={{ overflow: 'hidden', border: '1px solid #E2E8F0', borderRadius: 8 }}>
            <table style={{ width: '100%', borderCollapse: 'separate', borderSpacing: 0, fontSize: 14 }}>
              <thead>
                <tr style={{ background: '#F1F5F9' }}>
                  <th style={{ padding: '14px 18px', textAlign: 'left', fontWeight: 600, color: '#1A2E4B' }}>Feature</th>
                  <th style={{ padding: '14px 18px', fontWeight: 600, color: '#1A2E4B' }}>Starter</th>
                  <th style={{ padding: '14px 18px', fontWeight: 600, color: '#D63031', background: '#FFFBFB' }}>Business</th>
                  <th style={{ padding: '14px 18px', fontWeight: 600, color: '#1A2E4B' }}>Enterprise</th>
                </tr>
              </thead>
              <tbody>
                {[
                  ['28+ verification types',  '✓', '✓', '✓'],
                  ['POPIA compliance',         '✓', '✓', '✓'],
                  ['Partner Portal access',    '—', '✓', '✓'],
                  ['Policy Builder',           '—', '✓', '✓'],
                  ['Bulk uploads',             '—', 'Up to 5,000/job', 'Unlimited'],
                  ['Continuous monitoring',    '—', '—', '✓'],
                  ['Composite Reports',        '—', '—', '✓'],
                  ['AI Assistant',             '—', '—', '✓'],
                  ['Team seats',               '1', '5', 'Unlimited'],
                  ['Support',                  'Email', 'Priority · 4h', '24/7 · 1h · CSM'],
                  ['SLA',                      '99.5%', '99.5%', '99.95%'],
                  ['Webhooks + API',           '—', '✓', '✓'],
                  ['Single-tenant',            '—', '—', '✓'],
                ].map((row, i) => (
                  <tr key={i} style={{ background: i % 2 === 0 ? '#fff' : '#F8FAFC' }}>
                    <td style={{ padding: '12px 18px', borderTop: '1px solid #E2E8F0', fontWeight: 500, color: '#334155' }}>{row[0]}</td>
                    <td style={{ padding: '12px 18px', borderTop: '1px solid #E2E8F0', textAlign: 'center', color: row[1] === '—' ? '#94A3B8' : '#1A2E4B' }}>{row[1]}</td>
                    <td style={{ padding: '12px 18px', borderTop: '1px solid #E2E8F0', textAlign: 'center', background: 'rgba(214,48,49,0.04)', color: row[2] === '—' ? '#94A3B8' : '#1A2E4B', fontWeight: row[2] !== '—' ? 600 : 400 }}>{row[2]}</td>
                    <td style={{ padding: '12px 18px', borderTop: '1px solid #E2E8F0', textAlign: 'center', color: row[3] === '—' ? '#94A3B8' : '#1A2E4B' }}>{row[3]}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </section>

      {/* FAQ teaser */}
      <section style={{ padding: '64px 32px 96px', background: '#F8FAFC' }}>
        <div style={{ maxWidth: 760, margin: '0 auto' }}>
          <SectionHeader eyebrow="Common questions" title="Pricing questions, answered" />
          {[
            { q: 'Is there a setup fee?',                                   a: 'No. Sign up, run a check, get charged only for what you use. Starter has no monthly minimum.' },
            { q: "What's included in the 50 KYC checks on Business?",       a: 'Standard KYC — ID + DHA + sanctions + basic credit. Add-ons (biometric, criminal) are billed à la carte at standard rates.' },
            { q: 'Do you offer non-profit or B-BBEE pricing?',              a: 'Yes. We offer 25% off all plans for Level 1 B-BBEE accredited organisations and registered non-profits. Get in touch.' },
            { q: 'Can I switch plans anytime?',                              a: 'Yes — upgrade or downgrade from the portal. Annual billing locks your rate; monthly is flexible.' },
          ].map((f, i) => (
            <details key={i} style={{ background: '#fff', border: '1px solid #E2E8F0', borderRadius: 8, padding: '14px 20px', marginBottom: 10 }}>
              <summary style={{ fontSize: 15, fontWeight: 600, color: '#1A2E4B', cursor: 'pointer', listStyle: 'none', display: 'flex', justifyContent: 'space-between' }}>{f.q}<span style={{ color: '#00B3D9' }}>+</span></summary>
              <div style={{ fontSize: 14, color: '#64748B', marginTop: 10, lineHeight: 1.6 }}>{f.a}</div>
            </details>
          ))}
        </div>
      </section>
    </main>
  );
}

/* ──────────────────────────────────────────────────────────
   PLATFORM — long-form product page
   ──────────────────────────────────────────────────────── */

function Platform() {
  return (
    <main style={{ paddingTop: 60, background: '#fff' }} data-screen-label="Platform">
      {/* Hero */}
      <section style={{ background: 'linear-gradient(160deg,#0F1A2E 0%,#1A2E4B 40%,#1a3a5c 70%,#0d2440 100%)', padding: '96px 32px 80px', position: 'relative', overflow: 'hidden' }}>
        <div style={{ position: 'absolute', inset: 0, backgroundImage: 'radial-gradient(circle, rgba(255,255,255,0.07) 1px, transparent 1px)', backgroundSize: '24px 24px' }} />
        <div style={{ position: 'relative', maxWidth: 1100, margin: '0 auto', textAlign: 'center' }}>
          <span style={{ fontSize: 12, fontWeight: 600, color: '#00B3D9', textTransform: 'uppercase', letterSpacing: '0.1em' }}>The platform</span>
          <h1 style={{ fontSize: 60, fontWeight: 700, color: '#fff', lineHeight: 1.05, letterSpacing: '-0.02em', margin: '12px auto 16px', maxWidth: 880 }}>
            One platform. <span style={{ background: 'linear-gradient(90deg,#22d3ee,#60a5fa,#06b6d4)', WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent', backgroundClip: 'text' }}>Every verification.</span>
          </h1>
          <p style={{ fontSize: 18, color: 'rgba(255,255,255,0.75)', lineHeight: 1.6, maxWidth: 640, margin: '0 auto' }}>
            From real-time KYC to ongoing sanctions monitoring, the VeriGate platform handles the entire identity and risk journey through one API and one portal.
          </p>
        </div>
      </section>

      {/* Capability grid */}
      <section style={{ padding: '96px 32px', background: '#fff' }}>
        <div style={{ maxWidth: 1100, margin: '0 auto' }}>
          <SectionHeader eyebrow="Capabilities" title="Four pillars, one platform." />
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(2, 1fr)', gap: 24 }}>
            {[
              { icon: 'UserCheck',  title: 'Identity', desc: 'KYC, document verification, biometric capture, and the full SA ID + DHA + SAPS stack. From one-off checks to bulk batches.' },
              { icon: 'FileSearch', title: 'Compliance', desc: 'POPIA, AML/CFT, FICA. Continuous sanctions screening, PEP monitoring, and case management for what needs human review.' },
              { icon: 'Shield',     title: 'Risk Intelligence', desc: 'Composite scoring across all signals — credit, criminal, adverse media, fraud watchlists — into a single decision.' },
              { icon: 'Zap',        title: 'Operations', desc: 'Policy Builder, bulk processing, monitoring dashboards, an AI assistant, and webhooks to feed any downstream system.' },
            ].map(c => (
              <div key={c.title} style={{ border: '1px solid #E2E8F0', borderRadius: 8, padding: 32, background: '#fff' }}>
                <div style={{ width: 48, height: 48, borderRadius: 10, background: 'rgba(0,179,217,0.10)', display: 'inline-flex', alignItems: 'center', justifyContent: 'center', marginBottom: 14 }}>
                  <Icon name={c.icon} size={24} color="#00B3D9" />
                </div>
                <h3 style={{ fontSize: 22, fontWeight: 700, color: '#1A2E4B', marginBottom: 8 }}>{c.title}</h3>
                <p style={{ fontSize: 14, color: '#64748B', lineHeight: 1.6 }}>{c.desc}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* How it works */}
      <section style={{ padding: '96px 32px', background: '#F8FAFC' }}>
        <div style={{ maxWidth: 1100, margin: '0 auto' }}>
          <SectionHeader eyebrow="How it works" title="From submission to decision in under 3 minutes." subtitle="The same workflow runs whether you're verifying one person or ten thousand." />
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: 16, position: 'relative' }}>
            {[
              { n: '01', t: 'Submit', d: 'Through the portal, the API, or as a bulk CSV. Subject consent captured under POPIA.' },
              { n: '02', t: 'Verify', d: 'We check against 14+ official sources in parallel — DHA, SAPS, sanctions, credit, biometric.' },
              { n: '03', t: 'Score',  d: 'Your Policy Builder applies weighted scoring + override rules to produce a single composite risk.' },
              { n: '04', t: 'Decide', d: 'Approve, route to manual review, or reject. Decision flows back via webhook or the case queue.' },
            ].map((s, i) => (
              <div key={s.n} style={{ position: 'relative', background: '#fff', borderRadius: 8, padding: 24, border: '1px solid #E2E8F0' }}>
                <div style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: 14, fontWeight: 700, color: '#00B3D9' }}>{s.n}</div>
                <h4 style={{ fontSize: 18, fontWeight: 700, color: '#1A2E4B', margin: '6px 0 8px' }}>{s.t}</h4>
                <p style={{ fontSize: 13, color: '#64748B', lineHeight: 1.6 }}>{s.d}</p>
                {i < 3 && <div style={{ position: 'absolute', top: '50%', right: -10, transform: 'translateY(-50%)', color: '#CBD5E1', fontSize: 18, display: 'inline-flex' }}>→</div>}
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* CTA strip */}
      <section style={{ padding: '64px 32px', background: '#1A2E4B', position: 'relative', overflow: 'hidden' }}>
        <div style={{ position: 'absolute', top: 0, left: 0, right: 0, height: 3, background: 'linear-gradient(90deg,#D63031 25%,#1A2E4B 25% 75%,#00B3D9 75%)' }} />
        <div style={{ maxWidth: 760, margin: '0 auto', textAlign: 'center' }}>
          <h2 style={{ fontSize: 32, fontWeight: 700, color: '#fff', lineHeight: 1.2 }}>See it running with your data.</h2>
          <p style={{ fontSize: 16, color: 'rgba(255,255,255,0.7)', marginTop: 12, marginBottom: 24 }}>Book a 30-minute session — we'll wire up a test policy and run it against a sample of your subjects.</p>
          <div style={{ display: 'flex', gap: 12, justifyContent: 'center', flexWrap: 'wrap' }}>
            <Button variant="hero">Book a demo</Button>
            <Button variant="ghost" style={{ color: '#fff' }}>Talk to sales →</Button>
          </div>
        </div>
      </section>
    </main>
  );
}

/* ──────────────────────────────────────────────────────────
   CONTACT, REQUEST DEMO, TECHNICAL SUPPORT — forms
   ──────────────────────────────────────────────────────── */

function FormPage({ title, eyebrow, subtitle, fields, sidebar, submitLabel }) {
  const [sent, setSent] = useStateM2(false);
  return (
    <main style={{ paddingTop: 60, background: '#fff' }} data-screen-label={title}>
      <section style={{ padding: '64px 32px', background: '#F8FAFC' }}>
        <div style={{ maxWidth: 1100, margin: '0 auto', display: 'grid', gridTemplateColumns: '1.4fr 1fr', gap: 56 }}>
          <div>
            <span style={{ fontSize: 12, fontWeight: 600, color: '#00B3D9', textTransform: 'uppercase', letterSpacing: '0.1em' }}>{eyebrow}</span>
            <h1 style={{ fontSize: 40, fontWeight: 700, color: '#1A2E4B', margin: '8px 0 12px', letterSpacing: '-0.01em', lineHeight: 1.2 }}>{title}</h1>
            <p style={{ fontSize: 16, color: '#64748B', lineHeight: 1.6, marginBottom: 32 }}>{subtitle}</p>
            {sent ? (
              <div style={{ background: 'rgba(44,151,75,0.06)', border: '1px solid rgba(44,151,75,0.3)', borderRadius: 8, padding: 28, textAlign: 'center' }}>
                <div style={{ width: 56, height: 56, borderRadius: 999, background: '#2C974B', display: 'inline-flex', alignItems: 'center', justifyContent: 'center', marginBottom: 14 }}>
                  <Icon name="CheckCircle" size={26} color="#fff" stroke={2.5} />
                </div>
                <div style={{ fontSize: 18, fontWeight: 600, color: '#1A2E4B' }}>Thanks — we've got it.</div>
                <div style={{ fontSize: 13, color: '#64748B', marginTop: 6 }}>Someone from the team will be in touch within 4 business hours.</div>
              </div>
            ) : (
              <form onSubmit={e => { e.preventDefault(); setSent(true); }} style={{ background: '#fff', border: '1px solid #E2E8F0', borderRadius: 8, padding: 28, display: 'flex', flexDirection: 'column', gap: 16 }}>
                {fields.map((row, ri) => (
                  <div key={ri} style={{ display: 'grid', gridTemplateColumns: row.length === 1 ? '1fr' : 'repeat(2, 1fr)', gap: 14 }}>
                    {row.map(f => (
                      <div key={f.name}>
                        <label style={{ display: 'block', fontSize: 12, fontWeight: 500, color: '#1A2E4B', marginBottom: 5 }}>{f.label}{f.required && <span style={{ color: '#E23D36', marginLeft: 2 }}>*</span>}</label>
                        {f.type === 'textarea' ? (
                          <textarea placeholder={f.placeholder} rows={5} required={f.required} style={{ width: '100%', padding: '10px 12px', border: '1px solid #E2E8F0', borderRadius: 6, fontSize: 14, fontFamily: "'Inter', sans-serif", outline: 'none', resize: 'vertical' }} />
                        ) : f.type === 'select' ? (
                          <select required={f.required} style={{ width: '100%', padding: '10px 12px', border: '1px solid #E2E8F0', borderRadius: 6, fontSize: 14, fontFamily: "'Inter', sans-serif", background: '#fff' }}>
                            {f.options.map(o => <option key={o}>{o}</option>)}
                          </select>
                        ) : (
                          <input type={f.type || 'text'} placeholder={f.placeholder} required={f.required} style={{ width: '100%', padding: '10px 12px', border: '1px solid #E2E8F0', borderRadius: 6, fontSize: 14, fontFamily: "'Inter', sans-serif", outline: 'none' }} />
                        )}
                      </div>
                    ))}
                  </div>
                ))}
                <label style={{ display: 'flex', gap: 8, alignItems: 'flex-start', fontSize: 12, color: '#64748B', cursor: 'pointer' }}>
                  <input type="checkbox" required style={{ accentColor: '#00B3D9', marginTop: 2 }} />
                  I agree to VeriGate's POPIA-compliant <a href="#" style={{ color: '#00B3D9' }}>privacy policy</a>. We'll only use this to respond.
                </label>
                <Button variant="hero" style={{ width: '100%' }}>{submitLabel} →</Button>
              </form>
            )}
          </div>
          <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
            {sidebar}
          </div>
        </div>
      </section>
    </main>
  );
}

function Contact() {
  return (
    <FormPage
      eyebrow="Get in touch"
      title="Talk to a real person."
      subtitle="Sales, partnerships, compliance — drop us a line and we'll route you to the right human."
      submitLabel="Send message"
      fields={[
        [{ name: 'first', label: 'First name', required: true }, { name: 'last', label: 'Last name', required: true }],
        [{ name: 'email', label: 'Work email', type: 'email', required: true }, { name: 'phone', label: 'Phone (optional)', type: 'tel' }],
        [{ name: 'company', label: 'Company', required: true }],
        [{ name: 'topic', label: 'What can we help with?', type: 'select', required: true, options: ['— Select —', 'Sales enquiry', 'Partnership', 'POPIA / compliance question', 'Support escalation', 'Press', 'Other'] }],
        [{ name: 'message', label: 'Tell us more', type: 'textarea', placeholder: 'A few sentences about what you need', required: true }],
      ]}
      sidebar={(
        <>
          <div style={{ background: '#fff', border: '1px solid #E2E8F0', borderRadius: 8, padding: 24 }}>
            <div style={{ fontSize: 12, fontWeight: 600, color: '#00B3D9', textTransform: 'uppercase', letterSpacing: '0.1em', marginBottom: 14 }}>Reach us directly</div>
            {[
              { icon: 'Globe', label: 'info@verigate.co.za' },
              { icon: 'User',  label: '+27 82 211 8921' },
              { icon: 'Shield',label: '1 Cinnebar St, Table View, 7441' },
            ].map(c => (
              <div key={c.label} style={{ display: 'flex', alignItems: 'center', gap: 12, padding: '8px 0', borderBottom: '1px solid #F1F5F9' }}>
                <div style={{ width: 32, height: 32, borderRadius: 6, background: 'rgba(0,179,217,0.10)', display: 'inline-flex', alignItems: 'center', justifyContent: 'center' }}>
                  <Icon name={c.icon} size={14} color="#00B3D9" />
                </div>
                <span style={{ fontSize: 13, color: '#334155', fontWeight: 500 }}>{c.label}</span>
              </div>
            ))}
          </div>
          <div style={{ background: '#1A2E4B', borderRadius: 8, padding: 24, color: '#fff', position: 'relative', overflow: 'hidden' }}>
            <div style={{ position: 'absolute', top: 0, left: 0, right: 0, height: 3, background: 'linear-gradient(90deg,#D63031 25%,#1A2E4B 25% 75%,#00B3D9 75%)' }} />
            <div style={{ fontSize: 12, fontWeight: 600, color: '#00B3D9', textTransform: 'uppercase', letterSpacing: '0.1em', marginBottom: 8 }}>Office hours</div>
            <div style={{ fontSize: 14, lineHeight: 1.7 }}>Mon–Fri · 08:00–18:00 SAST<br />Sat · 09:00–13:00 SAST<br /><span style={{ color: 'rgba(255,255,255,0.6)' }}>Sun · Closed</span></div>
          </div>
        </>
      )}
    />
  );
}

function RequestDemo() {
  return (
    <FormPage
      eyebrow="See it live"
      title="Book a 30-minute demo."
      subtitle="We'll wire up a test policy with your actual subject data and walk you through the platform end-to-end."
      submitLabel="Book demo"
      fields={[
        [{ name: 'first', label: 'First name', required: true }, { name: 'last', label: 'Last name', required: true }],
        [{ name: 'email', label: 'Work email', type: 'email', required: true }, { name: 'role', label: 'Your role', required: true }],
        [{ name: 'company', label: 'Company', required: true }, { name: 'size', label: 'Company size', type: 'select', required: true, options: ['1–10', '11–50', '51–200', '201–1,000', '1,000+'] }],
        [{ name: 'volume', label: 'Monthly verification volume', type: 'select', required: true, options: ['< 100', '100–1,000', '1,000–10,000', '10,000+'] }],
        [{ name: 'when', label: 'When would you like to chat?', type: 'select', required: true, options: ['This week', 'Next week', 'Within 2 weeks', 'Flexible'] }],
        [{ name: 'notes', label: 'Anything specific you want to see?', type: 'textarea', placeholder: 'Specific verification types, integrations, use cases…' }],
      ]}
      sidebar={(
        <>
          <div style={{ background: '#fff', border: '1px solid #E2E8F0', borderRadius: 8, padding: 24 }}>
            <div style={{ fontSize: 12, fontWeight: 600, color: '#00B3D9', textTransform: 'uppercase', letterSpacing: '0.1em', marginBottom: 14 }}>What to expect</div>
            {[
              { n: '1', t: 'A real conversation', d: 'No-pressure 30-minute call. We ask about your stack and compliance needs.' },
              { n: '2', t: 'A live platform tour',   d: 'We walk through the portal with your data shape — KYC, sanctions, policy builder.' },
              { n: '3', t: 'A working test policy',  d: 'You leave with a sandbox account + a policy ready for your subjects.' },
            ].map(s => (
              <div key={s.n} style={{ display: 'flex', gap: 12, padding: '12px 0', borderBottom: '1px solid #F1F5F9' }}>
                <div style={{ width: 28, height: 28, borderRadius: 999, background: '#1A2E4B', color: '#00B3D9', display: 'inline-flex', alignItems: 'center', justifyContent: 'center', fontFamily: "'JetBrains Mono', monospace", fontSize: 12, fontWeight: 700, flexShrink: 0 }}>{s.n}</div>
                <div>
                  <div style={{ fontSize: 13, fontWeight: 600, color: '#1A2E4B' }}>{s.t}</div>
                  <div style={{ fontSize: 12, color: '#64748B', marginTop: 2, lineHeight: 1.5 }}>{s.d}</div>
                </div>
              </div>
            ))}
          </div>
          <div style={{ background: '#F8FAFC', border: '1px dashed #CBD5E1', borderRadius: 8, padding: 16, fontSize: 12, color: '#64748B', lineHeight: 1.55 }}>
            <b style={{ color: '#1A2E4B' }}>Quick tip:</b> bring 5–10 sample subjects (IDs only, no PII) so we can run actual policy on real data during the call.
          </div>
        </>
      )}
    />
  );
}

function TechnicalSupport() {
  return (
    <FormPage
      eyebrow="Technical support"
      title="We've got your back."
      subtitle="Existing customer? Open a ticket. Browse the docs, or jump straight to the live chat."
      submitLabel="Open ticket"
      fields={[
        [{ name: 'email', label: 'Account email', type: 'email', required: true }],
        [{ name: 'subject', label: 'Subject', required: true }],
        [{ name: 'severity', label: 'Severity', type: 'select', required: true, options: ['— Select —', 'Sev 1 · Production down', 'Sev 2 · Major degradation', 'Sev 3 · Minor issue', 'Sev 4 · Question'] }],
        [{ name: 'service', label: 'Affected service', type: 'select', required: true, options: ['— Select —', 'KYC', 'Sanctions', 'Document Verification', 'Bulk Identity', 'Policy Builder', 'API / Webhooks', 'Other'] }],
        [{ name: 'desc', label: 'Description (include correlation IDs if any)', type: 'textarea', required: true }],
      ]}
      sidebar={(
        <>
          <div style={{ background: '#fff', border: '1px solid #E2E8F0', borderRadius: 8, padding: 24 }}>
            <div style={{ fontSize: 12, fontWeight: 600, color: '#00B3D9', textTransform: 'uppercase', letterSpacing: '0.1em', marginBottom: 14 }}>Response targets</div>
            {[
              { sev: 'Sev 1', target: 'Within 30 min · 24/7', color: '#E23D36' },
              { sev: 'Sev 2', target: 'Within 2 hr · 24/7',    color: '#C28B0B' },
              { sev: 'Sev 3', target: '4 business hours',      color: '#00B3D9' },
              { sev: 'Sev 4', target: '1 business day',         color: '#64748B' },
            ].map(r => (
              <div key={r.sev} style={{ display: 'flex', justifyContent: 'space-between', padding: '8px 0', borderBottom: '1px solid #F1F5F9', fontSize: 13 }}>
                <span style={{ color: r.color, fontWeight: 600 }}>{r.sev}</span>
                <span style={{ color: '#334155' }}>{r.target}</span>
              </div>
            ))}
          </div>
          <div style={{ background: '#1A2E4B', borderRadius: 8, padding: 24, color: '#fff' }}>
            <div style={{ fontSize: 13, fontWeight: 600, marginBottom: 8 }}>Need it now?</div>
            <div style={{ fontSize: 12, color: 'rgba(255,255,255,0.7)', lineHeight: 1.6, marginBottom: 14 }}>For production-down emergencies, call the on-call line directly.</div>
            <div style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: 16, color: '#00B3D9', fontWeight: 600 }}>+27 82 211 8921</div>
          </div>
        </>
      )}
    />
  );
}

/* ──────────────────────────────────────────────────────────
   FAQ
   ──────────────────────────────────────────────────────── */

function FAQ() {
  const cats = [
    { name: 'Getting started', q: [
      { q: 'How quickly can we be live?',                          a: 'Most teams are running their first production verification within 24 hours of signing up. Enterprise integrations take 1–2 weeks.' },
      { q: 'Do we need to be POPIA-compliant before signing up?',  a: "No — VeriGate handles the operator side. You still need consent capture, but we provide POPIA-compliant templates and the audit log." },
      { q: 'What about integrations?',                              a: 'REST API, webhook callbacks, native SDKs for Node + Python + Java, plus a Zapier connector for low-code automation.' },
    ]},
    { name: 'Verifications', q: [
      { q: "Which verification types do you support?",              a: '28+ types across identity, financial, business, screening, and property categories. See /verification-types for the full catalogue.' },
      { q: 'How fresh is the data?',                                a: 'Real-time pulls from official sources. Sanctions data refreshes every 4 hours. Credit, criminal, employment are point-in-time queries.' },
      { q: 'What about subject consent?',                           a: 'POPIA-compliant digital consent capture is built-in. You can also pass pre-captured consent via the API.' },
    ]},
    { name: 'Pricing', q: [
      { q: 'Are there volume discounts?',                          a: 'Yes — Enterprise pricing kicks in past 1,000 monthly verifications. Discounts range from 15–40% depending on commit.' },
      { q: 'Failed verifications — do we still pay?',              a: "You pay for the check, not the outcome. A 'failed' verification still consumed source-system queries on our side." },
    ]},
  ];
  return (
    <main style={{ paddingTop: 60, background: '#fff' }} data-screen-label="FAQ">
      <section style={{ padding: '80px 32px 32px', background: '#F8FAFC' }}>
        <div style={{ maxWidth: 760, margin: '0 auto', textAlign: 'center' }}>
          <span style={{ fontSize: 12, fontWeight: 600, color: '#00B3D9', textTransform: 'uppercase', letterSpacing: '0.1em' }}>FAQs</span>
          <h1 style={{ fontSize: 44, fontWeight: 700, color: '#1A2E4B', margin: '8px 0 12px', letterSpacing: '-0.01em' }}>Frequently asked questions</h1>
          <p style={{ fontSize: 16, color: '#64748B', lineHeight: 1.6 }}>Can't find what you need? <a href="#contact" style={{ color: '#00B3D9' }}>Get in touch →</a></p>
        </div>
      </section>
      <section style={{ padding: '32px 32px 96px', background: '#F8FAFC' }}>
        <div style={{ maxWidth: 760, margin: '0 auto' }}>
          {cats.map(c => (
            <div key={c.name} style={{ marginBottom: 36 }}>
              <h3 style={{ fontSize: 20, fontWeight: 600, color: '#1A2E4B', marginBottom: 14 }}>{c.name}</h3>
              {c.q.map(item => (
                <details key={item.q} style={{ background: '#fff', border: '1px solid #E2E8F0', borderRadius: 8, padding: '14px 20px', marginBottom: 10 }}>
                  <summary style={{ fontSize: 15, fontWeight: 600, color: '#1A2E4B', cursor: 'pointer', listStyle: 'none', display: 'flex', justifyContent: 'space-between' }}>{item.q}<span style={{ color: '#00B3D9' }}>+</span></summary>
                  <div style={{ fontSize: 14, color: '#64748B', marginTop: 10, lineHeight: 1.6 }}>{item.a}</div>
                </details>
              ))}
            </div>
          ))}
        </div>
      </section>
    </main>
  );
}

/* ──────────────────────────────────────────────────────────
   ABOUT (combined with South Africa credentials)
   ──────────────────────────────────────────────────────── */

function About() {
  return (
    <main style={{ paddingTop: 60, background: '#fff' }} data-screen-label="About">
      <section style={{ background: 'linear-gradient(160deg,#0F1A2E 0%,#1A2E4B 60%,#1a3a5c 100%)', padding: '96px 32px', color: '#fff', position: 'relative', overflow: 'hidden' }}>
        <div style={{ position: 'absolute', inset: 0, backgroundImage: 'radial-gradient(circle, rgba(255,255,255,0.06) 1px, transparent 1px)', backgroundSize: '24px 24px' }} />
        <div style={{ position: 'relative', maxWidth: 920, margin: '0 auto', textAlign: 'center' }}>
          <span style={{ fontSize: 12, fontWeight: 600, color: '#00B3D9', textTransform: 'uppercase', letterSpacing: '0.1em' }}>About VeriGate</span>
          <h1 style={{ fontSize: 56, fontWeight: 700, lineHeight: 1.1, letterSpacing: '-0.02em', margin: '12px 0 16px' }}>Built in South Africa, for South African compliance.</h1>
          <p style={{ fontSize: 18, color: 'rgba(255,255,255,0.75)', lineHeight: 1.6 }}>VeriGate exists because POPIA and FICA don't take excuses. We make the right thing — the compliant thing — also the fastest, easiest thing.</p>
        </div>
      </section>

      <section style={{ padding: '96px 32px', background: '#fff' }}>
        <div style={{ maxWidth: 1100, margin: '0 auto', display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: 24 }}>
          {[
            { v: '2025', l: 'Founded' },
            { v: '28+', l: 'Verification types' },
            { v: '14+', l: 'Official sources' },
            { v: 'Level 1', l: 'B-BBEE accredited' },
          ].map(s => (
            <div key={s.l} style={{ textAlign: 'center' }}>
              <div style={{ fontSize: 44, fontWeight: 700, color: '#E23D36' }}>{s.v}</div>
              <div style={{ fontSize: 12, color: '#64748B', textTransform: 'uppercase', letterSpacing: '0.08em', marginTop: 4 }}>{s.l}</div>
            </div>
          ))}
        </div>
      </section>

      <section style={{ padding: '64px 32px', background: '#F8FAFC' }}>
        <div style={{ maxWidth: 920, margin: '0 auto' }}>
          <SectionHeader eyebrow="Mission" title="The right thing should be the easy thing." />
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 32, fontSize: 15, color: '#334155', lineHeight: 1.7 }}>
            <p>For most South African businesses, compliance feels like a tax on growth. KYC checks take days. Sanctions data is stale. POPIA puts the burden on the operator, not the vendor.</p>
            <p>We're building the platform we wished existed when we ran compliance teams ourselves — one that's POPIA-compliant by default, fast enough to run during a phone call, and honest about the trade-offs.</p>
          </div>
        </div>
      </section>

      <section style={{ padding: '96px 32px', background: '#fff' }}>
        <div style={{ maxWidth: 1100, margin: '0 auto' }}>
          <SectionHeader eyebrow="South African credentials" title="Local-first. Globally compatible." />
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: 16 }}>
            {[
              { title: 'POPIA-compliant',  body: 'Registered Information Officer. Operator agreement template. 7-year consent retention. Subject access requests automated.' },
              { title: 'Level 1 B-BBEE',   body: '100% Black-owned. Eligible Enterprise Development / Supplier Development beneficiary contributions.' },
              { title: 'FICA-aligned',     body: 'Risk-based approach to customer due diligence. Continuous PEP and sanctions monitoring built in.' },
              { title: 'Direct integrations', body: 'SAPS-AFIS · DHA · SAQA · Umalusi · TransUnion · CIPC · SARS. No intermediaries, no proxies.' },
              { title: 'Western Cape based', body: 'HQ in Table View. SA business hours support. SLA-backed phone line for production incidents.' },
              { title: 'Reg. 2025/525145/07', body: 'Private company registered with CIPC. Tax-compliant with SARS. Annual returns up to date.' },
            ].map(c => (
              <div key={c.title} style={{ background: '#fff', border: '1px solid #E2E8F0', borderRadius: 8, padding: 24, borderTop: '3px solid #1A2E4B' }}>
                <h4 style={{ fontSize: 16, fontWeight: 700, color: '#1A2E4B', marginBottom: 8 }}>{c.title}</h4>
                <p style={{ fontSize: 13, color: '#64748B', lineHeight: 1.6 }}>{c.body}</p>
              </div>
            ))}
          </div>
        </div>
      </section>
    </main>
  );
}

/* ──────────────────────────────────────────────────────────
   HUB TEMPLATE (used by Verification Types, Compliance, Fraud, Solutions)
   ──────────────────────────────────────────────────────── */

const HUB_DATA = {
  'verification-types': {
    eyebrow: 'Verification catalogue',
    title: 'Every verification type, in one platform.',
    subtitle: '28+ verification types across identity, business, financial, screening, and property categories.',
    categories: [
      { id: 'identity', name: 'Identity', icon: 'UserCheck', slugs: ['ID Verification', 'Document Verification', 'Biometric / Liveness', 'SA ID Smart Card', 'Passport', "Driver's License"] },
      { id: 'business', name: 'Business', icon: 'Briefcase', slugs: ['Company & Directors', 'UBO Identification', 'CIPC Lookup', 'VAT Vendor Search', 'BEE Verification'] },
      { id: 'financial', name: 'Financial', icon: 'CreditCard', slugs: ['Credit Bureau', 'Bank Account (AVS)', 'Income Verification', 'Tax Compliance'] },
      { id: 'screening', name: 'Screening', icon: 'Shield', slugs: ['Sanctions', 'PEP', 'Adverse Media', 'Fraud Watchlist', 'Criminal Record'] },
      { id: 'property',  name: 'Property', icon: 'Home', slugs: ['Deeds Registry', 'Property Valuation', 'Street / ERF Conversion', 'Deeds Map'] },
    ],
  },
  'compliance': {
    eyebrow: 'Compliance',
    title: 'Compliance, automated.',
    subtitle: 'POPIA, FICA, AML — the regulatory layer that protects you and your customers.',
    categories: [
      { id: 'kyc',  name: 'KYC',           icon: 'UserCheck', slugs: ['Customer KYC', 'KYB for business onboarding', 'Re-screening & refresh'] },
      { id: 'popia',name: 'POPIA',         icon: 'Shield',    slugs: ['Consent capture', 'Subject access requests', 'Operator agreements', 'Data retention'] },
      { id: 'fica', name: 'FICA / AML',    icon: 'BarChart',  slugs: ['Risk-based CDD', 'Sanctions screening', 'Suspicious activity reporting'] },
    ],
  },
  'fraud-prevention': {
    eyebrow: 'Fraud prevention',
    title: 'Stop fraud at the door.',
    subtitle: 'Identity fraud, synthetic IDs, account takeover, mule networks — caught before they cost you.',
    categories: [
      { id: 'identity', name: 'Identity fraud',     icon: 'UserCheck', slugs: ['Document tampering', 'Synthetic identity', 'Impersonation'] },
      { id: 'session',  name: 'Session / device',  icon: 'Zap',       slugs: ['Device fingerprinting', 'Account takeover detection'] },
      { id: 'pattern',  name: 'Pattern detection', icon: 'BarChart',  slugs: ['Velocity rules', 'Mule networks', 'Adverse media'] },
    ],
  },
  'solutions': {
    eyebrow: 'Solutions by industry',
    title: 'Built for industries that take compliance seriously.',
    subtitle: "Templates and policies tuned for each industry's regulatory baseline.",
    categories: [
      { id: 'banking', name: 'Banking & Finance', icon: 'CreditCard', slugs: ['Tier-1 retail bank', 'Niche lender', 'Insurance', 'Asset manager'] },
      { id: 'fintech', name: 'Fintech',           icon: 'Zap',         slugs: ['Wallets', 'BNPL', 'Cards', 'Cross-border payments'] },
      { id: 'crypto',  name: 'Crypto & Web3',     icon: 'Shield',      slugs: ['Exchanges', 'Custodians', 'Stablecoin issuers'] },
      { id: 'other',   name: 'Other',             icon: 'Briefcase',   slugs: ['Gaming', 'Healthcare', 'E-commerce', 'Real estate', 'Travel'] },
    ],
  },
};

function HubTemplate({ slug }) {
  const d = HUB_DATA[slug] || HUB_DATA['verification-types'];
  return (
    <main style={{ paddingTop: 60, background: '#fff' }} data-screen-label={`Hub · ${slug}`}>
      <section style={{ background: 'linear-gradient(160deg,#0F1A2E,#1A2E4B 60%,#1a3a5c)', padding: '80px 32px', color: '#fff', position: 'relative', overflow: 'hidden' }}>
        <div style={{ position: 'absolute', inset: 0, backgroundImage: 'radial-gradient(circle, rgba(255,255,255,0.06) 1px, transparent 1px)', backgroundSize: '24px 24px' }} />
        <div style={{ position: 'relative', maxWidth: 920, margin: '0 auto', textAlign: 'center' }}>
          <span style={{ fontSize: 12, fontWeight: 600, color: '#00B3D9', textTransform: 'uppercase', letterSpacing: '0.1em' }}>{d.eyebrow}</span>
          <h1 style={{ fontSize: 46, fontWeight: 700, lineHeight: 1.15, letterSpacing: '-0.02em', margin: '12px 0 14px' }}>{d.title}</h1>
          <p style={{ fontSize: 16, color: 'rgba(255,255,255,0.75)', lineHeight: 1.6 }}>{d.subtitle}</p>
        </div>
      </section>
      <section style={{ padding: '64px 32px 96px', background: '#fff' }}>
        <div style={{ maxWidth: 1100, margin: '0 auto', display: 'flex', flexDirection: 'column', gap: 40 }}>
          {d.categories.map(cat => (
            <div key={cat.id}>
              <div style={{ display: 'flex', alignItems: 'center', gap: 12, marginBottom: 18 }}>
                <div style={{ width: 44, height: 44, borderRadius: 10, background: 'rgba(0,179,217,0.10)', display: 'inline-flex', alignItems: 'center', justifyContent: 'center' }}>
                  <Icon name={cat.icon} size={20} color="#00B3D9" />
                </div>
                <h3 style={{ fontSize: 22, fontWeight: 700, color: '#1A2E4B' }}>{cat.name}</h3>
              </div>
              <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: 14 }}>
                {cat.slugs.map(s => (
                  <a key={s} href="#" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '14px 18px', background: '#F8FAFC', border: '1px solid #E2E8F0', borderRadius: 8, textDecoration: 'none', color: '#1A2E4B', fontSize: 14, fontWeight: 500, transition: 'all 200ms cubic-bezier(0.4,0,0.2,1)' }}
                    onMouseEnter={e => { e.currentTarget.style.borderColor = 'rgba(0,179,217,0.5)'; e.currentTarget.style.background = '#fff'; e.currentTarget.style.transform = 'translateY(-2px)'; e.currentTarget.style.boxShadow = '0 8px 16px -4px rgba(0,179,217,0.1)'; }}
                    onMouseLeave={e => { e.currentTarget.style.borderColor = '#E2E8F0'; e.currentTarget.style.background = '#F8FAFC'; e.currentTarget.style.transform = 'translateY(0)'; e.currentTarget.style.boxShadow = 'none'; }}>
                    {s}
                    <span style={{ color: '#00B3D9' }}>→</span>
                  </a>
                ))}
              </div>
            </div>
          ))}
        </div>
      </section>
    </main>
  );
}

window.Pricing = Pricing;
window.Platform = Platform;
window.Contact = Contact;
window.RequestDemo = RequestDemo;
window.TechnicalSupport = TechnicalSupport;
window.FAQ = FAQ;
window.About = About;
window.HubTemplate = HubTemplate;
