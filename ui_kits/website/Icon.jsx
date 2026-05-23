/* global React, lucide */
const { useState } = React;

/* ──────────────────────────────────────────────────────────
   Shared icon helper — Lucide is loaded globally from CDN
   ──────────────────────────────────────────────────────── */
function Icon({ name, size = 20, color = 'currentColor', stroke = 2, ...rest }) {
  // Map of icon paths inlined to avoid CDN race on first paint
  const paths = {
    Shield:        '<path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>',
    UserCheck:     '<path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><polyline points="16 11 18 13 22 9"/>',
    FileSearch:    '<path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><circle cx="11.5" cy="14.5" r="2.5"/><line x1="13.5" y1="16.5" x2="15.5" y2="18.5"/>',
    Zap:           '<polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"/>',
    Check:         '<polyline points="20 6 9 17 4 12"/>',
    CheckCircle:   '<path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/>',
    ArrowRight:    '<line x1="5" y1="12" x2="19" y2="12"/><polyline points="12 5 19 12 12 19"/>',
    Fingerprint:   '<path d="M2 12a2 2 0 0 0 2-2V7a2 2 0 1 1 4 0v4M14 13V5a2 2 0 1 1 4 0v6M18 11v2a8 8 0 0 1-8 8M8 11a2 2 0 1 1 4 0v3M10 11v3"/>',
    Database:      '<ellipse cx="12" cy="5" rx="9" ry="3"/><path d="M21 12c0 1.66-4 3-9 3s-9-1.34-9-3M3 5v14c0 1.66 4 3 9 3s9-1.34 9-3V5"/>',
    Menu:          '<line x1="3" y1="6" x2="21" y2="6"/><line x1="3" y1="12" x2="21" y2="12"/><line x1="3" y1="18" x2="21" y2="18"/>',
    Globe:         '<circle cx="12" cy="12" r="10"/><line x1="2" y1="12" x2="22" y2="12"/><path d="M12 2a15.3 15.3 0 0 1 4 10 15.3 15.3 0 0 1-4 10 15.3 15.3 0 0 1-4-10 15.3 15.3 0 0 1 4-10z"/>',
  };
  const d = paths[name] || paths.Check;
  return (
    <svg width={size} height={size} viewBox="0 0 24 24" fill="none" stroke={color} strokeWidth={stroke} strokeLinecap="round" strokeLinejoin="round" {...rest} dangerouslySetInnerHTML={{ __html: d }} />
  );
}

window.Icon = Icon;
