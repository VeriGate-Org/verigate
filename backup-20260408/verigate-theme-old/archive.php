<?php
/**
 * Blog listing / archive template.
 */
get_header();
$t = get_template_directory_uri();
?>

  <section class="hero hero--dark">
    <div class="container">
      <div class="hero__centered">
        <span class="badge">VeriGate Blog</span>
        <h1 class="hero__title">Insights & Resources</h1>
        <p class="hero__subtitle">Stay informed with the latest in background screening, compliance regulations, and verification technology across South Africa.</p>
      </div>
    </div>
  </section>

  <section class="section fade-in-up">
    <div class="container">
      <div class="grid grid--3">
        <?php if (have_posts()) : while (have_posts()) : the_post(); ?>
        <a href="<?php the_permalink(); ?>" class="card" style="text-decoration:none;color:inherit">
          <?php if (has_post_thumbnail()) : ?>
          <div style="border-radius:var(--radius-lg) var(--radius-lg) 0 0;overflow:hidden;aspect-ratio:16/9">
            <?php the_post_thumbnail('medium_large', array('style' => 'width:100%;height:100%;object-fit:cover', 'loading' => 'lazy')); ?>
          </div>
          <?php endif; ?>
          <div style="padding:var(--spacing-4)">
            <span class="badge" style="font-size:0.7rem"><?php echo esc_html(get_the_date('d M Y')); ?></span>
            <h3 class="card__title" style="margin-top:var(--spacing-2)"><?php the_title(); ?></h3>
            <p class="card__text"><?php echo wp_trim_words(get_the_excerpt(), 20); ?></p>
          </div>
        </a>
        <?php endwhile; endif; ?>
      </div>

      <?php
      the_posts_pagination(array(
          'mid_size'  => 2,
          'prev_text' => '&laquo; Previous',
          'next_text' => 'Next &raquo;',
      ));
      ?>
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

<?php get_footer(); ?>
