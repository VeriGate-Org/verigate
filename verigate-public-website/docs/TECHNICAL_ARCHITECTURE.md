# VeriGate Technical Architecture & Stack

## Technology Stack Overview

### Frontend Architecture

#### Core Framework
```
React 18.x with TypeScript
├── Vite (Build tool)
├── React Router v6 (Routing)
├── TanStack Query (Data fetching & caching)
└── React Hook Form (Form management)
```

#### UI Component Library
```
shadcn/ui (Radix UI primitives)
├── 50+ pre-built components
├── Accessible (WCAG 2.1 AA)
├── Customizable with Tailwind
└── Type-safe with TypeScript
```

#### Styling
```
Tailwind CSS 3.x
├── HSL-based design tokens
├── Custom VeriGate theme
├── Responsive utilities
├── Dark mode support (future)
└── PostCSS for processing
```

#### State Management
```
React Context API (Theme, Auth)
├── TanStack Query (Server state)
├── Local state (useState, useReducer)
└── Form state (React Hook Form)
```

#### Animation & Interactions
```
Framer Motion (recommended)
├── Page transitions
├── Scroll animations
├── Micro-interactions
└── Loading states
```

---

## Recommended Project Structure

```
verigate-public-website/
├── public/
│   ├── favicon.ico
│   ├── robots.txt
│   ├── sitemap.xml (generated)
│   └── assets/
│       ├── images/
│       ├── videos/
│       └── documents/
│
├── src/
│   ├── main.tsx
│   ├── App.tsx
│   ├── index.css
│   ├── vite-env.d.ts
│   │
│   ├── pages/
│   │   ├── Index.tsx (Homepage)
│   │   ├── Pricing.tsx
│   │   ├── Contact.tsx
│   │   ├── About.tsx
│   │   ├── Security.tsx
│   │   ├── TrustCenter.tsx
│   │   ├── Product.tsx
│   │   ├── CaseStudies.tsx
│   │   ├── HelpCenter.tsx
│   │   ├── FAQ.tsx
│   │   ├── Blog.tsx
│   │   ├── Resources.tsx
│   │   ├── Integrations.tsx
│   │   ├── Partners.tsx
│   │   ├── NotFound.tsx
│   │   │
│   │   ├── solutions/
│   │   │   ├── KYC.tsx
│   │   │   ├── AML.tsx
│   │   │   ├── DocumentVerification.tsx
│   │   │   └── Biometric.tsx
│   │   │
│   │   ├── industries/
│   │   │   ├── Banking.tsx
│   │   │   ├── Fintech.tsx
│   │   │   ├── Crypto.tsx
│   │   │   ├── Gaming.tsx
│   │   │   ├── Healthcare.tsx
│   │   │   └── Ecommerce.tsx
│   │   │
│   │   ├── developers/
│   │   │   ├── Overview.tsx
│   │   │   ├── APIReference.tsx
│   │   │   ├── SDKs.tsx
│   │   │   ├── Examples.tsx
│   │   │   └── Sandbox.tsx
│   │   │
│   │   ├── legal/
│   │   │   ├── Privacy.tsx
│   │   │   ├── Terms.tsx
│   │   │   ├── Cookies.tsx
│   │   │   ├── DPA.tsx
│   │   │   ├── AUP.tsx
│   │   │   └── SLA.tsx
│   │   │
│   │   ├── tools/
│   │   │   ├── ROICalculator.tsx
│   │   │   ├── DocumentChecker.tsx
│   │   │   └── Demo.tsx
│   │   │
│   │   ├── blog/
│   │   │   └── [slug].tsx
│   │   │
│   │   └── case-studies/
│   │       └── [slug].tsx
│   │
│   ├── components/
│   │   ├── Navigation.tsx
│   │   ├── Footer.tsx
│   │   ├── Hero.tsx
│   │   ├── Features.tsx
│   │   ├── TrustIndicators.tsx
│   │   ├── HowItWorks.tsx
│   │   ├── CTA.tsx
│   │   ├── CustomerLogos.tsx
│   │   ├── Testimonials.tsx
│   │   ├── StatsCounter.tsx
│   │   ├── CookieConsent.tsx
│   │   ├── ChatWidget.tsx
│   │   ├── SEO.tsx
│   │   ├── ABTest.tsx
│   │   │
│   │   ├── forms/
│   │   │   ├── ContactForm.tsx
│   │   │   ├── DemoRequestForm.tsx
│   │   │   ├── NewsletterSignup.tsx
│   │   │   └── LeadCaptureModal.tsx
│   │   │
│   │   ├── blog/
│   │   │   ├── BlogCard.tsx
│   │   │   ├── BlogPost.tsx
│   │   │   ├── AuthorBio.tsx
│   │   │   └── RelatedPosts.tsx
│   │   │
│   │   ├── pricing/
│   │   │   ├── PricingCard.tsx
│   │   │   ├── ComparisonTable.tsx
│   │   │   └── FAQAccordion.tsx
│   │   │
│   │   └── ui/ (shadcn/ui components)
│   │       ├── button.tsx
│   │       ├── card.tsx
│   │       ├── input.tsx
│   │       ├── form.tsx
│   │       ├── dialog.tsx
│   │       ├── tooltip.tsx
│   │       └── ... (50+ components)
│   │
│   ├── lib/
│   │   ├── utils.ts (cn helper, etc.)
│   │   ├── analytics.ts
│   │   ├── tracking.ts
│   │   ├── email.ts
│   │   ├── crm.ts
│   │   ├── validation.ts
│   │   ├── api.ts
│   │   └── i18n.ts
│   │
│   ├── hooks/
│   │   ├── use-toast.ts
│   │   ├── use-mobile.tsx
│   │   ├── useAnalytics.ts
│   │   ├── useScrollPosition.ts
│   │   └── useIntersectionObserver.ts
│   │
│   ├── types/
│   │   ├── index.ts
│   │   ├── blog.ts
│   │   ├── case-study.ts
│   │   ├── pricing.ts
│   │   └── api.ts
│   │
│   ├── data/
│   │   ├── customers.ts
│   │   ├── testimonials.ts
│   │   ├── integrations.ts
│   │   ├── faqs.ts
│   │   ├── team.ts
│   │   └── stats.ts
│   │
│   ├── content/ (if using MDX)
│   │   ├── blog/
│   │   │   ├── post-1.mdx
│   │   │   └── post-2.mdx
│   │   └── case-studies/
│   │       ├── customer-1.mdx
│   │       └── customer-2.mdx
│   │
│   ├── locales/ (i18n)
│   │   ├── en/
│   │   │   ├── common.json
│   │   │   ├── pricing.json
│   │   │   └── navigation.json
│   │   ├── es/
│   │   └── fr/
│   │
│   └── assets/
│       ├── verigate-logo.webp
│       ├── hero-background.jpg
│       └── icons/
│
├── api/ (serverless functions or separate backend)
│   ├── contact.ts
│   ├── demo-request.ts
│   ├── newsletter.ts
│   └── download.ts
│
├── docs/
│   ├── VeriGate - Website Review.md
│   ├── ENTERPRISE_IMPLEMENTATION_ROADMAP.md
│   ├── IMPLEMENTATION_CHECKLIST.md
│   ├── PRICING_PAGE_IMPLEMENTATION.md
│   ├── PRICING_PAGE_QUICK_REFERENCE.md
│   └── TECHNICAL_ARCHITECTURE.md (this file)
│
├── .env.example
├── .env.local
├── .gitignore
├── package.json
├── tsconfig.json
├── vite.config.ts
├── tailwind.config.ts
├── postcss.config.js
├── components.json (shadcn config)
└── README.md
```

---

## Backend/API Architecture

### Serverless Functions (Recommended for Phase 1)

**Platform Options:**
1. **Vercel Functions** (if hosted on Vercel)
2. **Netlify Functions** (if hosted on Netlify)
3. **AWS Lambda** (with API Gateway)
4. **Cloudflare Workers**

**Recommended Stack:**
```
Vercel Edge Functions
├── TypeScript
├── Zod (validation)
├── API rate limiting
└── Environment variables
```

### API Endpoints Needed

```typescript
// Contact Form
POST /api/contact
{
  name: string;
  email: string;
  company?: string;
  phone?: string;
  message: string;
}

// Demo Request
POST /api/demo-request
{
  name: string;
  email: string;
  company: string;
  role: string;
  useCase: string;
  preferredDate?: string;
}

// Newsletter Signup
POST /api/newsletter
{
  email: string;
  source?: string;
}

// Resource Download
POST /api/download
{
  email: string;
  resourceId: string;
  name?: string;
  company?: string;
}

// Webhook (CRM)
POST /api/webhook/hubspot
// Handles CRM webhooks
```

### Email Service Integration

**Recommended: SendGrid**
```typescript
// lib/email.ts
import sgMail from '@sendgrid/mail';

export async function sendContactEmail(data: ContactFormData) {
  const msg = {
    to: 'sales@verigate.com',
    from: 'noreply@verigate.com',
    subject: 'New Contact Form Submission',
    templateId: 'd-xxxxx', // SendGrid template
    dynamicTemplateData: data,
  };
  
  await sgMail.send(msg);
}
```

**Alternatives:**
- AWS SES (cheaper, more setup)
- Mailgun
- Postmark
- Resend (modern, developer-friendly)

---

## Third-Party Integrations

### CRM Integration

**Option 1: HubSpot** (Recommended)
```typescript
// lib/crm.ts
import axios from 'axios';

export async function createHubSpotContact(data: LeadData) {
  const response = await axios.post(
    'https://api.hubapi.com/crm/v3/objects/contacts',
    {
      properties: {
        email: data.email,
        firstname: data.firstName,
        lastname: data.lastName,
        company: data.company,
        phone: data.phone,
        lead_source: 'Website',
      },
    },
    {
      headers: {
        Authorization: `Bearer ${process.env.HUBSPOT_API_KEY}`,
        'Content-Type': 'application/json',
      },
    }
  );
  
  return response.data;
}
```

**Option 2: Salesforce**
```typescript
// Web-to-Lead form submission
// Or Salesforce REST API
```

### Analytics Stack

**Core Analytics:**
```typescript
// lib/analytics.ts
import ReactGA from 'react-ga4';

// Initialize
ReactGA.initialize('G-XXXXXXXXXX');

// Track page views
export const trackPageView = (path: string) => {
  ReactGA.send({ hitType: 'pageview', page: path });
};

// Track events
export const trackEvent = (
  category: string,
  action: string,
  label?: string,
  value?: number
) => {
  ReactGA.event({
    category,
    action,
    label,
    value,
  });
};

// Track CTA clicks
export const trackCTAClick = (ctaName: string) => {
  trackEvent('CTA', 'Click', ctaName);
};

// Track form submissions
export const trackFormSubmission = (formName: string) => {
  trackEvent('Form', 'Submit', formName);
};
```

**Additional Tools:**
- **Hotjar/Microsoft Clarity:** Heatmaps & session recordings
- **Google Tag Manager:** Tag management
- **Mixpanel/Amplitude:** Product analytics (optional)
- **Segment:** Customer data platform (enterprise)

### Live Chat

**Recommended Options:**

**1. Tawk.to (Free)**
```html
<!-- Add to index.html -->
<script type="text/javascript">
  var Tawk_API=Tawk_API||{}, Tawk_LoadStart=new Date();
  (function(){
    var s1=document.createElement("script"),s0=document.getElementsByTagName("script")[0];
    s1.async=true;
    s1.src='https://embed.tawk.to/YOUR_PROPERTY_ID/YOUR_WIDGET_ID';
    s1.charset='UTF-8';
    s1.setAttribute('crossorigin','*');
    s0.parentNode.insertBefore(s1,s0);
  })();
</script>
```

**2. Intercom (Premium - $74/mo)**
```typescript
// components/ChatWidget.tsx
import { useEffect } from 'react';

export function IntercomChat() {
  useEffect(() => {
    (window as any).Intercom('boot', {
      app_id: process.env.VITE_INTERCOM_APP_ID,
    });
  }, []);
  
  return null;
}
```

### Status Page

**Recommended: StatusPage.io or custom**
```typescript
// lib/status.ts
export async function getSystemStatus() {
  const response = await fetch(
    'https://status.verigate.com/api/v2/status.json'
  );
  return response.json();
}
```

---

## Content Management

### Blog Content Strategy

**Option 1: MDX Files (Recommended for Phase 1)**
```typescript
// content/blog/post-1.mdx
---
title: "The Complete Guide to KYC Compliance"
author: "John Doe"
publishedAt: "2025-01-15"
category: "Compliance"
tags: ["KYC", "Compliance", "Regulations"]
excerpt: "Everything you need to know about KYC compliance in 2025"
---

# Introduction

Your content here...
```

**Loading MDX:**
```typescript
// Using next-mdx-remote or similar
import { serialize } from 'next-mdx-remote/serialize';
import { MDXRemote } from 'next-mdx-remote';

// Or with Vite: vite-plugin-mdx
```

**Option 2: Headless CMS**

**Contentful:**
```typescript
// lib/contentful.ts
import { createClient } from 'contentful';

const client = createClient({
  space: process.env.VITE_CONTENTFUL_SPACE_ID,
  accessToken: process.env.VITE_CONTENTFUL_ACCESS_TOKEN,
});

export async function getBlogPosts() {
  const entries = await client.getEntries({
    content_type: 'blogPost',
    order: '-fields.publishedAt',
  });
  
  return entries.items;
}
```

**Option 3: Strapi (Self-hosted)**
- Free and open-source
- Full control
- GraphQL or REST API

---

## SEO Implementation

### Meta Tags Component
```typescript
// components/SEO.tsx
import { Helmet } from 'react-helmet-async';

interface SEOProps {
  title: string;
  description: string;
  image?: string;
  canonical?: string;
  type?: 'website' | 'article' | 'product';
  schema?: any;
}

export function SEO({
  title,
  description,
  image = '/og-image.jpg',
  canonical,
  type = 'website',
  schema,
}: SEOProps) {
  const siteUrl = 'https://verigate.com';
  const fullTitle = `${title} | VeriGate`;
  const imageUrl = `${siteUrl}${image}`;
  
  return (
    <Helmet>
      {/* Basic Meta Tags */}
      <title>{fullTitle}</title>
      <meta name="description" content={description} />
      {canonical && <link rel="canonical" href={canonical} />}
      
      {/* Open Graph */}
      <meta property="og:type" content={type} />
      <meta property="og:title" content={fullTitle} />
      <meta property="og:description" content={description} />
      <meta property="og:image" content={imageUrl} />
      <meta property="og:url" content={canonical || siteUrl} />
      
      {/* Twitter Card */}
      <meta name="twitter:card" content="summary_large_image" />
      <meta name="twitter:title" content={fullTitle} />
      <meta name="twitter:description" content={description} />
      <meta name="twitter:image" content={imageUrl} />
      
      {/* Schema.org JSON-LD */}
      {schema && (
        <script type="application/ld+json">
          {JSON.stringify(schema)}
        </script>
      )}
    </Helmet>
  );
}
```

### Structured Data Examples
```typescript
// Organization Schema
const organizationSchema = {
  "@context": "https://schema.org",
  "@type": "Organization",
  "name": "VeriGate",
  "url": "https://verigate.com",
  "logo": "https://verigate.com/logo.png",
  "description": "Enterprise-grade identity verification",
  "sameAs": [
    "https://twitter.com/verigate",
    "https://linkedin.com/company/verigate"
  ],
  "contactPoint": {
    "@type": "ContactPoint",
    "telephone": "+1-XXX-XXX-XXXX",
    "contactType": "Sales"
  }
};

// Product Schema (for pricing page)
const productSchema = {
  "@context": "https://schema.org",
  "@type": "Product",
  "name": "VeriGate Professional Plan",
  "description": "Identity verification for growing companies",
  "offers": {
    "@type": "Offer",
    "price": "665",
    "priceCurrency": "USD",
    "priceValidUntil": "2025-12-31"
  }
};

// Article Schema (for blog)
const articleSchema = {
  "@context": "https://schema.org",
  "@type": "Article",
  "headline": "The Complete Guide to KYC Compliance",
  "author": {
    "@type": "Person",
    "name": "John Doe"
  },
  "datePublished": "2025-01-15",
  "image": "https://verigate.com/blog/kyc-guide.jpg"
};
```

---

## Performance Optimization

### Image Optimization
```typescript
// Use modern formats
import heroBackground from '@/assets/hero-background.webp';

// Lazy loading
<img loading="lazy" src={image} alt={alt} />

// Responsive images
<picture>
  <source 
    srcSet="/hero-mobile.webp" 
    media="(max-width: 768px)" 
  />
  <source 
    srcSet="/hero-tablet.webp" 
    media="(max-width: 1024px)" 
  />
  <img src="/hero-desktop.webp" alt="Hero" />
</picture>
```

### Code Splitting
```typescript
// Route-based splitting (React.lazy)
import { lazy, Suspense } from 'react';

const Pricing = lazy(() => import('./pages/Pricing'));
const About = lazy(() => import('./pages/About'));

// In routes
<Route 
  path="/pricing" 
  element={
    <Suspense fallback={<LoadingSpinner />}>
      <Pricing />
    </Suspense>
  } 
/>
```

### Vite Configuration
```typescript
// vite.config.ts
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import { visualizer } from 'rollup-plugin-visualizer';

export default defineConfig({
  plugins: [
    react(),
    visualizer(), // Bundle analysis
  ],
  build: {
    rollupOptions: {
      output: {
        manualChunks: {
          'react-vendor': ['react', 'react-dom', 'react-router-dom'],
          'ui-vendor': ['@radix-ui/react-dialog', '@radix-ui/react-dropdown-menu'],
        },
      },
    },
    chunkSizeWarningLimit: 1000,
  },
});
```

---

## Deployment Architecture

### Recommended: Vercel

**Why Vercel:**
- Zero-config deployment
- Edge functions support
- Automatic HTTPS
- Global CDN
- Preview deployments
- Analytics included

**Configuration:**
```json
// vercel.json
{
  "buildCommand": "npm run build",
  "outputDirectory": "dist",
  "framework": "vite",
  "rewrites": [
    { "source": "/api/:path*", "destination": "/api/:path*" }
  ],
  "headers": [
    {
      "source": "/(.*)",
      "headers": [
        {
          "key": "X-Content-Type-Options",
          "value": "nosniff"
        },
        {
          "key": "X-Frame-Options",
          "value": "DENY"
        },
        {
          "key": "X-XSS-Protection",
          "value": "1; mode=block"
        }
      ]
    }
  ]
}
```

### Alternative: Netlify
Similar benefits, different pricing structure

### Alternative: AWS (Advanced)
```
CloudFront (CDN)
├── S3 (Static hosting)
├── Lambda@Edge (Functions)
├── API Gateway (Backend API)
└── Route 53 (DNS)
```

---

## Security Best Practices

### Environment Variables
```bash
# .env.example
VITE_API_URL=
VITE_GOOGLE_ANALYTICS_ID=
VITE_INTERCOM_APP_ID=

# Server-side only (not exposed to client)
SENDGRID_API_KEY=
HUBSPOT_API_KEY=
DATABASE_URL=
```

### Content Security Policy
```typescript
// Add to index.html or server config
<meta 
  http-equiv="Content-Security-Policy" 
  content="
    default-src 'self';
    script-src 'self' 'unsafe-inline' https://www.googletagmanager.com;
    style-src 'self' 'unsafe-inline';
    img-src 'self' data: https:;
    font-src 'self' data:;
    connect-src 'self' https://api.verigate.com;
  "
/>
```

### Rate Limiting (API)
```typescript
// api/middleware/rateLimiter.ts
import rateLimit from 'express-rate-limit';

export const apiLimiter = rateLimit({
  windowMs: 15 * 60 * 1000, // 15 minutes
  max: 100, // limit each IP to 100 requests per windowMs
  message: 'Too many requests, please try again later',
});
```

---

## Testing Strategy

### Unit Tests
```typescript
// Using Vitest
import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import { Button } from '@/components/ui/button';

describe('Button', () => {
  it('renders correctly', () => {
    render(<Button>Click me</Button>);
    expect(screen.getByText('Click me')).toBeInTheDocument();
  });
});
```

### E2E Tests
```typescript
// Using Playwright
import { test, expect } from '@playwright/test';

test('contact form submission', async ({ page }) => {
  await page.goto('/contact');
  await page.fill('[name="email"]', 'test@example.com');
  await page.fill('[name="message"]', 'Test message');
  await page.click('button[type="submit"]');
  await expect(page.locator('.success-message')).toBeVisible();
});
```

---

## Monitoring & Error Tracking

### Sentry Integration
```typescript
// main.tsx
import * as Sentry from '@sentry/react';

Sentry.init({
  dsn: process.env.VITE_SENTRY_DSN,
  environment: process.env.NODE_ENV,
  tracesSampleRate: 1.0,
});

// Wrap app
<Sentry.ErrorBoundary fallback={<ErrorFallback />}>
  <App />
</Sentry.ErrorBoundary>
```

### Performance Monitoring
```typescript
// lib/performance.ts
export function measurePerformance() {
  if ('performance' in window) {
    const navigation = performance.getEntriesByType('navigation')[0];
    console.log('Page load time:', navigation.loadEventEnd - navigation.fetchStart);
  }
}
```

---

## CI/CD Pipeline

### GitHub Actions
```yaml
# .github/workflows/deploy.yml
name: Deploy to Production

on:
  push:
    branches: [main]

jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Setup Node.js
        uses: actions/setup-node@v3
        with:
          node-version: '18'
          
      - name: Install dependencies
        run: npm ci
        
      - name: Run tests
        run: npm test
        
      - name: Build
        run: npm run build
        
      - name: Deploy to Vercel
        uses: amondnet/vercel-action@v20
        with:
          vercel-token: ${{ secrets.VERCEL_TOKEN }}
          vercel-org-id: ${{ secrets.ORG_ID }}
          vercel-project-id: ${{ secrets.PROJECT_ID }}
```

---

## Package.json Scripts

```json
{
  "scripts": {
    "dev": "vite",
    "build": "tsc && vite build",
    "preview": "vite preview",
    "lint": "eslint src --ext ts,tsx",
    "lint:fix": "eslint src --ext ts,tsx --fix",
    "format": "prettier --write \"src/**/*.{ts,tsx}\"",
    "type-check": "tsc --noEmit",
    "test": "vitest",
    "test:ui": "vitest --ui",
    "test:e2e": "playwright test",
    "analyze": "vite-bundle-visualizer"
  }
}
```

---

## Dependencies Summary

### Production Dependencies
```json
{
  "dependencies": {
    "react": "^18.2.0",
    "react-dom": "^18.2.0",
    "react-router-dom": "^6.20.0",
    "@tanstack/react-query": "^5.17.0",
    "react-hook-form": "^7.49.0",
    "@hookform/resolvers": "^3.3.0",
    "zod": "^3.22.0",
    "axios": "^1.6.0",
    "framer-motion": "^11.0.0",
    "lucide-react": "^0.300.0",
    "date-fns": "^3.0.0",
    "react-i18next": "^13.5.0",
    "react-markdown": "^9.0.0",
    "recharts": "^2.10.0",
    "react-ga4": "^2.1.0"
  }
}
```

### Development Dependencies
```json
{
  "devDependencies": {
    "@types/react": "^18.2.0",
    "@types/react-dom": "^18.2.0",
    "@typescript-eslint/eslint-plugin": "^6.0.0",
    "@typescript-eslint/parser": "^6.0.0",
    "@vitejs/plugin-react": "^4.2.0",
    "autoprefixer": "^10.4.0",
    "eslint": "^8.0.0",
    "postcss": "^8.4.0",
    "prettier": "^3.1.0",
    "tailwindcss": "^3.4.0",
    "typescript": "^5.3.0",
    "vite": "^5.0.0",
    "vitest": "^1.1.0",
    "@playwright/test": "^1.40.0"
  }
}
```

---

**Document Version:** 1.0
**Last Updated:** January 2025
**Owner:** Technical Lead
