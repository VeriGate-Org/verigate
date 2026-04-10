/**
 * VeriGate Scroll Animations
 *
 * Uses IntersectionObserver to trigger CSS animations.
 * Replaces Framer Motion from the React site.
 */
(function () {
  'use strict';

  // Bail if user prefers reduced motion
  if (window.matchMedia('(prefers-reduced-motion: reduce)').matches) {
    // Make everything visible immediately
    document.querySelectorAll('.animate-on-scroll, .stagger-list').forEach(function (el) {
      el.classList.add('is-visible');
    });
    return;
  }

  var observer = new IntersectionObserver(
    function (entries) {
      entries.forEach(function (entry) {
        if (entry.isIntersecting) {
          entry.target.classList.add('is-visible');
          observer.unobserve(entry.target); // Only animate once
        }
      });
    },
    {
      threshold: 0.1,
      rootMargin: '-50px 0px',
    }
  );

  // Observe all elements with animation classes
  document.querySelectorAll('.animate-on-scroll, .stagger-list').forEach(function (el) {
    observer.observe(el);
  });
})();
