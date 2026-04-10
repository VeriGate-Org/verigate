/**
 * VeriGate Carousel
 *
 * Auto-rotating testimonial carousel with manual navigation.
 */
(function () {
  'use strict';

  var carousels = document.querySelectorAll('[data-carousel]');

  carousels.forEach(function (carousel) {
    var items = carousel.querySelectorAll('[data-carousel-item]');
    var dots = carousel.querySelectorAll('[data-carousel-dot]');
    var prevBtn = carousel.querySelector('[data-carousel-prev]');
    var nextBtn = carousel.querySelector('[data-carousel-next]');

    if (items.length === 0) return;

    var currentIndex = 0;
    var isPaused = false;
    var autoplayInterval;
    var autoplayDelay = parseInt(carousel.dataset.carouselDelay || '6000', 10);

    function showSlide(index) {
      items.forEach(function (item, i) {
        item.classList.toggle('hidden', i !== index);
        item.setAttribute('aria-hidden', i !== index ? 'true' : 'false');
      });

      dots.forEach(function (dot, i) {
        dot.classList.toggle('bg-accent', i === index);
        dot.classList.toggle('w-8', i === index);
        dot.classList.toggle('bg-border', i !== index);
        dot.classList.toggle('w-2', i !== index);
        dot.setAttribute('aria-current', i === index ? 'true' : 'false');
      });

      currentIndex = index;
    }

    function next() {
      showSlide((currentIndex + 1) % items.length);
    }

    function prev() {
      showSlide((currentIndex - 1 + items.length) % items.length);
    }

    function startAutoplay() {
      if (autoplayInterval) clearInterval(autoplayInterval);
      autoplayInterval = setInterval(function () {
        if (!isPaused) next();
      }, autoplayDelay);
    }

    function pauseAndResume() {
      isPaused = true;
      setTimeout(function () {
        isPaused = false;
      }, 10000);
    }

    // Event listeners
    if (nextBtn) {
      nextBtn.addEventListener('click', function () {
        next();
        pauseAndResume();
      });
    }

    if (prevBtn) {
      prevBtn.addEventListener('click', function () {
        prev();
        pauseAndResume();
      });
    }

    dots.forEach(function (dot, i) {
      dot.addEventListener('click', function () {
        showSlide(i);
        pauseAndResume();
      });
    });

    // Pause on hover/focus
    carousel.addEventListener('mouseenter', function () { isPaused = true; });
    carousel.addEventListener('mouseleave', function () { isPaused = false; });
    carousel.addEventListener('focusin', function () { isPaused = true; });
    carousel.addEventListener('focusout', function () { isPaused = false; });

    // Keyboard navigation
    carousel.addEventListener('keydown', function (e) {
      if (e.key === 'ArrowLeft') { prev(); pauseAndResume(); }
      if (e.key === 'ArrowRight') { next(); pauseAndResume(); }
    });

    // Initialize
    showSlide(0);
    startAutoplay();
  });
})();
