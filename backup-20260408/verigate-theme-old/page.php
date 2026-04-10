<?php
/**
 * Default page template.
 * Renders the_content() directly — each page's imported HTML includes its own
 * section wrappers and CSS classes, so no additional markup is needed.
 */
get_header();

if (have_posts()) :
    while (have_posts()) : the_post();
        the_content();
    endwhile;
endif;

get_footer();
