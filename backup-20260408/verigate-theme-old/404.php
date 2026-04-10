<?php
/**
 * 404 page template.
 */
get_header();
?>

  <section class="hero hero--dark">
    <div class="container">
      <div class="hero__centered">
        <h1 class="hero__title" style="font-size:6rem;margin-bottom:var(--spacing-4)">404</h1>
        <p class="hero__subtitle">The page you're looking for doesn't exist or has been moved.</p>
        <div class="hero__actions" style="margin-top:var(--spacing-6)">
          <a href="<?php echo esc_url(home_url('/')); ?>" class="btn btn--primary btn--lg">Back to Home</a>
          <a href="<?php echo esc_url(home_url('/contact/')); ?>" class="btn btn--outline-white btn--lg">Contact Us</a>
        </div>
      </div>
    </div>
  </section>

<?php get_footer(); ?>
