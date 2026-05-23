/* global React */

function Modal({ open, title, body, primaryLabel = 'Confirm', secondaryLabel = 'Cancel', primaryVariant = 'primary', onPrimary, onClose }) {
  if (!open) return null;
  const variantStyle = {
    primary: { background: '#1A2E4B', borderColor: '#0f1f36', color: '#fff' },
    cta: { background: '#00B3D9', borderColor: '#00B3D9', color: '#fff' },
    destructive: { background: '#E23D36', borderColor: '#E23D36', color: '#fff' },
  }[primaryVariant];
  return (
    <div onClick={onClose} style={{ position: 'fixed', inset: 0, background: 'rgba(15,26,46,0.45)', backdropFilter: 'blur(2px)', display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 100, animation: 'fadeIn 200ms cubic-bezier(0.4,0,0.2,1)' }}>
      <div onClick={e => e.stopPropagation()} style={{ background: '#fff', borderRadius: 8, padding: '20px 24px 18px', width: 460, maxWidth: 'calc(100vw - 32px)', boxShadow: '0 8px 16px 4px rgba(0,28,36,0.18)', border: '1px solid #D5DBDB' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 6 }}>
          <span style={{ fontSize: 16, fontWeight: 600, color: '#1A2024' }}>{title}</span>
          <button onClick={onClose} style={{ background: 'transparent', border: 'none', cursor: 'pointer', color: '#4F5B67', fontSize: 18, lineHeight: 1 }}>×</button>
        </div>
        <div style={{ fontSize: 13, color: '#4F5B67', lineHeight: 1.55, marginBottom: 20 }}>{body}</div>
        <div style={{ display: 'flex', gap: 8, justifyContent: 'flex-end' }}>
          <button onClick={onClose} style={{ padding: '7px 14px', borderRadius: 2, fontSize: 13, fontWeight: 500, background: '#fff', border: '1px solid #687078', color: '#1A2024', cursor: 'pointer', fontFamily: "'Inter', sans-serif" }}>{secondaryLabel}</button>
          <button onClick={onPrimary} style={{ padding: '7px 14px', borderRadius: 2, fontSize: 13, fontWeight: 500, ...variantStyle, border: '1px solid', cursor: 'pointer', fontFamily: "'Inter', sans-serif" }}>{primaryLabel}</button>
        </div>
      </div>
    </div>
  );
}

window.PortalModal = Modal;
