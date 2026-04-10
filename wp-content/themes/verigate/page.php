<?php
/**
 * Default Page Template
 *
 * Renders any standard WordPress page with proper hero and content.
 *
 * @package VeriGate
 */

get_header(); ?>

<main class="flex-1 pt-16">

    <?php while ( have_posts() ) : the_post(); ?>

        <!-- Hero -->
        <section class="py-20 bg-gradient-to-br from-secondary via-background to-accent/5 relative overflow-hidden">
            <div class="absolute inset-0 bg-gradient-mesh opacity-30"></div>
            <div class="container mx-auto max-w-6xl relative z-10 px-4">
                <div class="animate-on-scroll fade-up">
                    <h1 class="text-4xl md:text-5xl lg:text-6xl font-bold text-foreground">
                        <?php the_title(); ?>
                    </h1>
                    <?php if ( has_excerpt() ) : ?>
                        <p class="text-xl text-muted-foreground mt-4 max-w-2xl"><?php echo get_the_excerpt(); ?></p>
                    <?php endif; ?>
                </div>
            </div>
        </section>

        <!-- Content -->
        <section class="py-20 px-4">
            <div class="container mx-auto max-w-4xl">
                <div class="entry-content prose prose-lg max-w-none text-foreground
                    prose-headings:text-foreground prose-headings:font-bold
                    prose-h2:text-3xl prose-h2:mt-12 prose-h2:mb-6
                    prose-h3:text-xl prose-h3:mt-8 prose-h3:mb-4
                    prose-p:text-muted-foreground prose-p:leading-relaxed
                    prose-li:text-muted-foreground
                    prose-strong:text-foreground
                    prose-a:text-accent prose-a:no-underline hover:prose-a:underline">
                    <?php the_content(); ?>
                </div>
            </div>
        </section>

    <?php endwhile; ?>

    <?php get_template_part( 'template-parts/cta/cta', null, array( 'variant' => 'company' ) ); ?>

</main>

<?php get_footer();
