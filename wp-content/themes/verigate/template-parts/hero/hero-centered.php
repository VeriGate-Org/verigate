<?php
/**
 * Hero — Centered Layout (About, Careers, etc.)
 *
 * @package VeriGate
 */

$title       = $args['title'] ?? get_the_title();
$description = $args['description'] ?? '';
$badge       = $args['badge'] ?? '';
$category    = $args['category'] ?? 'company';
$theme       = verigate_get_category_theme( $category );
?>

<section class="relative py-24 md:py-32 overflow-hidden pt-16">
    <div class="absolute inset-0 bg-gradient-to-br from-background <?php echo esc_attr( $theme['hero_bg_to'] ); ?>"></div>
    <div class="absolute inset-0 bg-gradient-mesh opacity-30"></div>

    <div class="container mx-auto max-w-4xl relative z-10 text-center px-4">
        <div class="animate-on-scroll fade-up space-y-6">
            <?php if ( $badge ) : ?>
                <div class="flex justify-center">
                    <?php verigate_badge( $badge, $category ); ?>
                </div>
            <?php endif; ?>

            <h1 class="text-4xl md:text-5xl lg:text-6xl font-bold text-foreground leading-tight">
                <?php echo wp_kses_post( $title ); ?>
            </h1>

            <?php if ( $description ) : ?>
                <p class="text-xl text-muted-foreground max-w-2xl mx-auto">
                    <?php echo esc_html( $description ); ?>
                </p>
            <?php endif; ?>
        </div>
    </div>
</section>
