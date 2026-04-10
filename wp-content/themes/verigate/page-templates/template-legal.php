<?php
/**
 * Template Name: Legal Page
 *
 * For Privacy Policy, Terms of Service, Cookie Policy, etc.
 *
 * @package VeriGate
 */

get_header(); ?>

<main class="flex-1 pt-16">

    <section class="py-16 md:py-20 bg-secondary/30">
        <div class="container mx-auto max-w-4xl px-4">
            <div class="animate-on-scroll fade-up">
                <?php verigate_breadcrumbs( array(
                    array( 'label' => 'Home', 'url' => home_url( '/' ) ),
                    array( 'label' => get_the_title() ),
                ) ); ?>
                <h1 class="text-4xl md:text-5xl font-bold text-foreground"><?php the_title(); ?></h1>
                <p class="text-muted-foreground mt-3">Last updated: <?php echo get_the_modified_date(); ?></p>
            </div>
        </div>
    </section>

    <section class="py-16 px-4">
        <div class="container mx-auto max-w-4xl">
            <div class="prose prose-lg max-w-none prose-headings:text-foreground prose-p:text-muted-foreground prose-a:text-accent">
                <?php while ( have_posts() ) : the_post(); the_content(); endwhile; ?>
            </div>
        </div>
    </section>

</main>

<?php get_footer();
