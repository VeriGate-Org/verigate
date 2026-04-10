import LegalPageTemplate from "@/components/templates/LegalPageTemplate";

const cookieSections = [
  {
    id: "what-are-cookies",
    title: "What Are Cookies",
    content: `
      <p>Cookies are small text files that are stored on your device (computer, tablet, or mobile phone) when you visit a website. They are widely used to make websites work more efficiently, provide a better user experience, and give website owners information about how their site is being used.</p>
      <p>Cookies can be "persistent" (they remain on your device until they expire or are deleted) or "session" cookies (they are deleted when you close your browser). Cookies can also be "first-party" (set by the website you are visiting) or "third-party" (set by a service used by the website, such as an analytics provider).</p>
      <p>VeriGate (Pty) Ltd ("VeriGate", "we", "us", or "our") uses cookies and similar tracking technologies on our website and platform. This Cookie Policy explains what cookies we use, why we use them, and how you can manage your preferences.</p>
    `,
  },
  {
    id: "how-we-use-cookies",
    title: "How We Use Cookies",
    content: `
      <p>We use the following categories of cookies on our website:</p>
      <p><strong>Necessary Cookies:</strong> These cookies are essential for the website to function properly. They enable core functionality such as security, session management, and accessibility. You cannot disable these cookies as the website would not function correctly without them.</p>
      <p><strong>Analytics Cookies:</strong> These cookies help us understand how visitors interact with our website by collecting and reporting information anonymously. We use this data to improve our website's structure, content, and user experience.</p>
      <p><strong>Functional Cookies:</strong> These cookies enable enhanced functionality and personalisation, such as remembering your preferences, language settings, and form inputs. If you disable these cookies, some features may not work as intended.</p>
      <p><strong>Marketing Cookies:</strong> These cookies are used to track visitors across websites and display relevant advertisements. They may be set by our advertising partners to build a profile of your interests and show you relevant content on other sites.</p>
    `,
  },
  {
    id: "cookie-types",
    title: "Cookie Types We Use",
    content: `
      <p>The following table details the specific cookies used on our website:</p>
      <table style="width: 100%; border-collapse: collapse; margin: 1rem 0;">
        <thead>
          <tr style="border-bottom: 2px solid hsl(var(--border));">
            <th style="text-align: left; padding: 0.75rem; font-weight: 600;">Cookie Name</th>
            <th style="text-align: left; padding: 0.75rem; font-weight: 600;">Purpose</th>
            <th style="text-align: left; padding: 0.75rem; font-weight: 600;">Duration</th>
            <th style="text-align: left; padding: 0.75rem; font-weight: 600;">Type</th>
          </tr>
        </thead>
        <tbody>
          <tr style="border-bottom: 1px solid hsl(var(--border));">
            <td style="padding: 0.75rem;"><code>cookieConsent</code></td>
            <td style="padding: 0.75rem;">Stores your cookie consent preferences</td>
            <td style="padding: 0.75rem;">1 year</td>
            <td style="padding: 0.75rem;">Necessary</td>
          </tr>
          <tr style="border-bottom: 1px solid hsl(var(--border));">
            <td style="padding: 0.75rem;"><code>cookieConsentDate</code></td>
            <td style="padding: 0.75rem;">Records when you gave cookie consent</td>
            <td style="padding: 0.75rem;">1 year</td>
            <td style="padding: 0.75rem;">Necessary</td>
          </tr>
          <tr style="border-bottom: 1px solid hsl(var(--border));">
            <td style="padding: 0.75rem;"><code>_session</code></td>
            <td style="padding: 0.75rem;">Maintains your authenticated session</td>
            <td style="padding: 0.75rem;">Session</td>
            <td style="padding: 0.75rem;">Necessary</td>
          </tr>
          <tr style="border-bottom: 1px solid hsl(var(--border));">
            <td style="padding: 0.75rem;"><code>_csrf</code></td>
            <td style="padding: 0.75rem;">Protects against cross-site request forgery attacks</td>
            <td style="padding: 0.75rem;">Session</td>
            <td style="padding: 0.75rem;">Necessary</td>
          </tr>
          <tr style="border-bottom: 1px solid hsl(var(--border));">
            <td style="padding: 0.75rem;"><code>_ga</code></td>
            <td style="padding: 0.75rem;">Google Analytics - Distinguishes unique users</td>
            <td style="padding: 0.75rem;">2 years</td>
            <td style="padding: 0.75rem;">Analytics</td>
          </tr>
          <tr style="border-bottom: 1px solid hsl(var(--border));">
            <td style="padding: 0.75rem;"><code>_ga_*</code></td>
            <td style="padding: 0.75rem;">Google Analytics 4 - Persists session state</td>
            <td style="padding: 0.75rem;">2 years</td>
            <td style="padding: 0.75rem;">Analytics</td>
          </tr>
          <tr style="border-bottom: 1px solid hsl(var(--border));">
            <td style="padding: 0.75rem;"><code>_gid</code></td>
            <td style="padding: 0.75rem;">Google Analytics - Distinguishes users for 24 hours</td>
            <td style="padding: 0.75rem;">24 hours</td>
            <td style="padding: 0.75rem;">Analytics</td>
          </tr>
          <tr style="border-bottom: 1px solid hsl(var(--border));">
            <td style="padding: 0.75rem;"><code>_hjSessionUser</code></td>
            <td style="padding: 0.75rem;">Hotjar - Persists user session across page views</td>
            <td style="padding: 0.75rem;">1 year</td>
            <td style="padding: 0.75rem;">Analytics</td>
          </tr>
          <tr style="border-bottom: 1px solid hsl(var(--border));">
            <td style="padding: 0.75rem;"><code>theme</code></td>
            <td style="padding: 0.75rem;">Remembers your preferred colour theme (light/dark)</td>
            <td style="padding: 0.75rem;">1 year</td>
            <td style="padding: 0.75rem;">Functional</td>
          </tr>
          <tr style="border-bottom: 1px solid hsl(var(--border));">
            <td style="padding: 0.75rem;"><code>locale</code></td>
            <td style="padding: 0.75rem;">Stores your preferred language setting</td>
            <td style="padding: 0.75rem;">1 year</td>
            <td style="padding: 0.75rem;">Functional</td>
          </tr>
          <tr style="border-bottom: 1px solid hsl(var(--border));">
            <td style="padding: 0.75rem;"><code>_fbp</code></td>
            <td style="padding: 0.75rem;">Facebook Pixel - Tracks visits across websites for ad targeting</td>
            <td style="padding: 0.75rem;">3 months</td>
            <td style="padding: 0.75rem;">Marketing</td>
          </tr>
          <tr style="border-bottom: 1px solid hsl(var(--border));">
            <td style="padding: 0.75rem;"><code>_gcl_au</code></td>
            <td style="padding: 0.75rem;">Google Ads - Stores conversion information</td>
            <td style="padding: 0.75rem;">3 months</td>
            <td style="padding: 0.75rem;">Marketing</td>
          </tr>
          <tr>
            <td style="padding: 0.75rem;"><code>li_sugr</code></td>
            <td style="padding: 0.75rem;">LinkedIn Insight Tag - Tracks conversions and retargeting</td>
            <td style="padding: 0.75rem;">3 months</td>
            <td style="padding: 0.75rem;">Marketing</td>
          </tr>
        </tbody>
      </table>
    `,
  },
  {
    id: "managing-cookies",
    title: "Managing Cookies",
    content: `
      <p>You have several options for managing cookies:</p>
      <p><strong>Our Cookie Preferences:</strong> When you first visit our website, a cookie consent banner is displayed allowing you to accept all cookies, accept only necessary cookies, or customise your preferences. You can change your cookie preferences at any time by clicking the cookie settings link in the footer of our website.</p>
      <p><strong>Browser Settings:</strong> Most web browsers allow you to control cookies through their settings. You can typically find cookie controls in the "Settings", "Preferences", or "Privacy" section of your browser. Common options include:</p>
      <ul>
        <li><strong>Block all cookies:</strong> Prevent all cookies from being set. Note that this will likely break website functionality.</li>
        <li><strong>Block third-party cookies:</strong> Allow first-party cookies but block cookies set by third-party services.</li>
        <li><strong>Delete cookies on close:</strong> Automatically delete all cookies when you close your browser.</li>
        <li><strong>Manage individual cookies:</strong> View and delete specific cookies stored on your device.</li>
      </ul>
      <p>Please note that disabling cookies may affect the functionality of our website and you may not be able to access certain features.</p>
      <p>For instructions specific to your browser, please visit: <a href="https://www.allaboutcookies.org/manage-cookies/" class="text-primary hover:underline" target="_blank" rel="noopener noreferrer">www.allaboutcookies.org/manage-cookies</a></p>
    `,
  },
  {
    id: "third-party-cookies",
    title: "Third-Party Cookies",
    content: `
      <p>We use cookies from the following third-party analytics and advertising providers:</p>
      <p><strong>Google Analytics:</strong> We use Google Analytics to understand how visitors use our website. Google Analytics collects information such as pages visited, time on site, and traffic sources. This data is aggregated and anonymised. Google's privacy policy is available at <a href="https://policies.google.com/privacy" class="text-primary hover:underline" target="_blank" rel="noopener noreferrer">policies.google.com/privacy</a>. You can opt out of Google Analytics by installing the <a href="https://tools.google.com/dlpage/gaoptout" class="text-primary hover:underline" target="_blank" rel="noopener noreferrer">Google Analytics Opt-out Browser Add-on</a>.</p>
      <p><strong>Hotjar:</strong> We use Hotjar to understand user behaviour through heatmaps, session recordings, and feedback tools. Hotjar anonymises user data and does not collect personally identifiable information. Hotjar's privacy policy is available at <a href="https://www.hotjar.com/legal/policies/privacy/" class="text-primary hover:underline" target="_blank" rel="noopener noreferrer">hotjar.com/legal/policies/privacy</a>.</p>
      <p><strong>Facebook Pixel:</strong> We use the Facebook Pixel to measure the effectiveness of our advertising campaigns and to display relevant ads to people who have visited our website. You can control Facebook ad preferences at <a href="https://www.facebook.com/ads/preferences" class="text-primary hover:underline" target="_blank" rel="noopener noreferrer">facebook.com/ads/preferences</a>.</p>
      <p><strong>LinkedIn Insight Tag:</strong> We use the LinkedIn Insight Tag for campaign reporting and audience insights. You can manage your LinkedIn advertising preferences at <a href="https://www.linkedin.com/psettings/advertising" class="text-primary hover:underline" target="_blank" rel="noopener noreferrer">linkedin.com/psettings/advertising</a>.</p>
      <p>These third-party services have their own privacy policies governing their use of cookies and data collection. We encourage you to review their policies.</p>
    `,
  },
  {
    id: "changes-to-policy",
    title: "Changes to This Policy",
    content: `
      <p>We may update this Cookie Policy from time to time to reflect changes in our practices, technology, legal requirements, or for other operational reasons.</p>
      <p>When we make material changes to this Cookie Policy, we will:</p>
      <ul>
        <li>Update the "Last updated" date at the top of this page.</li>
        <li>Display a prominent notice on our website informing you of the change.</li>
        <li>Where required, re-present the cookie consent banner so you can review and update your preferences.</li>
      </ul>
      <p>We encourage you to review this Cookie Policy periodically to stay informed about how we use cookies.</p>
    `,
  },
  {
    id: "contact-us",
    title: "Contact Us",
    content: `
      <p>If you have any questions about this Cookie Policy or our use of cookies, please contact us:</p>
      <p><strong>VeriGate (Pty) Ltd</strong><br />
      Email: <a href="mailto:info@verigate.co.za" class="text-primary hover:underline">info@verigate.co.za</a><br />
      Phone: +27 (0)21 555 0100<br />
      Address: 4th Floor, The Foundry, 68 Sir Lowry Road, Woodstock, Cape Town, 7925, South Africa</p>
      <p>For information about how we process your personal information more broadly, please refer to our <a href="/privacy" class="text-primary hover:underline">Privacy Policy</a>.</p>
    `,
  },
];

const CookiePolicy = () => {
  return (
    <LegalPageTemplate
      title="Cookie Policy"
      lastUpdated="1 April 2026"
      sections={cookieSections}
    />
  );
};

export default CookiePolicy;
