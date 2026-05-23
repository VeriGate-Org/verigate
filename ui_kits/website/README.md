# VeriGate Website UI Kit

The public marketing site. Audience: prospects and visitors landing from search/ads.

**Design language**
- Navy (#1A2E4B) primary · Cyan (#00B3D9) accent · Red (#E23D36) for the shield only
- Hero gradients (160deg navy stack + radial cyan glow + 8% dot-grid overlay)
- Generous spacing (64–96px section padding)
- Framer Motion scroll animations: fade-in `y: 20→0` over 600ms, staggered 100ms
- Rounded 6px buttons; 8px cards; cyan glow on hero CTAs
- Lucide icons in cyan stroke inside `rgba(0,179,217,0.10)` wells

**Index** — open `index.html` for an interactive walkthrough (homepage → features → pricing → CTA, with smooth scroll).

**Components**
- `Logo.jsx` — primary lockup, supports `dark` (navy bg) and shield-only variants
- `Nav.jsx` — sticky top nav, logo + links + cyan CTA
- `Hero.jsx` — navy gradient + dot-grid + text-gradient headline + glow CTA
- `FeatureGrid.jsx` — 3-col cards with cyan-well icons, hover lift
- `StatsBar.jsx` — 4-up navy stat strip (red number, white tracked label)
- `Section.jsx` — generic section with eyebrow + heading + body
- `Button.jsx` — `hero` / `default` / `outline` / `ghost` / `destructive`
- `Footer.jsx` — navy footer with tri-bar bottom strip

Built against `../../colors_and_type.css`.
