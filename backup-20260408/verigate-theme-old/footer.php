  <footer class="footer">
    <div class="container">
      <div class="footer__grid--expanded">
        <div class="footer__brand">
          <div style="height:32px"><svg viewBox="0 0 360 70" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M8 8 C8 4, 12 0, 16 0 L44 0 C48 0, 52 4, 52 8 L52 36 C52 48, 30 60, 30 60 C30 60, 8 48, 8 36Z" fill="#DC2626"/><polyline points="18,30 26,40 42,18" fill="none" stroke="white" stroke-width="5" stroke-linecap="round" stroke-linejoin="round"/><text x="64" y="42" font-family="'Manrope', sans-serif" font-weight="800" font-size="38" fill="white" letter-spacing="-0.5">VeriGate</text></svg></div>
          <p>Enterprise-grade verification platform trusted by South Africa's leading organisations for background screening and compliance.</p>
        </div>
        <div>
          <h4 class="footer__heading">Products</h4>
          <ul class="footer__links">
            <li><a href="<?php echo esc_url(home_url('/verification-types/')); ?>">Verification Types</a></li>
            <li><a href="<?php echo esc_url(home_url('/compliance/')); ?>">Compliance</a></li>
            <li><a href="<?php echo esc_url(home_url('/fraud-prevention/')); ?>">Fraud Prevention</a></li>
            <li><a href="<?php echo esc_url(home_url('/platform/')); ?>">Platform</a></li>
            <li><a href="<?php echo esc_url(home_url('/integrations/')); ?>">Integrations</a></li>
          </ul>
        </div>
        <div>
          <h4 class="footer__heading">Solutions</h4>
          <ul class="footer__links">
            <li><a href="<?php echo esc_url(home_url('/solutions/banking/')); ?>">Banking</a></li>
            <li><a href="<?php echo esc_url(home_url('/solutions/fintech/')); ?>">Fintech</a></li>
            <li><a href="<?php echo esc_url(home_url('/solutions/cryptocurrency/')); ?>">Cryptocurrency</a></li>
            <li><a href="<?php echo esc_url(home_url('/solutions/gaming/')); ?>">Gaming</a></li>
            <li><a href="<?php echo esc_url(home_url('/solutions/gig-economy/')); ?>">Gig Economy</a></li>
          </ul>
        </div>
        <div>
          <h4 class="footer__heading">Company</h4>
          <ul class="footer__links">
            <li><a href="<?php echo esc_url(home_url('/about/')); ?>">About Us</a></li>
            <li><a href="<?php echo esc_url(home_url('/careers/')); ?>">Careers</a></li>
            <li><a href="<?php echo esc_url(home_url('/partner-program/')); ?>">Partners</a></li>
            <li><a href="<?php echo esc_url(home_url('/events/')); ?>">Events</a></li>
            <li><a href="<?php echo esc_url(home_url('/contact/')); ?>">Contact</a></li>
          </ul>
        </div>
        <div>
          <h4 class="footer__heading">Resources</h4>
          <ul class="footer__links">
            <li><a href="<?php echo esc_url(home_url('/blog/')); ?>">Blog</a></li>
            <li><a href="<?php echo esc_url(home_url('/resources/')); ?>">Document Library</a></li>
            <li><a href="<?php echo esc_url(home_url('/faqs/')); ?>">FAQs</a></li>
            <li><a href="<?php echo esc_url(home_url('/technical-support/')); ?>">Technical Support</a></li>
            <li><a href="<?php echo esc_url(home_url('/analytics/')); ?>">Analytics</a></li>
          </ul>
        </div>
        <div>
          <h4 class="footer__heading">Legal</h4>
          <ul class="footer__links">
            <li><a href="<?php echo esc_url(home_url('/privacy/')); ?>">Privacy Policy</a></li>
            <li><a href="<?php echo esc_url(home_url('/terms/')); ?>">Terms of Service</a></li>
            <li><a href="<?php echo esc_url(home_url('/cookie-policy/')); ?>">Cookie Policy</a></li>
            <li><a href="<?php echo esc_url(home_url('/south-africa/')); ?>">South Africa</a></li>
            <li><a href="<?php echo esc_url(home_url('/supported-documents/')); ?>">Supported Documents</a></li>
          </ul>
        </div>
      </div>
      <div class="footer__bottom">
        <span>&copy; <?php echo date('Y'); ?> VeriGate (Pty) Ltd. All rights reserved.</span>
        <span>Built by <a href="https://arthmatic.co.za" style="color:rgba(255,255,255,0.5)">Arthmatic</a></span>
      </div>
    </div>
  </footer>

  <!-- Cookie Consent Banner (POPIA) -->
  <div class="cookie-banner" role="complementary" aria-label="Cookie consent">
    <p class="cookie-banner__text">We use cookies to improve your experience and analyse site traffic. By clicking "Accept", you consent to our use of cookies in accordance with our <a href="<?php echo esc_url(home_url('/cookie-policy/')); ?>">Cookie Policy</a> and the Protection of Personal Information Act (POPIA).</p>
    <div class="cookie-banner__actions">
      <button class="cookie-banner__btn cookie-banner__btn--accept">Accept</button>
      <button class="cookie-banner__btn cookie-banner__btn--decline">Decline</button>
    </div>
  </div>

  <?php wp_footer(); ?>
</body>
</html>
