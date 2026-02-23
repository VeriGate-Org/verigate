# Quick Start Deployment Guide

## 🚀 From Zero to Production in 30 Minutes

This guide will get your VeriGate website live with full functionality using the FREE tier.

---

## Step 1: Environment Setup (5 minutes)

### 1.1 Copy Environment Template
```bash
cp .env.example .env.local
```

### 1.2 Configure Free Tier Services

Edit `.env.local`:

```bash
# Email: Use SendGrid Free (100 emails/day)
VITE_EMAIL_API_KEY=your_sendgrid_api_key  # Get from sendgrid.com
VITE_FROM_EMAIL=noreply@yourdomain.com
VITE_SALES_EMAIL=sales@yourdomain.com

# CRM: Use custom (logs to console for now)
VITE_CRM_PLATFORM=custom

# Analytics: Use Google Analytics 4 (free)
VITE_GA_MEASUREMENT_ID=G-XXXXXXXXXX  # Get from analytics.google.com

# Heatmaps: Use Microsoft Clarity (free)
VITE_CLARITY_PROJECT_ID=your_project_id  # Get from clarity.microsoft.com

# Chat: Use Tawk.to (free)
VITE_CHAT_PROVIDER=tawk
VITE_TAWK_PROPERTY_ID=your_property_id  # Get from tawk.to
VITE_TAWK_WIDGET_ID=your_widget_id

# Optional: Leave others blank for now
VITE_FEATURE_CHAT=true
VITE_FEATURE_ANALYTICS=true
```

---

## Step 2: Get Free API Keys (10 minutes)

### 2.1 SendGrid (Email) - FREE
1. Go to https://sendgrid.com/
2. Click "Start for Free"
3. Verify email
4. Go to Settings → API Keys
5. Create API Key → Full Access
6. Copy key to `VITE_EMAIL_API_KEY`
7. Add sender email in Settings → Sender Authentication

**Free Tier:** 100 emails/day (3,000/month)

### 2.2 Google Analytics 4 - FREE
1. Go to https://analytics.google.com/
2. Create Account
3. Create Property (choose "Web")
4. Get Measurement ID (format: G-XXXXXXXXXX)
5. Copy to `VITE_GA_MEASUREMENT_ID`

**Free Tier:** Unlimited

### 2.3 Microsoft Clarity - FREE
1. Go to https://clarity.microsoft.com/
2. Sign up (free, no credit card)
3. Create new project
4. Get Project ID
5. Copy to `VITE_CLARITY_PROJECT_ID`

**Free Tier:** Unlimited (heatmaps & session recordings)

### 2.4 Tawk.to (Chat) - FREE
1. Go to https://tawk.to/
2. Sign up free
3. Add website
4. Go to Administration → Channels → Chat Widget
5. Copy Property ID and Widget ID
6. Paste into .env.local

**Free Tier:** Unlimited chats, unlimited agents

---

## Step 3: Build & Deploy (5 minutes)

### 3.1 Install Dependencies
```bash
npm install
```

### 3.2 Test Locally
```bash
npm run dev
```

Visit http://localhost:8080 and test:
- ✅ Forms load
- ✅ Cookie consent appears
- ✅ Chat widget shows
- ✅ All pages load

### 3.3 Build for Production
```bash
npm run build
```

Should output: `✓ built in 1.55s`

---

## Step 4: Deploy (10 minutes)

### Option A: Vercel (Recommended - Easiest)

1. Install Vercel CLI:
```bash
npm install -g vercel
```

2. Deploy:
```bash
vercel
```

3. Follow prompts:
   - Link to existing project? No
   - Project name? verigate-website
   - Framework? Vite
   - Build command? `npm run build`
   - Output directory? `dist`

4. Add environment variables in Vercel dashboard:
   - Project Settings → Environment Variables
   - Add all `VITE_*` variables from .env.local

5. Redeploy:
```bash
vercel --prod
```

**Done!** Your site is live at `https://verigate-website.vercel.app`

### Option B: Netlify

1. Install Netlify CLI:
```bash
npm install -g netlify-cli
```

2. Build:
```bash
npm run build
```

3. Deploy:
```bash
netlify deploy --prod --dir=dist
```

4. Add environment variables:
   - Site Settings → Environment Variables
   - Add all `VITE_*` variables

### Option C: Traditional Hosting (cPanel, etc.)

1. Build:
```bash
npm run build
```

2. Upload `dist/` folder contents to your hosting

3. Configure `.htaccess` for SPA routing:
```apache
<IfModule mod_rewrite.c>
  RewriteEngine On
  RewriteBase /
  RewriteRule ^index\.html$ - [L]
  RewriteCond %{REQUEST_FILENAME} !-f
  RewriteCond %{REQUEST_FILENAME} !-d
  RewriteRule . /index.html [L]
</IfModule>
```

---

## Step 5: DNS & Email Setup (Optional, 15 minutes)

### 5.1 Custom Domain
If using custom domain (e.g., verigate.com):

**Vercel:**
- Project Settings → Domains → Add Domain
- Follow DNS instructions

**Netlify:**
- Domain Settings → Add Domain
- Follow DNS instructions

### 5.2 Email Authentication (SendGrid)

Add these DNS records to prevent emails going to spam:

**SPF Record:**
```
Type: TXT
Host: @
Value: v=spf1 include:sendgrid.net ~all
```

**DKIM Records:**
Get from SendGrid → Settings → Sender Authentication

**DMARC Record:**
```
Type: TXT
Host: _dmarc
Value: v=DMARC1; p=none; rua=mailto:dmarc@yourdomain.com
```

---

## Step 6: Verify Everything Works (5 minutes)

### 6.1 Test Forms
Visit your deployed site:

1. **Contact Form:**
   - Fill out and submit
   - Check email for auto-responder
   - Check sales email for notification

2. **Demo Request:**
   - Fill out and submit
   - Should receive email with next steps
   - Check console for CRM log (if using custom)

3. **Newsletter:**
   - Enter email and submit
   - Should receive welcome email

### 6.2 Test Analytics

1. Visit Google Analytics Real-Time report
2. Should see yourself as active user
3. Click around site
4. Should see events firing

### 6.3 Test Chat

1. Click chat button on site
2. Send test message
3. Check Tawk.to dashboard for message

### 6.4 Test Cookie Consent

1. Refresh page
2. Cookie banner should appear
3. Click "Customize"
4. Toggle analytics off
5. Accept
6. Refresh again
7. No analytics should fire

---

## Troubleshooting

### Emails Not Sending

**Check:**
- ✅ SendGrid API key is correct
- ✅ Sender email is verified in SendGrid
- ✅ Check SendGrid Activity Feed for errors
- ✅ Check spam folder

**Fix:**
```bash
# Test in browser console:
import { emailService } from './lib/email';
await emailService.sendEmail({
  to: 'your@email.com',
  from: 'noreply@yourdomain.com',
  subject: 'Test',
  text: 'Test email'
});
```

### Analytics Not Tracking

**Check:**
- ✅ Cookie consent accepted
- ✅ GA Measurement ID is correct
- ✅ Browser ad-blocker disabled
- ✅ Check browser console for errors

**Fix:**
```bash
# In browser console:
window.gtag('event', 'test_event', { test: 'value' });
```

Check GA4 Real-Time → Events

### Chat Not Showing

**Check:**
- ✅ Tawk.to Property ID correct
- ✅ Widget ID correct
- ✅ VITE_CHAT_PROVIDER=tawk
- ✅ No JavaScript errors in console

**Fix:**
Use custom chat widget:
```bash
# In .env.local:
VITE_CHAT_PROVIDER=custom
```

### Build Fails

**Check:**
- ✅ Node version (use v18+)
- ✅ Clean install: `rm -rf node_modules && npm install`
- ✅ Clear cache: `npm run build -- --force`

---

## Performance Optimization (Post-Launch)

### 1. Enable Caching
Add to `vite.config.ts`:
```typescript
build: {
  rollupOptions: {
    output: {
      manualChunks: {
        vendor: ['react', 'react-dom'],
        ui: ['@radix-ui/react-*'],
      }
    }
  }
}
```

### 2. Optimize Images
```bash
# Install image optimizer
npm install -D vite-plugin-imagemin

# Add to vite.config.ts
import viteImagemin from 'vite-plugin-imagemin';

plugins: [
  viteImagemin({
    gifsicle: { optimizationLevel: 7 },
    optipng: { optimizationLevel: 7 },
    mozjpeg: { quality: 80 },
    pngquant: { quality: [0.8, 0.9] },
    svgo: { plugins: [{ removeViewBox: false }] },
  })
]
```

### 3. Add CDN
Use Cloudflare (free tier):
1. Sign up at cloudflare.com
2. Add your domain
3. Update nameservers
4. Enable Auto Minify (JS, CSS, HTML)
5. Enable Brotli compression

---

## Monitoring & Maintenance

### Daily (First Week)
- Check email delivery in SendGrid
- Monitor form submissions
- Review analytics for errors
- Respond to chat messages

### Weekly
- Review GA4 reports
- Check Clarity heatmaps
- Analyze user behavior
- Optimize based on data

### Monthly
- Review lead quality
- Update email templates
- Optimize conversion funnels
- Plan content updates

---

## Upgrade Path (When Ready)

### When You Get 100+ Leads/Month:
**Upgrade to:**
- HubSpot Starter ($45/mo) - Better CRM
- SendGrid Essentials ($15/mo) - More emails
- Total: $60/mo

### When You Get 500+ Leads/Month:
**Upgrade to:**
- HubSpot Professional ($800/mo) - Marketing automation
- Intercom ($74/mo) - Better chat
- SendGrid Pro ($90/mo) - Advanced features
- Total: $964/mo

---

## Success Checklist

- [ ] All environment variables configured
- [ ] SendGrid API key working
- [ ] Google Analytics tracking
- [ ] Microsoft Clarity recording
- [ ] Tawk.to chat showing
- [ ] Contact form sends emails
- [ ] Demo form sends emails
- [ ] Newsletter signup works
- [ ] Cookie consent appears
- [ ] Mobile responsive
- [ ] All pages load
- [ ] No console errors
- [ ] DNS configured
- [ ] Email authentication (SPF/DKIM)
- [ ] Custom domain (if applicable)
- [ ] SSL certificate active (https)

---

## Next Steps

Once everything is working:

1. **Phase 2:** Add more pages (product features, case studies, help center)
2. **Content:** Start blog, create whitepapers
3. **SEO:** Optimize for target keywords
4. **Marketing:** Set up email campaigns
5. **Scale:** Upgrade services as needed

---

## Support Resources

### Free Tier Services:
- SendGrid Docs: https://docs.sendgrid.com/
- Google Analytics: https://support.google.com/analytics/
- Microsoft Clarity: https://learn.microsoft.com/clarity/
- Tawk.to Support: https://help.tawk.to/

### Deployment Platforms:
- Vercel Docs: https://vercel.com/docs
- Netlify Docs: https://docs.netlify.com/

### Framework Docs:
- Vite: https://vitejs.dev/
- React: https://react.dev/
- Tailwind: https://tailwindcss.com/

---

## Emergency Contacts

If something breaks in production:

1. **Check Status Pages:**
   - SendGrid: https://status.sendgrid.com/
   - Vercel: https://www.vercel-status.com/
   - Google: https://www.google.com/appsstatus/

2. **Rollback (Vercel):**
```bash
vercel rollback
```

3. **Disable Feature:**
Edit `.env.local`:
```bash
VITE_FEATURE_CHAT=false  # Disable chat
VITE_FEATURE_ANALYTICS=false  # Disable analytics
```

Then redeploy.

---

**Total Setup Time:** 30-45 minutes  
**Monthly Cost:** $0 (Free tier)  
**Maintenance:** <1 hour/week

🚀 **You're ready to launch!**
