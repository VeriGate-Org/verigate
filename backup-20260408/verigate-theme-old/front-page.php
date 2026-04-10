<?php
/**
 * Template Name: Homepage
 * Front page template — hardcoded sections matching the static index.html
 */
get_header();
$t = get_template_directory_uri();
?>

  <!-- Hero Section -->
  <section class="hero hero--premium">
    <div class="hero-orb hero-orb--red hero-orb--1"></div>
    <div class="hero-orb hero-orb--blue hero-orb--2"></div>
    <div class="hero-orb hero-orb--red hero-orb--3"></div>
    <div class="container">
      <div class="hero__split">
        <div class="hero__content">
          <span class="badge">Enterprise Verification Platform</span>
          <h1><span class="typing-text" data-phrases='["Trusted Background Screening","Compliance-Ready Verification","Enterprise-Grade Security"]'>Trusted Background Screening</span> for South Africa<span class="typing-cursor"></span></h1>
          <p class="hero__subtitle">Comprehensive, accurate, and compliant verification services that help organisations make confident hiring and onboarding decisions. From criminal checks to qualification validation, VeriGate covers every step.</p>
          <div class="hero__actions">
            <a href="<?php echo esc_url(home_url('/contact/')); ?>" class="btn btn--primary btn--lg">Get Started</a>
            <a href="<?php echo esc_url(home_url('/platform/')); ?>" class="btn btn--outline-white btn--lg">View Platform</a>
          </div>
        </div>
        <div class="hero__media">
          <div class="youtube-facade" data-video-id="O0oF4YrzNEM">
            <img src="https://img.youtube.com/vi/O0oF4YrzNEM/maxresdefault.jpg" alt="What is Digital Identity Verification — Explained" class="youtube-facade__thumb" loading="lazy">
            <button class="youtube-facade__play" aria-label="Play video">
              <svg viewBox="0 0 68 48" width="68" height="48">
                <path d="M66.52 7.74c-.78-2.93-2.49-5.41-5.42-6.19C55.79.13 34 0 34 0S12.21.13 6.9 1.55c-2.93.78-4.63 3.26-5.42 6.19C.06 13.05 0 24 0 24s.06 10.95 1.48 16.26c.78 2.93 2.49 5.41 5.42 6.19C12.21 47.87 34 48 34 48s21.79-.13 27.1-1.55c2.93-.78 4.64-3.26 5.42-6.19C67.94 34.95 68 24 68 24s-.06-10.95-1.48-16.26z" fill="red"/>
                <path d="M45 24L27 14v20" fill="white"/>
              </svg>
            </button>
          </div>
        </div>
      </div>
    </div>
  </section>

  <!-- Stats Section -->
  <section class="stats fade-in-up">
    <div class="container">
      <div class="stats__grid">
        <div class="stat">
          <span class="stat__number">50,000+</span>
          <span class="stat__label">Verifications Completed</span>
        </div>
        <div class="stat">
          <span class="stat__number">99.2%</span>
          <span class="stat__label">Accuracy Rate</span>
        </div>
        <div class="stat">
          <span class="stat__number">24hrs</span>
          <span class="stat__label">Average Turnaround</span>
        </div>
        <div class="stat">
          <span class="stat__number">200+</span>
          <span class="stat__label">Enterprise Clients</span>
        </div>
      </div>
    </div>
  </section>

  <!-- Trust Bar -->
  <section class="trust-bar fade-in-up">
    <div class="container">
      <div class="trust-bar__inner">
        <span class="trust-bar__label">Trusted & Certified</span>
        <div class="cert-badges">
          <div class="cert-badge">
            <div class="cert-badge__icon cert-badge__icon--popia"><svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/><path d="m9 12 2 2 4-4"/></svg></div>
            <span class="cert-badge__label">POPIA Compliant</span>
          </div>
          <div class="cert-badge">
            <div class="cert-badge__icon cert-badge__icon--iso"><svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><path d="m9 12 2 2 4-4"/></svg></div>
            <span class="cert-badge__label">ISO 27001</span>
          </div>
          <div class="cert-badge">
            <div class="cert-badge__icon cert-badge__icon--soc2"><svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="11" width="18" height="11" rx="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg></div>
            <span class="cert-badge__label">SOC 2 Type II</span>
          </div>
        </div>
      </div>
    </div>
  </section>

  <!-- Client Logo Carousel -->
  <section class="section" style="padding:var(--spacing-10) 0">
    <div class="container">
      <p style="text-align:center;font-size:0.8rem;font-weight:600;color:#94A3B8;text-transform:uppercase;letter-spacing:0.08em;margin-bottom:var(--spacing-6)">Trusted by leading South African organisations</p>
      <div class="logo-carousel">
        <div class="logo-carousel__track">
          <span class="logo-carousel__text">Standard Bank</span>
          <span class="logo-carousel__text">Discovery</span>
          <span class="logo-carousel__text">Vodacom</span>
          <span class="logo-carousel__text">Old Mutual</span>
          <span class="logo-carousel__text">Absa Group</span>
          <span class="logo-carousel__text">Nedbank</span>
          <span class="logo-carousel__text">Sanlam</span>
          <span class="logo-carousel__text">Sasol</span>
          <span class="logo-carousel__text">MTN Group</span>
          <span class="logo-carousel__text">Capitec</span>
          <span class="logo-carousel__text">Standard Bank</span>
          <span class="logo-carousel__text">Discovery</span>
          <span class="logo-carousel__text">Vodacom</span>
          <span class="logo-carousel__text">Old Mutual</span>
          <span class="logo-carousel__text">Absa Group</span>
          <span class="logo-carousel__text">Nedbank</span>
          <span class="logo-carousel__text">Sanlam</span>
          <span class="logo-carousel__text">Sasol</span>
          <span class="logo-carousel__text">MTN Group</span>
          <span class="logo-carousel__text">Capitec</span>
        </div>
      </div>
    </div>
  </section>

  <!-- Features Section -->
  <section class="section section--gradient-mesh fade-in-up">
    <div class="container">
      <div class="section__header">
        <h2>Comprehensive Verification Services</h2>
        <p>From identity validation to compliance monitoring, VeriGate delivers thorough and reliable screening across every critical category.</p>
      </div>
      <div class="grid grid--3">
        <div class="card">
          <div class="card__icon"><svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/><circle cx="12" cy="11" r="3"/><path d="m14.5 13.5 2 2"/></svg></div>
          <h3 class="card__title">Criminal Checks</h3>
          <p class="card__text">Thorough criminal record screening through the South African Police Service and international databases to ensure a safe and trustworthy workforce.</p>
        </div>
        <div class="card">
          <div class="card__icon"><svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="m22 10-10-5L2 10l10 5 10-5z"/><path d="M6 12v5c0 0 3 3 6 3s6-3 6-3v-5"/><path d="M22 10v6"/></svg></div>
          <h3 class="card__title">Qualification Verification</h3>
          <p class="card__text">Direct validation of academic credentials, professional certifications, and training records with universities and issuing institutions.</p>
        </div>
        <div class="card">
          <div class="card__icon"><svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><rect x="2" y="7" width="20" height="14" rx="2"/><path d="M16 7V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v2"/><path d="M12 12v.01"/></svg></div>
          <h3 class="card__title">Employment History</h3>
          <p class="card__text">Comprehensive employment history verification including job titles, dates of employment, reasons for leaving, and performance references.</p>
        </div>
        <div class="card">
          <div class="card__icon"><svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M2 12C2 6.5 6.5 2 12 2a10 10 0 0 1 8 4"/><path d="M5 19.5C5.5 18 6 15 6 12c0-3.3 2.7-6 6-6 1.8 0 3.4.8 4.5 2"/><path d="M12 10a2 2 0 0 1 2 2c0 4.4-1.2 8-2.5 10"/><path d="M8.5 16.5c-.3 1.5-.5 3-.5 4.5"/><path d="M17 12c0 2-.5 4-1.5 6"/><path d="M22 12c0 2.7-.7 5.2-2 7.5"/></svg></div>
          <h3 class="card__title">Identity Validation</h3>
          <p class="card__text">Multi-layered identity verification using the Department of Home Affairs database, biometric matching, and document authentication.</p>
        </div>
        <div class="card">
          <div class="card__icon"><svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><rect x="1" y="4" width="22" height="16" rx="2"/><path d="M1 10h22"/><path d="M6 16h4"/></svg></div>
          <h3 class="card__title">Credit Screening</h3>
          <p class="card__text">Detailed credit profile assessments through registered credit bureaus to evaluate financial responsibility and risk for relevant roles.</p>
        </div>
        <div class="card">
          <div class="card__icon"><svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M9 5H7a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V7a2 2 0 0 0-2-2h-2"/><rect x="9" y="3" width="6" height="4" rx="1"/><path d="m9 14 2 2 4-4"/></svg></div>
          <h3 class="card__title">Compliance Monitoring</h3>
          <p class="card__text">Ongoing regulatory compliance monitoring including sanctions screening, PEP checks, and industry-specific regulatory requirements.</p>
        </div>
      </div>
    </div>
  </section>

  <!-- How It Works Section -->
  <section class="section section--gradient-cool fade-in-up">
    <div class="container">
      <div class="section__header">
        <h2>How It Works</h2>
        <p>Our streamlined four-step process ensures fast, accurate verification results every time.</p>
      </div>
      <div class="steps">
        <div class="step">
          <div class="step__number">1</div>
          <h3 class="step__title">Submit Request</h3>
          <p class="step__text">Upload candidate details through our secure portal or API. Select the verification types required and submit your request in minutes.</p>
        </div>
        <div class="step">
          <div class="step__number">2</div>
          <h3 class="step__title">Verification Processing</h3>
          <p class="step__text">Our automated systems and expert analysts begin processing your request, cross-referencing multiple authoritative data sources in real time.</p>
        </div>
        <div class="step">
          <div class="step__number">3</div>
          <h3 class="step__title">Quality Assurance</h3>
          <p class="step__text">Every result undergoes a rigorous quality assurance review to ensure accuracy, completeness, and compliance before delivery.</p>
        </div>
        <div class="step">
          <div class="step__number">4</div>
          <h3 class="step__title">Report Delivered</h3>
          <p class="step__text">Receive a comprehensive, easy-to-read verification report via your preferred channel -- portal, email, or directly through your HR system.</p>
        </div>
      </div>
    </div>
  </section>

  <!-- Video Section -->
  <section class="section fade-in-up">
    <div class="container">
      <div class="section__header">
        <h2>Understanding the KYC Verification Process</h2>
        <p>Learn how the three key steps of KYC verification ensure compliance and reduce risk in the onboarding process.</p>
      </div>
      <div class="youtube-facade youtube-facade--large" data-video-id="MfCHOSL5PgI">
        <img src="https://img.youtube.com/vi/MfCHOSL5PgI/maxresdefault.jpg" alt="3 Steps for KYC Verification Process" class="youtube-facade__thumb" loading="lazy">
        <button class="youtube-facade__play" aria-label="Play video">
          <svg viewBox="0 0 68 48" width="68" height="48">
            <path d="M66.52 7.74c-.78-2.93-2.49-5.41-5.42-6.19C55.79.13 34 0 34 0S12.21.13 6.9 1.55c-2.93.78-4.63 3.26-5.42 6.19C.06 13.05 0 24 0 24s.06 10.95 1.48 16.26c.78 2.93 2.49 5.41 5.42 6.19C12.21 47.87 34 48 34 48s21.79-.13 27.1-1.55c2.93-.78 4.64-3.26 5.42-6.19C67.94 34.95 68 24 68 24s-.06-10.95-1.48-16.26z" fill="red"/>
            <path d="M45 24L27 14v20" fill="white"/>
          </svg>
        </button>
      </div>
    </div>
  </section>

  <!-- CTA Banner -->
  <section class="cta-banner">
    <div class="container">
      <div class="cta-banner__inner">
        <h2>Ready to streamline your verification process?</h2>
        <p>Get started with VeriGate today and experience faster, more accurate background screening for your organisation.</p>
        <div class="cta-banner__actions">
          <a href="<?php echo esc_url(home_url('/contact/')); ?>" class="btn btn--primary btn--lg">Get Started</a>
          <a href="<?php echo esc_url(home_url('/pricing/')); ?>" class="btn btn--outline-white btn--lg">View Pricing</a>
        </div>
      </div>
    </div>
  </section>

<?php get_footer(); ?>
