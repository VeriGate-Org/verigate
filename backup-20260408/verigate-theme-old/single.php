<?php
/**
 * Single blog post template.
 */
get_header();
$t = get_template_directory_uri();

if (have_posts()) :
    while (have_posts()) : the_post(); ?>

  <section class="hero hero--dark">
    <div class="container">
      <div class="hero__centered">
        <span class="badge"><?php echo esc_html(get_the_date('d M Y')); ?></span>
        <h1 class="hero__title"><?php the_title(); ?></h1>
        <p class="hero__subtitle"><?php echo esc_html(get_the_excerpt()); ?></p>
      </div>
    </div>
  </section>

  <?php if (has_post_thumbnail()) : ?>
  <section class="section">
    <div class="container">
      <div class="process-visual">
        <?php the_post_thumbnail('full', array('loading' => 'lazy')); ?>
      </div>
    </div>
  </section>
  <?php endif; ?>

  <section class="section fade-in-up">
    <div class="container container--narrow">
      <div class="content-section">
        <?php the_content(); ?>
      </div>
    </div>
  </section>

  <section class="cta-banner">
    <div class="container">
      <div class="cta-banner__inner">
        <h2>Ready to streamline your verification process?</h2>
        <p>Get started with VeriGate today and experience faster, more accurate background screening.</p>
        <div class="cta-banner__actions">
          <a href="<?php echo esc_url(home_url('/contact/')); ?>" class="btn btn--primary btn--lg">Get Started</a>
        </div>
      </div>
    </div>
  </section>

    <?php endwhile;
endif;

get_footer();
