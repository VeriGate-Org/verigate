<?php
/**
 * Archive — Blog Listing
 *
 * @package VeriGate
 */

get_header(); ?>

<main class="flex-1 pt-16">

    <?php get_template_part( 'template-parts/hero/hero-compact', null, array(
        'title'       => 'Blog',
        'description' => 'Industry insights, compliance updates, and verification best practices for South African businesses',
        'badge'       => 'Insights & Updates',
        'category'    => 'company',
        'breadcrumbs' => array(
            array( 'label' => 'Home', 'url' => home_url( '/' ) ),
            array( 'label' => 'Blog' ),
        ),
    ) ); ?>

    <section class="py-16 px-4">
        <div class="container mx-auto max-w-6xl">
            <?php if ( have_posts() ) : ?>
                <div class="grid md:grid-cols-2 lg:grid-cols-3 gap-8 stagger-list">
                    <?php while ( have_posts() ) : the_post(); ?>
                        <?php get_template_part( 'template-parts/content/content', 'blog' ); ?>
                    <?php endwhile; ?>
                </div>

                <div class="mt-16 flex justify-center">
                    <?php the_posts_pagination( array(
                        'mid_size'  => 2,
                        'prev_text' => '&larr; Previous',
                        'next_text' => 'Next &rarr;',
                    ) ); ?>
                </div>
            <?php else : ?>
                <?php get_template_part( 'template-parts/content/content', 'none' ); ?>
            <?php endif; ?>
        </div>
    </section>

    <?php get_template_part( 'template-parts/cta/cta', null, array( 'variant' => 'blog' ) ); ?>

</main>

<?php get_footer();
