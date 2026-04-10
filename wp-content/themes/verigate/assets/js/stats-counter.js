/**
 * VeriGate Stats Counter
 *
 * Animated number counting triggered by IntersectionObserver.
 */
(function () {
  'use strict';

  if (window.matchMedia('(prefers-reduced-motion: reduce)').matches) return;

  var counters = document.querySelectorAll('[data-counter]');
  if (counters.length === 0) return;

  var observer = new IntersectionObserver(
    function (entries) {
      entries.forEach(function (entry) {
        if (entry.isIntersecting) {
          animateCounters(entry.target);
          observer.unobserve(entry.target);
        }
      });
    },
    { threshold: 0.2 }
  );

  // Observe each counter section
  var sections = document.querySelectorAll('[data-counter-section]');
  sections.forEach(function (section) {
    observer.observe(section);
  });

  function animateCounters(section) {
    var items = section.querySelectorAll('[data-counter]');

    items.forEach(function (item, index) {
      var target = item.dataset.counter;
      var numMatch = target.match(/[\d.]+/);
      if (!numMatch) return;

      var targetValue = parseFloat(numMatch[0]);
      var duration = 2000;
      var startTime = Date.now();

      // Stagger start
      setTimeout(function () {
        var timer = setInterval(function () {
          var now = Date.now();
          var progress = Math.min((now - startTime) / duration, 1);

          // Ease-out exponential
          var eased = progress === 1 ? 1 : 1 - Math.pow(2, -10 * progress);
          var current = targetValue * eased;

          item.textContent = formatValue(current, target);

          if (progress >= 1) {
            item.textContent = target;
            clearInterval(timer);
          }
        }, 16);

        // Fade in the parent
        var parent = item.closest('[data-counter-item]');
        if (parent) {
          parent.style.opacity = '1';
          parent.style.transform = 'translateY(0)';
        }
      }, index * 100);
    });
  }

  function formatValue(count, template) {
    if (template.includes('+')) {
      return Math.floor(count) + '+';
    } else if (template.includes('%')) {
      return count.toFixed(1) + '%';
    } else if (template.includes('B')) {
      return count.toFixed(1) + 'B+';
    } else if (template.includes('<')) {
      return '<' + Math.floor(count) + 's';
    }
    return Math.floor(count).toString();
  }
})();
