<?php
/**
 * The main template file
 *
 * Fallback for any content type without a specific template.
 *
 * @package VeriGate
 */

get_header(); ?>

<main class="flex-1 pt-16">
    <div class="container mx-auto max-w-6xl py-16 px-4">
        <?php if ( have_posts() ) : ?>
            <div class="grid gap-8">
                <?php while ( have_posts() ) : the_post(); ?>
                    <article <?php post_class( 'bg-card border border-border rounded-lg p-6 hover:shadow-md transition-shadow' ); ?>>
                        <h2 class="text-2xl font-bold mb-2">
                            <a href="<?php the_permalink(); ?>" class="text-foreground hover:text-accent transition-colors">
                                <?php the_title(); ?>
                            </a>
                        </h2>
                        <div class="text-muted-foreground text-sm mb-4">
                            <?php echo get_the_date(); ?>
                        </div>
                        <div class="text-muted-foreground">
                            <?php the_excerpt(); ?>
                        </div>
                    </article>
                <?php endwhile; ?>
            </div>

            <div class="mt-12">
                <?php the_posts_pagination( array(
                    'mid_size'  => 2,
                    'prev_text' => '&larr; Previous',
                    'next_text' => 'Next &rarr;',
                    'class'     => 'flex justify-center gap-2',
                ) ); ?>
            </div>
        <?php else : ?>
            <?php get_template_part( 'template-parts/content/content', 'none' ); ?>
        <?php endif; ?>
    </div>
</main>

<?php get_footer();
