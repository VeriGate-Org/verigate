# Pricing Page Quick Reference

## Access the Page
- **URL:** `http://localhost:8080/pricing`
- **Navigation:** Click "Pricing" in the main navigation menu

## Pricing Tiers Overview

### Starter - $249/mo (annual) | $299/mo (monthly)
**Target:** Startups and small businesses
**Key Features:**
- 500 verifications/month
- Basic KYC verification
- Document verification
- Email support
- 99.5% uptime SLA

### Professional - $665/mo (annual) | $799/mo (monthly) ⭐ MOST POPULAR
**Target:** Growing companies
**Key Features:**
- 5,000 verifications/month
- Advanced KYC & KYB verification
- Biometric + document verification
- Priority support (email & chat)
- AML screening (basic)
- 99.9% uptime SLA
- **Savings:** $1,608/year on annual plan

### Enterprise - Custom Pricing
**Target:** Large organizations
**Key Features:**
- Unlimited verifications
- Full KYC, KYB & AML suite
- 24/7 premium support
- White-label solution
- Custom workflows
- 99.99% uptime SLA
- Dedicated account manager

## Add-ons Available
1. **Advanced AML Screening** - $0.50 per check
2. **Ongoing Monitoring** - $99/month
3. **Premium Support** - $499/month
4. **Custom Integration** - Custom pricing

## Page Sections (in order)
1. Hero with billing toggle
2. Pricing cards (3 tiers)
3. Add-ons section
4. Detailed comparison table
5. FAQ (8 questions)
6. Final CTA section

## Key Features
✅ Monthly/Annual toggle (20% savings on annual)
✅ Responsive design (mobile-first)
✅ Professional card layout
✅ "Most Popular" badge on Professional tier
✅ Highlighted Professional plan (slightly larger)
✅ Feature comparison with checkmarks
✅ Comprehensive FAQ section
✅ Multiple CTAs for conversion

## CTAs on Page
- "Start Free Trial" (appears 4 times)
- "Contact Sales" (Enterprise plan)
- "Schedule a Demo" (final CTA section)
- "Add to Plan" (for each add-on)

## Trust Elements
- "14-day free trial"
- "No credit card required"
- "Cancel anytime"
- SLA guarantees (99.5%, 99.9%, 99.99%)
- Feature transparency

## Next Steps for Full Functionality
1. Connect CTAs to contact forms
2. Add calendar integration for demos (Calendly/HubSpot)
3. Set up email capture for trials
4. Implement CRM integration
5. Add analytics tracking
6. Set up A/B testing

## Design System Used
- **Colors:** HSL-based VeriGate brand colors
- **Primary:** Navy blue (217 91% 18%)
- **Accent:** Cyan (193 95% 48%)
- **Components:** shadcn/ui (Card, Button, Tooltip)
- **Icons:** Lucide React
- **Typography:** Existing font system

## Responsive Breakpoints
- Mobile: < 768px (stacked layout)
- Tablet: 768px - 1024px (2-column grid)
- Desktop: > 1024px (3-column grid with scale effect)

## File Structure
```
src/
├── pages/
│   ├── Pricing.tsx (NEW - main pricing page)
│   ├── Index.tsx (home page)
│   └── NotFound.tsx
├── components/
│   ├── Navigation.tsx (UPDATED - added pricing link)
│   └── ui/ (shadcn components)
└── App.tsx (UPDATED - added /pricing route)
```

## Development Commands
```bash
# Run development server
npm run dev

# Build for production
npm run build

# Preview production build
npm run preview
```

## Browser Testing
✅ Chrome/Edge (Chromium)
✅ Firefox
✅ Safari
✅ Mobile browsers (responsive)

---
Last Updated: January 2025
