<?php
/**
 * Fallback template — redirects to archive or page as appropriate.
 */
get_header();

if (have_posts()) :
    if (is_home()) :
        // Blog listing
        get_template_part('archive');
    else :
        while (have_posts()) : the_post();
            the_content();
        endwhile;
    endif;
else : ?>
  <section class="section">
    <div class="container container--narrow" style="text-align:center;padding:var(--spacing-16) 0">
      <h1>Nothing Found</h1>
      <p>The page you're looking for doesn't exist.</p>
      <a href="<?php echo esc_url(home_url('/')); ?>" class="btn btn--primary btn--lg">Back to Home</a>
    </div>
  </section>
<?php endif;

get_footer();
