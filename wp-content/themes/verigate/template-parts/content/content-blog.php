<?php
/**
 * Content — Blog post card (for archive listings)
 *
 * @package VeriGate
 */
?>

<article <?php post_class( 'bg-card border border-border rounded-lg overflow-hidden hover:shadow-md transition-shadow group' ); ?>>
    <?php if ( has_post_thumbnail() ) : ?>
        <a href="<?php the_permalink(); ?>" class="block overflow-hidden">
            <?php the_post_thumbnail( 'card-thumb', array(
                'class' => 'w-full h-48 object-cover group-hover:scale-105 transition-transform duration-300',
                'loading' => 'lazy',
            ) ); ?>
        </a>
    <?php endif; ?>

    <div class="p-6 space-y-3">
        <div class="flex items-center gap-2 text-xs text-muted-foreground">
            <time datetime="<?php echo get_the_date( 'c' ); ?>"><?php echo get_the_date(); ?></time>
            <span>&middot;</span>
            <span><?php echo verigate_reading_time(); ?> min read</span>
        </div>

        <h2 class="text-xl font-semibold text-foreground group-hover:text-accent transition-colors">
            <a href="<?php the_permalink(); ?>"><?php the_title(); ?></a>
        </h2>

        <p class="text-muted-foreground text-sm line-clamp-3"><?php echo get_the_excerpt(); ?></p>

        <div class="flex items-center gap-3 pt-3 border-t border-border">
            <?php echo get_avatar( get_the_author_meta( 'ID' ), 32, '', '', array( 'class' => 'w-8 h-8 rounded-full' ) ); ?>
            <div>
                <p class="text-sm font-medium text-foreground"><?php the_author(); ?></p>
            </div>
        </div>
    </div>
</article>
<?php

/**
 * Helper: Estimate reading time.
 */
if ( ! function_exists( 'verigate_reading_time' ) ) {
    function verigate_reading_time(): int {
        $content = get_the_content();
        $words   = str_word_count( strip_tags( $content ) );
        return max( 1, (int) ceil( $words / 250 ) );
    }
}
