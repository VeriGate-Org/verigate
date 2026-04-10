/* ==========================================================================
   VeriGate Main JavaScript
   ==========================================================================
   TEACHING NOTE: This script handles all interactive behaviour for the
   VeriGate website. Each section is documented to explain the technique
   being used, making this a learning resource for web design students.
   ========================================================================== */

(function () {
  'use strict';

  /* =====================================================================
     TECHNIQUE: Mobile Navigation Toggle
     TEACHING: A simple class toggle pattern. When the hamburger button is
     clicked, we add/remove the 'open' class on the mobile nav panel and
     toggle body overflow to prevent background scrolling.
     ===================================================================== */
  const navToggle = document.querySelector('.navbar__toggle');
  const navMobile = document.querySelector('.navbar__mobile');

  if (navToggle && navMobile) {
    navToggle.addEventListener('click', function () {
      navMobile.classList.toggle('open');
      navToggle.classList.toggle('active');
      /* Prevent body scroll when mobile nav is open */
      document.body.style.overflow = navMobile.classList.contains('open') ? 'hidden' : '';
    });

    /* Close mobile nav when any link is clicked */
    navMobile.querySelectorAll('a').forEach(function (link) {
      link.addEventListener('click', function () {
        navMobile.classList.remove('open');
        navToggle.classList.remove('active');
        document.body.style.overflow = '';
      });
    });
  }

  /* =====================================================================
     TECHNIQUE: Navbar Scroll Effect (Frosted Glass Enhancement)
     TEACHING: We listen for the scroll event and toggle a 'scrolled' class
     when the user scrolls past 10px. CSS uses this class to deepen the
     backdrop-filter and add a subtle box-shadow.
     ===================================================================== */
  const navbar = document.querySelector('.navbar');
  if (navbar) {
    window.addEventListener('scroll', function () {
      navbar.classList.toggle('scrolled', window.scrollY > 10);
    });
  }

  /* =====================================================================
     TECHNIQUE: Scroll-Triggered Fade-In (Intersection Observer)
     TEACHING: IntersectionObserver watches elements as they enter the
     viewport. When an element becomes visible (threshold: 10%), we add
     the 'visible' class which triggers a CSS transition (opacity + transform).
     This is much more performant than scroll event listeners.
     ===================================================================== */
  var fadeElements = document.querySelectorAll('.fade-in-up');
  if (fadeElements.length > 0 && 'IntersectionObserver' in window) {
    var fadeObserver = new IntersectionObserver(function (entries) {
      entries.forEach(function (entry) {
        if (entry.isIntersecting) {
          entry.target.classList.add('visible');
          fadeObserver.unobserve(entry.target); /* Only animate once */
        }
      });
    }, { threshold: 0.1, rootMargin: '0px 0px -40px 0px' });

    fadeElements.forEach(function (el) {
      fadeObserver.observe(el);
    });
  }

  /* =====================================================================
     TECHNIQUE: Animated Number Counter
     TEACHING: When stat numbers scroll into view, we animate them from 0
     to their target value using requestAnimationFrame. The easing function
     (1 - Math.pow(1 - progress, 3)) creates a smooth deceleration effect
     — the number counts fast at first then slows as it approaches the target.
     ===================================================================== */
  function animateCounter(el) {
    var text = el.textContent.trim();
    var match = text.match(/^([\d,.]+)/);
    if (!match) return;

    var raw = match[1];
    var suffix = text.slice(raw.length);
    var hasComma = raw.indexOf(',') !== -1;
    var hasDot = raw.indexOf('.') !== -1 && !hasComma;
    var target = parseFloat(raw.replace(/,/g, ''));
    if (isNaN(target)) return;

    var decimals = hasDot ? (raw.split('.')[1] || '').length : 0;
    var duration = 1200;
    var start = 0;
    var startTime = null;

    function step(timestamp) {
      if (!startTime) startTime = timestamp;
      var progress = Math.min((timestamp - startTime) / duration, 1);
      /* Cubic ease-out: fast start, slow finish */
      var eased = 1 - Math.pow(1 - progress, 3);
      var current = start + (target - start) * eased;

      var formatted;
      if (decimals > 0) {
        formatted = current.toFixed(decimals);
      } else {
        formatted = Math.round(current).toString();
        if (hasComma && formatted.length > 3) {
          formatted = formatted.replace(/\B(?=(\d{3})+(?!\d))/g, ',');
        }
      }
      el.textContent = formatted + suffix;

      if (progress < 1) {
        requestAnimationFrame(step);
      }
    }
    requestAnimationFrame(step);
  }

  var statElements = document.querySelectorAll('.stat__number, .stat-card__value');
  if (statElements.length > 0 && 'IntersectionObserver' in window) {
    var statObserver = new IntersectionObserver(function (entries) {
      entries.forEach(function (entry) {
        if (entry.isIntersecting) {
          animateCounter(entry.target);
          statObserver.unobserve(entry.target);
        }
      });
    }, { threshold: 0.3 });

    statElements.forEach(function (el) {
      statObserver.observe(el);
    });
  }

  /* =====================================================================
     TECHNIQUE: YouTube Facade (Performance Pattern)
     TEACHING: Instead of embedding a heavy YouTube iframe on page load,
     we show a thumbnail image with a play button. Only when the user clicks
     do we create the iframe. This saves ~500KB of initial page weight.
     ===================================================================== */
  document.querySelectorAll('.youtube-facade').forEach(function (facade) {
    facade.addEventListener('click', function () {
      var videoId = this.dataset.videoId;
      if (!videoId) return;
      var iframe = document.createElement('iframe');
      iframe.src = 'https://www.youtube.com/embed/' + videoId + '?autoplay=1&rel=0';
      iframe.allow = 'accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture';
      iframe.allowFullscreen = true;
      this.innerHTML = '';
      this.appendChild(iframe);
    });
  });

  /* =====================================================================
     TECHNIQUE: Document Library Search & Filter
     TEACHING: Client-side filtering with two inputs — a text search and
     category tabs. Both filters are combined: the text search checks title,
     description, and tags, while the tab filters by category. Results are
     re-rendered as HTML strings on every input change.
     ===================================================================== */
  var documents = [
    { title: 'Criminal Record Check \u2014 Sample Report', description: 'A sample criminal record verification report showing the standard format and data fields returned by VeriGate.', category: 'Verification Reports', tags: ['criminal', 'sample', 'report'], file: 'docs/criminal-record-sample-report.pdf', size: '245 KB' },
    { title: 'Employment Verification \u2014 Sample Report', description: 'Sample employment history verification report including dates, roles, and reason for leaving.', category: 'Verification Reports', tags: ['employment', 'sample', 'report'], file: 'docs/employment-verification-sample-report.pdf', size: '198 KB' },
    { title: 'Qualification Check \u2014 Sample Report', description: 'Academic qualification verification sample showing institution confirmation and qualification details.', category: 'Verification Reports', tags: ['qualification', 'academic', 'report'], file: 'docs/qualification-check-sample-report.pdf', size: '210 KB' },
    { title: 'Identity Verification \u2014 Sample Report', description: 'ID number validation report with DHA cross-reference results and identity confirmation.', category: 'Verification Reports', tags: ['identity', 'ID', 'DHA', 'report'], file: 'docs/identity-verification-sample-report.pdf', size: '156 KB' },
    { title: 'Credit Check \u2014 Sample Report', description: 'Consumer credit profile report showing credit score, payment history, and judgements.', category: 'Verification Reports', tags: ['credit', 'financial', 'report'], file: 'docs/credit-check-sample-report.pdf', size: '312 KB' },
    { title: 'POPIA Compliance Guide', description: 'Comprehensive guide to ensuring your verification processes comply with the Protection of Personal Information Act.', category: 'Compliance Templates', tags: ['POPIA', 'compliance', 'privacy', 'regulation'], file: 'docs/popia-compliance-guide.pdf', size: '420 KB' },
    { title: 'Consent Form Template', description: 'Standard candidate consent form template for background verification, POPIA-compliant.', category: 'Compliance Templates', tags: ['consent', 'POPIA', 'template', 'form'], file: 'docs/consent-form-template.pdf', size: '85 KB' },
    { title: 'Data Retention Policy Template', description: 'Template data retention policy for organisations conducting background verifications.', category: 'Compliance Templates', tags: ['data', 'retention', 'policy', 'template'], file: 'docs/data-retention-policy-template.pdf', size: '128 KB' },
    { title: 'Criminal Record Act Overview', description: 'Summary of the Criminal Procedure Act as it relates to background screening and employer obligations.', category: 'Regulatory Guides', tags: ['criminal', 'law', 'regulation', 'employer'], file: 'docs/criminal-record-act-overview.pdf', size: '340 KB' },
    { title: 'NQF & SAQA Verification Guide', description: 'Guide to understanding the National Qualifications Framework and SAQA verification processes.', category: 'Regulatory Guides', tags: ['NQF', 'SAQA', 'qualification', 'framework'], file: 'docs/nqf-saqa-verification-guide.pdf', size: '275 KB' },
    { title: 'Employment Equity Act \u2014 Screening Implications', description: 'How the Employment Equity Act affects background screening practices and employer responsibilities.', category: 'Regulatory Guides', tags: ['employment', 'equity', 'regulation', 'screening'], file: 'docs/employment-equity-screening.pdf', size: '190 KB' },
    { title: 'VeriGate API Documentation v2.1', description: 'Complete API reference for integrating VeriGate verification services into your systems via REST API.', category: 'Platform Documentation', tags: ['API', 'integration', 'REST', 'developer'], file: 'docs/verigate-api-documentation.pdf', size: '1.2 MB' },
    { title: 'Platform User Guide', description: 'End-to-end user guide for the VeriGate platform covering account setup, verification requests, and reporting.', category: 'Platform Documentation', tags: ['guide', 'user', 'platform', 'onboarding'], file: 'docs/platform-user-guide.pdf', size: '890 KB' },
    { title: 'Bulk Upload Template', description: 'CSV template for submitting bulk verification requests through the VeriGate platform.', category: 'Platform Documentation', tags: ['bulk', 'upload', 'CSV', 'template'], file: 'docs/bulk-upload-template.pdf', size: '12 KB' },
    { title: 'SSO Integration Guide', description: 'Technical guide for configuring Single Sign-On with VeriGate using SAML 2.0 or OAuth 2.0.', category: 'Platform Documentation', tags: ['SSO', 'SAML', 'OAuth', 'security', 'integration'], file: 'docs/sso-integration-guide.pdf', size: '345 KB' },
    { title: 'Webhook Configuration Guide', description: 'How to set up webhooks for real-time verification status notifications from VeriGate.', category: 'Platform Documentation', tags: ['webhook', 'notification', 'real-time', 'developer'], file: 'docs/webhook-configuration-guide.pdf', size: '210 KB' }
  ];

  /* Resolve document base path — WordPress passes themeUrl via wp_localize_script */
  var docsBaseUrl = (typeof verigateConfig !== 'undefined' && verigateConfig.themeUrl)
    ? verigateConfig.themeUrl + '/'
    : '';

  var docSearch = document.getElementById('doc-search');
  var docTabs = document.querySelectorAll('.doc-tab');
  var docList = document.getElementById('doc-list');
  var activeCategory = 'All';

  function renderDocuments(filter, category) {
    if (!docList) return;
    filter = (filter || '').toLowerCase();
    var filtered = documents.filter(function (doc) {
      var matchesCategory = category === 'All' || doc.category === category;
      if (!matchesCategory) return false;
      if (!filter) return true;
      var searchable = (doc.title + ' ' + doc.description + ' ' + doc.tags.join(' ')).toLowerCase();
      return searchable.indexOf(filter) !== -1;
    });

    if (filtered.length === 0) {
      docList.innerHTML = '<div class="doc-empty">No documents match your search. Try a different keyword or category.</div>';
      return;
    }

    docList.innerHTML = filtered.map(function (doc) {
      return '<div class="doc-card">' +
        '<div class="doc-card__icon">\uD83D\uDCC4</div>' +
        '<div class="doc-card__body">' +
        '<div class="doc-card__title">' + doc.title + '</div>' +
        '<p class="doc-card__desc">' + doc.description + '</p>' +
        '<div class="doc-card__meta">' +
        '<span class="doc-card__badge">' + doc.category + '</span>' +
        '<span class="doc-card__badge">' + doc.size + '</span>' +
        '<a href="' + docsBaseUrl + doc.file + '" class="doc-card__download">Download PDF \u2193</a>' +
        '</div></div></div>';
    }).join('');
  }

  if (docSearch) {
    docSearch.addEventListener('input', function () {
      renderDocuments(this.value, activeCategory);
    });
  }

  docTabs.forEach(function (tab) {
    tab.addEventListener('click', function () {
      docTabs.forEach(function (t) { t.classList.remove('active'); });
      this.classList.add('active');
      activeCategory = this.dataset.category;
      renderDocuments(docSearch ? docSearch.value : '', activeCategory);
    });
  });

  /* Initial render */
  if (docList) {
    renderDocuments('', 'All');
  }

  /* =====================================================================
     TECHNIQUE: Typing Animation (Typewriter Effect)
     TEACHING: This creates a typewriter effect by incrementally showing
     and deleting characters from an array of phrases. The cursor (a CSS
     animated element) blinks beside the text. We use setTimeout recursion
     rather than setInterval for variable-speed typing.
     ===================================================================== */
  var typingEl = document.querySelector('.typing-text');
  if (typingEl) {
    var phrases = JSON.parse(typingEl.dataset.phrases || '[]');
    if (phrases.length > 1) {
      var phraseIndex = 0;
      var charIndex = phrases[0].length;
      var isDeleting = false;
      var typeSpeed = 60;
      var deleteSpeed = 35;
      var pauseEnd = 2000;
      var pauseStart = 500;

      function typeLoop() {
        var current = phrases[phraseIndex];
        if (isDeleting) {
          charIndex--;
          typingEl.textContent = current.substring(0, charIndex);
          if (charIndex === 0) {
            isDeleting = false;
            phraseIndex = (phraseIndex + 1) % phrases.length;
            setTimeout(typeLoop, pauseStart);
            return;
          }
          setTimeout(typeLoop, deleteSpeed);
        } else {
          charIndex++;
          typingEl.textContent = current.substring(0, charIndex);
          if (charIndex === current.length) {
            isDeleting = true;
            setTimeout(typeLoop, pauseEnd);
            return;
          }
          setTimeout(typeLoop, typeSpeed);
        }
      }
      setTimeout(typeLoop, pauseEnd);
    }
  }

  /* =====================================================================
     TECHNIQUE: Smooth Scroll for Anchor Links
     TEACHING: Intercept clicks on hash links (#section) and use the native
     scrollIntoView API with behavior: 'smooth' for animated scrolling.
     This provides a better UX than the instant jump behaviour.
     ===================================================================== */
  document.querySelectorAll('a[href^="#"]').forEach(function (link) {
    link.addEventListener('click', function (e) {
      var target = document.querySelector(this.getAttribute('href'));
      if (target) {
        e.preventDefault();
        target.scrollIntoView({ behavior: 'smooth' });
      }
    });
  });

  /* =====================================================================
     TECHNIQUE: Sticky Card Scroll Scale Effect
     TEACHING: When cards are stacked using position: sticky, they overlap
     as you scroll. This script calculates how much each card is "covered"
     by the card below it, then applies a scale() and opacity reduction
     to create a visual depth/stacking illusion.

     How it works:
     1. On scroll, we check each sticky card's position
     2. If a card is being scrolled past (stuck at top), we calculate
        how far the next card has overlapped it
     3. We apply a proportional scale-down (0.95) and opacity (0.6)
     4. This makes cards appear to recede into the background
     ===================================================================== */
  var stickyContainer = document.querySelector('.sticky-cards');
  if (stickyContainer && window.innerWidth > 768) {
    var stickyCards = stickyContainer.querySelectorAll('.card');

    function updateStickyCards() {
      var cards = Array.from(stickyCards);
      for (var i = 0; i < cards.length - 1; i++) {
        var currentRect = cards[i].getBoundingClientRect();
        var nextRect = cards[i + 1].getBoundingClientRect();

        /* Calculate overlap: how much the next card covers this one */
        var overlap = currentRect.bottom - nextRect.top;
        var cardHeight = currentRect.height;

        if (overlap > 0 && cardHeight > 0) {
          /* Normalize overlap to 0-1 range */
          var progress = Math.min(overlap / cardHeight, 1);
          /* Scale down from 1 to 0.95, reduce opacity from 1 to 0.6 */
          var scale = 1 - (progress * 0.05);
          var opacity = 1 - (progress * 0.4);
          cards[i].style.transform = 'scale(' + scale + ')';
          cards[i].style.opacity = opacity;
        } else {
          /* No overlap — reset to default */
          cards[i].style.transform = 'scale(1)';
          cards[i].style.opacity = '1';
        }
      }
    }

    window.addEventListener('scroll', updateStickyCards, { passive: true });
    /* Also update on resize in case layout changes */
    window.addEventListener('resize', updateStickyCards, { passive: true });
  }

  /* =====================================================================
     TECHNIQUE: Mega-Menu Dropdown Interactions
     TEACHING: Desktop uses mouseenter/mouseleave with a 150ms delay to
     prevent flickering. Mobile uses click-toggle accordion. Escape key
     and click-outside close all open panels.
     ===================================================================== */
  var dropdownTriggers = document.querySelectorAll('.navbar__dropdown-trigger');
  var dropdownTimers = {};

  function closeAllDropdowns() {
    dropdownTriggers.forEach(function (t) {
      t.classList.remove('open');
      var panel = t.nextElementSibling;
      if (panel && panel.classList.contains('navbar__dropdown-panel')) {
        panel.classList.remove('open');
      }
    });
  }

  if (dropdownTriggers.length > 0 && window.innerWidth > 768) {
    dropdownTriggers.forEach(function (trigger, idx) {
      var panel = trigger.nextElementSibling;
      if (!panel || !panel.classList.contains('navbar__dropdown-panel')) return;
      var wrapper = trigger.parentElement;

      wrapper.addEventListener('mouseenter', function () {
        clearTimeout(dropdownTimers[idx]);
        closeAllDropdowns();
        trigger.classList.add('open');
        panel.classList.add('open');
      });

      wrapper.addEventListener('mouseleave', function () {
        dropdownTimers[idx] = setTimeout(function () {
          trigger.classList.remove('open');
          panel.classList.remove('open');
        }, 150);
      });
    });
  }

  /* Click-outside closes dropdowns */
  document.addEventListener('click', function (e) {
    if (!e.target.closest('.navbar__dropdown-trigger') && !e.target.closest('.navbar__dropdown-panel')) {
      closeAllDropdowns();
    }
  });

  /* Escape key closes dropdowns */
  document.addEventListener('keydown', function (e) {
    if (e.key === 'Escape') closeAllDropdowns();
  });

  /* Mobile accordion toggles */
  document.querySelectorAll('.navbar__mobile-section-toggle').forEach(function (btn) {
    btn.addEventListener('click', function () {
      var links = this.nextElementSibling;
      var isOpen = this.classList.contains('open');
      /* Close all other sections */
      document.querySelectorAll('.navbar__mobile-section-toggle').forEach(function (b) {
        b.classList.remove('open');
        if (b.nextElementSibling) b.nextElementSibling.classList.remove('open');
      });
      if (!isOpen) {
        this.classList.add('open');
        if (links) links.classList.add('open');
      }
    });
  });

  /* FAQ accordion */
  document.querySelectorAll('.faq-accordion__trigger').forEach(function (btn) {
    btn.addEventListener('click', function () {
      var content = this.nextElementSibling;
      var isOpen = this.classList.contains('open');
      this.classList.toggle('open');
      if (content) content.classList.toggle('open');
    });
  });

  /* =====================================================================
     TECHNIQUE: Image Fallback Handler
     TEACHING: When an <img> with data-fallback fails to load (e.g. the
     file doesn't exist yet), we hide the broken image and add a fallback
     class to the parent container. The CSS fallback class shows a
     gradient + icon instead of a broken image icon.
     ===================================================================== */
  document.querySelectorAll('img[data-fallback]').forEach(function (img) {
    img.addEventListener('error', function () {
      this.style.display = 'none';
      var parent = this.parentElement;
      if (parent) {
        parent.classList.add(this.dataset.fallback);
      }
    });
  });

  /* =====================================================================
     TECHNIQUE: Mouse-Following Glow Effect
     TEACHING: This creates a spotlight/glow that follows the mouse cursor
     over cards. It works by:
     1. Listening for mousemove on cards with the .card--glow class
     2. Calculating the mouse position relative to the card (as percentages)
     3. Setting CSS custom properties (--mouse-x, --mouse-y) on the card
     4. CSS uses these variables in a radial-gradient on a ::after pseudo-element

     The CSS side (in style.css) creates the visual effect:
       background: radial-gradient(
         400px circle at var(--mouse-x) var(--mouse-y),
         rgba(220, 38, 38, 0.06),
         transparent 60%
       );

     This approach is performant because:
     - CSS custom properties trigger only a repaint, not a reflow
     - The gradient is on a pseudo-element with pointer-events: none
     - We use passive event listeners for mousemove
     ===================================================================== */
  var glowCards = document.querySelectorAll('.card--glow');
  if (glowCards.length > 0) {
    glowCards.forEach(function (card) {
      card.addEventListener('mousemove', function (e) {
        var rect = card.getBoundingClientRect();
        /* Calculate position as percentage (0% to 100%) */
        var x = ((e.clientX - rect.left) / rect.width) * 100;
        var y = ((e.clientY - rect.top) / rect.height) * 100;
        /* Set CSS custom properties for the gradient position */
        card.style.setProperty('--mouse-x', x + '%');
        card.style.setProperty('--mouse-y', y + '%');
      }, { passive: true });

      /* Reset glow position when mouse leaves */
      card.addEventListener('mouseleave', function () {
        card.style.setProperty('--mouse-x', '50%');
        card.style.setProperty('--mouse-y', '50%');
      });
    });
  }

})();

/* =====================================================================
   TECHNIQUE: Cookie Consent Banner (POPIA Compliance)
   TEACHING: On first visit, show a cookie consent banner. When the user
   clicks Accept or Decline, store their preference in localStorage so
   the banner doesn't reappear on subsequent visits. We use
   DOMContentLoaded because the banner HTML appears after the script tag.
   ===================================================================== */
document.addEventListener('DOMContentLoaded', function () {
  var cookieBanner = document.querySelector('.cookie-banner');
  if (!cookieBanner) return;

  var consent = localStorage.getItem('verigate_cookie_consent');
  if (!consent) {
    setTimeout(function () {
      cookieBanner.classList.add('visible');
    }, 1000);
  }

  var acceptBtn = cookieBanner.querySelector('.cookie-banner__btn--accept');
  var declineBtn = cookieBanner.querySelector('.cookie-banner__btn--decline');

  if (acceptBtn) {
    acceptBtn.addEventListener('click', function () {
      localStorage.setItem('verigate_cookie_consent', 'accepted');
      cookieBanner.classList.remove('visible');
    });
  }
  if (declineBtn) {
    declineBtn.addEventListener('click', function () {
      localStorage.setItem('verigate_cookie_consent', 'declined');
      cookieBanner.classList.remove('visible');
    });
  }
});
