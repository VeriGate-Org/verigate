/* global React, PortalIcon, PortalButton */

/* ──────────────────────────────────────────────────────────
   Branded empty / error / 404 states
   ──────────────────────────────────────────────────────── */

function EmptyState({ icon = 'FileSearch', title, body, action, onAction }) {
  return (
    <div style={{ background: '#fff', border: '1px dashed #CBD5E1', borderRadius: 8, padding: '56px 24px', textAlign: 'center' }}>
      <div style={{ width: 64, height: 64, borderRadius: 16, background: 'rgba(0,179,217,0.08)', display: 'inline-flex', alignItems: 'center', justifyContent: 'center', marginBottom: 14 }}>
        <PortalIcon name={icon} size={28} color="#00B3D9" />
      </div>
      <div style={{ fontSize: 17, fontWeight: 600, color: '#1A2024' }}>{title}</div>
      <div style={{ fontSize: 13, color: '#4F5B67', marginTop: 6, maxWidth: 420, marginLeft: 'auto', marginRight: 'auto', lineHeight: 1.55 }}>{body}</div>
      {action && (
        <div style={{ marginTop: 18 }}>
          <PortalButton variant="cta" onClick={onAction}>{action}</PortalButton>
        </div>
      )}
    </div>
  );
}

function ErrorState({ title = 'Something went wrong', body = "We couldn't load this data. Try again, or contact support if the problem persists.", onRetry, errorCode }) {
  return (
    <div style={{ background: '#fff', border: '1px solid rgba(226,61,54,0.25)', borderRadius: 8, padding: '40px 28px', textAlign: 'center', maxWidth: 520, margin: '0 auto' }}>
      <div style={{ width: 64, height: 64, borderRadius: 16, background: 'rgba(226,61,54,0.08)', display: 'inline-flex', alignItems: 'center', justifyContent: 'center', marginBottom: 14 }}>
        <PortalIcon name="X" size={28} color="#E23D36" stroke={2.5} />
      </div>
      <div style={{ fontSize: 17, fontWeight: 600, color: '#1A2024' }}>{title}</div>
      <div style={{ fontSize: 13, color: '#4F5B67', marginTop: 6, lineHeight: 1.55 }}>{body}</div>
      {errorCode && <div style={{ marginTop: 10, fontSize: 11, color: '#4F5B67', fontFamily: "'JetBrains Mono', monospace" }}>Error ref: {errorCode}</div>}
      <div style={{ marginTop: 18, display: 'flex', gap: 8, justifyContent: 'center' }}>
        <PortalButton variant="secondary">Contact support</PortalButton>
        {onRetry && <PortalButton variant="primary" onClick={onRetry}>Try again →</PortalButton>}
      </div>
    </div>
  );
}

function NotFound({ onGoHome }) {
  return (
    <div style={{ background: 'linear-gradient(160deg,#0F1A2E 0%,#1A2E4B 60%,#1a3a5c 100%)', minHeight: '100%', padding: '80px 40px', position: 'relative', overflow: 'hidden', display: 'flex', alignItems: 'center', justifyContent: 'center' }} data-screen-label="404">
      <div style={{ position: 'absolute', inset: 0, backgroundImage: 'radial-gradient(circle, rgba(255,255,255,0.06) 1px, transparent 1px)', backgroundSize: '24px 24px', pointerEvents: 'none' }} />
      <div style={{ position: 'absolute', inset: 0, background: 'radial-gradient(ellipse 60% 50% at 50% 50%, rgba(0,179,217,0.10) 0%, transparent 70%)', pointerEvents: 'none' }} />
      <div style={{ position: 'relative', textAlign: 'center', maxWidth: 480, color: '#fff' }}>
        <div style={{ position: 'relative', display: 'inline-block', marginBottom: 24 }}>
          <svg width="110" height="120" viewBox="20 25 112 122" style={{ filter: 'drop-shadow(0 8px 24px rgba(226,61,54,0.4))' }}>
            <path fill="#E23D36" d="M76 30 C56 30 26 39 26 42 L26 74 C26 106 50 132 76 142 C102 132 126 106 126 74 L126 42 C126 39 96 30 76 30 Z"/>
            <line x1="46" y1="58" x2="106" y2="102" stroke="#fff" strokeWidth="13" strokeLinecap="round" />
            <line x1="106" y1="58" x2="46" y2="102" stroke="#fff" strokeWidth="13" strokeLinecap="round" />
          </svg>
        </div>
        <div style={{ fontSize: 96, fontWeight: 700, color: '#fff', lineHeight: 1, letterSpacing: '-0.04em', marginBottom: 8, background: 'linear-gradient(90deg,#22d3ee,#60a5fa,#06b6d4)', WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent', backgroundClip: 'text' }}>404</div>
        <div style={{ fontSize: 22, fontWeight: 600, color: '#fff', marginBottom: 10 }}>This verification doesn't exist</div>
        <div style={{ fontSize: 14, color: 'rgba(255,255,255,0.7)', lineHeight: 1.6, marginBottom: 28 }}>The page you're looking for has moved, been deleted, or was never here. Check the URL, or head back to your dashboard.</div>
        <div style={{ display: 'flex', gap: 10, justifyContent: 'center', flexWrap: 'wrap' }}>
          <PortalButton variant="cta" onClick={onGoHome}>← Back to dashboard</PortalButton>
          <PortalButton variant="ghost" style={{ color: '#fff' }}>Contact support</PortalButton>
        </div>
        <div style={{ marginTop: 40, paddingTop: 24, borderTop: '1px solid rgba(255,255,255,0.10)', fontSize: 11, color: 'rgba(255,255,255,0.5)' }}>
          Error ref · <span style={{ fontFamily: "'JetBrains Mono', monospace" }}>VG-404-{Math.floor(Math.random() * 9000) + 1000}</span> · {new Date().toISOString().slice(0, 19)}Z
        </div>
      </div>
    </div>
  );
}

window.EmptyState = EmptyState;
window.ErrorState = ErrorState;
window.NotFound = NotFound;
