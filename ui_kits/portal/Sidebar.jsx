/* global React, PortalIcon */
/* Theme-aware sidebar. Pass theme="light" or "dark" via prop. */

const SIDEBAR_CSS = `
.vg-side-btn {
  width: 100%;
  display: flex; align-items: center; gap: 10px;
  padding: 7px 18px;
  background: transparent;
  border: 0; border-left: 3px solid transparent;
  font-size: 13px; font-weight: 400;
  font-family: 'Inter', sans-serif;
  cursor: pointer;
  text-align: left;
  transition: all 100ms cubic-bezier(0.4,0,0.2,1);
}
/* LIGHT */
.vg-side-btn.light { color: #4F5B67; }
.vg-side-btn.light .icon-wrap { color: #64748B; }
.vg-side-btn.light:hover { background: #F8FAFC; color: #1A2024; }
.vg-side-btn.light.active { color: #1A2E4B; background: rgba(0,179,217,0.08); border-left-color: #00B3D9; font-weight: 600; }
.vg-side-btn.light.active .icon-wrap { color: #00B3D9; }
.vg-side-btn.light.disabled { opacity: 0.45; cursor: not-allowed; }
.vg-side-btn.light.disabled:hover { background: transparent; color: #4F5B67; }
/* DARK */
.vg-side-btn.dark { color: rgba(255,255,255,0.65); }
.vg-side-btn.dark .icon-wrap { color: rgba(255,255,255,0.65); }
.vg-side-btn.dark:hover { background: rgba(255,255,255,0.05); color: #fff; }
.vg-side-btn.dark.active { color: #00B3D9; background: rgba(0,179,217,0.08); border-left-color: #00B3D9; font-weight: 600; }
.vg-side-btn.dark.active .icon-wrap { color: #00B3D9; }
.vg-side-btn.dark.disabled { opacity: 0.5; cursor: not-allowed; }
.vg-side-btn.dark.disabled:hover { background: transparent; color: rgba(255,255,255,0.65); }
.vg-side-btn.collapsed { padding: 10px 0; justify-content: center; border-left: 0; }
.vg-side-btn .label { flex: 1; }
.vg-side-btn .icon-wrap { display: inline-flex; }
`;

function PortalSidebar({ active, onSelect, collapsed = false, onToggleCollapse, theme = 'light' }) {
  const isDark = theme === 'dark';
  const sections = [
    { label: 'Overview', items: [
      { id: 'dashboard',     icon: 'Home',       label: 'Dashboard' },
      { id: 'verifications', icon: 'FileSearch', label: 'Verifications', badge: 4 },
      { id: 'cases',         icon: 'Briefcase',  label: 'Cases', badge: 'NEW' },
    ]},
    { label: 'Identity & Personal', items: [
      { id: 'kyc',           icon: 'UserCheck',  label: 'KYC' },
      { id: 'documents',     icon: 'FileSearch', label: 'Document Verification' },
      { id: 'docinsights',   icon: 'BarChart',   label: 'Document Insights', badge: 'NEW' },
      { id: 'autofill',      icon: 'Zap',        label: 'Document Auto-Fill', badge: 'NEW' },
      { id: 'bulkid',        icon: 'Layers',     label: 'Bulk Identity', badge: 'NEW' },
      { id: 'biometric',     icon: 'Shield',     label: 'Biometric' },
    ]},
    { label: 'Financial', items: [
      { id: 'bank',          icon: 'CreditCard', label: 'Bank Account' },
      { id: 'credit',        icon: 'BarChart',   label: 'Credit Check' },
      { id: 'income',        icon: 'BarChart',   label: 'Income Verification' },
      { id: 'tax',           icon: 'Briefcase',  label: 'Tax Compliance' },
      { id: 'aml',           icon: 'Shield',     label: 'AML Monitoring' },
    ]},
    { label: 'Business & Compliance', items: [
      { id: 'company',       icon: 'Briefcase',  label: 'Company & Directors' },
      { id: 'employment',    icon: 'UserCheck',  label: 'Employment' },
      { id: 'qualification', icon: 'FileSearch', label: 'Qualification' },
      { id: 'vat',           icon: 'Receipt',    label: 'VAT Vendor Search' },
      { id: 'property_own',  icon: 'Home',       label: 'Deeds Registry' },
      { id: 'deeds_map',     icon: 'Globe',      label: 'Deeds Map' },
      { id: 'property_conv', icon: 'Search',     label: 'Street / ERF' },
      { id: 'property_val',  icon: 'BarChart',   label: 'Property Valuation' },
      { id: 'corporate',     icon: 'Briefcase',  label: 'Corporate' },
      { id: 'sanctions',     icon: 'Shield',     label: 'Sanctions & PEP' },
    ]},
    { label: 'Screening', items: [
      { id: 'negnews',       icon: 'FileSearch', label: 'Negative News' },
      { id: 'fraud',         icon: 'Shield',     label: 'Fraud Watchlist' },
    ]},
    { label: 'Composite', items: [
      { id: 'composite',     icon: 'Layers',     label: 'Composite Reports', requires: 'Enterprise' },
    ]},
    { label: 'Enterprise Features', items: [
      { id: 'policies',      icon: 'Layers',     label: 'Policy Builder', badge: 'NEW' },
      { id: 'conflicts',     icon: 'UserCheck',  label: 'Conflict of Interest', badge: 'NEW' },
      { id: 'monitoring',    icon: 'Shield',     label: 'Monitoring', badge: 'NEW' },
    ]},
    { label: 'Reporting', items: [
      { id: 'reports',       icon: 'BarChart',   label: 'Reports' },
    ]},
    { label: 'Admin & Settings', items: [
      { id: 'settings',      icon: 'Cog',        label: 'Settings' },
      { id: 'admin_users',   icon: 'User',       label: 'User Management' },
      { id: 'health',        icon: 'BarChart',   label: 'System Health' },
      { id: 'help',          icon: 'FileSearch', label: 'Help & Support' },
    ]},
  ];

  // Theme tokens
  const t = isDark ? {
    bg: '#1A2E4B', wordmark: '#fff', border: 'rgba(255,255,255,0.08)',
    chevron: 'rgba(255,255,255,0.4)', searchBg: 'rgba(0,0,0,0.2)', searchBorder: 'rgba(255,255,255,0.10)',
    searchText: '#fff', searchPh: 'rgba(255,255,255,0.5)', sectLabel: 'rgba(255,255,255,0.4)',
    footerBg: 'transparent', footerText: 'rgba(255,255,255,0.35)', footerMono: 'rgba(255,255,255,0.55)',
    requires: 'rgba(255,255,255,0.4)',
  } : {
    bg: '#FFFFFF', wordmark: '#1A2E4B', border: '#E2E8F0',
    chevron: '#94A3B8', searchBg: '#F8FAFC', searchBorder: '#E2E8F0',
    searchText: '#1A2024', searchPh: '#94A3B8', sectLabel: '#94A3B8',
    footerBg: '#F8FAFC', footerText: '#94A3B8', footerMono: '#64748B',
    requires: '#94A3B8',
  };

  return (
    <aside style={{ width: collapsed ? 64 : 250, background: t.bg, color: isDark ? '#fff' : '#1A2024', height: '100%', display: 'flex', flexDirection: 'column', flexShrink: 0, transition: 'background 200ms cubic-bezier(0.4,0,0.2,1), width 200ms cubic-bezier(0.4,0,0.2,1)', overflow: 'hidden', borderRight: `1px solid ${isDark ? '#0f1f36' : '#E2E8F0'}` }}>
      <style>{SIDEBAR_CSS}</style>

      {/* Brand */}
      <div style={{ padding: collapsed ? '18px 0' : '20px 18px 16px', borderBottom: `1px solid ${t.border}`, display: 'flex', alignItems: 'center', justifyContent: collapsed ? 'center' : 'space-between', gap: 10 }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
          <svg width="22" height="24" viewBox="20 25 112 122"><path fill="#E23D36" d="M76 30 C56 30 26 39 26 42 L26 74 C26 106 50 132 76 142 C102 132 126 106 126 74 L126 42 C126 39 96 30 76 30 Z"/><path d="M46 84 L63 102 L106 58" fill="none" stroke="#FFFFFF" strokeWidth="13" strokeLinecap="round" strokeLinejoin="round"/></svg>
          {!collapsed && <span style={{ fontFamily: "'Manrope', sans-serif", fontWeight: 500, fontSize: 17, letterSpacing: '-0.5px', color: t.wordmark }}>VeriGate</span>}
        </div>
        {!collapsed && (
          <button onClick={onToggleCollapse} title="Collapse navigation" style={{ background: 'transparent', border: 'none', color: t.chevron, cursor: 'pointer', padding: 4 }}>
            <PortalIcon name="ChevronRight" size={14} color="currentColor" stroke={2.5} style={{ transform: 'rotate(180deg)' }} />
          </button>
        )}
      </div>

      {/* Search */}
      {!collapsed && (
        <div style={{ padding: '10px 14px', borderBottom: `1px solid ${t.border}` }}>
          <div style={{ position: 'relative' }}>
            <span style={{ position: 'absolute', left: 8, top: '50%', transform: 'translateY(-50%)', display: 'inline-flex' }}>
              <PortalIcon name="Search" size={12} color={t.searchPh} />
            </span>
            <input placeholder="Quick navigate…" style={{ width: '100%', padding: '6px 8px 6px 28px', background: t.searchBg, border: `1px solid ${t.searchBorder}`, borderRadius: 4, color: t.searchText, fontSize: 12, fontFamily: "'Inter', sans-serif", outline: 'none' }}
              onFocus={e => { e.currentTarget.style.borderColor = '#00B3D9'; if (!isDark) { e.currentTarget.style.background = '#fff'; e.currentTarget.style.boxShadow = '0 0 0 3px rgba(0,179,217,0.12)'; } }}
              onBlur={e => { e.currentTarget.style.borderColor = t.searchBorder; if (!isDark) { e.currentTarget.style.background = t.searchBg; e.currentTarget.style.boxShadow = 'none'; } }}
            />
          </div>
        </div>
      )}

      {/* Nav */}
      <div style={{ flex: 1, overflowY: 'auto', padding: '8px 0 16px' }}>
        {sections.map(sec => (
          <div key={sec.label}>
            {!collapsed && <div style={{ fontSize: 10, textTransform: 'uppercase', letterSpacing: '0.12em', color: t.sectLabel, padding: '12px 18px 4px', fontWeight: 600 }}>{sec.label}</div>}
            {sec.items.map(item => {
              const isActive = active === item.id;
              const disabled = !!item.requires;
              const cls = ['vg-side-btn', theme];
              if (isActive) cls.push('active');
              if (collapsed) cls.push('collapsed');
              if (disabled) cls.push('disabled');
              return (
                <button
                  key={item.id}
                  className={cls.join(' ')}
                  onClick={() => !disabled && onSelect && onSelect(item.id)}
                  title={disabled ? `Requires ${item.requires}` : item.label}
                >
                  <span className="icon-wrap"><PortalIcon name={item.icon} size={15} color="currentColor" /></span>
                  {!collapsed && <span className="label">{item.label}</span>}
                  {!collapsed && item.badge && !disabled && (
                    typeof item.badge === 'number'
                      ? <span style={{ fontSize: 10, padding: '1px 7px', background: '#E23D36', color: '#fff', borderRadius: 999, fontWeight: 600 }}>{item.badge}</span>
                      : <span style={{ fontSize: 9, padding: '2px 6px', background: '#00B3D9', color: '#fff', borderRadius: 3, fontWeight: 700, letterSpacing: '0.06em' }}>{item.badge}</span>
                  )}
                  {!collapsed && disabled && <span style={{ fontSize: 9, color: t.requires, textTransform: 'uppercase', letterSpacing: '0.06em', fontWeight: 600 }}>{item.requires}</span>}
                </button>
              );
            })}
          </div>
        ))}
      </div>

      {/* Footer */}
      {!collapsed && (
        <div style={{ borderTop: `1px solid ${t.border}`, padding: '12px 18px', fontSize: 10, color: t.footerText, background: t.footerBg }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 6, marginBottom: 2 }}>
            <span style={{ width: 6, height: 6, borderRadius: 999, background: '#2C974B', boxShadow: '0 0 6px #2C974B' }} />
            <span>All services operational</span>
          </div>
          <div style={{ fontFamily: "'JetBrains Mono', monospace", color: t.footerMono }}>v2026.04 · Build #1428</div>
        </div>
      )}
    </aside>
  );
}

window.PortalSidebar = PortalSidebar;
