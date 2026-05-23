# VeriGate Partner Portal UI Kit

The operational console where partners run verifications, review reports, and manage billing. **Surface = `portal`** — set `data-surface="portal"` on `<html>` to flip semantic tokens.

**Design language**
- AWS Cloudscape-inspired: compact, data-dense, sharp corners
- Console Blue (#0972D3) primary · CTA Orange (#EC7211) · semantic green/red/amber
- Fixed navy sidebar (260px / collapsed 60px) — sections: Overview · Identity · Financial · Compliance · Screening · Composite · Enterprise · Configuration
- Topbar with breadcrumbs
- 2px button radius · 8px card radius · 100ms hover only — no scroll animations
- Status badges with unicode glyphs: `✓ ✗ ⚠ ⓘ ⊙`
- Dark mode via `data-theme="dark"` (not implemented in this kit yet)

**Index** — open `index.html` for an interactive dashboard. Click a verification row to drill down; the "+ New verification" button opens a modal.

**Components**
- `Logo.jsx` — same primary lockup as website (shared)
- `Sidebar.jsx` — fixed navy nav with sections, search, favourites, collapsible
- `TopBar.jsx` — breadcrumb + search + user menu
- `Button.jsx` — `primary` / `secondary` / `cta` / `destructive` / `ghost` / `link`
- `Badge.jsx` — status pills with leading unicode glyph
- `Table.jsx` — AWS-style table with sticky header
- `StatCard.jsx` — dashboard tiles
- `Modal.jsx` — overlay dialog with destructive confirmation pattern
- `FormField.jsx` — labelled inputs with cyan focus ring

Built against `../../colors_and_type.css`.
