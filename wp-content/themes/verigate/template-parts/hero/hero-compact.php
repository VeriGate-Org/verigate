<?php
/**
 * Hero — Compact (Archive pages, hub pages)
 *
 * @package VeriGate
 */

$title       = $args['title'] ?? '';
$description = $args['description'] ?? '';
$badge       = $args['badge'] ?? '';
$category    = $args['category'] ?? 'company';
$theme       = verigate_get_category_theme( $category );
$breadcrumbs = $args['breadcrumbs'] ?? array();
?>

<section class="relative py-16 md:py-20 pt-16 overflow-hidden">
    <div class="absolute inset-0 bg-gradient-to-br from-background <?php echo esc_attr( $theme['hero_bg_to'] ); ?>"></div>

    <div class="container mx-auto max-w-6xl relative z-10 px-4">
        <div class="animate-on-scroll fade-up space-y-4">
            <?php if ( ! empty( $breadcrumbs ) ) : ?>
                <?php verigate_breadcrumbs( $breadcrumbs ); ?>
            <?php endif; ?>

            <?php if ( $badge ) : ?>
                <div>
                    <?php verigate_badge( $badge, $category ); ?>
                </div>
            <?php endif; ?>

            <h1 class="text-3xl md:text-4xl lg:text-5xl font-bold text-foreground">
                <?php echo wp_kses_post( $title ); ?>
            </h1>

            <?php if ( $description ) : ?>
                <p class="text-lg text-muted-foreground max-w-2xl">
                    <?php echo esc_html( $description ); ?>
                </p>
            <?php endif; ?>
        </div>
    </div>
</section>
