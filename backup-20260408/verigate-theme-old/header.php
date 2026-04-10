<!DOCTYPE html>
<html <?php language_attributes(); ?>>
<head>
  <meta charset="<?php bloginfo('charset'); ?>">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <?php wp_head(); ?>
</head>
<body <?php body_class(); ?>>
<?php wp_body_open(); ?>

  <nav class="navbar">
    <div class="navbar__inner">
      <a href="<?php echo esc_url(home_url('/')); ?>" class="navbar__logo"><svg viewBox="0 0 360 70" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M8 8 C8 4, 12 0, 16 0 L44 0 C48 0, 52 4, 52 8 L52 36 C52 48, 30 60, 30 60 C30 60, 8 48, 8 36Z" fill="#DC2626"/><polyline points="18,30 26,40 42,18" fill="none" stroke="white" stroke-width="5" stroke-linecap="round" stroke-linejoin="round"/><text x="64" y="42" font-family="'Manrope', sans-serif" font-weight="800" font-size="38" fill="white" letter-spacing="-0.5">VeriGate</text></svg></a>
      <div class="navbar__links">
        <div style="position:relative">
          <button class="navbar__dropdown-trigger">Products <svg class="navbar__dropdown-arrow" width="10" height="10" viewBox="0 0 10 10" fill="none"><path d="M2 4l3 3 3-3" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg></button>
          <div class="navbar__dropdown-panel navbar__dropdown-panel--wide">
            <div class="navbar__dropdown-column">
              <div class="navbar__dropdown-heading">Verification Types</div>
              <a href="<?php echo esc_url(home_url('/verification-types/criminal-record-checks/')); ?>" class="navbar__dropdown-link">Criminal Checks</a>
              <a href="<?php echo esc_url(home_url('/verification-types/identity-verification/')); ?>" class="navbar__dropdown-link">Identity Verification</a>
              <a href="<?php echo esc_url(home_url('/verification-types/qualification-checks/')); ?>" class="navbar__dropdown-link">Qualification Checks</a>
              <a href="<?php echo esc_url(home_url('/verification-types/employment-history/')); ?>" class="navbar__dropdown-link">Employment History</a>
              <a href="<?php echo esc_url(home_url('/verification-types/credit-screening/')); ?>" class="navbar__dropdown-link">Credit Screening</a>
              <a href="<?php echo esc_url(home_url('/verification-types/face-verification/')); ?>" class="navbar__dropdown-link">Face Verification</a>
              <a href="<?php echo esc_url(home_url('/verification-types/document-verification/')); ?>" class="navbar__dropdown-link">Document Verification</a>
              <a href="<?php echo esc_url(home_url('/verification-types/business-verification/')); ?>" class="navbar__dropdown-link">Business Verification</a>
              <a href="<?php echo esc_url(home_url('/verification-types/address-verification/')); ?>" class="navbar__dropdown-link">Address Verification</a>
              <a href="<?php echo esc_url(home_url('/verification-types/eidv/')); ?>" class="navbar__dropdown-link">eIDV</a>
              <a href="<?php echo esc_url(home_url('/verification-types/')); ?>" class="navbar__dropdown-link" style="color:var(--color-primary);font-weight:700">View All &rarr;</a>
            </div>
            <div class="navbar__dropdown-column">
              <div class="navbar__dropdown-heading">Compliance</div>
              <a href="<?php echo esc_url(home_url('/compliance/kyc/')); ?>" class="navbar__dropdown-link">KYC</a>
              <a href="<?php echo esc_url(home_url('/compliance/kyb/')); ?>" class="navbar__dropdown-link">KYB</a>
              <a href="<?php echo esc_url(home_url('/compliance/kyi/')); ?>" class="navbar__dropdown-link">KYI</a>
              <a href="<?php echo esc_url(home_url('/compliance/aml-screening/')); ?>" class="navbar__dropdown-link">AML Screening</a>
              <a href="<?php echo esc_url(home_url('/compliance/popia-compliance/')); ?>" class="navbar__dropdown-link">POPIA Compliance</a>
              <a href="<?php echo esc_url(home_url('/compliance/')); ?>" class="navbar__dropdown-link" style="color:var(--color-primary);font-weight:700">View All &rarr;</a>
            </div>
            <div class="navbar__dropdown-column">
              <div class="navbar__dropdown-heading">Fraud Prevention</div>
              <a href="<?php echo esc_url(home_url('/fraud-prevention/identity-fraud/')); ?>" class="navbar__dropdown-link">Identity Fraud</a>
              <a href="<?php echo esc_url(home_url('/fraud-prevention/document-fraud/')); ?>" class="navbar__dropdown-link">Document Fraud</a>
              <a href="<?php echo esc_url(home_url('/fraud-prevention/deepfake-detection/')); ?>" class="navbar__dropdown-link">Deepfake Detection</a>
              <a href="<?php echo esc_url(home_url('/fraud-prevention/synthetic-identity/')); ?>" class="navbar__dropdown-link">Synthetic Identity</a>
              <a href="<?php echo esc_url(home_url('/fraud-prevention/device-intelligence/')); ?>" class="navbar__dropdown-link">Device Intelligence</a>
              <a href="<?php echo esc_url(home_url('/fraud-prevention/')); ?>" class="navbar__dropdown-link" style="color:var(--color-primary);font-weight:700">View All &rarr;</a>
            </div>
          </div>
        </div>
        <div style="position:relative">
          <button class="navbar__dropdown-trigger">Solutions <svg class="navbar__dropdown-arrow" width="10" height="10" viewBox="0 0 10 10" fill="none"><path d="M2 4l3 3 3-3" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg></button>
          <div class="navbar__dropdown-panel navbar__dropdown-panel--narrow">
            <div class="navbar__dropdown-column">
              <div class="navbar__dropdown-heading">By Industry</div>
              <a href="<?php echo esc_url(home_url('/solutions/banking/')); ?>" class="navbar__dropdown-link">Banking</a>
              <a href="<?php echo esc_url(home_url('/solutions/fintech/')); ?>" class="navbar__dropdown-link">Fintech</a>
              <a href="<?php echo esc_url(home_url('/solutions/cryptocurrency/')); ?>" class="navbar__dropdown-link">Cryptocurrency</a>
              <a href="<?php echo esc_url(home_url('/solutions/forex-trading/')); ?>" class="navbar__dropdown-link">Forex &amp; Trading</a>
              <a href="<?php echo esc_url(home_url('/solutions/gaming/')); ?>" class="navbar__dropdown-link">Gaming &amp; iGaming</a>
              <a href="<?php echo esc_url(home_url('/solutions/gig-economy/')); ?>" class="navbar__dropdown-link">Gig Economy</a>
              <a href="<?php echo esc_url(home_url('/solutions/marketplaces/')); ?>" class="navbar__dropdown-link">Marketplaces</a>
              <a href="<?php echo esc_url(home_url('/solutions/social-networks/')); ?>" class="navbar__dropdown-link">Social Networks</a>
            </div>
          </div>
        </div>
        <div style="position:relative">
          <button class="navbar__dropdown-trigger">Company <svg class="navbar__dropdown-arrow" width="10" height="10" viewBox="0 0 10 10" fill="none"><path d="M2 4l3 3 3-3" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg></button>
          <div class="navbar__dropdown-panel navbar__dropdown-panel--narrow">
            <div class="navbar__dropdown-column">
              <a href="<?php echo esc_url(home_url('/about/')); ?>" class="navbar__dropdown-link">About Us</a>
              <a href="<?php echo esc_url(home_url('/careers/')); ?>" class="navbar__dropdown-link">Careers</a>
              <a href="<?php echo esc_url(home_url('/partner-program/')); ?>" class="navbar__dropdown-link">Partner Program</a>
              <a href="<?php echo esc_url(home_url('/events/')); ?>" class="navbar__dropdown-link">Events</a>
              <a href="<?php echo esc_url(home_url('/contact/')); ?>" class="navbar__dropdown-link">Contact</a>
            </div>
          </div>
        </div>
        <div style="position:relative">
          <button class="navbar__dropdown-trigger">Resources <svg class="navbar__dropdown-arrow" width="10" height="10" viewBox="0 0 10 10" fill="none"><path d="M2 4l3 3 3-3" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg></button>
          <div class="navbar__dropdown-panel navbar__dropdown-panel--narrow">
            <div class="navbar__dropdown-column">
              <a href="<?php echo esc_url(home_url('/blog/')); ?>" class="navbar__dropdown-link">Blog</a>
              <a href="<?php echo esc_url(home_url('/resources/')); ?>" class="navbar__dropdown-link">Document Library</a>
              <a href="<?php echo esc_url(home_url('/faqs/')); ?>" class="navbar__dropdown-link">FAQs</a>
              <a href="<?php echo esc_url(home_url('/technical-support/')); ?>" class="navbar__dropdown-link">Technical Support</a>
              <a href="<?php echo esc_url(home_url('/supported-documents/')); ?>" class="navbar__dropdown-link">Supported Documents</a>
            </div>
          </div>
        </div>
        <a href="<?php echo esc_url(home_url('/pricing/')); ?>">Pricing</a>
        <a href="<?php echo esc_url(home_url('/contact/')); ?>" class="btn btn--primary btn--sm navbar__cta">Get Started</a>
      </div>
      <button class="navbar__toggle" aria-label="Toggle menu"><span></span><span></span><span></span></button>
    </div>
    <div class="navbar__mobile">
      <a href="<?php echo esc_url(home_url('/')); ?>">Home</a>
      <div class="navbar__mobile-section">
        <button class="navbar__mobile-section-toggle">Products <svg class="navbar__dropdown-arrow" width="10" height="10" viewBox="0 0 10 10" fill="none"><path d="M2 4l3 3 3-3" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg></button>
        <div class="navbar__mobile-section-links">
          <a href="<?php echo esc_url(home_url('/verification-types/')); ?>">All Verification Types</a>
          <a href="<?php echo esc_url(home_url('/verification-types/criminal-record-checks/')); ?>">Criminal Checks</a>
          <a href="<?php echo esc_url(home_url('/verification-types/identity-verification/')); ?>">Identity Verification</a>
          <a href="<?php echo esc_url(home_url('/verification-types/face-verification/')); ?>">Face Verification</a>
          <a href="<?php echo esc_url(home_url('/verification-types/document-verification/')); ?>">Document Verification</a>
          <a href="<?php echo esc_url(home_url('/compliance/')); ?>">All Compliance</a>
          <a href="<?php echo esc_url(home_url('/compliance/kyc/')); ?>">KYC</a>
          <a href="<?php echo esc_url(home_url('/compliance/aml-screening/')); ?>">AML Screening</a>
          <a href="<?php echo esc_url(home_url('/fraud-prevention/')); ?>">All Fraud Prevention</a>
          <a href="<?php echo esc_url(home_url('/fraud-prevention/identity-fraud/')); ?>">Identity Fraud</a>
        </div>
      </div>
      <div class="navbar__mobile-section">
        <button class="navbar__mobile-section-toggle">Solutions <svg class="navbar__dropdown-arrow" width="10" height="10" viewBox="0 0 10 10" fill="none"><path d="M2 4l3 3 3-3" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg></button>
        <div class="navbar__mobile-section-links">
          <a href="<?php echo esc_url(home_url('/solutions/banking/')); ?>">Banking</a>
          <a href="<?php echo esc_url(home_url('/solutions/fintech/')); ?>">Fintech</a>
          <a href="<?php echo esc_url(home_url('/solutions/cryptocurrency/')); ?>">Cryptocurrency</a>
          <a href="<?php echo esc_url(home_url('/solutions/gaming/')); ?>">Gaming</a>
          <a href="<?php echo esc_url(home_url('/solutions/gig-economy/')); ?>">Gig Economy</a>
          <a href="<?php echo esc_url(home_url('/solutions/marketplaces/')); ?>">Marketplaces</a>
        </div>
      </div>
      <div class="navbar__mobile-section">
        <button class="navbar__mobile-section-toggle">Company <svg class="navbar__dropdown-arrow" width="10" height="10" viewBox="0 0 10 10" fill="none"><path d="M2 4l3 3 3-3" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg></button>
        <div class="navbar__mobile-section-links">
          <a href="<?php echo esc_url(home_url('/about/')); ?>">About Us</a>
          <a href="<?php echo esc_url(home_url('/careers/')); ?>">Careers</a>
          <a href="<?php echo esc_url(home_url('/partner-program/')); ?>">Partner Program</a>
          <a href="<?php echo esc_url(home_url('/events/')); ?>">Events</a>
          <a href="<?php echo esc_url(home_url('/contact/')); ?>">Contact</a>
        </div>
      </div>
      <div class="navbar__mobile-section">
        <button class="navbar__mobile-section-toggle">Resources <svg class="navbar__dropdown-arrow" width="10" height="10" viewBox="0 0 10 10" fill="none"><path d="M2 4l3 3 3-3" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg></button>
        <div class="navbar__mobile-section-links">
          <a href="<?php echo esc_url(home_url('/blog/')); ?>">Blog</a>
          <a href="<?php echo esc_url(home_url('/resources/')); ?>">Document Library</a>
          <a href="<?php echo esc_url(home_url('/faqs/')); ?>">FAQs</a>
          <a href="<?php echo esc_url(home_url('/technical-support/')); ?>">Technical Support</a>
        </div>
      </div>
      <a href="<?php echo esc_url(home_url('/pricing/')); ?>">Pricing</a>
    </div>
  </nav>
