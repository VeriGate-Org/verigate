/**
 * VeriGate Navigation
 *
 * Handles desktop mega-menu dropdowns and mobile accordion menu.
 */
(function () {
  'use strict';

  /* ------------------------------------------------
   * Desktop: Mega-menu hover dropdowns
   * ------------------------------------------------ */
  const triggers = document.querySelectorAll('[data-mega-trigger]');

  triggers.forEach(function (trigger) {
    const btn = trigger.querySelector('[data-dropdown]');
    const dropdown = trigger.querySelector('[data-mega-dropdown]');
    if (!btn || !dropdown) return;

    let hideTimeout;

    function show() {
      clearTimeout(hideTimeout);
      // Close all other dropdowns first
      triggers.forEach(function (t) {
        const d = t.querySelector('[data-mega-dropdown]');
        const b = t.querySelector('[data-dropdown]');
        if (d && d !== dropdown) {
          d.classList.add('hidden');
          if (b) b.setAttribute('aria-expanded', 'false');
        }
      });
      dropdown.classList.remove('hidden');
      btn.setAttribute('aria-expanded', 'true');
    }

    function hide() {
      hideTimeout = setTimeout(function () {
        dropdown.classList.add('hidden');
        btn.setAttribute('aria-expanded', 'false');
      }, 100);
    }

    trigger.addEventListener('mouseenter', show);
    trigger.addEventListener('mouseleave', hide);

    // Keyboard: Enter/Space to toggle
    btn.addEventListener('keydown', function (e) {
      if (e.key === 'Enter' || e.key === ' ') {
        e.preventDefault();
        var isOpen = !dropdown.classList.contains('hidden');
        if (isOpen) {
          hide();
        } else {
          show();
        }
      }
      if (e.key === 'Escape') {
        dropdown.classList.add('hidden');
        btn.setAttribute('aria-expanded', 'false');
        btn.focus();
      }
    });
  });

  /* ------------------------------------------------
   * Mobile: Toggle menu + accordion sections
   * ------------------------------------------------ */
  var mobileToggle = document.getElementById('mobile-menu-toggle');
  var mobileMenu = document.getElementById('mobile-menu');
  var openIcon = mobileToggle ? mobileToggle.querySelector('.menu-open-icon') : null;
  var closeIcon = mobileToggle ? mobileToggle.querySelector('.menu-close-icon') : null;

  if (mobileToggle && mobileMenu) {
    mobileToggle.addEventListener('click', function () {
      var isOpen = !mobileMenu.classList.contains('hidden');
      mobileMenu.classList.toggle('hidden');
      mobileToggle.setAttribute('aria-expanded', String(!isOpen));

      if (openIcon && closeIcon) {
        openIcon.classList.toggle('hidden');
        closeIcon.classList.toggle('hidden');
      }
    });
  }

  // Mobile accordion sections
  var accordions = document.querySelectorAll('[data-mobile-accordion]');

  accordions.forEach(function (accordion) {
    var toggleBtn = accordion.querySelector('[data-mobile-toggle]');
    var panel = accordion.querySelector('[data-mobile-panel]');
    if (!toggleBtn || !panel) return;

    toggleBtn.addEventListener('click', function () {
      var isOpen = !panel.classList.contains('hidden');

      // Close all other panels
      accordions.forEach(function (a) {
        var p = a.querySelector('[data-mobile-panel]');
        var chevron = a.querySelector('[data-mobile-toggle] svg');
        if (p && p !== panel) {
          p.classList.add('hidden');
          if (chevron) chevron.style.transform = '';
        }
      });

      panel.classList.toggle('hidden');
      var chevron = toggleBtn.querySelector('svg');
      if (chevron) {
        chevron.style.transform = isOpen ? '' : 'rotate(180deg)';
      }
    });
  });

  // Close mobile menu when a link is clicked
  if (mobileMenu) {
    mobileMenu.addEventListener('click', function (e) {
      if (e.target.tagName === 'A') {
        mobileMenu.classList.add('hidden');
        if (mobileToggle) mobileToggle.setAttribute('aria-expanded', 'false');
        if (openIcon && closeIcon) {
          openIcon.classList.remove('hidden');
          closeIcon.classList.add('hidden');
        }
      }
    });
  }

  /* ------------------------------------------------
   * Scroll: Add shadow to nav on scroll
   * ------------------------------------------------ */
  var nav = document.getElementById('site-navigation');
  if (nav) {
    window.addEventListener('scroll', function () {
      if (window.scrollY > 10) {
        nav.classList.add('shadow-md');
      } else {
        nav.classList.remove('shadow-md');
      }
    }, { passive: true });
  }
})();
