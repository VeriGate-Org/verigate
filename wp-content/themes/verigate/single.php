<?php
/**
 * Single — Blog Post + Author Card
 *
 * @package VeriGate
 */

get_header();

$author_id    = get_the_author_meta( 'ID' );
$author_photo = function_exists( 'get_field' ) ? get_field( 'author_photo', 'user_' . $author_id ) : null;
$author_bio   = function_exists( 'get_field' ) ? get_field( 'bio', 'user_' . $author_id ) : get_the_author_meta( 'description' );
$author_li    = function_exists( 'get_field' ) ? get_field( 'linkedin_url', 'user_' . $author_id ) : '';
?>

<main class="flex-1 pt-16">

    <!-- Article -->
    <article class="py-16 px-4">
        <div class="container mx-auto max-w-3xl">

            <?php verigate_breadcrumbs( array(
                array( 'label' => 'Home', 'url' => home_url( '/' ) ),
                array( 'label' => 'Blog', 'url' => home_url( '/blog/' ) ),
                array( 'label' => get_the_title() ),
            ) ); ?>

            <!-- Meta -->
            <div class="flex items-center gap-3 text-sm text-muted-foreground mb-6">
                <time datetime="<?php echo get_the_date( 'c' ); ?>"><?php echo get_the_date(); ?></time>
                <span>&middot;</span>
                <span><?php echo verigate_reading_time(); ?> min read</span>
            </div>

            <h1 class="text-4xl md:text-5xl font-bold text-foreground mb-8 leading-tight"><?php the_title(); ?></h1>

            <?php if ( has_post_thumbnail() ) : ?>
                <div class="rounded-lg overflow-hidden mb-10">
                    <?php the_post_thumbnail( 'large', array( 'class' => 'w-full h-auto' ) ); ?>
                </div>
            <?php endif; ?>

            <!-- Content -->
            <div class="prose prose-lg max-w-none prose-headings:text-foreground prose-p:text-muted-foreground prose-a:text-accent hover:prose-a:text-accent/80 prose-strong:text-foreground prose-code:text-accent prose-code:bg-muted prose-code:px-1.5 prose-code:py-0.5 prose-code:rounded">
                <?php the_content(); ?>
            </div>

            <!-- Author Card -->
            <div class="mt-16 pt-8 border-t border-border">
                <div class="flex items-start gap-4 p-6 bg-secondary/30 rounded-lg">
                    <?php if ( $author_photo ) : ?>
                        <img src="<?php echo esc_url( $author_photo['sizes']['thumbnail'] ?? $author_photo['url'] ); ?>" alt="<?php the_author(); ?>" class="w-16 h-16 rounded-full object-cover flex-shrink-0">
                    <?php else : ?>
                        <?php echo get_avatar( $author_id, 64, '', '', array( 'class' => 'w-16 h-16 rounded-full flex-shrink-0' ) ); ?>
                    <?php endif; ?>

                    <div class="flex-1">
                        <div class="flex items-center gap-2 mb-1">
                            <h4 class="font-semibold text-foreground"><?php the_author(); ?></h4>
                            <?php if ( $author_li ) : ?>
                                <a href="<?php echo esc_url( $author_li ); ?>" target="_blank" rel="noopener noreferrer" class="text-accent hover:text-accent/80">
                                    <?php verigate_icon( 'linkedin', 'w-4 h-4', 16 ); ?>
                                </a>
                            <?php endif; ?>
                        </div>
                        <?php if ( $author_bio ) : ?>
                            <p class="text-sm text-muted-foreground"><?php echo esc_html( $author_bio ); ?></p>
                        <?php endif; ?>
                    </div>
                </div>
            </div>

        </div>
    </article>

    <?php get_template_part( 'template-parts/cta/cta', null, array( 'variant' => 'blog' ) ); ?>

</main>

<?php get_footer();

if ( ! function_exists( 'verigate_reading_time' ) ) {
    function verigate_reading_time(): int {
        return max( 1, (int) ceil( str_word_count( strip_tags( get_the_content() ) ) / 250 ) );
    }
}
