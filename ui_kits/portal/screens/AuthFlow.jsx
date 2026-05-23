/* global React, PortalIcon, PortalButton */
const { useState: useStateA, useRef: useRefA, useEffect: useEffectA } = React;

/* ──────────────────────────────────────────────────────────
   AuthFlow — sign in → MFA → forgot → reset
   Manages view state internally; calls onAuthenticated() on success.
   ──────────────────────────────────────────────────────── */

function AuthShellCard({ children, footer }) {
  return (
    <div style={{ width: 400, maxWidth: 'calc(100vw - 32px)', background: '#fff', borderRadius: 8, boxShadow: '0 20px 40px -10px rgba(0,28,36,0.4)', overflow: 'hidden', border: '1px solid #D5DBDB' }}>
      {/* Tri-bar accent */}
      <div style={{ display: 'flex', height: 3 }}>
        <div style={{ flex: 3, background: '#E23D36' }} />
        <div style={{ flex: 5, background: '#1A2E4B' }} />
        <div style={{ flex: 2, background: '#00B3D9' }} />
      </div>
      {children}
      {footer && (
        <div style={{ padding: '14px 28px', borderTop: '1px solid #e9ebed', background: '#F8FAFC', fontSize: 11, color: '#4F5B67', textAlign: 'center' }}>
          {footer}
        </div>
      )}
    </div>
  );
}

function FormField({ id, label, type = 'text', value, onChange, placeholder, autoComplete, hint, mono, error, ...rest }) {
  return (
    <div>
      <label htmlFor={id} style={{ display: 'block', fontSize: 11, fontWeight: 500, color: '#1A2024', marginBottom: 4 }}>{label}</label>
      <input
        id={id}
        type={type}
        value={value}
        onChange={onChange}
        placeholder={placeholder}
        autoComplete={autoComplete}
        style={{ width: '100%', padding: '9px 11px', border: `1px solid ${error ? '#E23D36' : '#D5DBDB'}`, borderRadius: 4, fontSize: 13, fontFamily: mono ? "'JetBrains Mono', monospace" : "'Inter', sans-serif", outline: 'none', transition: 'all 100ms cubic-bezier(0.4,0,0.2,1)' }}
        onFocus={e => { if (!error) { e.currentTarget.style.borderColor = '#00B3D9'; e.currentTarget.style.boxShadow = '0 0 0 3px rgba(0,179,217,0.15)'; } }}
        onBlur={e => { if (!error) { e.currentTarget.style.borderColor = '#D5DBDB'; e.currentTarget.style.boxShadow = 'none'; } }}
        {...rest}
      />
      {hint && <div style={{ fontSize: 10, color: '#4F5B67', marginTop: 5, lineHeight: 1.5 }}>{hint}</div>}
    </div>
  );
}

function AlertBanner({ tone = 'danger', children }) {
  const styles = {
    danger:  { bg: 'rgba(226,61,54,0.06)', border: 'rgba(226,61,54,0.35)', color: '#E23D36', glyph: '✗' },
    success: { bg: 'rgba(44,151,75,0.08)', border: 'rgba(44,151,75,0.35)', color: '#2C974B', glyph: '✓' },
    info:    { bg: 'rgba(0,179,217,0.08)', border: 'rgba(0,179,217,0.35)', color: '#0099bb', glyph: 'ⓘ' },
  }[tone];
  return (
    <div style={{ background: styles.bg, border: `1px solid ${styles.border}`, color: styles.color, borderRadius: 4, padding: '8px 12px', fontSize: 12, display: 'flex', gap: 8, alignItems: 'flex-start', lineHeight: 1.5 }}>
      <span style={{ flexShrink: 0 }}>{styles.glyph}</span>
      <span>{children}</span>
    </div>
  );
}

function OtpInput({ value, onChange, length = 6 }) {
  const refs = useRefA([]);
  const handleChange = (i, v) => {
    const clean = v.replace(/\D/g, '').slice(-1);
    const next = value.split('');
    next[i] = clean;
    const joined = next.join('').padEnd(length, '').trim().slice(0, length);
    onChange(joined);
    if (clean && i < length - 1) refs.current[i + 1]?.focus();
  };
  const handleKeyDown = (i, e) => {
    if (e.key === 'Backspace' && !value[i] && i > 0) refs.current[i - 1]?.focus();
  };
  const handlePaste = (e) => {
    const txt = e.clipboardData.getData('text').replace(/\D/g, '').slice(0, length);
    if (txt.length) { e.preventDefault(); onChange(txt.padEnd(length, '').trim().slice(0, length)); }
  };
  return (
    <div style={{ display: 'flex', gap: 8, justifyContent: 'center' }} onPaste={handlePaste}>
      {Array.from({ length }).map((_, i) => (
        <input
          key={i}
          ref={el => refs.current[i] = el}
          value={value[i] || ''}
          onChange={e => handleChange(i, e.target.value)}
          onKeyDown={e => handleKeyDown(i, e)}
          maxLength={1}
          inputMode="numeric"
          style={{ width: 42, height: 50, textAlign: 'center', fontSize: 20, fontWeight: 600, fontFamily: "'JetBrains Mono', monospace", color: '#1A2024', background: '#fff', border: `1px solid ${value[i] ? '#00B3D9' : '#D5DBDB'}`, borderRadius: 6, outline: 'none' }}
          onFocus={e => { e.currentTarget.style.borderColor = '#00B3D9'; e.currentTarget.style.boxShadow = '0 0 0 3px rgba(0,179,217,0.15)'; }}
          onBlur={e => { e.currentTarget.style.borderColor = value[i] ? '#00B3D9' : '#D5DBDB'; e.currentTarget.style.boxShadow = 'none'; }}
        />
      ))}
    </div>
  );
}

function PasswordStrength({ value }) {
  const tests = [
    { test: v => v.length >= 12,           label: '12+ characters' },
    { test: v => /[A-Z]/.test(v),          label: 'Uppercase' },
    { test: v => /[a-z]/.test(v),          label: 'Lowercase' },
    { test: v => /[0-9]/.test(v),          label: 'Number' },
    { test: v => /[^A-Za-z0-9]/.test(v),   label: 'Symbol' },
  ];
  const passed = tests.filter(t => t.test(value)).length;
  return (
    <div style={{ marginTop: 8 }}>
      <div style={{ display: 'flex', gap: 4, marginBottom: 6 }}>
        {tests.map((_, i) => (
          <div key={i} style={{ flex: 1, height: 3, borderRadius: 2, background: i < passed ? (passed < 3 ? '#E23D36' : passed < 5 ? '#C28B0B' : '#2C974B') : '#E2E8F0', transition: 'background 100ms cubic-bezier(0.4,0,0.2,1)' }} />
        ))}
      </div>
      <div style={{ display: 'flex', flexWrap: 'wrap', gap: '4px 12px', fontSize: 10, color: '#4F5B67' }}>
        {tests.map((t, i) => (
          <span key={i} style={{ color: t.test(value) ? '#2C974B' : '#4F5B67', display: 'inline-flex', alignItems: 'center', gap: 3 }}>
            {t.test(value) ? '✓' : '○'} {t.label}
          </span>
        ))}
      </div>
    </div>
  );
}

function AuthFlow({ onAuthenticated }) {
  const [view, setView] = useStateA('signin'); // signin | mfa | forgot | reset | success
  const [email, setEmail] = useStateA('arthur@verigate.co.za');
  const [password, setPassword] = useStateA('');
  const [otp, setOtp] = useStateA('');
  const [resetCode, setResetCode] = useStateA('');
  const [newPassword, setNewPassword] = useStateA('');
  const [confirmPassword, setConfirmPassword] = useStateA('');
  const [remember, setRemember] = useStateA(true);
  const [error, setError] = useStateA('');
  const [success, setSuccess] = useStateA('');
  const [loading, setLoading] = useStateA(false);

  const clearMessages = () => { setError(''); setSuccess(''); };

  const handleSignIn = (e) => {
    e.preventDefault(); clearMessages();
    if (!email || !password) return setError('Please enter your email and password.');
    setLoading(true);
    setTimeout(() => {
      setLoading(false);
      // Demo: any password works, but trip MFA challenge
      setView('mfa');
      setSuccess('Code sent to •••• 8921. Enter to continue.');
    }, 700);
  };

  const handleMfa = (e) => {
    e.preventDefault(); clearMessages();
    if (otp.length < 6) return setError('Enter the 6-digit code from your authenticator.');
    setLoading(true);
    setTimeout(() => {
      setLoading(false);
      onAuthenticated();
    }, 700);
  };

  const handleForgot = (e) => {
    e.preventDefault(); clearMessages();
    if (!email) return setError('Enter your email so we can send a code.');
    setLoading(true);
    setTimeout(() => {
      setLoading(false);
      setSuccess(`A verification code has been sent to ${email}. (Demo: use any 6-digit code.)`);
      setView('reset');
    }, 600);
  };

  const handleReset = (e) => {
    e.preventDefault(); clearMessages();
    if (resetCode.length < 6)                          return setError('Enter the 6-digit verification code.');
    if (newPassword.length < 12)                       return setError('Password must be at least 12 characters.');
    if (newPassword !== confirmPassword)               return setError('Passwords do not match.');
    setLoading(true);
    setTimeout(() => {
      setLoading(false);
      setView('success');
      setSuccess('Password reset successfully. Sign in to continue.');
    }, 800);
  };

  const brandHeader = (
    <div style={{ padding: '28px 28px 4px', textAlign: 'center' }}>
      <svg width="46" height="50" viewBox="20 25 112 122" style={{ filter: 'drop-shadow(0 2px 6px rgba(226,61,54,0.25))' }}>
        <path fill="#E23D36" d="M76 30 C56 30 26 39 26 42 L26 74 C26 106 50 132 76 142 C102 132 126 106 126 74 L126 42 C126 39 96 30 76 30 Z"/>
        <path d="M46 84 L63 102 L106 58" fill="none" stroke="#FFFFFF" strokeWidth="13" strokeLinecap="round" strokeLinejoin="round"/>
      </svg>
      <div style={{ marginTop: 10, fontFamily: "'Manrope', sans-serif", fontWeight: 500, fontSize: 22, color: '#1A2E4B', letterSpacing: '-1px' }}>VeriGate</div>
      <div style={{ fontSize: 11, color: '#4F5B67', marginTop: 2, letterSpacing: '0.02em' }}>Partner Portal</div>
    </div>
  );

  let content;
  if (view === 'signin') {
    content = (
      <form onSubmit={handleSignIn}>
        {brandHeader}
        <div style={{ padding: '20px 28px 24px', display: 'flex', flexDirection: 'column', gap: 14 }}>
          <div style={{ textAlign: 'center', fontSize: 14, fontWeight: 600, color: '#1A2024' }}>Sign in to your account</div>
          {error && <AlertBanner tone="danger">{error}</AlertBanner>}
          {success && <AlertBanner tone="success">{success}</AlertBanner>}
          <FormField id="email" label="Email address" type="email" autoComplete="email" placeholder="you@company.com" value={email} onChange={e => setEmail(e.target.value)} />
          <div>
            <FormField id="password" label="Password" type="password" autoComplete="current-password" value={password} onChange={e => setPassword(e.target.value)} />
            <div style={{ marginTop: 8, display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
              <label style={{ fontSize: 11, color: '#4F5B67', display: 'inline-flex', gap: 6, alignItems: 'center', cursor: 'pointer' }}>
                <input type="checkbox" checked={remember} onChange={e => setRemember(e.target.checked)} style={{ accentColor: '#00B3D9' }} />
                Remember me for 30 days
              </label>
              <button type="button" onClick={() => { clearMessages(); setView('forgot'); }} style={{ background: 'transparent', border: 'none', color: '#00B3D9', fontSize: 11, fontWeight: 500, cursor: 'pointer', padding: 0, fontFamily: "'Inter', sans-serif" }}>Forgot password?</button>
            </div>
          </div>
          <PortalButton variant="primary" style={{ width: '100%', padding: '10px 0', fontSize: 14 }} disabled={loading}>{loading ? 'Signing in…' : 'Sign in →'}</PortalButton>
          <div style={{ position: 'relative', textAlign: 'center', margin: '4px 0' }}>
            <span style={{ background: '#fff', padding: '0 10px', fontSize: 10, color: '#4F5B67', textTransform: 'uppercase', letterSpacing: '0.08em', position: 'relative', zIndex: 1 }}>or</span>
            <span style={{ position: 'absolute', top: '50%', left: 0, right: 0, height: 1, background: '#e9ebed' }} />
          </div>
          <button type="button" style={{ width: '100%', padding: '9px 0', background: '#fff', border: '1px solid #CBD5E1', borderRadius: 2, fontSize: 13, fontWeight: 500, color: '#1A2E4B', cursor: 'pointer', display: 'inline-flex', alignItems: 'center', justifyContent: 'center', gap: 8, fontFamily: "'Inter', sans-serif" }}>
            <svg width="14" height="14" viewBox="0 0 24 24"><path fill="#4285F4" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"/><path fill="#34A853" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/><path fill="#FBBC05" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"/><path fill="#EA4335" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"/></svg>
            Continue with Google SSO
          </button>
        </div>
      </form>
    );
  } else if (view === 'mfa') {
    content = (
      <form onSubmit={handleMfa}>
        {brandHeader}
        <div style={{ padding: '20px 28px 24px', display: 'flex', flexDirection: 'column', gap: 16 }}>
          <div style={{ textAlign: 'center' }}>
            <div style={{ width: 44, height: 44, borderRadius: 999, background: 'rgba(0,179,217,0.10)', display: 'inline-flex', alignItems: 'center', justifyContent: 'center', marginBottom: 10 }}>
              <PortalIcon name="Shield" size={22} color="#00B3D9" />
            </div>
            <div style={{ fontSize: 14, fontWeight: 600, color: '#1A2024' }}>Two-factor authentication</div>
            <div style={{ fontSize: 12, color: '#4F5B67', marginTop: 4, lineHeight: 1.55 }}>Enter the 6-digit code from your authenticator app, or the SMS we just sent to <b style={{ color: '#1A2024' }}>•••• 8921</b>.</div>
          </div>
          {error && <AlertBanner tone="danger">{error}</AlertBanner>}
          {success && !error && <AlertBanner tone="info">{success}</AlertBanner>}
          <OtpInput value={otp} onChange={setOtp} />
          <PortalButton variant="primary" style={{ width: '100%', padding: '10px 0', fontSize: 14 }} disabled={loading}>{loading ? 'Verifying…' : 'Verify & continue →'}</PortalButton>
          <div style={{ textAlign: 'center', fontSize: 11, color: '#4F5B67' }}>
            Didn't get a code? <button type="button" style={{ background: 'transparent', border: 'none', color: '#00B3D9', fontSize: 11, fontWeight: 500, cursor: 'pointer', padding: 0, fontFamily: "'Inter', sans-serif" }}>Resend</button> · <button type="button" onClick={() => { clearMessages(); setView('signin'); setOtp(''); }} style={{ background: 'transparent', border: 'none', color: '#00B3D9', fontSize: 11, fontWeight: 500, cursor: 'pointer', padding: 0, fontFamily: "'Inter', sans-serif" }}>Back to sign in</button>
          </div>
        </div>
      </form>
    );
  } else if (view === 'forgot') {
    content = (
      <form onSubmit={handleForgot}>
        {brandHeader}
        <div style={{ padding: '20px 28px 24px', display: 'flex', flexDirection: 'column', gap: 14 }}>
          <div style={{ textAlign: 'center', fontSize: 14, fontWeight: 600, color: '#1A2024' }}>Reset your password</div>
          <div style={{ textAlign: 'center', fontSize: 12, color: '#4F5B67', lineHeight: 1.55 }}>Enter your email and we'll send a verification code.</div>
          {error && <AlertBanner tone="danger">{error}</AlertBanner>}
          <FormField id="reset-email" label="Email address" type="email" placeholder="you@company.com" value={email} onChange={e => setEmail(e.target.value)} />
          <PortalButton variant="primary" style={{ width: '100%', padding: '10px 0', fontSize: 14 }} disabled={loading}>{loading ? 'Sending…' : 'Send verification code →'}</PortalButton>
          <button type="button" onClick={() => { clearMessages(); setView('signin'); }} style={{ background: 'transparent', border: 'none', color: '#00B3D9', fontSize: 12, fontWeight: 500, cursor: 'pointer', textAlign: 'center', fontFamily: "'Inter', sans-serif" }}>← Back to sign in</button>
        </div>
      </form>
    );
  } else if (view === 'reset') {
    content = (
      <form onSubmit={handleReset}>
        {brandHeader}
        <div style={{ padding: '20px 28px 24px', display: 'flex', flexDirection: 'column', gap: 14 }}>
          <div style={{ textAlign: 'center', fontSize: 14, fontWeight: 600, color: '#1A2024' }}>Choose a new password</div>
          {error && <AlertBanner tone="danger">{error}</AlertBanner>}
          {success && <AlertBanner tone="success">{success}</AlertBanner>}
          <FormField id="code" label="Verification code" mono placeholder="6-digit code" value={resetCode} onChange={e => setResetCode(e.target.value.replace(/\D/g, '').slice(0, 6))} />
          <div>
            <FormField id="new-pw" label="New password" type="password" autoComplete="new-password" value={newPassword} onChange={e => setNewPassword(e.target.value)} />
            <PasswordStrength value={newPassword} />
          </div>
          <FormField id="confirm-pw" label="Confirm new password" type="password" autoComplete="new-password" value={confirmPassword} onChange={e => setConfirmPassword(e.target.value)} error={confirmPassword && confirmPassword !== newPassword} hint={confirmPassword && confirmPassword !== newPassword ? 'Passwords do not match.' : null} />
          <PortalButton variant="primary" style={{ width: '100%', padding: '10px 0', fontSize: 14 }} disabled={loading}>{loading ? 'Resetting…' : 'Reset password →'}</PortalButton>
          <button type="button" onClick={() => { clearMessages(); setView('signin'); }} style={{ background: 'transparent', border: 'none', color: '#00B3D9', fontSize: 12, fontWeight: 500, cursor: 'pointer', textAlign: 'center', fontFamily: "'Inter', sans-serif" }}>← Back to sign in</button>
        </div>
      </form>
    );
  } else if (view === 'success') {
    content = (
      <div>
        {brandHeader}
        <div style={{ padding: '20px 28px 24px', display: 'flex', flexDirection: 'column', gap: 16, textAlign: 'center' }}>
          <div style={{ width: 56, height: 56, borderRadius: 999, background: '#2C974B', display: 'inline-flex', alignItems: 'center', justifyContent: 'center', margin: '0 auto', boxShadow: '0 0 24px rgba(44,151,75,0.4)' }}>
            <PortalIcon name="CheckCircle" size={28} color="#fff" stroke={2.5} />
          </div>
          <div style={{ fontSize: 15, fontWeight: 600, color: '#1A2024' }}>Password reset</div>
          <div style={{ fontSize: 12, color: '#4F5B67', lineHeight: 1.55 }}>Your password has been updated. Sign in to continue.</div>
          <PortalButton variant="primary" style={{ width: '100%', padding: '10px 0', fontSize: 14 }} onClick={() => { clearMessages(); setView('signin'); setPassword(''); }}>Sign in →</PortalButton>
        </div>
      </div>
    );
  }

  return (
    <div style={{ position: 'fixed', inset: 0, background: 'linear-gradient(160deg,#0F1A2E 0%,#1A2E4B 40%,#1a3a5c 70%,#0d2440 100%)', display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', padding: 20, overflowY: 'auto' }} data-screen-label="Sign in">
      {/* Background pattern */}
      <div style={{ position: 'absolute', inset: 0, backgroundImage: 'radial-gradient(circle, rgba(255,255,255,0.06) 1px, transparent 1px)', backgroundSize: '24px 24px', pointerEvents: 'none' }} />
      <div style={{ position: 'absolute', inset: 0, background: 'radial-gradient(ellipse 60% 50% at 50% 50%, rgba(0,179,217,0.12) 0%, transparent 70%)', pointerEvents: 'none' }} />

      <div style={{ position: 'relative', display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 24 }}>
        <AuthShellCard footer={<>Secure sign-in protected by Cognito · POPIA-compliant · v2026.04</>}>
          {content}
        </AuthShellCard>

        {/* Demo helper */}
        <div style={{ display: 'flex', gap: 8, alignItems: 'center', padding: '8px 14px', background: 'rgba(255,255,255,0.06)', border: '1px solid rgba(255,255,255,0.10)', borderRadius: 999, color: 'rgba(255,255,255,0.7)', fontSize: 11 }}>
          <span style={{ width: 6, height: 6, borderRadius: 999, background: '#00B3D9' }} />
          <span>Demo mode — any credentials work · <button onClick={onAuthenticated} style={{ background: 'transparent', border: 'none', color: '#00B3D9', fontSize: 11, fontWeight: 500, cursor: 'pointer', fontFamily: "'Inter', sans-serif" }}>skip to dashboard →</button></span>
        </div>

        <div style={{ fontSize: 11, color: 'rgba(255,255,255,0.5)' }}>
          New to VeriGate? <a href="#" style={{ color: '#00B3D9', textDecoration: 'none' }}>Request access →</a>
        </div>
      </div>
    </div>
  );
}

window.AuthFlow = AuthFlow;
